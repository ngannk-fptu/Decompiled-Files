/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import org.apache.xmlgraphics.java2d.color.AbstractDeviceSpecificColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaceOrigin;
import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;

public class DeviceCMYKColorSpace
extends AbstractDeviceSpecificColorSpace
implements ColorSpaceOrigin {
    private static final long serialVersionUID = 2925508946083542974L;
    public static final String PSEUDO_PROFILE_NAME = "#CMYK";

    public DeviceCMYKColorSpace() {
        super(9, 4);
    }

    @Deprecated
    public static DeviceCMYKColorSpace getInstance() {
        return ColorSpaces.getDeviceCMYKColorSpace();
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        return new float[]{(1.0f - colorvalue[0]) * (1.0f - colorvalue[3]), (1.0f - colorvalue[1]) * (1.0f - colorvalue[3]), (1.0f - colorvalue[2]) * (1.0f - colorvalue[3])};
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        assert (rgbvalue.length == 3);
        float r = rgbvalue[0];
        float g = rgbvalue[1];
        float b = rgbvalue[2];
        if (r == g && r == b) {
            return new float[]{0.0f, 0.0f, 0.0f, 1.0f - r};
        }
        float c = 1.0f - r;
        float m = 1.0f - g;
        float y = 1.0f - b;
        float k = Math.min(c, Math.min(m, y));
        return new float[]{c, m, y, k};
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        throw new UnsupportedOperationException("NYI");
    }

    public static Color createCMYKColor(float[] cmykComponents) {
        DeviceCMYKColorSpace cmykCs = ColorSpaces.getDeviceCMYKColorSpace();
        ColorWithAlternatives cmykColor = new ColorWithAlternatives(cmykCs, cmykComponents, 1.0f, null);
        return cmykColor;
    }

    @Override
    public String getProfileName() {
        return PSEUDO_PROFILE_NAME;
    }

    @Override
    public String getProfileURI() {
        return null;
    }
}

