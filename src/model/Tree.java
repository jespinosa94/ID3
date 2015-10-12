package model;

import java.util.ArrayList;
import java.util.List;

public class Tree {
	public Node root = new Node();

	public Tree() {

	}

	public String AuxTraverse() {
		String s = null;

		if (root.label == null) {

		}

		return s;
	}

	public List<String> Traverse(List<String> expression) {
		for (int i = 0; i < root.sons.size(); i++) {
			if (root.a.getId() != -1) {
				expression.add(Integer.toString(root.a.getId()));
				root.sons.get(i).Traverse(expression);

			} else if (root.v.GetName() != null) {
				expression.add(root.v.GetName());
				root.sons.get(i).Traverse(expression);

			} else
				root.sons.get(i).Traverse(expression);
		}

		return expression;
	}

	public String toString() {
		return root.toString();
	}
}
