/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codecimpl.util.PropertyUtil;

class JaiI18N {
    static String packageName = "com.sun.media.jai.codec";

    JaiI18N() {
    }

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }
}

