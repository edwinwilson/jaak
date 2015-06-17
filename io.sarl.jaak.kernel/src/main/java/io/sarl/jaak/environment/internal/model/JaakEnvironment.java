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
package io.sarl.jaak.environment.internal.model;

import io.sarl.jaak.environment.external.EnvironmentArea;
import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.body.TurtleBodyFactory;
import io.sarl.jaak.environment.external.frustum.SquareTurtleFrustum;
import io.sarl.jaak.environment.external.frustum.TurtleFrustum;
import io.sarl.jaak.environment.external.influence.Influence;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.external.perception.PerceivedTurtle;
import io.sarl.jaak.environment.external.time.TimeManager;
import io.sarl.jaak.environment.internal.endogenousengine.EnvironmentEndogenousEngine;
import io.sarl.jaak.environment.internal.solver.ActionApplier;
import io.sarl.jaak.environment.internal.solver.Box2DInfluenceSolver;
import io.sarl.jaak.util.MultiCollection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * This class defines the Jaak environment model.
 * <p>
 * The environment is a grid composed of cells. Each cell is able to contain
 * zero or one turtle body and many environmental objects.
 * <p>
 * The environment may be wrapped or not. When the environment is wrapped and a
 * turtle is trying to go outside the environment grid, it is moved on the
 * opposite side of the grid. If the envrionment is not wrapped, when a turtle
 * is trying to move outside the grid, it is moved until it reach the border of
 * the grid.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JaakEnvironment implements EnvironmentArea {

	/**
	 * Defines the default perception distance for turtles.
	 */
	public static final float DEFAULT_PERCEPTION_DISTANCE = 7;

	private final UUID id = UUID.randomUUID();
	private final Map<UUID, RealTurtleBody> bodies = new TreeMap<>();
	private final Map<UUID, EnvironmentalObject> environmentalObjects = new TreeMap<>();
	private JaakContinuousWorld model;
	private TimeManager timeManager;
	private final AtomicBoolean isWrapped = new AtomicBoolean(false);
	private volatile EnvironmentEndogenousEngine endogenousEngine;
	private volatile Collection<Influence> endogenousInfluences;
	private Box2DInfluenceSolver boxSolver;
	private float lastSimulationTime = Float.NaN;

	private final LinkedList<JaakEnvironmentListener> listeners = new LinkedList<>();

	private final RealTurtleBodyFactory factory;

	/**
	 * @param width
	 *            is the width of the world grid.
	 * @param height
	 *            is the height of the world grid.
	 * @param timeManager
	 *            is the time manager used to run Jaak.
	 */
	public JaakEnvironment(float width, float height, TimeManager timeManager) {
		this.model = new JaakContinuousWorld(this);
		this.timeManager = timeManager;
		this.factory = new RealTurtleBodyFactory(model.getWorld());
	}

	/**
	 * @param width
	 *            is the width of the world.
	 * @param height
	 *            is the height of the world.
	 */
	public JaakEnvironment(float width, float height) {
		this(width, height, null);
	}

	/**
	 * Add listener on environment events.
	 *
	 * @param listener
	 *            - the listener.
	 */
	public void addJaakEnvironmentListener(JaakEnvironmentListener listener) {
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Remove listener on environment events.
	 *
	 * @param listener
	 *            - the listener.
	 */
	public void removeJaakEnvironmentListener(JaakEnvironmentListener listener) {
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Fire pre agent scheduling event.
	 */
	protected void firePreAgentScheduling() {
		JaakEnvironmentListener[] list;
		synchronized (this.listeners) {
			list = new JaakEnvironmentListener[this.listeners.size()];
			this.listeners.toArray(list);
		}
		for (JaakEnvironmentListener listener : list) {
			listener.preAgentScheduling();
		}
	}

	/**
	 * Fire post agent scheudling event.
	 */
	protected void firePostAgentScheduling() {
		JaakEnvironmentListener[] list;
		synchronized (this.listeners) {
			list = new JaakEnvironmentListener[this.listeners.size()];
			this.listeners.toArray(list);
		}
		for (JaakEnvironmentListener listener : list) {
			listener.postAgentScheduling();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getX() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getY() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return model.getWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return model.getHeight();
	}

	/**
	 * Set the time manager for the environment.
	 *
	 * @param timeManager
	 *            is the time manager which must be used by the environment.
	 */
	public void setTimeManager(TimeManager timeManager) {
		assert (timeManager != null);
		this.timeManager = timeManager;
	}

	/**
	 * Replies the action applier for this environment.
	 *
	 * @return the action applier for this environment.
	 */
	public ActionApplier getActionApplier() {
		return this.model;
	}

	/**
	 * Replies the unique identifier of this environment object.
	 *
	 * @return the identifier of the environment.
	 */
	public UUID getIdentifier() {
		return this.id;
	}

	/**
	 * Replies a factory for the bodies which is connected to this environment.
	 * <p>
	 * Caution: this factory create bodies, and adds them inside the
	 * environment.
	 *
	 * @return a body factory.
	 */
	public TurtleBodyFactory getTurtleBodyFactory() {
		return this.factory;
	}

	/**
	 * Replies if the environment is wrapped.
	 *
	 * @return <code>true</code> if the environment is wrapped, otherwise
	 *         <code>false</code>.
	 */
	public boolean isWrapped() {
		return this.isWrapped.get();
	}

	/**
	 * Change the wrapping flag of the environment.
	 *
	 * @param wrapped
	 *            indicates if the environment is wrapped or not.
	 */
	public void setWrapped(boolean wrapped) {
		this.isWrapped.set(wrapped);
	}

	@Override
	public synchronized int getTurtleCount() {
		return this.bodies.size();
	}

	/**
	 * Set the endogenous engine to use.
	 *
	 * @param engine
	 *            is the endogenous engine to use.
	 */
	public void setEndogenousEngine(EnvironmentEndogenousEngine engine) {
		this.endogenousEngine = engine;
	}

	/**
	 * Set the solver of influence conflicts.
	 *
	 * @param solver
	 *            is the solver of influence conflicts to use.
	 */
	public void setInfluenceSolver(Box2DInfluenceSolver solver) {
		this.boxSolver = solver;
	}

	/**
	 * Add a body in the environment.
	 *
	 * @param body
	 *            is the body to add.
	 * @param position
	 *            is the position of the body.
	 * @return <code>true</code> if the body was successfully added,
	 *         <code>false</code> otherwise.
	 */
	synchronized boolean addBody(RealTurtleBody body, Point2f position) {
		assert (body != null);
		assert (position != null);
		if (!this.bodies.containsKey(body.getTurtleId())) {
			if (this.model.putTurtle(position.x(), position.y(), body)) {
				this.bodies.put(body.getTurtleId(), body);
				body.setPhysicalState(position.getX(), position.getY(),
						body.getHeadingAngle(), 0f, new Vector2f());
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove a body from the environment.
	 *
	 * @param turtle
	 *            is the identifier of the turtle for which the body must be
	 *            removed.
	 * @return the success state of the removal action.
	 */
	public synchronized boolean removeBodyFor(UUID turtle) {
		if (turtle != null) {
			RealTurtleBody body = this.bodies.remove(turtle);
			if (body != null) {
				this.model.removeTurtle(turtle);
				return true;
			}
		}
		return false;
	}

	/**
	 * Replies if the given address is associated to a body in the environment.
	 *
	 * @param turtle
	 *            is the identifier of the turtle.
	 * @return <code>true</code> if the turtle has a body, otherwise
	 *         <code>false</code>.
	 */
	public synchronized boolean hasBodyFor(UUID turtle) {
		if (turtle != null) {
			return this.bodies.containsKey(turtle);
		}
		return false;
	}

	/**
	 * Replies the body for the given address.
	 *
	 * @param turtle
	 *            is the identifier of the turtle.
	 * @return the turtle body or <code>null</code>.
	 */
	public synchronized TurtleBody getBodyFor(UUID turtle) {
		if (turtle != null) {
			return this.bodies.get(turtle);
		}
		return null;
	}

	/**
	 * Apply the given function on all the bodies.
	 *
	 * @param function
	 *            - the function to apply.
	 */
	public synchronized void apply(Lambda<TurtleBody> function) {
		for (TurtleBody body : this.bodies.values()) {
			function.apply(body);
		}
	}

	/**
	 * Run the environment behaviour before any turtle execution.
	 */
	public synchronized void runPreTurtles() {
		computePerceptions();
		firePreAgentScheduling();
	}

	private void computePerceptions() {
		UUID id;
		TurtleBody perceivedBody;
		EnvironmentalObject perceivedObject;
		List<PerceivedTurtle> bodies;
		List<EnvironmentalObject> objects;
		TurtleFrustum frustum;
		Iterator<UUID> iterator;
		for (RealTurtleBody observerBody : this.bodies.values()) {
			bodies = new ArrayList<>();
			objects = new ArrayList<>();
			if (observerBody.isPerceptionEnable()) {
				frustum = observerBody.getPerceptionFrustum();
				if (frustum != null) {
					iterator = frustum.getPerceivedObjects(observerBody,
							lastSimulationTime, null);
					if (iterator != null) {
						while (iterator.hasNext()) {
							id = iterator.next();
							perceivedBody = this.getBodyFor(id);
							if (perceivedBody != null) {
								bodies.add(new PerceivedTurtle(perceivedBody
										.getTurtleId(), observerBody
										.getPosition(), perceivedBody
										.getPosition(), (float) Math.sqrt(Math
										.pow(perceivedBody.getHeadingVector()
												.x(), 2)
										+ Math.pow(perceivedBody
												.getHeadingVector().y(), 2)),
										perceivedBody.getHeadingAngle(),
										perceivedBody.getSemantic()));
							} else {
								perceivedObject = this
										.getEnvironmentalObject(id);
								if (perceivedObject != null)
									objects.add(perceivedObject);
							}
						}
					}
				}
			}
			observerBody.setPerceptions(bodies, objects);
		}
	}

	/**
	 * Run the environment behaviour after all turtle executions.
	 */
	public synchronized void runPostTurtles() {
		applyInfluences();
		stepBox2d();
		runEndogenousEngine();
		firePostAgentScheduling();
	}

	private void applyInfluences() {
		Box2DInfluenceSolver theSolver = this.boxSolver;
		if (theSolver == null) {
			this.boxSolver = new Box2DInfluenceSolver();
			theSolver = this.boxSolver;
		}
		theSolver.solve(this.bodies.values());
	}

	private void stepBox2d() {
		this.model.getWorld()
				.step(this.timeManager.getLastStepDuration(), 6, 3);
	}

	private void runEndogenousEngine() {
		// Run autonomous environmental processes
		float currentTime = this.timeManager.getCurrentTime(TimeUnit.SECONDS);
		float simulationStepDuration;
		if (Float.isNaN(this.lastSimulationTime)) {
			simulationStepDuration = 0f;
		} else {
			simulationStepDuration = currentTime - this.lastSimulationTime;
		}

		MultiCollection<Influence> endoInfluences = new MultiCollection<>();

		this.lastSimulationTime = currentTime;
		Collection<Influence> col = this.model.runAutonomousProcesses(
				currentTime, simulationStepDuration);
		if (col != null && !col.isEmpty()) {
			endoInfluences.addCollection(col);
		}

		// Run endogenous engine
		EnvironmentEndogenousEngine engine = this.endogenousEngine;
		if (engine != null) {
			col = engine.computeInfluences(this.model, this.timeManager);
			if (col != null && !col.isEmpty()) {
				endoInfluences.addCollection(col);
			}
		}

		if (!endoInfluences.isEmpty()) {
			this.endogenousInfluences = endoInfluences;
		}
	}

	/**
	 * This class defines an iterable object which is able to filter its
	 * content.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class FilteringIterable<T> implements Iterable<T> {

		private final Class<T> type;
		private final Collection<?> collection;

		/**
		 * @param type
		 *            is the type of the elements to reply
		 * @param collection
		 *            is the collection to iterate on.
		 */
		public FilteringIterable(Class<T> type, Collection<?> collection) {
			this.type = type;
			this.collection = collection;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<T> iterator() {
			return new FilteringIterator<>(this.type,
					this.collection.iterator());
		}

	}

	/**
	 * This class defines an iterable object which is able to filter its
	 * content.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class FilteringIterator<T> implements Iterator<T> {

		private final Class<T> type;
		private final Iterator<?> iterator;
		private T next;

		/**
		 * @param type
		 *            is the type of the elements to reply
		 * @param iterator
		 *            is the iterator to use.
		 */
		public FilteringIterator(Class<T> type, Iterator<?> iterator) {
			this.type = type;
			this.iterator = iterator;
			searchNext();
		}

		private void searchNext() {
			this.next = null;
			Object o;
			while (this.iterator.hasNext()) {
				o = this.iterator.next();
				if (o != null && this.type.isInstance(o)) {
					this.next = this.type.cast(o);
					return;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T next() {
			T n = this.next;
			searchNext();
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			//
		}

	}

	/**
	 * This class defines an implementation of turtle body factory.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RealTurtleBodyFactory implements TurtleBodyFactory {

		private World world;

		/**
		 */
		public RealTurtleBodyFactory(World world) {
			this.world = world;
		}

		private TurtleFrustum getDefaultFrustum() {
			return new SquareTurtleFrustum(DEFAULT_PERCEPTION_DISTANCE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle,
				Serializable semantic) {
			return createTurtleBody(turtleId, desiredPosition, desiredAngle,
					semantic, getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle) {
			return createTurtleBody(turtleId, desiredPosition, desiredAngle,
					getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition) {
			return createTurtleBody(turtleId, desiredPosition,
					getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId) {
			return createTurtleBody(turtleId, getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId, float desiredAngle,
				Serializable semantic) {
			return createTurtleBody(turtleId, desiredAngle, semantic,
					getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId, Serializable semantic) {
			return createTurtleBody(turtleId, semantic, getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, Serializable semantic) {
			return createTurtleBody(turtleId, desiredPosition, semantic,
					getDefaultFrustum());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle,
				Serializable semantic, TurtleFrustum frustum) {
			float bodyRadius = 1;

			Point2f position = null;

			if (desiredPosition == null) {
				position = getRandomPosition();
			} else {
				position = desiredPosition;
			}

			if (position == null) {
				return null;
			}
			// create and configure Jbox body
			BodyDef ballBodydef = new BodyDef();
			ballBodydef.type = BodyType.DYNAMIC;
			ballBodydef.position = new Vec2(desiredPosition.getX(),
					desiredPosition.getY());
			ballBodydef.angle = desiredAngle;

			CircleShape ballShape = new CircleShape();
			ballShape.setRadius(bodyRadius);

			FixtureDef ballFixture = new FixtureDef();
			ballFixture.shape = ballShape;
			ballFixture.density = 1;
			ballFixture.friction = 0.4f;

			Body ballBody = this.world.createBody(ballBodydef);
			ballBody.createFixture(ballFixture);
			ballBody.setUserData(turtleId);

			RealTurtleBody body = new RealTurtleBody(turtleId, frustum,
					semantic, ballBody);
			if (JaakEnvironment.this.addBody(body, position)) {
				return body;
			}
			return null;
		}

		private Point2f getRandomPosition() {
			Random r = new Random();
			while (true) {
				float x = r.nextFloat() * model.getWidth();
				float y = r.nextFloat() * model.getHeight();
				Point2f resultPoint = new Point2f(x, y);
				if (!spaceOccupiedByEnvironmentalObject(resultPoint)) {
					return resultPoint;
				}
			}
		}

		/**
		 * Checks if the position indicated by a given point is contained in a
		 * static object of the world.
		 * 
		 * @param pointToTest
		 * @return
		 */
		public boolean spaceOccupiedByEnvironmentalObject(Point2f pointToTest) {
			if(pointToTest == null){
				return false;
			}
			Body body = world.getBodyList();
			if(body == null){
				return true;
			}
			for (; body.getNext() != null;) {
				if (body.getType() == BodyType.STATIC) {
					Fixture f = body.getFixtureList();
					if (f.testPoint(new Vec2(pointToTest.getX(), pointToTest.getY()))) {
						return true;
					}
				}
				body = body.getNext();
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle,
				TurtleFrustum frustum) {
			return createTurtleBody(turtleId, desiredPosition, desiredAngle,
					null, frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, TurtleFrustum frustum) {
			return createTurtleBody(turtleId, desiredPosition, Float.NaN, null,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId, TurtleFrustum frustum) {
			return createTurtleBody(turtleId, null, Float.NaN, null, frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId, float desiredAngle,
				Serializable semantic, TurtleFrustum frustum) {
			return createTurtleBody(turtleId, null, desiredAngle, semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Serializable semantic, TurtleFrustum frustum) {
			return createTurtleBody(turtleId, null, Float.NaN, semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, Serializable semantic,
				TurtleFrustum frustum) {
			return createTurtleBody(turtleId, desiredPosition, Float.NaN,
					semantic, frustum);
		}

		@Override
		public JaakEnvironment getEnvironment() {
			return JaakEnvironment.this;
		}

	} /* class RealTurtleBodyFactory */

	/**
	 * Definition of a function.
	 *
	 * @param <T>
	 *            - the type of the lambda's parameter.
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public interface Lambda<T> {

		/**
		 * Apply the lambda.
		 *
		 * @param object
		 *            - the object on whic hthe lambda must be applied.
		 */
		void apply(T object);

	} /* interface Lambda */

	public EnvironmentalObject getEnvironmentalObject(UUID id) {
		return this.environmentalObjects.get(id);
	}

	public EnvironmentalObject removeEnvironmentalObject(UUID id) {
		if (environmentalObjects.containsKey(id)) {
			return this.environmentalObjects.remove(id);
		}
		return null;
	}

	/**
	 * @return the bodies
	 */
	public Map<UUID, RealTurtleBody> getBodies() {
		return bodies;
	}


}
