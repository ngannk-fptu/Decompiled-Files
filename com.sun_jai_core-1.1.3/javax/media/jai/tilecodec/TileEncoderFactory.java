/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import java.io.OutputStream;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;

public interface TileEncoderFactory {
    public TileEncoder createEncoder(OutputStream var1, TileCodecParameterList var2, SampleModel var3);

    public NegotiableCapability getEncodeCapability();
}

