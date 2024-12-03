/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;

interface PnmWriter {
    public void writeImage(BufferedImage var1, OutputStream var2, Map<String, Object> var3) throws ImageWriteException, IOException;
}

