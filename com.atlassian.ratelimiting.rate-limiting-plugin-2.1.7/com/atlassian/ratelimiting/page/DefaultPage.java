/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.math.IntMath
 */
package com.atlassian.ratelimiting.page;

import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultPage<T>
implements Page<T> {
    private final List<T> content;
    private final PageRequest pageRequest;
    private final int totalElements;

    public DefaultPage(List<T> content, PageRequest pageRequest, int totalElements) {
        Preconditions.checkArgument((content.size() <= pageRequest.getSize() ? 1 : 0) != 0, (Object)("Page should not contain more elements than requested (requested " + pageRequest.getSize() + ", found " + content.size() + ")"));
        this.content = content;
        this.pageRequest = pageRequest;
        this.totalElements = totalElements;
    }

    @Override
    public int getPageNumber() {
        return this.pageRequest.getPage();
    }

    @Override
    public int getPageSize() {
        return this.pageRequest.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return this.content.size();
    }

    @Override
    public int getTotalPages() {
        return IntMath.divide((int)this.totalElements, (int)this.pageRequest.getSize(), (RoundingMode)RoundingMode.CEILING);
    }

    @Override
    public boolean isFirst() {
        return this.pageRequest.getPage() == 0;
    }

    @Override
    public boolean isLast() {
        return this.pageRequest.getPage() >= this.getTotalPages() - 1;
    }

    @Override
    public PageRequest nextPageRequest() {
        return this.isLast() ? this.pageRequest : this.pageRequest.next();
    }

    @Override
    public PageRequest previousPageRequest() {
        return this.isFirst() ? this.pageRequest : this.pageRequest.previous();
    }

    @Override
    public <E> Page<E> map(Function<T, E> mappingFunction) {
        List mappedContent = this.content.stream().map(mappingFunction).collect(Collectors.toList());
        return new DefaultPage(mappedContent, this.pageRequest, this.totalElements);
    }

    @Override
    public Page<T> filter(Predicate<? super T> predicate) {
        List filteredContent = this.content.stream().filter(predicate).collect(Collectors.toList());
        int totalElements = this.totalElements - (this.content.size() - filteredContent.size());
        return new DefaultPage(filteredContent, this.pageRequest, totalElements);
    }

    @Override
    public List<T> getContent() {
        return this.content;
    }

    @Override
    public PageRequest getPageRequest() {
        return this.pageRequest;
    }

    @Override
    public int getTotalElements() {
        return this.totalElements;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DefaultPage)) {
            return false;
        }
        DefaultPage other = (DefaultPage)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTotalElements() != other.getTotalElements()) {
            return false;
        }
        List<T> this$content = this.getContent();
        List<T> other$content = other.getContent();
        if (this$content == null ? other$content != null : !((Object)this$content).equals(other$content)) {
            return false;
        }
        PageRequest this$pageRequest = this.getPageRequest();
        PageRequest other$pageRequest = other.getPageRequest();
        return !(this$pageRequest == null ? other$pageRequest != null : !((Object)this$pageRequest).equals(other$pageRequest));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DefaultPage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTotalElements();
        List<T> $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : ((Object)$content).hashCode());
        PageRequest $pageRequest = this.getPageRequest();
        result = result * 59 + ($pageRequest == null ? 43 : ((Object)$pageRequest).hashCode());
        return result;
    }

    public String toString() {
        return "DefaultPage(content=" + this.getContent() + ", pageRequest=" + this.getPageRequest() + ", totalElements=" + this.getTotalElements() + ")";
    }
}

