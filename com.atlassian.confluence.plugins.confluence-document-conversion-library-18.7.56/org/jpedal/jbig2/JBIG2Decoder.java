/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.pageinformation.PageInformationSegment;

public class JBIG2Decoder {
    private JBIG2StreamDecoder streamDecoder = new JBIG2StreamDecoder();

    public void setGlobalData(byte[] byArray) throws IOException, JBIG2Exception {
        this.streamDecoder.setGlobalData(byArray);
    }

    public void decodeJBIG2(File file) throws IOException, JBIG2Exception {
        this.decodeJBIG2(file.getAbsolutePath());
    }

    public void decodeJBIG2(String string) throws IOException, JBIG2Exception {
        this.decodeJBIG2(new FileInputStream(string));
    }

    public void decodeJBIG2(InputStream inputStream) throws IOException, JBIG2Exception {
        int n = inputStream.available();
        byte[] byArray = new byte[n];
        inputStream.read(byArray);
        this.decodeJBIG2(byArray);
    }

    public void decodeJBIG2(byte[] byArray) throws IOException, JBIG2Exception {
        this.streamDecoder.decodeJBIG2(byArray);
    }

    public BufferedImage getPageAsBufferedImage(int n) {
        JBIG2Bitmap jBIG2Bitmap = this.getPageAsJBIG2Bitmap(n);
        byte[] byArray = jBIG2Bitmap.getData(true);
        if (byArray == null) {
            return null;
        }
        int n2 = byArray.length;
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, 0, byArray2, 0, n2);
        int n3 = jBIG2Bitmap.getWidth();
        int n4 = jBIG2Bitmap.getHeight();
        DataBufferByte dataBufferByte = new DataBufferByte(byArray2, byArray2.length);
        WritableRaster writableRaster = Raster.createPackedRaster(dataBufferByte, n3, n4, 1, null);
        BufferedImage bufferedImage = new BufferedImage(n3, n4, 12);
        bufferedImage.setData(writableRaster);
        return bufferedImage;
    }

    public boolean isNumberOfPagesKnown() {
        return this.streamDecoder.isNumberOfPagesKnown();
    }

    public int getNumberOfPages() {
        int n = this.streamDecoder.getNumberOfPages();
        if (this.streamDecoder.isNumberOfPagesKnown() && n != 0) {
            return n;
        }
        int n2 = 0;
        List list = this.getAllSegments();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Segment segment = (Segment)iterator.next();
            if (segment.getSegmentHeader().getSegmentType() != 48) continue;
            ++n2;
        }
        return n2;
    }

    public List getAllSegments() {
        return this.streamDecoder.getAllSegments();
    }

    public PageInformationSegment findPageSegement(int n) {
        return this.streamDecoder.findPageSegement(n);
    }

    public Segment findSegment(int n) {
        return this.streamDecoder.findSegment(n);
    }

    public JBIG2Bitmap getPageAsJBIG2Bitmap(int n) {
        return this.findPageSegement(n).getPageBitmap();
    }

    public boolean isRandomAccessOrganisationUsed() {
        return this.streamDecoder.isRandomAccessOrganisationUsed();
    }
}

