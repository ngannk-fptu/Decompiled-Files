/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.HashSet;
import org.eclipse.jetty.util.StringUtil;

public class AsciiLowerCaseSet
extends HashSet<String> {
    @Override
    public boolean add(String s) {
        return super.add(s == null ? null : StringUtil.asciiToLowerCase(s));
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            return super.contains(StringUtil.asciiToLowerCase((String)o));
        }
        return super.contains(o);
    }
}

