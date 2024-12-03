/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.joda.time.DateTime
 *  org.joda.time.Interval
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.query.TermRangeQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class DateRangeQuery
implements SearchQuery {
    private static final String KEY = "dateRange";
    private final DateRange dateRange;
    private final String fieldName;

    public static Builder newDateRangeQuery(DateRangeQueryType type) {
        return new Builder().queryType(type);
    }

    public static Builder newDateRangeQuery(String fieldName) {
        return new Builder().fieldName(fieldName);
    }

    public DateRangeQuery(@Nullable Date from, @Nullable Date to, boolean includeFrom, boolean includeTo, DateRangeQueryType dateRangeQueryType) {
        this(new DateRange(from, to, includeFrom, includeTo), dateRangeQueryType);
    }

    public DateRangeQuery(Date fromDate, Date toDate, boolean includeFrom, boolean includeTo, String fieldName) {
        this.dateRange = new DateRange(fromDate, toDate, includeFrom, includeTo);
        this.fieldName = fieldName;
    }

    public DateRangeQuery(DateRange dateRange, DateRangeQueryType dateRangeQueryType) {
        this.dateRange = dateRange;
        this.fieldName = dateRangeQueryType.getFieldName();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.dateRange.getFrom(), this.dateRange.getTo(), this.dateRange.isIncludeFrom(), this.dateRange.isIncludeTo(), this.fieldName);
    }

    public Date getFromDate() {
        return this.dateRange.getFrom();
    }

    public Date getToDate() {
        return this.dateRange.getTo();
    }

    public boolean isIncludeFrom() {
        return this.dateRange.isIncludeFrom();
    }

    public boolean isIncludeTo() {
        return this.dateRange.isIncludeTo();
    }

    public Optional<DateRangeQueryType> queryType() {
        if (this.fieldName.equals(DateRangeQueryType.MODIFIED.getFieldName())) {
            return Optional.of(DateRangeQueryType.MODIFIED);
        }
        if (this.fieldName.equals(DateRangeQueryType.CREATED.getFieldName())) {
            return Optional.of(DateRangeQueryType.CREATED);
        }
        return Optional.empty();
    }

    public Optional<String> fieldName() {
        return Optional.ofNullable(this.fieldName);
    }

    @Override
    public SearchQuery expand() {
        String lowerBound = this.getFromDate() != null ? LuceneUtils.dateToString(this.getFromDate()) : null;
        String upperBound = this.getToDate() != null ? LuceneUtils.dateToString(this.getToDate()) : null;
        return new TermRangeQuery(this.fieldName, lowerBound, upperBound, this.isIncludeFrom(), this.isIncludeTo());
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        DateRangeQuery other = (DateRangeQuery)obj;
        return new EqualsBuilder().append((Object)this.fieldName, (Object)other.fieldName).append((Object)this.dateRange, (Object)other.dateRange).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(111, 37).append((Object)this.fieldName).append((Object)this.dateRange).toHashCode();
    }

    public static class Builder {
        private DateRangeQueryType queryType;
        private String fieldName;
        private Date fromDate;
        private Date toDate;
        private Boolean includeFrom;
        private Boolean includeTo;

        public Builder queryType(DateRangeQueryType queryType) {
            if (this.fieldName != null) {
                throw new IllegalStateException("Cannot set both queryType and fieldName");
            }
            this.queryType = queryType;
            return this;
        }

        public Builder fromDate(@Nullable Date fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        @Deprecated(forRemoval=true)
        public Builder fromDate(@Nullable DateTime fromDate) {
            if (fromDate == null) {
                return this.fromDate((Date)null);
            }
            return this.fromDate(fromDate.toDate());
        }

        public Builder toDate(@Nullable Date toDate) {
            this.toDate = toDate;
            return this;
        }

        @Deprecated(forRemoval=true)
        public Builder toDate(@Nullable DateTime toDate) {
            if (toDate == null) {
                return this.toDate((Date)null);
            }
            return this.toDate(toDate.toDate());
        }

        public Builder includeFrom(boolean includeFrom) {
            this.includeFrom = includeFrom;
            return this;
        }

        public Builder includeTo(boolean includeTo) {
            this.includeTo = includeTo;
            return this;
        }

        @Deprecated(forRemoval=true)
        public Builder interval(Interval interval) {
            this.fromDate = interval.getStart().toDate();
            this.toDate = interval.getEnd().toDate();
            this.includeFrom = true;
            this.includeTo = false;
            return this;
        }

        public Builder fieldName(String fieldName) {
            if (this.queryType != null) {
                throw new IllegalStateException("Cannot set both queryType and fieldName");
            }
            this.fieldName = fieldName;
            return this;
        }

        public DateRangeQuery build() {
            if (this.queryType != null) {
                return new DateRangeQuery(this.fromDate, this.toDate, (boolean)this.includeFrom, (boolean)this.includeTo, this.queryType);
            }
            return new DateRangeQuery(this.fromDate, this.toDate, (boolean)this.includeFrom, (boolean)this.includeTo, this.fieldName);
        }
    }

    @Deprecated
    public static class DateRange
    extends Range<Date> {
        public DateRange(@Nullable Date from, @Nullable Date to, boolean includeFrom, boolean includeTo) {
            super(from, to, includeFrom, includeTo);
        }

        @Override
        public Date getFrom() {
            if (super.getFrom() == null) {
                return null;
            }
            return new Date(((Date)super.getFrom()).getTime());
        }

        @Override
        public Date getTo() {
            if (super.getTo() == null) {
                return null;
            }
            return new Date(((Date)super.getTo()).getTime());
        }
    }

    public static enum DateRangeQueryType {
        MODIFIED(SearchFieldNames.LAST_MODIFICATION_DATE),
        CREATED(SearchFieldNames.CREATION_DATE);

        private final String fieldName;

        private DateRangeQueryType(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return this.fieldName;
        }
    }
}

