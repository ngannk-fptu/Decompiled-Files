/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.ConnectionSpec
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.cci.core.support;

import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jca.cci.CannotGetCciConnectionException;
import org.springframework.jca.cci.connection.ConnectionFactoryUtils;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public abstract class CciDaoSupport
extends DaoSupport {
    @Nullable
    private CciTemplate cciTemplate;

    public final void setConnectionFactory(ConnectionFactory connectionFactory) {
        if (this.cciTemplate == null || connectionFactory != this.cciTemplate.getConnectionFactory()) {
            this.cciTemplate = this.createCciTemplate(connectionFactory);
        }
    }

    protected CciTemplate createCciTemplate(ConnectionFactory connectionFactory) {
        return new CciTemplate(connectionFactory);
    }

    @Nullable
    public final ConnectionFactory getConnectionFactory() {
        return this.cciTemplate != null ? this.cciTemplate.getConnectionFactory() : null;
    }

    public final void setCciTemplate(CciTemplate cciTemplate) {
        this.cciTemplate = cciTemplate;
    }

    @Nullable
    public final CciTemplate getCciTemplate() {
        return this.cciTemplate;
    }

    @Override
    protected final void checkDaoConfig() {
        if (this.cciTemplate == null) {
            throw new IllegalArgumentException("'connectionFactory' or 'cciTemplate' is required");
        }
    }

    protected final CciTemplate getCciTemplate(ConnectionSpec connectionSpec) {
        CciTemplate cciTemplate = this.getCciTemplate();
        Assert.state((cciTemplate != null ? 1 : 0) != 0, (String)"No CciTemplate set");
        return cciTemplate.getDerivedTemplate(connectionSpec);
    }

    protected final Connection getConnection() throws CannotGetCciConnectionException {
        ConnectionFactory connectionFactory = this.getConnectionFactory();
        Assert.state((connectionFactory != null ? 1 : 0) != 0, (String)"No ConnectionFactory set");
        return ConnectionFactoryUtils.getConnection(connectionFactory);
    }

    protected final void releaseConnection(Connection con) {
        ConnectionFactoryUtils.releaseConnection(con, this.getConnectionFactory());
    }
}

