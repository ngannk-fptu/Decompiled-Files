/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color.profile;

import org.apache.xmlgraphics.java2d.color.NamedColorSpace;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;

public class NamedColorProfile {
    private String profileName;
    private String copyright;
    private NamedColorSpace[] namedColors;
    private RenderingIntent renderingIntent = RenderingIntent.PERCEPTUAL;

    public NamedColorProfile(String profileName, String copyright, NamedColorSpace[] namedColors, RenderingIntent intent) {
        this.profileName = profileName;
        this.copyright = copyright;
        this.namedColors = namedColors;
        this.renderingIntent = intent;
    }

    public RenderingIntent getRenderingIntent() {
        return this.renderingIntent;
    }

    public NamedColorSpace[] getNamedColors() {
        NamedColorSpace[] copy = new NamedColorSpace[this.namedColors.length];
        System.arraycopy(this.namedColors, 0, copy, 0, this.namedColors.length);
        return copy;
    }

    public NamedColorSpace getNamedColor(String name) {
        if (this.namedColors != null) {
            for (NamedColorSpace namedColor : this.namedColors) {
                if (!namedColor.getColorName().equals(name)) continue;
                return namedColor;
            }
        }
        return null;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Named color profile: ");
        sb.append(this.getProfileName());
        sb.append(", ").append(this.namedColors.length).append(" colors");
        return sb.toString();
    }
}

