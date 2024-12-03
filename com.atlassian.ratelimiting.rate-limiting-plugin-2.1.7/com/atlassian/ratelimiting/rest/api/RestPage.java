/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.page.Page;
import java.util.List;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestPage<T> {
    private List<T> values;
    private Integer page;
    private Integer size;
    private Boolean isLastPage;
    private Integer totalPages;

    public RestPage(Page<T> page) {
        this.values = page.getContent();
        this.page = page.getPageNumber();
        this.size = page.getPageSize();
        this.isLastPage = page.isLast();
        this.totalPages = page.getTotalPages();
    }

    public List<T> getValues() {
        return this.values;
    }

    public Integer getPage() {
        return this.page;
    }

    public Integer getSize() {
        return this.size;
    }

    public Boolean getIsLastPage() {
        return this.isLastPage;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setIsLastPage(Boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestPage)) {
            return false;
        }
        RestPage other = (RestPage)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$page = this.getPage();
        Integer other$page = other.getPage();
        if (this$page == null ? other$page != null : !((Object)this$page).equals(other$page)) {
            return false;
        }
        Integer this$size = this.getSize();
        Integer other$size = other.getSize();
        if (this$size == null ? other$size != null : !((Object)this$size).equals(other$size)) {
            return false;
        }
        Boolean this$isLastPage = this.getIsLastPage();
        Boolean other$isLastPage = other.getIsLastPage();
        if (this$isLastPage == null ? other$isLastPage != null : !((Object)this$isLastPage).equals(other$isLastPage)) {
            return false;
        }
        Integer this$totalPages = this.getTotalPages();
        Integer other$totalPages = other.getTotalPages();
        if (this$totalPages == null ? other$totalPages != null : !((Object)this$totalPages).equals(other$totalPages)) {
            return false;
        }
        List<T> this$values = this.getValues();
        List<T> other$values = other.getValues();
        return !(this$values == null ? other$values != null : !((Object)this$values).equals(other$values));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestPage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $page = this.getPage();
        result = result * 59 + ($page == null ? 43 : ((Object)$page).hashCode());
        Integer $size = this.getSize();
        result = result * 59 + ($size == null ? 43 : ((Object)$size).hashCode());
        Boolean $isLastPage = this.getIsLastPage();
        result = result * 59 + ($isLastPage == null ? 43 : ((Object)$isLastPage).hashCode());
        Integer $totalPages = this.getTotalPages();
        result = result * 59 + ($totalPages == null ? 43 : ((Object)$totalPages).hashCode());
        List<T> $values = this.getValues();
        result = result * 59 + ($values == null ? 43 : ((Object)$values).hashCode());
        return result;
    }

    public String toString() {
        return "RestPage(values=" + this.getValues() + ", page=" + this.getPage() + ", size=" + this.getSize() + ", isLastPage=" + this.getIsLastPage() + ", totalPages=" + this.getTotalPages() + ")";
    }

    public RestPage() {
    }

    public RestPage(List<T> values, Integer page, Integer size, Boolean isLastPage, Integer totalPages) {
        this.values = values;
        this.page = page;
        this.size = size;
        this.isLastPage = isLastPage;
        this.totalPages = totalPages;
    }
}

