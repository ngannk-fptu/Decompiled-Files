/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util.autodetect;

import org.apache.fontbox.util.autodetect.NativeFontDirFinder;

public class MacFontDirFinder
extends NativeFontDirFinder {
    @Override
    protected String[] getSearchableDirectories() {
        return new String[]{System.getProperty("user.home") + "/Library/Fonts/", "/Library/Fonts/", "/System/Library/Fonts/", "/Network/Library/Fonts/"};
    }
}

