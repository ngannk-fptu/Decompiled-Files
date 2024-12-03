/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.bundlerepository.LocalRepositoryImpl;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.RepositoryAdminImpl;
import org.apache.felix.bundlerepository.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

public class ResolverImpl
implements Resolver {
    private final BundleContext m_context;
    private final RepositoryAdmin m_admin;
    private final Logger m_logger;
    private final LocalRepositoryImpl m_local;
    private final Set m_addedSet = new HashSet();
    private final Set m_failedSet = new HashSet();
    private final Set m_resolveSet = new HashSet();
    private final Set m_requiredSet = new HashSet();
    private final Set m_optionalSet = new HashSet();
    private final Map m_reasonMap = new HashMap();
    private final Map m_unsatisfiedMap = new HashMap();
    private boolean m_resolved = false;
    private long m_resolveTimeStamp;

    public ResolverImpl(BundleContext context, RepositoryAdminImpl admin, Logger logger) {
        this.m_context = context;
        this.m_admin = admin;
        this.m_logger = logger;
        this.m_local = admin.getLocalRepository();
    }

    public synchronized void add(Resource resource) {
        this.m_resolved = false;
        this.m_addedSet.add(resource);
    }

    public synchronized Requirement[] getUnsatisfiedRequirements() {
        if (this.m_resolved) {
            return this.m_unsatisfiedMap.keySet().toArray(new Requirement[this.m_unsatisfiedMap.size()]);
        }
        throw new IllegalStateException("The resources have not been resolved.");
    }

    public synchronized Resource[] getOptionalResources() {
        if (this.m_resolved) {
            return this.m_optionalSet.toArray(new Resource[this.m_optionalSet.size()]);
        }
        throw new IllegalStateException("The resources have not been resolved.");
    }

    public synchronized Requirement[] getReason(Resource resource) {
        if (this.m_resolved) {
            return (Requirement[])this.m_reasonMap.get(resource);
        }
        throw new IllegalStateException("The resources have not been resolved.");
    }

    public synchronized Resource[] getResources(Requirement requirement) {
        if (this.m_resolved) {
            return (Resource[])this.m_unsatisfiedMap.get(requirement);
        }
        throw new IllegalStateException("The resources have not been resolved.");
    }

    public synchronized Resource[] getRequiredResources() {
        if (this.m_resolved) {
            return this.m_requiredSet.toArray(new Resource[this.m_requiredSet.size()]);
        }
        throw new IllegalStateException("The resources have not been resolved.");
    }

    public synchronized Resource[] getAddedResources() {
        return this.m_addedSet.toArray(new Resource[this.m_addedSet.size()]);
    }

    public synchronized boolean resolve() {
        this.m_resolveTimeStamp = this.m_local.getLastModified();
        this.m_failedSet.clear();
        this.m_resolveSet.clear();
        this.m_requiredSet.clear();
        this.m_optionalSet.clear();
        this.m_reasonMap.clear();
        this.m_unsatisfiedMap.clear();
        this.m_resolved = true;
        boolean result = true;
        Iterator iter = this.m_addedSet.iterator();
        while (iter.hasNext()) {
            if (this.resolve((Resource)iter.next())) continue;
            result = false;
        }
        List<Resource> locals = Arrays.asList(this.m_local.getResources());
        this.m_requiredSet.removeAll(this.m_addedSet);
        this.m_requiredSet.removeAll(locals);
        this.m_optionalSet.removeAll(this.m_addedSet);
        this.m_optionalSet.removeAll(this.m_requiredSet);
        this.m_optionalSet.removeAll(locals);
        return result;
    }

    private boolean resolve(Resource resource) {
        boolean result = true;
        if (this.m_resolveSet.contains(resource)) {
            return true;
        }
        if (this.m_failedSet.contains(resource)) {
            return false;
        }
        this.m_resolveSet.add(resource);
        Requirement[] reqs = resource.getRequirements();
        if (reqs != null) {
            Resource candidate = null;
            for (int reqIdx = 0; reqIdx < reqs.length; ++reqIdx) {
                candidate = this.searchAddedResources(reqs[reqIdx]);
                if (candidate == null && (candidate = this.searchResolvingResources(reqs[reqIdx])) == null) {
                    List possibleCandidates = this.searchLocalResources(reqs[reqIdx]);
                    possibleCandidates.addAll(this.searchRemoteResources(reqs[reqIdx]));
                    while (candidate == null && !possibleCandidates.isEmpty()) {
                        Resource bestResource = this.getBestResource(possibleCandidates);
                        if (this.resolve(bestResource)) {
                            candidate = bestResource;
                            continue;
                        }
                        possibleCandidates.remove(bestResource);
                    }
                }
                if (candidate == null && !reqs[reqIdx].isOptional()) {
                    result = false;
                    Resource[] resources = (Resource[])this.m_unsatisfiedMap.get(reqs[reqIdx]);
                    if (resources == null) {
                        resources = new Resource[]{resource};
                    } else {
                        Resource[] tmp = new Resource[resources.length + 1];
                        System.arraycopy(resources, 0, tmp, 0, resources.length);
                        tmp[resources.length] = resource;
                        resources = tmp;
                    }
                    this.m_unsatisfiedMap.put(reqs[reqIdx], resources);
                    continue;
                }
                if (candidate == null) continue;
                if (this.resolve(candidate)) {
                    if (reqs[reqIdx].isOptional()) {
                        this.m_optionalSet.add(candidate);
                    } else {
                        this.m_requiredSet.add(candidate);
                    }
                    this.addReason(candidate, reqs[reqIdx]);
                    continue;
                }
                result = false;
            }
        }
        if (!result) {
            this.m_resolveSet.remove(resource);
            this.m_failedSet.add(resource);
        }
        return result;
    }

    private Resource searchAddedResources(Requirement req) {
        Iterator iter = this.m_addedSet.iterator();
        while (iter.hasNext()) {
            Resource resource = (Resource)iter.next();
            Capability[] caps = resource.getCapabilities();
            for (int capIdx = 0; caps != null && capIdx < caps.length; ++capIdx) {
                if (!caps[capIdx].getName().equals(req.getName()) || !req.isSatisfied(caps[capIdx])) continue;
                return resource;
            }
        }
        return null;
    }

    private Resource searchResolvingResources(Requirement req) {
        Iterator iterator = this.m_resolveSet.iterator();
        while (iterator.hasNext()) {
            Resource resource = (Resource)iterator.next();
            Capability[] caps = resource.getCapabilities();
            for (int capIdx = 0; caps != null && capIdx < caps.length; ++capIdx) {
                if (!caps[capIdx].getName().equals(req.getName()) || !req.isSatisfied(caps[capIdx])) continue;
                return resource;
            }
        }
        return null;
    }

    private List searchLocalResources(Requirement req) {
        ArrayList<Resource> matchingCandidates = new ArrayList<Resource>();
        Resource[] resources = this.m_local.getResources();
        for (int resIdx = 0; resources != null && resIdx < resources.length; ++resIdx) {
            if (this.m_failedSet.contains(resources[resIdx]) || this.m_resolveSet.contains(resources[resIdx])) continue;
            Capability[] caps = resources[resIdx].getCapabilities();
            for (int capIdx = 0; caps != null && capIdx < caps.length; ++capIdx) {
                if (!caps[capIdx].getName().equals(req.getName()) || !req.isSatisfied(caps[capIdx])) continue;
                matchingCandidates.add(resources[resIdx]);
            }
        }
        return matchingCandidates;
    }

    private List searchRemoteResources(Requirement req) {
        ArrayList<Resource> matchingCandidates = new ArrayList<Resource>();
        Repository[] repos = this.m_admin.listRepositories();
        for (int repoIdx = 0; repos != null && repoIdx < repos.length; ++repoIdx) {
            Resource[] resources = repos[repoIdx].getResources();
            for (int resIdx = 0; resources != null && resIdx < resources.length; ++resIdx) {
                if (this.m_failedSet.contains(resources[resIdx]) || this.m_resolveSet.contains(resources[resIdx])) continue;
                Capability[] caps = resources[resIdx].getCapabilities();
                for (int capIdx = 0; caps != null && capIdx < caps.length; ++capIdx) {
                    if (!caps[capIdx].getName().equals(req.getName()) || !req.isSatisfied(caps[capIdx])) continue;
                    matchingCandidates.add(resources[resIdx]);
                }
            }
        }
        return matchingCandidates;
    }

    private Resource getBestResource(List resources) {
        Version bestVersion = null;
        Resource best = null;
        for (int resIdx = 0; resIdx < resources.size(); ++resIdx) {
            Object v;
            Resource currentResource = (Resource)resources.get(resIdx);
            if (best == null) {
                best = currentResource;
                v = currentResource.getProperties().get("version");
                if (v == null || !(v instanceof Version)) continue;
                bestVersion = (Version)v;
                continue;
            }
            v = currentResource.getProperties().get("version");
            if (v == null && bestVersion == null && best.getCapabilities().length < currentResource.getCapabilities().length) {
                best = currentResource;
                bestVersion = (Version)v;
                continue;
            }
            if (v == null || !(v instanceof Version)) continue;
            if (bestVersion == null || bestVersion.compareTo(v) < 0) {
                best = currentResource;
                bestVersion = (Version)v;
                continue;
            }
            if (bestVersion == null || bestVersion.compareTo(v) != 0 || best.getCapabilities().length >= currentResource.getCapabilities().length) continue;
            best = currentResource;
            bestVersion = (Version)v;
        }
        return best;
    }

    public synchronized void deploy(boolean start) {
        int i;
        int i2;
        if (!this.m_resolved && !this.resolve()) {
            this.m_logger.log(1, "Resolver: Cannot resolve target resources.");
            return;
        }
        if (this.m_resolveTimeStamp != this.m_local.getLastModified()) {
            throw new IllegalStateException("Framework state has changed, must resolve again.");
        }
        HashMap<Resource, Resource> deployMap = new HashMap<Resource, Resource>();
        Resource[] resources = this.getAddedResources();
        for (i2 = 0; resources != null && i2 < resources.length; ++i2) {
            deployMap.put(resources[i2], resources[i2]);
        }
        resources = this.getRequiredResources();
        for (i2 = 0; resources != null && i2 < resources.length; ++i2) {
            deployMap.put(resources[i2], resources[i2]);
        }
        resources = this.getOptionalResources();
        for (i2 = 0; resources != null && i2 < resources.length; ++i2) {
            deployMap.put(resources[i2], resources[i2]);
        }
        Resource[] deployResources = deployMap.keySet().toArray(new Resource[deployMap.size()]);
        ArrayList<Bundle> startList = new ArrayList<Bundle>();
        for (i = 0; i < deployResources.length; ++i) {
            Bundle bundle;
            LocalRepositoryImpl.LocalResourceImpl localResource = this.findUpdatableLocalResource(deployResources[i]);
            if (localResource != null && this.isResourceUpdatable(localResource, deployResources[i], deployResources)) {
                if (localResource.equals(deployResources[i])) continue;
                try {
                    boolean doStartBundle = start;
                    if (localResource.getBundle().getState() == 32) {
                        doStartBundle = true;
                        localResource.getBundle().stop();
                    }
                    localResource.getBundle().update(deployResources[i].getURL().openStream());
                    if (!doStartBundle || this.isFragmentBundle(bundle = localResource.getBundle())) continue;
                    startList.add(bundle);
                    continue;
                }
                catch (Exception ex) {
                    this.m_logger.log(1, "Resolver: Update error - " + Util.getBundleName(localResource.getBundle()), ex);
                    return;
                }
            }
            try {
                URL url = deployResources[i].getURL();
                if (url == null) continue;
                bundle = this.m_context.installBundle("obr://" + deployResources[i].getSymbolicName() + "/" + System.currentTimeMillis(), url.openStream());
                if (!start || this.isFragmentBundle(bundle)) continue;
                startList.add(bundle);
                continue;
            }
            catch (Exception ex) {
                this.m_logger.log(1, "Resolver: Install error - " + deployResources[i].getSymbolicName(), ex);
                return;
            }
        }
        for (i = 0; i < startList.size(); ++i) {
            try {
                ((Bundle)startList.get(i)).start();
                continue;
            }
            catch (BundleException ex) {
                this.m_logger.log(1, "Resolver: Start error - " + ((Bundle)startList.get(i)).getSymbolicName(), ex);
            }
        }
    }

    private boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get("Fragment-Host") != null;
    }

    private void addReason(Resource resource, Requirement req) {
        Requirement[] reasons = (Requirement[])this.m_reasonMap.get(resource);
        if (reasons == null) {
            reasons = new Requirement[]{req};
        } else {
            Requirement[] tmp = new Requirement[reasons.length + 1];
            System.arraycopy(reasons, 0, tmp, 0, reasons.length);
            tmp[reasons.length] = req;
            reasons = tmp;
        }
        this.m_reasonMap.put(resource, reasons);
    }

    private LocalRepositoryImpl.LocalResourceImpl findUpdatableLocalResource(Resource resource) {
        Resource[] localResources = this.findLocalResources(resource.getSymbolicName());
        if (localResources != null) {
            for (int i = 0; i < localResources.length; ++i) {
                if (!this.isResourceUpdatable(localResources[i], resource, this.m_local.getResources())) continue;
                return (LocalRepositoryImpl.LocalResourceImpl)localResources[i];
            }
        }
        return null;
    }

    private Resource[] findLocalResources(String symName) {
        Resource[] localResources = this.m_local.getResources();
        ArrayList<Resource> matchList = new ArrayList<Resource>();
        for (int i = 0; i < localResources.length; ++i) {
            String localSymName = localResources[i].getSymbolicName();
            if (localSymName == null || !localSymName.equals(symName)) continue;
            matchList.add(localResources[i]);
        }
        return matchList.toArray(new Resource[matchList.size()]);
    }

    private boolean isResourceUpdatable(Resource oldVersion, Resource newVersion, Resource[] resources) {
        Requirement[] reqs = this.getResolvableRequirements(oldVersion, resources);
        if (reqs == null) {
            return true;
        }
        Capability[] caps = newVersion.getCapabilities();
        if (caps == null) {
            return false;
        }
        for (int reqIdx = 0; reqIdx < reqs.length; ++reqIdx) {
            boolean satisfied = false;
            for (int capIdx = 0; !satisfied && capIdx < caps.length; ++capIdx) {
                if (!reqs[reqIdx].isSatisfied(caps[capIdx])) continue;
                satisfied = true;
            }
            if (satisfied) continue;
            return false;
        }
        return true;
    }

    private Requirement[] getResolvableRequirements(Resource resource, Resource[] resources) {
        Capability[] caps = resource.getCapabilities();
        if (caps != null && caps.length > 0) {
            ArrayList<Requirement> reqList = new ArrayList<Requirement>();
            for (int capIdx = 0; capIdx < caps.length; ++capIdx) {
                boolean added = false;
                for (int resIdx = 0; !added && resIdx < resources.length; ++resIdx) {
                    Requirement[] reqs = resources[resIdx].getRequirements();
                    for (int reqIdx = 0; reqs != null && reqIdx < reqs.length; ++reqIdx) {
                        if (!reqs[reqIdx].isSatisfied(caps[capIdx])) continue;
                        added = true;
                        reqList.add(reqs[reqIdx]);
                    }
                }
            }
            return reqList.toArray(new Requirement[reqList.size()]);
        }
        return null;
    }
}

