/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.image;

import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;

public interface ImageIO {
    public void saveImage(String var1, Image var2, OutputStream var3) throws Exception;

    public Image loadImage(InputStream var1) throws Exception;
}

