/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductSchemaProvider {
    private final TransactionalExecutorFactory executorFactory;

    @Autowired
    public ProductSchemaProvider(@ComponentImport TransactionalExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

    public Optional<String> getProductSchema() {
        return Optional.ofNullable(this.executorFactory.createReadOnly().getSchemaName().getOrNull());
    }
}

