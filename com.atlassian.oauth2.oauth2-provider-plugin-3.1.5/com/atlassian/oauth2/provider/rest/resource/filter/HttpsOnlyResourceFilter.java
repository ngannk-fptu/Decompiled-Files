/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  javax.ws.rs.ext.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.resource.filter;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.oauth2.provider.rest.exception.ValidationException;
import com.atlassian.sal.api.message.I18nResolver;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class HttpsOnlyResourceFilter
implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(HttpsOnlyResourceFilter.class);
    private final HttpsValidator httpsValidator;
    private final I18nResolver i18nResolver;

    public HttpsOnlyResourceFilter(HttpsValidator httpsValidator, I18nResolver i18nResolver) {
        log.info(this.getClass().getName());
        this.httpsValidator = httpsValidator;
        this.i18nResolver = i18nResolver;
    }

    public ContainerRequest filter(ContainerRequest request) {
        log.info(this.getClass().getName() + ".filter");
        if (!(!this.httpsValidator.isBaseUrlHttpsRequired() || this.httpsValidator.isBaseUrlHttps() && request.isSecure())) {
            throw new ValidationException(ErrorCollection.forMessage(this.i18nResolver.getText("oauth2.rest.error.no.https.warning.message")));
        }
        return request;
    }
}

