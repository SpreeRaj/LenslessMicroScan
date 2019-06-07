/*
 * HoloJFrame.java
 *
 * Created on 6 juin 2007, 19:35
 */

package holoj;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.util.Java2;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import ij.process.ImageProcessor;


/**
 *
 * @author  Luca Ortolani and Pier Francesco Fazzini
 * @version 1.0
 */
public class HoloJFrame extends javax.swing.JFrame {
    
    private int x=0;
    private int y=0;
    private int radius=50;
    private int ratio=2;
    private boolean butterworth=false;
    private boolean amplitude=false;
    private boolean phase=false;
    private String standardItem =new String("No Image selected");
    private HoloJProcessor hp;
    private Calibration imageCal;
    private String title = null;
	private double distance, wavelength, dx, dy;
	//private double dx = 0.00000345;
	//private double dy = 0.00000345;
	//private double distance = 0.00899;
	//private double wavelength = 0.000000633;
	private HoloJProcessor holo,ref,rec;
	private double Tolerance;
	private int Iterations,Sigma;



	/*

	get sqrt of holo => Intensity || new HoloJprocessor that returns real = sqrt(raw)
	input = abs ( Intensity)
	make raw_abs = input
	 */
    private void operate(){//DONT EDIT
		holo=getHologramProcessor();
		ref=getReferenceProcessor();
        dx=getDouble(dxTextField);
        dy=getDouble(dyTextField);
        Tolerance=getDouble(toleranceTextField);
        wavelength=getDouble(wavelengthTextField);
        distance=getDouble(distanceTextField);
        Sigma=getInteger(sigmaTextField);
        Iterations=getInteger(iterationsTextField);
         
        if (holo == null) 
            throw new ArrayStoreException("reconstruct: No hologram selected.");
        else 
		{

			//rec = HoloJUtils.reconstruct(holo.getWidth(),1,sideCenter,holo,butterworth);
            rec = HoloJUtils.reconstruct(holo,ref,distance,wavelength,Iterations,Tolerance,Sigma);
//            if (ref != null)
//			{
//			    Point p = new Point();
//			    p.x = holo.getWidth()/2;
//                p.y = holo.getHeight()/2;
//				rec = HoloJUtils.reconstruct(radius,ratio,p,ref,holo,butterworth);
//            }
//			rec.setCalibration(imageCal);
            rec.setTitle(""+title);
			rec.showHolo("Hologram : "+rec.getTitle()+" :");
			rec.showAmplitude("Amplitude");
		}
    } 
	private void operate2(){
            HoloJProcessor recon;
            holo=getHologramProcessor();
            ref=getReferenceProcessor();
			//imageCal.pixelWidth *= ratio;
            //imageCal.pixelHeight *= ratio;
			wavelength=getDouble(wavelengthTextField);
			distance=getDouble(distanceTextField);
            if((ref==null) && (holo == null))
            {
                throw new ArrayStoreException("reconstruct: No hologram or reference selected.");
            }
			else {
                recon = HoloJUtils.propogatefunc(holo, distance, wavelength);
            }

            //rec.setCalibration(imageCal);
            recon.setTitle(""+title);
			if (phase) recon.showPhase("Hologram : "+recon.getTitle()+" : Phase");
            if (amplitude) recon.showAmplitude("Hologram : "+recon.getTitle()+" : Amplitude");
        }

    private void previewImage() {
        holo=getHologramProcessor();
        Tolerance=getDouble(toleranceTextField);
        wavelength=getDouble(wavelengthTextField);
        distance=getDouble(distanceTextField);
        Sigma=getInteger(sigmaTextField);
        Iterations=getInteger(iterationsTextField);
        HoloJUtils.previewPoints(holo,distance,wavelength,Iterations,Tolerance,Sigma);

    }


    /** Creates new form HoloJFrame */
    public HoloJFrame() {
        Java2.setSystemLookAndFeel();
        initComponents();
        initFileList(hologramComboBox);
        initFileList(referenceComboBox);
		
    }
   
    private void initFileList(JComboBox cb){
        int[] ids=WindowManager.getIDList();
        int n= WindowManager.getImageCount();
        
        cb.removeAllItems();
        if (n>0){
            cb.addItem(standardItem);
            for (int i=0;i<WindowManager.getImageCount();i++){
             cb.addItem(WindowManager.getImage(ids[i]).getTitle());
            }
        }
        else cb.addItem(standardItem);
        
        //pack();
    }
    private boolean hasIt(JComboBox cb, String s){
        for (int i=0;i<WindowManager.getImageCount();i++){
            if(cb.getItemAt(i).equals(s)) return true;
        }
        return false;
    }
    
