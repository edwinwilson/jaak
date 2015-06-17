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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

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

	private final float radius;


	public SquareTurtleFrustum(float side) {
		this.radius = side;
	}

	/** Replies the side of the square.
	 *
	 * @return the side of the square.
	 */
	public float getRadiusLength() {
		return this.radius;
	}

	@Override
	public Iterator<UUID> getPerceivedObjects(TurtleObject perceiver,
			float direction, EnvironmentArea environment) {
		//return new PerceivableIterator(perceiver);
		final List<UUID> nearBodies = new ArrayList<UUID>();
		//Get the bodies near the perceiver.
		Vec2 topLeft = new Vec2(perceiver.getPosition().x()-radius,perceiver.getPosition().y()-radius);
		Vec2 bottomRight = new Vec2(perceiver.getPosition().x()+radius,perceiver.getPosition().y()+radius);
		QueryCallback queryCallback = new QueryCallback() {

            @Override
            public boolean reportFixture(Fixture fixture) {
                Body body = fixture.getBody();
                if(body.getUserData() instanceof UUID){
                	nearBodies.add((UUID)(body.getUserData()));
                }  
                return true;
            }

        };
		perceiver.getBox().getWorld().queryAABB(queryCallback, new AABB(topLeft,bottomRight));
		return nearBodies.iterator();
	}

}
