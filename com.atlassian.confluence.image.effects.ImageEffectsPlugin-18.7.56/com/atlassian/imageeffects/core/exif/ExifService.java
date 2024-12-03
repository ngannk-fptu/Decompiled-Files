/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core.exif;

import com.atlassian.imageeffects.core.exif.ExifException;
import com.atlassian.imageeffects.core.exif.ExifInfo;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface ExifService {
    public ExifInfo readExifInfo(InputStream var1) throws ExifException;

    public BufferedImage rotate(InputStream var1, ExifInfo var2) throws ExifException;

    public BufferedImage rotate(BufferedImage var1, ExifInfo var2) throws ExifException;
}