    private void addFileToList(JComboBox cb){
        OpenDialog od=new OpenDialog("Choose image","");
        String tmp=od.getFileName();
        if (!hasIt(cb, tmp)) cb.addItem(tmp);
        cb.setSelectedItem(tmp);
        cb.removeItem(standardItem);
        pathTextField.setText(od.getDirectory());
    }
    
    private void setDir(JTextField tf){
        OpenDialog od=new OpenDialog("Choose directory ","");
        tf.setText(od.getDirectory());
    }
    
    private ImagePlus getOpenedImage(String name){
        int[] ids=WindowManager.getIDList();
        int n= WindowManager.getImageCount();
        ImagePlus imp;
      
        if (n>0){
            for (int i=0;i<n;i++){
                imp=WindowManager.getImage(ids[i]);
                if(imp.getTitle()==name) return imp;
            }   
        }
        return null;
    }
    
    private ImagePlus getImage(JComboBox cb){
        String dir=pathTextField.getText();
        String name=cb.getSelectedItem().toString();
        ImagePlus imp;
        
        imp=getOpenedImage(name);
        if(imp==null){
            Opener op = new Opener();
            imp=op.openImage(dir,name);
        }
        return imp;
    }
    
    private HoloJProcessor getHologramProcessor(){
        HoloJProcessor proc=null;
        ImagePlus imp=getImage(hologramComboBox);
        if(imp!=null) {
            imageCal = imp.getCalibration().copy();
            title = imp.getTitle();
            proc=new HoloJProcessor(imp.getProcessor(),getDouble(dxTextField),getDouble(dyTextField));
        }
        return proc;
    }
    
    private HoloJProcessor getReferenceProcessor(){
        HoloJProcessor proc=null;
        ImagePlus imp=getImage(referenceComboBox);
        if(imp!=null) proc=new HoloJProcessor(imp.getProcessor(),getDouble(dxTextField),getDouble(dyTextField));
        return proc;
    }

	
	private void button6function(){
		IJ.log(" button custom reconstruct pressed");
		//ip.show();
	}
    
