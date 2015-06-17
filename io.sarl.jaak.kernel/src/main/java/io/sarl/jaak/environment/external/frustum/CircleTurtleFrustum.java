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
import io.sarl.jaak.util.ShapeMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;


/** This class defines a frustum for for a turtle which is
 * restricted to a circle.
 * This frustum is not orientable.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CircleTurtleFrustum implements TurtleFrustum {

	private final float radius;
	private Circle2f shape;
	
	public CircleTurtleFrustum(float radius) {
		this.radius = radius;

	}
	
	public float getRadius() {
		return this.radius;
	}
	
	@Override
	public Iterator<UUID> getPerceivedObjects(TurtleObject perceiver, float direction, EnvironmentArea environment) {
		this.shape = new Circle2f(perceiver.getPosition().x(),perceiver.getPosition().y(),this.radius);
		return new PerceivableIterator(perceiver,shape);
	}
	
	private class PerceivableIterator implements Iterator<UUID> {
		
		private List<Body> nearBodies;
		private List<UUID> inViewBodies;
		private Circle2f shape;
		
		public PerceivableIterator(TurtleObject perceiver,Circle2f shape){
			//Get the bodies near the perceiver.
			this.nearBodies = new ArrayList<Body>();
			this.inViewBodies = new ArrayList<UUID>();
			this.shape = shape;
			Vec2 topLeft = new Vec2(perceiver.getPosition().x()-radius,perceiver.getPosition().y()-radius);
			Vec2 bottomRight = new Vec2(perceiver.getPosition().x()+radius,perceiver.getPosition().y()+radius);
			QueryCallback queryCallback = new QueryCallback() {

	            @Override
	            public boolean reportFixture(Fixture fixture) {
	            	Body body = fixture.getBody();
	                if(body.getUserData() instanceof UUID){
	                	nearBodies.add(body);
	                }  
	                return true;
	            }

	        };
			perceiver.getBox().getWorld().queryAABB(queryCallback, new AABB(topLeft,bottomRight));
			this.searchNexts();
		}
		
		public void searchNexts(){
			while(!this.nearBodies.isEmpty() && this.inViewBodies.isEmpty()){
				Body b = nearBodies.remove(0);				
				Shape2f bodyShape = ShapeMapper.getShapeFromFixture(b.getFixtureList());
				if(shape.intersects(this.shape)){
					inViewBodies.add((UUID)b.getUserData());
				}	
			}
		}
		
		@Override
		public UUID next(){
			UUID id = inViewBodies.remove(0);
			this.searchNexts();
			return id;
		}
		
		@Override
		public boolean hasNext(){
			return !this.inViewBodies.isEmpty();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Frustrum iterator remove not supported");
		}
	}
}
