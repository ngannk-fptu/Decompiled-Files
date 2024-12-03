/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.awt.image.codec.JPEGImageEncoderImpl
 */
package org.apache.axis.components.image;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axis.components.image.ImageIO;
import org.apache.axis.utils.IOUtils;
import org.apache.axis.utils.Messages;
import sun.awt.image.codec.JPEGImageEncoderImpl;

public class JDK13IO
extends Component
implements ImageIO {
    public void saveImage(String mimeType, Image image, OutputStream os) throws Exception {
        BufferedImage rendImage = null;
        if (image instanceof BufferedImage) {
            rendImage = (BufferedImage)image;
        } else {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(image, 0);
            tracker.waitForAll();
            rendImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
            Graphics2D g = rendImage.createGraphics();
            g.drawImage(image, 0, 0, null);
        }
        if (!"image/jpeg".equals(mimeType)) {
            throw new IOException(Messages.getMessage("jpegOnly", mimeType));
        }
        JPEGImageEncoderImpl j = new JPEGImageEncoderImpl(os);
        j.encode(rendImage);
    }

    public Image loadImage(InputStream in) throws Exception {
        if (in.available() <= 0) {
            return null;
        }
        byte[] bytes = new byte[in.available()];
        IOUtils.readFully(in, bytes);
        return Toolkit.getDefaultToolkit().createImage(bytes);
    }
}

