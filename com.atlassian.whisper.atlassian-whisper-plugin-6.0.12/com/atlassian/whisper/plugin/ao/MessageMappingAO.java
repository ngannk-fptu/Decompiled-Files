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

public interface MessageMappingAO
extends Entity {
    @Indexed
    @NotNull
    public String getUserHash();

    public void setUserHash(String var1);

    @NotNull
    @Indexed
    public String getMessageId();

    public void setMessageId(String var1);
}

