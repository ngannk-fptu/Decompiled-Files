/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.repository.config.QueryCreatorType
 *  org.springframework.data.repository.config.DefaultRepositoryBaseClass
 *  org.springframework.data.repository.query.QueryLookupStrategy$Key
 */
package org.springframework.vault.repository.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.keyvalue.repository.config.QueryCreatorType;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.vault.repository.configuration.VaultRepositoriesRegistrar;
import org.springframework.vault.repository.query.VaultPartTreeQuery;
import org.springframework.vault.repository.query.VaultQueryCreator;
import org.springframework.vault.repository.support.VaultRepositoryFactoryBean;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value={VaultRepositoriesRegistrar.class})
@QueryCreatorType(value=VaultQueryCreator.class, repositoryQueryType=VaultPartTreeQuery.class)
public @interface EnableVaultRepositories {
    public String[] value() default {};

    public String[] basePackages() default {};

    public Class<?>[] basePackageClasses() default {};

    public ComponentScan.Filter[] excludeFilters() default {};

    public ComponentScan.Filter[] includeFilters() default {};

    public String repositoryImplementationPostfix() default "Impl";

    public String namedQueriesLocation() default "";

    public QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    public Class<?> repositoryFactoryBeanClass() default VaultRepositoryFactoryBean.class;

    public Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    public String keyValueTemplateRef() default "vaultKeyValueTemplate";

    public boolean considerNestedRepositories() default false;

    public String vaultTemplateRef() default "vaultTemplate";
}

