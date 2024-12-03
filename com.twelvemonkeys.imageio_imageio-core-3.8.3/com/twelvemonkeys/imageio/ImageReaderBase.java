/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.image.BufferedImageIcon
 *  com.twelvemonkeys.image.ImageUtil
 */
package com.twelvemonkeys.imageio;

import com.twelvemonkeys.image.BufferedImageIcon;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.imageio.util.IIOUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public abstract class ImageReaderBase
extends ImageReader {
    private static final Point ORIGIN = new Point(0, 0);
    protected ImageInputStream imageInput;

    protected ImageReaderBase(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    @Override
    public void setInput(Object object, boolean bl, boolean bl2) {
        this.resetMembers();
        super.setInput(object, bl, bl2);
        this.imageInput = object instanceof ImageInputStream ? (ImageInputStream)object : null;
    }

    @Override
    public void dispose() {
        this.resetMembers();
        super.dispose();
    }

    @Override
    public void reset() {
        this.resetMembers();
        super.reset();
    }

    protected abstract void resetMembers();

    @Override
    public IIOMetadata getImageMetadata(int n) throws IOException {
        return null;
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    @Override
    public int getNumImages(boolean bl) throws IOException {
        this.assertInput();
        return 1;
    }

    protected void checkBounds(int n) throws IOException {
        this.assertInput();
        if (n < this.getMinIndex()) {
            throw new IndexOutOfBoundsException("index < minIndex");
        }
        int n2 = this.getNumImages(false);
        if (n2 != -1 && n >= n2) {
            throw new IndexOutOfBoundsException("index >= numImages (" + n + " >= " + n2 + ")");
        }
    }

    protected void assertInput() {
        if (this.getInput() == null) {
            throw new IllegalStateException("getInput() == null");
        }
    }

    public static BufferedImage getDestination(ImageReadParam imageReadParam, Iterator<ImageTypeSpecifier> iterator, int n, int n2) throws IIOException {
        Object object;
        Object object2;
        if (iterator == null || !iterator.hasNext()) {
            throw new IllegalArgumentException("imageTypes null or empty!");
        }
        ImageTypeSpecifier imageTypeSpecifier = null;
        if (imageReadParam != null) {
            object2 = imageReadParam.getDestination();
            if (object2 != null) {
                boolean bl = false;
                while (iterator.hasNext()) {
                    ImageTypeSpecifier imageTypeSpecifier2 = iterator.next();
                    int n3 = imageTypeSpecifier2.getBufferedImageType();
                    if (n3 != 0 && n3 == ((BufferedImage)object2).getType()) {
                        bl = true;
                        break;
                    }
                    if (imageTypeSpecifier2.getSampleModel().getTransferType() != ((BufferedImage)object2).getSampleModel().getTransferType() || !Arrays.equals(imageTypeSpecifier2.getSampleModel().getSampleSize(), ((BufferedImage)object2).getSampleModel().getSampleSize()) || imageTypeSpecifier2.getNumBands() > ((BufferedImage)object2).getSampleModel().getNumBands()) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    throw new IIOException(String.format("Destination image from ImageReadParam does not match legal imageTypes from reader: %s", object2));
                }
                return object2;
            }
            imageTypeSpecifier = imageReadParam.getDestinationType();
        }
        if (imageTypeSpecifier == null) {
            imageTypeSpecifier = iterator.next();
        } else {
            boolean bl = false;
            while (iterator.hasNext()) {
                object = iterator.next();
                if (!((ImageTypeSpecifier)object).equals(imageTypeSpecifier)) continue;
                bl = true;
                break;
            }
            if (!bl) {
                throw new IIOException(String.format("Destination type from ImageReadParam does not match legal imageTypes from reader: %s", imageTypeSpecifier));
            }
        }
        object2 = new Rectangle(0, 0, 0, 0);
        object = new Rectangle(0, 0, 0, 0);
        ImageReaderBase.computeRegions(imageReadParam, n, n2, null, (Rectangle)object2, (Rectangle)object);
        int n4 = ((Rectangle)object).x + ((Rectangle)object).width;
        int n5 = ((Rectangle)object).y + ((Rectangle)object).height;
        long l = (long)n4 * (long)n5;
        if (l > Integer.MAX_VALUE) {
            throw new IIOException(String.format("destination width * height > Integer.MAX_VALUE: %d", l));
        }
        long l2 = l * (long)imageTypeSpecifier.getSampleModel().getNumDataElements();
        if (l2 > Integer.MAX_VALUE) {
            throw new IIOException(String.format("destination width * height * samplesPerPixel > Integer.MAX_VALUE: %d", l2));
        }
        return imageTypeSpecifier.createBufferedImage(n4, n5);
    }

    protected static BufferedImage fakeAOI(BufferedImage bufferedImage, ImageReadParam imageReadParam) {
        return IIOUtil.fakeAOI(bufferedImage, ImageReaderBase.getSourceRegion(imageReadParam, bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    protected static Image fakeSubsampling(Image image, ImageReadParam imageReadParam) {
        return IIOUtil.fakeSubsampling(image, imageReadParam);
    }

    protected static boolean hasExplicitDestination(ImageReadParam imageReadParam) {
        return imageReadParam != null && (imageReadParam.getDestination() != null || imageReadParam.getDestinationType() != null || !ORIGIN.equals(imageReadParam.getDestinationOffset()));
    }

    public static void main(String[] stringArray) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(stringArray[0]));
        if (bufferedImage == null) {
            System.err.println("Supported formats: " + Arrays.toString(IIOUtil.getNormalizedReaderFormatNames()));
            System.exit(1);
        }
        ImageReaderBase.showIt(bufferedImage, stringArray[0]);
    }

    protected static void showIt(final BufferedImage bufferedImage, final String string) {
        try {
            SwingUtilities.invokeAndWait(new Runnable(){

                @Override
                public void run() {
                    JFrame jFrame = new JFrame(string);
                    jFrame.getRootPane().getActionMap().put("window-close", new AbstractAction(){

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            Window window = SwingUtilities.getWindowAncestor((Component)actionEvent.getSource());
                            window.setVisible(false);
                            window.dispose();
                        }
                    });
                    jFrame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(87, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "window-close");
                    jFrame.addWindowListener(new ExitIfNoWindowPresentHandler());
                    jFrame.setDefaultCloseOperation(2);
                    jFrame.setLocationByPlatform(true);
                    JPanel jPanel = new JPanel(new BorderLayout());
                    JScrollPane jScrollPane = new JScrollPane(bufferedImage != null ? new ImageLabel(bufferedImage) : new JLabel("(no image data)", 0));
                    jScrollPane.setBorder(null);
                    jPanel.add(jScrollPane);
                    jFrame.setContentPane(jPanel);
                    jFrame.pack();
                    jFrame.setVisible(true);
                }
            });
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        catch (InvocationTargetException invocationTargetException) {
            if (invocationTargetException.getCause() instanceof RuntimeException) {
                throw (RuntimeException)invocationTargetException.getCause();
            }
            throw new RuntimeException(invocationTargetException);
        }
    }

    private static class ExitIfNoWindowPresentHandler
    extends WindowAdapter {
        private ExitIfNoWindowPresentHandler() {
        }

        @Override
        public void windowClosed(WindowEvent windowEvent) {
            Window[] windowArray = Window.getWindows();
            if (windowArray == null || windowArray.length == 0) {
                System.exit(0);
            }
        }
    }

    private static class ImageLabel
    extends JLabel {
        static final String ZOOM_IN = "zoom-in";
        static final String ZOOM_OUT = "zoom-out";
        static final String ZOOM_ACTUAL = "zoom-actual";
        static final String ZOOM_FIT = "zoom-fit";
        private BufferedImage image;
        Paint backgroundPaint;
        final Paint checkeredBG;
        final Color defaultBG;

        public ImageLabel(BufferedImage bufferedImage) {
            super((Icon)new BufferedImageIcon(bufferedImage));
            this.setOpaque(false);
            this.setCursor(Cursor.getPredefinedCursor(1));
            this.image = bufferedImage;
            this.checkeredBG = ImageLabel.createTexture();
            this.defaultBG = ImageLabel.getDefaultBackground(bufferedImage);
            this.backgroundPaint = this.defaultBG != null ? this.defaultBG : this.checkeredBG;
            this.setupActions();
            this.setComponentPopupMenu(this.createPopupMenu());
            this.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (mouseEvent.isPopupTrigger()) {
                        ImageLabel.this.getComponentPopupMenu().show(ImageLabel.this, mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            });
            this.setTransferHandler(new TransferHandler(){

                @Override
                public int getSourceActions(JComponent jComponent) {
                    return 1;
                }

                @Override
                protected Transferable createTransferable(JComponent jComponent) {
                    return new ImageTransferable(ImageLabel.this.image);
                }

                @Override
                public boolean importData(JComponent jComponent, Transferable transferable) {
                    if (this.canImport(jComponent, transferable.getTransferDataFlavors())) {
                        try {
                            Image image = (Image)transferable.getTransferData(DataFlavor.imageFlavor);
                            ImageLabel.this.image = ImageUtil.toBuffered((Image)image);
                            ImageLabel.this.setIcon((Icon)new BufferedImageIcon(ImageLabel.this.image));
                            return true;
                        }
                        catch (UnsupportedFlavorException | IOException exception) {
                            // empty catch block
                        }
                    }
                    return false;
                }

                @Override
                public boolean canImport(JComponent jComponent, DataFlavor[] dataFlavorArray) {
                    for (DataFlavor dataFlavor : dataFlavorArray) {
                        if (!dataFlavor.equals(DataFlavor.imageFlavor)) continue;
                        return true;
                    }
                    return false;
                }
            });
        }

        private void setupActions() {
            this.bindAction(new ZoomAction("Zoom in", 2.0), ZOOM_IN, KeyStroke.getKeyStroke('+'), KeyStroke.getKeyStroke(521, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), KeyStroke.getKeyStroke(107, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.bindAction(new ZoomAction("Zoom out", 0.5), ZOOM_OUT, KeyStroke.getKeyStroke('-'), KeyStroke.getKeyStroke(45, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), KeyStroke.getKeyStroke(109, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.bindAction(new ZoomAction("Zoom actual"), ZOOM_ACTUAL, KeyStroke.getKeyStroke('0'), KeyStroke.getKeyStroke(48, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.bindAction(new ZoomToFitAction("Zoom fit"), ZOOM_FIT, KeyStroke.getKeyStroke('9'), KeyStroke.getKeyStroke(57, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.bindAction(TransferHandler.getCopyAction(), (String)TransferHandler.getCopyAction().getValue("Name"), KeyStroke.getKeyStroke(67, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            this.bindAction(TransferHandler.getPasteAction(), (String)TransferHandler.getPasteAction().getValue("Name"), KeyStroke.getKeyStroke(86, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        private void bindAction(Action action, String string, KeyStroke ... keyStrokeArray) {
            for (KeyStroke keyStroke : keyStrokeArray) {
                this.getInputMap(2).put(keyStroke, string);
            }
            this.getActionMap().put(string, action);
        }

        private JPopupMenu createPopupMenu() {
            JPopupMenu jPopupMenu = new JPopupMenu();
            jPopupMenu.add(this.getActionMap().get(ZOOM_FIT));
            jPopupMenu.add(this.getActionMap().get(ZOOM_ACTUAL));
            jPopupMenu.add(this.getActionMap().get(ZOOM_IN));
            jPopupMenu.add(this.getActionMap().get(ZOOM_OUT));
            jPopupMenu.addSeparator();
            ButtonGroup buttonGroup = new ButtonGroup();
            JMenu jMenu = new JMenu("Background");
            jPopupMenu.add(jMenu);
            ChangeBackgroundAction changeBackgroundAction = new ChangeBackgroundAction("Checkered", this.checkeredBG);
            changeBackgroundAction.putValue("SwingSelectedKey", this.backgroundPaint == this.checkeredBG);
            this.addCheckBoxItem(changeBackgroundAction, jMenu, buttonGroup);
            jMenu.addSeparator();
            this.addCheckBoxItem(new ChangeBackgroundAction("White", Color.WHITE), jMenu, buttonGroup);
            this.addCheckBoxItem(new ChangeBackgroundAction("Light", Color.LIGHT_GRAY), jMenu, buttonGroup);
            this.addCheckBoxItem(new ChangeBackgroundAction("Gray", Color.GRAY), jMenu, buttonGroup);
            this.addCheckBoxItem(new ChangeBackgroundAction("Dark", Color.DARK_GRAY), jMenu, buttonGroup);
            this.addCheckBoxItem(new ChangeBackgroundAction("Black", Color.BLACK), jMenu, buttonGroup);
            jMenu.addSeparator();
            ChooseBackgroundAction chooseBackgroundAction = new ChooseBackgroundAction("Choose...", this.defaultBG != null ? this.defaultBG : new Color(0xFF6600));
            chooseBackgroundAction.putValue("SwingSelectedKey", this.backgroundPaint == this.defaultBG);
            this.addCheckBoxItem(chooseBackgroundAction, jMenu, buttonGroup);
            return jPopupMenu;
        }

        private void addCheckBoxItem(Action action, JMenu jMenu, ButtonGroup buttonGroup) {
            JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(action);
            buttonGroup.add(jCheckBoxMenuItem);
            jMenu.add(jCheckBoxMenuItem);
        }

        private static Color getDefaultBackground(BufferedImage bufferedImage) {
            IndexColorModel indexColorModel;
            int n;
            if (bufferedImage.getColorModel() instanceof IndexColorModel && (n = (indexColorModel = (IndexColorModel)bufferedImage.getColorModel()).getTransparentPixel()) >= 0) {
                return new Color(indexColorModel.getRGB(n), false);
            }
            return null;
        }

        private static Paint createTexture() {
            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
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
            return new TexturePaint(bufferedImage, new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.setPaint(this.backgroundPaint);
            graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());
            super.paintComponent(graphics);
        }

        private static class ImageTransferable
        implements Transferable {
            private final BufferedImage image;

            public ImageTransferable(BufferedImage bufferedImage) {
                this.image = bufferedImage;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
                return DataFlavor.imageFlavor.equals(dataFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
                if (this.isDataFlavorSupported(dataFlavor)) {
                    return this.image;
                }
                throw new UnsupportedFlavorException(dataFlavor);
            }
        }

        private class ZoomToFitAction
        extends ZoomAction {
            public ZoomToFitAction(String string) {
                super(string, -1.0);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Container container;
                JComponent jComponent = (JComponent)actionEvent.getSource();
                if (jComponent instanceof JMenuItem) {
                    container = (JPopupMenu)SwingUtilities.getAncestorOfClass(JPopupMenu.class, jComponent);
                    jComponent = (JComponent)((JPopupMenu)container).getInvoker();
                }
                container = SwingUtilities.getAncestorOfClass(JViewport.class, jComponent);
                double d = (double)container.getWidth() / (double)ImageLabel.this.image.getWidth();
                double d2 = (double)container.getHeight() / (double)ImageLabel.this.image.getHeight();
                double d3 = Math.min(d, d2);
                int n = Math.max(Math.min((int)((double)ImageLabel.this.image.getWidth() * d3), ImageLabel.this.image.getWidth() * 16), ImageLabel.this.image.getWidth() / 16);
                int n2 = Math.max(Math.min((int)((double)ImageLabel.this.image.getHeight() * d3), ImageLabel.this.image.getHeight() * 16), ImageLabel.this.image.getHeight() / 16);
                ImageLabel.this.setIcon((Icon)new BufferedImageIcon(ImageLabel.this.image, n, n2, d3 > 1.0));
            }
        }

        private class ZoomAction
        extends AbstractAction {
            private final double zoomFactor;

            public ZoomAction(String string, double d) {
                super(string);
                this.zoomFactor = d;
            }

            public ZoomAction(String string) {
                this(string, 0.0);
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (this.zoomFactor <= 0.0) {
                    ImageLabel.this.setIcon((Icon)new BufferedImageIcon(ImageLabel.this.image));
                } else {
                    Icon icon = ImageLabel.this.getIcon();
                    int n = Math.max(Math.min((int)((double)icon.getIconWidth() * this.zoomFactor), ImageLabel.this.image.getWidth() * 16), ImageLabel.this.image.getWidth() / 16);
                    int n2 = Math.max(Math.min((int)((double)icon.getIconHeight() * this.zoomFactor), ImageLabel.this.image.getHeight() * 16), ImageLabel.this.image.getHeight() / 16);
                    ImageLabel.this.setIcon((Icon)new BufferedImageIcon(ImageLabel.this.image, Math.max(n, 2), Math.max(n2, 2), n > ImageLabel.this.image.getWidth() || n2 > ImageLabel.this.image.getHeight()));
                }
            }
        }

        private class ChooseBackgroundAction
        extends ChangeBackgroundAction {
            public ChooseBackgroundAction(String string, Color color) {
                super(string, color);
                this.putValue("SmallIcon", new Icon(){

                    @Override
                    public void paintIcon(Component component, Graphics graphics, int n, int n2) {
                        Graphics graphics2 = graphics.create();
                        graphics2.setColor((Color)ChooseBackgroundAction.this.paint);
                        graphics2.fillRect(n, n2, 16, 16);
                        graphics2.dispose();
                    }

                    @Override
                    public int getIconWidth() {
                        return 16;
                    }

                    @Override
                    public int getIconHeight() {
                        return 16;
                    }
                });
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color color = JColorChooser.showDialog(ImageLabel.this, "Choose background", (Color)this.paint);
                if (color != null) {
                    this.paint = color;
                    super.actionPerformed(actionEvent);
                }
            }
        }

        private class ChangeBackgroundAction
        extends AbstractAction {
            protected Paint paint;

            public ChangeBackgroundAction(String string, Paint paint) {
                super(string);
                this.paint = paint;
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ImageLabel.this.backgroundPaint = this.paint;
                ImageLabel.this.repaint();
            }
        }
    }
}

