/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.ComponentScan$Filter
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.data.activeobjects.repository.config;

import com.atlassian.data.activeobjects.repository.config.ActiveObjectsRepositoriesRegistrar;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsRepositoryFactoryBean;
import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value={ActiveObjectsRepositoriesRegistrar.class})
public @interface EnableActiveObjectsRepositories {
    public String[] value() default {};

    public String[] basePackages() default {};

    public Class<?>[] basePackageClasses() default {};

    public ComponentScan.Filter[] includeFilters() default {};

    public ComponentScan.Filter[] excludeFilters() default {};

    public String repositoryImplementationPostfix() default "Impl";

    public String namedQueriesLocation() default "";

    public QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    public Class<?> repositoryFactoryBeanClass() default ActiveObjectsRepositoryFactoryBean.class;

    public Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    public boolean considerNestedRepositories() default false;

    public PocketKnifeQuerydslPredicateExecutor.TransactionType transactionType() default PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION;

    public BootstrapMode bootstrapMode() default BootstrapMode.DEFAULT;

    public char escapeCharacter() default 92;
}

