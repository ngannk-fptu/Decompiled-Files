/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.expression;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public abstract class AbstractSecurityExpressionHandler<T>
implements SecurityExpressionHandler<T>,
ApplicationContextAware {
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private BeanResolver beanResolver;
    private RoleHierarchy roleHierarchy;
    private PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();

    @Override
    public final ExpressionParser getExpressionParser() {
        return this.expressionParser;
    }

    public final void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull((Object)expressionParser, (String)"expressionParser cannot be null");
        this.expressionParser = expressionParser;
    }

    @Override
    public final EvaluationContext createEvaluationContext(Authentication authentication, T invocation) {
        SecurityExpressionOperations root = this.createSecurityExpressionRoot(authentication, invocation);
        StandardEvaluationContext ctx = this.createEvaluationContextInternal(authentication, invocation);
        ctx.setBeanResolver(this.beanResolver);
        ctx.setRootObject((Object)root);
        return ctx;
    }

    protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, T invocation) {
        return new StandardEvaluationContext();
    }

    protected abstract SecurityExpressionOperations createSecurityExpressionRoot(Authentication var1, T var2);

    protected RoleHierarchy getRoleHierarchy() {
        return this.roleHierarchy;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    protected PermissionEvaluator getPermissionEvaluator() {
        return this.permissionEvaluator;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    protected BeanResolver getBeanResolver() {
        return this.beanResolver;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.beanResolver = new BeanFactoryResolver((BeanFactory)applicationContext);
    }
}

