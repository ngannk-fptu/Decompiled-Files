/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.TagSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BucketTaggingConfiguration
implements Serializable {
    private List<TagSet> tagSets = new ArrayList<TagSet>(1);

    public BucketTaggingConfiguration() {
    }

    public BucketTaggingConfiguration(Collection<TagSet> tagSets) {
        this.tagSets.addAll(tagSets);
    }

    public BucketTaggingConfiguration withTagSets(TagSet ... tagSets) {
        this.tagSets.clear();
        for (int index = 0; index < tagSets.length; ++index) {
            this.tagSets.add(tagSets[index]);
        }
        return this;
    }

    public void setTagSets(Collection<TagSet> tagSets) {
        this.tagSets.clear();
        this.tagSets.addAll(tagSets);
    }

    public List<TagSet> getAllTagSets() {
        return this.tagSets;
    }

    public TagSet getTagSet() {
        return this.tagSets.get(0);
    }

    public TagSet getTagSetAtIndex(int index) {
        return this.tagSets.get(index);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("TagSets: " + this.getAllTagSets());
        sb.append("}");
        return sb.toString();
    }
}

