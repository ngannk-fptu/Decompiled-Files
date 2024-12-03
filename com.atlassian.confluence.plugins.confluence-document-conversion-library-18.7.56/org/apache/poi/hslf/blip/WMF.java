/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.blip;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.blip.Metafile;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.sl.image.ImageHeaderWMF;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;

public final class WMF
extends Metafile {
    @Deprecated
    @Removal(version="5.3")
    public WMF() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    public WMF(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] getData() {
        byte[] rawdata = this.getRawData();
        try (UnsynchronizedByteArrayInputStream is = new UnsynchronizedByteArrayInputStream(rawdata);){
            Metafile.Header header = new Metafile.Header();
            header.read(rawdata, 16 * this.getUIDInstanceCount());
            long skipLen = (long)header.getSize() + 16L * (long)this.getUIDInstanceCount();
            long skipped = IOUtils.skipFully((InputStream)is, skipLen);
            assert (skipped == skipLen);
            ImageHeaderWMF aldus = new ImageHeaderWMF(header.getBounds());
            UnsynchronizedByteArrayOutputStream out = new UnsynchronizedByteArrayOutputStream();
            aldus.write((OutputStream)out);
            try (InflaterInputStream inflater = new InflaterInputStream((InputStream)is);){
                IOUtils.copy((InputStream)inflater, (OutputStream)out);
            }
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    @Override
    protected byte[] formatImageForSlideshow(byte[] data) {
        int pos = 0;
        ImageHeaderWMF nHeader = new ImageHeaderWMF(data, pos);
        byte[] compressed = WMF.compress(data, pos += nHeader.getLength(), data.length - pos);
        Metafile.Header header = new Metafile.Header();
        header.setWmfSize(data.length - nHeader.getLength());
        header.setBounds(nHeader.getBounds());
        Dimension nDim = nHeader.getSize();
        header.setDimension(new Dimension(Units.toEMU(nDim.getWidth()), Units.toEMU(nDim.getHeight())));
        header.setZipSize(compressed.length);
        byte[] checksum = WMF.getChecksum(data);
        byte[] rawData = new byte[checksum.length * this.getUIDInstanceCount() + header.getSize() + compressed.length];
        int offset = 0;
        System.arraycopy(checksum, 0, rawData, offset, checksum.length);
        offset += checksum.length;
        if (this.getUIDInstanceCount() == 2) {
            System.arraycopy(checksum, 0, rawData, offset, checksum.length);
            offset += checksum.length;
        }
        header.write(rawData, offset);
        System.arraycopy(compressed, 0, rawData, offset += header.getSize(), compressed.length);
        return rawData;
    }

    @Override
    public PictureData.PictureType getType() {
        return PictureData.PictureType.WMF;
    }

    @Override
    public int getSignature() {
        return this.getUIDInstanceCount() == 1 ? 8544 : 8560;
    }

    @Override
    public void setSignature(int signature) {
        switch (signature) {
            case 8544: {
                this.setUIDInstanceCount(1);
                break;
            }
            case 8560: {
                this.setUIDInstanceCount(2);
                break;
            }
            default: {
                throw new IllegalArgumentException(signature + " is not a valid instance/signature value for WMF");
            }
        }
    }
}

