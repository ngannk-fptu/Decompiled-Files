/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.util.autodetect;

import org.apache.fontbox.util.autodetect.NativeFontDirFinder;

public class OS400FontDirFinder
extends NativeFontDirFinder {
    @Override
    protected String[] getSearchableDirectories() {
        return new String[]{System.getProperty("user.home") + "/.fonts", "/QIBM/ProdData/OS400/Fonts"};
    }
}

