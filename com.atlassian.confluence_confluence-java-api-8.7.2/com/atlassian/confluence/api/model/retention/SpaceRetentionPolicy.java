/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class SpaceRetentionPolicy
implements RetentionPolicy {
    @JsonProperty
    private boolean spaceAdminCanEdit;
    @JsonProperty
    private DateTime lastModifiedDate = new DateTime();
    @JsonProperty
    private RetentionRule pageRetentionRule = new RetentionRule();
    @JsonProperty
    private RetentionRule attachmentRetentionRule = new RetentionRule();
    @JsonProperty
    private TrashRetentionRule trashRetentionRule = new TrashRetentionRule();
    @JsonProperty
    private String lastModifiedBy;

    @Override
    public RetentionRule getPageVersionRule() {
        return this.pageRetentionRule;
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

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Deprecated
    public DateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    @JsonIgnore
    public OffsetDateTime getLastModifiedAt() {
        return JodaTimeUtils.convert(this.lastModifiedDate);
    }

    @Deprecated
    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonIgnore
    public void setLastModifiedAt(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = JodaTimeUtils.convert(lastModifiedDate);
    }

    @Override
    public List<String> validate() {
        ArrayList<String> validations = new ArrayList<String>();
        validations.addAll(this.pageRetentionRule.validate());
        validations.addAll(this.attachmentRetentionRule.validate());
        validations.addAll(this.trashRetentionRule.validate());
        return validations;
    }

    @JsonProperty
    public boolean getSpaceAdminCanEdit() {
        return this.spaceAdminCanEdit;
    }

    public void setSpaceAdminCanEdit(boolean spaceAdminCanEdit) {
        this.spaceAdminCanEdit = spaceAdminCanEdit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceRetentionPolicy that = (SpaceRetentionPolicy)o;
        return Objects.equals(this.spaceAdminCanEdit, that.spaceAdminCanEdit) && Objects.equals(this.pageRetentionRule, that.pageRetentionRule) && Objects.equals(this.attachmentRetentionRule, that.attachmentRetentionRule) && Objects.equals(this.trashRetentionRule, that.trashRetentionRule) && Objects.equals(this.lastModifiedBy, that.lastModifiedBy);
    }

    public int hashCode() {
        return Objects.hash(this.spaceAdminCanEdit, this.pageRetentionRule, this.attachmentRetentionRule, this.trashRetentionRule, this.lastModifiedBy);
    }

    public String toString() {
        return "SpaceRetentionPolicy{spaceAdminCanEdit=" + this.spaceAdminCanEdit + ", lastModifiedDate=" + this.lastModifiedDate + ", pageRetentionRule=" + this.pageRetentionRule + ", attachmentRetentionRule=" + this.attachmentRetentionRule + ", trashRetentionRule=" + this.trashRetentionRule + ", lastModifiedBy='" + this.lastModifiedBy + '\'' + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RetentionRule pageRetentionRule = new RetentionRule();
        private RetentionRule attachmentRetentionRule = new RetentionRule();
        private TrashRetentionRule trashRetentionRule = new TrashRetentionRule();
        private boolean spaceAdminCanEdit;

        public Builder pageRetentionRule(RetentionRule pageRetentionRule) {
            this.pageRetentionRule = pageRetentionRule;
            return this;
        }

        public Builder attachmentRetentionRule(RetentionRule attachmentRetentionRule) {
            this.attachmentRetentionRule = attachmentRetentionRule;
            return this;
        }

        public Builder trashRetentionRule(TrashRetentionRule trashRetentionRule) {
            this.trashRetentionRule = trashRetentionRule;
            return this;
        }

        public Builder spaceAdminCanEdit(boolean spaceAdminCanEdit) {
            this.spaceAdminCanEdit = spaceAdminCanEdit;
            return this;
        }

        public SpaceRetentionPolicy build() {
            SpaceRetentionPolicy spaceRetentionPolicy = new SpaceRetentionPolicy();
            spaceRetentionPolicy.pageRetentionRule = this.pageRetentionRule;
            spaceRetentionPolicy.attachmentRetentionRule = this.attachmentRetentionRule;
            spaceRetentionPolicy.trashRetentionRule = this.trashRetentionRule;
            spaceRetentionPolicy.spaceAdminCanEdit = this.spaceAdminCanEdit;
            return spaceRetentionPolicy;
        }
    }
}

