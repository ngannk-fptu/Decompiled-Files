/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.image;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import org.apache.axis.components.image.ImageIO;

public class MerlinIO
extends Component
implements ImageIO {
    public void saveImage(String mimeType, Image image, OutputStream os) throws Exception {
        ImageWriter writer = null;
        Iterator<ImageWriter> iter = javax.imageio.ImageIO.getImageWritersByMIMEType(mimeType);
        if (iter.hasNext()) {
            writer = iter.next();
        }
        writer.setOutput(javax.imageio.ImageIO.createImageOutputStream(os));
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
        writer.write(new IIOImage(rendImage, null, null));
        writer.dispose();
    }

    public Image loadImage(InputStream in) throws Exception {
        return javax.imageio.ImageIO.read(in);
    }
}

