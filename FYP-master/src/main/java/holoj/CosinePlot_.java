package holoj;

import holoj.HoloJUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
/*
 * CosinePlot_.java
 *
 * Created on June 11, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author luca
 */
public class CosinePlot_ implements PlugInFilter{
    
    public int setup(String arg, ImagePlus imp){
        return DOES_ALL;
    }
    
    public void run(ImageProcessor ip) {
        double factor = (double) IJ.getNumber("Multiplier: ", 2);
        FloatProcessor fp = HoloJUtils.makeCosProcessor(HoloJUtils.multiply(factor,ip));
        fp.resetMinAndMax();
        ImagePlus img = new ImagePlus("Cosine "+factor+"X",fp);
        img.show();
    }
    
}
