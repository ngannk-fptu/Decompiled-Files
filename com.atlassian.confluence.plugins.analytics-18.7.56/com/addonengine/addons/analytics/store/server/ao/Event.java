/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.ao;

import kotlin.Metadata;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import org.jetbrains.annotations.Nullable;

@Indexes(value={@Index(name="name_container_id_event_at", methodNames={"getName", "getContainerId", "getEventAt"}), @Index(name="name_space_key_event_at", methodNames={"getName", "getSpaceKey", "getEventAt"}), @Index(name="space_key", methodNames={"getSpaceKey"}), @Index(name="id_event_at", methodNames={"getEventAt", "getId"})})
@Preload
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0002\b\u000e\bg\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001R\u0018\u0010\u0003\u001a\u00020\u0002X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u0004\u0018\u00010\u0002X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u00020\u00028gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u000e\u0010\u0005\"\u0004\b\u000f\u0010\u0007R\u001a\u0010\u0010\u001a\u00020\u00028gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0011\u0010\u0005\"\u0004\b\u0012\u0010\u0007R\u001a\u0010\u0013\u001a\u00020\u00148gX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u0018\u0010\u0019\u001a\u00020\u0014X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u001a\u0010\u0016\"\u0004\b\u001b\u0010\u0018R\u0018\u0010\u001c\u001a\u00020\u0014X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\u001d\u0010\u0016\"\u0004\b\u001e\u0010\u0018R\u001a\u0010\u001f\u001a\u0004\u0018\u00010\u0002X\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b \u0010\n\"\u0004\b!\u0010\f\u00a8\u0006\""}, d2={"Lcom/addonengine/addons/analytics/store/server/ao/Event;", "Lnet/java/ao/RawEntity;", "", "containerId", "getContainerId", "()J", "setContainerId", "(J)V", "contentId", "getContentId", "()Ljava/lang/Long;", "setContentId", "(Ljava/lang/Long;)V", "eventAt", "getEventAt", "setEventAt", "id", "getId", "setId", "name", "", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "spaceKey", "getSpaceKey", "setSpaceKey", "userKey", "getUserKey", "setUserKey", "versionModificationDate", "getVersionModificationDate", "setVersionModificationDate", "analytics"})
public interface Event
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    public void setId(long var1);

    @NotNull
    @org.jetbrains.annotations.NotNull
    public String getName();

    public void setName(@org.jetbrains.annotations.NotNull String var1);

    @NotNull
    public long getEventAt();

    public void setEventAt(long var1);

    public long getContainerId();

    public void setContainerId(long var1);

    @org.jetbrains.annotations.NotNull
    public String getSpaceKey();

    public void setSpaceKey(@org.jetbrains.annotations.NotNull String var1);

    @org.jetbrains.annotations.NotNull
    public String getUserKey();

    public void setUserKey(@org.jetbrains.annotations.NotNull String var1);

    @Nullable
    public Long getContentId();

    public void setContentId(@Nullable Long var1);

    @Nullable
    public Long getVersionModificationDate();

    public void setVersionModificationDate(@Nullable Long var1);
}

