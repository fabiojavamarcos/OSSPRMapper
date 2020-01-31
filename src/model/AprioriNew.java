package model;

import java.util.ArrayList;

public class AprioriNew {
	
	private int pr;
	private ArrayList<String> generals;
	public AprioriNew() {
		super();
		generals = new ArrayList<String>();
	}
	public int getPr() {
		return pr;
	}
	public void setPr(int pr) {
		this.pr = pr;
	}
	public ArrayList<String> getGenerals() {
		return generals;
	}
	public void setGenerals(ArrayList<String> generals) {
		this.generals = generals;
	}
	public void insertGeneral(String general) {
		generals.add(general);
	}

}
