package org.uma.jmetalsp.problem.prbd;

public class PRBDMatrixData {
	private int origin ;
	private int destiny ;
	private int instant;
	private boolean ride;
	private boolean served;
	private int busId;
	Object matrixIdentifier ;

	public PRBDMatrixData(Object id, int x, int y, int instant) {
	    this.matrixIdentifier = id ;
	    this.origin = x ;
	    this.destiny = y ;
	    this.instant = instant ;
	    this.ride = false;
	    this.served = false;
	    this.busId = -1;
	}

	public int getOrigin() {
	    return origin;
	}
	
	public boolean getRide() {
		return ride;
	}
	
	public void setRide(boolean ride) {
		this.ride = ride;
	}
	
	public boolean getServed() {
		return served;
	}
	
	public void setServed(boolean served) {
		this.served = served;
	}

	public int getDestiny() {
	    return destiny;
	}

	public int getInstant() {
		return instant;
	}

	public Object getMatrixIdentifier() {
	    return matrixIdentifier;
	}
	
	public void setBus(int busId) {
		this.busId = busId;
	}
	
	public int getBusId() {
		return busId;
	}
	
	public void reset() {
		this.ride = false;
	    this.served = false;
	    this.busId = -1;
	}

	@Override
	public String toString() {
		return "PRBDMatrixData [origin=" + origin + ", destiny=" + destiny + ", instant=" + instant + ", ride=" + ride
				+ ", served=" + served + "]";
	}
	
	
}
