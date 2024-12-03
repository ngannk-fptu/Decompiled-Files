/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import java.util.List;
import javax.annotation.Nullable;

public final class SoyMsg {
    private final long id;
    private final long altId;
    private final String localeString;
    private final String meaning;
    private final String desc;
    private final boolean isHidden;
    private final String contentType;
    private ImmutableSet<String> sourcePaths;
    private final boolean isPlrselMsg;
    private final ImmutableList<SoyMsgPart> parts;

    public SoyMsg(long id, long altId, @Nullable String localeString, @Nullable String meaning, @Nullable String desc, boolean isHidden, @Nullable String contentType, @Nullable String sourcePath, boolean isPlrselMsg, List<SoyMsgPart> parts) {
        Preconditions.checkArgument((id >= 0L ? 1 : 0) != 0);
        Preconditions.checkArgument((altId >= -1L ? 1 : 0) != 0);
        this.id = id;
        this.altId = altId;
        this.localeString = localeString;
        this.meaning = meaning;
        this.desc = desc;
        this.isHidden = isHidden;
        this.contentType = contentType;
        this.sourcePaths = ImmutableSet.of();
        if (sourcePath != null) {
            this.addSourcePath(sourcePath);
        }
        this.isPlrselMsg = isPlrselMsg;
        this.parts = ImmutableList.copyOf(parts);
    }

    public SoyMsg(long id, @Nullable String localeString, @Nullable String meaning, @Nullable String desc, boolean isHidden, @Nullable String contentType, @Nullable String sourcePath, List<SoyMsgPart> parts) {
        this(id, -1L, localeString, meaning, desc, isHidden, contentType, sourcePath, false, parts);
    }

    public SoyMsg(long id, @Nullable String localeString, boolean isPlrselMsg, List<SoyMsgPart> parts) {
        this(id, -1L, localeString, null, null, false, null, null, isPlrselMsg, parts);
    }

    public String getLocaleString() {
        return this.localeString;
    }

    public long getId() {
        return this.id;
    }

    public long getAltId() {
        return this.altId;
    }

    public String getMeaning() {
        return this.meaning;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void addSourcePath(String sourcePath) {
        this.sourcePaths = ImmutableSet.builder().addAll(this.sourcePaths).add((Object)sourcePath).build();
    }

    public ImmutableSet<String> getSourcePaths() {
        return this.sourcePaths;
    }

    public boolean isPlrselMsg() {
        return this.isPlrselMsg;
    }

    public ImmutableList<SoyMsgPart> getParts() {
        return this.parts;
    }

    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof SoyMsg)) {
            return false;
        }
        SoyMsg other = (SoyMsg)otherObject;
        return this.id == other.id && this.altId == other.altId && Objects.equal((Object)this.localeString, (Object)other.localeString) && Objects.equal((Object)this.meaning, (Object)other.meaning) && Objects.equal((Object)this.desc, (Object)other.desc) && this.isHidden == other.isHidden && Objects.equal((Object)this.contentType, (Object)other.contentType) && this.isPlrselMsg == other.isPlrselMsg && Objects.equal(this.parts, other.parts);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.id, this.altId, this.localeString, this.meaning, this.desc, this.contentType, this.isPlrselMsg, this.parts});
    }

    public String toString() {
        return this.getClass() + "(" + this.id + ", " + this.altId + ", " + this.localeString + ", " + this.meaning + ", " + this.desc + ", " + this.isHidden + ", " + this.contentType + ", " + this.sourcePaths + ", " + this.isPlrselMsg + ", " + this.parts + ")";
    }
}

