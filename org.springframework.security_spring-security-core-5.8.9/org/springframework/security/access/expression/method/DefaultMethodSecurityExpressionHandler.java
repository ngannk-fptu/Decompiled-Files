/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.log.LogMessage
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.expression.method;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.log.LogMessage;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.PermissionCacheOptimizer;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityEvaluationContext;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.expression.method.MethodSecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer;
import org.springframework.util.Assert;

public class DefaultMethodSecurityExpressionHandler
extends AbstractSecurityExpressionHandler<MethodInvocation>
implements MethodSecurityExpressionHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultSecurityParameterNameDiscoverer();
    private PermissionCacheOptimizer permissionCacheOptimizer = null;
    private String defaultRolePrefix = "ROLE_";

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication auth, MethodInvocation mi) {
        return new MethodSecurityEvaluationContext(auth, mi, this.getParameterNameDiscoverer());
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        MethodSecurityExpressionOperations root = this.createSecurityExpressionRoot(authentication, mi);
        MethodSecurityEvaluationContext ctx = new MethodSecurityEvaluationContext(root, mi, this.getParameterNameDiscoverer());
        ctx.setBeanResolver(this.getBeanResolver());
        return ctx;
    }

    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        return this.createSecurityExpressionRoot(() -> authentication, invocation);
    }

    private MethodSecurityExpressionOperations createSecurityExpressionRoot(Supplier<Authentication> authentication, MethodInvocation invocation) {
        MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.getTrustResolver());
        root.setRoleHierarchy(this.getRoleHierarchy());
        root.setDefaultRolePrefix(this.getDefaultRolePrefix());
        return root;
    }

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
        MethodSecurityExpressionOperations rootObject = (MethodSecurityExpressionOperations)ctx.getRootObject().getValue();
        this.logger.debug((Object)LogMessage.format((String)"Filtering with expression: %s", (Object)filterExpression.getExpressionString()));
        if (filterTarget instanceof Collection) {
            return this.filterCollection((Collection)filterTarget, filterExpression, ctx, rootObject);
        }
        if (filterTarget.getClass().isArray()) {
            return this.filterArray((Object[])filterTarget, filterExpression, ctx, rootObject);
        }
        if (filterTarget instanceof Map) {
            return this.filterMap((Map)filterTarget, filterExpression, ctx, rootObject);
        }
        if (filterTarget instanceof Stream) {
            return this.filterStream((Stream)filterTarget, filterExpression, ctx, rootObject);
        }
        throw new IllegalArgumentException("Filter target must be a collection, array, map or stream type, but was " + filterTarget);
    }

    private <T> Object filterCollection(Collection<T> filterTarget, Expression filterExpression, EvaluationContext ctx, MethodSecurityExpressionOperations rootObject) {
        this.logger.debug((Object)LogMessage.format((String)"Filtering collection with %s elements", (Object)filterTarget.size()));
        ArrayList<T> retain = new ArrayList<T>(filterTarget.size());
        if (this.permissionCacheOptimizer != null) {
            this.permissionCacheOptimizer.cachePermissionsFor(rootObject.getAuthentication(), filterTarget);
        }
        for (T filterObject : filterTarget) {
            rootObject.setFilterObject(filterObject);
            if (!ExpressionUtils.evaluateAsBoolean(filterExpression, ctx)) continue;
            retain.add(filterObject);
        }
        this.logger.debug((Object)LogMessage.format((String)"Retaining elements: %s", retain));
        filterTarget.clear();
        filterTarget.addAll(retain);
        return filterTarget;
    }

    private Object filterArray(Object[] filterTarget, Expression filterExpression, EvaluationContext ctx, MethodSecurityExpressionOperations rootObject) {
        ArrayList<Object> retain = new ArrayList<Object>(filterTarget.length);
        this.logger.debug((Object)LogMessage.format((String)"Filtering array with %s elements", (Object)filterTarget.length));
        if (this.permissionCacheOptimizer != null) {
            this.permissionCacheOptimizer.cachePermissionsFor(rootObject.getAuthentication(), Arrays.asList(filterTarget));
        }
        for (Object filterObject : filterTarget) {
            rootObject.setFilterObject(filterObject);
            if (!ExpressionUtils.evaluateAsBoolean(filterExpression, ctx)) continue;
            retain.add(filterObject);
        }
        this.logger.debug((Object)LogMessage.format((String)"Retaining elements: %s", retain));
        Object[] filtered = (Object[])Array.newInstance(filterTarget.getClass().getComponentType(), retain.size());
        for (int i = 0; i < retain.size(); ++i) {
            filtered[i] = retain.get(i);
        }
        return filtered;
    }

    private <K, V> Object filterMap(Map<K, V> filterTarget, Expression filterExpression, EvaluationContext ctx, MethodSecurityExpressionOperations rootObject) {
        LinkedHashMap<K, V> retain = new LinkedHashMap<K, V>(filterTarget.size());
        this.logger.debug((Object)LogMessage.format((String)"Filtering map with %s elements", (Object)filterTarget.size()));
        for (Map.Entry<K, V> filterObject : filterTarget.entrySet()) {
            rootObject.setFilterObject(filterObject);
            if (!ExpressionUtils.evaluateAsBoolean(filterExpression, ctx)) continue;
            retain.put(filterObject.getKey(), filterObject.getValue());
        }
        this.logger.debug((Object)LogMessage.format((String)"Retaining elements: %s", retain));
        filterTarget.clear();
        filterTarget.putAll(retain);
        return filterTarget;
    }

    private Object filterStream(Stream<?> filterTarget, Expression filterExpression, EvaluationContext ctx, MethodSecurityExpressionOperations rootObject) {
        return filterTarget.filter(filterObject -> {
            rootObject.setFilterObject(filterObject);
            return ExpressionUtils.evaluateAsBoolean(filterExpression, ctx);
        }).onClose(filterTarget::close);
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        Assert.notNull((Object)trustResolver, (String)"trustResolver cannot be null");
        this.trustResolver = trustResolver;
    }

    protected AuthenticationTrustResolver getTrustResolver() {
        return this.trustResolver;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    public void setPermissionCacheOptimizer(PermissionCacheOptimizer permissionCacheOptimizer) {
        this.permissionCacheOptimizer = permissionCacheOptimizer;
    }

    @Override
    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        ((MethodSecurityExpressionOperations)ctx.getRootObject().getValue()).setReturnObject(returnObject);
    }

    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    protected String getDefaultRolePrefix() {
        return this.defaultRolePrefix;
    }
}

