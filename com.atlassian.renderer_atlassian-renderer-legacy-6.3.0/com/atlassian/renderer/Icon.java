/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer;

import java.text.MessageFormat;

public class Icon {
    public static final String IMAGE_TEMPLATE = "<img class=\"{5}\" src=\"{0}/{1}\" height=\"{2}\" width=\"{3}\" align=\"absmiddle\" alt=\"{4}\" border=\"0\"/>";
    public static final int ICON_LEFT = -1;
    public static final int ICON_RIGHT = 1;
    public static final Icon NULL_ICON = new Icon(null, 0, 0, 0, "");
    private static final String LINK_DECORATION_CLASS = "rendericon";
    private static final String EMOTICON_CLASS = "emoticon";
    public final String path;
    public final int position;
    public final int width;
    public final int height;
    public final String cssClass;

    public static Icon makeRenderIcon(String path, int position, int width, int height) {
        return new Icon(path, position, width, height, LINK_DECORATION_CLASS);
    }

    public static Icon makeEmoticon(String path, int height, int width) {
        return new Icon(path, 0, width, height, EMOTICON_CLASS);
    }

    private Icon(String path, int position, int width, int height, String cssClass) {
        this.path = path;
        this.position = position;
        this.width = width;
        this.height = height;
        this.cssClass = cssClass;
    }

    public String toHtml(String imageRoot) {
        String imgTag = MessageFormat.format(IMAGE_TEMPLATE, imageRoot, this.path, Integer.toString(this.width), Integer.toString(this.height), "", this.cssClass);
        if (this.width == 0 || this.height == 0) {
            return "";
        }
        if (LINK_DECORATION_CLASS.equals(this.cssClass)) {
            return "<sup>" + imgTag + "</sup>";
        }
        return imgTag;
    }

    public String getPath() {
        return this.path;
    }

    public String toString() {
        return this.cssClass + ": " + this.path;
    }
}

