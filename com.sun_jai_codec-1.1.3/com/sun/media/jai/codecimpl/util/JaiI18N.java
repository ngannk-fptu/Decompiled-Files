/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.PropertyUtil;
import java.text.MessageFormat;
import java.util.Locale;

class JaiI18N {
    static String packageName = "com.sun.media.jai.codecimpl.util";

    JaiI18N() {
    }

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }

    public static String formatMsg(String key, Object[] args) {
        MessageFormat mf = new MessageFormat(JaiI18N.getString(key));
        mf.setLocale(Locale.getDefault());
        return mf.format(args);
    }
}

