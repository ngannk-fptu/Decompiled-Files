/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public interface Loadable
extends EntityPersister {
    public static final String ROWID_ALIAS = "rowid_";

    public boolean hasSubclasses();

    public Type getDiscriminatorType();

    public Object getDiscriminatorValue();

    public String getSubclassForDiscriminatorValue(Object var1);

    public String[] getIdentifierColumnNames();

    public String[] getIdentifierAliases(String var1);

    public String[] getPropertyAliases(String var1, int var2);

    public String[] getPropertyColumnNames(int var1);

    public String getDiscriminatorAlias(String var1);

    public String getDiscriminatorColumnName();

    public boolean hasRowId();

    default public Object[] hydrate(ResultSet rs, Serializable id, Object object, Loadable rootLoadable, String[][] suffixedPropertyColumns, boolean forceEager, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        return this.hydrate(rs, id, object, rootLoadable, suffixedPropertyColumns, forceEager, null, session);
    }

    public Object[] hydrate(ResultSet var1, Serializable var2, Object var3, Loadable var4, String[][] var5, boolean var6, boolean[] var7, SharedSessionContractImplementor var8) throws SQLException, HibernateException;

    public boolean isAbstract();

    public void registerAffectingFetchProfile(String var1);

    public String getTableAliasForColumn(String var1, String var2);
}

