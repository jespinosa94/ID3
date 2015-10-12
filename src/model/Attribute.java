package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Attribute {
	private int id;
	private List<Value> possibleValues = new ArrayList<Value>();
	private int[] pn = new int[2];
	private boolean tar;
	public float gain;

	public Attribute() {
		id = -1;
		tar = false;
		gain = 0;
	}

	public Attribute(int id) {
		this.id = id;
		tar = false;
		gain = 0;
	}

	public Attribute(Attribute a) {
		id = a.id;
		for (int i = 0; i < possibleValues.size(); i++)
			possibleValues.add(a.possibleValues.get(i));
		pn[0] = a.pn[0];
		pn[1] = a.pn[1];
		tar = a.tar;
		gain = a.gain;
	}

	public String toString() {
		String n;
		n = Integer.toString(id) + "[";
		for (int i = 0; i < possibleValues.size(); i++) {
			n = n + possibleValues.get(i);
		}
		n += "]";
		n += gain;
		// n+= "{" + pn[0] + ", " + pn[1] + "}";
		return n;
	}

	/*
	 * Set the possible values, the possitive, and the negative ones
	 */
	public void SetValues(Map<Integer, List<String>> space, Attribute target) {
		List<String> aux = new ArrayList<String>();
		for (int i = 0; i < space.size(); i++) {
			// System.out.println(space.get(i).get(id));
			if (!aux.contains(space.get(i).get(id))) {
				aux.add(space.get(i).get(id));
				possibleValues.add(new Value(space.get(i).get(id)));
			}
		}
	}

	public boolean isTar() {
		return tar;
	}

	public void setTar(boolean tar) {
		this.tar = tar;
	}

	public void setPN(Map<Integer, List<String>> space, Attribute target) {
		Integer[] aux = space.keySet().toArray(new Integer[0]);

		for (int i = 0; i < aux.length; i++) {
			if (space.get(aux[i]).get(target.id).equals(target.possibleValues.get(0).GetName()))
				pn[0]++;
			else
				pn[1]++;

			for (int j = 0; j < possibleValues.size(); j++) {
				if (possibleValues.get(j).GetName().equals(space.get(aux[i]).get(id))
						&& space.get(aux[i]).get(target.id).equals(target.possibleValues.get(0).GetName())) {
					possibleValues.get(j).SumPositive();
				} else if (possibleValues.get(j).GetName().equals(space.get(aux[i]).get(id))
						&& space.get(aux[i]).get(target.id).equals(target.possibleValues.get(1).GetName()))
					possibleValues.get(j).SumNegative();
			}
		}

		// for (int i = 0; i < space.size(); i++) {
		// if (space.get(i).get(target.id)
		// .equals(target.possibleValues.get(0).GetName()))
		// pn[0]++;
		// else
		// pn[1]++;
		//
		// for (int j = 0; j < possibleValues.size(); j++) {
		// if (possibleValues.get(j).GetName()
		// .equals(space.get(i).get(id))
		// && space.get(i).get(target.id)
		// .equals(target.possibleValues.get(0).GetName())) {
		// possibleValues.get(j).SumPositive();
		// } else if (possibleValues.get(j).GetName()
		// .equals(space.get(i).get(id))
		// && space.get(i).get(target.id)
		// .equals(target.possibleValues.get(1).GetName()))
		// possibleValues.get(j).SumNegative();
		// }
		// }
		// CalculateGain();
	}

	private void CalculateGain() {
		float sumOfValues = 0;
		for (int i = 0; i < possibleValues.size(); i++) {
			sumOfValues = sumOfValues - (possibleValues.get(i).SumPN() / SumPN()) * possibleValues.get(i).Entropy();
		}
		gain = Entropy() + sumOfValues;
	}

	public float SumPN() {
		return (float) pn[0] + pn[1];
	}

	public float Entropy() {
		float total = pn[0] + pn[1];
		total = -((float) pn[0] / total) * (float) (Math.log10((float) pn[0] / total) / Math.log10(2.))
				- ((float) pn[1] / total) * (float) (Math.log10((float) pn[1] / total) / Math.log10(2.));
		if (total != total)
			total = 0;
		return total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Value> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(List<Value> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public int[] getPn() {
		return pn;
	}

}
