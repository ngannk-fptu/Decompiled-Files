/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.ui.WhitelistBean;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
@Immutable
public class WhitelistListResponseBean {
    private final List<WhitelistBean> rules;
    private final int page;
    private final int totalPages;

    @JsonCreator
    public WhitelistListResponseBean(@JsonProperty(value="rules") List<WhitelistBean> rules, @JsonProperty(value="page") int page, @JsonProperty(value="totalPages") int totalPages) {
        this.rules = new ArrayList<WhitelistBean>(rules);
        this.page = page;
        this.totalPages = totalPages;
    }

    public List<WhitelistBean> getRules() {
        return this.rules;
    }

    public int getPage() {
        return this.page;
    }

    public int getTotalPages() {
        return this.totalPages;
    }
}

