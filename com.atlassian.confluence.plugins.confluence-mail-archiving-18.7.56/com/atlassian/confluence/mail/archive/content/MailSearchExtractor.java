/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.TextFieldDescriptor
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.mail.archive.content.ContentBackedMail;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class MailSearchExtractor
implements Extractor2 {
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof CustomContentEntityObject) {
            CustomContentEntityObject contentEntityObject = (CustomContentEntityObject)searchable;
            if (!ContentBackedMail.isMailContentEntity(contentEntityObject)) {
                return resultBuilder.build();
            }
            ContentBackedMail mail = ContentBackedMail.newInstance(contentEntityObject);
            resultBuilder.add((Object)new StringFieldDescriptor("canonicalsubject", mail.getCanonicalSubject(), FieldDescriptor.Store.YES));
            resultBuilder.add((Object)new StringFieldDescriptor("messageid", mail.getMessageId(), FieldDescriptor.Store.YES));
            resultBuilder.add((Object)new StringFieldDescriptor("inreplyto", mail.getInReplyTo(), FieldDescriptor.Store.YES));
            mail.getReferences().forEach(ref -> resultBuilder.add((Object)new StringFieldDescriptor("references", ref, FieldDescriptor.Store.YES)));
            resultBuilder.add((Object)new TextFieldDescriptor("from", mail.getFrom().toString(), FieldDescriptor.Store.NO));
            mail.getRecipients().forEach(recipient -> resultBuilder.add((Object)new TextFieldDescriptor("recipients", recipient.toString(), FieldDescriptor.Store.NO)));
        }
        return resultBuilder.build();
    }
}

