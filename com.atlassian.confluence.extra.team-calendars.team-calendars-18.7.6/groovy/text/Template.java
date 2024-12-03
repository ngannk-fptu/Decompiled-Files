/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

import groovy.lang.Writable;
import java.util.Map;

public interface Template {
    public Writable make();

    public Writable make(Map var1);
}

