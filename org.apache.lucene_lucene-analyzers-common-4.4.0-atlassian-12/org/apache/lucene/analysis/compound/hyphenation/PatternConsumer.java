/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.compound.hyphenation;

import java.util.ArrayList;

public interface PatternConsumer {
    public void addClass(String var1);

    public void addException(String var1, ArrayList<Object> var2);

    public void addPattern(String var1, String var2);
}

