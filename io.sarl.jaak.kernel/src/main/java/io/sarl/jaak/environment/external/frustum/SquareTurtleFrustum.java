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
package io.sarl.jaak.environment.external.frustum;

import io.sarl.jaak.environment.external.EnvironmentArea;
import io.sarl.jaak.environment.external.perception.JaakObject;

import java.util.Iterator;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;

/** This class defines a frustum for for a turtle which is
 * restricted to a square.
 * This frustum is not orientable.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SquareTurtleFrustum implements TurtleFrustum {

	private final float side;

	/**
	 * @param side is the length of the square side
	 */
	public SquareTurtleFrustum(float side) {
		this.side = side;
	}

	/** Replies the side of the square.
	 *
	 * @return the side of the square.
	 */
	public float getSideLength() {
		return this.side;
	}

	@Override
	public Iterator<JaakObject> getPerceivedObjects(Point2f origin,
			float direction, EnvironmentArea environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
