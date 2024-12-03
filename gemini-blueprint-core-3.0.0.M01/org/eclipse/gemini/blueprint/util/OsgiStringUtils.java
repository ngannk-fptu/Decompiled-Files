/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.FrameworkEvent
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceReference
 *  org.springframework.core.Constants
 *  org.springframework.core.Constants$ConstantException
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.util;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.springframework.core.Constants;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class OsgiStringUtils {
    public static final Constants BUNDLE_EVENTS = new Constants(BundleEvent.class);
    public static final Constants FRAMEWORK_EVENTS = new Constants(FrameworkEvent.class);
    public static final Constants SERVICE_EVENTS = new Constants(ServiceEvent.class);
    public static final Constants BUNDLE_STATES = new Constants(Bundle.class);
    private static final String UNKNOWN_EVENT_TYPE = "UNKNOWN EVENT TYPE";
    private static final String NULL_STRING = "null";
    private static final String EMPTY_STRING = "";

    public static String nullSafeBundleEventToString(int eventType) {
        try {
            return BUNDLE_EVENTS.toCode((Object)eventType, EMPTY_STRING);
        }
        catch (Constants.ConstantException cex) {
            return UNKNOWN_EVENT_TYPE;
        }
    }

    public static String nullSafeToString(BundleEvent event) {
        if (event == null) {
            return NULL_STRING;
        }
        try {
            return BUNDLE_EVENTS.toCode((Object)event.getType(), EMPTY_STRING);
        }
        catch (Constants.ConstantException cex) {
            return UNKNOWN_EVENT_TYPE;
        }
    }

    public static String nullSafeToString(ServiceEvent event) {
        if (event == null) {
            return NULL_STRING;
        }
        try {
            return SERVICE_EVENTS.toCode((Object)event.getType(), EMPTY_STRING);
        }
        catch (Constants.ConstantException cex) {
            return UNKNOWN_EVENT_TYPE;
        }
    }

    public static String nullSafeToString(FrameworkEvent event) {
        if (event == null) {
            return NULL_STRING;
        }
        try {
            return FRAMEWORK_EVENTS.toCode((Object)event.getType(), EMPTY_STRING);
        }
        catch (Constants.ConstantException cex) {
            return UNKNOWN_EVENT_TYPE;
        }
    }

    public static String nullSafeToString(ServiceReference reference) {
        if (reference == null) {
            return NULL_STRING;
        }
        StringBuilder buf = new StringBuilder();
        Bundle owningBundle = reference.getBundle();
        buf.append("ServiceReference [").append(OsgiStringUtils.nullSafeSymbolicName(owningBundle)).append("] ");
        Object[] clazzes = (String[])reference.getProperty("objectClass");
        buf.append(ObjectUtils.nullSafeToString((Object[])clazzes));
        buf.append("={");
        String[] keys = reference.getPropertyKeys();
        for (int i = 0; i < keys.length; ++i) {
            if ("objectClass".equals(keys[i])) continue;
            buf.append(keys[i]).append('=').append(reference.getProperty(keys[i]));
            if (i >= keys.length - 1) continue;
            buf.append(',');
        }
        buf.append('}');
        return buf.toString();
    }

    public static String bundleStateAsString(Bundle bundle) {
        Assert.notNull((Object)bundle, (String)"bundle is required");
        int state = bundle.getState();
        try {
            return BUNDLE_STATES.toCode((Object)state, EMPTY_STRING);
        }
        catch (Constants.ConstantException cex) {
            return "UNKNOWN STATE";
        }
    }

    public static String nullSafeSymbolicName(Bundle bundle) {
        if (bundle == null) {
            return NULL_STRING;
        }
        Dictionary headers = bundle.getHeaders();
        if (headers == null) {
            return NULL_STRING;
        }
        return bundle.getSymbolicName() == null ? NULL_STRING : bundle.getSymbolicName();
    }

    public static String nullSafeName(Bundle bundle) {
        if (bundle == null) {
            return NULL_STRING;
        }
        Dictionary headers = bundle.getHeaders();
        if (headers == null) {
            return NULL_STRING;
        }
        String name = (String)headers.get("Bundle-Name");
        return name == null ? NULL_STRING : name;
    }

    public static String nullSafeNameAndSymName(Bundle bundle) {
        if (bundle == null) {
            return NULL_STRING;
        }
        Dictionary dict = bundle.getHeaders();
        if (dict == null) {
            return NULL_STRING;
        }
        StringBuilder buf = new StringBuilder();
        String name = (String)dict.get("Bundle-Name");
        if (name == null) {
            buf.append(NULL_STRING);
        } else {
            buf.append(name);
        }
        buf.append(" (");
        String sname = (String)dict.get("Bundle-SymbolicName");
        if (sname == null) {
            buf.append(NULL_STRING);
        } else {
            buf.append(sname);
        }
        buf.append(")");
        return buf.toString();
    }
}

