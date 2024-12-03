/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.util;

import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.eclipse.gemini.blueprint.util.internal.ServiceReferenceBasedMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class OsgiServiceReferenceUtils {
    private static final Log log = LogFactory.getLog(OsgiServiceReferenceUtils.class);

    public static ServiceReference getServiceReference(BundleContext bundleContext, String[] classes) {
        return OsgiServiceReferenceUtils.getServiceReference(bundleContext, classes, null);
    }

    public static ServiceReference getServiceReference(BundleContext bundleContext, String clazz, String filter) {
        ServiceReference[] refs = OsgiServiceReferenceUtils.getServiceReferences(bundleContext, clazz, filter);
        return OsgiServiceReferenceUtils.getServiceReference(refs);
    }

    public static ServiceReference getServiceReference(ServiceReference ... references) {
        if (ObjectUtils.isEmpty((Object[])references)) {
            return null;
        }
        ServiceReference winningReference = references[0];
        if (references.length > 1) {
            long winningId = OsgiServiceReferenceUtils.getServiceId(winningReference);
            int winningRanking = OsgiServiceReferenceUtils.getServiceRanking(winningReference);
            for (int i = 1; i < references.length; ++i) {
                ServiceReference reference = references[i];
                int serviceRanking = OsgiServiceReferenceUtils.getServiceRanking(reference);
                long serviceId = OsgiServiceReferenceUtils.getServiceId(reference);
                if (serviceRanking <= winningRanking && (serviceRanking != winningRanking || winningId <= serviceId)) continue;
                winningReference = reference;
                winningId = serviceId;
                winningRanking = serviceRanking;
            }
        }
        return winningReference;
    }

    public static ServiceReference getServiceReference(BundleContext bundleContext, String[] classes, String filter) {
        String clazz = ObjectUtils.isEmpty((Object[])classes) ? null : classes[0];
        return OsgiServiceReferenceUtils.getServiceReference(bundleContext, clazz, OsgiFilterUtils.unifyFilter(classes, filter));
    }

    public static ServiceReference getServiceReference(BundleContext bundleContext, String filter) {
        return OsgiServiceReferenceUtils.getServiceReference(bundleContext, (String)null, filter);
    }

    public static ServiceReference[] getServiceReferences(BundleContext bundleContext, String[] classes) {
        return OsgiServiceReferenceUtils.getServiceReferences(bundleContext, classes, null);
    }

    public static ServiceReference[] getServiceReferences(BundleContext bundleContext, String clazz, String filter) {
        Assert.notNull((Object)bundleContext, (String)"bundleContext should be not null");
        try {
            ServiceReference[] refs = bundleContext.getServiceReferences(clazz, filter);
            return refs == null ? new ServiceReference[]{} : refs;
        }
        catch (InvalidSyntaxException ise) {
            throw (RuntimeException)new IllegalArgumentException("invalid filter: " + ise.getFilter()).initCause(ise);
        }
    }

    public static ServiceReference[] getServiceReferences(BundleContext bundleContext, String[] classes, String filter) {
        String clazz = ObjectUtils.isEmpty((Object[])classes) ? null : classes[0];
        return OsgiServiceReferenceUtils.getServiceReferences(bundleContext, clazz, OsgiFilterUtils.unifyFilter(classes, filter));
    }

    public static ServiceReference[] getServiceReferences(BundleContext bundleContext, String filter) {
        return OsgiServiceReferenceUtils.getServiceReferences(bundleContext, (String)null, filter);
    }

    public static long getServiceId(ServiceReference reference) {
        Assert.notNull((Object)reference);
        return (Long)reference.getProperty("service.id");
    }

    public static int getServiceRanking(ServiceReference reference) {
        Assert.notNull((Object)reference);
        Object ranking = reference.getProperty("service.ranking");
        return ranking != null && ranking instanceof Integer ? (Integer)ranking : 0;
    }

    public static String[] getServiceObjectClasses(ServiceReference reference) {
        Assert.notNull((Object)reference);
        return (String[])reference.getProperty("objectClass");
    }

    public static Dictionary getServicePropertiesSnapshot(ServiceReference reference) {
        return new MapBasedDictionary(OsgiServiceReferenceUtils.getServicePropertiesSnapshotAsMap(reference));
    }

    public static Map getServicePropertiesSnapshotAsMap(ServiceReference reference) {
        Assert.notNull((Object)reference);
        String[] keys = reference.getPropertyKeys();
        Map<String, Object> map = new LinkedHashMap<String, Object>(keys.length);
        for (int i = 0; i < keys.length; ++i) {
            map.put(keys[i], reference.getProperty(keys[i]));
        }
        map = Collections.unmodifiableMap(map);
        return map;
    }

    public static Dictionary getServiceProperties(ServiceReference reference) {
        return new MapBasedDictionary(OsgiServiceReferenceUtils.getServicePropertiesAsMap(reference));
    }

    public static Map getServicePropertiesAsMap(ServiceReference reference) {
        Assert.notNull((Object)reference);
        return new ServiceReferenceBasedMap(reference);
    }

    public static boolean isServicePresent(BundleContext bundleContext, String filter) {
        return !ObjectUtils.isEmpty((Object[])OsgiServiceReferenceUtils.getServiceReferences(bundleContext, filter));
    }
}

