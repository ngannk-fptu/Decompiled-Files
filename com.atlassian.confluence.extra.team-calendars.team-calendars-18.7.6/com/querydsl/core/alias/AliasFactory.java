/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.sf.cglib.proxy.Callback
 *  net.sf.cglib.proxy.Enhancer
 */
package com.querydsl.core.alias;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.QueryException;
import com.querydsl.core.alias.ManagedObject;
import com.querydsl.core.alias.PathFactory;
import com.querydsl.core.alias.PropertyAccessInvocationHandler;
import com.querydsl.core.alias.TypeSystem;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.PathMetadataFactory;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

class AliasFactory {
    private final ThreadLocal<Expression<?>> current = new ThreadLocal();
    private final PathFactory pathFactory;
    private final TypeSystem typeSystem;
    private final LoadingCache<Pair<Class<?>, String>, EntityPath<?>> pathCache;
    private final LoadingCache<Pair<Class<?>, Expression<?>>, ManagedObject> proxyCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Class<?>, Expression<?>>, ManagedObject>(){

        public ManagedObject load(Pair<Class<?>, Expression<?>> input) {
            return (ManagedObject)AliasFactory.this.createProxy(input.getFirst(), input.getSecond());
        }
    });

    public AliasFactory(final PathFactory pathFactory, TypeSystem typeSystem) {
        this.pathFactory = pathFactory;
        this.typeSystem = typeSystem;
        this.pathCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Class<?>, String>, EntityPath<?>>(){

            public EntityPath<?> load(Pair<Class<?>, String> input) {
                return (EntityPath)pathFactory.createEntityPath(input.getFirst(), PathMetadataFactory.forVariable(input.getSecond()));
            }
        });
    }

    public <A> A createAliasForExpr(Class<A> cl, Expression<? extends A> expr) {
        try {
            return (A)this.proxyCache.get(Pair.of(cl, expr));
        }
        catch (ExecutionException e) {
            throw new QueryException(e);
        }
    }

    public <A> A createAliasForProperty(Class<A> cl, Expression<?> path) {
        return this.createProxy(cl, path);
    }

    public <A> A createAliasForVariable(Class<A> cl, String var) {
        try {
            Expression path = (Expression)this.pathCache.get(Pair.of(cl, var));
            return (A)this.proxyCache.get(Pair.of(cl, path));
        }
        catch (ExecutionException e) {
            throw new QueryException(e);
        }
    }

    protected <A> A createProxy(Class<A> cl, Expression<?> path) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(AliasFactory.class.getClassLoader());
        if (cl.isInterface()) {
            enhancer.setInterfaces(new Class[]{cl, ManagedObject.class});
        } else {
            enhancer.setSuperclass(cl);
            enhancer.setInterfaces(new Class[]{ManagedObject.class});
        }
        PropertyAccessInvocationHandler handler = new PropertyAccessInvocationHandler(path, this, this.pathFactory, this.typeSystem);
        enhancer.setCallback((Callback)handler);
        return (A)enhancer.create();
    }

    @Nullable
    public <A extends Expression<?>> A getCurrent() {
        return (A)this.current.get();
    }

    @Nullable
    public <A extends Expression<?>> A getCurrentAndReset() {
        A rv = this.getCurrent();
        this.reset();
        return rv;
    }

    public void reset() {
        this.current.set(null);
    }

    public void setCurrent(Expression<?> expr) {
        this.current.set(expr);
    }
}

