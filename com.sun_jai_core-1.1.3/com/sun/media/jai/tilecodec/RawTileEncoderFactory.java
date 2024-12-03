/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.JaiI18N;
import com.sun.media.jai.tilecodec.RawTileEncoder;
import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;

public class RawTileEncoderFactory
implements TileEncoderFactory {
    static /* synthetic */ Class class$com$sun$media$jai$tilecodec$RawTileEncoderFactory;

    public TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel) {
        if (output == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoder0"));
        }
        return new RawTileEncoder(output, paramList);
    }

    public NegotiableCapability getEncodeCapability() {
        Vector<Class> generators = new Vector<Class>();
        generators.add(class$com$sun$media$jai$tilecodec$RawTileEncoderFactory == null ? (class$com$sun$media$jai$tilecodec$RawTileEncoderFactory = RawTileEncoderFactory.class$("com.sun.media.jai.tilecodec.RawTileEncoderFactory")) : class$com$sun$media$jai$tilecodec$RawTileEncoderFactory);
        return new NegotiableCapability("tileCodec", "raw", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
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

