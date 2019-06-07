//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package holoj;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.process.FHT;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.Point;

public class HoloJProcessor {
    private static final int EXCLUDED_RADIUS = 20;
    double[] realPixels = null;
    double[] imagPixels = null;
    private int width = 0;
    private int height = 0;
    private int size = 0;
    private double dx =0;
    private double dy = 0;
    private Calibration cal;
    private String title = null;
    private boolean isRealOrigin = true;
    private boolean isSpectrumDomain = false;

    public HoloJProcessor(HoloJProcessor input){
        this.realPixels = input.realPixels;
        this.imagPixels = input.imagPixels;
        this.width = input.width;
        this.height = input.height;
        this.size = input.size;
        this.dx = input.dx;
        this.dy = input.dy;
        this.cal = input.cal;
        this.title = input.title;
        this.isRealOrigin = input.isRealOrigin;
        this.isSpectrumDomain = input.isSpectrumDomain;
    }

    public HoloJProcessor(int var1, int var2) {
        if (var1 < 1) {
            throw new ArrayStoreException("Constructor: width < 1.");
        } else if (var2 < 1) {
            throw new ArrayStoreException("Constructor: height < 1.");
        } else {
            this.width = var1;
            this.height = var2;
            this.size = var1 * var2;
            this.realPixels = new double[this.size];
            this.imagPixels = new double[this.size];
            this.setComplexOrigin();
        }
    }

