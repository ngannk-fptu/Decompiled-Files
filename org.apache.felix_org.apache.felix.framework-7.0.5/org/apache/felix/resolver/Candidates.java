/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.felix.resolver.ResolutionError;
import org.apache.felix.resolver.ResolverImpl;
import org.apache.felix.resolver.SimpleHostedCapability;
import org.apache.felix.resolver.Util;
import org.apache.felix.resolver.WrappedCapability;
import org.apache.felix.resolver.WrappedRequirement;
import org.apache.felix.resolver.WrappedResource;
import org.apache.felix.resolver.reason.ReasonException;
import org.apache.felix.resolver.util.CandidateSelector;
import org.apache.felix.resolver.util.CopyOnWriteSet;
import org.apache.felix.resolver.util.OpenHashMap;
import org.apache.felix.resolver.util.OpenHashMapList;
import org.apache.felix.resolver.util.OpenHashMapSet;
import org.apache.felix.resolver.util.ShadowList;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;

class Candidates {
    private final ResolverImpl.ResolveSession m_session;
    private final OpenHashMapSet<Capability, Requirement> m_dependentMap;
    private final OpenHashMapList m_candidateMap;
    private final Map<Resource, WrappedResource> m_allWrappedHosts;
    private final OpenHashMap<Resource, PopulateResult> m_populateResultCache;
    private final Map<Capability, Requirement> m_subtitutableMap;
    private final OpenHashMapSet<Requirement, Capability> m_delta;
    private final AtomicBoolean m_candidateSelectorsUnmodifiable;
    private static final int UNPROCESSED = 0;
    private static final int PROCESSING = 1;
    private static final int SUBSTITUTED = 2;
    private static final int EXPORTED = 3;

    private Candidates(ResolverImpl.ResolveSession session, AtomicBoolean candidateSelectorsUnmodifiable, OpenHashMapSet<Capability, Requirement> dependentMap, OpenHashMapList candidateMap, Map<Resource, WrappedResource> wrappedHosts, OpenHashMap<Resource, PopulateResult> populateResultCache, Map<Capability, Requirement> substitutableMap, OpenHashMapSet<Requirement, Capability> delta) {
        this.m_session = session;
        this.m_candidateSelectorsUnmodifiable = candidateSelectorsUnmodifiable;
        this.m_dependentMap = dependentMap;
        this.m_candidateMap = candidateMap;
        this.m_allWrappedHosts = wrappedHosts;
        this.m_populateResultCache = populateResultCache;
        this.m_subtitutableMap = substitutableMap;
        this.m_delta = delta;
    }

    public Candidates(ResolverImpl.ResolveSession session) {
        this.m_session = session;
        this.m_candidateSelectorsUnmodifiable = new AtomicBoolean(false);
        this.m_dependentMap = new OpenHashMapSet();
        this.m_candidateMap = new OpenHashMapList();
        this.m_allWrappedHosts = new HashMap<Resource, WrappedResource>();
        this.m_populateResultCache = new OpenHashMap();
        this.m_subtitutableMap = new OpenHashMap<Capability, Requirement>();
        this.m_delta = new OpenHashMapSet(3);
    }

    public int getNbResources() {
        return this.m_populateResultCache.size();
    }

    public Map<Resource, Resource> getRootHosts() {
        LinkedHashMap<Resource, Resource> hosts = new LinkedHashMap<Resource, Resource>();
        for (Resource res : this.m_session.getMandatoryResources()) {
            this.addHost(res, hosts);
        }
        for (Resource res : this.m_session.getOptionalResources()) {
            if (!this.isPopulated(res)) continue;
            this.addHost(res, hosts);
        }
        return hosts;
    }

    private void addHost(Resource res, Map<Resource, Resource> hosts) {
        if (res instanceof WrappedResource) {
            res = ((WrappedResource)res).getDeclaredResource();
        }
        if (!Util.isFragment(res)) {
            hosts.put(res, this.getWrappedHost(res));
        } else {
            Requirement hostReq = res.getRequirements("osgi.wiring.host").get(0);
            Capability hostCap = this.getFirstCandidate(hostReq);
            if (hostCap != null && (res = this.getWrappedHost(hostCap.getResource())) instanceof WrappedResource) {
                hosts.put(((WrappedResource)res).getDeclaredResource(), res);
            }
        }
    }

