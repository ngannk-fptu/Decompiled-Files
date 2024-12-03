/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.LabelService;
import org.springframework.stereotype.Component;

@Component(value="attachmentLabelsCopyHandler")
public class AttachmentLabelsCopyHandler
implements CopyHandler {
    private final LabelService labelService;

    public AttachmentLabelsCopyHandler(LabelService labelService) {
        this.labelService = labelService;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        this.labelService.copyAttachmentLabels(event.getOrigin().getAttachments(), event.getDestination().getAttachments(), context);
    }
}

