/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency
 *  org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency
 *  org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceEvent
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;

class MandatoryServiceDependency
implements OsgiServiceDependency {
    private static final Pattern PATTERN = Pattern.compile("objectClass=(?:[^\\)]+)");
    protected final BundleContext bundleContext;
    private OsgiServiceDependency serviceDependency;
    private final AtomicInteger matchingServices = new AtomicInteger(0);
    protected final String filterAsString;
    private final String[] classes;

    MandatoryServiceDependency(BundleContext bc, Filter serviceFilter, boolean isMandatory, String beanName) {
        this(bc, (OsgiServiceDependency)new DefaultOsgiServiceDependency(beanName, serviceFilter, isMandatory));
    }

    MandatoryServiceDependency(BundleContext bc, OsgiServiceDependency dependency) {
        this.bundleContext = bc;
        this.serviceDependency = dependency;
        this.filterAsString = dependency.getServiceFilter().toString();
        this.classes = MandatoryServiceDependency.extractObjectClassFromFilter(this.filterAsString);
    }

    boolean matches(ServiceEvent event) {
        return this.serviceDependency.getServiceFilter().match(event.getServiceReference());
    }

    boolean isServicePresent() {
        return !this.serviceDependency.isMandatory() || OsgiServiceReferenceUtils.isServicePresent((BundleContext)this.bundleContext, (String)this.filterAsString);
    }

    public String toString() {
        return "Dependency on [" + this.filterAsString + "] (from bean [" + this.serviceDependency.getBeanName() + "])";
    }

    public Filter getServiceFilter() {
        return this.serviceDependency.getServiceFilter();
    }

    public String getBeanName() {
        return this.serviceDependency.getBeanName();
    }

    public boolean isMandatory() {
        return this.serviceDependency.isMandatory();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MandatoryServiceDependency that = (MandatoryServiceDependency)o;
        return this.serviceDependency.equals(that.serviceDependency);
    }

    public int hashCode() {
        int result = MandatoryServiceDependency.class.hashCode();
        result = 29 * result + this.serviceDependency.hashCode();
        return result;
    }

    public OsgiServiceDependency getServiceDependency() {
        return this.serviceDependency;
    }

    int increment() {
        return this.matchingServices.incrementAndGet();
    }

    int decrement() {
        return this.matchingServices.decrementAndGet();
    }

    private static String[] extractObjectClassFromFilter(String filterString) {
        ArrayList<String> matches = null;
        Matcher matcher = PATTERN.matcher(filterString);
        while (matcher.find()) {
            if (matches == null) {
                matches = new ArrayList<String>(4);
            }
            matches.add(matcher.group());
        }
        return matches == null ? new String[]{} : matches.toArray(new String[matches.size()]);
    }
}

