/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.token.TokenException;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.license.LicensedPlugins;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

public class UpmResources {
    public static Option<Response> licensingPreconditionFailed(Plugin plugin, BaseRepresentationFactory representationFactory, LicensingUsageVerifier licensingUsageVerifier) {
        if (!LicensedPlugins.usesLicensing(plugin.getPlugin(), licensingUsageVerifier)) {
            return Option.some(Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).entity((Object)representationFactory.createI18nErrorRepresentation("upm.plugin.error.plugin.not.using.licensing")).type("application/vnd.atl.plugins.error+json").build());
        }
        return Option.none(Response.class);
    }

    public static void validateToken(String token, UserKey userKey, String responseContentType, TokenManager tokenManager, BaseRepresentationFactory representationFactory) {
        String error = "";
        try {
            if (token == null || !tokenManager.attemptToMatchAndInvalidateToken(userKey, token)) {
                error = "invalid token";
            }
        }
        catch (TokenException e) {
            error = e.getMessage();
        }
        if (StringUtils.isNotEmpty((CharSequence)error)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)representationFactory.createErrorRepresentation("invalid token", "upm.error.invalid.token")).type(responseContentType).build());
        }
    }
}

