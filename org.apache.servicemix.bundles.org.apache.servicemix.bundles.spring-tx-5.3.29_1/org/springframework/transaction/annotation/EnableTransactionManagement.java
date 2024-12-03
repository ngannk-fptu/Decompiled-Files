/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.AdviceMode
 *  org.springframework.context.annotation.Import
 */
package org.springframework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={TransactionManagementConfigurationSelector.class})
public @interface EnableTransactionManagement {
    public boolean proxyTargetClass() default false;

    public AdviceMode mode() default AdviceMode.PROXY;

    public int order() default 0x7FFFFFFF;
}

