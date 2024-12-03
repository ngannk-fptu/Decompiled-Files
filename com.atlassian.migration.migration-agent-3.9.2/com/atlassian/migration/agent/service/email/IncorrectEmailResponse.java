/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.service.email.IncorrectEmailDTO;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class IncorrectEmailResponse {
    @JsonProperty
    private final String scanId;
    @JsonProperty
    private final int page;
    @JsonProperty
    private final int perPage;
    @JsonProperty
    private final long pagesCount;
    @JsonProperty
    private final long totalCount;
    @JsonProperty
    private final List<IncorrectEmailDTO> data;

    public IncorrectEmailResponse(String scanId, int page, int perPage, long totalCount, List<IncorrectEmailDTO> data) {
        this.scanId = scanId;
        this.page = page;
        this.perPage = perPage;
        this.pagesCount = this.calculatePagesCount(perPage, totalCount);
        this.totalCount = totalCount;
        this.data = data;
    }

    private long calculatePagesCount(int limit, long count) {
        return (long)Math.ceil((double)count / (double)limit);
    }

    @Generated
    public String getScanId() {
        return this.scanId;
    }

    @Generated
    public int getPage() {
        return this.page;
    }

    @Generated
    public int getPerPage() {
        return this.perPage;
    }

    @Generated
    public long getPagesCount() {
        return this.pagesCount;
    }

    @Generated
    public long getTotalCount() {
        return this.totalCount;
    }

    @Generated
    public List<IncorrectEmailDTO> getData() {
        return this.data;
    }
}

