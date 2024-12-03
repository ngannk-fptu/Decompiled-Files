/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalRetentionPolicy
implements RetentionPolicy {
    @JsonProperty
    private RetentionRule pageRetentionRule = new RetentionRule();
    @JsonProperty
    private RetentionRule attachmentRetentionRule = new RetentionRule();
    @JsonProperty
    private TrashRetentionRule trashRetentionRule = new TrashRetentionRule();
    @JsonProperty
    private String lastModifiedBy;
    @JsonProperty
    private boolean spaceOverridesAllowed = false;

    @Override
    public RetentionRule getPageVersionRule() {
        return this.pageRetentionRule;
    }

    public void setPageRetentionRule(RetentionRule pageRetentionRule) {
        this.pageRetentionRule = pageRetentionRule;
    }

    @Override
    public RetentionRule getAttachmentRetentionRule() {
        return this.attachmentRetentionRule;
    }

    public void setTrashRetentionRule(TrashRetentionRule trashRetentionRule) {
        this.trashRetentionRule = trashRetentionRule;
    }

    @Override
    public TrashRetentionRule getTrashRetentionRule() {
        return this.trashRetentionRule;
    }

    public void setAttachmentRetentionRule(RetentionRule attachmentRetentionRule) {
        this.attachmentRetentionRule = attachmentRetentionRule;
    }

    public boolean getSpaceOverridesAllowed() {
        return this.spaceOverridesAllowed;
    }

    public void setSpaceOverridesAllowed(boolean spaceOverridesAllowed) {
        this.spaceOverridesAllowed = spaceOverridesAllowed;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public List<String> validate() {
        ArrayList<String> validations = new ArrayList<String>();
        validations.addAll(this.pageRetentionRule.validate());
        validations.addAll(this.attachmentRetentionRule.validate());
        validations.addAll(this.trashRetentionRule.validate());
        return validations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GlobalRetentionPolicy that = (GlobalRetentionPolicy)o;
        return Objects.equals(this.pageRetentionRule, that.pageRetentionRule) && Objects.equals(this.attachmentRetentionRule, that.attachmentRetentionRule) && Objects.equals(this.trashRetentionRule, that.trashRetentionRule) && Objects.equals(this.lastModifiedBy, that.lastModifiedBy) && Objects.equals(this.spaceOverridesAllowed, that.spaceOverridesAllowed);
    }

    public int hashCode() {
        return Objects.hash(this.pageRetentionRule, this.attachmentRetentionRule, this.trashRetentionRule, this.lastModifiedBy, this.spaceOverridesAllowed);
    }

    public String toString() {
        return new StringJoiner(", ", "GlobalRetentionPolicy[", "]").add("pageRetentionRule=" + this.pageRetentionRule).add("attachmentRetentionRule=" + this.attachmentRetentionRule).add("trashRetentionRule=" + this.trashRetentionRule).add("lastModifiedBy='" + this.lastModifiedBy + "'").add("spaceOverridesAllowed=" + this.spaceOverridesAllowed).toString();
    }
}

