/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import com.atlassian.troubleshooting.stp.salext.mail.EmailValidator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;

public class SupportRequestCreationRequest {
    private final Boolean limitFileSizes;
    private final Integer fileConstraintSize;
    private final Integer fileConstraintLastModified;
    private final int priority;
    private final List<SupportZipBundle> bundles;
    private final String description;
    private final String fromAddress;
    private final String subject;

    SupportRequestCreationRequest(Builder builder) {
        this.bundles = ImmutableList.copyOf((Collection)builder.bundles.build());
        this.description = Objects.requireNonNull(builder.description);
        this.fromAddress = Objects.requireNonNull(builder.fromAddress);
        this.limitFileSizes = builder.limitFileSizes;
        this.fileConstraintSize = builder.fileConstraintSize;
        this.fileConstraintLastModified = builder.fileConstraintLastModified;
        this.priority = builder.priority;
        this.subject = Objects.requireNonNull(builder.subject);
    }

    @Nonnull
    public List<SupportZipBundle> getBundles() {
        return this.bundles;
    }

    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public String getFromAddress() {
        return this.fromAddress;
    }

    @Nonnull
    public String getSubject() {
        return this.subject;
    }

    public int getPriority() {
        return this.priority;
    }

    public Boolean isLimitFileSizes() {
        return this.limitFileSizes;
    }

    public Integer getFileConstraintSize() {
        return this.fileConstraintSize;
    }

    public Integer getFileConstraintLastModified() {
        return this.fileConstraintLastModified;
    }

    @Nonnull
    public SupportZipCreationRequest getSupportZipCreationRequest() {
        return new SupportZipCreationRequest(new SupportZipCreationRequest.Builder().limitFileSizes(this.limitFileSizes).fileConstraintSize(this.fileConstraintSize).fileConstraintLastModified(this.fileConstraintLastModified).bundles(this.bundles));
    }

    public static class Builder {
        private final ImmutableList.Builder<SupportZipBundle> bundles = ImmutableList.builder();
        private Boolean limitFileSizes;
        private Integer fileConstraintSize;
        private Integer fileConstraintLastModified;
        private int priority;
        private String description;
        private String fromAddress;
        private String subject;

        @Nonnull
        public SupportRequestCreationRequest build() {
            return new SupportRequestCreationRequest(this);
        }

        public Builder description(@Nonnull String value) {
            this.description = value;
            return this;
        }

        public Builder fromAddress(@Nonnull String value) {
            Validate.notBlank((CharSequence)value, (String)"fromAddress is required", (Object[])new Object[0]);
            Preconditions.checkArgument((boolean)EmailValidator.isValidEmailAddress(value), (Object)"fromAddress is invalid");
            this.fromAddress = value;
            return this;
        }

        @Nonnull
        public Builder limitFileSizes(Boolean value) {
            this.limitFileSizes = value;
            return this;
        }

        @Nonnull
        public Builder fileConstraintSize(Integer value) {
            this.fileConstraintSize = value;
            return this;
        }

        @Nonnull
        public Builder fileConstraintLastModified(Integer value) {
            this.fileConstraintLastModified = value;
            return this;
        }

        public Builder subject(@Nonnull String value) {
            Preconditions.checkArgument((boolean)StringUtils.isNotBlank((String)value), (Object)"subject is required");
            this.subject = value;
            return this;
        }

        public Builder priority(int value) {
            this.priority = value;
            return this;
        }

        public Builder bundles(@Nonnull Iterable<SupportZipBundle> bundles) {
            this.bundles.addAll(Objects.requireNonNull(bundles));
            return this;
        }
    }
}

