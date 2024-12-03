/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.image.ImageUtil
 *  com.twelvemonkeys.imageio.ImageReaderBase
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.imageio.util.ImageTypeSpecifiers
 *  com.twelvemonkeys.util.WeakWeakMap
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapDescriptor;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapIndexed;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapMask;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapRGB;
import com.twelvemonkeys.imageio.plugins.bmp.BitmapUnsupported;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.Directory;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.util.WeakWeakMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

abstract class DIBImageReader
extends ImageReaderBase {
    private Directory directory;
    private Map<DirectoryEntry, DIBHeader> headers = new WeakHashMap<DirectoryEntry, DIBHeader>();
    private Map<DirectoryEntry, BitmapDescriptor> descriptors = new WeakWeakMap();
    private ImageReader pngImageReader;

    protected DIBImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    protected void resetMembers() {
        this.directory = null;
        this.headers.clear();
        this.descriptors.clear();
        if (this.pngImageReader != null) {
            this.pngImageReader.dispose();
            this.pngImageReader = null;
        }
    }

    public Iterator<ImageTypeSpecifier> getImageTypes(int n) throws IOException {
        ImageTypeSpecifier imageTypeSpecifier;
        DirectoryEntry directoryEntry = this.getEntry(n);
        if (this.isPNG(directoryEntry)) {
            return this.getImageTypesPNG(directoryEntry);
        }
        ArrayList<ImageTypeSpecifier> arrayList = new ArrayList<ImageTypeSpecifier>();
        DIBHeader dIBHeader = this.getHeader(directoryEntry);
        switch (dIBHeader.getBitCount()) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                int n2 = directoryEntry.getOffset() + dIBHeader.getSize();
                if ((long)n2 != this.imageInput.getStreamPosition()) {
                    this.imageInput.seek(n2);
                }
                BitmapIndexed bitmapIndexed = new BitmapIndexed(directoryEntry, dIBHeader);
                this.readColorMap(bitmapIndexed);
                imageTypeSpecifier = ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)bitmapIndexed.createColorModel());
                break;
            }
            case 16: {
                imageTypeSpecifier = ImageTypeSpecifiers.createFromBufferedImageType((int)9);
                break;
            }
            case 24: {
                imageTypeSpecifier = new BitmapRGB(directoryEntry, dIBHeader).hasMask() ? ImageTypeSpecifiers.createFromBufferedImageType((int)6) : ImageTypeSpecifiers.createFromBufferedImageType((int)5);
                break;
            }
            case 32: {
                imageTypeSpecifier = ImageTypeSpecifiers.createFromBufferedImageType((int)2);
                break;
            }
            default: {
                throw new IIOException(String.format("Unknown bit depth: %d", dIBHeader.getBitCount()));
            }
        }
        arrayList.add(imageTypeSpecifier);
        return arrayList.iterator();
    }

    public int getNumImages(boolean bl) throws IOException {
        return this.getDirectory().count();
    }

    public int getWidth(int n) throws IOException {
        return this.getEntry(n).getWidth();
    }

    public int getHeight(int n) throws IOException {
        return this.getEntry(n).getHeight();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        BufferedImage bufferedImage;
        this.checkBounds(n);
        this.processImageStarted(n);
        DirectoryEntry directoryEntry = this.getEntry(n);
        if (this.isPNG(directoryEntry)) {
            bufferedImage = this.readPNG(directoryEntry, imageReadParam);
        } else {
            bufferedImage = DIBImageReader.hasExplicitDestination((ImageReadParam)imageReadParam) ? DIBImageReader.getDestination((ImageReadParam)imageReadParam, this.getImageTypes(n), (int)this.getWidth(n), (int)this.getHeight(n)) : null;
            BufferedImage bufferedImage2 = this.readBitmap(directoryEntry);
            if (imageReadParam != null) {
                bufferedImage2 = DIBImageReader.fakeAOI((BufferedImage)bufferedImage2, (ImageReadParam)imageReadParam);
                bufferedImage2 = ImageUtil.toBuffered((Image)DIBImageReader.fakeSubsampling((Image)bufferedImage2, (ImageReadParam)imageReadParam));
            }
            if (bufferedImage == null) {
                bufferedImage = bufferedImage2;
            } else {
                Graphics2D graphics2D = bufferedImage.createGraphics();
                try {
                    graphics2D.setComposite(AlphaComposite.Src);
                    graphics2D.drawImage((Image)bufferedImage2, 0, 0, null);
                }
                finally {
                    graphics2D.dispose();
                }
            }
        }
        this.processImageProgress(100.0f);
        this.processImageComplete();
        return bufferedImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isPNG(DirectoryEntry directoryEntry) throws IOException {
        long l;
        this.imageInput.seek(directoryEntry.getOffset());
        this.imageInput.setByteOrder(ByteOrder.BIG_ENDIAN);
        try {
            l = this.imageInput.readLong();
        }
        finally {
            this.imageInput.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
        return l == -8552249625308161526L;
    }

    private BufferedImage readPNG(DirectoryEntry directoryEntry, ImageReadParam imageReadParam) throws IOException {
        return this.initPNGReader(directoryEntry).read(0, imageReadParam);
    }

    private Iterator<ImageTypeSpecifier> getImageTypesPNG(DirectoryEntry directoryEntry) throws IOException {
        return this.initPNGReader(directoryEntry).getImageTypes(0);
    }

    private ImageReader initPNGReader(DirectoryEntry directoryEntry) throws IOException {
        ImageReader imageReader = this.getPNGReader();
        this.imageInput.seek(directoryEntry.getOffset());
        SubImageInputStream subImageInputStream = new SubImageInputStream(this.imageInput, (long)directoryEntry.getSize());
        imageReader.setInput(subImageInputStream);
        return imageReader;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private ImageReader getPNGReader() throws IIOException {
        if (this.pngImageReader == null) {
            Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("PNG");
            if (!iterator.hasNext()) throw new IIOException("No PNGImageReader found using ImageIO, can't read PNG encoded ICO format.");
            this.pngImageReader = iterator.next();
            return this.pngImageReader;
        } else {
            this.pngImageReader.reset();
        }
        return this.pngImageReader;
    }

    private DIBHeader getHeader(DirectoryEntry directoryEntry) throws IOException {
        if (!this.headers.containsKey(directoryEntry)) {
            this.imageInput.seek(directoryEntry.getOffset());
            DIBHeader dIBHeader = DIBHeader.read(this.imageInput);
            this.headers.put(directoryEntry, dIBHeader);
        }
        return this.headers.get(directoryEntry);
    }

    private BufferedImage readBitmap(DirectoryEntry directoryEntry) throws IOException {
        BitmapDescriptor bitmapDescriptor = this.descriptors.get(directoryEntry);
        if (bitmapDescriptor == null || !this.descriptors.containsKey(directoryEntry)) {
            DIBHeader dIBHeader = this.getHeader(directoryEntry);
            int n = directoryEntry.getOffset() + dIBHeader.getSize();
            if ((long)n != this.imageInput.getStreamPosition()) {
                this.imageInput.seek(n);
            }
            if (dIBHeader.getCompression() != 0) {
                bitmapDescriptor = new BitmapUnsupported(directoryEntry, dIBHeader, String.format("Unsupported compression: %d", dIBHeader.getCompression()));
            } else {
                int n2 = dIBHeader.getBitCount();
                switch (n2) {
                    case 1: 
                    case 4: 
                    case 8: {
                        bitmapDescriptor = new BitmapIndexed(directoryEntry, dIBHeader);
                        this.readBitmapIndexed((BitmapIndexed)bitmapDescriptor);
                        break;
                    }
                    case 16: {
                        bitmapDescriptor = new BitmapRGB(directoryEntry, dIBHeader);
                        this.readBitmap16(bitmapDescriptor);
                        break;
                    }
                    case 24: {
                        bitmapDescriptor = new BitmapRGB(directoryEntry, dIBHeader);
                        this.readBitmap24(bitmapDescriptor);
                        break;
                    }
                    case 32: {
                        bitmapDescriptor = new BitmapRGB(directoryEntry, dIBHeader);
                        this.readBitmap32(bitmapDescriptor);
                        break;
                    }
                    default: {
                        bitmapDescriptor = new BitmapUnsupported(directoryEntry, dIBHeader, String.format("Unsupported bit count %d", n2));
                    }
                }
            }
            this.descriptors.put(directoryEntry, bitmapDescriptor);
        }
        return bitmapDescriptor.getImage();
    }

    private void readBitmapIndexed(BitmapIndexed bitmapIndexed) throws IOException {
        this.readColorMap(bitmapIndexed);
        switch (bitmapIndexed.getBitCount()) {
            case 1: {
                this.readBitmapIndexed1(bitmapIndexed, false);
                break;
            }
            case 4: {
                this.readBitmapIndexed4(bitmapIndexed);
                break;
            }
            case 8: {
                this.readBitmapIndexed8(bitmapIndexed);
            }
        }
        BitmapMask bitmapMask = new BitmapMask(bitmapIndexed.entry, bitmapIndexed.header);
        this.readBitmapIndexed1(bitmapMask.bitMask, true);
        bitmapIndexed.setMask(bitmapMask);
    }

    private void readColorMap(BitmapIndexed bitmapIndexed) throws IOException {
        int n = bitmapIndexed.getColorCount();
        for (int i = 0; i < n; ++i) {
            bitmapIndexed.colors[i] = this.imageInput.readInt() & 0xFFFFFF | 0xFF000000;
        }
    }

    private void readBitmapIndexed1(BitmapIndexed bitmapIndexed, boolean bl) throws IOException {
        int n = DIBImageReader.adjustToPadding(bitmapIndexed.getWidth() + 7 >> 3);
        byte[] byArray = new byte[n];
        for (int i = 0; i < bitmapIndexed.getHeight(); ++i) {
            this.imageInput.readFully(byArray, 0, n);
            int n2 = 0;
            int n3 = 128;
            int n4 = (bitmapIndexed.getHeight() - i - 1) * bitmapIndexed.getWidth();
            for (int j = 0; j < bitmapIndexed.getWidth(); ++j) {
                bitmapIndexed.bits[n4++] = (byArray[n2] & n3) / n3 & 0xFF;
                if (n3 == 1) {
                    n3 = 128;
                    ++n2;
                    continue;
                }
                n3 >>= 1;
            }
            if (bl) continue;
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapIndexed.getHeight());
        }
    }

    private void readBitmapIndexed4(BitmapIndexed bitmapIndexed) throws IOException {
        int n = DIBImageReader.adjustToPadding(bitmapIndexed.getWidth() + 1 >> 1);
        byte[] byArray = new byte[n];
        for (int i = 0; i < bitmapIndexed.getHeight(); ++i) {
            this.imageInput.readFully(byArray, 0, n);
            int n2 = 0;
            boolean bl = true;
            int n3 = (bitmapIndexed.getHeight() - i - 1) * bitmapIndexed.getWidth();
            for (int j = 0; j < bitmapIndexed.getWidth(); ++j) {
                int n4;
                if (bl) {
                    n4 = (byArray[n2] & 0xF0) >> 4;
                } else {
                    n4 = byArray[n2] & 0xF;
                    ++n2;
                }
                bitmapIndexed.bits[n3++] = n4 & 0xFF;
                bl = !bl;
            }
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapIndexed.getHeight());
        }
    }

    private void readBitmapIndexed8(BitmapIndexed bitmapIndexed) throws IOException {
        int n = DIBImageReader.adjustToPadding(bitmapIndexed.getWidth());
        byte[] byArray = new byte[n];
        for (int i = 0; i < bitmapIndexed.getHeight(); ++i) {
            this.imageInput.readFully(byArray, 0, n);
            int n2 = 0;
            int n3 = (bitmapIndexed.getHeight() - i - 1) * bitmapIndexed.getWidth();
            for (int j = 0; j < bitmapIndexed.getWidth(); ++j) {
                bitmapIndexed.bits[n3++] = byArray[n2++] & 0xFF;
            }
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapIndexed.getHeight());
        }
    }

    private static int adjustToPadding(int n) {
        if ((n & 3) != 0) {
            return (n & 0xFFFFFFFC) + 4;
        }
        return n;
    }

    private void readBitmap16(BitmapDescriptor bitmapDescriptor) throws IOException {
        short[] sArray = new short[bitmapDescriptor.getWidth() * bitmapDescriptor.getHeight()];
        DirectColorModel directColorModel = new DirectColorModel(16, 31744, 992, 31);
        DataBufferUShort dataBufferUShort = new DataBufferUShort(sArray, sArray.length);
        WritableRaster writableRaster = Raster.createPackedRaster(dataBufferUShort, bitmapDescriptor.getWidth(), bitmapDescriptor.getHeight(), bitmapDescriptor.getWidth(), directColorModel.getMasks(), null);
        bitmapDescriptor.image = new BufferedImage(directColorModel, writableRaster, directColorModel.isAlphaPremultiplied(), null);
        for (int i = 0; i < bitmapDescriptor.getHeight(); ++i) {
            int n = (bitmapDescriptor.getHeight() - i - 1) * bitmapDescriptor.getWidth();
            this.imageInput.readFully(sArray, n, bitmapDescriptor.getWidth());
            if (bitmapDescriptor.getWidth() % 2 != 0) {
                this.imageInput.readShort();
            }
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapDescriptor.getHeight());
        }
    }

    private void readBitmap24(BitmapDescriptor bitmapDescriptor) throws IOException {
        byte[] byArray = new byte[bitmapDescriptor.getWidth() * bitmapDescriptor.getHeight() * 3];
        DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length);
        ColorSpace colorSpace = ColorSpace.getInstance(1000);
        int[] nArray = new int[]{8, 8, 8};
        int[] nArray2 = new int[]{2, 1, 0};
        ComponentColorModel componentColorModel = new ComponentColorModel(colorSpace, nArray, false, false, 1, 0);
        int n = bitmapDescriptor.getWidth() * 3;
        int n2 = (8 * n + 31) / 32 * 4;
        WritableRaster writableRaster = Raster.createInterleavedRaster(dataBufferByte, bitmapDescriptor.getWidth(), bitmapDescriptor.getHeight(), n, 3, nArray2, null);
        bitmapDescriptor.image = new BufferedImage(componentColorModel, writableRaster, componentColorModel.isAlphaPremultiplied(), null);
        for (int i = 0; i < bitmapDescriptor.getHeight(); ++i) {
            int n3 = (bitmapDescriptor.getHeight() - i - 1) * n;
            this.imageInput.readFully(byArray, n3, n);
            this.imageInput.skipBytes(n2 - n);
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapDescriptor.getHeight());
        }
        if (bitmapDescriptor.hasMask()) {
            BitmapMask bitmapMask = new BitmapMask(bitmapDescriptor.entry, bitmapDescriptor.header);
            this.readBitmapIndexed1(bitmapMask.bitMask, true);
            bitmapDescriptor.setMask(bitmapMask);
        }
    }

    private void readBitmap32(BitmapDescriptor bitmapDescriptor) throws IOException {
        int[] nArray = new int[bitmapDescriptor.getWidth() * bitmapDescriptor.getHeight()];
        DirectColorModel directColorModel = (DirectColorModel)ColorModel.getRGBdefault();
        DataBufferInt dataBufferInt = new DataBufferInt(nArray, nArray.length);
        WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, bitmapDescriptor.getWidth(), bitmapDescriptor.getHeight(), bitmapDescriptor.getWidth(), directColorModel.getMasks(), null);
        bitmapDescriptor.image = new BufferedImage(directColorModel, writableRaster, directColorModel.isAlphaPremultiplied(), null);
        for (int i = 0; i < bitmapDescriptor.getHeight(); ++i) {
            int n = (bitmapDescriptor.getHeight() - i - 1) * bitmapDescriptor.getWidth();
            this.imageInput.readFully(nArray, n, bitmapDescriptor.getWidth());
            if (this.abortRequested()) {
                this.processReadAborted();
                break;
            }
            this.processImageProgress((float)(100 * i) / (float)bitmapDescriptor.getHeight());
        }
    }

    private Directory getDirectory() throws IOException {
        this.assertInput();
        if (this.directory == null) {
            this.readFileHeader();
        }
        return this.directory;
    }

    private void readFileHeader() throws IOException {
        this.imageInput.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        this.imageInput.readUnsignedShort();
        int n = this.imageInput.readUnsignedShort();
        int n2 = this.imageInput.readUnsignedShort();
        this.directory = Directory.read(n, n2, this.imageInput);
    }

    final DirectoryEntry getEntry(int n) throws IOException {
        Directory directory = this.getDirectory();
        if (n < 0 || n >= directory.count()) {
            throw new IndexOutOfBoundsException(String.format("Index: %d, ImageCount: %d", n, directory.count()));
        }
        return directory.getEntry(n);
    }

    public static void main(String[] stringArray) throws IOException {
        if (stringArray.length == 0) {
            System.err.println("Please specify the icon file name");
            System.exit(1);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        String string = new File(stringArray[0]).getName();
        JFrame jFrame = DIBImageReader.createWindow(string);
        JPanel jPanel = new JPanel(new FlowLayout());
        JScrollPane jScrollPane = new JScrollPane(jPanel, 22, 30);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        jFrame.setContentPane(jScrollPane);
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("ico");
        if (!iterator.hasNext()) {
            System.err.println("No reader for format 'ico' found");
            System.exit(1);
        }
        ImageReader imageReader = iterator.next();
        for (String string2 : stringArray) {
            JPanel jPanel2 = new JPanel(null);
            jPanel2.setLayout(new BoxLayout(jPanel2, 1));
            DIBImageReader.readImagesInFile(string2, imageReader, jPanel2);
            jPanel.add(jPanel2);
        }
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private static void readImagesInFile(String string, ImageReader imageReader, Container container) throws IOException {
        File file = new File(string);
        if (!file.isFile()) {
            System.err.println(string + " not found, or is no file");
        }
        imageReader.setInput(ImageIO.createImageInputStream(file));
        int n = imageReader.getNumImages(true);
        for (int i = 0; i < n; ++i) {
            try {
                DIBImageReader.addImage(container, imageReader, i);
                continue;
            }
            catch (Exception exception) {
                System.err.println("FileName: " + string);
                System.err.println("Icon: " + i);
                exception.printStackTrace();
            }
        }
    }

    private static JFrame createWindow(String string) {
        JFrame jFrame = new JFrame(string);
        jFrame.setDefaultCloseOperation(2);
        jFrame.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        return jFrame;
    }

    private static void addImage(Container container, ImageReader imageReader, int n) throws IOException {
        JButton jButton = new JButton();
        BufferedImage bufferedImage = imageReader.read(n);
        jButton.setIcon(new ImageIcon(bufferedImage){
            TexturePaint texture;

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            private void createTexture(GraphicsConfiguration graphicsConfiguration) {
                BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(20, 20);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                try {
                    graphics2D.setColor(Color.LIGHT_GRAY);
                    graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
                    graphics2D.setColor(Color.GRAY);
                    graphics2D.fillRect(0, 0, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);
                    graphics2D.fillRect(bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);
                }
                finally {
                    graphics2D.dispose();
                }
                this.texture = new TexturePaint(bufferedImage, new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
            }

            @Override
            public void paintIcon(Component component, Graphics graphics, int n, int n2) {
                if (this.texture == null) {
                    this.createTexture(component.getGraphicsConfiguration());
                }
                Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.setPaint(this.texture);
                graphics2D.fillRect(n, n2, this.getIconWidth(), this.getIconHeight());
                super.paintIcon(component, graphics, n, n2);
            }
        });
        jButton.setText(bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + ": " + (bufferedImage.getColorModel() instanceof IndexColorModel ? String.valueOf(((IndexColorModel)bufferedImage.getColorModel()).getMapSize()) : "TrueColor"));
        container.add(jButton);
    }
}

