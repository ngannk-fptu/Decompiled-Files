/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import org.apache.batik.ext.awt.image.spi.AbstractRegistryEntry;
import org.apache.batik.ext.awt.image.spi.StreamRegistryEntry;

public abstract class MagicNumberRegistryEntry
extends AbstractRegistryEntry
implements StreamRegistryEntry {
    public static final float PRIORITY = 1000.0f;
    MagicNumber[] magicNumbers;

    public MagicNumberRegistryEntry(String name, float priority, String ext, String mimeType, int offset, byte[] magicNumber) {
        super(name, priority, ext, mimeType);
        this.magicNumbers = new MagicNumber[1];
        this.magicNumbers[0] = new MagicNumber(offset, magicNumber);
    }

    public MagicNumberRegistryEntry(String name, String ext, String mimeType, int offset, byte[] magicNumber) {
        this(name, 1000.0f, ext, mimeType, offset, magicNumber);
    }

    public MagicNumberRegistryEntry(String name, float priority, String ext, String mimeType, MagicNumber[] magicNumbers) {
        super(name, priority, ext, mimeType);
        this.magicNumbers = magicNumbers;
    }

    public MagicNumberRegistryEntry(String name, String ext, String mimeType, MagicNumber[] magicNumbers) {
        this(name, 1000.0f, ext, mimeType, magicNumbers);
    }

    public MagicNumberRegistryEntry(String name, float priority, String[] exts, String[] mimeTypes, int offset, byte[] magicNumber) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = new MagicNumber[1];
        this.magicNumbers[0] = new MagicNumber(offset, magicNumber);
    }

    public MagicNumberRegistryEntry(String name, String[] exts, String[] mimeTypes, int offset, byte[] magicNumbers) {
        this(name, 1000.0f, exts, mimeTypes, offset, magicNumbers);
    }

    public MagicNumberRegistryEntry(String name, float priority, String[] exts, String[] mimeTypes, MagicNumber[] magicNumbers) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }

    public MagicNumberRegistryEntry(String name, String[] exts, String[] mimeTypes, MagicNumber[] magicNumbers) {
        this(name, 1000.0f, exts, mimeTypes, magicNumbers);
    }

    public MagicNumberRegistryEntry(String name, String[] exts, String[] mimeTypes, MagicNumber[] magicNumbers, float priority) {
        super(name, priority, exts, mimeTypes);
        this.magicNumbers = magicNumbers;
    }

    @Override
    public int getReadlimit() {
        int maxbuf = 0;
        for (MagicNumber magicNumber : this.magicNumbers) {
            int req = magicNumber.getReadlimit();
            if (req <= maxbuf) continue;
            maxbuf = req;
        }
        return maxbuf;
    }

    @Override
    public boolean isCompatibleStream(InputStream is) throws StreamCorruptedException {
        for (MagicNumber magicNumber : this.magicNumbers) {
            if (!magicNumber.isMatch(is)) continue;
            return true;
        }
        return false;
    }

    public static class MagicNumber {
        int offset;
        byte[] magicNumber;
        byte[] buffer;

        public MagicNumber(int offset, byte[] magicNumber) {
            this.offset = offset;
            this.magicNumber = (byte[])magicNumber.clone();
            this.buffer = new byte[magicNumber.length];
        }

        int getReadlimit() {
            return this.offset + this.magicNumber.length;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean isMatch(InputStream is) throws StreamCorruptedException {
            is.mark(this.getReadlimit());
            try {
                int idx;
                int rn;
                for (idx = 0; idx < this.offset; idx += rn) {
                    rn = (int)is.skip(this.offset - idx);
                    if (rn != -1) continue;
                    boolean bl = false;
                    return bl;
                }
                for (idx = 0; idx < this.buffer.length; idx += rn) {
                    rn = is.read(this.buffer, idx, this.buffer.length - idx);
                    if (rn != -1) continue;
                    boolean bl = false;
                    return bl;
                }
                for (int i = 0; i < this.magicNumber.length; ++i) {
                    if (this.magicNumber[i] == this.buffer[i]) continue;
                    boolean bl = false;
                    return bl;
                }
            }
            catch (IOException ioe) {
                boolean bl = false;
                return bl;
            }
            finally {
                try {
                    is.reset();
                }
                catch (IOException ioe) {
                    throw new StreamCorruptedException(ioe.getMessage());
                }
            }
            return true;
        }
    }
}

