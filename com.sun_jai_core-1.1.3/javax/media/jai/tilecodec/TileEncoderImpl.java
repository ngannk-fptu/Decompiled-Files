/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.TileCodecUtils;
import java.io.OutputStream;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.tilecodec.JaiI18N;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;

public abstract class TileEncoderImpl
implements TileEncoder {
    protected String formatName;
    protected OutputStream outputStream;
    protected TileCodecParameterList paramList;

    public TileEncoderImpl(String formatName, OutputStream output, TileCodecParameterList param) {
        if (formatName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
        }
        if (output == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl0"));
        }
        TileCodecDescriptor tcd = TileCodecUtils.getTileCodecDescriptor("tileEncoder", formatName);
        if (param == null) {
            param = tcd.getDefaultParameters("tileEncoder");
        }
        if (param != null) {
            if (!param.getFormatName().equalsIgnoreCase(formatName)) {
                throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl1"));
            }
            if (!param.isValidForMode("tileEncoder")) {
                throw new IllegalArgumentException(JaiI18N.getString("TileEncoderImpl2"));
            }
            if (!param.getParameterListDescriptor().equals(tcd.getParameterListDescriptor("tileEncoder"))) {
                throw new IllegalArgumentException(JaiI18N.getString("TileCodec0"));
            }
        } else {
            ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileEncoder");
            if (pld != null && pld.getNumParameters() != 0) {
                throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl6"));
            }
        }
        this.formatName = formatName;
        this.outputStream = output;
        this.paramList = param;
    }

    public String getFormatName() {
        return this.formatName;
    }

    public TileCodecParameterList getEncodeParameterList() {
        return this.paramList;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }
}

