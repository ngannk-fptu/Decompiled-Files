/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.examples.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import org.jpedal.jbig2.JBIG2Decoder;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.examples.viewer.FileFilterer;
import org.jpedal.jbig2.examples.viewer.NavigationToolbar;
import org.jpedal.jbig2.image.JBIG2Bitmap;

public class JBIG2Viewer
extends JFrame {
    private JFrame mainFrame = this;
    private JScrollPane jsp;
    private BufferedImage image;
    private JLabel imageLabel = new JLabel();
    private JComboBox scalingBox;
    private String scalingItem = "";
    private double scaling;
    private JComboBox rotationBox;
    private String rotationItem = "";
    private int rotation;
    private NavigationToolbar navToolbar;
    private JBIG2Decoder decoder;
    private int currentPage;

    public static void main(String[] stringArray) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new JBIG2Viewer();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public JBIG2Viewer() {
        this.setSize(500, 500);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);
        this.getContentPane().setLayout(new BorderLayout());
        this.setUpToolbar();
        this.imageLabel.setHorizontalAlignment(0);
        this.imageLabel.setVerticalAlignment(1);
        this.jsp = new JScrollPane(this.imageLabel);
        this.getContentPane().add((Component)this.jsp, "Center");
        this.navToolbar = new NavigationToolbar(this);
        this.navToolbar.setFloatable(false);
        this.getContentPane().add((Component)this.navToolbar, "South");
        this.setTitle("JPedal JBIG2 Image Decoder");
        this.setVisible(true);
    }

    private void setUpToolbar() {
        JToolBar jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setBorder(BorderFactory.createEtchedBorder());
        JButton jButton = new JButton(new AbstractAction("Open", new ImageIcon(this.getClass().getResource("/org/jpedal/jbig2/examples/viewer/res/open.png"))){

            public void actionPerformed(ActionEvent actionEvent) {
                JBIG2Viewer.this.openFile();
            }
        });
        jButton.setText(null);
        jButton.setToolTipText("Open New File");
        jToolBar.add(jButton);
        jToolBar.add(Box.createRigidArea(new Dimension(7, 0)));
        JButton jButton2 = new JButton(new AbstractAction("Save", new ImageIcon(this.getClass().getResource("/org/jpedal/jbig2/examples/viewer/res/save.png"))){

            public void actionPerformed(ActionEvent actionEvent) {
                if (JBIG2Viewer.this.image == null) {
                    JOptionPane.showMessageDialog(JBIG2Viewer.this.mainFrame, "No image is open");
                } else {
                    JBIG2Viewer.this.saveFile();
                }
            }
        });
        jButton2.setText(null);
        jButton2.setToolTipText("Save File As");
        jToolBar.add(jButton2);
        jToolBar.add(Box.createRigidArea(new Dimension(7, 0)));
        JButton jButton3 = new JButton(new AbstractAction("Properties", new ImageIcon(this.getClass().getResource("/org/jpedal/jbig2/examples/viewer/res/properties.png"))){

            public void actionPerformed(ActionEvent actionEvent) {
            }
        });
        jButton3.setText(null);
        jButton3.setToolTipText("File Properties");
        jToolBar.add(Box.createRigidArea(new Dimension(7, 0)));
        jToolBar.add(new JLabel("Zoom:"));
        jToolBar.add(Box.createRigidArea(new Dimension(3, 0)));
        ActionListener actionListener = new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JBIG2Viewer.this.setScalingAndRotation();
            }
        };
        this.scalingBox = new JComboBox<String>(new String[]{"Window", "Height", "Width", "25", "50", "75", "100", "125", "150", "200", "250", "500", "750", "1000"});
        this.scalingBox.setEditable(true);
        this.scalingBox.setPreferredSize(new Dimension(this.scalingBox.getPreferredSize().width, jToolBar.getHeight()));
        this.scalingBox.setPrototypeDisplayValue("XXXXXXXX");
        this.scalingBox.setMaximumSize(new Dimension(100, 100));
        jToolBar.add(this.scalingBox);
        jToolBar.add(Box.createRigidArea(new Dimension(7, 0)));
        jToolBar.add(new JLabel("Rotation:"));
        jToolBar.add(Box.createRigidArea(new Dimension(3, 0)));
        this.rotationBox = new JComboBox<String>(new String[]{"0", "90", "180", "270"});
        this.rotationBox.setEditable(true);
        this.rotationBox.setPreferredSize(new Dimension(this.rotationBox.getPreferredSize().width, jToolBar.getHeight()));
        this.rotationBox.setMaximumSize(new Dimension(100, 100));
        jToolBar.add(this.rotationBox);
        this.getContentPane().add((Component)jToolBar, "North");
        this.rotationBox.addActionListener(actionListener);
        this.scalingBox.addActionListener(actionListener);
    }

    private void saveFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.addChoosableFileFilter(new FileFilterer(new String[]{"png"}, "PNG (*.png)"));
        jFileChooser.setFileSelectionMode(0);
        int n = jFileChooser.showSaveDialog(null);
        if (n == 0) {
            File file = jFileChooser.getSelectedFile();
            String string = file.getAbsolutePath();
            if (!string.toLowerCase().endsWith(".png")) {
                file = new File(string + ".png");
            }
            try {
                ImageIO.write((RenderedImage)this.image, "png", file);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    private void openFile() {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setFileSelectionMode(0);
        String[] stringArray = new String[]{"jb2", "jbig2"};
        jFileChooser.addChoosableFileFilter(new FileFilterer(stringArray, "JBIG2 (jb2, jbig2)"));
        int n = jFileChooser.showOpenDialog(this.mainFrame);
        if (n == 0) {
            this.rotation = 0;
            this.rotationItem = "0";
            this.rotationBox.setSelectedItem(this.rotationItem);
            this.scaling = 1.0;
            this.scalingItem = "Window";
            this.scalingBox.setSelectedItem(this.scalingItem);
            this.decoder = new JBIG2Decoder();
            try {
                this.decoder.decodeJBIG2(jFileChooser.getSelectedFile());
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            catch (JBIG2Exception jBIG2Exception) {
                jBIG2Exception.printStackTrace();
            }
            this.navToolbar.setCurrentPage(1);
            this.navToolbar.setTotalNoOfPages(this.decoder.getNumberOfPages());
            this.currentPage = 1;
            this.image = this.decoder.getPageAsBufferedImage(this.currentPage);
            this.setScalingAndRotation();
        }
    }

    private void setScalingAndRotation() {
        int n;
        if (this.image == null) {
            return;
        }
        String string = (String)this.scalingBox.getSelectedItem();
        if (string.equals("Window")) {
            int n2;
            n = this.jsp.getWidth();
            int n3 = this.jsp.getHeight();
            int n4 = this.image.getWidth();
            if ((double)n / (double)n4 < (double)n3 / (double)(n2 = this.image.getHeight())) {
                this.scaleToWidth();
            } else {
                this.scaleToHeight();
            }
            this.jsp.setHorizontalScrollBarPolicy(31);
            this.jsp.setVerticalScrollBarPolicy(21);
        } else {
            this.jsp.setHorizontalScrollBarPolicy(30);
            this.jsp.setVerticalScrollBarPolicy(20);
            if (string.equals("Height")) {
                this.scaleToHeight();
            } else if (string.equals("Width")) {
                this.scaleToWidth();
            } else {
                try {
                    n = Integer.parseInt(string);
                    this.scaling = (double)n / 100.0;
                }
                catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    this.scalingBox.setSelectedItem(this.scalingItem);
                    return;
                }
            }
        }
        String string2 = (String)this.rotationBox.getSelectedItem();
        try {
            this.rotation = Integer.parseInt(string2);
        }
        catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
            this.rotationBox.setSelectedItem(this.rotationItem);
            return;
        }
        Image image = this.image.getScaledInstance((int)((double)this.image.getWidth() * this.scaling), -1, 1);
        BufferedImage bufferedImage = this.rotate(this.toBufferedImage(image), (double)this.rotation * Math.PI / 180.0);
        this.imageLabel.setIcon(new ImageIcon(bufferedImage));
        this.scalingItem = (String)this.scalingBox.getSelectedItem();
    }

    private void setRotation() {
        String string = (String)this.rotationBox.getSelectedItem();
        try {
            this.rotation = Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
            this.rotationBox.setSelectedItem(this.rotationItem);
            return;
        }
        BufferedImage bufferedImage = this.rotate(this.image, (double)this.rotation * Math.PI / 180.0);
        this.imageLabel.setIcon(new ImageIcon(bufferedImage));
        this.rotationItem = (String)this.rotationBox.getSelectedItem();
    }

    private void scaleToWidth() {
        if (this.image == null) {
            return;
        }
        System.out.println(this.jsp.getWidth() + " " + this.image.getWidth() + " <<<");
        this.scaling = (double)this.jsp.getWidth() / (double)this.image.getWidth();
    }

    private void scaleToHeight() {
        if (this.image == null) {
            return;
        }
        this.scaling = (double)this.jsp.getHeight() / (double)this.image.getHeight();
    }

    private BufferedImage rotate(BufferedImage bufferedImage, double d) {
        if (bufferedImage == null) {
            return null;
        }
        int n = bufferedImage.getWidth();
        int n2 = bufferedImage.getHeight();
        int n3 = (int)Math.round((double)n2 * Math.abs(Math.sin(d)) + (double)n * Math.abs(Math.cos(d)));
        int n4 = (int)Math.round((double)n2 * Math.abs(Math.cos(d)) + (double)n * Math.abs(Math.sin(d)));
        AffineTransform affineTransform = AffineTransform.getTranslateInstance((n3 - n) / 2, (n4 - n2) / 2);
        affineTransform.rotate(d, n / 2, n2 / 2);
        BufferedImage bufferedImage2 = new BufferedImage(n3, n4, 2);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        graphics2D.drawRenderedImage(bufferedImage, affineTransform);
        graphics2D.dispose();
        return bufferedImage2;
    }

    private boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage)image;
            return bufferedImage.getColorModel().hasAlpha();
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        ColorModel colorModel = pixelGrabber.getColorModel();
        return colorModel.hasAlpha();
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();
        boolean bl = this.hasAlpha(image);
        BufferedImage bufferedImage = null;
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int n = 1;
            if (bl) {
                n = 2;
            }
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
            bufferedImage = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null), n);
        }
        catch (HeadlessException headlessException) {
            // empty catch block
        }
        if (bufferedImage == null) {
            int n = 1;
            if (bl) {
                n = 2;
            }
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), n);
        }
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public static void displayJBIG2AsImage(JBIG2Bitmap jBIG2Bitmap) {
        byte[] byArray = jBIG2Bitmap.getData(true);
        if (byArray == null) {
            return;
        }
        int n = byArray.length;
        byte[] byArray2 = new byte[n];
        System.arraycopy(byArray, 0, byArray2, 0, n);
        int n2 = jBIG2Bitmap.getWidth();
        int n3 = jBIG2Bitmap.getHeight();
        DataBufferByte dataBufferByte = new DataBufferByte(byArray2, byArray2.length);
        WritableRaster writableRaster = Raster.createPackedRaster(dataBufferByte, n2, n3, 1, null);
        BufferedImage bufferedImage = new BufferedImage(n2, n3, 12);
        bufferedImage.setData(writableRaster);
        Image image = bufferedImage.getScaledInstance(500, -1, 16);
        BufferedImage bufferedImage2 = new BufferedImage(image.getWidth(null), image.getHeight(null), 4);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        JLabel jLabel = new JLabel(new ImageIcon(bufferedImage2));
        JOptionPane.showConfirmDialog(null, jLabel, "JBIG2 Display", -1, -1);
    }

    public JBIG2Decoder getDecoder() {
        return this.decoder;
    }

    public void displayPage(int n) {
        if (this.image != null && n > 0 && n <= this.decoder.getNumberOfPages()) {
            this.image = this.decoder.getPageAsBufferedImage(n);
            this.currentPage = n;
            this.setScalingAndRotation();
            this.navToolbar.setCurrentPage(this.currentPage);
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }
}

