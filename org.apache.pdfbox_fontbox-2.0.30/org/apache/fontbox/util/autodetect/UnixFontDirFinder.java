/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util.autodetect;

import org.apache.fontbox.util.autodetect.NativeFontDirFinder;

public class UnixFontDirFinder
extends NativeFontDirFinder {
    @Override
    protected String[] getSearchableDirectories() {
        return new String[]{System.getProperty("user.home") + "/.fonts", "/usr/local/fonts", "/usr/local/share/fonts", "/usr/share/fonts", "/usr/X11R6/lib/X11/fonts", "/usr/share/X11/fonts"};
    }
}

