/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.mma.model.processor;

import java.util.Collections;
import java.util.List;
import lombok.Generated;

public class MetadataBatch<T> {
    private List<T> data;

    public MetadataBatch(T t) {
        this.data = Collections.singletonList(t);
    }

    @Generated
    public List<T> getData() {
        return this.data;
    }

    @Generated
    public void setData(List<T> data) {
        this.data = data;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MetadataBatch)) {
            return false;
        }
        MetadataBatch other = (MetadataBatch)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<T> this$data = this.getData();
        List<T> other$data = other.getData();
        return !(this$data == null ? other$data != null : !((Object)this$data).equals(other$data));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MetadataBatch;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<T> $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MetadataBatch(data=" + this.getData() + ")";
    }

    @Generated
    public MetadataBatch(List<T> data) {
        this.data = data;
    }

    @Generated
    public MetadataBatch() {
    }
}

