/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;
import javax.media.jai.tilecodec.TileCodecParameterList;

public interface TileEncoder {
    public String getFormatName();

    public TileCodecParameterList getEncodeParameterList();

    public OutputStream getOutputStream();

    public void encode(Raster var1) throws IOException;
}

