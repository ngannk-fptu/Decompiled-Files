/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.license.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class RestPage<T> {
    @JsonProperty(value="start")
    private int start;
    @JsonProperty(value="limit")
    private int limit;
    @JsonProperty(value="isLastPage")
    private boolean isLastPage;
    @JsonProperty(value="values")
    private List<T> values;

    public static <T> RestPage<T> fromLimitPlusOne(List<T> results, int start, int limit) {
        boolean lastPage = results.size() <= limit;
        return RestPage.builder().setIsLastPage(lastPage).setStart(start).setLimit(limit).setValues(lastPage ? results : results.subList(0, limit)).build();
    }

    @JsonCreator
    public RestPage(@JsonProperty(value="start") int start, @JsonProperty(value="limit") int limit, @JsonProperty(value="isLastPage") boolean isLastPage, @JsonProperty(value="values") List<T> values) {
        this.start = start;
        this.limit = limit;
        this.isLastPage = isLastPage;
        this.values = values != null ? new ArrayList<T>(values) : null;
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

    public boolean getIsLastPage() {
        return this.isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public List<T> getValues() {
        return this.values;
    }

    public void setValues(List<T> values) {
        this.values = values != null ? new ArrayList<T>(values) : null;
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public static <T> Builder<T> builder(RestPage<T> data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestPage that = (RestPage)o;
        return Objects.equals(this.getStart(), that.getStart()) && Objects.equals(this.getLimit(), that.getLimit()) && Objects.equals(this.getIsLastPage(), that.getIsLastPage()) && Objects.equals(this.getValues(), that.getValues());
    }

    public int hashCode() {
        return Objects.hash(this.getStart(), this.getLimit(), this.getIsLastPage(), this.getValues());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("start=" + this.getStart()).add("limit=" + this.getLimit()).add("isLastPage=" + this.getIsLastPage()).add("values=" + this.getValues()).toString();
    }

    public static final class Builder<T> {
        private int start;
        private int limit;
        private boolean isLastPage;
        private List<T> values = new ArrayList<T>();

        private Builder() {
        }

        private Builder(RestPage<T> initialData) {
            this.start = initialData.getStart();
            this.limit = initialData.getLimit();
            this.isLastPage = initialData.getIsLastPage();
            this.values = new ArrayList<T>(initialData.getValues());
        }

        public Builder<T> setStart(int start) {
            this.start = start;
            return this;
        }

        public Builder<T> setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder<T> setIsLastPage(boolean isLastPage) {
            this.isLastPage = isLastPage;
            return this;
        }

        public Builder<T> setValues(List<T> values) {
            this.values = values;
            return this;
        }

        public Builder<T> addValue(T value) {
            this.values.add(value);
            return this;
        }

        public Builder<T> addValues(Iterable<T> values) {
            for (T value : values) {
                this.addValue(value);
            }
            return this;
        }

        public RestPage<T> build() {
            return new RestPage<T>(this.start, this.limit, this.isLastPage, this.values);
        }
    }
}

