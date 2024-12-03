/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.whisper.plugin.ao;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

public interface MessageAO
extends RawEntity<String> {
    @PrimaryKey
    @NotNull
    public String getId();

    public void setId(String var1);

    @NotNull
    @StringLength(value=-1)
    public String getContent();

    public void setContent(String var1);
}

