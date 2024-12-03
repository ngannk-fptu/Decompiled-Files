/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.color.ColorSpace;
import org.apache.xmlgraphics.java2d.color.AbstractDeviceSpecificColorSpace;
import org.apache.xmlgraphics.java2d.color.CIELabColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaceOrigin;
import org.apache.xmlgraphics.java2d.color.DeviceCMYKColorSpace;

public final class ColorSpaces {
    private static DeviceCMYKColorSpace deviceCMYK;
    private static CIELabColorSpace cieLabD50;
    private static CIELabColorSpace cieLabD65;
    private static final ColorSpaceOrigin UNKNOWN_ORIGIN;

    private ColorSpaces() {
    }

    public static synchronized DeviceCMYKColorSpace getDeviceCMYKColorSpace() {
        if (deviceCMYK == null) {
            deviceCMYK = new DeviceCMYKColorSpace();
        }
        return deviceCMYK;
    }

    public static boolean isDeviceColorSpace(ColorSpace cs) {
        return cs instanceof AbstractDeviceSpecificColorSpace;
    }

    public static synchronized CIELabColorSpace getCIELabColorSpaceD50() {
        if (cieLabD50 == null) {
            cieLabD50 = new CIELabColorSpace(CIELabColorSpace.getD50WhitePoint());
        }
        return cieLabD50;
    }

    public static synchronized CIELabColorSpace getCIELabColorSpaceD65() {
        if (cieLabD65 == null) {
            cieLabD65 = new CIELabColorSpace(CIELabColorSpace.getD65WhitePoint());
        }
        return cieLabD65;
    }

    public static ColorSpaceOrigin getColorSpaceOrigin(ColorSpace cs) {
        if (cs instanceof ColorSpaceOrigin) {
            return (ColorSpaceOrigin)((Object)cs);
        }
        return UNKNOWN_ORIGIN;
    }

    static {
        UNKNOWN_ORIGIN = new ColorSpaceOrigin(){

            @Override
            public String getProfileURI() {
                return null;
            }

            @Override
            public String getProfileName() {
                return null;
            }
        };
    }
}

