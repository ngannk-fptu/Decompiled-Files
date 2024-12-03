/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jimi.core.Jimi
 */
package org.apache.axis.components.image;

import com.sun.jimi.core.Jimi;
import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axis.components.image.ImageIO;

public class JimiIO
implements ImageIO {
    public void saveImage(String id, Image image, OutputStream os) throws Exception {
        Jimi.putImage((String)id, (Image)image, (OutputStream)os);
    }

    public Image loadImage(InputStream in) throws Exception {
        return Jimi.getImage((InputStream)in);
    }
}

