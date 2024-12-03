/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.tilecodec.JaiI18N;
import javax.media.jai.tilecodec.TileCodecDescriptorImpl;
import javax.media.jai.tilecodec.TileCodecParameterList;

public class RawTileCodecDescriptor
extends TileCodecDescriptorImpl {
    private static ParameterListDescriptorImpl pld = new ParameterListDescriptorImpl();

    public RawTileCodecDescriptor() {
        super("raw", true, true);
    }

    public TileCodecParameterList getCompatibleParameters(String modeName, TileCodecParameterList otherParamList) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return null;
    }

    public TileCodecParameterList getDefaultParameters(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return null;
    }

    public TileCodecParameterList getDefaultParameters(String modeName, SampleModel sm) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return null;
    }

    public ParameterListDescriptor getParameterListDescriptor(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return pld;
    }
}

