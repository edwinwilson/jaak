package io.sarl.jaak.util;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Fixture;

public final class ShapeMapper {
	
	public static Shape2f getShapeFromFixture(Fixture fixture){
		Shape2f res = null;
		if(fixture.getShape().getType() == ShapeType.CIRCLE){
			res = new Circle2f(fixture.getBody().getPosition().x,fixture.getBody().getPosition().y,fixture.getShape().getRadius());
		}
		else if(fixture.getShape().getType() == ShapeType.POLYGON){
			PolygonShape poly = (PolygonShape)fixture.getShape();
			//We only handle rectangle
			if(poly.getVertexCount()==4){
				res = new Rectangle2f(new Point2f(poly.getVertex(0).x,poly.getVertex(0).y),new Point2f(poly.getVertex(2).x,poly.getVertex(2).y));
			}
		}
		return res;
	}
}
