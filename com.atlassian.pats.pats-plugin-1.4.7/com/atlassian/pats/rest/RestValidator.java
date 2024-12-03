/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.pats.rest;

import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.exception.InvalidDateStringFormatException;
import com.atlassian.pats.exception.InvalidSortException;
import com.atlassian.pats.exception.UserTokenLimitExceededException;
import com.atlassian.pats.rest.RestNewTokenRequest;
import com.atlassian.pats.rest.RestTokenSearchRequest;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class RestValidator {
    private static final Logger logger = LoggerFactory.getLogger(RestValidator.class);
    static final String INVALID_SORT_PROPERTY_VALUE = "rest.error.sort.invalid";
    static final String INVALID_ORDER_PROPERTY_VALUE = "rest.error.order.invalid";
    static final String INVALID_DATE_FORMAT_PROPERTY_VALUE = "rest.error.date.invalid";
    private static final List<String> SORT_CATEGORIES = ImmutableList.of((Object)"createdAt", (Object)"expiringAt", (Object)"lastAccessedAt", (Object)"name", (Object)"userKey");
    public static final String INVALID_NO_ETERNAL_TOKENS = "rest.error.eternal.tokens.disabled";
    private final I18nResolver i18nResolver;
    private final TokenRepository tokenRepository;

    public RestValidator(I18nResolver i18nResolver, TokenRepository tokenRepository) {
        this.i18nResolver = i18nResolver;
        this.tokenRepository = tokenRepository;
    }

    public void verifyCreateTokenRequest(RestNewTokenRequest createTokenRequest, UserKey userKey) {
        Assert.isTrue((boolean)SystemProperty.PATS_ENABLED.getValue(), () -> this.i18nResolver.getText("rest.error.feature.disabled"));
        int maximumExpiryDays = SystemProperty.MAX_TOKEN_EXPIRY_DAYS.getValue();
        int tokenNameLength = SystemProperty.TOKEN_NAME_LENGTH.getValue();
        Assert.hasText((String)createTokenRequest.getName(), () -> this.i18nResolver.getText("rest.error.empty.token.name"));
        Assert.isTrue((createTokenRequest.getName().length() <= tokenNameLength ? 1 : 0) != 0, () -> this.i18nResolver.getText("rest.error.token.name.length", new Serializable[]{Integer.valueOf(tokenNameLength)}));
        Assert.isTrue((createTokenRequest.getExpirationDuration() == null || maximumExpiryDays >= createTokenRequest.getExpirationDuration() ? 1 : 0) != 0, () -> this.i18nResolver.getText("rest.error.max.expiry.length.exceeded", new Serializable[]{Integer.valueOf(maximumExpiryDays)}));
        Assert.isTrue((createTokenRequest.getExpirationDuration() == null || createTokenRequest.getExpirationDuration() > 0 ? 1 : 0) != 0, () -> this.i18nResolver.getText("rest.error.min.expiry.length.exceeded"));
        this.verifyUserTokenLimitNotExceeded(userKey);
        this.verifyCanCreateEternalTokens(createTokenRequest);
    }

    public void verifyTokenSearchRequest(RestTokenSearchRequest tokenSearchRequest) {
        this.validateSortString(tokenSearchRequest.getSortBy());
        this.validateOrderString("orderBy", tokenSearchRequest.getOrderBy());
        this.validateDateString("tokenCreatedDateFrom", tokenSearchRequest.getTokenCreatedDateFrom());
        this.validateDateString("tokenCreatedDateTo", tokenSearchRequest.getTokenCreatedDateTo());
        this.validateDateString("expiryDateFrom", tokenSearchRequest.getTokenExpiryDateFrom());
        this.validateDateString("expiryDateTo", tokenSearchRequest.getTokenExpiryDateTo());
        this.validateDateString("lastAuthenticatedDateFrom", tokenSearchRequest.getLastAuthenticatedDateFrom());
        this.validateDateString("lastAuthenticatedDateTo", tokenSearchRequest.getLastAuthenticatedDateTo());
    }

    private void verifyUserTokenLimitNotExceeded(UserKey userKey) {
        int maximumTokenNumberPerUser = SystemProperty.MAX_TOKENS_PER_USER.getValue();
        long tokensByUser = this.tokenRepository.countAllByUserKey(userKey.getStringValue());
        if (tokensByUser >= (long)maximumTokenNumberPerUser) {
            this.userHasReachedMaximumTokenNumber(userKey);
        }
    }

    public ZonedDateTime validateDateString(String fieldName, String fieldValue) {
        try {
            return Objects.nonNull(fieldValue) ? ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(fieldValue)) : null;
        }
        catch (DateTimeException e) {
            throw new InvalidDateStringFormatException(this.i18nResolver.getText(INVALID_DATE_FORMAT_PROPERTY_VALUE, new Serializable[]{fieldName, fieldValue}));
        }
    }

    public void validateSortString(String fieldValue) {
        if (!SORT_CATEGORIES.contains(fieldValue)) {
            throw new InvalidSortException(this.i18nResolver.getText(INVALID_SORT_PROPERTY_VALUE, new Serializable[]{fieldValue}));
        }
    }

    public void validateOrderString(String fieldName, String fieldValue) {
        try {
            Sort.Direction.fromString(fieldValue.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidSortException(this.i18nResolver.getText(INVALID_ORDER_PROPERTY_VALUE, new Serializable[]{fieldName, fieldValue}));
        }
    }

    private void userHasReachedMaximumTokenNumber(UserKey userKey) {
        int maximumTokenNumberPerUser = SystemProperty.MAX_TOKENS_PER_USER.getValue();
        logger.debug("User [{}] has reached maximum tokens number [{}]", (Object)userKey.getStringValue(), (Object)maximumTokenNumberPerUser);
        throw new UserTokenLimitExceededException(this.i18nResolver.getText("rest.error.too.many.tokens", new Serializable[]{Integer.valueOf(maximumTokenNumberPerUser)}));
    }

    private void verifyCanCreateEternalTokens(RestNewTokenRequest createTokenRequest) {
        if (Objects.isNull(createTokenRequest.getExpirationDuration()) && !SystemProperty.ETERNAL_TOKENS_ENABLED.getValue().booleanValue()) {
            throw new IllegalArgumentException(this.i18nResolver.getText(INVALID_NO_ETERNAL_TOKENS));
        }
    }
}

