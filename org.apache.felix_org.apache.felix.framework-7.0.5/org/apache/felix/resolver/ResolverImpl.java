/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.felix.resolver.Candidates;
import org.apache.felix.resolver.Logger;
import org.apache.felix.resolver.ResolutionError;
import org.apache.felix.resolver.Util;
import org.apache.felix.resolver.WireImpl;
import org.apache.felix.resolver.WrappedCapability;
import org.apache.felix.resolver.WrappedRequirement;
import org.apache.felix.resolver.WrappedResource;
import org.apache.felix.resolver.reason.ReasonException;
import org.apache.felix.resolver.util.ArrayMap;
import org.apache.felix.resolver.util.CandidateSelector;
import org.apache.felix.resolver.util.OpenHashMap;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;

public class ResolverImpl
implements Resolver {
    private final AccessControlContext m_acc = System.getSecurityManager() != null ? AccessController.getContext() : null;
    private final Logger m_logger;
    private final int m_parallelism;
    private final Executor m_executor;

    public ResolverImpl(Logger logger) {
        this(logger, Runtime.getRuntime().availableProcessors());
    }

    public ResolverImpl(Logger logger, int parallelism) {
        this.m_logger = logger;
        this.m_parallelism = parallelism;
        this.m_executor = null;
    }

    public ResolverImpl(Logger logger, Executor executor) {
        this.m_logger = logger;
        this.m_parallelism = -1;
        this.m_executor = executor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Resource, List<Wire>> resolve(ResolveContext rc) throws ResolutionException {
        if (this.m_executor != null) {
            return this.resolve(rc, this.m_executor);
        }
        if (this.m_parallelism > 1) {
            Map<Resource, List<Wire>> map;
            ExecutorService executor = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<ExecutorService>(){

                @Override
                public ExecutorService run() {
                    return Executors.newFixedThreadPool(ResolverImpl.this.m_parallelism);
                }
            }, this.m_acc) : Executors.newFixedThreadPool(this.m_parallelism);
            try {
                map = this.resolve(rc, executor);
            }
            catch (Throwable throwable) {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Void>(executor){
                        final /* synthetic */ ExecutorService val$executor;
                        {
                            this.val$executor = executorService;
                        }

                        @Override
                        public Void run() {
                            this.val$executor.shutdownNow();
                            return null;
                        }
                    }, this.m_acc);
                } else {
                    executor.shutdownNow();
                }
                throw throwable;
            }
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */, this.m_acc);
            } else {
                executor.shutdownNow();
            }
            return map;
        }
        return this.resolve(rc, new DumbExecutor());
    }

    public Map<Resource, List<Wire>> resolve(ResolveContext rc, Executor executor) throws ResolutionException {
        ResolveSession session = ResolveSession.createSession(rc, executor, null, null, null);
        return this.doResolve(session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<Resource, List<Wire>> doResolve(ResolveSession session) throws ResolutionException {
        boolean retry;
        Map<Resource, List<Wire>> wireMap = new HashMap<Resource, List<Wire>>();
        do {
            retry = false;
            try {
                this.getInitialCandidates(session);
                if (session.getCurrentError() != null) {
                    throw session.getCurrentError().toException();
                }
                HashMap<Resource, ResolutionError> faultyResources = new HashMap<Resource, ResolutionError>();
                Candidates allCandidates = this.findValidCandidates(session, faultyResources);
                session.checkForCancel();
                if (session.getCurrentError() != null) {
                    Set resourceKeys = faultyResources.keySet();
                    retry = session.getOptionalResources().removeAll(resourceKeys);
                    for (Resource resource : resourceKeys) {
                        if (!session.invalidateRelatedResource(resource)) continue;
                        retry = true;
                    }
                    for (Map.Entry entry : faultyResources.entrySet()) {
                        this.m_logger.logUsesConstraintViolation((Resource)entry.getKey(), (ResolutionError)entry.getValue());
                    }
                    if (retry) continue;
                    throw session.getCurrentError().toException();
                }
                if (session.getMultipleCardCandidates() != null) {
                    allCandidates = session.getMultipleCardCandidates();
                }
                if (session.isDynamic()) {
                    wireMap = ResolverImpl.populateDynamicWireMap(session, wireMap, allCandidates);
                    continue;
                }
                for (Resource resource : allCandidates.getRootHosts().keySet()) {
                    if (!allCandidates.isPopulated(resource)) continue;
                    wireMap = ResolverImpl.populateWireMap(session, allCandidates.getWrappedHost(resource), wireMap, allCandidates);
                }
            }
            finally {
                session.clearPermutations();
            }
        } while (retry);
        return wireMap;
    }

    private void getInitialCandidates(ResolveSession session) {
        ResolutionError prepareError;
        Candidates initialCandidates;
        if (session.isDynamic()) {
            initialCandidates = new Candidates(session);
            prepareError = initialCandidates.populateDynamic();
            if (prepareError != null) {
                session.setCurrentError(prepareError);
                return;
            }
        } else {
            ArrayList<Resource> toPopulate = new ArrayList<Resource>();
            for (Resource resource : session.getMandatoryResources()) {
                if (!Util.isFragment(resource) && session.getContext().getWirings().get(resource) != null) continue;
                toPopulate.add(resource);
            }
            for (Resource resource : session.getOptionalResources()) {
                if (!Util.isFragment(resource) && session.getContext().getWirings().get(resource) != null) continue;
                toPopulate.add(resource);
            }
            initialCandidates = new Candidates(session);
            initialCandidates.populate(toPopulate);
        }
        prepareError = initialCandidates.prepare();
        if (prepareError != null) {
            session.setCurrentError(prepareError);
        } else {
            session.addPermutation(PermutationType.USES, initialCandidates);
        }
    }

    private Candidates findValidCandidates(ResolveSession session, Map<Resource, ResolutionError> faultyResources) {
        Candidates allCandidates = null;
        boolean foundFaultyResources = false;
        while ((allCandidates = session.getNextPermutation()) != null) {
            HashMap<Resource, ResolutionError> currentFaultyResources = new HashMap<Resource, ResolutionError>();
            session.setCurrentError(this.checkConsistency(session, allCandidates, currentFaultyResources));
            if (!currentFaultyResources.isEmpty()) {
                if (!foundFaultyResources) {
                    foundFaultyResources = true;
                    faultyResources.putAll(currentFaultyResources);
                } else if (faultyResources.size() > currentFaultyResources.size()) {
                    faultyResources.clear();
                    faultyResources.putAll(currentFaultyResources);
                }
            }
            if (!session.isCancelled() && session.getCurrentError() != null) continue;
        }
        return allCandidates;
    }

    private ResolutionError checkConsistency(ResolveSession session, Candidates allCandidates, Map<Resource, ResolutionError> currentFaultyResources) {
        ResolutionError rethrow = allCandidates.checkSubstitutes();
        if (rethrow != null) {
            return rethrow;
        }
        Map<Resource, Resource> allhosts = allCandidates.getRootHosts();
        Map<Resource, Packages> resourcePkgMap = this.calculatePackageSpaces(session, allCandidates, allhosts.values());
        ResolutionError error = null;
        OpenHashMap<Resource, Object> resultCache = new OpenHashMap<Resource, Object>(resourcePkgMap.size());
        for (Map.Entry<Resource, Resource> entry : allhosts.entrySet()) {
            rethrow = this.checkPackageSpaceConsistency(session, entry.getValue(), allCandidates, session.isDynamic(), resourcePkgMap, resultCache);
            if (session.isCancelled()) {
                return null;
            }
            if (rethrow == null) continue;
            Resource faultyResource = entry.getKey();
            for (Requirement faultyReq : rethrow.getUnresolvedRequirements()) {
                if (!(faultyReq instanceof WrappedRequirement)) continue;
                faultyResource = ((WrappedRequirement)faultyReq).getDeclaredRequirement().getResource();
                break;
            }
            currentFaultyResources.put(faultyResource, rethrow);
            error = rethrow;
        }
        return error;
    }

    @Override
    public Map<Resource, List<Wire>> resolveDynamic(ResolveContext context, Wiring hostWiring, Requirement dynamicRequirement) throws ResolutionException {
        Resource host = hostWiring.getResource();
        List<Capability> matches = context.findProviders(dynamicRequirement);
        if (!matches.isEmpty()) {
            for (Capability cap : matches) {
                if (cap.getNamespace().equals("osgi.wiring.package")) continue;
                throw new IllegalArgumentException("Matching candidate does not provide a package name.");
            }
            ResolveSession session = ResolveSession.createSession(context, new DumbExecutor(), host, dynamicRequirement, matches);
            return this.doResolve(session);
        }
        throw new Candidates.MissingRequirementError(dynamicRequirement).toException();
    }

    private static List<WireCandidate> getWireCandidates(ResolveSession session, Candidates allCandidates, Resource resource) {
        ArrayList<WireCandidate> wireCandidates = new ArrayList<WireCandidate>(256);
        Wiring wiring = session.getContext().getWirings().get(resource);
        if (wiring != null) {
            for (Wire wire : wiring.getRequiredResourceWires(null)) {
                Capability c;
                Requirement r = wire.getRequirement();
                if (!r.getResource().equals(wire.getRequirer()) || Util.isDynamic(r)) {
                    r = new WrappedRequirement(wire.getRequirer(), r);
                }
                if (!(c = wire.getCapability()).getResource().equals(wire.getProvider())) {
                    c = new WrappedCapability(wire.getProvider(), c);
                }
                wireCandidates.add(new WireCandidate(r, c));
            }
            Requirement dynamicReq = session.getDynamicRequirement();
            if (dynamicReq != null && resource.equals(session.getDynamicHost())) {
                Capability cap = allCandidates.getFirstCandidate(dynamicReq);
                wireCandidates.add(new WireCandidate(dynamicReq, cap));
            }
        } else {
            for (Requirement req : resource.getRequirements(null)) {
                List<Capability> candCaps;
                if (Util.isDynamic(req) || (candCaps = allCandidates.getCandidates(req)) == null) continue;
                if (Util.isMultiple(req)) {
                    for (Capability cap : candCaps) {
                        wireCandidates.add(new WireCandidate(req, cap));
                    }
                    continue;
                }
                Capability cap = candCaps.get(0);
                wireCandidates.add(new WireCandidate(req, cap));
            }
        }
        return wireCandidates;
    }

    private static Packages getPackages(ResolveSession session, Candidates allCandidates, Map<Resource, List<WireCandidate>> allWireCandidates, Map<Resource, Packages> allPackages, Resource resource, Packages resourcePkgs) {
        for (WireCandidate wire : allWireCandidates.get(resource)) {
            String pkgName;
            if (Util.isDynamic(wire.requirement) && (resourcePkgs.m_exportedPkgs.containsKey(pkgName = (String)wire.capability.getAttributes().get("osgi.wiring.package")) || resourcePkgs.m_importedPkgs.containsKey(pkgName) || resourcePkgs.m_requiredPkgs.containsKey(pkgName))) {
                throw new IllegalArgumentException("Resource " + resource + " cannot dynamically import package '" + pkgName + "' since it already has access to it.");
            }
            ResolverImpl.mergeCandidatePackages(session, allPackages, allCandidates, resourcePkgs, wire.requirement, wire.capability, new HashSet<Capability>(), new HashSet<Resource>());
        }
        return resourcePkgs;
    }

    private void computeUses(ResolveSession session, Map<Resource, List<WireCandidate>> allWireCandidates, Map<Resource, Packages> resourcePkgMap, Resource resource) {
        boolean isDynamicImporting;
        List<WireCandidate> wireCandidates = allWireCandidates.get(resource);
        Packages resourcePkgs = resourcePkgMap.get(resource);
        Wiring wiring = session.getContext().getWirings().get(resource);
        HashSet<Capability> usesCycleMap = new HashSet<Capability>();
        int size = wireCandidates.size();
        boolean bl = isDynamicImporting = size > 0 && Util.isDynamic(wireCandidates.get((int)(size - 1)).requirement);
        if (wiring == null || isDynamicImporting) {
            List<Requirement> blameReqs;
            for (WireCandidate wireCandidate : wireCandidates) {
                Requirement req = wireCandidate.requirement;
                Capability cap = wireCandidate.capability;
                if (req.getNamespace().equals("osgi.wiring.bundle") || req.getNamespace().equals("osgi.wiring.package")) continue;
                blameReqs = Collections.singletonList(req);
                this.mergeUses(session, resource, resourcePkgs, cap, blameReqs, cap, resourcePkgMap, usesCycleMap);
            }
            for (List list : resourcePkgs.m_importedPkgs.values()) {
                for (Blame blame : list) {
                    blameReqs = Collections.singletonList(blame.m_reqs.get(0));
                    this.mergeUses(session, resource, resourcePkgs, blame.m_cap, blameReqs, null, resourcePkgMap, usesCycleMap);
                }
            }
            for (List list : resourcePkgs.m_requiredPkgs.values()) {
                for (Blame blame : list) {
                    blameReqs = Collections.singletonList(blame.m_reqs.get(0));
                    this.mergeUses(session, resource, resourcePkgs, blame.m_cap, blameReqs, null, resourcePkgMap, usesCycleMap);
                }
            }
        }
    }

    private static void mergeCandidatePackages(ResolveSession session, Map<Resource, Packages> resourcePkgMap, Candidates allCandidates, Packages packages, Requirement currentReq, Capability candCap, Set<Capability> capabilityCycles, Set<Resource> visitedRequiredBundles) {
        block9: {
            Wiring candWiring;
            block8: {
                if (!capabilityCycles.add(candCap)) {
                    return;
                }
                if (!candCap.getNamespace().equals("osgi.wiring.package")) break block8;
                ResolverImpl.mergeCandidatePackage(packages.m_importedPkgs, currentReq, candCap);
                break block9;
            }
            if (!candCap.getNamespace().equals("osgi.wiring.bundle")) break block9;
            if (visitedRequiredBundles.add(candCap.getResource())) {
                for (Blame blame : resourcePkgMap.get((Object)candCap.getResource()).m_exportedPkgs.values()) {
                    ResolverImpl.mergeCandidatePackage(packages.m_requiredPkgs, currentReq, blame.m_cap);
                }
                for (Blame blame : resourcePkgMap.get((Object)candCap.getResource()).m_substitePkgs.values()) {
                    ResolverImpl.mergeCandidatePackage(packages.m_requiredPkgs, currentReq, blame.m_cap);
                }
            }
            if ((candWiring = session.getContext().getWirings().get(candCap.getResource())) != null) {
                for (Wire w : candWiring.getRequiredResourceWires(null)) {
                    if (!w.getRequirement().getNamespace().equals("osgi.wiring.bundle") || !Util.isReexport(w.getRequirement())) continue;
                    ResolverImpl.mergeCandidatePackages(session, resourcePkgMap, allCandidates, packages, currentReq, w.getCapability(), capabilityCycles, visitedRequiredBundles);
                }
            } else {
                for (Requirement req : candCap.getResource().getRequirements(null)) {
                    Capability cap;
                    if (!req.getNamespace().equals("osgi.wiring.bundle") || !Util.isReexport(req) || (cap = allCandidates.getFirstCandidate(req)) == null) continue;
                    ResolverImpl.mergeCandidatePackages(session, resourcePkgMap, allCandidates, packages, currentReq, cap, capabilityCycles, visitedRequiredBundles);
                }
            }
        }
    }

    private static void mergeCandidatePackage(OpenHashMap<String, List<Blame>> packages, Requirement currentReq, Capability candCap) {
        if (candCap.getNamespace().equals("osgi.wiring.package")) {
            String pkgName = (String)candCap.getAttributes().get("osgi.wiring.package");
            List<Requirement> blameReqs = Collections.singletonList(currentReq);
            List<Blame> blames = packages.getOrCompute(pkgName);
            blames.add(new Blame(candCap, blameReqs));
        }
    }

    private void mergeUses(ResolveSession session, Resource current, Packages currentPkgs, Capability mergeCap, List<Requirement> blameReqs, Capability matchingCap, Map<Resource, Packages> resourcePkgMap, Set<Capability> cycleMap) {
        if (current.equals(mergeCap.getResource())) {
            return;
        }
        if (!cycleMap.add(mergeCap)) {
            return;
        }
        for (Capability candSourceCap : ResolverImpl.getPackageSources(mergeCap, resourcePkgMap)) {
            String s = candSourceCap.getDirectives().get("uses");
            if (s == null || s.length() <= 0) continue;
            List<String> uses = (List<String>)session.getUsesCache().get(s);
            if (uses == null) {
                uses = ResolverImpl.parseUses(s);
                session.getUsesCache().put(s, uses);
            }
            Packages candSourcePkgs = resourcePkgMap.get(candSourceCap.getResource());
            for (String usedPkgName : uses) {
                List<Blame> candSourceBlames;
                Blame candExportedBlame = candSourcePkgs.m_exportedPkgs.get(usedPkgName);
                if (candExportedBlame != null) {
                    candSourceBlames = Collections.singletonList(candExportedBlame);
                } else {
                    candSourceBlames = candSourcePkgs.m_requiredPkgs.get(usedPkgName);
                    if (candSourceBlames == null) {
                        candSourceBlames = candSourcePkgs.m_importedPkgs.get(usedPkgName);
                    }
                }
                if (candSourceBlames == null) continue;
                ArrayMap<Set<Capability>, UsedBlames> usedPkgBlames = currentPkgs.m_usedPkgs.getOrCompute(usedPkgName);
                ArrayList<Blame> newBlames = new ArrayList<Blame>();
                for (Blame blame : candSourceBlames) {
                    List<Requirement> newBlameReqs;
                    if (blame.m_reqs != null) {
                        newBlameReqs = new ArrayList<Requirement>(blameReqs.size() + 1);
                        newBlameReqs.addAll(blameReqs);
                        newBlameReqs.add(blame.m_reqs.get(blame.m_reqs.size() - 1));
                    } else {
                        newBlameReqs = blameReqs;
                    }
                    newBlames.add(new Blame(blame.m_cap, newBlameReqs));
                }
                this.addUsedBlames(usedPkgBlames, newBlames, matchingCap, resourcePkgMap);
                for (Blame newBlame : newBlames) {
                    this.mergeUses(session, current, currentPkgs, newBlame.m_cap, newBlame.m_reqs, matchingCap, resourcePkgMap, cycleMap);
                }
            }
        }
    }

    private Map<Resource, Packages> calculatePackageSpaces(final ResolveSession session, final Candidates allCandidates, Collection<Resource> hosts) {
        Packages packages;
        Resource resource;
        EnhancedExecutor executor = new EnhancedExecutor(session.getExecutor());
        final ConcurrentHashMap allWireCandidates = new ConcurrentHashMap();
        ConcurrentHashMap tasks = new ConcurrentHashMap(allCandidates.getNbResources());
        for (Resource resource2 : hosts) {
            class Computer
            implements Runnable {
                final Resource resource;
                final /* synthetic */ ResolveSession val$session;
                final /* synthetic */ Candidates val$allCandidates;
                final /* synthetic */ Map val$allWireCandidates;
                final /* synthetic */ ConcurrentMap val$tasks;
                final /* synthetic */ EnhancedExecutor val$executor;

                public Computer(Resource resource) {
                    this.val$session = resolveSession;
                    this.val$allCandidates = candidates;
                    this.val$allWireCandidates = map;
                    this.val$tasks = concurrentMap;
                    this.val$executor = enhancedExecutor;
                    this.resource = resource;
                }

                @Override
                public void run() {
                    List wireCandidates = ResolverImpl.getWireCandidates(this.val$session, this.val$allCandidates, this.resource);
                    this.val$allWireCandidates.put(this.resource, wireCandidates);
                    for (WireCandidate w : wireCandidates) {
                        Computer c;
                        Resource u = w.capability.getResource();
                        if (this.val$tasks.containsKey(u) || this.val$tasks.putIfAbsent(u, c = new Computer(u)) != null) continue;
                        this.val$executor.execute(c);
                    }
                }
            }
            executor.execute(new Computer(resource2));
        }
        executor.await();
        final OpenHashMap<Resource, Packages> allPackages = new OpenHashMap<Resource, Packages>(allCandidates.getNbResources());
        for (final Resource resource3 : allWireCandidates.keySet()) {
            final Packages packages2 = new Packages(resource3);
            allPackages.put(resource3, packages2);
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    ResolverImpl.calculateExportedPackages(session, allCandidates, resource3, packages2.m_exportedPkgs, packages2.m_substitePkgs);
                }
            });
        }
        executor.await();
        for (final Resource resource4 : allWireCandidates.keySet()) {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    ResolverImpl.getPackages(session, allCandidates, allWireCandidates, allPackages, resource4, (Packages)allPackages.get(resource4));
                }
            });
        }
        executor.await();
        for (Map.Entry entry : allPackages.fast()) {
            resource = (Resource)entry.getKey();
            packages = (Packages)entry.getValue();
            if (packages.m_requiredPkgs.isEmpty()) continue;
            ResolverImpl.getPackageSourcesInternal(session, allPackages, resource, packages);
        }
        for (Map.Entry entry : allPackages.fast()) {
            resource = (Resource)entry.getKey();
            packages = (Packages)entry.getValue();
            if (!packages.m_sources.isEmpty()) continue;
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    ResolverImpl.getPackageSourcesInternal(session, allPackages, resource, packages);
                }
            });
        }
        executor.await();
        for (final Resource resource5 : allWireCandidates.keySet()) {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    ResolverImpl.this.computeUses(session, allWireCandidates, allPackages, resource5);
                }
            });
        }
        executor.await();
        return allPackages;
    }

    private static List<String> parseUses(String s) {
        int nb = 1;
        int l = s.length();
        for (int i = 0; i < l; ++i) {
            if (s.charAt(i) != ',') continue;
            ++nb;
        }
        ArrayList<String> uses = new ArrayList<String>(nb);
        int start = 0;
        while (true) {
            char c;
            int end;
            char c2;
            if (start < l && ((c2 = s.charAt(start)) == ' ' || c2 == ',')) {
                ++start;
                continue;
            }
            for (end = start + 1; end < l && (c = s.charAt(end)) != ' ' && c != ','; ++end) {
            }
            if (start >= l) break;
            uses.add(s.substring(start, end));
            start = end + 1;
        }
        return uses;
    }

    private void addUsedBlames(ArrayMap<Set<Capability>, UsedBlames> usedBlames, Collection<Blame> blames, Capability matchingCap, Map<Resource, Packages> resourcePkgMap) {
        Set<Object> usedCaps;
        if (blames.size() == 1) {
            usedCaps = ResolverImpl.getPackageSources(blames.iterator().next().m_cap, resourcePkgMap);
        } else {
            usedCaps = new HashSet();
            for (Blame blame : blames) {
                usedCaps.addAll(ResolverImpl.getPackageSources(blame.m_cap, resourcePkgMap));
            }
        }
        if (usedCaps.isEmpty()) {
            this.m_logger.log(3, "Package sources are empty for used capability: " + blames);
            return;
        }
        UsedBlames addToBlame = usedBlames.getOrCompute(usedCaps);
        for (Blame blame : blames) {
            addToBlame.addBlame(blame, matchingCap);
        }
    }

    /*
     * WARNING - void declaration
     */
    private ResolutionError checkPackageSpaceConsistency(ResolveSession session, Resource resource, Candidates allCandidates, boolean dynamic, Map<Resource, Packages> resourcePkgMap, Map<Resource, Object> resultCache) {
        OpenHashMap<String, List<Blame>> allImportRequirePkgs;
        ArrayMap<Set<Capability>, UsedBlames> pkgBlames;
        if (!dynamic && session.getContext().getWirings().containsKey(resource)) {
            return null;
        }
        Object cache = resultCache.get(resource);
        if (cache != null) {
            return cache instanceof ResolutionError ? (ResolutionError)cache : null;
        }
        Packages pkgs = resourcePkgMap.get(resource);
        ResolutionError rethrow = null;
        for (Map.Entry<String, List<Blame>> entry : pkgs.m_importedPkgs.fast()) {
            String pkgName = entry.getKey();
            List<Blame> blames = entry.getValue();
            if (blames.size() <= 1) continue;
            Object var14_15 = null;
            for (Blame blame : blames) {
                void var14_14;
                if (var14_14 == null) {
                    Blame blame2 = blame;
                    continue;
                }
                if (var14_14.m_cap.getResource().equals(blame.m_cap.getResource())) continue;
                session.addPermutation(PermutationType.IMPORT, allCandidates.permutate(blame.m_reqs.get(0)));
                session.addPermutation(PermutationType.IMPORT, allCandidates.permutate(var14_14.m_reqs.get(0)));
                rethrow = new UseConstraintError(session.getContext(), allCandidates, resource, pkgName, (Blame)var14_14, blame);
                if (this.m_logger.isDebugEnabled()) {
                    this.m_logger.debug("Candidate permutation failed due to a conflict with a fragment import; will try another if possible. (" + rethrow.getMessage() + ")");
                }
                return rethrow;
            }
        }
        AtomicReference<Candidates> permRef1 = new AtomicReference<Candidates>();
        AtomicReference<Candidates> permRef2 = new AtomicReference<Candidates>();
        Set mutated = null;
        for (Map.Entry entry : pkgs.m_exportedPkgs.fast()) {
            String string = (String)entry.getKey();
            Blame exportBlame = (Blame)entry.getValue();
            pkgBlames = pkgs.m_usedPkgs.get(string);
            if (pkgBlames == null) continue;
            for (UsedBlames usedBlames : pkgBlames.values()) {
                if (ResolverImpl.isCompatible(exportBlame, usedBlames.m_caps, resourcePkgMap)) continue;
                mutated = mutated != null ? mutated : new HashSet();
                rethrow = this.permuteUsedBlames(session, rethrow, allCandidates, resource, string, null, usedBlames, permRef1, permRef2, mutated);
            }
            if (rethrow == null) continue;
            if (!mutated.isEmpty()) {
                session.addPermutation(PermutationType.USES, (Candidates)permRef1.get());
                session.addPermutation(PermutationType.USES, (Candidates)permRef2.get());
            }
            if (this.m_logger.isDebugEnabled()) {
                this.m_logger.debug("Candidate permutation failed due to a conflict between an export and import; will try another if possible. (" + rethrow.getMessage() + ")");
            }
            return rethrow;
        }
        if (pkgs.m_requiredPkgs.isEmpty()) {
            allImportRequirePkgs = pkgs.m_importedPkgs;
        } else {
            allImportRequirePkgs = new OpenHashMap(pkgs.m_requiredPkgs.size() + pkgs.m_importedPkgs.size());
            allImportRequirePkgs.putAll(pkgs.m_requiredPkgs);
            allImportRequirePkgs.putAll(pkgs.m_importedPkgs);
        }
        for (Map.Entry<String, List<Blame>> entry : allImportRequirePkgs.fast()) {
            String pkgName = entry.getKey();
            pkgBlames = pkgs.m_usedPkgs.get(pkgName);
            if (pkgBlames == null) continue;
            List<Blame> requirementBlames = entry.getValue();
            for (UsedBlames usedBlames : pkgBlames.values()) {
                if (!ResolverImpl.isCompatible(requirementBlames, usedBlames.m_caps, resourcePkgMap)) {
                    mutated = mutated != null ? mutated : new HashSet();
                    Blame requirementBlame = requirementBlames.get(0);
                    rethrow = this.permuteUsedBlames(session, rethrow, allCandidates, resource, pkgName, requirementBlame, usedBlames, permRef1, permRef2, mutated);
                }
                if (rethrow == null) continue;
                if (!mutated.isEmpty()) {
                    session.addPermutation(PermutationType.USES, permRef1.get());
                    session.addPermutation(PermutationType.USES, permRef2.get());
                }
                for (Blame requirementBlame : requirementBlames) {
                    Requirement req = requirementBlame.m_reqs.get(0);
                    if (mutated.contains(req)) continue;
                    session.permutateIfNeeded(PermutationType.IMPORT, req, allCandidates);
                }
                if (this.m_logger.isDebugEnabled()) {
                    this.m_logger.debug("Candidate permutation failed due to a conflict between imports; will try another if possible. (" + rethrow.getMessage() + ")");
                }
                return rethrow;
            }
        }
        resultCache.put(resource, Boolean.TRUE);
        long l = session.getPermutationCount();
        for (Requirement req : resource.getRequirements(null)) {
            Capability cap = allCandidates.getFirstCandidate(req);
            if (cap == null || resource.equals(cap.getResource())) continue;
            rethrow = this.checkPackageSpaceConsistency(session, cap.getResource(), allCandidates, false, resourcePkgMap, resultCache);
            if (session.isCancelled()) {
                return null;
            }
            if (rethrow == null) continue;
            if (l == session.getPermutationCount()) {
                session.addPermutation(PermutationType.IMPORT, allCandidates.permutate(req));
            }
            return rethrow;
        }
        return null;
    }

    private ResolutionError permuteUsedBlames(ResolveSession session, ResolutionError rethrow, Candidates allCandidates, Resource resource, String pkgName, Blame requirementBlame, UsedBlames usedBlames, AtomicReference<Candidates> permRef1, AtomicReference<Candidates> permRef2, Set<Requirement> mutated) {
        for (Blame usedBlame : usedBlames.m_blames) {
            Requirement req;
            Requirement req2;
            Candidates perm1;
            if (session.checkMultiple(usedBlames, usedBlame, allCandidates)) continue;
            if (rethrow == null) {
                rethrow = requirementBlame == null ? new UseConstraintError(session.getContext(), allCandidates, resource, pkgName, usedBlame) : new UseConstraintError(session.getContext(), allCandidates, resource, pkgName, requirementBlame, usedBlame);
            }
            if ((perm1 = permRef1.get()) == null) {
                perm1 = allCandidates.copy();
                permRef1.set(perm1);
            }
            for (int reqIdx = usedBlame.m_reqs.size() - 1; reqIdx >= 0 && !this.permuteUsedBlameRequirement(req2 = usedBlame.m_reqs.get(reqIdx), mutated, perm1); --reqIdx) {
            }
            Candidates perm2 = permRef2.get();
            if (perm2 == null) {
                perm2 = allCandidates.copy();
                permRef2.set(perm2);
            }
            for (int reqIdx = 0; reqIdx < usedBlame.m_reqs.size() && !this.permuteUsedBlameRequirement(req = usedBlame.m_reqs.get(reqIdx), mutated, perm2); ++reqIdx) {
            }
        }
        return rethrow;
    }

    private boolean permuteUsedBlameRequirement(Requirement req, Set<Requirement> mutated, Candidates permutation) {
        if (Util.isMultiple(req)) {
            return false;
        }
        if (mutated.contains(req)) {
            return true;
        }
        if (permutation.canRemoveCandidate(req)) {
            permutation.removeFirstCandidate(req);
            mutated.add(req);
            return true;
        }
        return false;
    }

    private static OpenHashMap<String, Blame> calculateExportedPackages(ResolveSession session, Candidates allCandidates, Resource resource, OpenHashMap<String, Blame> exports, OpenHashMap<String, Blame> substitutes) {
        block6: {
            block5: {
                Wiring wiring = session.getContext().getWirings().get(resource);
                List<Capability> caps = wiring != null ? wiring.getResourceCapabilities(null) : resource.getCapabilities(null);
                for (Capability cap : caps) {
                    if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                    if (!cap.getResource().equals(resource)) {
                        cap = new WrappedCapability(resource, cap);
                    }
                    exports.put((String)cap.getAttributes().get("osgi.wiring.package"), new Blame(cap, null));
                }
                if (wiring == null) break block5;
                for (Wire wire : session.getContext().getSubstitutionWires(wiring)) {
                    Capability cap = wire.getCapability();
                    if (!cap.getResource().equals(wire.getProvider())) {
                        cap = new WrappedCapability(wire.getProvider(), cap);
                    }
                    substitutes.put((String)cap.getAttributes().get("osgi.wiring.package"), new Blame(cap, null));
                }
                break block6;
            }
            if (exports.isEmpty()) break block6;
            for (Requirement req : resource.getRequirements(null)) {
                String pkgName;
                Blame blame;
                Capability cand;
                if (!req.getNamespace().equals("osgi.wiring.package") || (cand = allCandidates.getFirstCandidate(req)) == null || (blame = exports.remove(pkgName = (String)cand.getAttributes().get("osgi.wiring.package"))) == null) continue;
                substitutes.put(pkgName, new Blame(cand, null));
            }
        }
        return exports;
    }

    private static boolean isCompatible(Blame currentBlame, Set<Capability> candSources, Map<Resource, Packages> resourcePkgMap) {
        if (candSources.contains(currentBlame.m_cap)) {
            return true;
        }
        Set<Capability> currentSources = ResolverImpl.getPackageSources(currentBlame.m_cap, resourcePkgMap);
        return currentSources.containsAll(candSources) || candSources.containsAll(currentSources);
    }

    private static boolean isCompatible(List<Blame> currentBlames, Set<Capability> candSources, Map<Resource, Packages> resourcePkgMap) {
        int size = currentBlames.size();
        switch (size) {
            case 0: {
                return true;
            }
            case 1: {
                return ResolverImpl.isCompatible(currentBlames.get(0), candSources, resourcePkgMap);
            }
        }
        HashSet<Capability> currentSources = new HashSet<Capability>(currentBlames.size());
        for (Blame currentBlame : currentBlames) {
            Set<Capability> blameSources = ResolverImpl.getPackageSources(currentBlame.m_cap, resourcePkgMap);
            currentSources.addAll(blameSources);
        }
        return currentSources.containsAll(candSources) || candSources.containsAll(currentSources);
    }

    private static Set<Capability> getPackageSources(Capability cap, Map<Resource, Packages> resourcePkgMap) {
        Resource resource = cap.getResource();
        if (resource == null) {
            return new HashSet<Capability>();
        }
        OpenHashMap<Capability, Set<Capability>> sources = resourcePkgMap.get((Object)resource).m_sources;
        if (sources == null) {
            return new HashSet<Capability>();
        }
        Set<Capability> packageSources = sources.get(cap);
        if (packageSources == null) {
            return new HashSet<Capability>();
        }
        return packageSources;
    }

    /*
     * WARNING - void declaration
     */
    private static void getPackageSourcesInternal(ResolveSession session, Map<Resource, Packages> resourcePkgMap, Resource resource, Packages packages) {
        String pkgName;
        Wiring wiring = session.getContext().getWirings().get(resource);
        List<Capability> caps = wiring != null ? wiring.getResourceCapabilities(null) : resource.getCapabilities(null);
        OpenHashMap<String, Set<Capability>> pkgs = new OpenHashMap<String, Set<Capability>>(caps.size()){

            @Override
            public Set<Capability> compute(String pkgName) {
                return new HashSet<Capability>();
            }
        };
        OpenHashMap<Capability, Set<Capability>> sources = packages.m_sources;
        for (Capability capability : caps) {
            if (capability.getNamespace().equals("osgi.wiring.package")) {
                void var9_9;
                pkgName = (String)capability.getAttributes().get("osgi.wiring.package");
                Set pkgCaps = (Set)pkgs.getOrCompute(pkgName);
                if (!resource.equals(capability.getResource())) {
                    WrappedCapability wrappedCapability = new WrappedCapability(resource, capability);
                }
                sources.put((Capability)var9_9, pkgCaps);
                pkgCaps.add(var9_9);
                continue;
            }
            String uses = capability.getDirectives().get("uses");
            if (uses != null && uses.length() > 0) {
                sources.put(capability, Collections.singleton(capability));
                continue;
            }
            sources.put(capability, Collections.emptySet());
        }
        for (Map.Entry entry : pkgs.fast()) {
            pkgName = (String)entry.getKey();
            List<Blame> required = packages.m_requiredPkgs.get(pkgName);
            if (required == null) continue;
            Set srcs = (Set)entry.getValue();
            for (Blame blame : required) {
                Capability bcap = blame.m_cap;
                if (!srcs.add(bcap)) continue;
                Resource capResource = bcap.getResource();
                Packages capPackages = resourcePkgMap.get(capResource);
                Set<Capability> additional = capPackages.m_sources.get(bcap);
                if (additional == null) {
                    ResolverImpl.getPackageSourcesInternal(session, resourcePkgMap, capResource, capPackages);
                    additional = capPackages.m_sources.get(bcap);
                }
                srcs.addAll(additional);
            }
        }
    }

    private static Resource getDeclaredResource(Resource resource) {
        if (resource instanceof WrappedResource) {
            return ((WrappedResource)resource).getDeclaredResource();
        }
        return resource;
    }

    private static Capability getDeclaredCapability(Capability c) {
        if (c instanceof HostedCapability) {
            return ((HostedCapability)c).getDeclaredCapability();
        }
        return c;
    }

    private static Requirement getDeclaredRequirement(Requirement r) {
        if (r instanceof WrappedRequirement) {
            return ((WrappedRequirement)r).getDeclaredRequirement();
        }
        return r;
    }

    private static Map<Resource, List<Wire>> populateWireMap(ResolveSession session, Resource resource, Map<Resource, List<Wire>> wireMap, Candidates allCandidates) {
        Resource unwrappedResource = ResolverImpl.getDeclaredResource(resource);
        if (!session.getContext().getWirings().containsKey(unwrappedResource) && !wireMap.containsKey(unwrappedResource)) {
            Wire wire;
            wireMap.put(unwrappedResource, Collections.emptyList());
            ArrayList<WireImpl> packageWires = new ArrayList<WireImpl>();
            ArrayList<WireImpl> bundleWires = new ArrayList<WireImpl>();
            ArrayList<WireImpl> capabilityWires = new ArrayList<WireImpl>();
            block0: for (Requirement requirement : resource.getRequirements(null)) {
                List<Capability> cands = allCandidates.getCandidates(requirement);
                if (cands == null || cands.size() <= 0) continue;
                for (Capability cand : cands) {
                    if (!cand.getNamespace().startsWith("osgi.wiring.") || !resource.equals(cand.getResource())) {
                        ResolverImpl.populateWireMap(session, cand.getResource(), wireMap, allCandidates);
                        Resource provider = requirement.getNamespace().equals("osgi.identity") ? ResolverImpl.getDeclaredCapability(cand).getResource() : ResolverImpl.getDeclaredResource(cand.getResource());
                        wire = new WireImpl(unwrappedResource, ResolverImpl.getDeclaredRequirement(requirement), provider, ResolverImpl.getDeclaredCapability(cand));
                        if (requirement.getNamespace().equals("osgi.wiring.package")) {
                            packageWires.add((WireImpl)wire);
                        } else if (requirement.getNamespace().equals("osgi.wiring.bundle")) {
                            bundleWires.add((WireImpl)wire);
                        } else {
                            capabilityWires.add((WireImpl)wire);
                        }
                    }
                    if (Util.isMultiple(requirement)) continue;
                    continue block0;
                }
            }
            packageWires.addAll(bundleWires);
            packageWires.addAll(capabilityWires);
            wireMap.put(unwrappedResource, packageWires);
            if (resource instanceof WrappedResource) {
                List<Resource> fragments = ((WrappedResource)resource).getFragments();
                for (Resource fragment : fragments) {
                    ArrayList<WireImpl> fragmentWires = wireMap.get(fragment);
                    fragmentWires = fragmentWires == null ? new ArrayList<WireImpl>() : fragmentWires;
                    for (Requirement req : fragment.getRequirements(null)) {
                        if (ResolverImpl.isPayload(req)) continue;
                        if (req.getNamespace().equals("osgi.wiring.host")) {
                            fragmentWires.add(new WireImpl(ResolverImpl.getDeclaredResource(fragment), req, unwrappedResource, unwrappedResource.getCapabilities("osgi.wiring.host").get(0)));
                            continue;
                        }
                        if (session.getContext().getWirings().containsKey(fragment) || wireMap.containsKey(fragment) || (wire = ResolverImpl.createWire(req, allCandidates)) == null) continue;
                        fragmentWires.add((WireImpl)wire);
                    }
                    wireMap.put(fragment, fragmentWires);
                }
            }
            for (Resource resource2 : session.getRelatedResources(unwrappedResource)) {
                if (!allCandidates.isPopulated(resource2)) continue;
                ResolverImpl.populateWireMap(session, resource2, wireMap, allCandidates);
            }
        }
        return wireMap;
    }

    private static Wire createWire(Requirement requirement, Candidates allCandidates) {
        Capability cand = allCandidates.getFirstCandidate(requirement);
        if (cand == null) {
            return null;
        }
        return new WireImpl(ResolverImpl.getDeclaredResource(requirement.getResource()), ResolverImpl.getDeclaredRequirement(requirement), ResolverImpl.getDeclaredResource(cand.getResource()), ResolverImpl.getDeclaredCapability(cand));
    }

    private static boolean isPayload(Requirement fragmentReq) {
        if ("osgi.ee".equals(fragmentReq.getNamespace())) {
            return false;
        }
        return !"osgi.wiring.host".equals(fragmentReq.getNamespace());
    }

    private static Map<Resource, List<Wire>> populateDynamicWireMap(ResolveSession session, Map<Resource, List<Wire>> wireMap, Candidates allCandidates) {
        wireMap.put(session.getDynamicHost(), Collections.emptyList());
        ArrayList<WireImpl> packageWires = new ArrayList<WireImpl>();
        Capability dynCand = allCandidates.getFirstCandidate(session.getDynamicRequirement());
        if (!session.getContext().getWirings().containsKey(dynCand.getResource())) {
            ResolverImpl.populateWireMap(session, dynCand.getResource(), wireMap, allCandidates);
        }
        packageWires.add(new WireImpl(session.getDynamicHost(), session.getDynamicRequirement(), ResolverImpl.getDeclaredResource(dynCand.getResource()), ResolverImpl.getDeclaredCapability(dynCand)));
        wireMap.put(session.getDynamicHost(), packageWires);
        return wireMap;
    }

    private static void dumpResourcePkgMap(ResolveContext rc, Map<Resource, Packages> resourcePkgMap) {
        System.out.println("+++RESOURCE PKG MAP+++");
        for (Map.Entry<Resource, Packages> entry : resourcePkgMap.entrySet()) {
            ResolverImpl.dumpResourcePkgs(rc, entry.getKey(), entry.getValue());
        }
    }

    private static void dumpResourcePkgs(ResolveContext rc, Resource resource, Packages packages) {
        Wiring wiring = rc.getWirings().get(resource);
        System.out.println(resource + " (" + (wiring != null ? "RESOLVED)" : "UNRESOLVED)"));
        System.out.println("  EXPORTED");
        for (Map.Entry entry : packages.m_exportedPkgs.entrySet()) {
            System.out.println("    " + (String)entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("  IMPORTED");
        for (Map.Entry entry : packages.m_importedPkgs.entrySet()) {
            System.out.println("    " + (String)entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("  REQUIRED");
        for (Map.Entry entry : packages.m_requiredPkgs.entrySet()) {
            System.out.println("    " + (String)entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("  USED");
        for (Map.Entry entry : packages.m_usedPkgs.entrySet()) {
            System.out.println("    " + (String)entry.getKey() + " - " + ((ArrayMap)entry.getValue()).values());
        }
    }

    static class DumbExecutor
    implements Executor {
        DumbExecutor() {
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    private static class EnhancedExecutor {
        private final Executor executor;
        private final Queue<Future<Void>> awaiting = new ConcurrentLinkedQueue<Future<Void>>();
        private final AtomicReference<Throwable> throwable = new AtomicReference();

        public EnhancedExecutor(Executor executor) {
            this.executor = executor;
        }

        public void execute(final Runnable runnable) {
            FutureTask<Void> task = new FutureTask<Void>(new Runnable(){

                @Override
                public void run() {
                    try {
                        runnable.run();
                    }
                    catch (Throwable t) {
                        EnhancedExecutor.this.throwable.compareAndSet(null, t);
                    }
                }
            }, null);
            this.awaiting.add(task);
            try {
                this.executor.execute(task);
            }
            catch (Throwable t) {
                task.cancel(false);
                this.throwable.compareAndSet(null, t);
            }
        }

        public void await() {
            Future<Void> awaitTask;
            while (this.throwable.get() == null && (awaitTask = this.awaiting.poll()) != null) {
                if (awaitTask.isDone() || awaitTask.isCancelled()) continue;
                try {
                    awaitTask.get();
                }
                catch (CancellationException cancellationException) {
                }
                catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
            Throwable t = this.throwable.get();
            if (t != null) {
                if (t instanceof Runnable) {
                    throw (RuntimeException)t;
                }
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new RuntimeException(t);
            }
        }
    }

    private static final class UseConstraintError
    extends ResolutionError {
        private final ResolveContext m_context;
        private final Candidates m_allCandidates;
        private final Resource m_resource;
        private final String m_pkgName;
        private final Blame m_blame1;
        private final Blame m_blame2;

        public UseConstraintError(ResolveContext context, Candidates allCandidates, Resource resource, String pkgName, Blame blame) {
            this(context, allCandidates, resource, pkgName, blame, null);
        }

        public UseConstraintError(ResolveContext context, Candidates allCandidates, Resource resource, String pkgName, Blame blame1, Blame blame2) {
            this.m_context = context;
            this.m_allCandidates = allCandidates;
            this.m_resource = resource;
            this.m_pkgName = pkgName;
            if (blame1 == null) {
                throw new NullPointerException("First blame cannot be null.");
            }
            this.m_blame1 = blame1;
            this.m_blame2 = blame2;
        }

        @Override
        public String getMessage() {
            if (this.m_blame2 == null) {
                return "Uses constraint violation. Unable to resolve resource " + Util.getSymbolicName(this.m_resource) + " [" + this.m_resource + "] because it exports package '" + this.m_pkgName + "' and is also exposed to it from resource " + Util.getSymbolicName(this.m_blame1.m_cap.getResource()) + " [" + this.m_blame1.m_cap.getResource() + "] via the following dependency chain:\n\n" + this.toStringBlame(this.m_blame1);
            }
            return "Uses constraint violation. Unable to resolve resource " + Util.getSymbolicName(this.m_resource) + " [" + this.m_resource + "] because it is exposed to package '" + this.m_pkgName + "' from resources " + Util.getSymbolicName(this.m_blame1.m_cap.getResource()) + " [" + this.m_blame1.m_cap.getResource() + "] and " + Util.getSymbolicName(this.m_blame2.m_cap.getResource()) + " [" + this.m_blame2.m_cap.getResource() + "] via two dependency chains.\n\nChain 1:\n" + this.toStringBlame(this.m_blame1) + "\n\nChain 2:\n" + this.toStringBlame(this.m_blame2);
        }

        @Override
        public Collection<Requirement> getUnresolvedRequirements() {
            if (this.m_blame2 == null) {
                return Collections.singleton(this.m_blame1.m_reqs.get(0));
            }
            return Collections.singleton(this.m_blame2.m_reqs.get(0));
        }

        private String toStringBlame(Blame blame) {
            StringBuilder sb = new StringBuilder();
            if (blame.m_reqs != null && !blame.m_reqs.isEmpty()) {
                for (int i = 0; i < blame.m_reqs.size(); ++i) {
                    Requirement req = blame.m_reqs.get(i);
                    sb.append("  ");
                    sb.append(Util.getSymbolicName(req.getResource()));
                    sb.append(" [");
                    sb.append(req.getResource().toString());
                    sb.append("]\n");
                    if (req.getNamespace().equals("osgi.wiring.package")) {
                        sb.append("    import: ");
                    } else {
                        sb.append("    require: ");
                    }
                    sb.append(req.getDirectives().get("filter"));
                    sb.append("\n     |");
                    if (req.getNamespace().equals("osgi.wiring.package")) {
                        sb.append("\n    export: ");
                    } else {
                        sb.append("\n    provide: ");
                    }
                    if (i + 1 < blame.m_reqs.size()) {
                        Capability cap = this.getSatisfyingCapability(blame.m_reqs.get(i));
                        if (cap.getNamespace().equals("osgi.wiring.package")) {
                            sb.append("osgi.wiring.package");
                            sb.append("=");
                            sb.append(cap.getAttributes().get("osgi.wiring.package"));
                            Capability usedCap = this.getSatisfyingCapability(blame.m_reqs.get(i + 1));
                            sb.append("; uses:=");
                            sb.append(usedCap.getAttributes().get("osgi.wiring.package"));
                        } else {
                            sb.append(cap);
                        }
                        sb.append("\n");
                        continue;
                    }
                    Capability export = this.getSatisfyingCapability(blame.m_reqs.get(i));
                    sb.append(export.getNamespace());
                    sb.append(": ");
                    Object namespaceVal = export.getAttributes().get(export.getNamespace());
                    if (namespaceVal != null) {
                        sb.append(namespaceVal.toString());
                    } else {
                        for (Map.Entry<String, Object> attrEntry : export.getAttributes().entrySet()) {
                            sb.append(attrEntry.getKey()).append('=').append(attrEntry.getValue()).append(';');
                        }
                    }
                    if (export.getNamespace().equals("osgi.wiring.package") && !export.getAttributes().get("osgi.wiring.package").equals(blame.m_cap.getAttributes().get("osgi.wiring.package"))) {
                        sb.append("; uses:=");
                        sb.append(blame.m_cap.getAttributes().get("osgi.wiring.package"));
                        sb.append("\n    export: ");
                        sb.append("osgi.wiring.package");
                        sb.append("=");
                        sb.append(blame.m_cap.getAttributes().get("osgi.wiring.package"));
                    }
                    sb.append("\n  ");
                    sb.append(Util.getSymbolicName(blame.m_cap.getResource()));
                    sb.append(" [");
                    sb.append(blame.m_cap.getResource().toString());
                    sb.append("]");
                }
            } else {
                sb.append(blame.m_cap.getResource().toString());
            }
            return sb.toString();
        }

        private Capability getSatisfyingCapability(Requirement req) {
            Capability cap = this.m_allCandidates.getFirstCandidate(req);
            if (cap == null && this.m_context.getWirings().containsKey(req.getResource())) {
                List<Wire> wires = this.m_context.getWirings().get(req.getResource()).getRequiredResourceWires(null);
                req = ResolverImpl.getDeclaredRequirement(req);
                for (Wire w : wires) {
                    if (!w.getRequirement().equals(req)) continue;
                    cap = w.getCapability();
                    break;
                }
            }
            return cap;
        }

        @Override
        public ResolutionException toException() {
            return new ReasonException(ReasonException.Reason.UseConstraint, this.getMessage(), null, this.getUnresolvedRequirements());
        }
    }

    private static class UsedBlames {
        public final Set<Capability> m_caps;
        public final List<Blame> m_blames = new ArrayList<Blame>();
        private Map<Requirement, Set<Capability>> m_rootCauses;

        public UsedBlames(Set<Capability> caps) {
            this.m_caps = caps;
        }

        public void addBlame(Blame blame, Capability matchingRootCause) {
            Requirement req;
            if (!this.m_caps.contains(blame.m_cap)) {
                throw new IllegalArgumentException("Attempt to add a blame with a different used capability: " + blame.m_cap);
            }
            this.m_blames.add(blame);
            if (matchingRootCause != null && Util.isMultiple(req = blame.m_reqs.get(0))) {
                Set<Capability> rootCauses;
                if (this.m_rootCauses == null) {
                    this.m_rootCauses = new HashMap<Requirement, Set<Capability>>();
                }
                if ((rootCauses = this.m_rootCauses.get(req)) == null) {
                    rootCauses = new HashSet<Capability>();
                    this.m_rootCauses.put(req, rootCauses);
                }
                rootCauses.add(matchingRootCause);
            }
        }

        public Set<Capability> getRootCauses(Requirement req) {
            if (this.m_rootCauses == null) {
                return Collections.emptySet();
            }
            Set<Capability> result = this.m_rootCauses.get(req);
            return result == null ? Collections.emptySet() : result;
        }

        public String toString() {
            return this.m_blames.toString();
        }
    }

    private static class Blame {
        public final Capability m_cap;
        public final List<Requirement> m_reqs;

        public Blame(Capability cap, List<Requirement> reqs) {
            this.m_cap = cap;
            this.m_reqs = reqs;
        }

        public String toString() {
            return this.m_cap.getResource() + "." + this.m_cap.getAttributes().get("osgi.wiring.package") + (this.m_reqs == null || this.m_reqs.isEmpty() ? " NO BLAME" : " BLAMED ON " + this.m_reqs);
        }

        public boolean equals(Object o) {
            return o instanceof Blame && this.m_reqs.equals(((Blame)o).m_reqs) && this.m_cap.equals(((Blame)o).m_cap);
        }
    }

    public static class Packages {
        public final OpenHashMap<String, Blame> m_exportedPkgs;
        public final OpenHashMap<String, Blame> m_substitePkgs;
        public final OpenHashMap<String, List<Blame>> m_importedPkgs;
        public final OpenHashMap<String, List<Blame>> m_requiredPkgs;
        public final OpenHashMap<String, ArrayMap<Set<Capability>, UsedBlames>> m_usedPkgs;
        public final OpenHashMap<Capability, Set<Capability>> m_sources;

        public Packages(Resource resource) {
            int nbCaps = resource.getCapabilities(null).size();
            int nbReqs = resource.getRequirements(null).size();
            this.m_exportedPkgs = new OpenHashMap(nbCaps);
            this.m_substitePkgs = new OpenHashMap(nbCaps);
            this.m_importedPkgs = new OpenHashMap<String, List<Blame>>(nbReqs){

                @Override
                public List<Blame> compute(String s) {
                    return new ArrayList<Blame>();
                }
            };
            this.m_requiredPkgs = new OpenHashMap<String, List<Blame>>(nbReqs){

                @Override
                public List<Blame> compute(String s) {
                    return new ArrayList<Blame>();
                }
            };
            this.m_usedPkgs = new OpenHashMap<String, ArrayMap<Set<Capability>, UsedBlames>>(128){

                @Override
                protected ArrayMap<Set<Capability>, UsedBlames> compute(String s) {
                    return new ArrayMap<Set<Capability>, UsedBlames>(){

                        @Override
                        protected UsedBlames compute(Set<Capability> key) {
                            return new UsedBlames(key);
                        }
                    };
                }
            };
            this.m_sources = new OpenHashMap(nbCaps);
        }
    }

    private static final class WireCandidate {
        public final Requirement requirement;
        public final Capability capability;

        public WireCandidate(Requirement requirement, Capability capability) {
            this.requirement = requirement;
            this.capability = capability;
        }
    }

    static class ResolveSession
    implements Runnable {
        private final ResolveContext m_resolveContext;
        private final Collection<Resource> m_mandatoryResources;
        private final Collection<Resource> m_optionalResources;
        private final Resource m_dynamicHost;
        private final Requirement m_dynamicReq;
        private final List<Capability> m_dynamicCandidates;
        private Map<Resource, Boolean> m_validRelatedResources = new HashMap<Resource, Boolean>(0);
        private Map<Resource, Collection<Resource>> m_relatedResources = new HashMap<Resource, Collection<Resource>>(0);
        private final List<Candidates> m_usesPermutations = new LinkedList<Candidates>();
        private int m_usesIndex = 0;
        private final List<Candidates> m_importPermutations = new LinkedList<Candidates>();
        private int m_importIndex = 0;
        private final List<Candidates> m_substPermutations = new LinkedList<Candidates>();
        private int m_substituteIndex = 0;
        private Candidates m_multipleCardCandidates = null;
        private final Set<Object> m_processedDeltas = new HashSet<Object>();
        private final Executor m_executor;
        private final Set<Requirement> m_mutated = new HashSet<Requirement>();
        private final Set<Requirement> m_sub_mutated = new HashSet<Requirement>();
        private final ConcurrentMap<String, List<String>> m_usesCache = new ConcurrentHashMap<String, List<String>>();
        private ResolutionError m_currentError;
        private volatile CancellationException m_isCancelled = null;

        static ResolveSession createSession(ResolveContext resolveContext, Executor executor, Resource dynamicHost, Requirement dynamicReq, List<Capability> dynamicCandidates) {
            ResolveSession session = new ResolveSession(resolveContext, executor, dynamicHost, dynamicReq, dynamicCandidates);
            session.getContext().onCancel(session);
            session.initMandatoryAndOptionalResources();
            return session;
        }

        private ResolveSession(ResolveContext resolveContext, Executor executor, Resource dynamicHost, Requirement dynamicReq, List<Capability> dynamicCandidates) {
            this.m_resolveContext = resolveContext;
            this.m_executor = executor;
            this.m_dynamicHost = dynamicHost;
            this.m_dynamicReq = dynamicReq;
            this.m_dynamicCandidates = dynamicCandidates;
            if (this.m_dynamicHost != null) {
                this.m_mandatoryResources = Collections.singletonList(dynamicHost);
                this.m_optionalResources = Collections.emptyList();
            } else {
                this.m_mandatoryResources = new ArrayList<Resource>();
                this.m_optionalResources = new ArrayList<Resource>();
            }
        }

        private void initMandatoryAndOptionalResources() {
            if (!this.isDynamic()) {
                this.m_mandatoryResources.addAll(this.getContext().getMandatoryResources());
                this.m_optionalResources.addAll(this.getContext().getOptionalResources());
            }
        }

        Candidates getMultipleCardCandidates() {
            return this.m_multipleCardCandidates;
        }

        ResolveContext getContext() {
            return this.m_resolveContext;
        }

        ConcurrentMap<String, List<String>> getUsesCache() {
            return this.m_usesCache;
        }

        void permutateIfNeeded(PermutationType type, Requirement req, Candidates permutation) {
            List<Capability> candidates = permutation.getCandidates(req);
            if (candidates != null && candidates.size() > 1) {
                if (type == PermutationType.SUBSTITUTE ? !this.m_sub_mutated.add(req) : !this.m_mutated.add(req)) {
                    return;
                }
                this.addPermutation(type, permutation.permutate(req));
            }
        }

        private void clearMutateIndexes() {
            this.m_usesIndex = 0;
            this.m_importIndex = 0;
            this.m_substituteIndex = 0;
            this.m_mutated.clear();
        }

        void addPermutation(PermutationType type, Candidates permutation) {
            if (permutation != null) {
                List<Candidates> typeToAddTo = null;
                try {
                    switch (type) {
                        case USES: {
                            typeToAddTo = this.m_usesPermutations;
                            this.m_usesPermutations.add(this.m_usesIndex++, permutation);
                            break;
                        }
                        case IMPORT: {
                            typeToAddTo = this.m_importPermutations;
                            this.m_importPermutations.add(this.m_importIndex++, permutation);
                            break;
                        }
                        case SUBSTITUTE: {
                            typeToAddTo = this.m_substPermutations;
                            this.m_substPermutations.add(this.m_substituteIndex++, permutation);
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Unknown permitation type: " + (Object)((Object)type));
                        }
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    typeToAddTo.add(permutation);
                }
            }
        }

        Candidates getNextPermutation() {
            Candidates next = null;
            do {
                if (!this.m_usesPermutations.isEmpty()) {
                    next = this.m_usesPermutations.remove(0);
                    continue;
                }
                if (!this.m_importPermutations.isEmpty()) {
                    next = this.m_importPermutations.remove(0);
                    continue;
                }
                if (!this.m_substPermutations.isEmpty()) {
                    next = this.m_substPermutations.remove(0);
                    continue;
                }
                return null;
            } while (!this.m_processedDeltas.add(next.getDelta()));
            this.m_multipleCardCandidates = null;
            this.clearMutateIndexes();
            return next;
        }

        void clearPermutations() {
            this.m_usesPermutations.clear();
            this.m_importPermutations.clear();
            this.m_substPermutations.clear();
            this.m_multipleCardCandidates = null;
            this.m_processedDeltas.clear();
            this.m_currentError = null;
        }

        boolean checkMultiple(UsedBlames usedBlames, Blame usedBlame, Candidates permutation) {
            CandidateSelector candidates = null;
            Requirement req = usedBlame.m_reqs.get(0);
            if (Util.isMultiple(req)) {
                if (this.m_multipleCardCandidates == null) {
                    this.m_multipleCardCandidates = permutation.copy();
                }
                candidates = this.m_multipleCardCandidates.clearMultipleCardinalityCandidates(req, usedBlames.getRootCauses(req));
            }
            return candidates != null && !candidates.isEmpty();
        }

        long getPermutationCount() {
            return this.m_usesPermutations.size() + this.m_importPermutations.size() + this.m_substPermutations.size();
        }

        Executor getExecutor() {
            return this.m_executor;
        }

        ResolutionError getCurrentError() {
            return this.m_currentError;
        }

        void setCurrentError(ResolutionError error) {
            this.m_currentError = error;
        }

        boolean isDynamic() {
            return this.m_dynamicHost != null;
        }

        Collection<Resource> getMandatoryResources() {
            return this.m_mandatoryResources;
        }

        Collection<Resource> getOptionalResources() {
            return this.m_optionalResources;
        }

        Resource getDynamicHost() {
            return this.m_dynamicHost;
        }

        Requirement getDynamicRequirement() {
            return this.m_dynamicReq;
        }

        List<Capability> getDynamicCandidates() {
            return this.m_dynamicCandidates;
        }

        public boolean isValidRelatedResource(Resource resource) {
            Boolean valid = this.m_validRelatedResources.get(resource);
            if (valid == null) {
                this.m_validRelatedResources.put(resource, Boolean.TRUE);
                valid = Boolean.TRUE;
            }
            return valid;
        }

        public boolean invalidateRelatedResource(Resource faultyResource) {
            Boolean valid = this.m_validRelatedResources.get(faultyResource);
            if (valid != null && valid.booleanValue()) {
                this.m_validRelatedResources.put(faultyResource, Boolean.FALSE);
                return true;
            }
            return false;
        }

        public Collection<Resource> getRelatedResources(Resource resource) {
            Collection<Resource> related = this.m_relatedResources.get(resource);
            return related == null ? Collections.emptyList() : related;
        }

        public void setRelatedResources(Resource resource, Collection<Resource> related) {
            this.m_relatedResources.put(resource, related);
        }

        @Override
        public void run() {
            this.m_isCancelled = new CancellationException();
        }

        boolean isCancelled() {
            return this.m_isCancelled != null;
        }

        void checkForCancel() throws ResolutionException {
            if (this.isCancelled()) {
                throw new ResolutionException("Resolver operation has been cancelled.", this.m_isCancelled, null);
            }
        }
    }

    static enum PermutationType {
        USES,
        IMPORT,
        SUBSTITUTE;

    }
}

