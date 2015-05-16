package io.sarl.jaak.util;

import javafx.util.Pair;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
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
				//TODO: Make sure the right vertices are used.
				res = new Rectangle2f(new Point2f(poly.getVertex(0).x,poly.getVertex(0).y),new Point2f(poly.getVertex(2).x,poly.getVertex(2).y));
			}
		}
		return res;
	}
	
	public static Pair<Point2f,Shape> getJboxShapeFromShape(Shape2f shape){
		Shape resShape = null;
		Point2f resPos=null;
		Pair<Point2f,Shape> res =null;
		if(shape instanceof Circle2f){
			resShape = new CircleShape();
			resShape.setRadius(((Circle2f) shape).getRadius());
			resPos = new Point2f(((Circle2f) shape).getX(),((Circle2f) shape).getY());
			res = new Pair<Point2f,Shape>(resPos,resShape);
		}
		return res;
	}

}
