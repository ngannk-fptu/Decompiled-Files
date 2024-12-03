/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.core.ao.dao.AoEntityOptOutIdentifierDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoEntityOptOutIdentifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class DefaultAoEntityOptOutIdentifierDao
implements AoEntityOptOutIdentifierDao {
    private final ActiveObjects ao;

    public DefaultAoEntityOptOutIdentifierDao(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public void delete(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        Objects.requireNonNull(optOutEntityIdentifiers, "optOutResourceIdentifiers");
        this.ao.executeInTransaction(() -> {
            optOutEntityIdentifiers.forEach(identifier -> {
                AoEntityOptOutIdentifier[] toBeDeleted = this.findWithoutTransaction((OptOutEntityIdentifier)identifier);
                if (toBeDeleted.length == 0) {
                    return;
                }
                this.ao.delete((RawEntity[])toBeDeleted);
            });
            return true;
        });
    }

    @Override
    @Nonnull
    public List<AoEntityOptOutIdentifier> get(int offset, int limit) throws IllegalArgumentException {
        if (offset < 0 || limit < 0) {
            throw new IllegalArgumentException(String.format("Offset and limit need to be positive but were %d and %d", offset, limit));
        }
        String orderByQuery = "ID ASC";
        return (List)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoEntityOptOutIdentifier.class, Query.select().offset(offset).limit(limit).order(orderByQuery))).collect(Collectors.toList()));
    }

    @Override
    @Nonnull
    public List<AoEntityOptOutIdentifier> save(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        Objects.requireNonNull(optOutEntityIdentifiers, "optOutResourceIdentifier");
        return (List)this.ao.executeInTransaction(() -> optOutEntityIdentifiers.stream().map(this::saveSingleOptOutIdentifierWithoutTransaction).collect(Collectors.toList()));
    }

    private AoEntityOptOutIdentifier saveSingleOptOutIdentifierWithoutTransaction(OptOutEntityIdentifier optOutEntityIdentifier) {
        AoEntityOptOutIdentifier[] aoEntityOptOutIdentifiers = this.findWithoutTransaction(optOutEntityIdentifier);
        if (aoEntityOptOutIdentifiers.length != 0) {
            return aoEntityOptOutIdentifiers[0];
        }
        AoEntityOptOutIdentifier aoEntityOptOutIdentifier = (AoEntityOptOutIdentifier)this.ao.create(AoEntityOptOutIdentifier.class, new DBParam[]{new DBParam("ENTITY_IDENTIFIER", (Object)optOutEntityIdentifier.getIdentifier()), new DBParam("ENTITY_TYPE", (Object)optOutEntityIdentifier.getType())});
        aoEntityOptOutIdentifier.save();
        return aoEntityOptOutIdentifier;
    }

    private AoEntityOptOutIdentifier[] findWithoutTransaction(OptOutEntityIdentifier optOutEntityIdentifier) {
        return (AoEntityOptOutIdentifier[])this.ao.find(AoEntityOptOutIdentifier.class, Query.select().where("ENTITY_IDENTIFIER = ? AND ENTITY_TYPE = ?", new Object[]{optOutEntityIdentifier.getIdentifier(), optOutEntityIdentifier.getType()}));
    }
}

