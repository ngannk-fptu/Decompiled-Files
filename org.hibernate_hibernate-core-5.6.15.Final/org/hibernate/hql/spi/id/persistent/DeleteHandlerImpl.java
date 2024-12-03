/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.persistent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
import org.hibernate.hql.spi.id.persistent.Helper;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.SelectValues;

public class DeleteHandlerImpl
extends TableBasedDeleteHandlerImpl {
    private final IdTableInfo idTableInfo;

    public DeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker, IdTableInfo idTableInfo) {
        super(factory, walker, idTableInfo);
        this.idTableInfo = idTableInfo;
    }

    @Override
    protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
        selectClause.addParameter(1, 36);
    }

    @Override
    protected String generateIdSubselect(Queryable persister, IdTableInfo idTableInfo) {
        return super.generateIdSubselect(persister, idTableInfo) + " where " + "hib_sess_id" + "=?";
    }

    @Override
    protected String generateIdSubselect(Queryable persister, AbstractCollectionPersister cPersister, IdTableInfo idTableInfo) {
        return super.generateIdSubselect(persister, cPersister, idTableInfo) + " where " + "hib_sess_id" + "=?";
    }

    @Override
    protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SharedSessionContractImplementor session, int pos) throws SQLException {
        Helper.INSTANCE.bindSessionIdentifier(ps, session, pos);
        return 1;
    }

    @Override
    protected void handleAddedParametersOnDelete(PreparedStatement ps, SharedSessionContractImplementor session) throws SQLException {
        Helper.INSTANCE.bindSessionIdentifier(ps, session, 1);
    }

    @Override
    protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
        Helper.INSTANCE.cleanUpRows(this.idTableInfo.getQualifiedIdTableName(), session);
    }
}

