/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.MacroMetadataChangedEvent
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Option
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetRequestContext$Builder
 *  com.atlassian.gadgets.GadgetRequestContext$User
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.admin.actions;

import com.atlassian.confluence.event.events.admin.MacroMetadataChangedEvent;
import com.atlassian.confluence.plugins.gadgets.admin.actions.ViewGadgetsAdminAction;
import com.atlassian.confluence.plugins.gadgets.events.GadgetInstalledEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetUninstalledEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Option;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddOrRemoveGadgetSpecAction
extends ViewGadgetsAdminAction {
    private static final Logger log = LoggerFactory.getLogger(AddOrRemoveGadgetSpecAction.class);
    private String gadgetUrlToAdd;
    private String gadgetUrlToRemove;
    private EventPublisher eventPublisher;
    private GadgetSpecFactory gadgetSpecFactory;
    private WhitelistService whitelistService;
    private OutboundWhitelist outboundWhitelist;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setGadgetSpecFactory(GadgetSpecFactory gadgetSpecFactory) {
        this.gadgetSpecFactory = gadgetSpecFactory;
    }

    public void setWhitelistService(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    public void setOutboundWhitelist(OutboundWhitelist outboundWhitelist) {
        this.outboundWhitelist = outboundWhitelist;
    }

    public String getGadgetUrlToAdd() {
        return this.gadgetUrlToAdd;
    }

    public void setGadgetUrlToAdd(String gadgetUrlToAdd) {
        this.gadgetUrlToAdd = gadgetUrlToAdd;
    }

    public void setGadgetUrlToRemove(String gadgetUrlToRemove) {
        this.gadgetUrlToRemove = gadgetUrlToRemove;
    }

    public String doAdd() {
        this.gadgetUrlToAdd = this.gadgetUrlToAdd.trim();
        if (StringUtils.isNotBlank((CharSequence)this.gadgetUrlToAdd)) {
            try {
                ConfluenceUser user = AuthenticatedUserThreadLocal.get();
                Locale locale = this.getLocaleManager().getLocale((User)user);
                GadgetRequestContext requestContext = GadgetRequestContext.Builder.gadgetRequestContext().locale(locale).ignoreCache(false).user(new GadgetRequestContext.User(user.getKey().getStringValue(), user.getName())).build();
                URI uri = new URI(this.gadgetUrlToAdd);
                try {
                    this.tryGetGadgetSpec(uri, requestContext);
                }
                catch (GadgetParsingException e) {
                    log.warn("Could not add gadget at {}", (Object)uri, (Object)e);
                    return this.returnInvalidUrl();
                }
                this.clusterSafeGadgetDirectoryStore.add(uri);
                this.eventPublisher.publish((Object)new GadgetInstalledEvent((Object)this, uri));
                this.eventPublisher.publish((Object)new MacroMetadataChangedEvent((Object)this));
                return "success";
            }
            catch (URISyntaxException e) {
                return this.returnInvalidUrl();
            }
        }
        this.addFieldError("gadgetUrlToAdd", this.getText("gadgets.add.uri.required"));
        return "input";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryGetGadgetSpec(URI uri, GadgetRequestContext requestContext) {
        Option<WhitelistRule> whitelistRule = this.whitelistIfNotAllowed(uri);
        try {
            this.gadgetSpecFactory.getGadgetSpec(uri, requestContext);
        }
        catch (Throwable throwable) {
            whitelistRule.foreach((Effect)new Effect<WhitelistRule>(){

                public void apply(WhitelistRule whitelistRule) {
                    AddOrRemoveGadgetSpecAction.this.whitelistService.remove(whitelistRule.getId().intValue());
                }
            });
            throw throwable;
        }
        whitelistRule.foreach((Effect)new /* invalid duplicate definition of identical inner class */);
    }

    private Option<WhitelistRule> whitelistIfNotAllowed(URI uri) {
        if (!this.outboundWhitelist.isAllowed(uri)) {
            ImmutableWhitelistRule whitelistRule = ImmutableWhitelistRule.builder().expression(uri.toString()).type(WhitelistType.EXACT_URL).build();
            try {
                return Option.some((Object)this.whitelistService.add((WhitelistRule)whitelistRule));
            }
            catch (NotAuthorizedException e) {
                log.warn("The current user is not authorized to add whitelist rules; requesting the gadget spec from '{}' is very likely to fail.", (Object)uri);
                return Option.none();
            }
        }
        return Option.none();
    }

    private String returnInvalidUrl() {
        this.addFieldError("gadgetUrlToAdd", this.getText("gadgets.invalid.uri"));
        return "input";
    }

    public String doRemove() {
        if (StringUtils.isNotBlank((CharSequence)this.gadgetUrlToRemove)) {
            try {
                URI uri = new URI(this.gadgetUrlToRemove);
                ExternalGadgetSpec matchingSpec = this.getMatchingSpec(uri);
                if (matchingSpec == null) {
                    this.addActionError("gadget.unable.to.find.given.uri", new Object[]{this.gadgetUrlToRemove});
                    return "error";
                }
                this.clusterSafeGadgetDirectoryStore.remove(matchingSpec.getId());
                this.eventPublisher.publish((Object)new GadgetUninstalledEvent((Object)this, uri));
                this.eventPublisher.publish((Object)new MacroMetadataChangedEvent((Object)this));
                return "success";
            }
            catch (URISyntaxException e) {
                this.addActionError("gadget.unable.to.remove.invalid.uri", new Object[]{this.gadgetUrlToRemove});
                return "error";
            }
        }
        return "input";
    }

    private ExternalGadgetSpec getMatchingSpec(URI uri) {
        for (ExternalGadgetSpec externalGadgetSpec : this.clusterSafeGadgetDirectoryStore.entries()) {
            if (!externalGadgetSpec.getSpecUri().equals(uri)) continue;
            return externalGadgetSpec;
        }
        return null;
    }
}

