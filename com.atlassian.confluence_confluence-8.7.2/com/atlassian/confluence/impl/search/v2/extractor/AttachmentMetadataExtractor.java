/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class AttachmentMetadataExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        StringBuilder resultBuilder = new StringBuilder();
        if (searchable instanceof Attachment) {
            Attachment attachment = (Attachment)searchable;
            resultBuilder.append(attachment.getVersionComment());
        }
        return resultBuilder;
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof Attachment) {
            Attachment attachment = (Attachment)searchable;
            Optional.ofNullable(attachment.getContainer()).ifPresent(container -> {
                if (!StringUtils.isEmpty((CharSequence)attachment.getContainer().getTitle())) {
                    resultBuilder.add((Object)SearchFieldMappings.PARENT_TITLE_UNSTEMMED.createField(attachment.getContainer().getTitle()));
                }
            });
            if (!StringUtils.isEmpty((CharSequence)attachment.getFileName())) {
                resultBuilder.add((Object)SearchFieldMappings.CONTENT_NAME_UNSTEMMED.createField(attachment.getFileName()));
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_FILE_NAME.createField(attachment.getFileName()));
            }
            if (!StringUtils.isEmpty((CharSequence)attachment.getVersionComment())) {
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_VERSION_COMMENT.createField(attachment.getVersionComment()));
            }
            if (!StringUtils.isEmpty((CharSequence)attachment.getNiceType())) {
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_NICE_TYPE.createField(attachment.getNiceType()));
            }
            if (!StringUtils.isEmpty((CharSequence)attachment.getDownloadPath())) {
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_DOWNLOAD_PATH.createField(attachment.getDownloadPath()));
            }
            if (!StringUtils.isEmpty((CharSequence)attachment.getNiceFileSize())) {
                resultBuilder.add((Object)SearchFieldMappings.ATTACHMENT_NICE_FILE_SIZE.createField(attachment.getNiceFileSize()));
            }
        }
        return resultBuilder.build();
    }
}

