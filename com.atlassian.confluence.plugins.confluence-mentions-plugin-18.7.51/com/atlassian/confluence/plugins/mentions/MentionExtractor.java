/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.mentions.api.MentionFinder;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class MentionExtractor
implements Extractor2 {
    private static final String FIELD_NAME = "mentions";
    private final MentionFinder mentionFinder;

    public MentionExtractor(MentionFinder mentionFinder) {
        this.mentionFinder = mentionFinder;
    }

    public StringBuilder extractText(Object searchable) {
        return null;
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        ContentEntityObject ceo = (ContentEntityObject)searchable;
        Set<String> mentions = this.mentionFinder.getMentionedUsernames(ceo);
        return mentions.stream().map(mention -> new FieldDescriptor(FIELD_NAME, mention, FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED)).collect(Collectors.toList());
    }
}

