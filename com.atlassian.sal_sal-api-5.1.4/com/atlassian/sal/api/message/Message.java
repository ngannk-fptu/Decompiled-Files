/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.message;

import java.io.Serializable;

public interface Message
extends Serializable {
    public String getKey();

    public Serializable[] getArguments();
}

