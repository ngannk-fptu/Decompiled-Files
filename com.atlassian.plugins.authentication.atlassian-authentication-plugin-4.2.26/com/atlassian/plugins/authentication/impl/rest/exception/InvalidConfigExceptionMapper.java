/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  javax.inject.Inject
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.plugins.authentication.impl.rest.exception;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.api.exception.InvalidConfigException;
import com.atlassian.plugins.authentication.impl.rest.model.ValidationResultEntity;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidConfigExceptionMapper
implements ExceptionMapper<InvalidConfigException> {
    private static final Map<String, String> FIELD_NAMES_I18N_KEYS = ImmutableMap.builder().put((Object)"sso-type", (Object)"authentication.config.save.fail.sso.type").put((Object)"idp-type", (Object)"authentication.config.save.fail.idp.type").put((Object)"sso-url", (Object)"authentication.config.save.fail.sso.url").put((Object)"sso-issuer", (Object)"authentication.config.save.fail.sso.issuer").put((Object)"crowd-url", (Object)"authentication.config.save.fail.crowd.url").put((Object)"certificate", (Object)"authentication.config.save.fail.certificate").put((Object)"issuer-url", (Object)"authentication.config.save.fail.issuer.url").put((Object)"client-id", (Object)"authentication.config.save.fail.client.id").put((Object)"client-secret", (Object)"authentication.config.save.fail.client.secret").put((Object)"authorization-endpoint", (Object)"authentication.config.save.fail.auth.endpoint").put((Object)"token-endpoint", (Object)"authentication.config.save.fail.token.endpoint").put((Object)"userinfo-endpoint", (Object)"authentication.config.save.fail.userinfo.endpoint").put((Object)"additional-scopes", (Object)"authentication.config.save.fail.additional.scopes").put((Object)"username-claim", (Object)"authentication.config.save.fail.username.claim.invalid").build();
    private static final Map<String, String> INCORRECT_VALUES_I18N_KEYS = ImmutableMap.builder().put((Object)"sso-url", (Object)"authentication.config.save.fail.sso.url.malformed").put((Object)"crowd-url", (Object)"authentication.config.save.fail.crowd.url.malformed").put((Object)"certificate", (Object)"authentication.config.save.fail.certificate.not.parsable").put((Object)"additional-scopes", (Object)"authentication.config.save.fail.additional.scopes.invalid").put((Object)"username-claim", (Object)"config.page.oidc.settings.usernameclaim.error.incorrect").put((Object)"username-attribute", (Object)"config.page.user.attribute.error.incorrect").put((Object)"discovery-enabled", (Object)"config.page.oidc.additional.settings.discoveryenabled.incorrect").put((Object)"mapping-display-name", (Object)"config.page.generic.jit.config.displayname.error.incorrect").put((Object)"mapping-email", (Object)"config.page.generic.jit.config.email.error.incorrect").build();
    private final I18nResolver i18nResolver;

    @Inject
    public InvalidConfigExceptionMapper(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(InvalidConfigException exception) {
        Multimap errors = Multimaps.transformEntries(exception.getErrorsOnFields(), this::mapErrorEntry);
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ValidationResultEntity((Multimap<String, ValidationError.Entity>)errors)).build();
    }

    private ValidationError.Entity mapErrorEntry(String fieldName, ValidationError validationError) {
        switch (validationError.getReason()) {
            case REQUIRED: {
                return validationError.toEntity(this.mapMissingRequiredValueError(fieldName));
            }
            case INCORRECT: 
            case TOO_LONG: {
                return validationError.toEntity(this.mapIncorrectValueError(fieldName));
            }
            case INSECURE: {
                return validationError.toEntity(this.mapInsecureValueError(fieldName));
            }
            case NOT_SUPPORTED: {
                return validationError.toEntity(this.mappingNotSupportedError());
            }
            case NON_UNIQUE: {
                return validationError.toEntity(this.mapNotUniqueError());
            }
        }
        return validationError.toEntity(this.mapGenericError(fieldName));
    }

    private String mapNotUniqueError() {
        return this.i18nResolver.getText("authentication.config.save.fail.field.not.unique");
    }

    private String mapInsecureValueError(String fieldName) {
        return this.mapGenericInsecureUrlError(fieldName);
    }

    private String mapIncorrectValueError(String fieldName) {
        return Optional.ofNullable(INCORRECT_VALUES_I18N_KEYS.get(fieldName)).map(arg_0 -> ((I18nResolver)this.i18nResolver).getText(arg_0)).orElseGet(() -> this.mapGenericError(fieldName));
    }

    private String mapMissingRequiredValueError(String fieldName) {
        return this.i18nResolver.getText("authentication.config.save.fail.field.missing", new Serializable[]{this.mapFieldName(fieldName)});
    }

    private String mapGenericInsecureUrlError(String fieldName) {
        return this.i18nResolver.getText("authentication.config.save.fail.url.insecure.generic", new Serializable[]{this.mapFieldName(fieldName)});
    }

    private String mappingNotSupportedError() {
        return this.i18nResolver.getText("authentication.config.save.fail.mapping.not.supported");
    }

    private String mapGenericError(String fieldName) {
        return this.i18nResolver.getText("authentication.config.save.fail.generic", new Serializable[]{this.mapFieldName(fieldName)});
    }

    private String mapFieldName(String fieldName) {
        return Maps.transformValues(FIELD_NAMES_I18N_KEYS, arg_0 -> ((I18nResolver)this.i18nResolver).getText(arg_0)).getOrDefault(fieldName, fieldName);
    }
}

