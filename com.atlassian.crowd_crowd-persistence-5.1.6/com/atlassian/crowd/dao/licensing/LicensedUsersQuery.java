/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.dao.licensing;

import com.atlassian.crowd.model.licensing.DirectoryInfo;
import com.atlassian.crowd.model.licensing.LicensingSummary;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class LicensedUsersQuery {
    private LicensingSummary licensingSummary;
    @Nullable
    private String textSearch;
    @Nullable
    private Date lastActiveOnOrBefore;
    private boolean neverLoggedIn;
    @Nullable
    private DirectoryInfo directoryInfo;
    private int start = 0;
    private int limit = -1;

    protected LicensedUsersQuery(LicensingSummary licensingSummary, @Nullable String textSearch, @Nullable Date lastActiveOnOrBefore, boolean neverLoggedIn, @Nullable DirectoryInfo directoryInfo, int start, int limit) {
        this.licensingSummary = Objects.requireNonNull(licensingSummary);
        this.textSearch = textSearch;
        this.lastActiveOnOrBefore = lastActiveOnOrBefore;
        this.directoryInfo = directoryInfo;
        this.neverLoggedIn = neverLoggedIn;
        this.start = start;
        this.limit = limit;
    }

    public LicensingSummary getLicensingSummary() {
        return this.licensingSummary;
    }

    public void setLicensingSummary(LicensingSummary licensingSummary) {
        this.licensingSummary = Objects.requireNonNull(licensingSummary);
    }

    public String getTextSearch() {
        return this.textSearch;
    }

    public boolean isNeverLoggedIn() {
        return this.neverLoggedIn;
    }

    public void setTextSearch(@Nullable String textSearch) {
        this.textSearch = textSearch;
    }

    public Optional<Date> getLastActiveOnOrBefore() {
        return Optional.ofNullable(this.lastActiveOnOrBefore);
    }

    public void setLastActiveOnOrBefore(@Nullable Date lastActiveOnOrBefore) {
        this.lastActiveOnOrBefore = lastActiveOnOrBefore;
    }

    public Optional<DirectoryInfo> getDirectoryInfo() {
        return Optional.ofNullable(this.directoryInfo);
    }

    public void setDirectoryInfo(@Nullable DirectoryInfo directoryInfo) {
        this.directoryInfo = directoryInfo;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LicensedUsersQuery data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LicensedUsersQuery that = (LicensedUsersQuery)o;
        return Objects.equals(this.getLicensingSummary(), that.getLicensingSummary()) && Objects.equals(this.getTextSearch(), that.getTextSearch()) && Objects.equals(this.getLastActiveOnOrBefore(), that.getLastActiveOnOrBefore()) && Objects.equals(this.getDirectoryInfo(), that.getDirectoryInfo()) && Objects.equals(this.getStart(), that.getStart()) && Objects.equals(this.getLimit(), that.getLimit());
    }

    public int hashCode() {
        return Objects.hash(this.getLicensingSummary(), this.getTextSearch(), this.getLastActiveOnOrBefore(), this.getDirectoryInfo(), this.getStart(), this.getLimit());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("licensingSummary", (Object)this.getLicensingSummary()).add("textSearch", (Object)this.getTextSearch()).add("lastActiveOnOrBefore", this.getLastActiveOnOrBefore()).add("directoryInfo", this.getDirectoryInfo()).add("start", this.getStart()).add("limit", this.getLimit()).toString();
    }

    public static final class Builder {
        private LicensingSummary licensingSummary;
        private String textSearch;
        private Date lastActiveOnOrBefore;
        private DirectoryInfo directoryInfo;
        private boolean neverLoggedIn;
        private int start;
        private int limit;

        private Builder() {
        }

        private Builder(LicensedUsersQuery initialData) {
            this.licensingSummary = initialData.getLicensingSummary();
            this.textSearch = initialData.getTextSearch();
            this.lastActiveOnOrBefore = initialData.getLastActiveOnOrBefore().orElse(null);
            this.directoryInfo = initialData.getDirectoryInfo().orElse(null);
            this.start = initialData.getStart();
            this.limit = initialData.getLimit();
        }

        public Builder setLicensingSummary(LicensingSummary licensingSummary) {
            this.licensingSummary = licensingSummary;
            return this;
        }

        public Builder setTextSearch(@Nullable String textSearch) {
            this.textSearch = textSearch;
            return this;
        }

        public Builder setNeverLoggedIn(boolean neverLoggedIn) {
            this.neverLoggedIn = neverLoggedIn;
            return this;
        }

        public Builder setLastActiveOnOrBefore(@Nullable Date lastActiveOnOrBefore) {
            this.lastActiveOnOrBefore = lastActiveOnOrBefore;
            return this;
        }

        public Builder setDirectoryInfo(@Nullable DirectoryInfo directoryInfo) {
            this.directoryInfo = directoryInfo;
            return this;
        }

        public Builder setStart(int start) {
            this.start = start;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public LicensedUsersQuery build() {
            return new LicensedUsersQuery(this.licensingSummary, this.textSearch, this.lastActiveOnOrBefore, this.neverLoggedIn, this.directoryInfo, this.start, this.limit);
        }
    }
}

