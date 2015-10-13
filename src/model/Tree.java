package model;

import java.util.ArrayList;
import java.util.List;

public class Tree {
	public Node root = new Node();
	public Node parent = new Node();

	public Tree() {

	}

	public String AuxTraverse() {
		String s = null;

		if (root.label == null) {

		}

		return s;
	}

	public List<String> Traverse(List<String> expression, int numLabels) {
		if(root.label!=null){
			expression.add(Integer.toString(root.a.getId()));
			expression.add(root.label);
		}
		else{
			if(root.v.GetName()==null){
				expression.add(Integer.toString(root.a.getId()));
			}
			else{
				expression.add(root.v.GetName());
			}
			
			for(int i=0; i<root.sons.size(); i++){
				if(parent.a.getId()!=-1){
					expression.add(Integer.toString(parent.a.getId()));
				}
				root.sons.get(i).Traverse(expression, numLabels);
				
				
			}
		}

		return expression;
	}

	public String toString() {
		return root.toString();
	}
}
