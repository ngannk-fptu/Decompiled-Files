/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.validation.constraints.Null
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.pats.api.TokenGeneratorService;
import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.db.NotificationState;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.events.TokenEventPublisher;
import com.atlassian.pats.exception.CreateTokenFailedException;
import com.atlassian.pats.rest.RestTokenSearchRequest;
import com.atlassian.pats.rest.SearchFilterPredicateBuilder;
import com.atlassian.pats.service.GeneratedToken;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Strings;
import com.querydsl.core.types.Predicate;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class DefaultTokenService
implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenService.class);
    private static final int MAX_GENERATE_TOKEN_ID_RETRY = 10;
    private final TokenRepository tokenRepository;
    private final TokenGeneratorService tokenGeneratorService;
    private final UserManager userManager;
    private final Clock utcClock;
    private final TokenEventPublisher tokenEventPublisher;
    private final CrowdService crowdService;

    public DefaultTokenService(TokenRepository tokenRepository, TokenGeneratorService tokenGeneratorService, UserManager userManager, CrowdService crowdService, Clock utcClock, TokenEventPublisher tokenEventPublisher) {
        this.tokenRepository = tokenRepository;
        this.tokenGeneratorService = tokenGeneratorService;
        this.userManager = userManager;
        this.crowdService = crowdService;
        this.utcClock = utcClock;
        this.tokenEventPublisher = tokenEventPublisher;
    }

    @Override
    @Nonnull
    public TokenDTO create(@Nonnull UserKey userKey, @Nonnull String tokenName, @Null Integer tokenExpirationDays) {
        int attempts = 0;
        while (attempts++ < 10) {
            logger.trace("Attempting to create a token, attempt [{}]", (Object)attempts);
            GeneratedToken token = this.tokenGeneratorService.createToken();
            ZonedDateTime currentUtcTime = ZonedDateTime.now(this.utcClock);
            if (this.tokenRepository.existsByTokenId(token.getTokenId())) continue;
            logger.trace("Token with id [{}] does not already exist, so creating token...", (Object)token.getTokenId());
            TokenDTO rawToken = TokenDTO.builder().hashedToken(token.getHashedToken()).createdAt(this.getDateFromZonedDateTime(currentUtcTime)).name(tokenName).expiringAt(this.getExpirationDate(currentUtcTime, tokenExpirationDays)).tokenId(token.getTokenId()).userKey(userKey.getStringValue()).rawToken(token.getRawToken()).notificationState(NotificationState.NOT_SENT).build();
            TokenDTO newToken = this.tokenRepository.save(rawToken);
            logger.trace("Token saved: [{}]", (Object)newToken);
            this.tokenEventPublisher.tokenCreatedEvent(newToken, userKey.getStringValue());
            return newToken;
        }
        throw new CreateTokenFailedException("Unable to create token, tokenId already exists");
    }

    private Date getDateFromZonedDateTime(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    @Override
    public int delete(@Nullable UserKey deletingUser, @Nonnull Predicate predicate) {
        Iterable tokensToDelete = this.tokenRepository.findAll(predicate);
        if (!tokensToDelete.isEmpty()) {
            this.tokenRepository.deleteAll(tokensToDelete.toArray(new TokenDTO[0]));
            tokensToDelete.forEach(token -> {
                logger.trace("Token deleted: [{}]", token);
                this.tokenEventPublisher.tokenDeletedEvent((TokenDTO)token, deletingUser != null ? deletingUser.getStringValue() : null);
            });
        }
        return tokensToDelete.size();
    }

    private Date getExpirationDate(ZonedDateTime currentUtcTime, Integer tokenExpirationDays) {
        return tokenExpirationDays != null ? Date.from(currentUtcTime.plusDays(tokenExpirationDays.intValue()).toInstant()) : TokenDTO.NON_EXPIRING_DATE;
    }

    @Override
    public Page<TokenDTO> search(@Nonnull RestTokenSearchRequest tokenSearchRequest) {
        Sort.Direction direction = Sort.Direction.fromString(tokenSearchRequest.getOrderBy());
        Sort sortBy = Sort.by(direction, tokenSearchRequest.getSortBy()).and(Sort.by(Sort.Direction.DESC, "createdAt"));
        PageRequest pageSortOrder = PageRequest.of(tokenSearchRequest.getPage(), tokenSearchRequest.getLimit(), sortBy);
        Predicate predicate = new SearchFilterPredicateBuilder().build(tokenSearchRequest);
        return this.tokenRepository.findAll(predicate, pageSortOrder);
    }

    @Override
    public List<UserProfile> searchForUsers(@Nullable String nameExpression, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        UserQuery query = new UserQuery(User.class, DefaultTokenService.createUserSearchCriteria(nameExpression), 0, limit);
        return StreamSupport.stream(this.crowdService.search((Query)query).spliterator(), false).map(this::crowdUserToSalUser).collect(Collectors.toList());
    }

    @VisibleForTesting
    public static SearchRestriction createUserSearchCriteria(String nameExpression) {
        if (Strings.isNullOrEmpty((String)nameExpression)) {
            return NullRestrictionImpl.INSTANCE;
        }
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, new SearchRestriction[]{new TermRestriction(UserTermKeys.USERNAME, MatchMode.CONTAINS, (Object)nameExpression), new TermRestriction(UserTermKeys.DISPLAY_NAME, MatchMode.CONTAINS, (Object)nameExpression), new TermRestriction(UserTermKeys.EMAIL, MatchMode.CONTAINS, (Object)nameExpression)});
    }

    private UserProfile crowdUserToSalUser(User crowdUser) {
        return this.userManager.getUserProfile(crowdUser.getName());
    }
}

