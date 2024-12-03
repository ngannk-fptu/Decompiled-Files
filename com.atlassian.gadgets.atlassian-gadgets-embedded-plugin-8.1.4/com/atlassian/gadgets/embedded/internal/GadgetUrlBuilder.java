/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.gadgets.spec.UserPrefSpec
 *  com.atlassian.gadgets.util.AbstractUrlBuilder
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.gadgets.view.ModuleId
 *  com.atlassian.gadgets.view.RenderedGadgetUriBuilder
 *  com.atlassian.gadgets.view.SecurityTokenFactory
 *  com.atlassian.gadgets.view.View
 *  com.atlassian.gadgets.view.ViewType
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.embedded.internal;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.gadgets.spec.UserPrefSpec;
import com.atlassian.gadgets.util.AbstractUrlBuilder;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.RenderedGadgetUriBuilder;
import com.atlassian.gadgets.view.SecurityTokenFactory;
import com.atlassian.gadgets.view.View;
import com.atlassian.gadgets.view.ViewType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={RenderedGadgetUriBuilder.class})
public class GadgetUrlBuilder
extends AbstractUrlBuilder
implements RenderedGadgetUriBuilder {
    private static final String CONTAINER = "atlassian";
    private final SecurityTokenFactory securityTokenFactory;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    @Autowired
    public GadgetUrlBuilder(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport SecurityTokenFactory securityTokenFactory, @ComponentImport GadgetSpecFactory gadgetSpecFactory) {
        super(applicationProperties, webResourceUrlProvider, "");
        this.securityTokenFactory = securityTokenFactory;
        this.gadgetSpecFactory = gadgetSpecFactory;
    }

    @Deprecated
    public URI build(GadgetState gadgetState, View view, GadgetRequestContext gadgetRequestContext) {
        return this.build(gadgetState, ModuleId.valueOf((String)gadgetState.getId().value()), view, gadgetRequestContext);
    }

    public final URI build(GadgetState gadgetState, ModuleId moduleId, View view, GadgetRequestContext gadgetRequestContext) {
        ViewType viewType = view.getViewType();
        Map viewParams = view.paramsAsMap();
        return URI.create(this.getBaseUrl() + "/ifr?container=" + CONTAINER + "&mid=" + Uri.encodeUriComponent((String)((ModuleId)Preconditions.checkNotNull((Object)moduleId, (Object)"moduleId")).toString()) + (gadgetRequestContext.getIgnoreCache() ? "&nocache=1" : "") + "&country=" + gadgetRequestContext.getLocale().getCountry() + "&lang=" + gadgetRequestContext.getLocale().getLanguage() + "&view=" + viewType.getCanonicalName().toLowerCase() + (gadgetRequestContext.isDebuggingEnabled() ? "&debug=1" : "") + this.buildViewParams(viewParams) + "&st=" + Uri.encodeUriComponent((String)this.securityTokenFactory.newSecurityToken(gadgetState, gadgetRequestContext.getViewer())) + this.buildUserPrefsParams(gadgetState, gadgetRequestContext) + "&url=" + Uri.encodeUriComponent((String)this.absoluteGadgetSpecUri(gadgetState).toASCIIString()) + "&libs=auth-refresh");
    }

    private String buildUserPrefsParams(GadgetState openSocialGadgetState, GadgetRequestContext gadgetRequestContext) {
        StringBuilder userPrefsParam = new StringBuilder();
        URI specUri = openSocialGadgetState.getGadgetSpecUri();
        try {
            GadgetSpec spec = this.gadgetSpecFactory.getGadgetSpec(specUri, gadgetRequestContext);
            for (UserPrefSpec userPrefSpec : spec.getUserPrefs()) {
                String prefName = userPrefSpec.getName();
                String prefValue = (String)openSocialGadgetState.getUserPrefs().get(prefName);
                if (prefValue == null) {
                    prefValue = userPrefSpec.getDefaultValue();
                }
                userPrefsParam.append("&up_").append(Uri.encodeUriComponent((String)prefName)).append("=").append(Uri.encodeUriComponent((String)prefValue));
            }
        }
        catch (GadgetParsingException e) {
            this.log.warn("GadgetUrlBuilder: could not parse spec at " + specUri);
            this.log.debug("GadgetUrlBuilder", (Throwable)e);
        }
        return userPrefsParam.toString();
    }

    private URI absoluteGadgetSpecUri(GadgetState gadget) {
        return Uri.resolveUriAgainstBase((String)this.applicationProperties.getBaseUrl(), (URI)gadget.getGadgetSpecUri());
    }

    private String buildViewParams(Map<String, String> viewParams) {
        if (viewParams != null && !viewParams.isEmpty()) {
            return "&view-params=" + Uri.encodeUriComponent((String)new JSONObject(viewParams).toString());
        }
        return "";
    }
}

