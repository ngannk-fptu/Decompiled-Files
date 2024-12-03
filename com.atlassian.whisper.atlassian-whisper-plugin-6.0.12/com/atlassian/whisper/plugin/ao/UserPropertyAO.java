/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 */
package com.atlassian.whisper.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;

public interface UserPropertyAO
extends Entity {
    @Indexed
    @NotNull
    public String getUser();

    public void setUser(String var1);

    @NotNull
    public String getKey();

    public void setKey(String var1);

    @NotNull
    public String getValue();

    public void setValue(String var1);
}

