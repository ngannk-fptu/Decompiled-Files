/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.rest;

import com.atlassian.pats.db.QAOToken;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.rest.OptionalBooleanBuilder;
import com.atlassian.pats.rest.RestTokenSearchRequest;
import com.querydsl.core.types.Predicate;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class SearchFilterPredicateBuilder {
    private final QAOToken TOKEN = Tables.TOKEN;

    public Predicate build(RestTokenSearchRequest filter) {
        return new OptionalBooleanBuilder(this.TOKEN.userKey.isNotNull()).notEmptyAnd(this.TOKEN.userKey::in, filter.getUserKeys()).notEmptyAnd(this.TOKEN.name::containsIgnoreCase, filter.getName()).notNullAnd(this.TOKEN.createdAt::goe, this.getNullableTimestamp(filter.getTokenCreatedDateFrom())).notNullAnd(this.TOKEN.createdAt::loe, this.getNullableTimestamp(filter.getTokenCreatedDateTo())).notNullAnd(this.TOKEN.expiringAt::goe, this.getNullableTimestamp(filter.getTokenExpiryDateFrom())).notNullAnd(this.TOKEN.expiringAt::loe, this.getNullableTimestamp(filter.getTokenExpiryDateTo())).notNullAnd(this.TOKEN.lastAccessedAt::goe, this.getNullableTimestamp(filter.getLastAuthenticatedDateFrom())).notNullAnd(this.TOKEN.lastAccessedAt::loe, this.getNullableTimestamp(filter.getLastAuthenticatedDateTo())).notNullAndValueExistenceCheck(this.TOKEN.lastAccessedAt, filter.getIsUsed()).build();
    }

    private Timestamp getNullableTimestamp(String tokenCreatedDate) {
        if (tokenCreatedDate != null) {
            ZonedDateTime dateTime = ZonedDateTime.parse(tokenCreatedDate);
            ZonedDateTime zdtInstanceAtUTC = dateTime.withZoneSameInstant(ZoneOffset.UTC);
            return Timestamp.from(zdtInstanceAtUTC.toInstant());
        }
        return null;
    }
}

