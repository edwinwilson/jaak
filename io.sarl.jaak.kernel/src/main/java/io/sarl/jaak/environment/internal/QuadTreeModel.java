package io.sarl.jaak.environment.internal;

import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.internal.model.QuadTreeNode;

import org.arakhne.afc.math.continous.object2d.Point2f;

public interface QuadTreeModel {
	

	//TODO javadoc
	float getWidth();
	
	float getHeight();
	
	QuadTreeNode getNode(Point2f position);
	
	QuadTreeNode getObjectNode(EnvironmentalObject worldObject);

	Iterable<? extends EnvironmentalObject> getEnvObjects(Point2f position);
	
	Iterable<? extends EnvironmentalObject> getEnvObjects(QuadTreeNode node);
	
	Iterable<TurtleBody> getTurtles(QuadTreeNode node);
	
	Iterable<TurtleBody> getTurtles(Point2f position);
	
	ValidationResult validatePosition(boolean isWrapped, boolean allowDiscard, Point2f position);
}
