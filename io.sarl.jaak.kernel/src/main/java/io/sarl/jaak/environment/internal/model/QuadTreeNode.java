package io.sarl.jaak.environment.internal.model;

import io.sarl.jaak.environment.external.perception.EnvironmentalObject;

import java.util.ArrayList;
import java.util.Collection;

public class QuadTreeNode {
	
	private QuadTreeNode parent;
	private Collection<EnvironmentalObject> nodeObjects;
	private Collection<QuadTreeNode> children;

	public QuadTreeNode(){
		this(null,null,null);
	}
	
	public QuadTreeNode(Collection<EnvironmentalObject> nodeObjects){
		this(null, nodeObjects, null);
	}
	
	public QuadTreeNode(QuadTreeNode parent, Collection<EnvironmentalObject> nodeObjects){
		this(parent,nodeObjects,null);
	}
	
	public QuadTreeNode(Collection<EnvironmentalObject> nodeObjects, Collection<QuadTreeNode> children){
		this(null,nodeObjects,children);
	}
	
	public QuadTreeNode(Collection<QuadTreeNode> children, QuadTreeNode parent){
		this(parent,null,children);
	}
	
	public QuadTreeNode(QuadTreeNode parent, Collection<EnvironmentalObject> nodeObjects, Collection<QuadTreeNode> children){
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



	public Collection<EnvironmentalObject> getNodeObjects() {
		return nodeObjects;
	}



	public void setNodeObjects(Collection<EnvironmentalObject> nodeObjects) {
		this.nodeObjects = nodeObjects;
	}



	public Collection<QuadTreeNode> getChildren() {
		return this.children;
	}



	public Collection<EnvironmentalObject> getEnvObjects() {
		return this.nodeObjects;
	}
	
	
}