    public Object getDelta() {
        return this.m_delta;
    }

    public void populate(Collection<Resource> resources) {
        ResolveContext rc = this.m_session.getContext();
        HashSet<Resource> toRemove = new HashSet<Resource>();
        LinkedList<Resource> toPopulate = new LinkedList<Resource>(resources);
        while (!toPopulate.isEmpty()) {
            Resource resource = toPopulate.getFirst();
            PopulateResult result = this.m_populateResultCache.get(resource);
            if (result == null) {
                result = new PopulateResult();
                result.candidates = new OpenHashMap<Requirement, List<Capability>>();
                result.remaining = new ArrayList<Requirement>(resource.getRequirements(null));
                this.m_populateResultCache.put(resource, result);
            }
            if (result.success || result.error != null) {
                toPopulate.removeFirst();
                continue;
            }
            if (result.remaining.isEmpty()) {
                toPopulate.removeFirst();
                result.success = true;
                this.addCandidates(result.candidates);
                result.candidates = null;
                result.remaining = null;
                Collection<Resource> relatedResources = rc.findRelatedResources(resource);
                this.m_session.setRelatedResources(resource, relatedResources);
                for (Resource relatedResource : relatedResources) {
                    if (!this.m_session.isValidRelatedResource(relatedResource)) continue;
                    toPopulate.addFirst(relatedResource);
                }
                continue;
            }
            Requirement requirement = result.remaining.remove(0);
            if (!this.isEffective(requirement)) continue;
            List<Capability> candidates = rc.findProviders(requirement);
            LinkedList<Resource> newToPopulate = new LinkedList<Resource>();
            ResolutionError thrown = this.processCandidates(newToPopulate, requirement, candidates);
            if (candidates.isEmpty() && !Util.isOptional(requirement)) {
                if (Util.isFragment(resource) && rc.getWirings().containsKey(resource)) {
                    result.success = true;
                } else {
                    result.error = new MissingRequirementError(requirement, thrown);
                    toRemove.add(resource);
                }
                toPopulate.removeFirst();
                continue;
            }
            if (!candidates.isEmpty()) {
                result.candidates.put(requirement, candidates);
            }
            if (newToPopulate.isEmpty()) continue;
            toPopulate.addAll(0, newToPopulate);
        }
        while (!toRemove.isEmpty()) {
            Iterator iterator = toRemove.iterator();
            Resource resource = (Resource)iterator.next();
            iterator.remove();
            this.remove(resource, toRemove);
        }
    }

    private boolean isEffective(Requirement req) {
        if (!this.m_session.getContext().isEffective(req)) {
            return false;
        }
        String res = req.getDirectives().get("resolution");
        return !"dynamic".equals(res);
    }

    private void populateSubstitutables() {
        for (Map.Entry<Resource, PopulateResult> populated : this.m_populateResultCache.fast()) {
            if (!populated.getValue().success) continue;
            this.populateSubstitutables(populated.getKey());
        }
    }

    private void populateSubstitutables(Resource resource) {
        OpenHashMap<String, List<Capability>> exportNames = new OpenHashMap<String, List<Capability>>(){

            @Override
            protected List<Capability> compute(String s) {
                return new ArrayList<Capability>(1);
            }
        };
        for (Capability packageExport : resource.getCapabilities(null)) {
            if (!"osgi.wiring.package".equals(packageExport.getNamespace())) continue;
            String packageName = (String)packageExport.getAttributes().get("osgi.wiring.package");
            List caps = (List)exportNames.getOrCompute(packageName);
            caps.add(packageExport);
        }
        if (exportNames.isEmpty()) {
            return;
        }
        for (Requirement req : resource.getRequirements(null)) {
            String packageName;
            List exportedPackages;
            CandidateSelector substitutes;
            if (!"osgi.wiring.package".equals(req.getNamespace()) || (substitutes = (CandidateSelector)this.m_candidateMap.get(req)) == null || substitutes.isEmpty() || (exportedPackages = (List)exportNames.get(packageName = (String)substitutes.getCurrentCandidate().getAttributes().get("osgi.wiring.package"))) == null || exportedPackages.containsAll(substitutes.getRemainingCandidates())) continue;
            for (Capability exportedPackage : exportedPackages) {
                this.m_subtitutableMap.put(exportedPackage, req);
            }
        }
    }

