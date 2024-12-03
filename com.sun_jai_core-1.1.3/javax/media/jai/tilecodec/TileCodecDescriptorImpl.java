/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import javax.media.jai.PropertyGenerator;
import javax.media.jai.tilecodec.JaiI18N;
import javax.media.jai.tilecodec.TileCodecDescriptor;

public abstract class TileCodecDescriptorImpl
implements TileCodecDescriptor {
    private String formatName;
    private boolean includesSMInfo;
    private boolean includesLocationInfo;

    public TileCodecDescriptorImpl(String formatName, boolean includesSampleModelInfo, boolean includesLocationInfo) {
        if (formatName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
        }
        this.formatName = formatName;
        this.includesSMInfo = includesSampleModelInfo;
        this.includesLocationInfo = includesLocationInfo;
    }

    public String getName() {
        return this.formatName;
    }

    public String[] getSupportedModes() {
        return new String[]{"tileDecoder", "tileEncoder"};
    }

    public boolean isModeSupported(String registryModeName) {
        if (registryModeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        return registryModeName.equalsIgnoreCase("tileDecoder") || registryModeName.equalsIgnoreCase("tileEncoder");
    }

    public boolean arePropertiesSupported() {
        return false;
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        throw new UnsupportedOperationException(JaiI18N.getString("TileCodecDescriptorImpl2"));
    }

    public boolean includesSampleModelInfo() {
        return this.includesSMInfo;
    }

    public boolean includesLocationInfo() {
        return this.includesLocationInfo;
    }
}

