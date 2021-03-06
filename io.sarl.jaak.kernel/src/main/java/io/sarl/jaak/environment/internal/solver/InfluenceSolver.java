/*
 * $Id$
 *
 * Jaak environment model is an open-source multiagent library.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014 Stéphane GALLAND.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.jaak.environment.internal.solver;

import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.influence.DropDownInfluence;
import io.sarl.jaak.environment.external.influence.EnvironmentalObjectRemovalInfluence;
import io.sarl.jaak.environment.external.influence.Influence;
import io.sarl.jaak.environment.external.influence.MotionInfluence;
import io.sarl.jaak.environment.external.influence.MotionInfluenceStatus;
import io.sarl.jaak.environment.external.influence.PickUpInfluence;
import io.sarl.jaak.environment.external.influence.SemanticChangeInfluence;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.external.perception.PickedObject;
import io.sarl.jaak.environment.external.perception.Substance;
import io.sarl.jaak.environment.external.time.TimeManager;
import io.sarl.jaak.environment.internal.GridModel;
import io.sarl.jaak.environment.internal.ValidationResult;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;

/** This class defines the methods of a solver of
 * influence conflicts.
 *
 * @param <T> is the type of the turtle bodies supported by this solver.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class InfluenceSolver<T extends TurtleBody> {

	private final AtomicBoolean isWrapped = new AtomicBoolean();
	private WeakReference<GridModel> grid;
	private WeakReference<TimeManager> timeManager;

	/** Replies if the environment is wrapped.
	 *
	 * @return <code>true</code> if the environment is
	 * wrapped, otherwise <code>false</code>.
	 */
	public boolean isWrapped() {
		return this.isWrapped.get();
	}

	/** Change the wrapping flag of the environment.
	 *
	 * @param wrapped indicates if the environment is
	 * wrapped or not.
	 */
	public void setWrapped(boolean wrapped) {
		this.isWrapped.set(wrapped);
	}

	/** Replies if the grid model used by the solver.
	 *
	 * @return the grid model.
	 */
	public GridModel getGridModel() {
		return this.grid == null ? null : this.grid.get();
	}

	/** Set the grid model.
	 *
	 * @param grid - the model of the world, ie. the grid.
	 */
	public void setGridModel(GridModel grid) {
		this.grid = new WeakReference<>(grid);
	}

	/** Replies the time manager used by the environment model.
	 *
	 * @return the time manager used by the environment model.
	 */
	public TimeManager getTimeManager() {
		return this.timeManager == null ? null : this.timeManager.get();
	}

	/** Set the time manager used by the environment model.
	 *
	 * @param timeManager - the time manager to use during solving the conflicts.
	 */
	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = new WeakReference<>(timeManager);
	}

	/** Detect and solve the conflicts between influences.
	 *
	 * @param endogenousInfluences are all the influences generated by the endogenous engine, could be <code>null</code>.
	 * @param bodies is the collection of the bodies which may generate influences.
	 * @param actionApplier is the object which may receive actions.
	 */
	public abstract void solve(
			Collection<? extends Influence> endogenousInfluences,
			Collection<T> bodies,
			ActionApplier actionApplier);

	/** Transform the given influence into an action and apply it with
	 * the given applier.
	 *
	 * @param actionApplier is the object which is able to apply action.
	 * @param influence is the influence to transform.
	 * @param motionStatus is the status of the last motion influence. If the given influence
	 * is not a motion influence, this parameter is ignored.
	 */
	protected void applyInfluence(ActionApplier actionApplier, Influence influence, MotionInfluenceStatus motionStatus) {
		assert (actionApplier != null);
		assert (influence != null);

		if (influence instanceof MotionInfluence) {
			MotionInfluenceStatus mis = motionStatus;
			TurtleBody emitter = (TurtleBody) influence.getEmitter();
			assert (emitter != null);
			Point2f position = emitter.getPosition();
			assert (position != null);
			Point2f newPosition = new Point2f();
			MotionInfluence mi = (MotionInfluence) influence;
			newPosition.set(
					Math.round(position.getX() + mi.getLinearMotionX()),
					Math.round(position.getY() + mi.getLinearMotionY()));

			ValidationResult validationResult = validatePosition(newPosition);

			float heading = emitter.getHeadingAngle();
			heading += mi.getAngularMotion();

			int dx = newPosition.x() - position.x();
			int dy = newPosition.y() - position.y();

			if (dx != 0 || dy != 0) {

				if (validationResult == ValidationResult.WRAPPED) {
					dx = Math.round(mi.getLinearMotionX());
					dy = Math.round(mi.getLinearMotionY());
				}

				if (actionApplier.putTurtle(newPosition.x(), newPosition.y(), emitter)) {
					if (!actionApplier.removeTurtle(position.x(), position.y(), emitter)) {
						actionApplier.removeTurtle(newPosition.x(), newPosition.y(), emitter);
						newPosition.set(position);
						dx = 0;
						dy = 0;
					}
				} else {
					newPosition.set(position);
					dx = 0;
					dy = 0;
				}

				TimeManager tm = getTimeManager();
				assert (tm != null);
				float stepDuration = tm.getLastStepDuration();
				float speed;
				if (stepDuration == 0f) {
					speed = 0f;
				} else {
					speed = (float) Math.sqrt(dx * dx + dy * dy) / stepDuration;
					if (speed < 0f) {
						speed = 0f;
					}
				}

				actionApplier.setPhysicalState(
						newPosition.x(), newPosition.y(),
						heading,
						speed,
						emitter);
			} else {
				actionApplier.setPhysicalState(
						position.x(), position.y(),
						heading,
						0f,
						emitter);
				mis = MotionInfluenceStatus.NO_MOTION;
			}
			putBackMotionInfluenceStatus(emitter, mis);
		} else if (influence instanceof PickUpInfluence) {
			TurtleBody emitter = (TurtleBody) influence.getEmitter();
			assert (emitter != null);
			Point2f position = emitter.getPosition();
			assert (position != null);
			PickUpInfluence pui = (PickUpInfluence) influence;
			EnvironmentalObject pickedUp = actionApplier.removeObject(position.x(), position.y(), pui.getPickUpObject());
			if (pickedUp instanceof Substance) {
				putBackPickingAction(emitter, new PickedObject(pickedUp));
			} else if (pickedUp != null) {
				putBackPickingAction(emitter, new PickedObject(pickedUp));
			}
		} else if (influence instanceof DropDownInfluence) {
			TurtleBody emitter = (TurtleBody) influence.getEmitter();
			assert (emitter != null);
			Point2f position = emitter.getPosition();
			assert (position != null);
			DropDownInfluence ddi = (DropDownInfluence) influence;
			actionApplier.putObject(position.x(), position.y(), ddi.getDropOffObject());
		} else if (influence instanceof EnvironmentalObjectRemovalInfluence) {
			EnvironmentalObjectRemovalInfluence eori = (EnvironmentalObjectRemovalInfluence) influence;
			EnvironmentalObject obj = eori.getRemovableObject();
			if (obj != null) {
				Point2f position = obj.getPosition();
				assert (position != null);
				actionApplier.removeObject(position.x(), position.y(), obj);
			}
		} else if (influence instanceof SemanticChangeInfluence) {
			SemanticChangeInfluence sci = (SemanticChangeInfluence) influence;
			TurtleBody emitter = (TurtleBody) influence.getEmitter();
			assert (emitter != null);
			Serializable semantic = sci.getSemantic();
			emitter.setSemantic(semantic);
		} else {
			throw new IllegalStateException();
		}

	}

	/** Invoked to put back a picking perception for the given body.
	 *
	 * @param body is the body which has realized a picking action.
	 * @param action is the picking action.
	 */
	protected abstract void putBackPickingAction(TurtleBody body, PickedObject action);

	/** Invoked to save the status of the application of the motion influence.
	 *
	 * @param body is the body which has realized a motion action.
	 * @param status is the status of the motion influence application.
	 */
	protected abstract void putBackMotionInfluenceStatus(TurtleBody body, MotionInfluenceStatus status);

	/** Validate the given position to be on the grid.
	 * This function ensures that the given position is
	 * updated to fit the bounds of the grid.
	 *
	 * @param position is the position to validate.
	 * @return how the position was validated.
	 */
	protected ValidationResult validatePosition(Point2f position) {
		GridModel g = getGridModel();
		//TODO: adapat to tree
		//return g.validatePosition(isWrapped(), false, position);
		return null;
	}

}
