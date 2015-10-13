package model;

import java.util.ArrayList;
import java.util.List;

public class Tree {
	public Node root = new Node();
	public Node parent = new Node();

	public Tree() {

	}

	public Tree(Node root2) {
		parent = root2;
	}

	public ArrayList<List<String>> Traverse(ArrayList<List<String>> expressions, List<String> s) {
		if(root.label!=null){
			s.add(Integer.toString(root.a.getId()));
			s.add(root.label);
			List<String> c = new ArrayList<String>();
			for(int i=0; i<s.size(); i++){
				c.add(s.get(i));
			}
			expressions.add(c);
			s.removeAll(s);
		}
		else{
			for(int i=0; i<root.sons.size(); i++){
//				root.sons.get(i).parent = root;
				if(parent.v.GetName()!=null || parent.a.getId()!=-1){
					if(parent.a.getId()!=-1)
						s.add(Integer.toString(parent.a.getId()));
					if(parent.v.GetName()!=null){
						s.add(expressions.get(i).get(0));
						s.add(parent.v.GetName());
					}
						
				}
				s.add(Integer.toString(root.a.getId()));
				s.add(root.sons.get(i).root.v.GetName());
				
				root.sons.get(i).root.sons.get(0).Traverse(expressions, s);
			}
		}

		return expressions;
	}

	private List<String> Extract(List<String> s) {

		
		return s;
	}

	public String toString() {
		return root.toString();
	}
}
