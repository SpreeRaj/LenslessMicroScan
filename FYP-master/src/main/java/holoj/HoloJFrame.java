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
        dx=getDouble(dxTF);
        dy=getDouble(dyTF);
        Tolerance=getDouble(toleranceTF);
        wavelength=getDouble(wavelengthTF);
        distance=getDouble(distanceTF);
        Sigma=getInteger(sigmaTF);
        Iterations=getInteger(iterationsTF);
         
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
			wavelength=getDouble(wavelengthTF);
			distance=getDouble(distanceTF);
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
        Tolerance=getDouble(toleranceTF);
        wavelength=getDouble(wavelengthTF);
        distance=getDouble(distanceTF);
        Sigma=getInteger(sigmaTF);
        Iterations=getInteger(iterationsTF);
        HoloJUtils.previewPoints(holo,distance,wavelength,Iterations,Tolerance,Sigma);

    }


    /** Creates new form HoloJFrame */
    public HoloJFrame() {
        Java2.setSystemLookAndFeel();
        initComponents();
        initFileList(holoCB);
        initFileList(refCB);
		
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
        pathTF.setText(od.getDirectory());
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
        String dir=pathTF.getText();
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
        ImagePlus imp=getImage(holoCB);
        if(imp!=null) {
            imageCal = imp.getCalibration().copy();
            title = imp.getTitle();
            proc=new HoloJProcessor(imp.getProcessor(),getDouble(dxTF),getDouble(dyTF));
        }
        return proc;
    }
    
    private HoloJProcessor getReferenceProcessor(){
        HoloJProcessor proc=null;
        ImagePlus imp=getImage(refCB);
        if(imp!=null) proc=new HoloJProcessor(imp.getProcessor(),getDouble(dxTF),getDouble(dyTF));
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
		

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        pathTF = new JTextField();
        pathB = new JButton();
        jLabel2 = new JLabel();
        holoB = new JButton();
        holoCB = new JComboBox();
        jLabel3 = new JLabel();
        refCB = new JComboBox();
        rholoB = new JButton();
        jButton1 = new JButton();
        jPanel2 = new JPanel();
        jLabel4 = new JLabel();
        toleranceTF = new JTextField();
        jLabel5 = new JLabel();
        sigmaTF = new JTextField();
        jLabel6 = new JLabel();
        iterationsTF = new JTextField();
        jButton4 = new javax.swing.JButton();
        //jLabel8 = new JLabel();
        //ratioTF = new JTextField();
        jPanel3 = new JPanel();
        jButton5 = new JButton();
        amplitudeCB = new JCheckBox();
        phaseCB = new JCheckBox();
        jLabel7 = new JLabel();
        butterCB = new JCheckBox();
        jButton3 = new JButton();
        jButton6 = new JButton();
        jButton2 = new JButton();
		dxTF = new JTextField();
        dyTF = new JTextField();
        wavelengthTF = new JTextField();
        distanceTF = new JTextField();
        jLabel9 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("HoloJ");

        jPanel1.setBorder(BorderFactory.createTitledBorder("Files"));

        jLabel1.setText("Path");

        pathTF.setText("No Directory Selected");
        pathTF.setMaximumSize(new Dimension(500, 20));
        pathTF.setPreferredSize(new Dimension(50, 20));

        pathB.setText("...");
        pathB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pathBActionPerformed(evt);
            }
        });

        jLabel2.setText("Hologram");

        holoB.setText("...");
        holoB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                holoBActionPerformed(evt);
            }
        });

        holoCB.setEditable(true);
        holoCB.setMaximumRowCount(5);
        holoCB.setModel(new DefaultComboBoxModel(new String[] { "No Image Selected" }));
        holoCB.setMaximumSize(new Dimension(138, 22));

        jLabel3.setText("Reference");

        refCB.setEditable(true);
        refCB.setMaximumRowCount(5);
        refCB.setModel(new DefaultComboBoxModel(new String[] { "No Image Selected" }));
        refCB.setMaximumSize(new Dimension(138, 22));

        rholoB.setText("...");
        rholoB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rholoBActionPerformed(evt);
            }
        });

        jButton1.setText("Reset File List");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(45, 45, 45)
                .addComponent(pathTF, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(pathB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(holoCB, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(holoB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(7, 7, 7)
                .addComponent(refCB, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(rholoB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jButton1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(pathTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pathB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(holoCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(holoB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(refCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rholoB, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jButton1))
        );

        jPanel2.setBorder(BorderFactory.createTitledBorder("Phase Retrieval"));

        jLabel4.setText("Tolerance");

        toleranceTF.setHorizontalAlignment(JTextField.TRAILING);
        toleranceTF.setText("3");
        toleranceTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                toleranceTFActionPerformed(evt);
            }
        });
        toleranceTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {

                toleranceTFFocusLost(evt);
            }
        });

        jLabel5.setText("Radius");

        sigmaTF.setHorizontalAlignment(JTextField.TRAILING);
        sigmaTF.setText("2");
        sigmaTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sigmaTFActionPerformed(evt);
            }
        });
        sigmaTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                sigmaTFFocusLost(evt);
            }
        });

        jLabel6.setText("Iterations");

        iterationsTF.setHorizontalAlignment(JTextField.TRAILING);
        iterationsTF.setText("1");
        iterationsTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                iterationsTFActionPerformed(evt);
            }
        });
        iterationsTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                iterationsTFFocusLost(evt);
            }
        });

        jButton4.setText("Preview Points");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
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

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel4)
                .addGap(4, 4, 4)
                .addComponent(toleranceTF, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                //.addComponent(jLabel8)
                .addGap(18, 18, 18)
                //.addComponent(ratioTF, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                    )
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addComponent(sigmaTF, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel6)
                .addGap(7, 7, 7)
                .addComponent(iterationsTF, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(toleranceTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    //.addComponent(jLabel8)
                    //.addComponent(ratioTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(sigmaTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(iterationsTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(jButton4))
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder("Reconstruct"));

        jButton5.setText("Reconstruct");
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        amplitudeCB.setText("Amplitude");
        amplitudeCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                amplitudeCBActionPerformed(evt);
            }
        });

        phaseCB.setText("Phase");
        phaseCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                phaseCBActionPerformed(evt);
            }
        });

        jLabel7.setText("Extract:");

        butterCB.setText("Butterworth Filter");
        butterCB.setMaximumSize(new Dimension(135, 25));
        butterCB.setMinimumSize(new Dimension(135, 25));
        butterCB.setPreferredSize(new Dimension(135, 25));
        butterCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butterCBActionPerformed(evt);
            }
        });

        jButton3.setText("3D graph");
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setText("Numerical Propagation");
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton2.setText("UnWrap");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////
        dxTF.setText("0.00000160");
        dxTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                dxTFFocusLost(evt);
            }
        });
        dxTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dxTFActionPerformed(evt);
            }
        });

        dyTF.setText("0.00000160");
        dyTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                dyTFFocusLost(evt);
            }
        });
        dyTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dyTFActionPerformed(evt);
            }
        });

        wavelengthTF.setText("0.000000633");
        wavelengthTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                wavelengthTFFocusLost(evt);
            }
        });
        wavelengthTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                wavelengthTFActionPerformed(evt);
            }
        });

        distanceTF.setText("0.001");
        distanceTF.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                distanceTFFocusLost(evt);
            }
        });
        distanceTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                distanceTFActionPerformed(evt);
            }
        });

        jLabel9.setText("dx");

        jLabel10.setText("dy");

        jLabel11.setText("wavelength");

        jLabel12.setText("distance");

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(amplitudeCB, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel7))
                            .addComponent(phaseCB, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                            .addComponent(butterCB, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(dxTF)
                            .addComponent(dyTF))))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jButton5, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(wavelengthTF, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(distanceTF, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton3, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton6, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
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
                                .addComponent(jLabel7)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(phaseCB, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(amplitudeCB)
                                .addGap(1, 1, 1)
                                .addComponent(butterCB, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton5)
                                .addGap(29, 29, 29)
                                .addComponent(jButton6)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(dxTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(wavelengthTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(jLabel11, GroupLayout.Alignment.TRAILING))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dyTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(distanceTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addComponent(jLabel10))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
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
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                              
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void iterationsTFFocusLost(java.awt.event.FocusEvent evt) {
        radius = getInteger(iterationsTF);
    }                                  

//    private void ratioTFFocusLost(java.awt.event.FocusEvent evt) {
//        ratio = getInteger(ratioTF);
//    }

    private void sigmaTFFocusLost(java.awt.event.FocusEvent evt) {
        //TBC = getInteger(yTF);
    }                             

    private void toleranceTFFocusLost(java.awt.event.FocusEvent evt) {
        Tolerance = getDouble(toleranceTF);
    }                             

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        operate();
    }                                        

    private void amplitudeCBActionPerformed(java.awt.event.ActionEvent evt) {                                            
        amplitude=getBoolean(amplitudeCB);
    }                                           

    private void phaseCBActionPerformed(java.awt.event.ActionEvent evt) {                                        
        phase=getBoolean(phaseCB);
    }                                       

    private void butterCBActionPerformed(java.awt.event.ActionEvent evt) {                                         
        butterworth=getBoolean(butterCB);
    }                                        

    private void sigmaTFActionPerformed(java.awt.event.ActionEvent evt) {
        //TBC = getInteger(yTF);
    }                                   

    private void toleranceTFActionPerformed(java.awt.event.ActionEvent evt) {
        Tolerance = getDouble(toleranceTF);
    }                                   

//    private void ratioTFActionPerformed(java.awt.event.ActionEvent evt) {
//        ratio=getInteger(ratioTF);
//    }

    private void iterationsTFActionPerformed(java.awt.event.ActionEvent evt) {
        radius=getInteger(iterationsTF);
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        initFileList(holoCB);
        initFileList(refCB);
    }                                        

    private void rholoBActionPerformed(java.awt.event.ActionEvent evt) {                                       
        addFileToList(refCB);
    }                                      

    private void holoBActionPerformed(java.awt.event.ActionEvent evt) {                                      
        addFileToList(holoCB);
    }                                     

    private void pathBActionPerformed(java.awt.event.ActionEvent evt) {                                      
       setDir(pathTF);
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
        if (amplitudeCB.isSelected() || phaseCB.isSelected() || butterCB.isSelected())
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
       dx=getDouble(dxTF);
    }                                    

    private void dyTFActionPerformed(java.awt.event.ActionEvent evt) {                                     
        dy=getDouble(dyTF);// TODO add your handling code here:
    }                                    

    private void distanceTFActionPerformed(java.awt.event.ActionEvent evt) {                                           
        distance=getDouble(distanceTF);
    }                                          

    private void wavelengthTFActionPerformed(java.awt.event.ActionEvent evt) {                                             
        wavelength=getDouble(wavelengthTF);// TODO add your handling code here:
    }                                            

    private void wavelengthTFFocusLost(java.awt.event.FocusEvent evt) {                                       
        wavelength = getDouble(wavelengthTF);
    }                                      

    private void distanceTFFocusLost(java.awt.event.FocusEvent evt) {                                     
        distance = getDouble(distanceTF);
    }

    private void dyTFFocusLost(java.awt.event.FocusEvent evt) {
        dy = getDouble(dyTF);
    }

    private void dxTFFocusLost(java.awt.event.FocusEvent evt) {
        dx = getDouble(dxTF);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox amplitudeCB;
    private javax.swing.JCheckBox butterCB;
    private javax.swing.JTextField distanceTF;
    private javax.swing.JTextField dxTF;
    private javax.swing.JTextField dyTF;
    private javax.swing.JButton holoB;
    private javax.swing.JComboBox holoCB;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    //private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton pathB;
    private javax.swing.JTextField pathTF;
    private javax.swing.JCheckBox phaseCB;
    private javax.swing.JTextField iterationsTF;
    //private javax.swing.JTextField ratioTF;
    private javax.swing.JComboBox refCB;
    private javax.swing.JButton rholoB;
    private javax.swing.JTextField wavelengthTF;
    private javax.swing.JTextField toleranceTF;
    private javax.swing.JTextField sigmaTF;
    // End of variables declaration                  
    
}
