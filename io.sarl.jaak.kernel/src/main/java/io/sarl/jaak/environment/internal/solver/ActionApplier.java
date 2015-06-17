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
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Vector2f;

/** This interface defines the methods which are used
 * to apply actions in a Jaak environment.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ActionApplier {

	boolean putTurtle(TurtleBody emitter);

	boolean removeTurtle(UUID emitter);

	boolean setPhysicalState(float x, float y, float heading, float speed, Vector2f linearVelocity, TurtleBody emitter);

	EnvironmentalObject removeObject(EnvironmentalObject pickUpObject);

	void putObject(float x, float y, EnvironmentalObject dropOffObject);

}
