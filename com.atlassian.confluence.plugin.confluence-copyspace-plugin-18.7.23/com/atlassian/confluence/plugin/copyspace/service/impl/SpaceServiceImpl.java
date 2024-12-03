/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.themes.CustomLayoutManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.LabelService;
import com.atlassian.confluence.plugin.copyspace.service.LogoCopier;
import com.atlassian.confluence.plugin.copyspace.service.LookAndFeelCopier;
import com.atlassian.confluence.plugin.copyspace.service.SpaceService;
import com.atlassian.confluence.plugin.copyspace.service.WatcherService;
import com.atlassian.confluence.plugin.copyspace.util.MetadataCopier;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.themes.CustomLayoutManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="spaceServiceImpl")
public class SpaceServiceImpl
implements SpaceService {
    private static final Logger log = LoggerFactory.getLogger(SpaceServiceImpl.class);
    private final SpaceManager spaceManager;
    private final TransactionTemplate transactionTemplate;
    private final LabelService labelService;
    private final LogoCopier logoCopier;
    private final LookAndFeelCopier lookAndFeelCopier;
    private final WatcherService watcherService;
    private final CustomLayoutManager customLayoutManager;

    @Autowired
    public SpaceServiceImpl(@ComponentImport SpaceManager spaceManager, @ComponentImport TransactionTemplate transactionTemplate, LabelService labelService, LogoCopier logoCopier, LookAndFeelCopier lookAndFeelCopier, WatcherService watcherService, @ComponentImport CustomLayoutManager customLayoutManager) {
        this.spaceManager = spaceManager;
        this.transactionTemplate = transactionTemplate;
        this.labelService = labelService;
        this.logoCopier = logoCopier;
        this.lookAndFeelCopier = lookAndFeelCopier;
        this.watcherService = watcherService;
        this.customLayoutManager = customLayoutManager;
    }

    @Override
    public Space createNewSpace(CopySpaceContext context) {
        return (Space)this.transactionTemplate.execute(() -> {
            Space originalSpace = this.spaceManager.getSpace(context.getOriginalSpaceKey());
            Space newSpace = this.spaceManager.createPrivateSpace(context.getTargetSpaceKey(), context.getTargetSpaceName(), context.getTargetSpaceDescription(), (User)AuthenticatedUserThreadLocal.get());
            newSpace.setSpaceStatus(originalSpace.getSpaceStatus());
            boolean keepMetadata = context.isCopyMetadata();
            if (keepMetadata) {
                MetadataCopier.copyEntityMetadata((ConfluenceEntityObject)originalSpace, (ConfluenceEntityObject)newSpace);
                newSpace.setClock(() -> ((Space)originalSpace).getLastModificationDate());
            }
            if (!context.isCopyPages()) {
                newSpace.getHomePage().setSpace(null);
                newSpace.setHomePage(null);
            }
            this.logoCopier.copyLogo(originalSpace, newSpace);
            this.lookAndFeelCopier.copyLookAndFeel(originalSpace, newSpace);
            SpaceServiceImpl.copyPageTemplates(context, originalSpace, newSpace);
            this.copySpaceLayouts(context);
            this.spaceManager.saveSpace(newSpace);
            if (context.isCopyLabels()) {
                this.copySpaceLabels(originalSpace, newSpace);
            }
            return newSpace;
        });
    }

    @Override
    public void copySpaceWatchers(CopySpaceContext context) {
        if (context.isPreserveWatchers()) {
            log.debug("Copying space watchers...");
            this.watcherService.copySpaceWatchers(this.getSpace(context.getOriginalSpaceKey()), context.getTargetSpaceKey());
        }
    }

    private void copySpaceLayouts(CopySpaceContext context) {
        Collection decorators = this.customLayoutManager.getCustomSpaceDecorators(context.getOriginalSpaceKey());
        decorators.forEach(decorator -> this.customLayoutManager.saveOrUpdate(context.getTargetSpaceKey(), decorator.getName(), decorator.getBody()));
    }

    private static void copyPageTemplates(CopySpaceContext context, Space originalSpace, Space newSpace) {
        originalSpace.getPageTemplates().forEach(template -> {
            if (template instanceof PageTemplate) {
                PageTemplate pageTemplate = (PageTemplate)template;
                try {
                    pageTemplate = (PageTemplate)pageTemplate.clone();
                    pageTemplate.setId(0L);
                    if (!context.isCopyMetadata()) {
                        pageTemplate.setCreator(AuthenticatedUserThreadLocal.get());
                        pageTemplate.setCreationDate(new Date());
                        pageTemplate.setLastModifier(AuthenticatedUserThreadLocal.get());
                        pageTemplate.setLastModificationDate(new Date());
                    }
                    newSpace.addPageTemplate(pageTemplate);
                }
                catch (CloneNotSupportedException e) {
                    log.warn("Cannot copy user template {} from space {}", (Object)pageTemplate.getId(), (Object)context.getOriginalSpaceKey());
                }
            }
        });
    }

    private void copySpaceLabels(Space originalSpace, Space newSpace) {
        originalSpace.getDescription().getLabels().forEach(label -> {
            try {
                this.labelService.addSpaceLabel(newSpace, (Label)label);
            }
            catch (Exception e) {
                log.error("Error copying label: ", (Throwable)e);
            }
        });
    }

    @Override
    public Space getSpace(String spaceKey) {
        return this.spaceManager.getSpace(spaceKey);
    }

    @Override
    public Optional<Long> getSpaceId(String spaceKey) {
        return Optional.ofNullable(this.spaceManager.getSpace(spaceKey)).flatMap(space -> Optional.of(space.getId()));
    }
}

