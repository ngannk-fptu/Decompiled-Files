/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.StaticUrlApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.atlassian.applinks.ui.velocity;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.auth.OrphanedTrustDetector;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth2.OAuth2ResultException;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.StaticUrlApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.ui.velocity.AbstractVelocityContext;
import com.atlassian.applinks.ui.velocity.OAuth2Result;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListApplicationLinksContext
extends AbstractVelocityContext {
    private static final String OAUTH2_CLIENT_CONFIG_PATH = "/plugins/servlet/oauth2";
    private final I18nResolver i18nResolver;
    private final OrphanedTrustDetector orphanedTrustDetector;
    private final Set<String> entityTypeIdStrings;
    private final boolean isSysadmin;
    private final OAuth2Result oauth2Result;
    private final PluginAccessor pluginAccessor;
    private List<OrphanedTrustCertificate> orphanedTrustCertificates;

    ListApplicationLinksContext(InternalHostApplication internalHostApplication, InternalTypeAccessor typeAccessor, I18nResolver i18nResolver, DocumentationLinker documentationLinker, OrphanedTrustDetector orphanedTrustDetector, HttpServletRequest request, PluginAccessor pluginAccessor, boolean isSysadmin) {
        super(request.getContextPath(), internalHostApplication, typeAccessor, documentationLinker);
        this.i18nResolver = i18nResolver;
        this.orphanedTrustDetector = orphanedTrustDetector;
        this.isSysadmin = isSysadmin;
        this.oauth2Result = request.getParameter("oauth2ResultType") != null ? new OAuth2Result(request.getParameter("oauth2ResultType"), request.getParameter("oauth2ResultMessage")) : OAuth2Result.empty();
        this.entityTypeIdStrings = Sets.newHashSet((Iterable)Iterables.transform(typeAccessor.getEntityTypesForApplicationType(TypeId.getTypeId((ApplicationType)internalHostApplication.getType())), (Function)new Function<EntityType, String>(){

            public String apply(@Nullable EntityType from) {
                return TypeId.getTypeId((EntityType)from).get();
            }
        }));
        this.pluginAccessor = pluginAccessor;
    }

    @HtmlSafe
    public JSONArray getNonAppLinksApplicationTypes() {
        return this.getApplicationTypesJSON();
    }

    @HtmlSafe
    public JSONArray getLocalEntityTypeIdStrings() {
        HTMLSafeJSONArray entityTypeIdJSON = new HTMLSafeJSONArray();
        for (String typeId : this.entityTypeIdStrings) {
            entityTypeIdJSON.put(typeId);
        }
        return entityTypeIdJSON;
    }

    public String getApplicationType() {
        return TypeId.getTypeId((ApplicationType)this.internalHostApplication.getType()).get();
    }

    private JSONArray getApplicationTypesJSON() {
        HTMLSafeJSONArray applicationTypesJSON = new HTMLSafeJSONArray();
        Iterable applicationTypes = Iterables.filter((Iterable)this.typeAccessor.getEnabledApplicationTypes(), NonAppLinksApplicationType.class);
        for (NonAppLinksApplicationType nonAppLinksApplicationType : applicationTypes) {
            HTMLSafeJSONObject appType = new HTMLSafeJSONObject();
            try {
                appType.put("typeId", nonAppLinksApplicationType.getId().get());
                appType.put("label", this.i18nResolver.getText(nonAppLinksApplicationType.getI18nKey()));
                applicationTypesJSON.put((Object)appType);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return applicationTypesJSON;
    }

    public String getProduct() {
        return this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey());
    }

    public Collection<StaticUrlApplicationType> getStaticUrlApplicationTypes() {
        ArrayList<StaticUrlApplicationType> types = new ArrayList<StaticUrlApplicationType>();
        for (StaticUrlApplicationType type : Iterables.filter((Iterable)this.typeAccessor.getEnabledApplicationTypes(), StaticUrlApplicationType.class)) {
            types.add(type);
        }
        return types;
    }

    public List<OrphanedTrustCertificate> getOrphanedTrustCertificates() {
        if (this.orphanedTrustCertificates == null) {
            this.orphanedTrustCertificates = this.orphanedTrustDetector.findOrphanedTrustCertificates();
        }
        return this.orphanedTrustCertificates;
    }

    public UnescapedI18nResolver getI18nNoEscape() {
        return new UnescapedI18nResolver();
    }

    public boolean isSysadmin() {
        return this.isSysadmin;
    }

    public String getOAuth2ClientConfigUrl() {
        return this.internalHostApplication.getBaseUrl().toString() + OAUTH2_CLIENT_CONFIG_PATH;
    }

    public boolean isOAuth2ClientDisabled() {
        return !this.pluginAccessor.isPluginEnabled("com.atlassian.oauth2.oauth2-client-plugin");
    }

    public boolean isOAuth2ProviderDisabled() {
        return !this.pluginAccessor.isPluginEnabled("com.atlassian.oauth2.oauth2-provider-plugin");
    }

    @HtmlSafe
    public JSONObject getOauth2Result() {
        if (this.oauth2Result.isEmpty()) {
            return null;
        }
        HTMLSafeJSONObject oauth2ResultJson = new HTMLSafeJSONObject();
        try {
            oauth2ResultJson.put("type", this.oauth2Result.getType());
            oauth2ResultJson.put("message", this.oauth2Result.getMessage());
            return oauth2ResultJson;
        }
        catch (JSONException e) {
            throw new OAuth2ResultException(e);
        }
    }

    protected static class HTMLSafeJSONArray
    extends JSONArray {
        protected HTMLSafeJSONArray() {
        }

        @HtmlSafe
        public String toString() {
            return super.toString();
        }
    }

    protected static class HTMLSafeJSONObject
    extends JSONObject {
        protected HTMLSafeJSONObject() {
        }

        @HtmlSafe
        public String toString() {
            return super.toString();
        }
    }

    public class UnescapedI18nResolver {
        @HtmlSafe
        public String getText(String key, String ... arguments) {
            return ListApplicationLinksContext.this.i18nResolver.getText(key, (Serializable[])arguments);
        }
    }
}