    private int getInteger(JTextField tf){
        int ret=(new Integer(tf.getText())).intValue();
        //IJ.write(""+ret);
        return ret;      
    }
	private double getDouble(JTextField tf){
        double ret=(new Double(tf.getText())).doubleValue();
        return ret;
    }
    private boolean getBoolean(JCheckBox cb){
        boolean ret=cb.isSelected();
        return ret;
    }
	private void button3function(){
		String[] args={};
        Interactive_3D_Surface_Plot.main(args);
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
	private void button2function(){
		//IJ.log(" button unwrap1 pressed");
		UnwrapJ_ um = new UnwrapJ_();
		ImagePlus imp=WindowManager.getCurrentImage();
		if(imp == null){IJ.log("error in imp,  ..... its null");}
		ImageProcessor ip = imp.getProcessor();
		if(ip == null){IJ.log("error in ip its null");}
		um.run(ip);
		
	}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
		

        filesPanel = new JPanel();
        pathLabel = new JLabel();
        pathTextField = new JTextField();
        pathSelectButton = new JButton();
        hologramLabel = new JLabel();
        hologramSelectButton = new JButton();
        hologramComboBox = new JComboBox();
        refrenceLabel = new JLabel();
        referenceComboBox = new JComboBox();
        refrenceSelectButton = new JButton();
        resetFileListButton = new JButton();
        phaseRetrievalPanel = new JPanel();
        toleranceLabel = new JLabel();
        toleranceTextField = new JTextField();
        radiusLabel = new JLabel();
        sigmaTextField = new JTextField();
        iterationsLabel = new JLabel();
        iterationsTextField = new JTextField();
        previewPointsButton = new javax.swing.JButton();
        //jLabel8 = new JLabel();
        //ratioTF = new JTextField();
        reconstructPanel = new JPanel();
        reconstructButton = new JButton();
        amplitudeCheckBox = new JCheckBox();
        phaseComboBox = new JCheckBox();
        extractLabel = new JLabel();
        butterworthFilterCheckBox = new JCheckBox();
        graph3DButton = new JButton();
        numericalPropagationButton = new JButton();
        unwrapButton = new JButton();
		dxTextField = new JTextField();
        dyTextField = new JTextField();
        wavelengthTextField = new JTextField();
        distanceTextField = new JTextField();
        dxLabel = new JLabel();
        dyLabel = new JLabel();
        wavelengthLabel = new JLabel();
        distanceLabel = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("HoloJ");

        filesPanel.setBorder(BorderFactory.createTitledBorder("Files"));

        pathLabel.setText("Path");

        pathTextField.setText("No Directory Selected");
        pathTextField.setMaximumSize(new Dimension(500, 20));
        pathTextField.setPreferredSize(new Dimension(50, 20));

        pathSelectButton.setText("...");
        pathSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pathBActionPerformed(evt);
            }
        });

        hologramLabel.setText("Hologram");

        hologramSelectButton.setText("...");
        hologramSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                holoBActionPerformed(evt);
            }
        });

        hologramComboBox.setEditable(true);
        hologramComboBox.setMaximumRowCount(5);
        hologramComboBox.setModel(new DefaultComboBoxModel(new String[] { "No Image Selected" }));
        hologramComboBox.setMaximumSize(new Dimension(138, 22));

        refrenceLabel.setText("Reference");

        referenceComboBox.setEditable(true);
        referenceComboBox.setMaximumRowCount(5);
        referenceComboBox.setModel(new DefaultComboBoxModel(new String[] { "No Image Selected" }));
        referenceComboBox.setMaximumSize(new Dimension(138, 22));

        refrenceSelectButton.setText("...");
        refrenceSelectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rholoBActionPerformed(evt);
            }
        });

        resetFileListButton.setText("Reset File List");
        resetFileListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(filesPanel);
        filesPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pathLabel)
                .addGap(45, 45, 45)
                .addComponent(pathTextField, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pathSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(hologramLabel)
                .addGap(6, 6, 6)
                .addComponent(hologramComboBox, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(hologramSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(refrenceLabel)
                .addGap(7, 7, 7)
                .addComponent(referenceComboBox, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(refrenceSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(resetFileListButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pathLabel)
                    .addComponent(pathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pathSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(hologramLabel)
                    .addComponent(hologramComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(hologramSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(refrenceLabel)
                    .addComponent(referenceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(refrenceSelectButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(resetFileListButton))
        );

        phaseRetrievalPanel.setBorder(BorderFactory.createTitledBorder("Phase Retrieval"));

        toleranceLabel.setText("Tolerance");

        toleranceTextField.setHorizontalAlignment(JTextField.TRAILING);
        toleranceTextField.setText("3");
        toleranceTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                toleranceTFActionPerformed(evt);
            }
        });
        toleranceTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {

                toleranceTFFocusLost(evt);
            }
        });

        radiusLabel.setText("Radius");

        sigmaTextField.setHorizontalAlignment(JTextField.TRAILING);
        sigmaTextField.setText("2");
        sigmaTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sigmaTFActionPerformed(evt);
            }
        });
        sigmaTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                sigmaTFFocusLost(evt);
            }
        });

        iterationsLabel.setText("Iterations");

        iterationsTextField.setHorizontalAlignment(JTextField.TRAILING);
        iterationsTextField.setText("1");
        iterationsTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                iterationsTFActionPerformed(evt);
            }
        });
        iterationsTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                iterationsTFFocusLost(evt);
            }
        });

        previewPointsButton.setText("Preview Points");
        previewPointsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        //jLabel8.setText("TBC");

