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
import io.sarl.jaak.environment.external.body.TurtleObject;
import io.sarl.jaak.environment.external.perception.JaakObject;

import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;

/** This class defines a frustum for for a turtle which is
 * a cross in the horizontal and vertical directions.
 * This frustum is not orientable.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CrossTurtleFrustum implements TurtleFrustum {

	private static final int SIDES = 4;

	private final float crossLength;

	/**
	 * @param crossLength is the length of the cross branches
	 */
	public CrossTurtleFrustum(float crossLength) {
		this.crossLength = Math.max(1, crossLength);
	}

	/** Replies the length of each cross branch.
	 *
	 * @return the length of each cross branch.
	 */
	public float getCrossBranchLength() {
		return this.crossLength;
	}

	@Override
	public Iterator<JaakObject> getPerceivedObjects(TurtleObject perceiver,
			float direction, EnvironmentArea environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
