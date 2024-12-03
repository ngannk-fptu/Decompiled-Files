/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;

public abstract class TagLibraryInfo {
    protected String prefix;
    protected String uri;
    protected TagInfo[] tags;
    protected TagFileInfo[] tagFiles;
    protected FunctionInfo[] functions;
    protected String tlibversion;
    protected String jspversion;
    protected String shortname;
    protected String urn;
    protected String info;

    protected TagLibraryInfo(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getURI() {
        return this.uri;
    }

    public String getPrefixString() {
        return this.prefix;
    }

    public String getShortName() {
        return this.shortname;
    }

    public String getReliableURN() {
        return this.urn;
    }

    public String getInfoString() {
        return this.info;
    }

    public String getRequiredVersion() {
        return this.jspversion;
    }

    public TagInfo[] getTags() {
        return this.tags;
    }

    public TagFileInfo[] getTagFiles() {
        return this.tagFiles;
    }

    public TagInfo getTag(String shortname) {
        TagInfo[] tags = this.getTags();
        if (tags == null || tags.length == 0 || shortname == null) {
            return null;
        }
        for (TagInfo tag : tags) {
            if (!shortname.equals(tag.getTagName())) continue;
            return tag;
        }
        return null;
    }

    public TagFileInfo getTagFile(String shortname) {
        TagFileInfo[] tagFiles = this.getTagFiles();
        if (tagFiles == null || tagFiles.length == 0) {
            return null;
        }
        for (TagFileInfo tagFile : tagFiles) {
            if (!tagFile.getName().equals(shortname)) continue;
            return tagFile;
        }
        return null;
    }

    public FunctionInfo[] getFunctions() {
        return this.functions;
    }

    public FunctionInfo getFunction(String name) {
        if (this.functions == null || this.functions.length == 0) {
            return null;
        }
        for (FunctionInfo function : this.functions) {
            if (!function.getName().equals(name)) continue;
            return function;
        }
        return null;
    }

    public abstract TagLibraryInfo[] getTagLibraryInfos();
}

