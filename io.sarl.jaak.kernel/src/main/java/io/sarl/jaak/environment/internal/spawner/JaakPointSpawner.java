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

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;

/** Provide implementation for a turtle spawner on a point.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JaakPointSpawner extends JaakSpawner {

	private final Point2f position;

	/**
	 * @param x is the position of the spawner.
	 * @param y is the position of the spawner.
	 */
	public JaakPointSpawner(int x, int y) {
		this.position = new Point2f(x, y);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f computeCurrentSpawningPosition(Point2f desiredPosition) {
		return this.position;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f getReferenceSpawningPosition() {
		return this.position;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Shape2f toShape() {
		return new Rectangle2f(this.position.getX(), this.position.getY(), 1, 1);
	}

}
