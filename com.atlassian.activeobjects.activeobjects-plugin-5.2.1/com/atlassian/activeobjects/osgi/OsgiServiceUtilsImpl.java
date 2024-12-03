/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.osgi;

import com.atlassian.activeobjects.osgi.OsgiServiceUtils;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiServiceUtilsImpl
implements OsgiServiceUtils {
    private static final String PROPERTY_KEY = "com.atlassian.plugin.key";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public <S, O extends S> ServiceRegistration registerService(Bundle bundle, Class<S> ifce, O obj) {
        Preconditions.checkNotNull((Object)bundle);
        Preconditions.checkNotNull(obj);
        Map<String, String> properties = this.getProperties(bundle);
        this.logger.debug("Registering service {} with interface {} and properties {}", new Object[]{obj, ifce.getName(), properties});
        return this.getContext(bundle).registerService(ifce.getName(), obj, new Hashtable<String, String>(properties));
    }

    Map<String, String> getProperties(Bundle bundle) {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put(PROPERTY_KEY, bundle.getSymbolicName());
        return props;
    }

    private BundleContext getContext(Bundle bundle) {
        return bundle.getBundleContext();
    }
}

