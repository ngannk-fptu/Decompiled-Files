/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGEncodeParam
 *  com.sun.image.codec.jpeg.JPEGImageEncoder
 *  com.sun.image.codec.jpeg.JPEGQTable
 *  sun.awt.image.codec.JPEGParam
 */
package com.sun.media.jai.tilecodec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGQTable;
import com.sun.media.jai.tilecodec.JaiI18N;
import com.sun.media.jai.tilecodec.TileCodecUtils;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoderImpl;
import sun.awt.image.codec.JPEGParam;

public class JPEGTileEncoder
extends TileEncoderImpl {
    private TileCodecDescriptor tcd = TileCodecUtils.getTileCodecDescriptor("tileEncoder", "jpeg");

    public JPEGTileEncoder(OutputStream output, TileCodecParameterList param) {
        super("jpeg", output, param);
    }

    public void encode(Raster ras) throws IOException {
        if (ras == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoder1"));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SampleModel sm = ras.getSampleModel();
        JPEGEncodeParam j2dEP = this.convertToJ2DJPEGEncodeParam(this.paramList, sm);
        ((JPEGParam)j2dEP).setWidth(ras.getWidth());
        ((JPEGParam)j2dEP).setHeight(ras.getHeight());
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder((OutputStream)baos, (JPEGEncodeParam)j2dEP);
        encoder.encode(ras);
        byte[] data = baos.toByteArray();
        ObjectOutputStream oos = new ObjectOutputStream(this.outputStream);
        oos.writeFloat(this.paramList.getFloatParameter("quality"));
        oos.writeBoolean(this.paramList.getBooleanParameter("qualitySet"));
        oos.writeObject(TileCodecUtils.serializeSampleModel(sm));
        Point location = new Point(ras.getMinX(), ras.getMinY());
        oos.writeObject(location);
        oos.writeObject(data);
        oos.close();
    }

    private JPEGEncodeParam convertToJ2DJPEGEncodeParam(TileCodecParameterList paramList, SampleModel sm) {
        if (sm == null) {
            return null;
        }
        int nbands = sm.getNumBands();
        JPEGParam j2dJP = this.createDefaultJ2DJPEGEncodeParam(nbands);
        int[] hSubSamp = (int[])paramList.getObjectParameter("horizontalSubsampling");
        int[] vSubSamp = (int[])paramList.getObjectParameter("verticalSubsampling");
        int[] qTabSlot = (int[])paramList.getObjectParameter("quantizationTableMapping");
        for (int i = 0; i < nbands; ++i) {
            j2dJP.setHorizontalSubsampling(i, hSubSamp[i]);
            j2dJP.setVerticalSubsampling(i, vSubSamp[i]);
            int[] qTab = (int[])paramList.getObjectParameter("quantizationTable" + i);
            if (qTab == null || !qTab.equals(ParameterListDescriptor.NO_PARAMETER_DEFAULT)) continue;
            j2dJP.setQTableComponentMapping(i, qTabSlot[i]);
            j2dJP.setQTable(qTabSlot[i], new JPEGQTable(qTab));
        }
        if (paramList.getBooleanParameter("qualitySet")) {
            float quality = paramList.getFloatParameter("quality");
            j2dJP.setQuality(quality, true);
        }
        int rInt = paramList.getIntParameter("restartInterval");
        j2dJP.setRestartInterval(rInt);
        j2dJP.setImageInfoValid(paramList.getBooleanParameter("writeImageInfo"));
        j2dJP.setTableInfoValid(paramList.getBooleanParameter("writeTableInfo"));
        if (paramList.getBooleanParameter("writeJFIFHeader")) {
            j2dJP.setMarkerData(224, (byte[][])null);
        }
        return j2dJP;
    }

    private JPEGParam createDefaultJ2DJPEGEncodeParam(int nbands) {
        if (nbands == 1) {
            return new JPEGParam(1, 1);
        }
        if (nbands == 3) {
            return new JPEGParam(3, 3);
        }
        if (nbands == 4) {
            return new JPEGParam(4, 4);
        }
        return null;
    }
}

