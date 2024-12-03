/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.blip;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import org.apache.poi.util.Units;

public abstract class Metafile
extends HSLFPictureData {
    @Internal
    protected Metafile(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    protected static byte[] compress(byte[] bytes, int offset, int length) {
        UnsynchronizedByteArrayOutputStream out = new UnsynchronizedByteArrayOutputStream();
        try (DeflaterOutputStream deflater = new DeflaterOutputStream((OutputStream)out);){
            deflater.write(bytes, offset, length);
        }
        catch (IOException ignored) {
            throw new AssertionError("Won't happen", ignored);
        }
        return out.toByteArray();
    }

    @Override
    public Dimension getImageDimension() {
        int prefixLen = 16 * this.getUIDInstanceCount();
        Header header = new Header();
        header.read(this.getRawData(), prefixLen);
        return new Dimension((int)Math.round(Units.toPoints((long)header.size.getWidth())), (int)Math.round(Units.toPoints((long)header.size.getHeight())));
    }

    public static class Header {
        private static final int RECORD_LENGTH = 34;
        private int wmfsize;
        private final Rectangle bounds = new Rectangle();
        private final Dimension size = new Dimension();
        private int zipsize;
        private int compression;
        private int filter = 254;

        public void read(byte[] data, int offset) {
            try (LittleEndianInputStream leis = new LittleEndianInputStream((InputStream)new UnsynchronizedByteArrayInputStream(data, offset, 34));){
                this.wmfsize = leis.readInt();
                int left = leis.readInt();
                int top = leis.readInt();
                int right = leis.readInt();
                int bottom = leis.readInt();
                this.bounds.setBounds(left, top, right - left, bottom - top);
                int width = leis.readInt();
                int height = leis.readInt();
                this.size.setSize(width, height);
                this.zipsize = leis.readInt();
                this.compression = leis.readUByte();
                this.filter = leis.readUByte();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public void write(OutputStream out) throws IOException {
            LittleEndianOutputStream leos = new LittleEndianOutputStream(out);
            leos.writeInt(this.wmfsize);
            leos.writeInt(this.bounds.x);
            leos.writeInt(this.bounds.y);
            leos.writeInt(this.bounds.x + this.bounds.width);
            leos.writeInt(this.bounds.y + this.bounds.height);
            leos.writeInt(this.size.width);
            leos.writeInt(this.size.height);
            leos.writeInt(this.zipsize);
            leos.writeByte(this.compression);
            leos.writeByte(this.filter);
        }

        void write(byte[] destination, int offset) {
            LittleEndian.putInt(destination, offset, this.wmfsize);
            LittleEndian.putInt(destination, offset += 4, this.bounds.x);
            LittleEndian.putInt(destination, offset += 4, this.bounds.y);
            LittleEndian.putInt(destination, offset += 4, this.bounds.x + this.bounds.width);
            LittleEndian.putInt(destination, offset += 4, this.bounds.y + this.bounds.height);
            LittleEndian.putInt(destination, offset += 4, this.size.width);
            LittleEndian.putInt(destination, offset += 4, this.size.height);
            LittleEndian.putInt(destination, offset += 4, this.zipsize);
            destination[offset += 4] = (byte)this.compression;
            destination[++offset] = (byte)this.filter;
        }

        public int getSize() {
            return 34;
        }

        public int getWmfSize() {
            return this.wmfsize;
        }

        protected void setWmfSize(int wmfSize) {
            this.wmfsize = wmfSize;
        }

        protected void setZipSize(int zipSize) {
            this.zipsize = zipSize;
        }

        public Rectangle getBounds() {
            return (Rectangle)this.bounds.clone();
        }

        protected void setBounds(Rectangle bounds) {
            this.bounds.setBounds(bounds);
        }

        protected void setDimension(Dimension size) {
            this.size.setSize(size);
        }
    }
}

