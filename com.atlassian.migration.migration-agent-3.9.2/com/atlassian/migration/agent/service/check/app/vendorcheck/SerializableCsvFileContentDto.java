/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import lombok.Generated;

public class SerializableCsvFileContentDto
implements Serializable {
    private static final long serialVersionUID = -5757690071917118100L;
    @JsonIgnore
    public final List<String> columnHeaders;
    @JsonIgnore
    public final List<List<String>> rows;

    public SerializableCsvFileContentDto(List<String> columnHeaders, List<List<String>> rows) {
        this.columnHeaders = columnHeaders;
        this.rows = rows;
    }

    @Generated
    public List<String> getColumnHeaders() {
        return this.columnHeaders;
    }

    @Generated
    public List<List<String>> getRows() {
        return this.rows;
    }
}

