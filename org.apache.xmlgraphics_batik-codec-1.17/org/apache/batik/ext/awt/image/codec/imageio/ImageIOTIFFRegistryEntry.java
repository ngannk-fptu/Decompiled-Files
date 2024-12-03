/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry$MagicNumber
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import org.apache.batik.ext.awt.image.codec.imageio.AbstractImageIORegistryEntry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;

public class ImageIOTIFFRegistryEntry
extends AbstractImageIORegistryEntry {
    static final byte[] sig1 = new byte[]{73, 73, 42, 0};
    static final byte[] sig2 = new byte[]{77, 77, 0, 42};
    static MagicNumberRegistryEntry.MagicNumber[] magicNumbers = new MagicNumberRegistryEntry.MagicNumber[]{new MagicNumberRegistryEntry.MagicNumber(0, sig1), new MagicNumberRegistryEntry.MagicNumber(0, sig2)};
    static final String[] exts = new String[]{"tiff", "tif"};
    static final String[] mimeTypes = new String[]{"image/tiff", "image/tif"};

    public ImageIOTIFFRegistryEntry() {
        super("TIFF", exts, mimeTypes, magicNumbers);
    }
}

