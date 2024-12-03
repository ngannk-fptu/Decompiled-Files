/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.commons.imaging.common.BinaryOutputStream;

interface BmpWriter {
    public int getPaletteSize();

    public int getBitsPerPixel();

    public void writePalette(BinaryOutputStream var1) throws IOException;

    public byte[] getImageData(BufferedImage var1);
}

