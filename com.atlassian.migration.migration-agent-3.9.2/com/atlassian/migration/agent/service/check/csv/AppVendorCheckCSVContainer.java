/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import java.util.Collection;
import java.util.List;

public class AppVendorCheckCSVContainer {
    private String[] headers;
    private Collection<List<String>> beans;

    public AppVendorCheckCSVContainer(String[] headers, Collection<List<String>> beans) {
        this.headers = headers;
        this.beans = beans;
    }

    public String[] headers() {
        return this.headers;
    }

    public Collection<List<String>> beans() {
        return this.beans;
    }
}

