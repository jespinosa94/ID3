package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import model.Attribute;
import model.Node;
import model.Tree;

public class Main {
	public static void main(String[] args) throws IOException {
		Map<Integer, List<String>> space = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> training = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> validation = new HashMap<Integer, List<String>>();
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute target = new Attribute();
		Tree decisionTree = new Tree();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (i == 0) {
				space = ReadFile(arg);
				ConstructSets(space, training, validation);
				SetAttributes(training, attributes);
			}
			if (i == 1) {
				target = new Attribute(attributes.get(Integer.parseInt(arg)));
				target.setTar(true);
				target.SetValues(space /***** training */
				, target);
				target.setPN(space/***** training */
				, target);
				// System.out.println(target);
				attributes.remove(Integer.parseInt(arg));
			}
			if (i == 2) {
				float fraction = Float.parseFloat(arg);
				if (fraction >= 0 && fraction <= 1) {
					// reduced-error prunning
				} else
					System.out.println("fraction not valid");
			}
		}

		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).SetValues(space/***** training */
			, target);
		}
		decisionTree.root = ID3(space/***** training */
		, target, attributes);
		AnalyzeTree(decisionTree);

		System.out.println("finished");
	}

	private static void AnalyzeTree(Tree dt) {
		List<List<String>> expressions = new ArrayList<List<String>>();
		List<String> path = new ArrayList<String>();

		dt.Traverse(path);
		System.out.println(path);

	}

	public static Map<Integer, List<String>> ReadFile(String arg) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(arg));
		Map<Integer, List<String>> s = new HashMap<Integer, List<String>>();
		String line = null;
		int id = 0;

		while ((line = br.readLine()) != null) {
			List<String> hypothesis = new ArrayList<String>();
			Scanner in = new Scanner(line);
			in.useDelimiter(",");

			while (in.hasNext()) {
				String value = in.next();
				hypothesis.add(value);
			}
			s.put(id, hypothesis);
			id++;
		}
		br.close();
		return s;
	}

	public static void ConstructSets(Map<Integer, List<String>> space, Map<Integer, List<String>> training,
			Map<Integer, List<String>> validation) {
		Random r = new Random();
		int split = r.nextInt(100);
		int ntr = (split * space.size() / 100);
		int nval = space.size() - ntr;
		int aux = 0;

		for (int i = 0; i < ntr; i++)
			training.put(i, space.get(i));
		for (int j = ntr; j < space.size(); j++) {
			validation.put(aux, space.get(j));
			aux++;
		}
	}

	private static void SetAttributes(Map<Integer, List<String>> training, List<Attribute> attributes) {
		for (int i = 0; i < training.get(0).size(); i++) {
			attributes.add(new Attribute(i));
		}
	}

	private static Node ID3(Map<Integer, List<String>> training, Attribute target, List<Attribute> attributes) {
		Node root = new Node();
		int bestAttr = 0;
		if (AllEqual(training, target)) {
			root.label = target.getPossibleValues().get(0).GetName();
			root.a = target;
		}

		else if (attributes.isEmpty()) {
			if (target.getPn()[0] > target.getPn()[1]) {
				root.label = target.getPossibleValues().get(0).GetName();
				root.a = target;
			} else {
				root.label = target.getPossibleValues().get(1).GetName();
				root.a = target;
			}
		} else {
			bestAttr = CalculateBestAttr(attributes, training, target);

			root.a = attributes.get(bestAttr);
			root.examples = training;
			attributes.remove(bestAttr);
			/*
			 * Creation of the attribute sons
			 */
			for (int j = 0; j < root.a.getPossibleValues().size(); j++) {
				root.sons.add(new Tree());
				root.sons.get(j).root.v = root.a.getPossibleValues().get(j);
				/*
				 * Examples vi converting in the examples that have value vi for
				 * A
				 */
				Integer[] aux = root.examples.keySet().toArray(new Integer[0]);

				for (int k = 1; k < aux.length; k++) {
					// for(int k=0; k < root.examples.size(); k++){
					if (root.examples.get(aux[k]).get(root.a.getId()).equals(root.sons.get(j).root.v.GetName())) {
						root.sons.get(j).root.examples.put(aux[k], root.examples.get(aux[k]));
					}
				}
				if (root.sons.get(j).root.examples.isEmpty()) {
					Tree tr = new Tree();
					if (target.getPn()[0] > target.getPn()[1]) {
						tr.root.label = target.getPossibleValues().get(0).GetName();
						tr.root.a = target;
					}

					else {
						tr.root.label = target.getPossibleValues().get(1).GetName();
						tr.root.a = target;
					}

					root.sons.get(j).root.sons.add(tr);
				} else {
					Tree tr = new Tree();
					tr.root = ID3(root.sons.get(j).root.examples, target, attributes);
					root.sons.get(j).root.sons.add(tr);
				}
			}
		}
		return root;
	}

	private static int CalculateBestAttr(List<Attribute> attributes, Map<Integer, List<String>> training,
			Attribute target) {
		int bestAttr = 0;
		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).setPN(training, target);
			if (attributes.get(i).gain > attributes.get(bestAttr).gain)
				bestAttr = i;
		}
		return bestAttr;
	}

	private static boolean AllEqual(Map<Integer, List<String>> training, Attribute target) {
		Integer[] aux = training.keySet().toArray(new Integer[0]);

		for (int i = 1; i < aux.length; i++) {
			if (!training.get(aux[i]).get(target.getId()).equals(training.get(aux[0]).get(target.getId())))
				return false;
		}
		return true;
	}
}
