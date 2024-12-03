/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Unique
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.ao;

import kotlin.Metadata;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Unique;

@Preload
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u000b\bg\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001R\u001a\u0010\u0003\u001a\u00020\u00028gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u00020\u00028gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\t\u0010\u0005\"\u0004\b\n\u0010\u0007R\u001a\u0010\u000b\u001a\u00020\f8gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u00028gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0012\u0010\u0005\"\u0004\b\u0013\u0010\u0007R\u001a\u0010\u0014\u001a\u00020\f8gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0015\u0010\u000e\"\u0004\b\u0016\u0010\u0010\u00a8\u0006\u0017"}, d2={"Lcom/addonengine/addons/analytics/store/server/ao/Settings;", "Lnet/java/ao/RawEntity;", "", "createdAt", "getCreatedAt", "()J", "setCreatedAt", "(J)V", "id", "getId", "setId", "key", "", "getKey", "()Ljava/lang/String;", "setKey", "(Ljava/lang/String;)V", "updatedAt", "getUpdatedAt", "setUpdatedAt", "value", "getValue", "setValue", "analytics"})
public interface Settings
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    public void setId(long var1);

    @NotNull
    @Unique
    @StringLength(value=255)
    @org.jetbrains.annotations.NotNull
    public String getKey();

    public void setKey(@org.jetbrains.annotations.NotNull String var1);

    @NotNull
    @StringLength(value=-1)
    @org.jetbrains.annotations.NotNull
    public String getValue();

    public void setValue(@org.jetbrains.annotations.NotNull String var1);

    @NotNull
    public long getCreatedAt();

    public void setCreatedAt(long var1);

    @NotNull
    public long getUpdatedAt();

    public void setUpdatedAt(long var1);
}

