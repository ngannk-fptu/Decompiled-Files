/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.AttachmentMetadataService;
import com.atlassian.confluence.plugin.copyspace.util.Constants;
import com.atlassian.confluence.plugin.copyspace.util.MetadataCopier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="metadataCopyHandler")
public class MetadataCopyHandler
implements CopyHandler {
    private final PageManager pageManager;
    private final AttachmentMetadataService attachmentMetadataService;

    @Autowired
    public MetadataCopyHandler(@ComponentImport PageManager pageManager, AttachmentMetadataService attachmentMetadataService) {
        this.pageManager = pageManager;
        this.attachmentMetadataService = attachmentMetadataService;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        if (!context.isCopyMetadata()) {
            return;
        }
        Page oldPage = event.getOrigin();
        Page pageToCopy = event.getDestination();
        if (context.isCopyAttachments()) {
            this.attachmentMetadataService.preserveMetadata((ContentEntityObject)oldPage, (ContentEntityObject)pageToCopy);
        }
        MetadataCopier.copyEntityMetadata((ConfluenceEntityObject)oldPage, (ConfluenceEntityObject)pageToCopy);
        pageToCopy.setSynchronyRevisionSource("restored");
        this.pageManager.saveContentEntity((ContentEntityObject)pageToCopy, Constants.SUPPRESS_EVENT_KEEP_LAST_MODIFIER);
    }
}

