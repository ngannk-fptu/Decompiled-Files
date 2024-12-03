/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.ia.service.SidebarLinkService
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.TemplatePageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
import com.atlassian.confluence.plugins.createcontent.actions.IndexPageManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.BlueprintLock;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={BlueprintManager.class})
public class DefaultBlueprintManager
implements BlueprintManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultBlueprintManager.class);
    private final ConcurrentHashMap<BlueprintLock, BlueprintLock> getOrCreateLocks = new ConcurrentHashMap();
    static final String INDEX_PAGE_LABEL = "blueprint-index-page";
    private static final String BLUEPRINT_KEY = "blueprintModuleKey";
    private final PluginAccessor pluginAccessor;
    private final SidebarLinkService sidebarLinkService;
    private final I18nResolver i18nResolver;
    private final BlueprintContentGenerator contentGenerator;
    private final ContentPropertyManager contentPropertyManager;
    private final IndexPageManager indexPageManager;
    private final LabelManager labelManager;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;
    private final BlueprintResolver resolver;

    @Autowired
    public DefaultBlueprintManager(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport SidebarLinkService sidebarLinkService, @ComponentImport I18nResolver i18nResolver, BlueprintContentGenerator contentGenerator, @ComponentImport ContentPropertyManager contentPropertyManager, IndexPageManager indexPageManager, @ComponentImport LabelManager labelManager, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, BlueprintResolver resolver) {
        this.pluginAccessor = pluginAccessor;
        this.i18nResolver = i18nResolver;
        this.sidebarLinkService = sidebarLinkService;
        this.contentGenerator = contentGenerator;
        this.contentPropertyManager = contentPropertyManager;
        this.indexPageManager = indexPageManager;
        this.labelManager = labelManager;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.resolver = resolver;
    }

    @Override
    public BlueprintModuleDescriptor getBlueprintDescriptor(ModuleCompleteKey blueprintKey) {
        BlueprintModuleDescriptor moduleDescriptor = (BlueprintModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(blueprintKey.getCompleteKey());
        Preconditions.checkNotNull((Object)moduleDescriptor, (Object)("module descriptor not found [key='" + blueprintKey + "']"));
        return moduleDescriptor;
    }

    @Override
    @Deprecated
    public Page createAndPinIndexPage(BlueprintDescriptor blueprintDescriptor, Space space) {
        if (blueprintDescriptor.isIndexDisabled()) {
            return null;
        }
        Preconditions.checkNotNull((Object)space, (Object)"space must be non-null");
        Preconditions.checkNotNull((Object)blueprintDescriptor, (Object)"blueprintDescriptor must be non-null");
        String indexPageTitle = this.getIndexPageTitle(blueprintDescriptor);
        assert (StringUtils.isNotBlank((CharSequence)indexPageTitle));
        Page indexPage = this.indexPageManager.getOrCreateIndexPage(blueprintDescriptor, space, indexPageTitle);
        if (this.sidebarServiceAvailable()) {
            this.pinIndexPageToSidebar(blueprintDescriptor.getIndexKey(), space, indexPage);
        }
        return indexPage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Page createAndPinIndexPage(ContentBlueprint blueprint, Space space) {
        if (blueprint.isIndexDisabled()) {
            return null;
        }
        Preconditions.checkNotNull((Object)space, (Object)"space must be non-null");
        Preconditions.checkNotNull((Object)blueprint, (Object)"blueprint must be non-null");
        String indexPageTitle = this.getIndexPageTitle(blueprint);
        assert (StringUtils.isNotBlank((CharSequence)indexPageTitle));
        UUID uuid = blueprint.getId();
        BlueprintLock newLock = new BlueprintLock(uuid, space);
        BlueprintLock lock = this.getOrCreateLocks.putIfAbsent(newLock, newLock);
        if (lock == null) {
            lock = newLock;
        }
        BlueprintLock blueprintLock = lock;
        synchronized (blueprintLock) {
            Page indexPage = this.indexPageManager.findIndexPage(blueprint, space);
            if (indexPage == null) {
                indexPage = this.indexPageManager.createIndexPage(blueprint, space, indexPageTitle);
                if (this.sidebarServiceAvailable()) {
                    this.pinIndexPageToSidebar(blueprint.getIndexKey(), space, indexPage);
                }
            }
            return indexPage;
        }
    }

    private boolean sidebarServiceAvailable() {
        return this.sidebarLinkService != null;
    }

    private void pinIndexPageToSidebar(String indexKey, Space space, Page indexPage) {
        try {
            if (!this.sidebarLinkToPageExists(space, indexPage)) {
                String iconClass = "blueprint " + indexKey;
                this.sidebarLinkService.forceCreate(space.getKey(), Long.valueOf(indexPage.getId()), null, null, iconClass);
            }
        }
        catch (Exception e) {
            log.info("Error pinning page", (Throwable)e);
        }
    }

    private boolean sidebarLinkToPageExists(Space space, Page page) throws NotPermittedException {
        return this.sidebarLinkService.hasQuickLink(space.getKey(), Long.valueOf(page.getId()));
    }

    @Override
    public String getIndexPageTitle(BlueprintDescriptor blueprintDescriptor) {
        String indexTitleI18nKey = this.getIndexPageTitleKey(blueprintDescriptor.getIndexTitleI18nKey(), blueprintDescriptor.getBlueprintKey().getCompleteKey());
        return this.i18nResolver.getText(indexTitleI18nKey);
    }

    @Override
    public String getIndexPageTitle(ContentBlueprint blueprint) {
        String indexTitleI18nKey = this.getIndexPageTitleKey(blueprint.getIndexTitleI18nKey(), blueprint.getModuleCompleteKey());
        return this.i18nResolver.getText(indexTitleI18nKey);
    }

    @Override
    public Page createBlueprintPage(ContentBlueprint blueprint, ConfluenceUser user, Space space, Page parentPage, Map<String, Object> context) {
        String indexKey;
        Page indexPage = this.createAndPinIndexPage(blueprint, space);
        if (parentPage == null) {
            parentPage = indexPage;
        }
        ContentTemplateRef contentTemplateRef = DefaultBlueprintManager.getContentTemplateRef(blueprint, context);
        Page blueprintPage = this.contentGenerator.generateBlueprintPageObject(contentTemplateRef, space, context);
        String blueprintPageCustomTitle = (String)context.get("title");
        if (StringUtils.isNotBlank((CharSequence)blueprintPageCustomTitle)) {
            blueprintPage.setTitle(blueprintPageCustomTitle);
        }
        if (StringUtils.isNotBlank((CharSequence)(indexKey = blueprint.getIndexKey()))) {
            Label blueprintLabel = new Label(indexKey);
            this.labelManager.addLabel((Labelable)blueprintPage, blueprintLabel);
        }
        if (parentPage != null) {
            parentPage.addChild(blueprintPage);
        }
        this.pageManager.saveContentEntity((ContentEntityObject)blueprintPage, DefaultSaveContext.DEFAULT);
        this.eventPublisher.publish((Object)new BlueprintPageCreateEvent(this, blueprintPage, blueprint, user, context));
        return blueprintPage;
    }

    @Override
    public Page createPageFromTemplate(ContentTemplateRef contentTemplateRef, ConfluenceUser user, Space space, Page parentPage, Map<String, Object> context) {
        return this.createPageFromTemplate(contentTemplateRef, user, space, parentPage, context, DefaultSaveContext.DEFAULT);
    }

    @Override
    public Page createPageFromTemplate(ContentTemplateRef contentTemplateRef, ConfluenceUser user, Space space, Page parentPage, Map<String, Object> context, SaveContext saveContext) {
        Page result = this.contentGenerator.generateBlueprintPageObject(contentTemplateRef, space, context);
        if (parentPage != null) {
            parentPage.addChild(result);
        }
        this.pageManager.saveContentEntity((ContentEntityObject)result, saveContext);
        this.eventPublisher.publish((Object)new TemplatePageCreateEvent(this, result, contentTemplateRef, user, context, saveContext));
        return result;
    }

    private static ContentTemplateRef getContentTemplateRef(ContentBlueprint blueprint, Map<String, Object> context) {
        String contentTemplateOverride = (String)context.get("contentTemplateRefId");
        if (StringUtils.isNotBlank((CharSequence)contentTemplateOverride)) {
            UUID contentTemplateRefId = UUID.fromString(contentTemplateOverride);
            return DefaultBlueprintManager.findContentTemplateRefInBlueprint(blueprint, contentTemplateRefId);
        }
        return blueprint.getFirstContentTemplateRef();
    }

    private static ContentTemplateRef findContentTemplateRefInBlueprint(ContentBlueprint contentBlueprint, UUID refId) {
        for (ContentTemplateRef ref : contentBlueprint.getContentTemplateRefs()) {
            if (!ref.getId().equals(refId)) continue;
            return ref;
        }
        throw new IllegalStateException("Content blueprint has no ContentTemplateRef with id: " + refId);
    }

    private String getIndexPageTitleKey(String indexTitleI18nKey, String blueprintModuleKey) {
        WebItemModuleDescriptor webItem;
        if (StringUtils.isBlank((CharSequence)indexTitleI18nKey) && (webItem = this.resolver.getWebItemMatchingBlueprint(blueprintModuleKey)) != null) {
            return webItem.getI18nNameKey();
        }
        return indexTitleI18nKey;
    }

    @Override
    public String getBlueprintKeyForContent(AbstractPage page) {
        return this.contentPropertyManager.getStringProperty((ContentEntityObject)page, BLUEPRINT_KEY);
    }
}