    ResolutionError checkSubstitutes() {
        OpenHashMap<Capability, Integer> substituteStatuses = new OpenHashMap<Capability, Integer>(this.m_subtitutableMap.size());
        for (Capability capability : this.m_subtitutableMap.keySet()) {
            substituteStatuses.put(capability, 0);
        }
        for (Capability capability : this.m_subtitutableMap.keySet()) {
            this.isSubstituted(capability, substituteStatuses);
        }
        for (Map.Entry entry : substituteStatuses.fast()) {
            Set dependents;
            Requirement substitutedReq = this.m_subtitutableMap.get(entry.getKey());
            if (substitutedReq != null) {
                this.m_session.permutateIfNeeded(ResolverImpl.PermutationType.SUBSTITUTE, substitutedReq, this);
            }
            if ((dependents = (Set)this.m_dependentMap.get(entry.getKey())) == null) continue;
            for (Requirement dependent : dependents) {
                CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(dependent);
                if (candidates == null) continue;
                block7: while (!candidates.isEmpty()) {
                    Capability candidate = candidates.getCurrentCandidate();
                    Integer candidateStatus = substituteStatuses.get(candidate);
                    if (candidateStatus == null) {
                        candidateStatus = 3;
                    }
                    switch (candidateStatus) {
                        case 3: {
                            break block7;
                        }
                        default: {
                            candidates.removeCurrentCandidate();
                            continue block7;
                        }
                    }
                }
                if (!candidates.isEmpty()) continue;
                if (Util.isOptional(dependent)) {
                    this.m_candidateMap.remove(dependent);
                    continue;
                }
                return new MissingRequirementError(dependent);
            }
        }
        return null;
    }

    private boolean isSubstituted(Capability substitutableCap, Map<Capability, Integer> substituteStatuses) {
        Integer substituteState = substituteStatuses.get(substitutableCap);
        if (substituteState == null) {
            return false;
        }
        switch (substituteState) {
            case 1: {
                substituteStatuses.put(substitutableCap, 3);
                return false;
            }
            case 2: {
                return true;
            }
            case 3: {
                return false;
            }
        }
        Requirement substitutableReq = this.m_subtitutableMap.get(substitutableCap);
        if (substitutableReq == null) {
            return false;
        }
        substituteStatuses.put(substitutableCap, 1);
        CandidateSelector substitutes = (CandidateSelector)this.m_candidateMap.get(substitutableReq);
        if (substitutes != null) {
            for (Capability substituteCandidate : substitutes.getRemainingCandidates()) {
                if (substituteCandidate.getResource().equals(substitutableCap.getResource())) {
                    substituteStatuses.put(substitutableCap, 3);
                    return false;
                }
                if (this.isSubstituted(substituteCandidate, substituteStatuses)) continue;
                substituteStatuses.put(substitutableCap, 2);
                return true;
            }
        }
        substituteStatuses.put(substitutableCap, 3);
        return false;
    }

    public ResolutionError populateDynamic() {
        LinkedList<Resource> toPopulate = new LinkedList<Resource>();
        ResolutionError rethrow = this.processCandidates(toPopulate, this.m_session.getDynamicRequirement(), this.m_session.getDynamicCandidates());
        this.addCandidates(this.m_session.getDynamicRequirement(), this.m_session.getDynamicCandidates());
        this.populate(toPopulate);
        CandidateSelector caps = (CandidateSelector)this.m_candidateMap.get(this.m_session.getDynamicRequirement());
        if (caps != null) {
            this.m_session.getDynamicCandidates().retainAll(caps.getRemainingCandidates());
        } else {
            this.m_session.getDynamicCandidates().clear();
        }
        if (this.m_session.getDynamicCandidates().isEmpty()) {
            if (rethrow == null) {
                rethrow = new DynamicImportFailed(this.m_session.getDynamicRequirement());
            }
            return rethrow;
        }
        PopulateResult result = new PopulateResult();
        result.success = true;
        this.m_populateResultCache.put(this.m_session.getDynamicHost(), result);
        return null;
    }

