/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import org.apache.commons.imaging.icc.IccProfileInfo;
import org.apache.commons.imaging.icc.IccProfileParser;

public class ImageDump {
    private static final Logger LOGGER = Logger.getLogger(ImageDump.class.getName());

    private String colorSpaceTypeToName(ColorSpace cs) {
        switch (cs.getType()) {
            case 9: {
                return "TYPE_CMYK";
            }
            case 5: {
                return "TYPE_RGB";
            }
            case 1000: {
                return "CS_sRGB";
            }
            case 1003: {
                return "CS_GRAY";
            }
            case 1001: {
                return "CS_CIEXYZ";
            }
            case 1004: {
                return "CS_LINEAR_RGB";
            }
            case 1002: {
                return "CS_PYCC";
            }
        }
        return "unknown";
    }

    public void dumpColorSpace(String prefix, ColorSpace cs) {
        LOGGER.fine(prefix + ": type: " + cs.getType() + " (" + this.colorSpaceTypeToName(cs) + ")");
        if (!(cs instanceof ICC_ColorSpace)) {
            LOGGER.fine(prefix + ": Unknown ColorSpace: " + cs.getClass().getName());
            return;
        }
        ICC_ColorSpace iccColorSpace = (ICC_ColorSpace)cs;
        ICC_Profile iccProfile = iccColorSpace.getProfile();
        byte[] bytes = iccProfile.getData();
        IccProfileParser parser = new IccProfileParser();
        IccProfileInfo info = parser.getICCProfileInfo(bytes);
        info.dump(prefix);
    }

    public void dump(BufferedImage src) {
        this.dump("", src);
    }

    public void dump(String prefix, BufferedImage src) {
        LOGGER.fine(prefix + ": dump");
        this.dumpColorSpace(prefix, src.getColorModel().getColorSpace());
        this.dumpBIProps(prefix, src);
    }

    public void dumpBIProps(String prefix, BufferedImage src) {
        String[] keys = src.getPropertyNames();
        if (keys == null) {
            LOGGER.fine(prefix + ": no props");
            return;
        }
        for (String key : keys) {
            LOGGER.fine(prefix + ": " + key + ": " + src.getProperty(key));
        }
    }
}

