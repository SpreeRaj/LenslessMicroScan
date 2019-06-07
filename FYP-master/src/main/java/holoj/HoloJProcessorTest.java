package holoj;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class HoloJProcessorTest extends TestCase {
    HoloJProcessor holotest = new HoloJProcessor(1024,1024);
    int size = holotest.getSize();
    double[] arr = new double[size];
    double[] real = new double[size];
    double[] imag = new double[size];

    @Test
    void getIntensity() {
        Arrays.fill(real,5);
        Arrays.fill(imag,10);
        holotest.setRealPixelsArray(real);
        holotest.setImagPixelsArray(imag);
        arr=holotest.getIntensity();

        for (int i=0;i<arr.length;i++){
            assertEquals(125.0,arr[i]);
        }
    }

    @Test
    void multiply() {
        Arrays.fill(real,5);
        Arrays.fill(imag,10);
        Arrays.fill(arr,2);
        holotest.setRealPixelsArray(real);
        holotest.setImagPixelsArray(imag);
        holotest.multiply(arr);

        for (int i=0;i<arr.length;i++){
            assertEquals(10.0,holotest.realPixels[i]);
            assertEquals(20.0,holotest.imagPixels[i]);
        }
    }

    @Test
    void getSize(){
        int Size = holotest.getSize();
        assertEquals(1048576,size);
    }

    @Test
    void getHeight(){
        int height = holotest.getHeight();
        assertEquals(1024,height);
    }

    @Test
    void getWidth(){
        int width = holotest.getWidth();
        assertEquals(1024,width);
    }
}