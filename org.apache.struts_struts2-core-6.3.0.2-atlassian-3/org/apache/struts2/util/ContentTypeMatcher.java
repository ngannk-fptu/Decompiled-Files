/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import java.util.Map;

public interface ContentTypeMatcher<E> {
    public E compilePattern(String var1);

    public boolean match(Map<String, String> var1, String var2, E var3);
}

