package com.microscope.lensless.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.FHT;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

@Component
@Scope(value = "prototype")
public class HoloJProcessor {

	private static final int EXCLUDED_RADIUS = 20;
	double[] realPixels = null;
	double[] imagPixels = null;
	private int width = 0;
	private int height = 0;
	private int size = 0;
	private double dx = 0;
	private double dy = 0;
	private Calibration cal;
	private String title = null;
	private boolean isRealOrigin = true;
	private boolean isSpectrumDomain = false;

	public double[] getRealPixels() {
		return realPixels;
	}

	public void setRealPixels(double[] realPixels) {
		this.realPixels = realPixels;
	}

	public double[] getImagPixels() {
		return imagPixels;
	}

	public void setImagPixels(double[] imagPixels) {
		this.imagPixels = imagPixels;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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

	public Calibration getCal() {
		return cal;
	}

	public void setCal(Calibration cal) {
		this.cal = cal;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isRealOrigin() {
		return isRealOrigin;
	}

	public void setRealOrigin(boolean isRealOrigin) {
		this.isRealOrigin = isRealOrigin;
	}

	public boolean isSpectrumDomain() {
		return isSpectrumDomain;
	}

	public void setSpectrumDomain(boolean isSpectrumDomain) {
		this.isSpectrumDomain = isSpectrumDomain;
	}

	public static int getExcludedRadius() {
		return EXCLUDED_RADIUS;
	}

	public HoloJProcessor() {
		super();
	}

	public HoloJProcessor(int width, int height, double dx, double dy, double distance, double wavelength) {
		System.out.println("    a. Creating HoloJProcess object and computing imaginary and real pixels");
		if (width < 1) {
			throw new ArrayStoreException("Constructor: width < 1.");
		} else if (height < 1) {
			throw new ArrayStoreException("Constructor: height < 1.");
		} else {
			this.width = width;
			this.height = height;
			this.size = width * height;
			this.dx = dx;
			this.dy = dy;
			this.realPixels = new double[this.size];
			this.imagPixels = new double[this.size];
			double[] temp = new double[width * height];
			int x, y, position;
			for (int m = 0; m < height; m++) {
				for (int n = 0; n < width; n++) {
					position = m * width + n;
					y = m - height / 2;
					x = n - width / 2;
					temp[position] = Math.pow((1 - Math.pow(((wavelength * x) / (width * dx)), 2)
							- Math.pow(((wavelength * y) / (height * dy)), 2)), 0.5);
				}
			}

			for (int m = 0; m < height; ++m) {
				for (int n = 0; n < width; ++n) {
					position = m * width + n;
					if (temp[position] >= 0) {
						this.realPixels[position] = Math.cos(((2 * Math.PI * distance) / wavelength) * temp[position]);
						this.imagPixels[position] = Math.sin(((2 * Math.PI * distance) / wavelength) * temp[position]);
					}
				}
			}
			System.out.println("	b. set real origin : false");
			this.isRealOrigin = false;
		}
	}

	public HoloJProcessor(double[] realPixels, double[] imagPixels, int width, int height, double dx, double dy) {
		if (realPixels.length != imagPixels.length) {
			throw new ArrayStoreException("Constructor: real and imaginary part differ in size.");
		} else {
			this.realPixels = realPixels;
			this.imagPixels = imagPixels;
			this.width = width;
			this.height = height;
			this.size = width * height;
			this.dx = dx;
			this.dy = dy;
			System.out.println("	a. Real orgin set : false");
			this.isRealOrigin = false;
		}
	}

	public HoloJProcessor(ImageProcessor ijProc) {
		if (ijProc == null) {
			throw new ArrayStoreException("Constructor: ImageProcessor == null.");
		} else {
			this.width = ijProc.getWidth();
			this.height = ijProc.getHeight();
			this.size = this.width * this.height;
			this.realPixels = new double[this.size];
			this.imagPixels = new double[this.size];
			int var3;
			if (ijProc.getPixels() instanceof byte[]) {
				byte[] byteArray = (byte[]) ((byte[]) ijProc.getPixels());

				for (var3 = 0; var3 < this.size; ++var3) {
					// this.realPixels[var3] = (double)(byteArray[var3] & 255);
					this.realPixels[var3] = Math.pow(0.5 * ((double) (byteArray[var3] & 255)), 0.5);
					this.imagPixels[var3] = this.realPixels[var3];
				}

				this.isRealOrigin = true;
			} else if (ijProc.getPixels() instanceof short[]) {
				short[] var4 = (short[]) ((short[]) ijProc.getPixels());

				for (var3 = 0; var3 < this.size; ++var3) {
					// this.realPixels[var3] = (double)(var4[var3] & '\uffff');
					this.realPixels[var3] = Math.pow(0.5 * ((double) (var4[var3] & '\uffff')), 0.5);
					this.imagPixels[var3] = this.realPixels[var3];
				}

				this.isRealOrigin = true;
			} else {
				if (!(ijProc.getPixels() instanceof float[])) {
					throw new ArrayStoreException("Constructor: Unexpected image type.");
				}

				float[] var5 = (float[]) ((float[]) ijProc.getPixels());

				for (var3 = 0; var3 < this.size; ++var3) {
					// this.realPixels[var3] = (double)var5[var3];
					this.realPixels[var3] = Math.pow(0.5 * ((double) var5[var3]), 0.5);
					this.imagPixels[var3] = this.realPixels[var3];
				}

				this.isRealOrigin = true;
			}

		}
	}

	public void createHoloJProcessorWithImage(ImageProcessor ijProcessor, double dx, double dy) {
		if (ijProcessor == null) {
			throw new ArrayStoreException("Constructor: ImageProcessor == null.");
		} else {
			System.out.println("	a. Seting up object properties");
			this.width = ijProcessor.getWidth();
			this.height = ijProcessor.getHeight();
			this.size = this.width * this.height;
			this.dx = dx;
			this.dy = dy;
			this.realPixels = new double[this.size];
			this.imagPixels = new double[this.size];

			convertSingedBitsToUnsign(ijProcessor);

			this.isRealOrigin = true;
			System.out.println("	c. Setting real origin to :" + this.isRealOrigin);
		}
	}

	public void convertSingedBitsToUnsign(ImageProcessor ijProcessor) {

		System.out.println("	b. converting Signed to unsignedbits inside convertSingedBitsToUnsign()");
		int var3;
		if (ijProcessor.getPixels() instanceof byte[]) {
			// var2 intensity i.e intensity of the image
			byte[] var2 = (byte[]) ((byte[]) ijProcessor.getPixels());

			for (var3 = 0; var3 < this.size; ++var3) {
				// this.realPixels[var3] = (double)(var2[var3] & 255);
				this.realPixels[var3] = Math.pow(0.5 * ((double) (var2[var3] & 255)), 0.5);// intensity = z sqr
				this.imagPixels[var3] = this.realPixels[var3];// 255 for conversion signed to unsigned bits
			}

			// this.isRealOrigin = true;
		} else if (ijProcessor.getPixels() instanceof short[]) {
			short[] var4 = (short[]) ((short[]) ijProcessor.getPixels());

			for (var3 = 0; var3 < this.size; ++var3) {
				// this.realPixels[var3] = (double)(var4[var3] & '\uffff');
				this.realPixels[var3] = Math.pow(0.5 * ((double) (var4[var3] & '\uffff')), 0.5);
				this.imagPixels[var3] = this.realPixels[var3];
			}

			// this.isRealOrigin = true;
		} else {
			if (!(ijProcessor.getPixels() instanceof float[])) {
				throw new ArrayStoreException("Constructor: Unexpected image type.");
			}

			float[] var5 = (float[]) ((float[]) ijProcessor.getPixels());

			for (var3 = 0; var3 < this.size; ++var3) {
				// this.realPixels[var3] = (double)var5[var3];
				this.realPixels[var3] = Math.pow(0.5 * ((double) var5[var3]), 0.5);
				this.imagPixels[var3] = this.realPixels[var3];
			}

		}
	}

	public void doFFT(int state)// 1 for normal FFT : -1 for inverse FFT
	{
		if (this.isRealOrigin) {
			System.out.println("	a. Real origin true starting Real To Complex FFT");
			this.doRealToComplexFFT();
		} else {
			System.out.println("	a. Real origin false starting Real To Complex FFT with state : " + state);
			this.doComplexToComplexFFT(state);
		}

		if (state == 1)
			this.isSpectrumDomain = true;
		else
			this.isSpectrumDomain = false;
	}

	private void doRealToComplexFFT() {
		System.out.println("	b. Fetching float processor for Image");
		FloatProcessor floatProcessor = new FloatProcessor(this.width, this.height, this.realPixels);// floatFormatImange
																										// //realImage
		FHT hartleyTransform = new FHT(floatProcessor);// hartleyTransform
		hartleyTransform.transform();
		double scale = 1.0D / (double) this.width;// scale
		hartleyTransform.swapQuadrants();
		float[] imageArray = (float[]) ((float[]) hartleyTransform.getPixels());// ImageArray//we have fourier transform
																				// till here
		System.out.println("	c. Navigating each pixel and computing real and imaginary part");
		for (int i = 0; i < this.height; ++i) {
			int rowMajorFormat = i * this.width;// row major format

			for (int j = 0; j < this.width; ++j) {
				int position = rowMajorFormat + j;// position
				int var10 = (this.height - i) % this.height * this.width + (this.width - j) % this.width;// ?????
				this.realPixels[i * this.width + j] = scale * 0.5D
						* (double) (imageArray[position] + imageArray[var10]);
				this.imagPixels[i * this.width + j] = scale * 0.5D
						* (double) (imageArray[position] - imageArray[var10]);
			}
		}
	}

	private void doComplexToComplexFFT(int i) {
		System.out.println("	a. Arrange to complex array");
		double[] var2 = arrangeToComplexArray(this.realPixels, this.imagPixels);
		int[] var3 = new int[] { this.width, this.height };
		c2cfft(var2, var3, i);
		System.out.println("	b. Extract and set Real pixels");
		this.realPixels = extractRealPixels(var2);
		System.out.println("	c. Extract and set Complex pixels");
		this.imagPixels = extractComplexPixels(var2);

	}

	public static void c2cfft(double[] data, int[] nn, int isign) {
		int idim, i1, i2, i3, i2rev, i3rev, ip1, ip2, ip3, ifp1, ifp2;
		int ibit, k1, k2, n, nprev, nrem, ntot;
		double tempi, tempr, theta, wi, wpi, wpr, wr, wtemp;
		double tmp;
		int ndim = nn.length;
		ntot = data.length >> 1;
		nprev = 1;
		if (isign == -1)
			swap(data, nn[0]);
		for (idim = ndim - 1; idim >= 0; idim--) {
			n = nn[idim];
			nrem = ntot / (n * nprev);
			ip1 = nprev << 1;
			ip2 = ip1 * n;
			ip3 = ip2 * nrem;
			i2rev = 0;
			for (i2 = 0; i2 < ip2; i2 += ip1) {
				if (i2 < i2rev) {
					for (i1 = i2; i1 < i2 + ip1 - 1; i1 += 2) {
						for (i3 = i1; i3 < ip3; i3 += ip2) {
							i3rev = i2rev + i3 - i2;
							tmp = data[i3];
							data[i3] = data[i3rev];
							data[i3rev] = tmp;
							tmp = data[i3 + 1];
							data[i3 + 1] = data[i3rev + 1];
							data[i3rev + 1] = tmp;
						}
					}
				}
				ibit = ip2 >> 1;
				while (ibit >= ip1 && i2rev + 1 > ibit) {
					i2rev -= ibit;
					ibit >>= 1;
				}
				i2rev += ibit;
			}
			ifp1 = ip1;
			while (ifp1 < ip2) {
				ifp2 = ifp1 << 1;
				theta = isign * 6.28318530717959 / ifp2 * ip1;
				wtemp = Math.sin(0.5 * theta);
				wpr = -2 * wtemp * wtemp;
				wpi = Math.sin(theta);
				wr = 1.0;
				wi = 0.0;
				for (i3 = 0; i3 < ifp1; i3 += ip1) {
					for (i1 = i3; i1 < i3 + ip1 - 1; i1 += 2) {
						for (i2 = i1; i2 < ip3; i2 += ifp2) {
							k1 = i2;
							k2 = k1 + ifp1;
							tempr = wr * data[k2] - wi * data[k2 + 1];
							tempi = wr * data[k2 + 1] + wi * data[k2];
							data[k2] = data[k1] - tempr;
							data[k2 + 1] = data[k1 + 1] - tempi;
							data[k1] += tempr;
							data[k1 + 1] += tempi;
						}
					}
					wr = (wtemp = wr) * wpr - wi * wpi + wr;
					wi = wi * wpr + wtemp * wpi + wi;
				}
				ifp1 = ifp2;
			}
			nprev *= n;
		}
		// Rescale data back down.
		double factor = 1.0 / nn[0];
		for (int off = 0; off < ntot << 1; off++) {
			data[off] *= factor;
		}
		if (isign == 1)
			swap(data, nn[0]);
		return;
	}

	public static void swap(double[] data, int nx) {
		int i1, i2, j1, j2;
		double tempr;
		for (int ii1 = 0; ii1 < nx * nx; ii1 += 2 * nx) {
			for (int ii2 = 0; ii2 < nx; ii2 += 2) {
				i1 = ii1 + ii2;
				i2 = i1 + nx;
				j1 = i1 + nx * nx + nx;
				j2 = j1 - nx;

				tempr = data[i1];
				data[i1] = data[j1];
				data[j1] = tempr;

				tempr = data[i2];
				data[i2] = data[j2];
				data[j2] = tempr;

				tempr = data[i1 + 1];
				data[i1 + 1] = data[j1 + 1];
				data[j1 + 1] = tempr;

				tempr = data[i2 + 1];
				data[i2 + 1] = data[j2 + 1];
				data[j2 + 1] = tempr;
			}
		}
		return;
	}

	public static double[] arrangeToComplexArray(double[] real, double[] complex) {
		double[] output = new double[real.length << 1];
		for (int i = 0; i < real.length; i++) {
			output[2 * i] = real[i];
			output[2 * i + 1] = complex[i];
		}
		return output;
	}

	public static double[] extractRealPixels(double[] input) {
		int nmax = input.length >> 1;
		double[] output = new double[nmax];
		for (int k = 0; k < nmax; k++)
			output[k] = input[2 * k];
		return output;
	}

	public static double[] extractComplexPixels(double[] input) {
		int nmax = input.length >> 1;
		double[] output = new double[nmax];
		for (int k = 0; k < nmax; k++)
			output[k] = input[2 * k + 1];
		return output;
	}

	public FloatProcessor createAmplitudeProcessor() {
		FloatProcessor var1 = new FloatProcessor(this.width, this.height);
		float[] var2 = new float[this.size];

		for (int var3 = 0; var3 < this.size; ++var3) {
			var2[var3] = (float) modulus(this.realPixels[var3], this.imagPixels[var3]);
		}

		var1.setPixels(var2);
		return var1;
	}

	public static double modulus(double real, double imaginary) {
		double mod = Math.sqrt(real * real + imaginary * imaginary);
		return mod;
	}

	public ImagePlus makeAmplitudeImage(String var1) {
		FloatProcessor var2 = this.createAmplitudeProcessor();
		var2.resetMinAndMax();
		ImagePlus var3 = new ImagePlus(var1, var2);
		var3.setCalibration(this.cal);
		return var3;
	}

	public double[] getIntensity() {
		double[] intensity = new double[this.getSize()];
		for (int i = 0; i < this.getSize(); i++) {
			intensity[i] = Math.pow(this.realPixels[i], 2) + Math.pow(this.imagPixels[i], 2);
		}
		return intensity;
	}

	public void showAmplitude(String var1) {
		FloatProcessor var2 = this.createAmplitudeProcessor();
		var2.resetMinAndMax();
		ImagePlus var3 = new ImagePlus(var1, var2);
		var3.setCalibration(this.cal);
		var3.show();
	}

	public FloatProcessor createHoloProcessor() {
		FloatProcessor var1 = new FloatProcessor(this.width, this.height);
		float[] var2 = new float[this.size];

		for (int var3 = 0; var3 < this.size; ++var3) {
			var2[var3] = (float) this.realPixels[var3];
		}

		var1.setPixels(var2);
		return var1;
	}

	public void showHolo(String var1) {
		FloatProcessor var2 = this.createHoloProcessor();
		var2.resetMinAndMax();
		ImagePlus var3 = new ImagePlus(var1, var2);
		var3.setTitle("Test");
		var3.setCalibration(this.cal);
		//var3.
		var3.show();
	}

}
