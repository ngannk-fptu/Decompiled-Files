/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.tagext.TagInfo;

public class TagFileInfo {
    private final String name;
    private final String path;
    private final TagInfo tagInfo;

    public TagFileInfo(String name, String path, TagInfo tagInfo) {
        this.name = name;
        this.path = path;
        this.tagInfo = tagInfo;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public TagInfo getTagInfo() {
        return this.tagInfo;
    }
}

