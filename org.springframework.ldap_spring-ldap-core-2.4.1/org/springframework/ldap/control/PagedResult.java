/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.control;

import java.util.List;
import org.springframework.ldap.control.PagedResultsCookie;

public class PagedResult {
    private List<?> resultList;
    private PagedResultsCookie cookie;

    public PagedResult(List<?> resultList, PagedResultsCookie cookie) {
        this.resultList = resultList;
        this.cookie = cookie;
    }

    public PagedResultsCookie getCookie() {
        return this.cookie;
    }

    public List<?> getResultList() {
        return this.resultList;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PagedResult that = (PagedResult)o;
        if (this.cookie != null ? !this.cookie.equals(that.cookie) : that.cookie != null) {
            return false;
        }
        return !(this.resultList != null ? !this.resultList.equals(that.resultList) : that.resultList != null);
    }

    public int hashCode() {
        int result = this.resultList != null ? this.resultList.hashCode() : 0;
        result = 31 * result + (this.cookie != null ? this.cookie.hashCode() : 0);
        return result;
    }
}

