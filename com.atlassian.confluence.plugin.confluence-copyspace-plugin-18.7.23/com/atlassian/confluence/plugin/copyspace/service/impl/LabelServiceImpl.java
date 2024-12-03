/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.labels.SpaceLabelManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.LabelService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="labelServiceImpl")
public class LabelServiceImpl
implements LabelService {
    private static final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);
    private final ContentLabelService contentLabelService;
    private final LabelManager labelManager;
    private final SpaceLabelManager spaceLabelManager;

    @Autowired
    public LabelServiceImpl(@ComponentImport(value="apiContentLabelService") ContentLabelService contentLabelService, @ComponentImport LabelManager labelManager, @ComponentImport SpaceLabelManager spaceLabelManager) {
        this.contentLabelService = contentLabelService;
        this.labelManager = labelManager;
        this.spaceLabelManager = spaceLabelManager;
    }

    @Override
    public void copyBlogPostLabels(long originalBlogPostId, BlogPost copy) {
        int offset = 0;
        boolean hasMoreLabels = false;
        do {
            List<Object> originalLabels;
            if ((originalLabels = this.findBatchedLabelsByBlogPostId(originalBlogPostId, offset)).size() == 0) {
                return;
            }
            if (originalLabels.size() > 99) {
                hasMoreLabels = true;
                originalLabels = originalLabels.stream().limit(99L).collect(Collectors.toList());
            }
            this.contentLabelService.addLabels(ContentId.of((long)copy.getId()), originalLabels);
            log.debug("Copy labels operation. Labels batch from {} to {} successfully copied.", (Object)(offset + 1), (Object)(offset + originalLabels.size()));
            offset += 99;
        } while (hasMoreLabels);
    }

    private List<com.atlassian.confluence.api.model.content.Label> findBatchedLabelsByBlogPostId(long originalBlogPostId, int offset) {
        List<Label.Prefix> labelTypes = Arrays.asList(Label.Prefix.system, Label.Prefix.global, Label.Prefix.team);
        return this.contentLabelService.getLabels(ContentId.of((long)originalBlogPostId), labelTypes, (PageRequest)new SimplePageRequest(offset, 100)).getResults();
    }

    @Override
    public void copyAttachmentLabels(List<Attachment> originalAttachments, List<Attachment> targetAttachments, CopySpaceContext context) {
        if (context.isCopyLabels() && context.isCopyAttachments()) {
            targetAttachments.forEach(attachment -> {
                Optional<Attachment> originalAttachment = originalAttachments.stream().filter(a -> a.getFileName().equals(attachment.getFileName())).findFirst();
                if (originalAttachment.isPresent()) {
                    List labels = originalAttachment.get().getLabels();
                    labels.forEach(label -> this.addLabel((Labelable)attachment, (Label)label));
                }
            });
        }
    }

    @Override
    public void addSpaceLabel(Space space, Label label) {
        if (!Namespace.isPersonal((Label)label)) {
            this.spaceLabelManager.addLabel(space, label.getName());
        }
    }

    @Override
    public void addLabel(Labelable labelable, Label label) {
        if (!Namespace.isPersonal((Label)label)) {
            this.labelManager.addLabel(labelable, label);
        }
    }
}

