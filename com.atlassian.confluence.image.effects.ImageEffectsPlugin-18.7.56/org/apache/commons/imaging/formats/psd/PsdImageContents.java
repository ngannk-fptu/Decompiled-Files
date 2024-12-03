/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.formats.psd.PsdHeaderInfo;

public class PsdImageContents {
    private static final Logger LOGGER = Logger.getLogger(PsdImageContents.class.getName());
    public final PsdHeaderInfo header;
    public final int ColorModeDataLength;
    public final int ImageResourcesLength;
    public final int LayerAndMaskDataLength;
    public final int Compression;

    public PsdImageContents(PsdHeaderInfo header, int ColorModeDataLength, int ImageResourcesLength, int LayerAndMaskDataLength, int Compression) {
        this.header = header;
        this.ColorModeDataLength = ColorModeDataLength;
        this.ImageResourcesLength = ImageResourcesLength;
        this.LayerAndMaskDataLength = LayerAndMaskDataLength;
        this.Compression = Compression;
    }

    public void dump() {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);){
            this.dump(pw);
            pw.flush();
            sw.flush();
            LOGGER.fine(sw.toString());
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("");
        pw.println("ImageContents");
        pw.println("Compression: " + this.Compression + " (" + Integer.toHexString(this.Compression) + ")");
        pw.println("ColorModeDataLength: " + this.ColorModeDataLength + " (" + Integer.toHexString(this.ColorModeDataLength) + ")");
        pw.println("ImageResourcesLength: " + this.ImageResourcesLength + " (" + Integer.toHexString(this.ImageResourcesLength) + ")");
        pw.println("LayerAndMaskDataLength: " + this.LayerAndMaskDataLength + " (" + Integer.toHexString(this.LayerAndMaskDataLength) + ")");
        pw.println("");
        pw.flush();
    }
}

