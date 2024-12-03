/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry$MagicNumber
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import org.apache.batik.ext.awt.image.codec.imageio.AbstractImageIORegistryEntry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;

public class ImageIOJPEGRegistryEntry
extends AbstractImageIORegistryEntry {
    static final byte[] sigJPEG = new byte[]{-1, -40, -1};
    static final String[] exts = new String[]{"jpeg", "jpg"};
    static final String[] mimeTypes = new String[]{"image/jpeg", "image/jpg"};
    static final MagicNumberRegistryEntry.MagicNumber[] magicNumbers = new MagicNumberRegistryEntry.MagicNumber[]{new MagicNumberRegistryEntry.MagicNumber(0, sigJPEG)};

    public ImageIOJPEGRegistryEntry() {
        super("JPEG", exts, mimeTypes, magicNumbers);
    }
}

