package model;

public class Value {
	private String name;
	private int pn[] = new int[2];

	public Value() {
		name = null;
		pn[0] = 0;
		pn[1] = 0;
	}

	public Value(String name) {
		this.name = name;
		pn[0] = 0;
		pn[1] = 0;
	}

	public String toString() {
		String s;
		s = name;
		// s += "++" + pn[0] + ", " + pn[1] + "++";
		return s;
	}

	public String GetName() {
		return name;
	}

	public void setPn(Attribute target, Attribute attr) {
		// for(int i=0; i<target.getValues().size(); i++){
		// if(attr.getValues().get(i).equals(this.name)){
		// if(target.getValues().get(i).equals(target.getPositiveValue()))
		// pn[0]++;
		// else
		// pn[1]++;
		// }
		// }
	}

	public void SumPositive() {
		pn[0]++;

	}

	public void SumNegative() {
		pn[1]++;

	}

	public float Entropy() {
		float total = pn[0] + pn[1];
		total = -((float) pn[0] / total) * (float) (Math.log10((float) pn[0] / total) / Math.log10(2.))
				- ((float) pn[1] / total) * (float) (Math.log10((float) pn[1] / total) / Math.log10(2.));
		if (total != total)
			total = 0;
		return total;
	}

	public float SumPN() {
		return (float) pn[0] + pn[1];
	}

	public int[] getPn() {
		return pn;
	}

	public void setPn(int[] pn) {
		this.pn = pn;
	}

}
