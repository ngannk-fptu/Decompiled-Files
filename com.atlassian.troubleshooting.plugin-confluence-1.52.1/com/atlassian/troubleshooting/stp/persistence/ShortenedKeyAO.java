/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.troubleshooting.stp.persistence;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Table(value="SHORTENED_KEY")
@Preload
public interface ShortenedKeyAO
extends Entity {
    @NotNull
    @Unique
    public String getKey();
}

