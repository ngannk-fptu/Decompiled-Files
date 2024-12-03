/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.credential.TokenRequestContext
 *  com.azure.core.http.HttpPipelineCallContext
 *  com.azure.core.http.HttpPipelineNextPolicy
 *  com.azure.core.http.HttpResponse
 *  com.azure.core.http.policy.HttpPipelinePolicy
 *  com.azure.core.util.CoreUtils
 *  reactor.core.publisher.Mono
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpPipelineNextPolicy;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.util.CoreUtils;
import com.microsoft.sqlserver.jdbc.KeyVaultTokenCredential;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ScopeTokenCache;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import reactor.core.publisher.Mono;

class KeyVaultCustomCredentialPolicy
implements HttpPipelinePolicy {
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private final ScopeTokenCache cache;
    private final KeyVaultTokenCredential keyVaultTokenCredential;

    KeyVaultCustomCredentialPolicy(KeyVaultTokenCredential credential) throws SQLServerException {
        if (null == credential) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
            Object[] msgArgs1 = new Object[]{"Credential"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.cache = new ScopeTokenCache(credential::getToken);
        this.keyVaultTokenCredential = credential;
    }

    public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
        if (!"https".equals(context.getHttpRequest().getUrl().getProtocol())) {
            return Mono.error((Throwable)new RuntimeException(SQLServerException.getErrString("R_TokenRequireUrl")));
        }
        return next.clone().process().doOnNext(HttpResponse::close).map(res -> res.getHeaderValue(WWW_AUTHENTICATE)).map(header -> KeyVaultCustomCredentialPolicy.extractChallenge(header, BEARER_TOKEN_PREFIX)).flatMap(map -> {
            this.keyVaultTokenCredential.setAuthorization((String)map.get("authorization"));
            this.keyVaultTokenCredential.setResource((String)map.get("resource"));
            this.keyVaultTokenCredential.setScope((String)map.get("scope"));
            this.cache.setRequest(new TokenRequestContext().addScopes(new String[]{(String)map.get("resource") + "/.default"}));
            return this.cache.getToken();
        }).flatMap(token -> {
            context.getHttpRequest().setHeader(AUTHORIZATION, BEARER_TOKEN_PREFIX + token.getToken());
            return next.process();
        });
    }

    private static Map<String, String> extractChallenge(String authenticateHeader, String authChallengePrefix) {
        if (!KeyVaultCustomCredentialPolicy.isValidChallenge(authenticateHeader, authChallengePrefix)) {
            return null;
        }
        authenticateHeader = authenticateHeader.toLowerCase(Locale.ROOT).replace(authChallengePrefix.toLowerCase(Locale.ROOT), "");
        String[] challenges = authenticateHeader.split(", ");
        HashMap<String, String> challengeMap = new HashMap<String, String>();
        for (String pair : challenges) {
            String[] keyValue = pair.split("=");
            challengeMap.put(keyValue[0].replaceAll("\"", ""), keyValue[1].replaceAll("\"", ""));
        }
        return challengeMap;
    }

    private static boolean isValidChallenge(String authenticateHeader, String authChallengePrefix) {
        return !CoreUtils.isNullOrEmpty((CharSequence)authenticateHeader) && authenticateHeader.toLowerCase(Locale.ROOT).startsWith(authChallengePrefix.toLowerCase(Locale.ROOT));
    }
}

