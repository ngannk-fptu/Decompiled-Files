/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.rdbms.ConnectionCallback
 *  com.atlassian.sal.spi.HostConnectionAccessor
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.sal.spring.connection;

import com.atlassian.sal.api.rdbms.ConnectionCallback;
import com.atlassian.sal.spi.HostConnectionAccessor;
import io.atlassian.fugue.Option;
import java.sql.Connection;
import javax.annotation.Nonnull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringHostConnectionAccessor
implements HostConnectionAccessor {
    private final ConnectionProvider connectionProvider;
    private final PlatformTransactionManager transactionManager;

    public SpringHostConnectionAccessor(@Nonnull ConnectionProvider connectionProvider, @Nonnull PlatformTransactionManager transactionManager) {
        this.connectionProvider = connectionProvider;
        this.transactionManager = transactionManager;
    }

    public <A> A execute(boolean readOnly, boolean newTransaction, @Nonnull ConnectionCallback<A> callback) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName("SALSpringTx");
        transactionDefinition.setReadOnly(readOnly);
        transactionDefinition.setPropagationBehavior(newTransaction ? 3 : 0);
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition);
        return (A)transactionTemplate.execute(status -> {
            Connection connection = this.connectionProvider.getConnection();
            return callback.execute(connection);
        });
    }

    @Nonnull
    public Option<String> getSchemaName() {
        return this.connectionProvider.getSchemaName();
    }

    public static interface ConnectionProvider {
        @Nonnull
        public Connection getConnection();

        @Nonnull
        public Option<String> getSchemaName();
    }
}

