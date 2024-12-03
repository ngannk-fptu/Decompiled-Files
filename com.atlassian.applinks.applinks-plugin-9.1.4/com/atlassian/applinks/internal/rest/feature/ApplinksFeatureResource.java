/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.internal.rest.feature;

import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.rest.util.RestEnumParser;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.internal.feature.JsonApplinksFeatures;
import com.atlassian.applinks.internal.rest.RestUrl;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Strings;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="features")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@AnonymousAllowed
@InterceptorChain(value={ServiceExceptionInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplinksFeatureResource {
    public static final String CONTEXT = "features";
    public static final RestUrl FEATURES_PATH = RestUrl.forPath("features");
    private static final String PARAM_FEATURE = "feature";
    private static final String PARAM_TEMPLATE_FEATURE = "{feature}";
    private final ApplinksFeatureService featureService;
    private final JsonApplinksFeatures jsonApplinksFeatures;
    private final RestEnumParser<ApplinksFeatures> featureParser;

    @Nonnull
    public static RestUrlBuilder featuresUrl() {
        return new RestUrlBuilder().addPath(FEATURES_PATH);
    }

    @Nonnull
    public static RestUrlBuilder featureUrl(@Nonnull ApplinksFeatures feature) {
        return ApplinksFeatureResource.featuresUrl().addPath(feature.name());
    }

    public ApplinksFeatureResource(I18nResolver i18nResolver, ApplinksFeatureService featureService) {
        this.featureService = featureService;
        this.jsonApplinksFeatures = new JsonApplinksFeatures(featureService);
        this.featureParser = new RestEnumParser<ApplinksFeatures>(ApplinksFeatures.class, i18nResolver, "applinks.rest.feature.error.unsupported");
    }

    @GET
    @Path(value="{feature}")
    public Response isEnabled(@PathParam(value="feature") String featureName) {
        ApplinksFeatures feature = this.parseFeature(Strings.nullToEmpty((String)featureName));
        return RestUtil.ok(this.jsonApplinksFeatures.isEnabled(feature));
    }

    @PUT
    @Path(value="{feature}")
    public Response enable(@PathParam(value="feature") String featureName) throws ServiceException {
        ApplinksFeatures feature = this.parseFeature(Strings.nullToEmpty((String)featureName));
        this.featureService.enable(feature, new ApplinksFeatures[0]);
        return RestUtil.ok(BaseRestEntity.createSingleFieldEntity(featureName, true));
    }

    @DELETE
    @Path(value="{feature}")
    public Response disable(@PathParam(value="feature") String featureName) throws ServiceException {
        ApplinksFeatures feature = this.parseFeature(Strings.nullToEmpty((String)featureName));
        this.featureService.disable(feature, new ApplinksFeatures[0]);
        return RestUtil.noContent();
    }

    private ApplinksFeatures parseFeature(String featureName) {
        return this.featureParser.parseEnumParameter(featureName, PARAM_FEATURE);
    }
}

