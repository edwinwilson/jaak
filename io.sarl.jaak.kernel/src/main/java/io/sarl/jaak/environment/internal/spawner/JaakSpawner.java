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
package io.sarl.jaak.environment.internal.spawner;

import io.sarl.jaak.environment.external.body.BodySpawner;
import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.body.TurtleBodyFactory;
import io.sarl.jaak.environment.external.frustum.TurtleFrustum;
import io.sarl.jaak.environment.external.time.TimeManager;
import io.sarl.jaak.environment.internal.model.JaakEnvironment;
import io.sarl.jaak.environment.internal.model.RealTurtleBody;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

/** Provide implementation for a turtle spawner in Jaak environment.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JaakSpawner implements BodySpawner {

	/**
	 * Is the number of retries to compute the position while
	 * the cell at the given position is not free.
	 */
	public static final int FREE_POSITION_COMPUTATION_RETRIES = 10;

	/**
	 */
	public JaakSpawner() {
		//
	}

	/** Spawn only a turtle body in environment and bind it with the
	 * turtle with the given identifier.
	 * <p>
	 * It is assumed that the turtle was already launched, but never
	 * binded with Jaak environment.
	 *
	 * @param turtleId is the identifier of the agent for which a body should be spawn.
	 * @param kernelAddress is the address of the kernel on which the spawned agent may be run.
	 * @param bodyFactory is the body factory to use.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @param creator - reference to the agent body creator.
	 * @return the spawn body, or <code>null</code>.
	 */
	public final TurtleBody spawnBodyFor(
			UUID turtleId,
			UUID kernelAddress,
			TurtleBodyFactory bodyFactory,
			TimeManager timeManager,
			JaakBodyCreator creator) {
		assert (turtleId != null);
		assert (kernelAddress != null);
		assert (bodyFactory != null);

		if (creator != null || isSpawnable(timeManager)) {
			TurtleBodyFactory wrappedFactory = new SpawnedBodyFactory(
					bodyFactory,
					computeSpawnedTurtleOrientation(timeManager));

			TurtleBody body;
			if (creator != null) {
				body = creator.createBody(turtleId, wrappedFactory, timeManager);
			} else {
				body = wrappedFactory.createTurtleBody(turtleId);
			}
			if (body != null) {
				turtleSpawned(turtleId, body, timeManager);
				return body;
			}
		}
		return null;
	}

	/** Replies if a turtle is spawnable according to the spawning law and
	 * the current time mananager.
	 *
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return <code>true</code> if a turtle is spawnable, otherwise <code>false</code>.
	 */
	protected abstract boolean isSpawnable(TimeManager timeManager);

	/** Replies the orientation for a newly spawned turtle.
	 *
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return the orientation angle for a newly spawned turtle.
	 */
	protected abstract float computeSpawnedTurtleOrientation(TimeManager timeManager);

	/** Invoked when a turtle was successfully spawned outside the spawner and
	 * a body was created by this spawner.
	 *
	 * @param turtle is the spawned turtle.
	 * @param body is the spawned turtle body.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 */
	protected abstract void turtleSpawned(UUID turtle, TurtleBody body, TimeManager timeManager);

	/** Replies the position where to spawn a turtle.
	 * The replied position could be different from the reference
	 * position replied by {@link #getReferenceSpawningPosition()}.
	 *
	 * @param desiredPosition is the position desired by the factory invoker.
	 * @return a position.
	 */
	protected abstract Point2f computeCurrentSpawningPosition(Point2f desiredPosition);

	/** Provide implementation for a body factory dedicated to spawners.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SpawnedBodyFactory implements TurtleBodyFactory {

		private final TurtleBodyFactory factory;
		private final float orientation;

		/**
		 * @param factory is the turtle body factory to wrap.
		 * @param orientation is the turtle body.
		 */
		public SpawnedBodyFactory(TurtleBodyFactory factory, float orientation) {
			this.factory = factory;
			this.orientation = orientation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle, Serializable semantic) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				float desiredAngle, Serializable semantic) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Serializable semantic) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, Serializable semantic) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle, Serializable semantic,
				TurtleFrustum frustum) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, float desiredAngle,
				TurtleFrustum frustum) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, TurtleFrustum frustum) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId, TurtleFrustum frustum) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				float desiredAngle, Serializable semantic, TurtleFrustum frustum) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Serializable semantic, TurtleFrustum frustum) {
			Point2f p = computeValidPosition(null);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(UUID turtleId,
				Point2f desiredPosition, Serializable semantic, TurtleFrustum frustum) {
			Point2f p = computeValidPosition(desiredPosition);
			if (p == null) {
				return null;
			}
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic,
					frustum);
		}

		public Point2f computeValidPosition(Point2f desiredPosition) {
			if(desiredPosition == null){
				return getRandomSpawningPosition();
			}
			if(canSpawnHere(desiredPosition)){
				return desiredPosition;
			}
			else{
				return getRandomSpawningPosition();
			}
		}

		public Point2f getRandomSpawningPosition() {
			Random r = new Random();
			while (true) {
				float x = r.nextFloat() * factory.getEnvironment().getWidth();
				float y = r.nextFloat() * factory.getEnvironment().getHeight();
				Point2f resultPoint = new Point2f(x, y);
				if (spaceOccupiedByEnvironmentalObject(resultPoint)) {
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
			Map<UUID, RealTurtleBody> bodyMap = factory.getEnvironment().getBodies();
			Iterator<UUID> bodyMapIt = bodyMap.keySet().iterator();
			while(bodyMapIt.hasNext()) {
				Body body = bodyMap.get(bodyMapIt.next()).getBox();
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

		public boolean canSpawnHere(Point2f desiredSpawningPosition) {
			return !spaceOccupiedByEnvironmentalObject(desiredSpawningPosition);
		}

		@Override
		public JaakEnvironment getEnvironment() {
			// returns null
			return null;
		}
	}

}
