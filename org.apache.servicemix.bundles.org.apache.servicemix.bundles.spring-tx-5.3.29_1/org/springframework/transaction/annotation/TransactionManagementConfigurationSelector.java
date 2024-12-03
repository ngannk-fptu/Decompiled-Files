/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.AdviceMode
 *  org.springframework.context.annotation.AdviceModeImportSelector
 *  org.springframework.context.annotation.AutoProxyRegistrar
 *  org.springframework.util.ClassUtils
 */
package org.springframework.transaction.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.util.ClassUtils;

public class TransactionManagementConfigurationSelector
extends AdviceModeImportSelector<EnableTransactionManagement> {
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY: {
                return new String[]{AutoProxyRegistrar.class.getName(), ProxyTransactionManagementConfiguration.class.getName()};
            }
            case ASPECTJ: {
                return new String[]{this.determineTransactionAspectClass()};
            }
        }
        return null;
    }

    private String determineTransactionAspectClass() {
        return ClassUtils.isPresent((String)"javax.transaction.Transactional", (ClassLoader)((Object)((Object)this)).getClass().getClassLoader()) ? "org.springframework.transaction.aspectj.AspectJJtaTransactionManagementConfiguration" : "org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration";
    }
}

