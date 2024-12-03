/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang.builder.CompareToBuilder
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@ParametersAreNonnullByDefault
class Release
implements Comparable<Release> {
    private static final DateFormat DATE_FORMAT;
    private final String version;
    private final Date releaseDate;
    @Nullable
    private Date overriddenEOLDate;

    public Release(String version, Date releaseDate, @Nullable Date overriddenEOLDate) {
        this.version = version;
        this.releaseDate = Release.justTheDate(releaseDate);
        this.overriddenEOLDate = overriddenEOLDate == null ? null : Release.justTheDate(overriddenEOLDate);
    }

    public Release(String version, Date releaseDate) {
        this(version, releaseDate, null);
    }

    private static Date justTheDate(Date releaseDate) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(releaseDate);
        c.clear(10);
        return c.getTime();
    }

    @Nonnull
    public String getVersion() {
        return this.version;
    }

    @Nonnull
    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public Date getOverriddenEOLDate() {
        return this.overriddenEOLDate;
    }

    public void setOverriddenEOLDate(Date overriddenEOLDate) {
        this.overriddenEOLDate = overriddenEOLDate;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Release release = (Release)o;
        return new EqualsBuilder().append((Object)this.version, (Object)release.version).append((Object)this.releaseDate, (Object)release.releaseDate).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.version).append((Object)this.releaseDate).toHashCode();
    }

    public String toString() {
        String overriddenEOLDateString = this.overriddenEOLDate == null ? "" : ", overriddenEOLDate=" + DATE_FORMAT.format(this.overriddenEOLDate);
        return "Release{version='" + this.version + '\'' + ", releaseDate=" + DATE_FORMAT.format(this.releaseDate) + overriddenEOLDateString + '}';
    }

    @Override
    public int compareTo(Release o) {
        return new CompareToBuilder().append((Object)this.version, (Object)o.version).append((Object)this.releaseDate, (Object)o.releaseDate).toComparison();
    }

    static {
        Locale auLocale = new Locale("en", "AU");
        DateFormatSymbols symbols = new DateFormatSymbols(auLocale);
        symbols.setAmPmStrings(new String[]{"AM", "PM"});
        DATE_FORMAT = new SimpleDateFormat("d/M/yy h:mm a", symbols);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}

