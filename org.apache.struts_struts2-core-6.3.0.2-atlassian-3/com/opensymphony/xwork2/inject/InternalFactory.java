/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.InternalContext;
import java.io.Serializable;

interface InternalFactory<T>
extends Serializable {
    public T create(InternalContext var1);

    public Class<? extends T> type();
}

