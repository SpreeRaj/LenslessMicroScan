package com.microscope.lensless.util;

import java.awt.Image;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.stereotype.Component;

import com.microscope.lensless.models.HoloJProcessor;

import ij.ImagePlus;
import ij.WindowManager;
import ij.io.Opener;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;

@Component
public class HoloJUtils implements ApplicationContextAware {

	private static ApplicationContext context;

	public HoloJProcessor reconstruct(HoloJProcessor hologram, HoloJProcessor reference, double distance,
			double wavelength, int iterations, double tolerance, int radius) {

		System.out.println("8. Hologram image propagate function");
		hologram = propogatefunc(hologram, distance, wavelength);
		System.out.println("9. Refrence image propagate function");
		reference = propogatefunc(reference, distance, wavelength);
		System.out.println("10. Building mask");
		 //Building Mask
		hologram=buildMask(hologram, reference, tolerance, radius, distance, wavelength,iterations);
       

		return hologram;

	}

	public ImagePlus getImage(String imagePath) {
		System.out.println("	a. Inside getImage()");
		//imagePath.replaceAll("/", "\\");
		String dir = imagePath.substring(0, imagePath.lastIndexOf('\\'));
		System.out.println("	b. dir--> " + dir);
		String name = imagePath.substring(imagePath.lastIndexOf('\\') + 1, imagePath.length());
		System.out.println("	c. image path--> " + name);
		ImagePlus imp;

		imp = getOpenedImage(name);
		if (imp == null) {
			Opener op = new Opener();
			imp = op.openImage(dir, name);
		}
		return imp;
	}

	public ImagePlus getOpenedImage(String name) {
		int[] ids = WindowManager.getIDList();
		int n = WindowManager.getImageCount();
		ImagePlus imp;

		if (n > 0) {
			for (int i = 0; i < n; i++) {
				imp = WindowManager.getImage(ids[i]);
				if (imp.getTitle() == name)
					return imp;
			}
		}
		return null;
	}

	public static HoloJProcessor propogatefunc(HoloJProcessor hologram, double distance, double wavelength) {
		// HoloJProcessor propagated = new HoloJProcessor(hologram);//redundant
		System.out.println("I. starting FFT");
		hologram.doFFT(1);// 1 for normal FFT
		System.out.println("II. Creating new Holoj object ");
		HoloJProcessor chirp = context.getBean(HoloJProcessor.class, hologram.getWidth(), hologram.getHeight(),
				hologram.getDx(), hologram.getDy(), distance, wavelength);
		System.out.println("III. Multiplying hologram with chirp" + chirp);
		hologram = multiply(chirp, hologram);
		System.out.println("IV. starting inverse FFT");
		hologram.doFFT(-1);// -1 for inverse FFT

		return hologram;
	}

	public static HoloJProcessor multiply(HoloJProcessor operand1, HoloJProcessor operand2) {
		if (operand1.getSize() != operand2.getSize())
			throw new IndexOutOfBoundsException("multiply: sizes must be equal.");
		double[] realPixels1 = operand1.getRealPixels();
		double[] realPixels2 = operand2.getRealPixels();
		double[] complexPixels1 = operand1.getImagPixels();
		double[] complexPixels2 = operand2.getImagPixels();
		double[] resultReal = new double[operand1.getSize()];
		double[] resultComplex = new double[operand1.getSize()];
		for (int i = 0; i < operand1.getSize(); i++) {
			resultReal[i] = realPixels1[i] * realPixels2[i] - complexPixels1[i] * complexPixels2[i];
			resultComplex[i] = realPixels1[i] * complexPixels2[i] + realPixels2[i] * complexPixels1[i];
		}

		HoloJProcessor result = context.getBean(HoloJProcessor.class, resultReal, resultComplex, operand1.getWidth(),
				operand1.getHeight(), operand1.getDx(), operand1.getDy());
	//	System.out.println("result ---> " + result);

		return result;
	}
	
	
	
