package com.microscope.lensless;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.microscope.lensless.controller.filestorage.FileStorage;
import com.microscope.lensless.models.HoloJProcessor;
import com.microscope.lensless.util.HoloJUtils;
import com.microscope.plugin.HoloJ_;

import ij.ImageJ;
import ij.ImagePlus;
import ij.measure.Calibration;

@SpringBootApplication
public class LenslessMicroscopeApplication implements CommandLineRunner {
	
	
	
		//Dummy Values for testing
		private static String hologramPath="R:\\College\\Project\\Matlab_CODE\\Matlab_CODE\\Petri_dish_simulation_lensfree_hologram.tif";
		private static String refrencePath="R:\\College\\Project\\Matlab_CODE\\Matlab_CODE\\Petri_dish_simulation_lensfree_hologram.tif";
	    private static boolean butterworth=true;
	    private static boolean amplitude=true;
	    private static boolean phase=true;
		private static double dx = 0.00000345;
		private static double dy = 0.00000345;
		private static double distance = 0.00899;
		private static double wavelength = 0.000000633;
		private static double tolerance=3.0;
		private static int iterations=1;
		private static int sigma=2;
		
		@Resource
		FileStorage fileStorage;
	    
		private static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		//context=SpringApplication.run(LenslessMicroscopeApplication.class);
		context=new SpringApplicationBuilder(LenslessMicroscopeApplication.class)
		        .headless(false).run(args);
	//	System.out.println("0. Strating Plugin");
		//startPlugin();
	//	System.out.println("1. Starting image processing");
		//HoloJUtils utils=context.getBean(HoloJUtils.class);
//		
	//operate(hologramPath,refrencePath,dx,dy,distance,wavelength,tolerance,iterations,sigma,butterworth,amplitude,phase);
//		
	}
	
	
	/*
	 * public static void startPlugin() { // TODO Auto-generated method stub
	 * Class<?> clazz = HoloJ_.class; String url = clazz.getResource("/" +
	 * clazz.getName().replace('.', '/') + ".class").toString(); String pluginsDir =
	 * url.substring("file:".length(), url.length() - clazz.getName().length() -
	 * ".class".length()); System.setProperty("plugins.dir", pluginsDir);
	 * 
	 * //1. start ImageJ new ImageJ(); }
	 * 
	 * public static void operate(String hologramPath, String refrencePath, double
	 * dx, double dy, double distance, double wavelength, double tolerance, int
	 * iterations, int sigma, boolean butterworth, boolean amplitude, boolean phase)
	 * { // TODO Auto-generated method stub System.out.println(dx);
	 * System.out.println(dy); System.out.println(distance);
	 * System.out.println(wavelength); System.out.println(tolerance);
	 * System.out.println(iterations); System.out.println(sigma);
	 * System.out.println(butterworth); System.out.println(amplitude);
	 * System.out.println(phase); System.out.println(hologramPath);
	 * System.out.println(refrencePath);
	 * System.out.println("2. Inside Operate() method"); HoloJUtils
	 * utils=context.getBean(HoloJUtils.class);
	 * 
	 * System.out.println("3. Fetching Hologram Image"); ImagePlus
	 * hologramImage=utils.getImage(hologramPath);
	 * 
	 * System.out.println("4.creating HolojProcessor for Hologram"); HoloJProcessor
	 * hologram=context.getBean(HoloJProcessor.class);
	 * hologram.createHoloJProcessorWithImage(hologramImage.getProcessor(), dx, dy);
	 * 
	 * System.out.println("5. Fetching Refrence Image"); ImagePlus
	 * refrenceImage=utils.getImage(refrencePath);
	 * 
	 * System.out.println("6.creating HolojProcessor for Hologram"); HoloJProcessor
	 * reference=context.getBean(HoloJProcessor.class);
	 * reference.createHoloJProcessorWithImage(refrenceImage.getProcessor(), dx,
	 * dy); HoloJProcessor reconstructImage=null; if (hologram == null) throw new
	 * ArrayStoreException("reconstruct: No hologram selected."); else {
	 * System.out.println("7. Starting Image reconstruction");
	 * reconstructImage=utils.reconstruct(hologram, reference, distance, wavelength,
	 * iterations, tolerance, sigma); }
	 * System.out.println("***** Showing Image ******* ");
	 * reconstructImage.setTitle(" New image");
	 * reconstructImage.showHolo("Hologram : "+reconstructImage.getTitle()+" :");
	 * reconstructImage.showAmplitude("Amplitude");
	 * 
	 * 
	 * }
	 */
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		fileStorage.deleteAll();
		fileStorage.init();
	}

}
