/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 */
package com.sun.xml.ws.encoding;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageDataContentHandler
extends Component
implements DataContentHandler {
    private static final Logger log = Logger.getLogger(ImageDataContentHandler.class.getName());
    private final DataFlavor[] flavor;

    public ImageDataContentHandler() {
        String[] mimeTypes = ImageIO.getReaderMIMETypes();
        this.flavor = new DataFlavor[mimeTypes.length];
        for (int i = 0; i < mimeTypes.length; ++i) {
            this.flavor[i] = new ActivationDataFlavor(Image.class, mimeTypes[i], "Image");
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(this.flavor, this.flavor.length);
    }

    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        for (DataFlavor aFlavor : this.flavor) {
            if (!aFlavor.equals(df)) continue;
            return this.getContent(ds);
        }
        return null;
    }

    public Object getContent(DataSource ds) throws IOException {
        return ImageIO.read(new BufferedInputStream(ds.getInputStream()));
    }

    public void writeTo(Object obj, String type, OutputStream os) throws IOException {
        try {
            BufferedImage bufImage;
            if (obj instanceof BufferedImage) {
                bufImage = (BufferedImage)obj;
            } else if (obj instanceof Image) {
                bufImage = this.render((Image)obj);
            } else {
                throw new IOException("ImageDataContentHandler requires Image object, was given object of type " + obj.getClass().toString());
            }
            ImageWriter writer = null;
            Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(type);
            if (i.hasNext()) {
                writer = i.next();
            }
            if (writer == null) {
                throw new IOException("Unsupported mime type:" + type);
            }
            ImageOutputStream stream = ImageIO.createImageOutputStream(os);
            writer.setOutput(stream);
            writer.write(bufImage);
            writer.dispose();
            stream.close();
        }
        catch (Exception e) {
            throw new IOException("Unable to encode the image to a stream " + e.getMessage());
        }
    }

    private BufferedImage render(Image img) throws InterruptedException {
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(img, 0);
        tracker.waitForAll();
        BufferedImage bufImage = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
        Graphics2D g = bufImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bufImage;
    }
}

