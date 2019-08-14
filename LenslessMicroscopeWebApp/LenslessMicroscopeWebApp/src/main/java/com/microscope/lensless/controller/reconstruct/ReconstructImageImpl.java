package com.microscope.lensless.controller.reconstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.microscope.lensless.models.HoloJProcessor;
import com.microscope.lensless.util.HoloJUtils;
import com.microscope.plugin.HoloJ_;

import ij.ImageJ;
import ij.ImagePlus;

@Component
public class ReconstructImageImpl implements ReconstructImage,ApplicationContextAware{
	
	private static ApplicationContext context;
	
	@Override
	public void startPlugin() {
		// TODO Auto-generated method stub
		Class<?> clazz = HoloJ_.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
		System.setProperty("plugins.dir", pluginsDir);

		//1. start ImageJ
		new ImageJ();
	}
	@Override
	public void operate(String hologramPath, String refrencePath, double dx, double dy, double distance,
			double wavelength, double tolerance, int iterations, int sigma, boolean butterworth, boolean amplitude,
			boolean phase) {
		
		System.out.println(dx);
		System.out.println(dy);
		System.out.println(distance);
		System.out.println(wavelength);
		System.out.println(tolerance);
		System.out.println(iterations);
		System.out.println(sigma);
		System.out.println(butterworth);
		System.out.println(amplitude);
		System.out.println(phase);
		System.out.println(hologramPath);
		System.out.println(refrencePath);
		// TODO Auto-generated method stub
		System.out.println("2. Inside Operate() method");
		HoloJUtils utils=context.getBean(HoloJUtils.class);
		 
		System.out.println("3. Fetching Hologram Image");
		ImagePlus hologramImage=utils.getImage(hologramPath);
		
		System.out.println("4.creating HolojProcessor for Hologram");
        HoloJProcessor hologram=context.getBean(HoloJProcessor.class); 
        hologram.createHoloJProcessorWithImage(hologramImage.getProcessor(), dx, dy);
    
        System.out.println("5. Fetching Refrence Image");
		ImagePlus refrenceImage=utils.getImage(refrencePath);
		
		System.out.println("6.creating HolojProcessor for Hologram");
        HoloJProcessor reference=context.getBean(HoloJProcessor.class); 
        reference.createHoloJProcessorWithImage(refrenceImage.getProcessor(), dx, dy);
        HoloJProcessor reconstructImage=null;
        if (hologram == null) 
            throw new ArrayStoreException("reconstruct: No hologram selected.");
        else {
        	System.out.println("7. Starting Image reconstruction");
			reconstructImage=utils.reconstruct(hologram, reference, distance, wavelength, iterations, tolerance, sigma); 
		}
        System.out.println("***** Showing Image ******* ");
        reconstructImage.setTitle(" New image");
        reconstructImage.showHolo("Hologram : "+reconstructImage.getTitle()+" :");
        reconstructImage.showAmplitude("Amplitude");
			
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;

	}
	

}
