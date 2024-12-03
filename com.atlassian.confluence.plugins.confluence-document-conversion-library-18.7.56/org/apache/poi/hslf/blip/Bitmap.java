/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hslf.blip;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;

public abstract class Bitmap
extends HSLFPictureData {
    @Deprecated
    @Removal(version="5.3")
    public Bitmap() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    protected Bitmap(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    @Override
    public byte[] getData() {
        byte[] rawdata = this.getRawData();
        int prefixLen = 16 * this.getUIDInstanceCount() + 1;
        return IOUtils.safelyClone(rawdata, prefixLen, rawdata.length - prefixLen, rawdata.length);
    }

    @Override
    protected byte[] formatImageForSlideshow(byte[] data) {
        byte[] checksum = Bitmap.getChecksum(data);
        byte[] rawData = new byte[checksum.length * this.getUIDInstanceCount() + 1 + data.length];
        int offset = 0;
        System.arraycopy(checksum, 0, rawData, offset, checksum.length);
        offset += checksum.length;
        if (this.getUIDInstanceCount() == 2) {
            System.arraycopy(checksum, 0, rawData, offset, checksum.length);
            offset += checksum.length;
        }
        System.arraycopy(data, 0, rawData, ++offset, data.length);
        return rawData;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Dimension getImageDimension() {
        try (UnsynchronizedByteArrayInputStream is = new UnsynchronizedByteArrayInputStream(this.getData());){
            BufferedImage bi = ImageIO.read((InputStream)is);
            Dimension dimension = new Dimension((int)Units.pixelToPoints(bi.getWidth()), (int)Units.pixelToPoints(bi.getHeight()));
            return dimension;
        }
        catch (IOException e) {
            return new Dimension(200, 200);
        }
    }
}

