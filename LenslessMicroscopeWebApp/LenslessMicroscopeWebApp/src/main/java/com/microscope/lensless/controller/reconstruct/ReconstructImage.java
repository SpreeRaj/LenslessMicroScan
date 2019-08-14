package com.microscope.lensless.controller.reconstruct;

public interface ReconstructImage {

	public void startPlugin();

	public void operate(String hologramPath, String refrencePath, double dx, double dy, double distance,
			double wavelength, double tolerance, int iterations, int sigma, boolean butterworth, boolean amplitude,
			boolean phase);

}
