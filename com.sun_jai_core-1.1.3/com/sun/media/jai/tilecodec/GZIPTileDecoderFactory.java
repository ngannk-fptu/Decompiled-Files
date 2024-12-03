/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.GZIPTileDecoder;
import com.sun.media.jai.tilecodec.JaiI18N;
import java.io.InputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;

public class GZIPTileDecoderFactory
implements TileDecoderFactory {
    static /* synthetic */ Class class$com$sun$media$jai$tilecodec$GZIPTileDecoderFactory;

    public TileDecoder createDecoder(InputStream input, TileCodecParameterList param) {
        if (input == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));
        }
        return new GZIPTileDecoder(input, param);
    }

    public NegotiableCapability getDecodeCapability() {
        Vector<Class> generators = new Vector<Class>();
        generators.add(class$com$sun$media$jai$tilecodec$GZIPTileDecoderFactory == null ? (class$com$sun$media$jai$tilecodec$GZIPTileDecoderFactory = GZIPTileDecoderFactory.class$("com.sun.media.jai.tilecodec.GZIPTileDecoderFactory")) : class$com$sun$media$jai$tilecodec$GZIPTileDecoderFactory);
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

