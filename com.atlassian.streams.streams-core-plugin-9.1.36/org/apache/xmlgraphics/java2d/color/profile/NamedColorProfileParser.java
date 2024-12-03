/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color.profile;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.xmlgraphics.java2d.color.CIELabColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.xmlgraphics.java2d.color.NamedColorSpace;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import org.apache.xmlgraphics.java2d.color.profile.NamedColorProfile;

public class NamedColorProfileParser {
    private static final int MLUC = 1835824483;
    private static final int NCL2 = 1852009522;

    public static boolean isNamedColorProfile(ICC_Profile profile) {
        return profile.getProfileClass() == 6;
    }

    public NamedColorProfile parseProfile(ICC_Profile profile, String profileName, String profileURI) throws IOException {
        if (!NamedColorProfileParser.isNamedColorProfile(profile)) {
            throw new IllegalArgumentException("Given profile is not a named color profile (NCP)");
        }
        String profileDescription = this.getProfileDescription(profile);
        String copyright = this.getCopyright(profile);
        RenderingIntent intent = this.getRenderingIntent(profile);
        NamedColorSpace[] ncs = this.readNamedColors(profile, profileName, profileURI);
        return new NamedColorProfile(profileDescription, copyright, ncs, intent);
    }

    public NamedColorProfile parseProfile(ICC_Profile profile) throws IOException {
        return this.parseProfile(profile, null, null);
    }

    private String getProfileDescription(ICC_Profile profile) throws IOException {
        byte[] tag = profile.getData(1684370275);
        return this.readSimpleString(tag);
    }

    private String getCopyright(ICC_Profile profile) throws IOException {
        byte[] tag = profile.getData(1668313716);
        return this.readSimpleString(tag);
    }

    private RenderingIntent getRenderingIntent(ICC_Profile profile) throws IOException {
        byte[] hdr = profile.getData(1751474532);
        byte value = hdr[64];
        return RenderingIntent.fromICCValue(value);
    }

    private NamedColorSpace[] readNamedColors(ICC_Profile profile, String profileName, String profileURI) throws IOException {
        byte[] tag = profile.getData(1852009522);
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(tag));
        int sig = din.readInt();
        if (sig != 1852009522) {
            throw new UnsupportedOperationException("Unsupported structure type: " + this.toSignatureString(sig) + ". Expected " + this.toSignatureString(1852009522));
        }
        din.skipBytes(8);
        int numColors = din.readInt();
        NamedColorSpace[] result = new NamedColorSpace[numColors];
        int numDeviceCoord = din.readInt();
        String prefix = this.readAscii(din, 32);
        String suffix = this.readAscii(din, 32);
        block4: for (int i = 0; i < numColors; ++i) {
            String name = prefix + this.readAscii(din, 32) + suffix;
            int[] pcs = this.readUInt16Array(din, 3);
            float[] colorvalue = new float[3];
            for (int j = 0; j < pcs.length; ++j) {
                colorvalue[j] = (float)pcs[j] / 32768.0f;
            }
            this.readUInt16Array(din, numDeviceCoord);
            switch (profile.getPCSType()) {
                case 0: {
                    result[i] = new NamedColorSpace(name, colorvalue, profileName, profileURI);
                    continue block4;
                }
                case 1: {
                    CIELabColorSpace labCS = ColorSpaces.getCIELabColorSpaceD50();
                    result[i] = new NamedColorSpace(name, labCS.toColor(colorvalue, 1.0f), profileName, profileURI);
                    continue block4;
                }
                default: {
                    throw new UnsupportedOperationException("PCS type is not supported: " + profile.getPCSType());
                }
            }
        }
        return result;
    }

    private int[] readUInt16Array(DataInput din, int count) throws IOException {
        if (count == 0) {
            return new int[0];
        }
        int[] result = new int[count];
        for (int i = 0; i < count; ++i) {
            int v;
            result[i] = v = din.readUnsignedShort();
        }
        return result;
    }

    private String readAscii(DataInput in, int maxLength) throws IOException {
        byte[] data = new byte[maxLength];
        in.readFully(data);
        String result = new String(data, "US-ASCII");
        int idx = result.indexOf(0);
        if (idx >= 0) {
            result = result.substring(0, idx);
        }
        return result;
    }

    private String readSimpleString(byte[] tag) throws IOException {
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(tag));
        int sig = din.readInt();
        if (sig == 1835824483) {
            return this.readMLUC(din);
        }
        return null;
    }

    private String readMLUC(DataInput din) throws IOException {
        din.skipBytes(16);
        int firstLength = din.readInt();
        int firstOffset = din.readInt();
        int offset = 28;
        din.skipBytes(firstOffset - offset);
        byte[] utf16 = new byte[firstLength];
        din.readFully(utf16);
        return new String(utf16, "UTF-16BE");
    }

    private String toSignatureString(int sig) {
        StringBuffer sb = new StringBuffer();
        sb.append((char)(sig >> 24 & 0xFF));
        sb.append((char)(sig >> 16 & 0xFF));
        sb.append((char)(sig >> 8 & 0xFF));
        sb.append((char)(sig & 0xFF));
        return sb.toString();
    }
}

