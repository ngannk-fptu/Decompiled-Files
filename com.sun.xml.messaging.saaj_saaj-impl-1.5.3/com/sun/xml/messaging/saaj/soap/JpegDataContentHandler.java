/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;

public class JpegDataContentHandler
extends Component
implements DataContentHandler {
    public static final String STR_SRC = "java.awt.Image";

    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[1];
        try {
            flavors[0] = new ActivationDataFlavor(Class.forName(STR_SRC), "image/jpeg", "JPEG");
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return flavors;
    }

    public Object getTransferData(DataFlavor df, DataSource ds) {
        if (df.getMimeType().startsWith("image/jpeg") && df.getRepresentationClass().getName().equals(STR_SRC)) {
            InputStream inputStream = null;
            BufferedImage jpegLoadImage = null;
            try {
                inputStream = ds.getInputStream();
                jpegLoadImage = ImageIO.read(inputStream);
            }
            catch (Exception e) {
                System.out.println(e);
            }
            return jpegLoadImage;
        }
        return null;
    }

    public Object getContent(DataSource ds) {
        InputStream inputStream = null;
        BufferedImage jpegLoadImage = null;
        try {
            inputStream = ds.getInputStream();
            jpegLoadImage = ImageIO.read(inputStream);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return jpegLoadImage;
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (!mimeType.equals("image/jpeg")) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for ImageContentHandler");
        }
        if (obj == null) {
            throw new IOException("Null object for ImageContentHandler");
        }
        try {
            BufferedImage bufImage = null;
            if (obj instanceof BufferedImage) {
                bufImage = (BufferedImage)obj;
            } else {
                Image img = (Image)obj;
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(img, 0);
                tracker.waitForAll();
                if (tracker.isErrorAny()) {
                    throw new IOException("Error while loading image");
                }
                bufImage = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
                Graphics2D g = bufImage.createGraphics();
                g.drawImage(img, 0, 0, null);
            }
            ImageIO.write((RenderedImage)bufImage, "jpeg", os);
        }
        catch (Exception ex) {
            throw new IOException("Unable to run the JPEG Encoder on a stream " + ex.getMessage());
        }
    }
}