    private ResolutionError processCandidates(LinkedList<Resource> toPopulate, Requirement req, List<Capability> candidates) {
        ResolveContext rc = this.m_session.getContext();
        ResolutionError rethrow = null;
        HashSet<Capability> fragmentCands = null;
        Iterator<Capability> itCandCap = candidates.iterator();
        while (itCandCap.hasNext()) {
            Capability candCap = itCandCap.next();
            boolean isFragment = Util.isFragment(candCap.getResource());
            if (isFragment) {
                if (fragmentCands == null) {
                    fragmentCands = new HashSet<Capability>();
                }
                fragmentCands.add(candCap);
            }
            if ("osgi.wiring.host".equals(req.getNamespace()) && rc.getWirings().containsKey(candCap.getResource())) {
                itCandCap.remove();
                continue;
            }
            if (!isFragment && rc.getWirings().containsKey(candCap.getResource()) || candCap.getResource().equals(req.getResource())) continue;
            PopulateResult result = this.m_populateResultCache.get(candCap.getResource());
            if (result != null) {
                if (result.error != null) {
                    if (rethrow == null) {
                        rethrow = result.error;
                    }
                    itCandCap.remove();
                    continue;
                }
                if (result.success) continue;
                toPopulate.add(candCap.getResource());
                continue;
            }
            toPopulate.add(candCap.getResource());
        }
        if (fragmentCands != null) {
            for (Capability fragCand : fragmentCands) {
                Wiring wiring;
                String fragCandName = fragCand.getNamespace();
                if ("osgi.identity".equals(fragCandName) || (wiring = rc.getWirings().get(fragCand.getResource())) == null) continue;
                for (Wire wire : wiring.getRequiredResourceWires("osgi.wiring.host")) {
                    if (fragCandName.equals("osgi.wiring.package") && !rc.getWirings().get(wire.getProvider()).getResourceCapabilities(null).contains(fragCand)) continue;
                    candidates.remove(fragCand);
                    rc.insertHostedCapability(candidates, new WrappedCapability(wire.getCapability().getResource(), fragCand));
                }
            }
        }
        return rethrow;
    }

    public boolean isPopulated(Resource resource) {
        PopulateResult value = this.m_populateResultCache.get(resource);
        return value != null && value.success;
    }

    public ResolutionError getResolutionError(Resource resource) {
        PopulateResult value = this.m_populateResultCache.get(resource);
        return value != null ? value.error : null;
    }

    private void addCandidates(Requirement req, List<Capability> candidates) {
        this.m_candidateMap.put(req, new CandidateSelector(candidates, this.m_candidateSelectorsUnmodifiable));
        for (Capability cap : candidates) {
            ((CopyOnWriteSet)this.m_dependentMap.getOrCompute(cap)).add(req);
        }
    }

    private void addCandidates(Map<Requirement, List<Capability>> candidates) {
        for (Map.Entry<Requirement, List<Capability>> entry : candidates.entrySet()) {
            this.addCandidates(entry.getKey(), entry.getValue());
        }
    }

    public Resource getWrappedHost(Resource r) {
        Resource wrapped = this.m_allWrappedHosts.get(r);
        return wrapped == null ? r : wrapped;
    }

