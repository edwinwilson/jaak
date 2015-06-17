package io.sarl.jaak.environment.internal.solver;

import io.sarl.jaak.environment.external.influence.Influence;
import io.sarl.jaak.environment.external.influence.MotionInfluence;
import io.sarl.jaak.environment.internal.model.RealTurtleBody;

import java.util.Collection;

import org.jbox2d.common.Vec2;

public class Box2DInfluenceSolver {
public void solve(Collection<RealTurtleBody> bodies) {
		
		if(bodies !=null){
			MotionInfluence mi;
			for(RealTurtleBody body : bodies){
				mi = body.consumeMotionInfluence();
				if(mi == null){
					mi = new MotionInfluence(body);
					applyInfluence(mi);
				}
				
			}
		}	
	}
	
	
	protected void applyInfluence(Influence influence) {
		if (influence instanceof MotionInfluence) {
			MotionInfluence fi = (MotionInfluence)influence;
			//applyMotionInfluence to Jbox2D
			fi.getMovedObject().getBox().applyLinearImpulse(new Vec2(fi.getLinearMotionX(),fi.getLinearMotionY() ), 
					new Vec2(fi.getMovedObject().getPosition().x(),fi.getMovedObject().getPosition().y())); 
			fi.getMovedObject().getBox().applyAngularImpulse(fi.getAngularMotion());
		}
	}
}
