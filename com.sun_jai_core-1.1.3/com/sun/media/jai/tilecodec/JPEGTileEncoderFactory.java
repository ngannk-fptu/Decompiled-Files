/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.JPEGTileEncoder;
import com.sun.media.jai.tilecodec.JaiI18N;
import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCollection;
import javax.media.jai.remote.NegotiableNumericRange;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;

public class JPEGTileEncoderFactory
implements TileEncoderFactory {
    static /* synthetic */ Class class$com$sun$media$jai$tilecodec$JPEGTileEncoderFactory;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableNumericRange;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableCollection;

    public TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel) {
        if (output == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoder0"));
        }
        int nbands = sampleModel.getNumBands();
        if (nbands != 1 && nbands != 3 && nbands != 4) {
            throw new IllegalArgumentException(JaiI18N.getString("JPEGTileEncoder0"));
        }
        if (sampleModel.getDataType() != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("JPEGTileEncoder1"));
        }
        return new JPEGTileEncoder(output, paramList);
    }

    public NegotiableCapability getEncodeCapability() {
        Vector<Class> generators = new Vector<Class>();
        generators.add(class$com$sun$media$jai$tilecodec$JPEGTileEncoderFactory == null ? (class$com$sun$media$jai$tilecodec$JPEGTileEncoderFactory = JPEGTileEncoderFactory.class$("com.sun.media.jai.tilecodec.JPEGTileEncoderFactory")) : class$com$sun$media$jai$tilecodec$JPEGTileEncoderFactory);
        ParameterListDescriptor jpegPld = JAI.getDefaultInstance().getOperationRegistry().getDescriptor("tileEncoder", "jpeg").getParameterListDescriptor("tileEncoder");
        Class[] paramClasses = new Class[]{class$javax$media$jai$remote$NegotiableNumericRange == null ? (class$javax$media$jai$remote$NegotiableNumericRange = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableNumericRange")) : class$javax$media$jai$remote$NegotiableNumericRange, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableNumericRange == null ? (class$javax$media$jai$remote$NegotiableNumericRange = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableNumericRange")) : class$javax$media$jai$remote$NegotiableNumericRange, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileEncoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection};
        String[] paramNames = new String[]{"quality", "qualitySet", "restartInterval", "writeImageInfo", "writeTableInfo", "writeJFIFHeader"};
        Vector<Boolean> v = new Vector<Boolean>();
        v.add(new Boolean(true));
        v.add(new Boolean(false));
        NegotiableCollection negCollection = new NegotiableCollection(v);
        NegotiableNumericRange nnr1 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[0]));
        NegotiableNumericRange nnr2 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[2]));
        Object[] defaults = new Object[]{nnr1, negCollection, nnr2, negCollection, negCollection, negCollection};
        NegotiableCapability encodeCap = new NegotiableCapability("tileCodec", "jpeg", generators, new ParameterListDescriptorImpl(null, paramNames, paramClasses, defaults, null), false);
        encodeCap.setParameter(paramNames[0], nnr1);
        encodeCap.setParameter(paramNames[1], negCollection);
        encodeCap.setParameter(paramNames[2], nnr2);
        encodeCap.setParameter(paramNames[3], negCollection);
        encodeCap.setParameter(paramNames[4], negCollection);
        encodeCap.setParameter(paramNames[5], negCollection);
        return encodeCap;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

