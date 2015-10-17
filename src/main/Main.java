package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
//import java.util.Set;

import model.Attribute;
import model.Node;
import model.Tree;

public class Main {
	static int numLabels = 0;

	public static void main(String[] args) throws IOException {
		float fraction = 0;
		Map<Integer, List<String>> space = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> training = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> validation = new HashMap<Integer, List<String>>();
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute target = new Attribute();
		Tree decisionTree = new Tree();
		ArrayList<List<String>> expressions = new ArrayList<List<String>>();
		float accuracy=0, sensitivity=0, specificity=0;
		ArrayList<List<String>> auxE = new ArrayList<List<String>>();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (i == 0) {
				space = ReadFile(arg);

				SetAttributes(space, attributes);
			}
			if (i == 1) {
				target = new Attribute(attributes.get(Integer.parseInt(arg)));

				// System.out.println(target);
				attributes.remove(Integer.parseInt(arg));
			}
			if (i == 2) {
				fraction = Float.parseFloat(arg);
				if (fraction >= 0 && fraction <= 1) {
					// reduced-error prunning
				} else
					System.out.println("fraction not valid");
			}
		}

		ConstructSets(space, training, validation, fraction);

		target.setTar(true);
		target.SetValues(training, target);
		target.setPN(training, target);

		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).SetValues(training, target);
		}
		decisionTree.root = ID3(training, target, attributes, new Node());

		CalculateExpressions(expressions, decisionTree);
		auxE = ProcessExpressions(expressions);
		System.out.println("-----------------------Before the pruning-------------------------------");
		ShowExpressions(expressions);
		accuracy = Accuracy(auxE, training, target);
		System.out.println("Accuracy for the training set: " + accuracy);
		accuracy =Accuracy(auxE, validation, target);
		System.out.println("Accuracy for the validation set: " + accuracy);
		 
		Pruning(expressions, validation, target, accuracy);
		System.out.println("-----------------------After the pruning-------------------------------");
		ShowExpressions(expressions);
		accuracy = Accuracy(auxE, training, target);
		System.out.println("Accuracy for the training set: " + accuracy);
		accuracy =Accuracy(auxE, validation, target);
		System.out.println("Accuracy for the validation set: " + accuracy);
		float[] pack = SS(auxE, validation, target); 	//Calculates sensitivity and specificity
		sensitivity = pack[0];
		specificity = pack[1];
		System.out.println("Sensitivity: " + sensitivity);
		System.out.println("Specificity: " + specificity);
		
		System.out.println("finished");
	}

	private static float[] SS(ArrayList<List<String>> auxE, Map<Integer, List<String>> validation, Attribute target) {
		int TP = 0, FP = 0, TN = 0, FN = 0;
		float sensitivity=0, specificity=0; 
		
		Integer aux[] = validation.keySet().toArray(new Integer[0]);
		for(int i=0; i<aux.length; i++){
//			System.out.println(validation.get(aux[i]));
			boolean noCount = false;
			for(int j=0; j<auxE.size(); j++){
//				System.out.println(auxE.get(j));
				if(validation.get(aux[i]).containsAll(auxE.get(j))){
					if(validation.get(aux[i]).get(target.getId()).equals(target.getPossibleValues().get(0).GetName())){
						TP++;
						noCount = true;
					}
					else
						TN++;
					noCount=true;
				}
			}
			if(validation.get(aux[i]).get(target.getId()).equals(target.getPossibleValues().get(0).GetName()) && !noCount){
				FP++;
			}
			else if(!noCount)
				FN++;
		}
		sensitivity = (float) TP/(TP+FN);
		specificity = (float) TN/(TN+FP);
		return new float[] {sensitivity, specificity};
	}

	private static ArrayList<List<String>> ProcessExpressions(ArrayList<List<String>> expressions) {
//		System.out.println(validation);
		ArrayList<List<String>> auxE = new ArrayList<List<String>>();
		for(int i=0; i<expressions.size(); i++){
			List<String> a = new ArrayList<String>();
			for(int j=1; j<expressions.get(i).size(); j+=2){
				a.add(expressions.get(i).get(j));
			}
			auxE.add(a);
		}
		return auxE;
	}

	private static void CalculateExpressions(ArrayList<List<String>> expressions, Tree decisionTree) {
		/*
		 * Set the tree in expressions
		 */
		expressions = decisionTree.Traverse(expressions, new ArrayList<String>());
		/*
		 * delete duplicates
		 */
		for (int i = 0; i < expressions.size(); i++) {
			Object[] aux = expressions.get(i).toArray();
			for (Object s : aux) {
				if (expressions.get(i).indexOf(s) != expressions.get(i).lastIndexOf(s)) {
					expressions.get(i).remove(expressions.get(i).lastIndexOf(s));
				}
			}
		}
	}

	private static void ShowExpressions(ArrayList<List<String>> expressions) {
		for(int i=0; i< expressions.size(); i++){
			System.out.println(expressions.get(i));
		}
		
	}

	private static float Accuracy(ArrayList<List<String>> auxE, Map<Integer, List<String>> validation, Attribute target) {
		int TP = 0, FP = 0, TN = 0, FN = 0;
		float accuracy=0, sensitivity=0, specificity=0; 
		
		Integer aux[] = validation.keySet().toArray(new Integer[0]);
		for(int i=0; i<aux.length; i++){
//			System.out.println(validation.get(aux[i]));
			boolean noCount = false;
			for(int j=0; j<auxE.size(); j++){
//				System.out.println(auxE.get(j));
				if(validation.get(aux[i]).containsAll(auxE.get(j))){
					if(validation.get(aux[i]).get(target.getId()).equals(target.getPossibleValues().get(0).GetName())){
						TP++;
						noCount = true;
					}
					else
						TN++;
					noCount=true;
				}
			}
			if(validation.get(aux[i]).get(target.getId()).equals(target.getPossibleValues().get(0).GetName()) && !noCount){
				FP++;
			}
			else if(!noCount)
				FN++;
		}
		
		accuracy = (float) TP/(TP+FP);
		
		return accuracy;
	}

	private static void Pruning(ArrayList<List<String>> expressions, Map<Integer, List<String>> validation, Attribute target, float accuracy) {
		ArrayList<List<String>> auxE = new ArrayList<List<String>>();
		ArrayList<List<String>> pruned = new ArrayList<List<String>>();
		float estimatedAccuracy = 0;
		
		for(int i=0; i<expressions.size(); i++){
			List<String> a = new ArrayList<String>();
			for(int j=1; j<expressions.get(i).size(); j+=2){
				a.add(expressions.get(i).get(j));
			}
			auxE.add(a);
		}
		
		for(int i=0; i<auxE.size(); i++){
			for(int j=0; j<auxE.get(i).size(); j++){
				pruned = CreateAux(auxE);
				pruned.remove(auxE.get(i).get(j));
				estimatedAccuracy = Accuracy(pruned, validation, target);
				if(estimatedAccuracy > accuracy && auxE.get(i).size()>2){
					//delete el real de expressions, hay que sacarlo primero y despues llamar al metodo recursivamente
					expressions.get(i).remove(expressions.get(i).indexOf(auxE.get(i).get(j))-1);
					expressions.get(i).remove(expressions.get(i).indexOf(auxE.get(i).get(j)));
					Pruning(expressions, validation, target, estimatedAccuracy);
					
//					expressions.get(i).remove(expressions.get(i).get(expressions.get(i).indexOf(auxE.get(i).get(j))-1));
//					expressions.get(i).remove(expressions.get(i).get(expressions.get(i).indexOf(auxE.get(i).get(j))));
//					Pruning(expressions, validation, target, estimatedAccuracy);
//					System.out.println(expressions.get(i).get(expressions.get(i).indexOf(auxE.get(i).get(j))));
				}
			}
		}
		
//		System.out.println(auxE);
//		System.out.println(pruned);
		
	}

	private static ArrayList<List<String>> CreateAux(ArrayList<List<String>> auxE) {
		ArrayList<List<String>> pruned = new ArrayList<List<String>>();
		for(int i=0; i < auxE.size(); i++){
			List<String> a = new ArrayList<String>();
			for(int j=0; j<auxE.get(i).size(); j++){
				a.add(auxE.get(i).get(j));
			}
			pruned.add(a);
		}
		return pruned;
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
			Map<Integer, List<String>> validation, float fraction) {
		Random r = new Random();
		int put = r.nextInt(space.size());
		int split = (int) (fraction*100);
		int ntr = (split * space.size() / 100);
		int aux=0;
		
		for(int i=0; i<ntr; i++){
			training.put(i, space.get(put));
			put = r.nextInt(space.size());
		}
		for(int j=ntr; j<space.size(); j++){
			validation.put(aux, space.get(j));
			aux++;
		}
		
		
		
//		int split = (int) (fraction * 100);
//		Random r = new Random(); 
////		int split = r.nextInt(100);
//		int ntr = (split * space.size() / 100);
//		// int nval = space.size() - ntr;
//		int aux = 0;
//
//		for (int i = 0; i < ntr; i++)
//			training.put(i, space.get(i));
//		for (int j = ntr; j < space.size(); j++) {
//			validation.put(aux, space.get(j));
//			aux++;
//		}
	}

	private static void SetAttributes(Map<Integer, List<String>> training, List<Attribute> attributes) {
		for (int i = 0; i < training.get(0).size(); i++) {
			attributes.add(new Attribute(i));
		}
	}

	private static Node ID3(Map<Integer, List<String>> training, Attribute target, List<Attribute> attributes,
			Node parent) {
		Node root = new Node();
		int bestAttr = 0;
		if (AllEqual(training, target)) {
			Integer[] aux = training.keySet().toArray(new Integer[0]);
			// System.out.println(training.get(aux[0]).get(target.getId()));
			if (training.get(aux[0]).get(target.getId()).equals(target.getPossibleValues().get(0).GetName())) {
				numLabels++;
				root.label = target.getPossibleValues().get(0).GetName();
				root.a = target;
			} else {
				numLabels++;
				root.label = target.getPossibleValues().get(1).GetName();
				root.a = target;
			}

		} else if (attributes.isEmpty()) {
			if (target.getPn()[0] > target.getPn()[1]) {
				numLabels++;
				root.label = target.getPossibleValues().get(0).GetName();
				root.a = target;
			} else {
				numLabels++;
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
				root.sons.add(new Tree(root));
				// root.sons.get(j).parent = parent;
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
					// tr.parent = parent;
					if (target.getPn()[0] > target.getPn()[1]) {
						numLabels++;
						tr.root.label = target.getPossibleValues().get(0).GetName();
						tr.root.a = target;
					}

					else {
						numLabels++;
						tr.root.label = target.getPossibleValues().get(1).GetName();
						tr.root.a = target;
					}

					root.sons.get(j).root.sons.add(tr);
				} else {
					Tree tr = new Tree(root.sons.get(j).root);
					// tr.parent = parent;
					tr.root = ID3(root.sons.get(j).root.examples, target, attributes, root);
					root.sons.get(j).root.sons.add(tr);
					// root.sons.get(j).root.sons.get(0).parent =
					// root.sons.get(j).root;
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
