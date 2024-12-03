/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.Point;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import javax.media.jai.tilecodec.TileCodecParameterList;

public interface TileDecoder {
    public String getFormatName();

    public TileCodecParameterList getDecodeParameterList();

    public InputStream getInputStream();

    public Raster decode() throws IOException;

    public Raster decode(Point var1) throws IOException;
}

