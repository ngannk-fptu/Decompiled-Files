/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.db;

import com.atlassian.pats.db.NotificationState;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.PrimaryKey;
import java.sql.Timestamp;

public class QAOToken
extends EnhancedRelationalPathBase<TokenDTO> {
    private static final long serialVersionUID = 1164723035L;
    public static final QAOToken QAOTOKEN = Tables.TOKEN;
    public final DateTimePath<Timestamp> createdAt = this.createDateTime("createdAt", Timestamp.class);
    public final StringPath name = this.createString("name");
    public final DateTimePath<Timestamp> expiringAt = this.createDateTime("expiringAt", Timestamp.class);
    public final NumberPath<Long> id = this.createNumber("id", Long.class);
    public final DateTimePath<Timestamp> lastAccessedAt = this.createDateTime("lastAccessedAt", Timestamp.class);
    public final StringPath hashedToken = this.createString("hashedToken");
    public final StringPath tokenId = this.createString("tokenId");
    public final StringPath userKey = this.createString("userKey");
    public final EnumPath<NotificationState> notificationState = this.createEnum("notificationState", NotificationState.class);
    public final PrimaryKey<TokenDTO> constraintA = this.createPrimaryKey(this.id);

    public QAOToken(String prefix) {
        super(TokenDTO.class, prefix + "_PERSONAL_TOKEN");
        this.addMetadata();
    }

    public void addMetadata() {
        this.addMetadata(this.createdAt, ColumnMetadata.named("CREATED_AT").withIndex(1).ofType(93).withSize(26).withDigits(6).notNull());
        this.addMetadata(this.name, ColumnMetadata.named("NAME").withIndex(2).ofType(12).withSize(255));
        this.addMetadata(this.expiringAt, ColumnMetadata.named("EXPIRING_AT").withIndex(3).ofType(93).withSize(26).withDigits(6));
        this.addMetadata(this.id, ColumnMetadata.named("ID").withIndex(4).ofType(-5).withSize(19).notNull());
        this.addMetadata(this.lastAccessedAt, ColumnMetadata.named("LAST_ACCESSED_AT").withIndex(5).ofType(93).withSize(26).withDigits(6));
        this.addMetadata(this.hashedToken, ColumnMetadata.named("HASHED_TOKEN").withIndex(6).ofType(12).withSize(255).notNull());
        this.addMetadata(this.tokenId, ColumnMetadata.named("TOKEN_ID").withIndex(7).ofType(12).withSize(255).notNull());
        this.addMetadata(this.userKey, ColumnMetadata.named("USER_KEY").withIndex(8).ofType(12).withSize(255).notNull());
        this.addMetadata(this.notificationState, ColumnMetadata.named("NOTIFICATION_STATE").withIndex(9).ofType(12).withSize(255).notNull());
    }
}