	public static HoloJProcessor buildMask(HoloJProcessor hologram,HoloJProcessor ref, double tolerance, int radius, double distance, double wavelength, int iterations) {
		
		System.out.println("	a. Getting pixel modulus  for hologram image"); 
		 ImagePlus amplitude = hologram.makeAmplitudeImage("Amplitude");//modulus of pixels
		ImageProcessor amplitudeProcessor = amplitude.getProcessor();
        MaximumFinder max = new MaximumFinder();
        amplitudeProcessor.invert();
        Image pointsImage = max.findMaxima(amplitudeProcessor,tolerance,0,false).createImage();
        ImagePlus x = new ImagePlus("points", pointsImage);
        ImageProcessor Mask = x.getProcessor();

        //getting reference background average
        System.out.println("	b. Getting refrence image background average");
        double[] refIntensity = ref.getIntensity();
        double refAvg=0.0;
        for(int j=0;j<refIntensity.length;j++){
            refAvg+=refIntensity[j];
        }
        refAvg = refAvg/refIntensity.length;



        //Create convolution kernal
        System.out.println("	c. Create convolution kernal"); 
        float[] kernal = new float[(int)Math.pow(((2*radius)+1),2)];
        for (int n= (-radius);n<=radius;n++){
            for (int m= (-radius);m<=radius;m++){
                if(((n*n)+(m*m))<(radius*radius))
                    kernal[(n+radius)+((m+radius)*((2*radius)+1))] = 1;
                else
                    kernal[(n+radius)+((m+radius)*((2*radius)+1))] = 0;
            }
        }

            Mask.convolve(kernal,((radius*2)+1),((radius*2)+1));
//
        HoloJProcessor holoMask = context.getBean(HoloJProcessor.class,Mask);
          //WORKS FOR RADIUS LESS THAN 13
        double real[] = new double[holoMask.getSize()];
        double imag[] = new double[holoMask.getSize()];

        for(int j=0;j<holoMask.getRealPixels().length;j++){
            if(holoMask.getRealPixels()[j]>0) {
                real[j] = 1.0;
            }else if (holoMask.getRealPixels()[j]<=0){
                real[j] = 0.0;
            }
            if(holoMask.getImagPixels()[j]>0){
                imag[j] = 1.0;
            } else if (holoMask.getImagPixels()[j]<=0) {
                imag[j] = 1.0;
            }
        }
//      //create mask only once. Same mask can be used to propogate 
        holoMask.setImagPixels(imag);
        holoMask.setRealPixels(real);
        double[] recIntensity; //= recon.getIntensity();
        double recAvg=0.0;
        double scale;

        for(int i=0;i<iterations;i++){

            recIntensity = hologram.getIntensity();
            recAvg=0.0;


            for(int j=0;j<recIntensity.length;j++){
                recAvg+=recIntensity[j];
            }
            recAvg = recAvg/recIntensity.length;


            scale = Math.pow(recAvg/refAvg,0.5);

            double realMult[] = new double[holoMask.getSize()];
            double imagMult[] = new double[holoMask.getSize()];

            
            for(int j=0;j<hologram.getSize();j++){
                if( holoMask.getRealPixels()[j]>0.0) { //inside mask
                    realMult[j] =  hologram.getRealPixels()[j];
                    imagMult[j] =  hologram.getImagPixels()[j];
                } else {
                    realMult[j] = scale * ref.getRealPixels()[j];
                    imagMult[j] = scale * ref.getImagPixels()[j];

                }
            }
            hologram.setRealPixels(realMult);
            hologram.setImagPixels(imagMult);
            hologram = propogatefunc(hologram,-distance,wavelength);
            //uncomment
//            for(int j=0;j<hologram.getSize();j++){
//                scale=Math.pow(Math.pow(hologram.getRealPixels()[j],2)+Math.pow(hologram.getImagPixels()[j],2),0.5);
//                scale=scale/(Math.pow(Math.pow(hologram.getRealPixels()[j],2)+Math.pow(hologram.getImagPixels()[j],2),0.5));
//                hologram.getRealPixels()[j] = scale * hologram.getRealPixels()[j];
//                hologram.getImagPixels()[j] = scale * hologram.getImagPixels()[j];
//            }
//            hologram.show("before 2nd propagate");

            hologram = propogatefunc(hologram,distance,wavelength);
           // recon.show("after 2nd propagate");

        }

        return hologram;
	}
	
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;

	}

}
