/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.plugins.authentication.impl.db.model;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Preload
@Table(value="SEEN_ASSERTIONS")
public interface SeenAssertion
extends Entity {
    public static final String EXPIRY_TIMESTAMP_COLUMN = "EXPIRY_TIMESTAMP";
    public static final String ASSERTION_ID_COLUMN = "ASSERTION_ID";

    @Unique
    @NotNull
    @Accessor(value="ASSERTION_ID")
    public String getValue();

    @Mutator(value="ASSERTION_ID")
    public void setValue(String var1);

    @NotNull
    @Accessor(value="EXPIRY_TIMESTAMP")
    @Indexed
    public long getExpiryTimestamp();

    @Mutator(value="EXPIRY_TIMESTAMP")
    public void setExpiryTimestamp(long var1);
}

