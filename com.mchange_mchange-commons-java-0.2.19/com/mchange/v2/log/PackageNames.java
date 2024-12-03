/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v2.log.NameTransformer;

public class PackageNames
implements NameTransformer {
    @Override
    public String transformName(String string) {
        return null;
    }

    @Override
    public String transformName(Class clazz) {
        String string = clazz.getName();
        int n = string.lastIndexOf(46);
        if (n <= 0) {
            return "";
        }
        return string.substring(0, n);
    }

    @Override
    public String transformName() {
        return null;
    }
}

