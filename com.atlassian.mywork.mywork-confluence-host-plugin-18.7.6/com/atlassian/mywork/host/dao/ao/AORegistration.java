/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.mywork.host.dao.ao;

import java.util.Date;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

@Preload
public interface AORegistration
extends RawEntity<String> {
    @NotNull
    @PrimaryKey(value="ID")
    public String getId();

    @StringLength(value=-1)
    public String getData();

    public void setData(String var1);

    public Date getUpdated();

    public void setUpdated(Date var1);
}

