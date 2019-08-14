package com.microscope.lensless.models;

public class ReconstructRequestBody {
	
	/*
	 * data['tolerance']=$("#tolerance").val();
	 *  data['radius']=$("#radius").val();
	 * data['iterations']=$("#iterations").val();
	 * data['phase']=$("#phase").prop('checked');
	 * data['amplitude']=$("#amplitude").prop('checked');
	 * data['butterworth']=$("#butterworth").prop('checked');
	 * data['dx']=$("#dx").val();
	 *  data['dy']=$("#dy").val();
	 * data['wavelength']=$("#wavelength").val(); 
	 * data['distance']=$
	 */
//	public void operate(String hologramPath, String refrencePath, double dx, double dy, double distance,
//			double wavelength, double tolerance, int iterations, int sigma, boolean butterworth, boolean amplitude,
//			boolean phase);
	
	
	private double tolerance;
	private int radius;
	private int iterations;
	private boolean phase;
	private boolean amplitude;
	private boolean butterworth;
	private double dx;
	private double dy;
	private double wavelength;
	private double distance;
	public double getTolerance() {
		return tolerance;
	}
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getIterations() {
		return iterations;
	}
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public boolean isPhase() {
		return phase;
	}
	public void setPhase(boolean phase) {
		this.phase = phase;
	}
	public boolean isAmplitude() {
		return amplitude;
	}
	public void setAmplitude(boolean amplitude) {
		this.amplitude = amplitude;
	}
	public boolean isButterworth() {
		return butterworth;
	}
	public void setButterworth(boolean butterworth) {
		this.butterworth = butterworth;
	}
	public double getDx() {
		return dx;
	}
	public void setDx(double dx) {
		this.dx = dx;
	}
	public double getDy() {
		return dy;
	}
	public void setDy(double dy) {
		this.dy = dy;
	}
	public double getWavelength() {
		return wavelength;
	}
	public void setWavelength(double wavelength) {
		this.wavelength = wavelength;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	
	
	
	
	
	
}
