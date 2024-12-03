/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationAttributes;
import com.opensymphony.xwork2.util.location.LocationImpl;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.struts2.config.StrutsJavaConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class LocationUtils {
    public static final String UNKNOWN_STRING = "[unknown location]";
    private static List<WeakReference<LocationFinder>> finders = new ArrayList<WeakReference<LocationFinder>>();

    private LocationUtils() {
    }

    public static String toString(Location location) {
        String uri;
        StringBuilder result = new StringBuilder();
        String description = location.getDescription();
        if (description != null) {
            result.append(description).append(" - ");
        }
        if ((uri = location.getURI()) != null) {
            result.append(uri).append(':').append(location.getLineNumber()).append(':').append(location.getColumnNumber());
        } else {
            result.append(UNKNOWN_STRING);
        }
        return result.toString();
    }

    public static LocationImpl parse(String text) throws IllegalArgumentException {
        String description;
        if (text == null || text.length() == 0) {
            return null;
        }
        int uriStart = text.lastIndexOf(" - ");
        if (uriStart > -1) {
            description = text.substring(0, uriStart);
            uriStart += 3;
        } else {
            description = null;
            uriStart = 0;
        }
        try {
            int colSep = text.lastIndexOf(58);
            if (colSep > -1) {
                int column = Integer.parseInt(text.substring(colSep + 1));
                int lineSep = text.lastIndexOf(58, colSep - 1);
                if (lineSep > -1) {
                    int line = Integer.parseInt(text.substring(lineSep + 1, colSep));
                    return new LocationImpl(description, text.substring(uriStart, lineSep), line, column);
                }
            } else if (text.endsWith(UNKNOWN_STRING)) {
                return LocationImpl.UNKNOWN;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return LocationImpl.UNKNOWN;
    }

    public static boolean isKnown(Location location) {
        return location != null && !Location.UNKNOWN.equals(location);
    }

    public static boolean isUnknown(Location location) {
        return location == null || Location.UNKNOWN.equals(location);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addFinder(LocationFinder finder) {
        if (finder == null) {
            return;
        }
        Class<LocationFinder> clazz = LocationFinder.class;
        synchronized (LocationFinder.class) {
            ArrayList<WeakReference<LocationFinder>> newFinders = new ArrayList<WeakReference<LocationFinder>>(finders);
            newFinders.add(new WeakReference<LocationFinder>(finder));
            finders = newFinders;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    public static Location getLocation(Object obj) {
        return LocationUtils.getLocation(obj, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static Location getLocation(Object obj, String description) {
        StackTraceElement trace;
        Throwable t;
        StackTraceElement[] stack;
        if (obj instanceof Location) {
            return (Location)obj;
        }
        if (obj instanceof Locatable) {
            return ((Locatable)obj).getLocation();
        }
        if (obj instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException)obj;
            if (spe.getSystemId() == null) return Location.UNKNOWN;
            return new LocationImpl(description, spe.getSystemId(), spe.getLineNumber(), spe.getColumnNumber());
        }
        if (obj instanceof TransformerException) {
            TransformerException ex = (TransformerException)obj;
            SourceLocator locator = ex.getLocator();
            if (locator == null) return Location.UNKNOWN;
            if (locator.getSystemId() == null) return Location.UNKNOWN;
            return new LocationImpl(description, locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        }
        if (obj instanceof Locator) {
            Locator locator = (Locator)obj;
            if (locator.getSystemId() == null) return Location.UNKNOWN;
            return new LocationImpl(description, locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        }
        if (obj instanceof Element) {
            return LocationAttributes.getLocation((Element)obj);
        }
        List<WeakReference<LocationFinder>> currentFinders = finders;
        int size = currentFinders.size();
        for (int i = 0; i < size; ++i) {
            WeakReference<LocationFinder> ref = currentFinders.get(i);
            LocationFinder finder = (LocationFinder)ref.get();
            if (finder == null) {
                Class<LocationFinder> clazz = LocationFinder.class;
                // MONITORENTER : com.opensymphony.xwork2.util.location.LocationUtils$LocationFinder.class
                ArrayList<WeakReference<LocationFinder>> newFinders = new ArrayList<WeakReference<LocationFinder>>(finders);
                newFinders.remove(ref);
                finders = newFinders;
                // MONITOREXIT : clazz
                continue;
            }
            Location result = finder.getLocation(obj, description);
            if (result == null) continue;
            return result;
        }
        if (obj instanceof Throwable && (stack = (t = (Throwable)obj).getStackTrace()) != null && stack.length > 0 && (trace = stack[0]).getLineNumber() >= 0) {
            String uri = trace.getClassName();
            if (trace.getFileName() != null) {
                uri = uri.replace('.', '/');
                uri = uri.substring(0, uri.lastIndexOf(47) + 1);
                URL url = ClassLoaderUtil.getResource(uri = uri + trace.getFileName(), LocationUtils.class);
                if (url != null) {
                    uri = url.toString();
                }
            }
            if (description != null) return new LocationImpl(description, uri, trace.getLineNumber(), -1);
            StringBuilder sb = new StringBuilder();
            sb.append("Class: ").append(trace.getClassName()).append("\n");
            sb.append("File: ").append(trace.getFileName()).append("\n");
            sb.append("Method: ").append(trace.getMethodName()).append("\n");
            sb.append("Line: ").append(trace.getLineNumber());
            description = sb.toString();
            return new LocationImpl(description, uri, trace.getLineNumber(), -1);
        }
        if (!(obj instanceof StrutsJavaConfiguration)) return Location.UNKNOWN;
        return new LocationImpl(description, obj.toString());
    }

    public static interface LocationFinder {
        public Location getLocation(Object var1, String var2);
    }
}

