package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	public Attribute a = new Attribute();
	public Value v = new Value();
	public Map<Integer, List<String>> examples = new HashMap<Integer, List<String>>();
	public List<Tree> sons = new ArrayList<Tree>();
	public String label;

	public Node() {
		label = null;
	}

	public String toString() {
		String s = null;
		if (a.getId() == -1) {
			s += v;
		} else if (v.GetName() == null) {
			s += a;
		}

		return s;
	}
}
