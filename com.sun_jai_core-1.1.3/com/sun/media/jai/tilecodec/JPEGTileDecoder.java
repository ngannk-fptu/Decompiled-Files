/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGDecodeParam
 *  com.sun.image.codec.jpeg.JPEGImageDecoder
 *  com.sun.image.codec.jpeg.JPEGQTable
 */
package com.sun.media.jai.tilecodec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGQTable;
import com.sun.media.jai.tilecodec.JaiI18N;
import com.sun.media.jai.tilecodec.TileCodecUtils;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RasterFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderImpl;
import javax.media.jai.util.ImagingListener;

public class JPEGTileDecoder
extends TileDecoderImpl {
    private TileCodecDescriptor tcd = TileCodecUtils.getTileCodecDescriptor("tileDecoder", "jpeg");

    public JPEGTileDecoder(InputStream input, TileCodecParameterList param) {
        super("jpeg", input, param);
    }

    public Raster decode() throws IOException {
        if (!this.tcd.includesLocationInfo()) {
            throw new IllegalArgumentException(JaiI18N.getString("JPEGTileDecoder0"));
        }
        return this.decode(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Raster decode(Point location) throws IOException {
        SampleModel sm = null;
        byte[] data = null;
        ObjectInputStream ois = new ObjectInputStream(this.inputStream);
        try {
            this.paramList.setParameter("quality", ois.readFloat());
            this.paramList.setParameter("qualitySet", ois.readBoolean());
            sm = TileCodecUtils.deserializeSampleModel(ois.readObject());
            location = (Point)ois.readObject();
            data = (byte[])ois.readObject();
        }
        catch (ClassNotFoundException e) {
            ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
            listener.errorOccurred(JaiI18N.getString("ClassNotFound"), e, this, false);
            Raster raster = null;
            return raster;
        }
        finally {
            ois.close();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder((InputStream)bais);
        Raster ras = decoder.decodeAsRaster().createTranslatedChild(location.x, location.y);
        this.extractParameters(decoder.getJPEGDecodeParam(), ras.getSampleModel().getNumBands());
        if (sm != null) {
            int minX = ras.getMinX();
            int minY = ras.getMinY();
            int h = ras.getHeight();
            int w = ras.getWidth();
            double[] buf = ras.getPixels(minX, minY, w, h, (double[])null);
            ras = RasterFactory.createWritableRaster(sm, new Point(minX, minY));
            ((WritableRaster)ras).setPixels(minX, minY, w, h, buf);
        }
        return ras;
    }

    private void extractParameters(JPEGDecodeParam jdp, int bandNum) {
        int i;
        int[] horizontalSubsampling = new int[bandNum];
        for (int i2 = 0; i2 < bandNum; ++i2) {
            horizontalSubsampling[i2] = jdp.getHorizontalSubsampling(i2);
        }
        this.paramList.setParameter("horizontalSubsampling", horizontalSubsampling);
        int[] verticalSubsampling = new int[bandNum];
        for (i = 0; i < bandNum; ++i) {
            verticalSubsampling[i] = jdp.getVerticalSubsampling(i);
        }
        this.paramList.setParameter("verticalSubsampling", verticalSubsampling);
        if (!this.paramList.getBooleanParameter("qualitySet")) {
            for (i = 0; i < 4; ++i) {
                JPEGQTable table = jdp.getQTable(i);
                this.paramList.setParameter("quantizationTable" + i, table == null ? null : table.getTable());
            }
        } else {
            ParameterListDescriptor pld = this.paramList.getParameterListDescriptor();
            for (int i3 = 0; i3 < 4; ++i3) {
                this.paramList.setParameter("quantizationTable" + i3, pld.getParamDefaultValue("quantizationTable" + i3));
            }
        }
        int[] quanTableMapping = new int[bandNum];
        for (int i4 = 0; i4 < bandNum; ++i4) {
            quanTableMapping[i4] = jdp.getQTableComponentMapping(i4);
        }
        this.paramList.setParameter("quantizationTableMapping", quanTableMapping);
        this.paramList.setParameter("writeTableInfo", jdp.isTableInfoValid());
        this.paramList.setParameter("writeImageInfo", jdp.isImageInfoValid());
        this.paramList.setParameter("restartInterval", jdp.getRestartInterval());
        this.paramList.setParameter("writeJFIFHeader", jdp.getMarker(224));
    }
}

