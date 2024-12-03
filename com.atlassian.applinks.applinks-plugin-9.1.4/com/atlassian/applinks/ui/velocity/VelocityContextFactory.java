/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.ui.velocity;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.OrphanedTrustDetector;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.ui.velocity.ListApplicationLinksContext;
import com.atlassian.applinks.ui.velocity.ListEntityLinksContext;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VelocityContextFactory {
    private final InternalHostApplication internalHostApplication;
    private final InternalTypeAccessor typeAccessor;
    private final I18nResolver i18nResolver;
    private final DocumentationLinker documentationLinker;
    private final OrphanedTrustDetector orphanedTrustDetector;
    private final ApplicationLinkService applicationLinkService;
    private final ManifestRetriever manifestRetriever;
    private final MessageFactory messageFactory;
    private final UserManager userManager;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public VelocityContextFactory(InternalHostApplication internalHostApplication, InternalTypeAccessor typeAccessor, I18nResolver i18nResolver, DocumentationLinker documentationLinker, @Qualifier(value="delegatingOrphanedTrustDetector") OrphanedTrustDetector orphanedTrustDetector, ApplicationLinkService applicationLinkService, ManifestRetriever manifestRetriever, MessageFactory messageFactory, UserManager userManager, PluginAccessor pluginAccessor) {
        this.internalHostApplication = internalHostApplication;
        this.typeAccessor = typeAccessor;
        this.i18nResolver = i18nResolver;
        this.documentationLinker = documentationLinker;
        this.orphanedTrustDetector = orphanedTrustDetector;
        this.applicationLinkService = applicationLinkService;
        this.manifestRetriever = manifestRetriever;
        this.messageFactory = messageFactory;
        this.userManager = userManager;
        this.pluginAccessor = pluginAccessor;
    }

    public ListApplicationLinksContext buildListApplicationLinksContext(HttpServletRequest request) {
        boolean isSysadmin = this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());
        return new ListApplicationLinksContext(this.internalHostApplication, this.typeAccessor, this.i18nResolver, this.documentationLinker, this.orphanedTrustDetector, request, this.pluginAccessor, isSysadmin);
    }

    public ListEntityLinksContext buildListEntityLinksContext(HttpServletRequest request, String entityTypeId, String entityKey) {
        boolean isAdmin = this.userManager.isAdmin(this.userManager.getRemoteUserKey(request));
        return new ListEntityLinksContext(this.applicationLinkService, this.manifestRetriever, this.internalHostApplication, this.documentationLinker, this.i18nResolver, this.messageFactory, this.typeAccessor, entityTypeId, entityKey, request.getContextPath(), this.getUsername(request), isAdmin);
    }

    private String getUsername(HttpServletRequest request) {
        UserProfile userProfile = this.userManager.getRemoteUser(request);
        return userProfile != null ? userProfile.getUsername() : null;
    }
}

