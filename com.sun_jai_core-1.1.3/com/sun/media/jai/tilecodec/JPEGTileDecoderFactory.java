/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.JPEGTileDecoder;
import com.sun.media.jai.tilecodec.JaiI18N;
import java.io.InputStream;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCollection;
import javax.media.jai.remote.NegotiableNumericRange;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;

public class JPEGTileDecoderFactory
implements TileDecoderFactory {
    static /* synthetic */ Class class$com$sun$media$jai$tilecodec$JPEGTileDecoderFactory;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableNumericRange;
    static /* synthetic */ Class class$javax$media$jai$remote$NegotiableCollection;

    public TileDecoder createDecoder(InputStream input, TileCodecParameterList param) {
        if (input == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));
        }
        return new JPEGTileDecoder(input, param);
    }

    public NegotiableCapability getDecodeCapability() {
        Vector<Class> generators = new Vector<Class>();
        generators.add(class$com$sun$media$jai$tilecodec$JPEGTileDecoderFactory == null ? (class$com$sun$media$jai$tilecodec$JPEGTileDecoderFactory = JPEGTileDecoderFactory.class$("com.sun.media.jai.tilecodec.JPEGTileDecoderFactory")) : class$com$sun$media$jai$tilecodec$JPEGTileDecoderFactory);
        ParameterListDescriptor jpegPld = JAI.getDefaultInstance().getOperationRegistry().getDescriptor("tileDecoder", "jpeg").getParameterListDescriptor("tileDecoder");
        Class[] paramClasses = new Class[]{class$javax$media$jai$remote$NegotiableNumericRange == null ? (class$javax$media$jai$remote$NegotiableNumericRange = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableNumericRange")) : class$javax$media$jai$remote$NegotiableNumericRange, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableNumericRange == null ? (class$javax$media$jai$remote$NegotiableNumericRange = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableNumericRange")) : class$javax$media$jai$remote$NegotiableNumericRange, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection, class$javax$media$jai$remote$NegotiableCollection == null ? (class$javax$media$jai$remote$NegotiableCollection = JPEGTileDecoderFactory.class$("javax.media.jai.remote.NegotiableCollection")) : class$javax$media$jai$remote$NegotiableCollection};
        String[] paramNames = new String[]{"quality", "qualitySet", "restartInterval", "writeImageInfo", "writeTableInfo", "writeJFIFHeader"};
        Vector<Boolean> v = new Vector<Boolean>();
        v.add(new Boolean(true));
        v.add(new Boolean(false));
        NegotiableCollection negCollection = new NegotiableCollection(v);
        NegotiableNumericRange nnr1 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[0]));
        NegotiableNumericRange nnr2 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[2]));
        Object[] defaults = new Object[]{nnr1, negCollection, nnr2, negCollection, negCollection, negCollection};
        NegotiableCapability decodeCap = new NegotiableCapability("tileCodec", "jpeg", generators, new ParameterListDescriptorImpl(null, paramNames, paramClasses, defaults, null), false);
        decodeCap.setParameter(paramNames[0], nnr1);
        decodeCap.setParameter(paramNames[1], negCollection);
        decodeCap.setParameter(paramNames[2], nnr2);
        decodeCap.setParameter(paramNames[3], negCollection);
        decodeCap.setParameter(paramNames[4], negCollection);
        decodeCap.setParameter(paramNames[5], negCollection);
        return decodeCap;
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

