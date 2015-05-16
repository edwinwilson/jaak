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
package io.sarl.jaak.environment.external.perception;

import io.sarl.jaak.environment.external.body.TurtleBody;

import java.io.Serializable;

import org.arakhne.afc.math.continous.object2d.Point2f;

/** This class defines a object which was picked up from the cell
 * according to a previous picking-up influence.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PickedObject implements Perceivable, Serializable {

	private static final long serialVersionUID = -5408636984133012977L;

	private final EnvironmentalObject pickedObject;

	/**
	 * @param pickedUpObject is the picked-up object.
	 */
	public PickedObject(EnvironmentalObject pickedUpObject) {
		this.pickedObject = pickedUpObject;
	}

	/** Replies the picked-up object.
	 *
	 * @return the picked-up object.
	 */
	public EnvironmentalObject getPickedUpObject() {
		return this.pickedObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("PICKED("); //$NON-NLS-1$
		buffer.append(this.pickedObject.getEnvironmentalObjectIdentifier());
		buffer.append(")@("); //$NON-NLS-1$
		Point2f position = getPosition();
		buffer.append(position.getX());
		buffer.append(';');
		buffer.append(position.getY());
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2f getPosition() {
		return this.pickedObject.getPosition();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f getRelativePosition(TurtleBody body) {
		Point2f p = this.pickedObject.getPosition();
		if (body == null) {
			return p;
		}
		Point2f bp = body.getPosition();
		return new Point2f(bp.x() - p.x(), bp.y() - p.y());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSemantic() {
		return this.pickedObject.getSemantic();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTurtle() {
		return this.pickedObject.isTurtle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBurrow() {
		return this.pickedObject.isBurrow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isObstacle() {
		return this.pickedObject.isObstacle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSubstance() {
		return this.pickedObject.isSubstance();
	}

}
