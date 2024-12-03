/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.user.PersonalInformation;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class ConfluenceAttachmentMetadataExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (!(searchable instanceof Attachment)) {
            return resultBuilder.build();
        }
        Attachment attachment = (Attachment)searchable;
        ContentEntityObject attachmentContainer = attachment.getContainer();
        resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_FILE_SIZE.createField(attachment.getFileSize()));
        resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_FILE_NAME_UNTOKENIZED.createField(attachment.getFileName()));
        resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_FILE_EXTENSION.createField(attachment.getFileExtension()));
        if (attachmentContainer != null) {
            resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_TYPE.createField(attachmentContainer.getType()));
            resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_ID.createField(attachmentContainer.getIdAsString()));
            resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_URL_PATH.createField(attachmentContainer.getUrlPath()));
            if (attachmentContainer instanceof PersonalInformation) {
                PersonalInformation personalInformation = (PersonalInformation)attachmentContainer;
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_USERNAME.createField(personalInformation.getUsername()));
            }
            if (attachmentContainer.getDisplayTitle() != null) {
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_OWNER_REAL_TITLE.createField(attachmentContainer.getDisplayTitle()));
            }
        }
        return resultBuilder.build();
    }
}

