/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.atlassian.renderer.WikiStyleRenderer
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.action;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.actions.LoginExemptionHelper;
import com.atlassian.confluence.impl.pages.actions.CommentAwareHelper;
import com.atlassian.confluence.impl.pages.actions.PageAwareHelper;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.editor.DefaultEditorManager;
import com.atlassian.confluence.plugin.editor.EditorManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.SetupLocks;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.ContentFacade;
import com.atlassian.confluence.validation.DefaultMessageHolderFactory;
import com.atlassian.confluence.validation.MessageHolderFactory;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.renderer.WikiStyleRenderer;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XWorkActionHelperContextConfig {
    @Resource
    private PageManager pageManager;
    @Resource
    private ContentEntityManager contentEntityManager;
    @Resource
    private PermissionManager permissionManager;
    @Resource
    private SpaceManager spaceManager;
    @Resource
    private CommentManager commentManager;
    @Resource
    private ConfluenceWebResourceManager webResourceManager;
    @Resource
    private CollaborativeEditingHelper collaborativeEditingHelper;
    @Resource
    private AccessModeService accessModeService;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private FileStore.Path sharedHome;
    @Resource
    private SettingsManager settingsManager;
    @Resource
    private WikiStyleRenderer wikiStyleRenderer;
    @Resource
    private PluginEventManager pluginEventManager;
    @Resource
    private PluginAccessor pluginAccessor;

    XWorkActionHelperContextConfig() {
    }

    @Bean
    PageAwareHelper pageAwareHelper() {
        return new PageAwareHelper(this.pageManager, this.contentEntityManager, this.permissionManager, this.spaceManager, this.webResourceManager, this.collaborativeEditingHelper, this.accessModeService, this.eventPublisher);
    }

    @Bean
    CommentAwareHelper commentAwareHelper() {
        return new CommentAwareHelper(this.commentManager, this.permissionManager, this.webResourceManager);
    }

    @Bean
    LoginExemptionHelper loginExemptionHelper() {
        return new LoginExemptionHelper(this.sharedHome);
    }

    @Bean
    ContentFacade contentFacade() {
        ContentFacade bean = new ContentFacade();
        bean.setPageManager(this.pageManager);
        bean.setSettingsManager(this.settingsManager);
        bean.setPermissionManager(this.permissionManager);
        bean.setSpaceManager(this.spaceManager);
        bean.setWikiStyleRenderer(this.wikiStyleRenderer);
        return bean;
    }

    @Bean
    @AvailableToPlugins
    EditorManager editorManager() {
        DefaultEditorManager bean = new DefaultEditorManager();
        bean.setPluginAccessor(this.pluginAccessor);
        return bean;
    }

    @Bean
    @AvailableToPlugins
    MessageHolderFactory messageHolderFactory() {
        return new DefaultMessageHolderFactory();
    }

    @Bean
    SetupLocks setupLocks() {
        return new SetupLocks();
    }
}

