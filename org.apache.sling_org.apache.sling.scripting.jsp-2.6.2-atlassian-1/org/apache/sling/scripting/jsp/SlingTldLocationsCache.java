/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.ServiceRegistration
 */
package org.apache.sling.scripting.jsp;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.TldLocationsCache;
import org.apache.sling.scripting.jsp.jasper.xmlparser.ParserUtils;
import org.apache.sling.scripting.jsp.jasper.xmlparser.TreeNode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

public class SlingTldLocationsCache
extends TldLocationsCache
implements BundleListener {
    private static final String TLD_SCHEME = "tld:";
    private final Map<String, TldLocationEntry> tldLocations = new HashMap<String, TldLocationEntry>();
    private ServiceRegistration serviceRegistration;
    private final BundleContext bundleContext;

    public SlingTldLocationsCache(BundleContext context) {
        this.bundleContext = context;
        context.addBundleListener((BundleListener)this);
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; ++i) {
            if (bundles[i].getState() != 4 && bundles[i].getState() != 32) continue;
            this.addBundle(bundles[i]);
        }
        Hashtable<String, String> tldConfigPrinterProperties = new Hashtable<String, String>();
        ((Dictionary)tldConfigPrinterProperties).put("felix.webconsole.label", "jsptaglibs");
        ((Dictionary)tldConfigPrinterProperties).put("felix.webconsole.title", "JSP Taglibs");
        ((Dictionary)tldConfigPrinterProperties).put("felix.webconsole.configprinter.modes", "always");
        this.serviceRegistration = context.registerService(Object.class.getName(), (Object)this, tldConfigPrinterProperties);
    }

    public void deactivate(BundleContext context) {
        if (this.serviceRegistration != null) {
            this.serviceRegistration.unregister();
            this.serviceRegistration = null;
        }
        context.removeBundleListener((BundleListener)this);
    }

    public void bundleChanged(BundleEvent event) {
        if (event.getType() == 32) {
            this.addBundle(event.getBundle());
        } else if (event.getType() == 64) {
            this.removeBundle(event.getBundle());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public URL getTldLocationURL(String tldLocation) {
        if (tldLocation.startsWith(TLD_SCHEME)) {
            TldLocationEntry tle;
            tldLocation = tldLocation.substring(TLD_SCHEME.length());
            Map<String, TldLocationEntry> map = this.tldLocations;
            synchronized (map) {
                tle = this.tldLocations.get(tldLocation);
            }
            if (tle != null) {
                return tle.getTldURL();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getLocation(String uri) throws JasperException {
        Map<String, TldLocationEntry> map = this.tldLocations;
        synchronized (map) {
            TldLocationEntry entry = this.tldLocations.get(uri);
            if (entry != null) {
                return new String[]{TLD_SCHEME + uri, entry.getTldURL().toString()};
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addBundle(Bundle bundle) {
        Enumeration entries = bundle.findEntries("META-INF", "*.tld", false);
        if (entries != null) {
            while (entries.hasMoreElements()) {
                URL taglib = (URL)entries.nextElement();
                String uri = this.getUriFromTld(taglib);
                Map<String, TldLocationEntry> map = this.tldLocations;
                synchronized (map) {
                    if (uri != null && !this.tldLocations.containsKey(uri)) {
                        this.tldLocations.put(uri, new TldLocationEntry(bundle, taglib));
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeBundle(Bundle bundle) {
        Map<String, TldLocationEntry> map = this.tldLocations;
        synchronized (map) {
            Iterator<Map.Entry<String, TldLocationEntry>> i = this.tldLocations.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, TldLocationEntry> entry = i.next();
                if (entry.getValue().getBundleId() != bundle.getBundleId()) continue;
                i.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getUriFromTld(URL resource) {
        InputStream stream = null;
        try {
            String body;
            stream = resource.openStream();
            TreeNode tld = new ParserUtils().parseXMLDocument(resource.toString(), stream);
            TreeNode uri = tld.findChild("uri");
            if (uri != null && (body = uri.getBody()) != null) {
                String string = body;
                return string;
            }
        }
        catch (Exception exception) {
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable throwable) {}
            }
        }
        return null;
    }

    public void printConfiguration(PrintWriter pw) {
        pw.println("Currently available JSP Taglibs:");
        TreeMap<String, String> taglibs = new TreeMap<String, String>();
        for (Map.Entry<String, TldLocationEntry> entry : this.tldLocations.entrySet()) {
            long bundleId = entry.getValue().getBundleId();
            Bundle bundle = this.bundleContext.getBundle(bundleId);
            if (bundle != null) {
                taglibs.put(entry.getKey(), String.format("%s (%s)", bundle.getSymbolicName(), bundleId));
                continue;
            }
            taglibs.put(entry.getKey(), String.format("INVALID BUNDLE ID: %s", bundleId));
        }
        for (Map.Entry<String, TldLocationEntry> entry : taglibs.entrySet()) {
            pw.printf("  %s - %s\n", entry.getKey(), entry.getValue());
        }
    }

    private static final class TldLocationEntry {
        private final long bundleId;
        private final URL tldURL;

        private TldLocationEntry(Bundle bundle, URL tldURL) {
            this.bundleId = bundle.getBundleId();
            this.tldURL = tldURL;
        }

        long getBundleId() {
            return this.bundleId;
        }

        URL getTldURL() {
            return this.tldURL;
        }
    }
}