    public HoloJProcessor(int width, int height, double dx, double dy, double distance, double wavelength) {
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
            double[] temp =  new double[width*height];
            int x,y,position;
            for (int m=0;m<height;m++){
                for (int n=0;n<width;n++){
                    position = m * width + n;
                    y = m - height / 2;
                    x = n - width / 2;
                    temp[position] = Math.pow((1-Math.pow(((wavelength*x)/(width*dx)),2) - Math.pow(((wavelength*y)/(height*dy)),2)),0.5);
                }
            }

            for(int m = 0; m < height; ++m) {
                for(int n = 0; n < width; ++n) {
                    position = m * width + n;
                    if(temp[position]>=0) {
                        this.realPixels[position] = Math.cos(((2 * Math.PI * distance)/wavelength)*temp[position]);
                        this.imagPixels[position] = Math.sin(((2 * Math.PI * distance)/wavelength)*temp[position]);
                    }
                }
            }
            this.setComplexOrigin();
        }
    }

    public HoloJProcessor(ImageProcessor ijProc, double dx, double dy) {
        if (ijProc == null) {
            throw new ArrayStoreException("Constructor: ImageProcessor == null.");
        } else {
            this.width = ijProc.getWidth();
            this.height = ijProc.getHeight();
            this.size = this.width * this.height;
            this.dx = dx;
            this.dy = dy;
            this.realPixels = new double[this.size];
            this.imagPixels = new double[this.size];
            int var3;
            if (ijProc.getPixels() instanceof byte[]) {
                byte[] var2 = (byte[])((byte[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)(var2[var3] & 255);
                    this.realPixels[var3] = Math.pow(0.5*((double)(var2[var3] & 255)),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            } else if (ijProc.getPixels() instanceof short[]) {
                short[] var4 = (short[])((short[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)(var4[var3] & '\uffff');
                    this.realPixels[var3] = Math.pow(0.5*((double)(var4[var3] & '\uffff')),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            } else {
                if (!(ijProc.getPixels() instanceof float[])) {
                    throw new ArrayStoreException("Constructor: Unexpected image type.");
                }

                float[] var5 = (float[])((float[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)var5[var3];
                    this.realPixels[var3] = Math.pow(0.5*((double)var5[var3]),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            }

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
                byte[] byteArray = (byte[])((byte[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)(byteArray[var3] & 255);
                    this.realPixels[var3] = Math.pow(0.5*((double)(byteArray[var3] & 255)),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            } else if (ijProc.getPixels() instanceof short[]) {
                short[] var4 = (short[])((short[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)(var4[var3] & '\uffff');
                    this.realPixels[var3] = Math.pow(0.5*((double)(var4[var3] & '\uffff')),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            } else {
                if (!(ijProc.getPixels() instanceof float[])) {
                    throw new ArrayStoreException("Constructor: Unexpected image type.");
                }

                float[] var5 = (float[])((float[])ijProc.getPixels());

                for(var3 = 0; var3 < this.size; ++var3) {
                    //this.realPixels[var3] = (double)var5[var3];
                    this.realPixels[var3] = Math.pow(0.5*((double)var5[var3]),0.5);
                    this.imagPixels[var3] = this.realPixels[var3];
                }

                this.setRealOrigin();
            }

        }
    }


    public HoloJProcessor(ImageProcessor var1, ImageProcessor var2) {
        if (var1 != null && var2 != null) {
            this.width = var1.getWidth();
            this.height = var1.getHeight();
            this.size = this.width * this.height;
            int var3 = var2.getWidth();
            int var4 = var2.getHeight();
            int var5 = this.width * this.height;
            if (var3 == this.width && var4 == this.height && var5 == this.size) {
                this.realPixels = new double[this.size];
                this.imagPixels = new double[this.size];
                this.setComplexOrigin();
                byte[] var6;
                int var7;
                short[] var8;
                float[] var9;
                if (var1.getPixels() instanceof byte[]) {
                    var6 = (byte[])((byte[])var1.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.realPixels[var7] = (double)(var6[var7] & 255);
                    }
                } else if (var1.getPixels() instanceof short[]) {
                    var8 = (short[])((short[])var1.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.realPixels[var7] = (double)(var8[var7] & '\uffff');
                    }
                } else {
                    if (!(var1.getPixels() instanceof float[])) {
                        throw new ArrayStoreException("Constructor: Unexpected image type in real part.");
                    }

                    var9 = (float[])((float[])var1.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.realPixels[var7] = (double)var9[var7];
                    }
                }

                if (var2.getPixels() instanceof byte[]) {
                    var6 = (byte[])((byte[])var2.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.imagPixels[var7] = (double)(var6[var7] & 255);
                    }
                } else if (var2.getPixels() instanceof short[]) {
                    var8 = (short[])((short[])var2.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.imagPixels[var7] = (double)(var8[var7] & '\uffff');
                    }
                } else {
                    if (!(var2.getPixels() instanceof float[])) {
                        throw new ArrayStoreException("Constructor: Unexpected image type in complex part.");
                    }

                    var9 = (float[])((float[])var2.getPixels());

                    for(var7 = 0; var7 < this.size; ++var7) {
                        this.imagPixels[var7] = (double)var9[var7];
                    }
                }

            } else {
                throw new ArrayStoreException("Constructor: Real and Complex part differ in size.");
            }
        } else {
            throw new ArrayStoreException("Constructor: ImageProcessor == null.");
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
            this.setComplexOrigin();
        }
    }

    public HoloJProcessor(double[] realPixels, double[] imagPixels, int width, int height) {
        if (realPixels.length != imagPixels.length) {
            throw new ArrayStoreException("Constructor: real and imaginary part differ in size.");
        } else {
            this.realPixels = realPixels;
            this.imagPixels = imagPixels;
            this.width = width;
            this.height = height;
            this.size = width * height;
            this.setComplexOrigin();
        }
    }



    public HoloJProcessor(double[] realPixels, int width, int height) {
        this.realPixels = realPixels;
        this.width = width;
        this.height = height;
        this.size = width * height;
        this.setRealOrigin();
    }

    public double[] getRealPixelsArray() {
        return this.realPixels;
    }

    public double[] getImagPixelsArray() {
        return this.imagPixels;
    }

    public void setRealPixelsArray(double[] var1) {
        this.realPixels = var1;
    }

    public void setImagPixelsArray(double[] var1) {
        this.imagPixels = var1;
    }

    public int getSize() {
        return this.size;
    }

    public double getDx() {return this.dx; }

    public double getDy() {return this.dy; }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isSpectrumDomain() {
        return this.isSpectrumDomain;
    }

    public void add(HoloJProcessor var1) {
        if (this.size != var1.getSize()) {
            throw new IndexOutOfBoundsException("add: sizes must be equal.");
        } else {
            double[] var2 = var1.getRealPixelsArray();
            double[] var3 = var1.getImagPixelsArray();

            for(int var4 = 0; var4 < this.size; ++var4) {
                this.realPixels[var4] += var2[var4];
                this.imagPixels[var4] += var3[var4];
            }

            this.setComplexOrigin();
        }
    }

    public void subtract(HoloJProcessor var1) {
        if (this.size != var1.getSize()) {
            throw new IndexOutOfBoundsException("subtract: sizes must be equal.");
        } else {
            double[] var2 = var1.getRealPixelsArray();
            double[] var3 = var1.getImagPixelsArray();

            for(int var4 = 0; var4 < this.size; ++var4) {
                this.realPixels[var4] -= var2[var4];
                this.imagPixels[var4] -= var3[var4];
            }

            this.setComplexOrigin();
        }
    }

    public void multiply(HoloJProcessor var1) {
        if (this.size != var1.getSize()) {
            throw new IndexOutOfBoundsException("multiply: sizes must be equal.");
        } else {
            double[] var2 = var1.getRealPixelsArray();
            double[] var3 = var1.getImagPixelsArray();

            for(int var4 = 0; var4 < this.size; ++var4) {
                double var5 = this.realPixels[var4] * var2[var4] - this.imagPixels[var4] * var3[var4];
                double var7 = this.realPixels[var4] * var3[var4] + var2[var4] * this.imagPixels[var4];
                this.realPixels[var4] = var5;
                this.imagPixels[var4] = var7;
            }

            this.setComplexOrigin();
        }
    }

    public void multiply(ImageProcessor var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        int var4 = var2 * var3;
        if (this.size != var4) {
            throw new IndexOutOfBoundsException("multiply: sizes must be equal.");
        } else {
            float[] var5 = (float[])((float[])var1.convertToFloat().getPixels());

            for(int var6 = 0; var6 < this.size; ++var6) {
                this.realPixels[var6] *= (double)var5[var6];
                this.imagPixels[var6] *= (double)var5[var6];
            }

            this.setComplexOrigin();
        }
    }

    public void add(ImageProcessor var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        int var4 = var2 * var3;
        if (this.size != var4) {
            throw new IndexOutOfBoundsException("add: sizes must be equal.");
        } else {
            float[] var5 = (float[])((float[])var1.convertToFloat().getPixels());

            for(int var6 = 0; var6 < this.size; ++var6) {
                this.realPixels[var6] += (double)var5[var6];
            }

            this.setComplexOrigin();
        }
    }

    public void subtract(ImageProcessor var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        int var4 = var2 * var3;
        if (this.size != var4) {
            throw new IndexOutOfBoundsException("subtract: sizes must be equal.");
        } else {
            float[] var5 = (float[])((float[])var1.convertToFloat().getPixels());

            for(int var6 = 0; var6 < this.size; ++var6) {
                this.realPixels[var6] -= (double)var5[var6];
            }

            this.setComplexOrigin();
        }
    }

    public void divide(ImageProcessor var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        int var4 = var2 * var3;
        if (this.size != var4) {
            throw new IndexOutOfBoundsException("subtract: sizes must be equal.");
        } else {
            float[] var5 = (float[])((float[])var1.convertToFloat().getPixels());

            for(int var6 = 0; var6 < this.size; ++var6) {
                this.realPixels[var6] /= (double)var5[var6];
                this.imagPixels[var6] /= (double)var5[var6];
            }

            this.setComplexOrigin();
        }
    }

    public void multiply(double[] var1) {
        int var2 = var1.length;
        if (this.size != var2) {
            throw new IndexOutOfBoundsException("multiply: sizes must be equal.");
        } else {
            for(int var3 = 0; var3 < this.size; ++var3) {
                this.realPixels[var3] *= var1[var3];
                this.imagPixels[var3] *= var1[var3];
            }

            this.setComplexOrigin();
        }
    }

    public void divide(double[] var1) {
        int var2 = var1.length;
        if (this.size != var2) {
            throw new IndexOutOfBoundsException("divide: sizes must be equal.");
        } else {
            for(int var3 = 0; var3 < this.size; ++var3) {
                this.realPixels[var3] /= var1[var3];
                this.imagPixels[var3] /= var1[var3];
            }

            this.setComplexOrigin();
        }
    }

    public void add(double[] var1) {
        int var2 = var1.length;
        if (this.size != var2) {
            throw new IndexOutOfBoundsException("add: sizes must be equal.");
        } else {
            for(int var3 = 0; var3 < this.size; ++var3) {
                this.realPixels[var3] += var1[var3];
            }

            this.setComplexOrigin();
        }
    }

    public void subtract(double[] var1) {
        int var2 = var1.length;
        if (this.size != var2) {
            throw new IndexOutOfBoundsException("subtract: sizes must be equal.");
        } else {
            for(int var3 = 0; var3 < this.size; ++var3) {
                this.realPixels[var3] -= var1[var3];
            }

            this.setComplexOrigin();
        }
    }

    public void divide(HoloJProcessor var1) {
        if (this.size != var1.getSize()) {
            throw new IndexOutOfBoundsException("divide: sizes must be equal.");
        } else {
            double[] var2 = var1.getRealPixelsArray();
            double[] var3 = var1.getImagPixelsArray();

            for(int var4 = 0; var4 < this.size; ++var4) {
                double var5 = (this.realPixels[var4] * var2[var4] - this.imagPixels[var4] * var3[var4]) / (var2[var4] * var2[var4] + var3[var4] * var3[var4]);
                double var7 = (this.realPixels[var4] * var3[var4] + var2[var4] * this.imagPixels[var4]) / (var2[var4] * var2[var4] + var3[var4] * var3[var4]);
                this.realPixels[var4] = var5;
                this.imagPixels[var4] = var7;
            }

            this.setComplexOrigin();
        }
    }

    public void addPhase(double var1) {
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);

        for(int var7 = 0; var7 < this.size; ++var7) {
            this.realPixels[var7] += var3;
            this.imagPixels[var7] += var5;
        }

    }

    public void addPhasePlate(double[] var1) {
        if (this.size != var1.length) {
            throw new IndexOutOfBoundsException("addPhasePlate: size of the phase-plate must be the same of HoloJProcessor.");
        } else {
            for(int var2 = 0; var2 < this.size; ++var2) {
                this.realPixels[var2] += Math.cos(var1[var2]);
                this.imagPixels[var2] += Math.sin(var1[var2]);
            }

        }
    }

    public void setRealOrigin() {
        this.isRealOrigin = true;
    }

    public void setComplexOrigin() {
        this.isRealOrigin = false;
    }

    public void setCalibration(Calibration var1) {
        this.cal = var1.copy();
    }

    public Calibration getCalibration() {
        return this.cal;
    }

    public void setTitle(String var1) {
        this.title = var1;
    }

    public String getTitle() {
        return this.title;
    }

    public void doFFT() {
        if (this.isRealOrigin) {
            this.doRealToComplexFFT();
        } else {
            this.doComplexToComplexFFT(1);
        }

        this.isSpectrumDomain = true;
    }

    public void doInverseFFT() {
        if (this.isRealOrigin) {
            this.doComplexToRealFFT();
        } else {
            this.doComplexToComplexFFT(-1);
        }

        this.isSpectrumDomain = false;
    }

    private void doRealToComplexFFT() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height, this.realPixels);
        FHT var2 = new FHT(var1);
        var2.transform();
        double var3 = 1.0D / (double)this.width;
        var2.swapQuadrants();
        float[] var5 = (float[])((float[])var2.getPixels());

        for(int var6 = 0; var6 < this.height; ++var6) {
            int var7 = var6 * this.width;

            for(int var8 = 0; var8 < this.width; ++var8) {
                int var9 = var7 + var8;
                int var10 = (this.height - var6) % this.height * this.width + (this.width - var8) % this.width;
                this.realPixels[var6 * this.width + var8] = var3 * 0.5D * (double)(var5[var9] + var5[var10]);
                this.imagPixels[var6 * this.width + var8] = var3 * 0.5D * (double)(var5[var9] - var5[var10]);
            }
        }

    }

    private void doComplexToRealFFT() {
        double[] var1 = new double[this.size];

        for(int var2 = 0; var2 < this.size; ++var2) {
            var1[var2] = this.realPixels[var2] + this.imagPixels[var2];
        }

        FloatProcessor var8 = new FloatProcessor(this.width, this.height, var1);
        FHT var3 = new FHT(var8);
        var3.swapQuadrants();
        var3.transform();
        double var4 = 1.0D / (double)this.width;
        float[] var6 = (float[])((float[])var3.getPixels());

        for(int var7 = 0; var7 < this.size; ++var7) {
            this.realPixels[var7] = var4 * (double)var6[var7];
            this.imagPixels[var7] = 0.0D;
        }

    }

    private void doComplexToComplexFFT(int var1) {
        double[] var2 = HoloJUtils.arrangeToComplexArray(this.realPixels, this.imagPixels);
        int[] var3 = new int[]{this.width, this.height};
        HoloJUtils.c2cfft(var2, var3, var1);
        this.realPixels = HoloJUtils.extractRealPixels(var2);
        this.imagPixels = HoloJUtils.extractComplexPixels(var2);
    }

    public void show(String var1) {
        this.showPowerSpectrum(var1);
    }

    public void showPowerSpectrum(String var1) {
        FloatProcessor var2 = this.createPowerSpectrumProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.show();
    }

    public void showSpectrum(String var1) {
        FloatProcessor var2 = this.createSpectrumProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.show();
    }

    public void showAmplitude(String var1) {
        FloatProcessor var2 = this.createAmplitudeProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.setCalibration(this.cal);
        var3.show();
    }

    public void showHolo(String var1) {
        FloatProcessor var2 = this.createHoloProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.setCalibration(this.cal);
        var3.show();
    }

    public void showPhase(String var1) {
        FloatProcessor var2 = this.createPhaseProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.setCalibration(this.cal);
        var3.show();
    }

    public ImagePlus makePowerSpectrumImage(String var1) {
        FloatProcessor var2 = this.createPowerSpectrumProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        return var3;
    }

    public ImagePlus makeSpectrumImage(String var1) {
        FloatProcessor var2 = this.createSpectrumProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        return var3;
    }

    public ImagePlus makeAmplitudeImage(String var1) {
        FloatProcessor var2 = this.createAmplitudeProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.setCalibration(this.cal);
        return var3;
    }

    public ImagePlus makePhaseImage(String var1) {
        FloatProcessor var2 = this.createPhaseProcessor();
        var2.resetMinAndMax();
        ImagePlus var3 = new ImagePlus(var1, var2);
        var3.setCalibration(this.cal);
        return var3;
    }

    public FloatProcessor createPowerSpectrumProcessor() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height);
        float[] var2 = new float[this.size];

        for(int var3 = 0; var3 < this.size; ++var3) {
            var2[var3] = (float)(0.5D * Math.log(Math.sqrt(this.realPixels[var3] * this.realPixels[var3] + this.imagPixels[var3] * this.imagPixels[var3])));
        }

        var1.setPixels(var2);
        return var1;
    }

    public FloatProcessor createSpectrumProcessor() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height);
        float[] var2 = new float[this.size];

        for(int var3 = 0; var3 < this.size; ++var3) {
            var2[var3] = (float)Math.log(Math.sqrt(this.realPixels[var3] * this.realPixels[var3] + this.imagPixels[var3] * this.imagPixels[var3]));
        }

        var1.setPixels(var2);
        return var1;
    }

    public FloatProcessor createAmplitudeProcessor() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height);
        float[] var2 = new float[this.size];

        for(int var3 = 0; var3 < this.size; ++var3) {
            var2[var3] = (float)HoloJUtils.modulus(this.realPixels[var3], this.imagPixels[var3]);
        }

        var1.setPixels(var2);
        return var1;
    }

    public FloatProcessor createHoloProcessor() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height);
        float[] var2 = new float[this.size];

        for(int var3 = 0; var3 < this.size; ++var3) {
            var2[var3] = (float)this.realPixels[var3];
        }

        var1.setPixels(var2);
        return var1;
    }

    public FloatProcessor createPhaseProcessor() {
        FloatProcessor var1 = new FloatProcessor(this.width, this.height);
        float[] var2 = new float[this.size];

        for(int var3 = 0; var3 < this.size; ++var3) {
            var2[var3] = (float)(Math.atan2(this.realPixels[var3], this.imagPixels[var3]) + 3.141592653589793D);
        }

        var1.setPixels(var2);
        HoloJUtils.resetMin(var1);
        return var1;
    }

    public Point getMaximumPosition() {
        double var1 = -1.0D / 0.0;
        Point var3 = new Point(0, 0);
        double var4 = 0.0D;

        for(int var6 = 0; var6 < this.height; ++var6) {
            for(int var7 = 0; var7 < this.width; ++var7) {
                var4 = HoloJUtils.modulus(this.realPixels[var6 * this.width + var7], this.imagPixels[var6 * this.width + var7]);
                if (var1 < var4) {
                    var1 = var4;
                    var3.setLocation(var7, var6);
                }
            }
        }

        return var3;
    }

    public Point getMaximumPosition(Roi var1) {
        double var2 = -1.0D / 0.0;
        Point var4 = new Point(0, 0);
        double var5 = 0.0D;

        for(int var7 = 0; var7 < this.height; ++var7) {
            for(int var8 = 0; var8 < this.width; ++var8) {
                if (var7 >= var1.getBounds().y && var7 <= var1.getBounds().y + var1.getBounds().height && var8 >= var1.getBounds().x && var8 <= var1.getBounds().x + var1.getBounds().width) {
                    var5 = HoloJUtils.modulus(this.realPixels[var7 * this.width + var8], this.imagPixels[var7 * this.width + var8]);
                    if (var2 < var5) {
                        var2 = var5;
                        var4.setLocation(var8, var7);
                    }
                }
            }
        }

        return var4;
    }

    public Point getSidebandCenter(int var1) {
        if (!this.isSpectrumDomain) {
            throw new ArrayStoreException("getSidebandCenter: image is not in Fourier domain.");
        } else {
            double var2 = -1.0D / 0.0;
            Point var4 = new Point(0, 0);
            int var5 = this.width >> 1;
            int var6 = this.height >> 1;
            double var7 = 0.0D;
            int var9;
            int var10;
            if (var1 >= 0) {
                for(var9 = 0; var9 < this.height >> 1; ++var9) {
                    for(var10 = 0; var10 < this.width; ++var10) {
                        var7 = HoloJUtils.modulus(this.realPixels[var9 * this.width + var10], this.imagPixels[var9 * this.width + var10]);
                        if ((var9 - var5) * (var9 - var5) + (var10 - var6) * (var10 - var6) > 400 && var2 < var7) {
                            var2 = var7;
                            var4.setLocation(var10, var9);
                        }
                    }
                }
            } else {
                for(var9 = this.height >> 1; var9 < this.height; ++var9) {
                    for(var10 = 0; var10 < this.width; ++var10) {
                        var7 = HoloJUtils.modulus(this.realPixels[var9 * this.width + var10], this.imagPixels[var9 * this.width + var10]);
                        if ((var9 - var5) * (var9 - var5) + (var10 - var6) * (var10 - var6) > 400 && var2 < var7) {
                            var2 = var7;
                            var4.setLocation(var10, var9);
                        }
                    }
                }
            }

            return var4;
        }
    }

    public HoloJProcessor getSideband(HoloJProcessor holo, Point sideCenter, int radius, int scaleFactor, boolean useButterworth) {
        if (!this.isSpectrumDomain) {
            throw new ArrayStoreException("getSideband: image is not in Fourier domain.");
        } else {
            int var5 = this.width / scaleFactor;
            int var6 = this.height / scaleFactor;
            int var7 = Math.min(this.width - sideCenter.x, sideCenter.x);
            int var8 = Math.min(this.height - sideCenter.y, sideCenter.y);
            int var9 = Math.min(var7, var8);
            int var10 = var9 << 1;
            if (var10 > var6) {
                var10 = var6;
            } else if (var10 > var5) {
                var10 = var5;
            }

            var9 = var10 >> 1;
            int var11 = sideCenter.x - var9 + (sideCenter.y - var9) * this.width;
            int var12 = var10 * var10;
            double[] var13 = new double[var10 * var10];
            if (useButterworth) {
                var13 = HoloJUtils.butterworthMask(var10, var10, radius, 0.414D);
            } else {
                var13 = HoloJUtils.circularMask(var10, var10, radius);
            }

            double[] var14 = new double[var12];
            double[] var15 = new double[var12];

            for(int var16 = 0; var16 < var10; ++var16) {
                for(int var17 = 0; var17 < var10; ++var17) {
                    int var18 = var17 + var16 * var10;
                    int var19 = var11 + var16 * this.width + var17;
                    var14[var18] = var13[var18] * this.realPixels[var19];
                    var15[var18] = var13[var18] * this.imagPixels[var19];
                }
            }

            HoloJProcessor var20 = new HoloJProcessor(var14, var15, var10, var10, holo.getDx(), holo.getDy());
            return var20;
        }
    }

    public double[] getIntensity(){
        double[] intensity = new double[this.getSize()];
        for(int i=0;i<this.getSize();i++){
            intensity[i]=Math.pow(this.realPixels[i],2) + Math.pow(this.imagPixels[i],2);
        }
        return intensity;
    }
}
