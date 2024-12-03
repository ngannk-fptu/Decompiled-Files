/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;

public interface TileCodecDescriptor
extends RegistryElementDescriptor {
    public boolean includesSampleModelInfo();

    public boolean includesLocationInfo();

    public TileCodecParameterList getDefaultParameters(String var1);

    public TileCodecParameterList getDefaultParameters(String var1, SampleModel var2);

    public TileCodecParameterList getCompatibleParameters(String var1, TileCodecParameterList var2);
}

