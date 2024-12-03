/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.db;

import com.atlassian.data.activeobjects.repository.ActiveObjectsNoOpRepository;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeCrudQuerydslPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.pats.db.AOToken;
import com.atlassian.pats.db.QAOToken;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLQuery;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TokenRepository
extends ActiveObjectsNoOpRepository<AOToken, Long>,
PocketKnifeCrudQuerydslPredicateExecutor<TokenDTO, Long> {
    public List<TokenDTO> deleteByExpiringAtIsBefore(Date var1);

    public Stream<TokenDTO> findAllByUserKey(String var1);

    public Optional<TokenDTO> getByTokenIdAndExpiringAtIsAfter(String var1, ZonedDateTime var2);

    public boolean existsByTokenId(String var1);

    public long countAllByUserKey(String var1);

    default public List<String> getDistinctUserKeys() {
        return this.executeQuery(PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION, databaseConnection -> ((SQLQuery)((QueryBase)((Object)databaseConnection.from(QAOToken.QAOTOKEN).select((Expression)Tables.TOKEN.userKey))).distinct()).fetch(), OnRollback.NOOP);
    }
}

