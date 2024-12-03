/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.util.regex.WildcardStringParser
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.util.regex.WildcardStringParser;
import java.io.File;
import java.io.FilenameFilter;

@Deprecated
public class FilenameMaskFilter
implements FilenameFilter {
    private String[] filenameMasksForInclusion;
    private String[] filenameMasksForExclusion;
    private boolean inclusion = true;

    public FilenameMaskFilter() {
    }

    public FilenameMaskFilter(String string) {
        String[] stringArray = new String[]{string};
        this.setFilenameMasksForInclusion(stringArray);
    }

    public FilenameMaskFilter(String[] stringArray) {
        this(stringArray, false);
    }

    public FilenameMaskFilter(String string, boolean bl) {
        String[] stringArray = new String[]{string};
        if (bl) {
            this.setFilenameMasksForExclusion(stringArray);
        } else {
            this.setFilenameMasksForInclusion(stringArray);
        }
    }

    public FilenameMaskFilter(String[] stringArray, boolean bl) {
        if (bl) {
            this.setFilenameMasksForExclusion(stringArray);
        } else {
            this.setFilenameMasksForInclusion(stringArray);
        }
    }

    public void setFilenameMasksForInclusion(String[] stringArray) {
        this.filenameMasksForInclusion = stringArray;
    }

    public String[] getFilenameMasksForInclusion() {
        return (String[])this.filenameMasksForInclusion.clone();
    }

    public void setFilenameMasksForExclusion(String[] stringArray) {
        this.filenameMasksForExclusion = stringArray;
        this.inclusion = false;
    }

    public String[] getFilenameMasksForExclusion() {
        return (String[])this.filenameMasksForExclusion.clone();
    }

    @Override
    public boolean accept(File file, String string) {
        if (this.inclusion) {
            for (String string2 : this.filenameMasksForInclusion) {
                WildcardStringParser wildcardStringParser = new WildcardStringParser(string2);
                if (!wildcardStringParser.parseString(string)) continue;
                return true;
            }
            return false;
        }
        for (String string3 : this.filenameMasksForExclusion) {
            WildcardStringParser wildcardStringParser = new WildcardStringParser(string3);
            if (!wildcardStringParser.parseString(string)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.inclusion) {
            if (this.filenameMasksForInclusion == null) {
                stringBuilder.append("No filename masks set - property filenameMasksForInclusion is null!");
            } else {
                stringBuilder.append(this.filenameMasksForInclusion.length);
                stringBuilder.append(" filename mask(s) - ");
                for (int i = 0; i < this.filenameMasksForInclusion.length; ++i) {
                    stringBuilder.append("\"");
                    stringBuilder.append(this.filenameMasksForInclusion[i]);
                    stringBuilder.append("\", \"");
                }
            }
        } else if (this.filenameMasksForExclusion == null) {
            stringBuilder.append("No filename masks set - property filenameMasksForExclusion is null!");
        } else {
            stringBuilder.append(this.filenameMasksForExclusion.length);
            stringBuilder.append(" exclusion filename mask(s) - ");
            for (int i = 0; i < this.filenameMasksForExclusion.length; ++i) {
                stringBuilder.append("\"");
                stringBuilder.append(this.filenameMasksForExclusion[i]);
                stringBuilder.append("\", \"");
            }
        }
        return stringBuilder.toString();
    }
}

