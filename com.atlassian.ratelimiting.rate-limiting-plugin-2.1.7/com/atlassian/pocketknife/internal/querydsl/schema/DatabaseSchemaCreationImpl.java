/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  io.atlassian.fugue.Option
 *  javax.annotation.PostConstruct
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.internal.querydsl.cache.PKQCacheClearer;
import com.atlassian.pocketknife.internal.querydsl.schema.DatabaseSchemaCreation;
import com.atlassian.pocketknife.internal.querydsl.util.MemoizingResettingReference;
import com.atlassian.pocketknife.internal.querydsl.util.Unit;
import com.google.common.base.Function;
import io.atlassian.fugue.Option;
import javax.annotation.PostConstruct;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaCreationImpl
implements DatabaseSchemaCreation {
    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaCreationImpl.class);
    private static final String AO_SERVICE_NAME = "com.atlassian.activeobjects.external.ActiveObjects";
    private final MemoizingResettingReference<Unit, Unit> schemaCreatedDecison;
    private final BundleContext bundleContext;
    private final PKQCacheClearer cacheClearer;

    @Autowired
    public DatabaseSchemaCreationImpl(BundleContext bundleContext, PKQCacheClearer cacheClearer) {
        this.bundleContext = bundleContext;
        this.cacheClearer = cacheClearer;
        this.schemaCreatedDecison = new MemoizingResettingReference<Unit, Unit>(this.primeImpl());
    }

    @PostConstruct
    void postConstruction() {
        this.cacheClearer.registerCacheClearing(this.schemaCreatedDecison::reset);
    }

    @Override
    public void prime() {
        this.schemaCreatedDecison.get(Unit.VALUE);
    }

    private Function<Unit, Unit> primeImpl() {
        return input -> {
            this.getService(AO_SERVICE_NAME).foreach(this::invokeAo);
            return Unit.VALUE;
        };
    }

    private Option<Object> getService(String serviceName) {
        Option sRef = Option.option((Object)this.bundleContext.getServiceReference(serviceName));
        if (sRef.isDefined()) {
            return Option.option((Object)this.bundleContext.getService((ServiceReference)sRef.get()));
        }
        return Option.none();
    }

    private void invokeAo(Object ao) {
        try {
            log.debug("ActiveObjects found - invoking via reflection....");
            Reflect.on(ao).call("flushAll");
        }
        catch (ReflectException e) {
            log.warn("ActiveObjects method flushAll is not available : " + e.toString());
        }
    }
}

