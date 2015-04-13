package io.sarl.jaak.environment.internal.model;

import io.sarl.jaak.environment.external.perception.JaakObject;

import java.util.ArrayList;
import java.util.Collection;

public class QuadTreeNode {
	
	private QuadTreeNode parent;
	private Collection<JaakObject> nodeObjects;
	private Collection<QuadTreeNode> children;
	private float cutX;
	private float cutY;

	public QuadTreeNode(){
		this(null,null,null);
	}
	
	public QuadTreeNode(Collection<JaakObject> nodeObjects){
		this(null, nodeObjects, null);
	}
	
	public QuadTreeNode(QuadTreeNode parent, Collection<JaakObject> nodeObjects){
		this(parent,nodeObjects,null);
	}
	
	public QuadTreeNode(Collection<JaakObject> nodeObjects, Collection<QuadTreeNode> children){
		this(null,nodeObjects,children);
	}
	
	public QuadTreeNode(Collection<QuadTreeNode> children, QuadTreeNode parent){
		this(parent,null,children);
	}
	
	public QuadTreeNode(QuadTreeNode parent, Collection<JaakObject> nodeObjects, Collection<QuadTreeNode> children){
		this.parent = parent;
		if(nodeObjects == null){
			this.nodeObjects = new ArrayList<>();
		} else {
			this.nodeObjects = nodeObjects;
		}
		if(children == null){
			children = new ArrayList<>();
		} else {
			this.children = children;	
		}
	}
	
	public QuadTreeNode getParent() {
		return parent;
	}



	public void setParent(QuadTreeNode parent) {
		this.parent = parent;
	}



	public Collection<JaakObject> getNodeObjects() {
		return nodeObjects;
	}



	public void setNodeObjects(Collection<JaakObject> nodeObjects) {
		this.nodeObjects = nodeObjects;
	}



	public Collection<QuadTreeNode> getChildren() {
		return this.children;
	}



	public Collection<JaakObject> getEnvObjects() {
		return this.nodeObjects;
	}
	
	public void setCutX(float cutX){
		this.cutX = cutX;
	}
	
	public void setCutY(float cutY){
		this.cutY = cutY;
	}

	public float getCutX() {
		return this.cutX;
	}
	
	public float getCutY() {
		return this.cutY;
	}
}
