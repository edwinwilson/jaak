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

import io.sarl.jaak.environment.external.EnvironmentArea;
import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.time.TimeManager;
import io.sarl.jaak.util.RandomNumber;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;

/** A spawner which is spawning on the overall world surface.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JaakWorldSpawner extends JaakSpawner {

	private final WeakReference<EnvironmentArea> environment;

	/**
	 * @param environment is the environment in which the spawning may proceed.
	 */
	public JaakWorldSpawner(EnvironmentArea environment) {
		this.environment = new WeakReference<>(environment);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f computeCurrentSpawningPosition(Point2f desiredPosition) {
		EnvironmentArea area = this.environment.get();
		assert (area != null);
		if (desiredPosition != null
				&& desiredPosition.x() >= area.getX()
				&& desiredPosition.y() >= area.getY()
				&& desiredPosition.x() <= area.getX() + area.getWidth()
				&& desiredPosition.y() <= area.getY() + area.getHeight()) {
			return new Point2f(desiredPosition);
		}
		float dx = RandomNumber.nextFloat()*area.getWidth();
		float dy = RandomNumber.nextFloat()*area.getHeight();
		return new Point2f(area.getX() + dx, area.getY() + dy);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f getReferenceSpawningPosition() {
		EnvironmentArea area = this.environment.get();
		assert (area != null);
		return new Point2f(area.getX(), area.getY());
	}

	/** {@inheritDoc}
	 */
	@Override
	public Shape2f toShape() {
		EnvironmentArea area = this.environment.get();
		assert (area != null);
		return new Rectangle2f(area.getX(), area.getY(), area.getWidth(), area.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float computeSpawnedTurtleOrientation(TimeManager timeManager) {
		return RandomNumber.nextFloat() * MathConstants.TWO_PI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSpawnable(TimeManager timeManager) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void turtleSpawned(UUID turtle, TurtleBody body, TimeManager timeManager) {
		//
	}

}
