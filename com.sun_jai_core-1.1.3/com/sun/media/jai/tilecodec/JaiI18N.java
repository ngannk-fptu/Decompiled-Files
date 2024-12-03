/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import com.sun.media.jai.util.PropertyUtil;

class JaiI18N {
    static String packageName = "com.sun.media.jai.tilecodec";

    JaiI18N() {
    }

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }
}

