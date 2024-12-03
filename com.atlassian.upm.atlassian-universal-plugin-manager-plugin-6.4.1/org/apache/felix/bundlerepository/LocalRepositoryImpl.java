/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.AllServiceListener
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.SynchronousBundleListener
 */
package org.apache.felix.bundlerepository;

import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.felix.bundlerepository.CapabilityImpl;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.PropertyImpl;
import org.apache.felix.bundlerepository.R4Import;
import org.apache.felix.bundlerepository.R4Package;
import org.apache.felix.bundlerepository.RequirementImpl;
import org.apache.felix.bundlerepository.ResourceImpl;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Resource;

public class LocalRepositoryImpl
implements Repository,
SynchronousBundleListener,
AllServiceListener {
    private final BundleContext m_context;
    private final Logger m_logger;
    private long m_snapshotTimeStamp = 0L;
    private Map m_localResourceList = new HashMap();

    public LocalRepositoryImpl(BundleContext context, Logger logger) {
        this.m_context = context;
        this.m_logger = logger;
        this.initialize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == 1) {
            LocalRepositoryImpl localRepositoryImpl = this;
            synchronized (localRepositoryImpl) {
                this.addBundle(event.getBundle(), this.m_logger);
                this.m_snapshotTimeStamp = System.currentTimeMillis();
            }
        }
        if (event.getType() == 16) {
            LocalRepositoryImpl localRepositoryImpl = this;
            synchronized (localRepositoryImpl) {
                this.removeBundle(event.getBundle(), this.m_logger);
                this.m_snapshotTimeStamp = System.currentTimeMillis();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serviceChanged(ServiceEvent event) {
        Bundle bundle = event.getServiceReference().getBundle();
        if (bundle.getState() == 32 && event.getType() != 2) {
            LocalRepositoryImpl localRepositoryImpl = this;
            synchronized (localRepositoryImpl) {
                this.removeBundle(bundle, this.m_logger);
                this.addBundle(bundle, this.m_logger);
                this.m_snapshotTimeStamp = System.currentTimeMillis();
            }
        }
    }

    private void addBundle(Bundle bundle, Logger logger) {
        try {
            this.m_localResourceList.put(new Long(bundle.getBundleId()), new LocalResourceImpl(bundle, this.m_logger));
        }
        catch (InvalidSyntaxException ex) {
            this.m_logger.log(2, ex.getMessage(), ex);
        }
    }

    private void removeBundle(Bundle bundle, Logger logger) {
        this.m_localResourceList.remove(new Long(bundle.getBundleId()));
    }

    public void dispose() {
        this.m_context.removeBundleListener((BundleListener)this);
        this.m_context.removeServiceListener((ServiceListener)this);
    }

    public URL getURL() {
        return null;
    }

    public String getName() {
        return "Locally Installed Repository";
    }

    public synchronized long getLastModified() {
        return this.m_snapshotTimeStamp;
    }

    public synchronized Resource[] getResources() {
        return this.m_localResourceList.values().toArray(new Resource[this.m_localResourceList.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialize() {
        this.m_context.addBundleListener((BundleListener)this);
        this.m_context.addServiceListener((ServiceListener)this);
        Bundle[] bundles = null;
        LocalRepositoryImpl localRepositoryImpl = this;
        synchronized (localRepositoryImpl) {
            bundles = this.m_context.getBundles();
            for (int i = 0; bundles != null && i < bundles.length; ++i) {
                this.addBundle(bundles[i], this.m_logger);
            }
            this.m_snapshotTimeStamp = System.currentTimeMillis();
        }
    }

    public static class LocalResourceImpl
    extends ResourceImpl {
        private Bundle m_bundle = null;

        LocalResourceImpl(Bundle bundle, Logger logger) throws InvalidSyntaxException {
            this(null, bundle, logger);
        }

        LocalResourceImpl(ResourceImpl resource, Bundle bundle, Logger logger) throws InvalidSyntaxException {
            super(resource);
            this.m_bundle = bundle;
            this.initialize();
        }

        public Bundle getBundle() {
            return this.m_bundle;
        }

        private void initialize() throws InvalidSyntaxException {
            String ee;
            Dictionary dict = this.m_bundle.getHeaders();
            this.convertAttributesToProperties(dict);
            this.convertImportPackageToRequirement(dict);
            this.convertImportServiceToRequirement(dict);
            this.convertExportPackageToCapability(dict);
            this.convertExportServiceToCapability(dict, this.m_bundle);
            if (this.m_bundle.getBundleId() == 0L && (ee = this.m_bundle.getBundleContext().getProperty("org.osgi.framework.executionenvironment")) != null) {
                StringTokenizer tokener = new StringTokenizer(ee, ",");
                ArrayList<String> eeList = new ArrayList<String>();
                while (tokener.hasMoreTokens()) {
                    String eeName = tokener.nextToken().trim();
                    if (eeName.length() <= 0) continue;
                    eeList.add(eeName);
                }
                CapabilityImpl cap = new CapabilityImpl();
                cap.setName("ee");
                cap.addP("ee", eeList);
                this.addCapability(cap);
            }
        }

        private void convertAttributesToProperties(Dictionary dict) {
            Enumeration keys = dict.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                if (key.equalsIgnoreCase("Bundle-SymbolicName")) {
                    this.put("symbolicname", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-Name")) {
                    this.put("presentationname", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-Version")) {
                    this.put("version", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-Source")) {
                    this.put("source", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-Description")) {
                    this.put("description", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-DocURL")) {
                    this.put("documentation", (String)dict.get(key));
                    continue;
                }
                if (key.equalsIgnoreCase("Bundle-Copyright")) {
                    this.put("copyright", (String)dict.get(key));
                    continue;
                }
                if (!key.equalsIgnoreCase("Bundle-License")) continue;
                this.put("license", (String)dict.get(key));
            }
        }

        private void convertImportPackageToRequirement(Dictionary dict) throws InvalidSyntaxException {
            String target = (String)dict.get("Import-Package");
            if (target != null) {
                R4Package[] pkgs = R4Package.parseImportOrExportHeader(target);
                R4Import[] imports = new R4Import[pkgs.length];
                for (int i = 0; i < pkgs.length; ++i) {
                    imports[i] = new R4Import(pkgs[i]);
                }
                for (int impIdx = 0; impIdx < imports.length; ++impIdx) {
                    String low;
                    RequirementImpl req = new RequirementImpl();
                    req.setMultiple("false");
                    req.setOptional(Boolean.toString(imports[impIdx].isOptional()));
                    req.setName("package");
                    req.addText("Import package " + imports[impIdx].toString());
                    String string = low = imports[impIdx].isLowInclusive() ? "(version>=" + imports[impIdx].getVersion() + ")" : "(!(version<=" + imports[impIdx].getVersion() + "))";
                    if (imports[impIdx].getVersionHigh() != null) {
                        String high = imports[impIdx].isHighInclusive() ? "(version<=" + imports[impIdx].getVersionHigh() + ")" : "(!(version>=" + imports[impIdx].getVersionHigh() + "))";
                        req.setFilter("(&(package=" + imports[impIdx].getName() + ")" + low + high + ")");
                    } else {
                        req.setFilter("(&(package=" + imports[impIdx].getName() + ")" + low + ")");
                    }
                    this.addRequire(req);
                }
            }
        }

        private void convertImportServiceToRequirement(Dictionary dict) throws InvalidSyntaxException {
            String target = (String)dict.get("Import-Service");
            if (target != null) {
                R4Package[] pkgs = R4Package.parseImportOrExportHeader(target);
                for (int pkgIdx = 0; pkgs != null && pkgIdx < pkgs.length; ++pkgIdx) {
                    RequirementImpl req = new RequirementImpl();
                    req.setMultiple("false");
                    req.setName("service");
                    req.addText("Import service " + pkgs[pkgIdx].toString());
                    req.setFilter("(service=" + pkgs[pkgIdx].getName() + ")");
                    this.addRequire(req);
                }
            }
        }

        private void convertExportPackageToCapability(Dictionary dict) {
            String target = (String)dict.get("Export-Package");
            if (target != null) {
                R4Package[] pkgs = R4Package.parseImportOrExportHeader(target);
                for (int pkgIdx = 0; pkgs != null && pkgIdx < pkgs.length; ++pkgIdx) {
                    CapabilityImpl cap = new CapabilityImpl();
                    cap.setName("package");
                    cap.addP(new PropertyImpl("package", null, pkgs[pkgIdx].getName()));
                    cap.addP(new PropertyImpl("version", "version", pkgs[pkgIdx].getVersion().toString()));
                    this.addCapability(cap);
                }
            }
        }

        private void convertExportServiceToCapability(Dictionary dict, Bundle bundle) {
            HashSet<String> services = new HashSet<String>();
            String target = (String)dict.get("Export-Service");
            if (target != null) {
                R4Package[] pkgs = R4Package.parseImportOrExportHeader(target);
                for (int pkgIdx = 0; pkgs != null && pkgIdx < pkgs.length; ++pkgIdx) {
                    services.add(pkgs[pkgIdx].getName());
                }
            }
            ServiceReference[] refs = bundle.getRegisteredServices();
            for (int i = 0; refs != null && i < refs.length; ++i) {
                String[] cls = (String[])refs[i].getProperty("objectClass");
                for (int j = 0; cls != null && j < cls.length; ++j) {
                    services.add(cls[j]);
                }
            }
            Iterator si = services.iterator();
            while (si.hasNext()) {
                CapabilityImpl cap = new CapabilityImpl();
                cap.setName("service");
                cap.addP(new PropertyImpl("service", null, (String)si.next()));
                this.addCapability(cap);
            }
        }
    }
}

