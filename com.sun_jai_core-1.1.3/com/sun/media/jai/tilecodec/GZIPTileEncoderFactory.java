/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.GZIPTileEncoder;
import com.sun.media.jai.tilecodec.JaiI18N;
import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;

public class GZIPTileEncoderFactory
implements TileEncoderFactory {
    static /* synthetic */ Class class$com$sun$media$jai$tilecodec$GZIPTileEncoderFactory;

    public TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel) {
        if (output == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoder0"));
        }
        return new GZIPTileEncoder(output, paramList);
    }

    public NegotiableCapability getEncodeCapability() {
        Vector<Class> generators = new Vector<Class>();
        generators.add(class$com$sun$media$jai$tilecodec$GZIPTileEncoderFactory == null ? (class$com$sun$media$jai$tilecodec$GZIPTileEncoderFactory = GZIPTileEncoderFactory.class$("com.sun.media.jai.tilecodec.GZIPTileEncoderFactory")) : class$com$sun$media$jai$tilecodec$GZIPTileEncoderFactory);
        return new NegotiableCapability("tileCodec", "gzip", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
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

