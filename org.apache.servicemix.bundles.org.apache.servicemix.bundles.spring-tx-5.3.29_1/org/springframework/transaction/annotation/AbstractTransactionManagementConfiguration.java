/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.ImportAware
 *  org.springframework.context.annotation.Role
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.transaction.annotation;

import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.util.CollectionUtils;

@Configuration
public abstract class AbstractTransactionManagementConfiguration
implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableTx;
    @Nullable
    protected TransactionManager txManager;

    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableTx = AnnotationAttributes.fromMap((Map)importMetadata.getAnnotationAttributes(EnableTransactionManagement.class.getName()));
        if (this.enableTx == null) {
            throw new IllegalArgumentException("@EnableTransactionManagement is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required=false)
    void setConfigurers(Collection<TransactionManagementConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("Only one TransactionManagementConfigurer may exist");
        }
        TransactionManagementConfigurer configurer = configurers.iterator().next();
        this.txManager = configurer.annotationDrivenTransactionManager();
    }

    @Bean(name={"org.springframework.transaction.config.internalTransactionalEventListenerFactory"})
    @Role(value=2)
    public static TransactionalEventListenerFactory transactionalEventListenerFactory() {
        return new TransactionalEventListenerFactory();
    }
}

