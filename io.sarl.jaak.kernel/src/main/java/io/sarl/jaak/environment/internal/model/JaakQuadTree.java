package io.sarl.jaak.environment.internal.model;

import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.endogenous.AutonomousEndogenousProcess;
import io.sarl.jaak.environment.external.perception.EnvironmentalObject;
import io.sarl.jaak.environment.external.perception.ObjectManipulator;
import io.sarl.jaak.environment.internal.QuadTreeModel;
import io.sarl.jaak.environment.internal.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.arakhne.afc.math.continous.object2d.Point2f;

public class JaakQuadTree implements QuadTreeModel{

	private final float width;
	private final float height;
	private final QuadTreeNode[] tree;
	private final ObjectManipulator objectManipulator;
	private final Collection<AutonomousEndogenousProcess> autonomousProcesses = new LinkedList<>();
	
	
	public JaakQuadTree(float width, float height, ObjectManipulator objectManipulator){
		assert (width > 0);
		assert (height > 0);
		assert (objectManipulator != null);
		this.width = width;
		this.height = height;
		this.tree = new QuadTreeNode[computeNumberOfNodes()];
		this.objectManipulator = objectManipulator;
	}
	
	private int computeNumberOfNodes(){
		return 0;
	}
	
	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public float getHeight() {
		return this.height;
	}

	@Override
	public Iterable<? extends EnvironmentalObject> getEnvObjects(Point2f position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<? extends EnvironmentalObject> getEnvObjects(QuadTreeNode node) {
		return node.getEnvObjects();
	}

	@Override
	public Iterable<TurtleBody> getTurtles(QuadTreeNode node) {
		Collection<EnvironmentalObject> envObjects = node.getEnvObjects();
		List<TurtleBody> result = new ArrayList<>();
		for(EnvironmentalObject envObject : envObjects){
			if(envObject instanceof TurtleBody){
				TurtleBody body = (TurtleBody)envObject;
				result.add(body);
			}
		}
		return result;
	}

	@Override
	public Iterable<TurtleBody> getTurtles(Point2f position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationResult validatePosition(boolean isWrapped, boolean allowDiscard, Point2f position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuadTreeNode getNode(Point2f position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuadTreeNode getObjectNode(EnvironmentalObject worldObject) {
		// TODO Auto-generated method stub
		return null;
	}
}
