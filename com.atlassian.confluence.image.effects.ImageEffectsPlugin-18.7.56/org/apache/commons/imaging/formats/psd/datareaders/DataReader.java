/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd.datareaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.formats.psd.PsdImageContents;

public interface DataReader {
    public void readData(InputStream var1, BufferedImage var2, PsdImageContents var3, BinaryFileParser var4) throws ImageReadException, IOException;
}

