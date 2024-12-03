/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  org.hibernate.EmptyInterceptor
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.encoding;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.encoding.SupplementaryCharacterUtils;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLUTF8SupportInterceptor
extends EmptyInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MySQLUTF8SupportInterceptor.class);
    private final boolean isMySQLUTF8config;

    public MySQLUTF8SupportInterceptor(SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig) {
        this.isMySQLUTF8config = MySQLUTF8SupportInterceptor.isMySQLUTF8config(Objects.requireNonNull(databaseHelper), Objects.requireNonNull(hibernateConfig));
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        this.validateBodyContent(entity);
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        this.validateBodyContent(entity);
        return super.onSave(entity, id, state, propertyNames, types);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static boolean isMySQLUTF8config(SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig) {
        if (!hibernateConfig.isMySql()) return false;
        String sql = "SELECT DEFAULT_COLLATION_NAME \nFROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ? AND DEFAULT_COLLATION_NAME IN (?, ?)";
        try (Connection connection = databaseHelper.getConnection(hibernateConfig.getHibernateProperties());
             PreparedStatement statement = connection.prepareStatement("SELECT DEFAULT_COLLATION_NAME \nFROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ? AND DEFAULT_COLLATION_NAME IN (?, ?)");){
            statement.setString(1, connection.getCatalog());
            statement.setString(2, "utf8_bin");
            statement.setString(3, "utf8mb3_bin");
            try (ResultSet rs = statement.executeQuery();){
                if (!rs.next()) return false;
                boolean bl = true;
                return bl;
            }
        }
        catch (SQLException e) {
            log.error("There is some problem finding the collation", (Throwable)e);
        }
        return false;
    }

    private void validateBodyContent(Object entity) {
        if (this.isMySQLUTF8config) {
            if (entity instanceof ContentEntityObject && Objects.nonNull(((ContentEntityObject)entity).getTitle())) {
                this.validateText(((ContentEntityObject)entity).getTitle());
            }
            if (entity instanceof BodyContent) {
                BodyContent bodyContent = (BodyContent)entity;
                this.validateText(bodyContent.getBody());
            }
        }
    }

    private void validateText(String text) {
        Optional<String> supplementaryBodyCharacter = SupplementaryCharacterUtils.getFirstSupplementaryCharacter(text);
        supplementaryBodyCharacter.ifPresent(a -> {
            throw new BadRequestException(String.format("Unsupported character found in content: %s", a));
        });
    }
}

