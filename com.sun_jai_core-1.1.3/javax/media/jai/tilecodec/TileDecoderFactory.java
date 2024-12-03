/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.io.InputStream;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;

public interface TileDecoderFactory {
    public TileDecoder createDecoder(InputStream var1, TileCodecParameterList var2);

    public NegotiableCapability getDecodeCapability();
}

