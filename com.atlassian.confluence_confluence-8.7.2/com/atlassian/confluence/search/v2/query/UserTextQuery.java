/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UserTextQuery
implements SearchQuery {
    public static final String KEY = "userText";
    private final String queryString;

    public UserTextQuery(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.singletonList(this.queryString);
    }

    public String getQueryString() {
        return this.queryString;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserTextQuery)) {
            return false;
        }
        UserTextQuery that = (UserTextQuery)o;
        return Objects.equals(this.getQueryString(), that.getQueryString());
    }

    public int hashCode() {
        return Objects.hash(this.getQueryString());
    }
}

