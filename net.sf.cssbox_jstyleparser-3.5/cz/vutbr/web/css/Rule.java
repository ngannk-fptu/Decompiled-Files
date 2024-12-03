/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import java.util.List;

public interface Rule<T>
extends List<T> {
    public Rule<T> replaceAll(List<T> var1);

    public Rule<T> unlock();

    public List<T> asList();
}