//        ratioTF.setHorizontalAlignment(JTextField.TRAILING);
//        ratioTF.setText("4");
//        ratioTF.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                ratioTFActionPerformed(evt);
//            }
//        });
//        ratioTF.addFocusListener(new FocusAdapter() {
//            public void focusLost(FocusEvent evt) {
//                ratioTFFocusLost(evt);
//            }
//        });

        GroupLayout jPanel2Layout = new GroupLayout(phaseRetrievalPanel);
        phaseRetrievalPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(toleranceLabel)
                .addGap(4, 4, 4)
                .addComponent(toleranceTextField, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                //.addComponent(jLabel8)
                .addGap(18, 18, 18)
                //.addComponent(ratioTF, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                    )
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(radiusLabel)
                .addGap(4, 4, 4)
                .addComponent(sigmaTextField, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(iterationsLabel)
                .addGap(7, 7, 7)
                .addComponent(iterationsTextField, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(previewPointsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(toleranceLabel)
                    .addComponent(toleranceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    //.addComponent(jLabel8)
                    //.addComponent(ratioTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(radiusLabel)
                    .addComponent(sigmaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(iterationsLabel)
                    .addComponent(iterationsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(previewPointsButton))
        );

        reconstructPanel.setBorder(BorderFactory.createTitledBorder("Reconstruct"));

        reconstructButton.setText("Reconstruct");
        reconstructButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        amplitudeCheckBox.setText("Amplitude");
        amplitudeCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                amplitudeCBActionPerformed(evt);
            }
        });

        phaseComboBox.setText("Phase");
        phaseComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                phaseCBActionPerformed(evt);
            }
        });

        extractLabel.setText("Extract:");

        butterworthFilterCheckBox.setText("Butterworth Filter");
        butterworthFilterCheckBox.setMaximumSize(new Dimension(135, 25));
        butterworthFilterCheckBox.setMinimumSize(new Dimension(135, 25));
        butterworthFilterCheckBox.setPreferredSize(new Dimension(135, 25));
        butterworthFilterCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butterCBActionPerformed(evt);
            }
        });

        graph3DButton.setText("3D graph");
        graph3DButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        numericalPropagationButton.setText("Numerical Propagation");
        numericalPropagationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        unwrapButton.setText("UnWrap");
        unwrapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////
        dxTextField.setText("0.00000160");
        dxTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                dxTFFocusLost(evt);
            }
        });
        dxTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dxTFActionPerformed(evt);
            }
        });

        dyTextField.setText("0.00000160");
        dyTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                dyTFFocusLost(evt);
            }
        });
        dyTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dyTFActionPerformed(evt);
            }
        });

        wavelengthTextField.setText("0.000000633");
        wavelengthTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                wavelengthTFFocusLost(evt);
            }
        });
        wavelengthTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                wavelengthTFActionPerformed(evt);
            }
        });

        distanceTextField.setText("0.001");
        distanceTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                distanceTFFocusLost(evt);
            }
        });
        distanceTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                distanceTFActionPerformed(evt);
            }
        });

        dxLabel.setText("dx");

        dyLabel.setText("dy");

        wavelengthLabel.setText("wavelength");

        distanceLabel.setText("distance");

        GroupLayout jPanel3Layout = new GroupLayout(reconstructPanel);
        reconstructPanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(amplitudeCheckBox, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(extractLabel))
                            .addComponent(phaseComboBox, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
                            .addComponent(unwrapButton, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                            .addComponent(butterworthFilterCheckBox, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(dyLabel)
                            .addComponent(dxLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(dxTextField)
                            .addComponent(dyTextField))))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(reconstructButton, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(wavelengthLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(wavelengthTextField, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(distanceLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(distanceTextField, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
                        .addComponent(graph3DButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
                    .addComponent(numericalPropagationButton, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(extractLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(phaseComboBox, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(amplitudeCheckBox)
                                .addGap(1, 1, 1)
                                .addComponent(butterworthFilterCheckBox, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(reconstructButton)
                                .addGap(29, 29, 29)
                                .addComponent(numericalPropagationButton)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(dxTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(wavelengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(dxLabel)))
                    .addComponent(wavelengthLabel, GroupLayout.Alignment.TRAILING))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(distanceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(distanceLabel))
                    .addComponent(dyLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(unwrapButton)
                    .addComponent(graph3DButton))
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phaseRetrievalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(reconstructPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(filesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(phaseRetrievalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reconstructPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                              
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void iterationsTFFocusLost(java.awt.event.FocusEvent evt) {
        radius = getInteger(iterationsTextField);
    }                                  

//    private void ratioTFFocusLost(java.awt.event.FocusEvent evt) {
//        ratio = getInteger(ratioTF);
//    }

    private void sigmaTFFocusLost(java.awt.event.FocusEvent evt) {
        //TBC = getInteger(yTF);
    }                             

    private void toleranceTFFocusLost(java.awt.event.FocusEvent evt) {
        Tolerance = getDouble(toleranceTextField);
    }                             

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        operate();
    }                                        

    private void amplitudeCBActionPerformed(java.awt.event.ActionEvent evt) {                                            
        amplitude=getBoolean(amplitudeCheckBox);
    }                                           

    private void phaseCBActionPerformed(java.awt.event.ActionEvent evt) {                                        
        phase=getBoolean(phaseComboBox);
    }                                       

    private void butterCBActionPerformed(java.awt.event.ActionEvent evt) {                                         
        butterworth=getBoolean(butterworthFilterCheckBox);
    }                                        

    private void sigmaTFActionPerformed(java.awt.event.ActionEvent evt) {
        //TBC = getInteger(yTF);
    }                                   

    private void toleranceTFActionPerformed(java.awt.event.ActionEvent evt) {
        Tolerance = getDouble(toleranceTextField);
    }                                   

//    private void ratioTFActionPerformed(java.awt.event.ActionEvent evt) {
//        ratio=getInteger(ratioTF);
//    }

    private void iterationsTFActionPerformed(java.awt.event.ActionEvent evt) {
        radius=getInteger(iterationsTextField);
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        initFileList(hologramComboBox);
        initFileList(referenceComboBox);
    }                                        

    private void rholoBActionPerformed(java.awt.event.ActionEvent evt) {                                       
        addFileToList(referenceComboBox);
    }                                      

    private void holoBActionPerformed(java.awt.event.ActionEvent evt) {                                      
        addFileToList(hologramComboBox);
    }                                     

    private void pathBActionPerformed(java.awt.event.ActionEvent evt) {                                      
       setDir(pathTextField);
    }                                     

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        previewImage();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        //sidebandFromFFT();
		button3function(); 
    }                                        
	
	private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        //sidebandFromFFT();
        if (amplitudeCheckBox.isSelected() || phaseComboBox.isSelected() || butterworthFilterCheckBox.isSelected())
		    operate2();
        else
            JOptionPane.showMessageDialog(null, "Please select Amplitude, Phase or Butterworth Filter", "ImageJ: " + "Error", JOptionPane.INFORMATION_MESSAGE);

        //button6function();
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) { 
		button2function();
        // TODO add your handling code here:
    }
	private void dxTFActionPerformed(java.awt.event.ActionEvent evt) {                                     
       dx=getDouble(dxTextField);
    }                                    

    private void dyTFActionPerformed(java.awt.event.ActionEvent evt) {                                     
        dy=getDouble(dyTextField);// TODO add your handling code here:
    }                                    

    private void distanceTFActionPerformed(java.awt.event.ActionEvent evt) {                                           
        distance=getDouble(distanceTextField);
    }                                          

    private void wavelengthTFActionPerformed(java.awt.event.ActionEvent evt) {                                             
        wavelength=getDouble(wavelengthTextField);// TODO add your handling code here:
    }                                            

    private void wavelengthTFFocusLost(java.awt.event.FocusEvent evt) {                                       
        wavelength = getDouble(wavelengthTextField);
    }                                      

    private void distanceTFFocusLost(java.awt.event.FocusEvent evt) {                                     
        distance = getDouble(distanceTextField);
    }

    private void dyTFFocusLost(java.awt.event.FocusEvent evt) {
        dy = getDouble(dyTextField);
    }

    private void dxTFFocusLost(java.awt.event.FocusEvent evt) {
        dx = getDouble(dxTextField);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox amplitudeCheckBox;//amplitude	
    private javax.swing.JCheckBox butterworthFilterCheckBox;//butterworth filter
    private javax.swing.JTextField distanceTextField;//distance
    private javax.swing.JTextField dxTextField;//dx
    private javax.swing.JTextField dyTextField;//dy
    private javax.swing.JButton hologramSelectButton;//hologram ....
    private javax.swing.JComboBox hologramComboBox;//hologram dropdown box
    private javax.swing.JButton resetFileListButton;//reset file list
    private javax.swing.JButton unwrapButton;// UnWrap
    private javax.swing.JButton graph3DButton;// 3D graph
    private javax.swing.JButton previewPointsButton;//preview points
    private javax.swing.JButton reconstructButton;// reconstruct
    private javax.swing.JButton numericalPropagationButton;// Numerical Propagation
    private javax.swing.JLabel pathLabel;//Path
    private javax.swing.JLabel dyLabel;//dy
    private javax.swing.JLabel wavelengthLabel;//wavelength
    private javax.swing.JLabel distanceLabel;//distance
    private javax.swing.JLabel hologramLabel;//Hologram
    private javax.swing.JLabel refrenceLabel;//Reference
    private javax.swing.JLabel toleranceLabel;//Tolerance
    private javax.swing.JLabel radiusLabel;//Radius
    private javax.swing.JLabel iterationsLabel;//Iterations
    private javax.swing.JLabel extractLabel;//Extract
    //private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel dxLabel;//dx
    private javax.swing.JPanel filesPanel;//files
    private javax.swing.JPanel phaseRetrievalPanel;//Phase Retrieval
    private javax.swing.JPanel reconstructPanel;//reconstruct
    private javax.swing.JButton pathSelectButton;// path ...
    private javax.swing.JTextField pathTextField;// path text field
    private javax.swing.JCheckBox phaseComboBox;// Phase
    private javax.swing.JTextField iterationsTextField;//iterations
    //private javax.swing.JTextField ratioTF;
    private javax.swing.JComboBox referenceComboBox;//Reference dropdown box
    private javax.swing.JButton refrenceSelectButton;//Reference ...
    private javax.swing.JTextField wavelengthTextField;//wavelength
    private javax.swing.JTextField toleranceTextField;//tollerance
    private javax.swing.JTextField sigmaTextField;//radius
    // End of variables declaration                  
    
}
