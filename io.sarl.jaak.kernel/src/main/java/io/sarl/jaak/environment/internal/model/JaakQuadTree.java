package io.sarl.jaak.environment.internal.model;

import io.sarl.jaak.environment.external.body.TurtleBody;
import io.sarl.jaak.environment.external.endogenous.AutonomousEndogenousProcess;
import io.sarl.jaak.environment.external.perception.JaakObject;
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
	private QuadTreeNode root;
	private final ObjectManipulator objectManipulator;
	private final Collection<AutonomousEndogenousProcess> autonomousProcesses = new LinkedList<>();
	private static final int SPLITCOUNT = 100; 
	
	
	public JaakQuadTree(float width, float height, ObjectManipulator objectManipulator, Collection<JaakObject> envObjects){
		assert (width > 0);
		assert (height > 0);
		assert (objectManipulator != null);
		this.width = width;
		this.height = height;
		this.objectManipulator = objectManipulator;
		buildTree(envObjects);
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
	public Iterable<? extends JaakObject> getEnvObjects(Point2f position) {
		QuadTreeNode node = this.getNode(position);
		return node.getEnvObjects();
	}

	@Override
	public Iterable<? extends JaakObject> getEnvObjects(QuadTreeNode node) {
		return node.getEnvObjects();
	}

	@Override
	public Iterable<TurtleBody> getTurtles(QuadTreeNode node) {
		Collection<JaakObject> envObjects = node.getEnvObjects();
		List<TurtleBody> result = new ArrayList<>();
		for(JaakObject envObject : envObjects){
			if(envObject instanceof TurtleBody){
				TurtleBody body = (TurtleBody)envObject;
				result.add(body);
			}
		}
		return result;
	}

	@Override
	public Iterable<TurtleBody> getTurtles(Point2f position) {
		QuadTreeNode node = this.getNode(position);
		return this.getTurtles(node);
	}

	/**
	 * Copy/pasted from JaakGrid and changed variables to float.
	 */
	@Override
	public ValidationResult validatePosition(boolean isWrapped, boolean allowDiscard, Point2f position) {
		if (allowDiscard
				&& (position.x() < 0
					|| position.x() >= getWidth()
					|| position.y() < 0
					|| position.y() >= getHeight())) {
				return ValidationResult.DISCARDED;
			}

			ValidationResult change = ValidationResult.NO_CHANGE;

			// Check X coordinate to be on grid
			while (position.x() < 0) {
				if (isWrapped) {
					float delta = -position.x();
					position.setX(getWidth() - delta);
					change = ValidationResult.WRAPPED;
				} else {
					position.setX(0);
					change = ValidationResult.CLIPPED;
				}
			}

			while (position.x() >= getWidth()) {
				if (isWrapped) {
					float delta = position.x() - getWidth();
					position.setX(delta);
					change = ValidationResult.WRAPPED;
				} else {
					position.setX(getWidth() - 1);
					change = ValidationResult.CLIPPED;
				}
			}

			// Check Y coordinate to be on grid
			while (position.y() < 0) {
				if (isWrapped) {
					float delta = -position.y();
					position.setY(getHeight() - delta);
					change = ValidationResult.WRAPPED;
				} else {
					position.setY(0);
					change = ValidationResult.CLIPPED;
				}
			}

			while (position.y() >= getHeight()) {
				if (isWrapped) {
					float delta = position.y() - getHeight();
					position.setY(delta);
					change = ValidationResult.WRAPPED;
				} else {
					position.setY(getHeight() - 1);
					change = ValidationResult.CLIPPED;
				}
			}

			return change;
	}

	@Override
	public QuadTreeNode getNode(Point2f position) {
		
	}

	@Override
	public QuadTreeNode getObjectNode(JaakObject worldObject) {
		return (this.getNode(worldObject.getPosition()));
	}
	
	public void buildTree(Collection<JaakObject> envObjects){
		this.root = new QuadTreeNode();
		root.setCutX(this.width);
		root.setCutY(this.height);
		root.setNodeObjects(envObjects);
		if(envObjects.size() > SPLITCOUNT){
			buildChildren(this.root);
		}
	}
	
	private void buildChildren(QuadTreeNode parent){
		if(parent == null){
			return;//TODO exception
		}
		
	}
	
	private int[] getEnclosingChildIndex(JaakObject envObject, QuadTreeNode node){
		
		Shape box= envObject.getShape();
		int[] res = {};
		
		if(box.getMaxX() < node.getCutX() && box.getMaxY() < node.getCutY()) {
			res[0] = 0;
			return res;
		}
		if(box.getMinX() > node.getCutX() && box.getMaxY() < node.getCutY()){
			res[0] = 1;
			return res;
		}
		if(box.getMaxX() < node.getCutX() && box.getMaxY() > node.getCutY()) {
			res[0] = 2;
			return res;
		}
		if(box.getMinX() > node.getCutX() && box.getMaxY() > node.getCutY()){
			res[0] = 3;
			return res;
		}
		if(box.getMaxX()<node.getCutX()){
			res[0] = 0;
			res[1] = 2;
			return res;
		}
		if(box.getMinX()>node.getCutX()){
			res[0] = 1;
			res[1] = 3;
			return res;
		}
		if(box.getMinX()>node.getCutX()){
			res[0] = 0;
			res[1] = 1;
			return res;
		}
		if(box.getMinX()>node.getCutX()){
			res[0] = 2;
			res[1] = 3;
			return res;
		}
		res[0] = 0;
		res[1] = 1;
		res[2] = 2;
		res[3] = 3;
		return res;
	}
}
