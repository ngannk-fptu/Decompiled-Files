/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import java.io.Serializable;

public interface Progress
extends Serializable {
    public int getCount();

    public int getTotal();

    public int getPercentComplete();

    public int increment();

    public int increment(int var1);
}