    public List<Capability> getCandidates(Requirement req) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        if (candidates != null) {
            return candidates.getRemainingCandidates();
        }
        return null;
    }

    public Capability getFirstCandidate(Requirement req) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        if (candidates != null && !candidates.isEmpty()) {
            return candidates.getCurrentCandidate();
        }
        return null;
    }

    public void removeFirstCandidate(Requirement req) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        Capability cap = candidates.removeCurrentCandidate();
        if (candidates.isEmpty()) {
            this.m_candidateMap.remove(req);
        }
        CopyOnWriteSet capPath = (CopyOnWriteSet)this.m_delta.getOrCompute(req);
        capPath.add(cap);
    }

    public CandidateSelector clearMultipleCardinalityCandidates(Requirement req, Collection<Capability> caps) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        ArrayList<Capability> remaining = new ArrayList<Capability>(candidates.getRemainingCandidates());
        remaining.removeAll(caps);
        candidates = new CandidateSelector(remaining, this.m_candidateSelectorsUnmodifiable);
        this.m_candidateMap.put(req, candidates);
        return candidates;
    }

    public ResolutionError prepare() {
        Map<Capability, Map<String, Map<Version, List<Requirement>>>> hostFragments = this.getHostFragments();
        ArrayList<WrappedResource> hostResources = new ArrayList<WrappedResource>();
        ArrayList<Resource> unselectedFragments = new ArrayList<Resource>();
        for (Map.Entry<Capability, Map<String, Map<Version, List<Requirement>>>> hostEntry : hostFragments.entrySet()) {
            Capability hostCap = hostEntry.getKey();
            Map<String, Map<Version, List<Requirement>>> fragments = hostEntry.getValue();
            ArrayList<Resource> selectedFragments = new ArrayList<Resource>();
            for (Map.Entry<String, Map<Version, List<Requirement>>> fragEntry : fragments.entrySet()) {
                boolean isFirst = true;
                for (Map.Entry<Version, List<Requirement>> versionEntry : fragEntry.getValue().entrySet()) {
                    for (Requirement hostReq : versionEntry.getValue()) {
                        if (isFirst) {
                            selectedFragments.add(hostReq.getResource());
                            isFirst = false;
                            continue;
                        }
                        ((CopyOnWriteSet)this.m_dependentMap.get(hostCap)).remove(hostReq);
                        CandidateSelector hosts = this.removeCandidate(hostReq, hostCap);
                        if (!hosts.isEmpty()) continue;
                        unselectedFragments.add(hostReq.getResource());
                    }
                }
            }
            WrappedResource wrappedHost = new WrappedResource(hostCap.getResource(), selectedFragments);
            hostResources.add(wrappedHost);
            this.m_allWrappedHosts.put(hostCap.getResource(), wrappedHost);
        }
        for (Resource fragment : unselectedFragments) {
            this.removeResource(fragment, new FragmentNotSelectedError(fragment));
        }
        for (WrappedResource hostResource : hostResources) {
            for (Requirement r : hostResource.getRequirements(null)) {
                Requirement origReq = ((WrappedRequirement)r).getDeclaredRequirement();
                CandidateSelector cands = (CandidateSelector)this.m_candidateMap.get(origReq);
                if (cands == null) continue;
                if (cands instanceof ShadowList) {
                    this.m_candidateMap.put(r, ShadowList.deepCopy((ShadowList)cands));
                } else {
                    this.m_candidateMap.put(r, cands.copy());
                }
                for (Capability cand : cands.getRemainingCandidates()) {
                    Set dependents = (Set)this.m_dependentMap.get(cand);
                    dependents.remove(origReq);
                    dependents.add(r);
                }
            }
        }
        for (WrappedResource hostResource : hostResources) {
            for (Capability c : hostResource.getCapabilities(null)) {
                Capability origCap;
                CopyOnWriteSet dependents;
                if (c.getNamespace().equals("osgi.wiring.host") || (dependents = (CopyOnWriteSet)this.m_dependentMap.get(origCap = ((HostedCapability)c).getDeclaredCapability())) == null) continue;
                dependents = new CopyOnWriteSet(dependents);
                this.m_dependentMap.put(c, (Requirement)((Object)dependents));
                for (Requirement r : dependents) {
                    ShadowList shadow;
                    CandidateSelector cands = (CandidateSelector)this.m_candidateMap.get(r);
                    if (!(cands instanceof ShadowList)) {
                        shadow = ShadowList.createShadowList(cands);
                        this.m_candidateMap.put(r, shadow);
                        cands = shadow;
                    } else {
                        shadow = (ShadowList)cands;
                    }
                    if (!origCap.getResource().equals(hostResource.getDeclaredResource())) {
                        shadow.insertHostedCapability(this.m_session.getContext(), (HostedCapability)c, new SimpleHostedCapability(hostResource.getDeclaredResource(), origCap));
                        continue;
                    }
                    shadow.replace(origCap, c);
                }
            }
        }
        for (Resource resource : this.m_session.getMandatoryResources()) {
            if (this.isPopulated(resource)) continue;
            return this.getResolutionError(resource);
        }
        this.populateSubstitutables();
        this.m_candidateMap.trim();
        this.m_dependentMap.trim();
        this.m_candidateSelectorsUnmodifiable.set(true);
        return null;
    }

    private Map<Capability, Map<String, Map<Version, List<Requirement>>>> getHostFragments() {
        HashMap<Capability, Map<String, Map<Version, List<Requirement>>>> hostFragments = new HashMap<Capability, Map<String, Map<Version, List<Requirement>>>>();
        for (Map.Entry entry : this.m_candidateMap.fast()) {
            Requirement req = (Requirement)entry.getKey();
            CandidateSelector caps = (CandidateSelector)entry.getValue();
            for (Capability cap : caps.getRemainingCandidates()) {
                ArrayList<Requirement> actual;
                TreeMap fragmentVersions;
                if (!req.getNamespace().equals("osgi.wiring.host")) continue;
                String resSymName = Util.getSymbolicName(req.getResource());
                Version resVersion = Util.getVersion(req.getResource());
                HashMap fragments = (HashMap)hostFragments.get(cap);
                if (fragments == null) {
                    fragments = new HashMap();
                    hostFragments.put(cap, fragments);
                }
                if ((fragmentVersions = (TreeMap)fragments.get(resSymName)) == null) {
                    fragmentVersions = new TreeMap(Collections.reverseOrder());
                    fragments.put(resSymName, fragmentVersions);
                }
                if ((actual = (ArrayList<Requirement>)fragmentVersions.get(resVersion)) == null) {
                    actual = new ArrayList<Requirement>();
                    if (resVersion == null) {
                        resVersion = new Version(0, 0, 0);
                    }
                    fragmentVersions.put(resVersion, actual);
                }
                actual.add(req);
            }
        }
        return hostFragments;
    }

    private void removeResource(Resource resource, ResolutionError ex) {
        PopulateResult result = this.m_populateResultCache.get(resource);
        result.success = false;
        result.error = ex;
        HashSet<Resource> unresolvedResources = new HashSet<Resource>();
        this.remove(resource, unresolvedResources);
        while (!unresolvedResources.isEmpty()) {
            Iterator it = unresolvedResources.iterator();
            resource = (Resource)it.next();
            it.remove();
            this.remove(resource, unresolvedResources);
        }
    }

    private void remove(Resource resource, Set<Resource> unresolvedResources) {
        for (Requirement r : resource.getRequirements(null)) {
            this.remove(r);
        }
        for (Capability c : resource.getCapabilities(null)) {
            this.remove(c, unresolvedResources);
        }
    }

    private void remove(Requirement req) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.remove(req);
        if (candidates != null) {
            for (Capability cap : candidates.getRemainingCandidates()) {
                Set dependents = (Set)this.m_dependentMap.get(cap);
                if (dependents == null) continue;
                dependents.remove(req);
            }
        }
    }

    private void remove(Capability c, Set<Resource> unresolvedResources) {
        Set dependents = (Set)this.m_dependentMap.remove(c);
        if (dependents != null) {
            for (Requirement r : dependents) {
                CandidateSelector candidates = this.removeCandidate(r, c);
                if (!candidates.isEmpty()) continue;
                this.m_candidateMap.remove(r);
                if (Util.isOptional(r)) continue;
                PopulateResult result = this.m_populateResultCache.get(r.getResource());
                if (result != null) {
                    result.success = false;
                    result.error = new MissingRequirementError(r, this.m_populateResultCache.get((Object)c.getResource()).error);
                }
                unresolvedResources.add(r.getResource());
            }
        }
    }

    private CandidateSelector removeCandidate(Requirement req, Capability cap) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        candidates.remove(cap);
        return candidates;
    }

    public Candidates copy() {
        return new Candidates(this.m_session, this.m_candidateSelectorsUnmodifiable, this.m_dependentMap, this.m_candidateMap.deepClone(), this.m_allWrappedHosts, this.m_populateResultCache, this.m_subtitutableMap, this.m_delta.deepClone());
    }

    public void dump(ResolveContext rc) {
        CopyOnWriteSet<Resource> resources = new CopyOnWriteSet<Resource>();
        for (Map.Entry entry : this.m_candidateMap.entrySet()) {
            resources.add(((Requirement)entry.getKey()).getResource());
        }
        System.out.println("=== BEGIN CANDIDATE MAP ===");
        for (Resource resource : resources) {
            CandidateSelector candidates;
            Wiring wiring = rc.getWirings().get(resource);
            System.out.println("  " + resource + " (" + (wiring != null ? "RESOLVED)" : "UNRESOLVED)"));
            List<Requirement> reqs = wiring != null ? wiring.getResourceRequirements(null) : resource.getRequirements(null);
            for (Requirement req : reqs) {
                candidates = (CandidateSelector)this.m_candidateMap.get(req);
                if (candidates == null || candidates.isEmpty()) continue;
                System.out.println("    " + req + ": " + candidates);
            }
            reqs = wiring != null ? Util.getDynamicRequirements(wiring.getResourceRequirements(null)) : Util.getDynamicRequirements(resource.getRequirements(null));
            for (Requirement req : reqs) {
                candidates = (CandidateSelector)this.m_candidateMap.get(req);
                if (candidates == null || candidates.isEmpty()) continue;
                System.out.println("    " + req + ": " + candidates);
            }
        }
        System.out.println("=== END CANDIDATE MAP ===");
    }

    public Candidates permutate(Requirement req) {
        if (!Util.isMultiple(req) && this.canRemoveCandidate(req)) {
            Candidates perm = this.copy();
            perm.removeFirstCandidate(req);
            return perm;
        }
        return null;
    }

    public boolean canRemoveCandidate(Requirement req) {
        CandidateSelector candidates = (CandidateSelector)this.m_candidateMap.get(req);
        if (candidates != null) {
            Set dependents;
            Capability current = candidates.getCurrentCandidate();
            if (current != null && req.equals(this.m_subtitutableMap.get(current)) && (dependents = (Set)this.m_dependentMap.get(current)) != null) {
                for (Requirement dependent : dependents) {
                    CandidateSelector dependentSelector = (CandidateSelector)this.m_candidateMap.get(dependent);
                    if (dependentSelector == null || dependentSelector.getRemainingCandidateCount() > 1 || !current.equals(dependentSelector.getCurrentCandidate())) continue;
                    return false;
                }
            }
            return candidates.getRemainingCandidateCount() > 1 || Util.isOptional(req);
        }
        return false;
    }

    static class MissingRequirementError
    extends ResolutionError {
        private final Requirement requirement;
        private final ResolutionError cause;

        public MissingRequirementError(Requirement requirement) {
            this(requirement, null);
        }

        public MissingRequirementError(Requirement requirement, ResolutionError cause) {
            this.requirement = requirement;
            this.cause = cause;
        }

        @Override
        public String getMessage() {
            String msg = "Unable to resolve " + this.requirement.getResource() + ": missing requirement " + this.requirement;
            if (this.cause != null) {
                msg = msg + " [caused by: " + this.cause.getMessage() + "]";
            }
            return msg;
        }

        @Override
        public Collection<Requirement> getUnresolvedRequirements() {
            return Collections.singleton(this.requirement);
        }

        @Override
        public ResolutionException toException() {
            return new ReasonException(ReasonException.Reason.MissingRequirement, this.getMessage(), this.cause != null ? this.cause.toException() : null, this.getUnresolvedRequirements());
        }
    }

    static class FragmentNotSelectedError
    extends ResolutionError {
        private final Resource resource;

        public FragmentNotSelectedError(Resource resource) {
            this.resource = resource;
        }

        @Override
        public String getMessage() {
            return "Fragment was not selected for attachment: " + this.resource;
        }

        @Override
        public Collection<Requirement> getUnresolvedRequirements() {
            return this.resource.getRequirements("osgi.wiring.host");
        }

        @Override
        public ResolutionException toException() {
            return new ReasonException(ReasonException.Reason.FragmentNotSelected, this.getMessage(), null, this.getUnresolvedRequirements());
        }
    }

    static class DynamicImportFailed
    extends ResolutionError {
        private final Requirement requirement;

        public DynamicImportFailed(Requirement requirement) {
            this.requirement = requirement;
        }

        @Override
        public String getMessage() {
            return "Dynamic import failed.";
        }

        @Override
        public Collection<Requirement> getUnresolvedRequirements() {
            return Collections.singleton(this.requirement);
        }

        @Override
        public ResolutionException toException() {
            return new ReasonException(ReasonException.Reason.DynamicImport, this.getMessage(), null, this.getUnresolvedRequirements());
        }
    }

    static class PopulateResult {
        boolean success;
        ResolutionError error;
        List<Requirement> remaining;
        Map<Requirement, List<Capability>> candidates;

        PopulateResult() {
        }

        public String toString() {
            return this.success ? "true" : (this.error != null ? this.error.getMessage() : "???");
        }
    }
}

