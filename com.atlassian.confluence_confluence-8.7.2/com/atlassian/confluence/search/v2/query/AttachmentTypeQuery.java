/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AbstractParameterListQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AttachmentTypeQuery
extends AbstractParameterListQuery<Attachment.Type> {
    private static final String KEY = "attachmentType";

    public AttachmentTypeQuery(Attachment.Type fileType) {
        super(fileType);
    }

    public AttachmentTypeQuery(Set<Attachment.Type> fileTypes) {
        super(fileTypes);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchQuery expand() {
        List<Attachment.Type> list = this.getParameters();
        if (list.size() == 1) {
            return this.makeSingleQuery(list.get(0));
        }
        return this.makeBooleanQuery(list);
    }

    private SearchQuery makeBooleanQuery(List<Attachment.Type> properties) {
        return (SearchQuery)BooleanQuery.builder().addShould(properties.stream().map(this::makeSingleQuery).collect(Collectors.toList())).build();
    }

    private SearchQuery makeSingleQuery(Attachment.Type type) {
        return new TermQuery(SearchFieldNames.ATTACHMENT_NICE_TYPE, type.getDescription());
    }
}

