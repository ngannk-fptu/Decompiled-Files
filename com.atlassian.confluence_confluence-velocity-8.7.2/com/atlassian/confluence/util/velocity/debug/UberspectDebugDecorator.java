/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.velocity.runtime.RuntimeLogger
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.Log
 *  org.apache.velocity.util.RuntimeServicesAware
 *  org.apache.velocity.util.introspection.Info
 *  org.apache.velocity.util.introspection.Uberspect
 *  org.apache.velocity.util.introspection.UberspectImpl
 *  org.apache.velocity.util.introspection.UberspectLoggable
 *  org.apache.velocity.util.introspection.VelMethod
 *  org.apache.velocity.util.introspection.VelPropertyGet
 *  org.apache.velocity.util.introspection.VelPropertySet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity.debug;

import com.atlassian.confluence.util.velocity.debug.VelMethodDebugDecorator;
import java.util.Iterator;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.Uberspect;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.UberspectLoggable;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.apache.velocity.util.introspection.VelPropertySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UberspectDebugDecorator
implements Uberspect,
UberspectLoggable,
RuntimeServicesAware {
    @Deprecated
    public static final org.apache.log4j.Logger VELOCITY_LOG = org.apache.log4j.Logger.getLogger((String)"VELOCITY");
    private static final Logger log = LoggerFactory.getLogger(UberspectDebugDecorator.class);
    private final UberspectImpl delegate = new UberspectImpl();

    public void init() throws Exception {
        log.info("start init");
        this.delegate.init();
    }

    public Iterator getIterator(Object obj, Info info) throws Exception {
        log.debug("getIterator [ {} {} ]", obj, (Object)info);
        return this.delegate.getIterator(obj, info);
    }

    public VelMethod getMethod(Object obj, String method, Object[] args, Info info) throws Exception {
        log.debug("getMethod [ {} {} {} {} ]", new Object[]{obj, method, args.length, info});
        VelMethod decorator = this.delegate.getMethod(obj, method, args, info);
        if (decorator == null) {
            if (log.isDebugEnabled()) {
                log.debug("resolving method [{}#{}] [{}] not found", new Object[]{obj.getClass().getName(), method, info});
            }
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("resolving method [{}#{}] [{}]", new Object[]{obj.getClass().getName(), method, info});
        }
        return new VelMethodDebugDecorator(decorator);
    }

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info info) throws Exception {
        log.debug("getPropertyGet [ {} {} {} ]", new Object[]{obj, identifier, info});
        return this.delegate.getPropertyGet(obj, identifier, info);
    }

    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info info) throws Exception {
        log.debug("getPropertySet [ {} {} {} {}", new Object[]{obj, identifier, arg, info});
        return this.delegate.getPropertySet(obj, identifier, arg, info);
    }

    public void setLog(Log log) {
        this.delegate.setLog(log);
    }

    public void setRuntimeLogger(RuntimeLogger logger) {
        this.delegate.setRuntimeLogger(logger);
    }

    public void setRuntimeServices(RuntimeServices runtimeServices) {
        this.delegate.setRuntimeServices(runtimeServices);
    }
}

