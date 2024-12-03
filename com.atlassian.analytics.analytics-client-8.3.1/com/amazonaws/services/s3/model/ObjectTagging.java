/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.Tag;
import java.io.Serializable;
import java.util.List;

public class ObjectTagging
implements Serializable {
    private List<Tag> tagSet;

    public ObjectTagging(List<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public List<Tag> getTagSet() {
        return this.tagSet;
    }

    public void setTagSet(List<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    private ObjectTagging withTagSet(List<Tag> tagSet) {
        this.tagSet = tagSet;
        return this;
    }
}

