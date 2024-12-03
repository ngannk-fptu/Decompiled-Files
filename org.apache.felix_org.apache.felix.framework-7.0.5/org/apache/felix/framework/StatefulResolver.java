/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleProtectionDomain;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.BundleWiringImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ResolveContextImpl;
import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.capabilityset.CapabilitySet;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.resolver.CandidateComparator;
import org.apache.felix.framework.resolver.ResolveException;
import org.apache.felix.framework.util.ShrinkableCollection;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.wiring.BundleRequirementImpl;
import org.apache.felix.framework.wiring.BundleWireImpl;
import org.apache.felix.resolver.ResolverImpl;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundlePermission;
import org.osgi.framework.CapabilityPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.Resolver;

class StatefulResolver {
    private final Logger m_logger;
    private final Felix m_felix;
    private final ServiceRegistry m_registry;
    private final Executor m_executor;
    private final ResolverImpl m_resolver;
    private boolean m_isResolving = false;
    private final Set<BundleRevision> m_revisions;
    private final Set<BundleRevision> m_fragments;
    private final Map<String, CapabilitySet> m_capSets;
    private final Map<String, List<BundleRevision>> m_singletons;
    private final Set<BundleRevision> m_selectedSingletons;
    private volatile ServiceRegistration<?> m_serviceRegistration;

    StatefulResolver(Felix felix, ServiceRegistry registry) {
        this.m_felix = felix;
        this.m_registry = registry;
        this.m_logger = this.m_felix.getLogger();
        this.m_executor = this.getExecutor();
        this.m_resolver = new ResolverImpl((org.apache.felix.resolver.Logger)this.m_logger, this.m_executor);
        this.m_revisions = new HashSet<BundleRevision>();
        this.m_fragments = new HashSet<BundleRevision>();
        this.m_capSets = new HashMap<String, CapabilitySet>();
        this.m_singletons = new HashMap<String, List<BundleRevision>>();
        this.m_selectedSingletons = new HashSet<BundleRevision>();
        ArrayList<String> indices = new ArrayList<String>();
        indices.add("osgi.wiring.bundle");
        this.m_capSets.put("osgi.wiring.bundle", new CapabilitySet(indices, true));
        indices = new ArrayList();
        indices.add("osgi.wiring.package");
        this.m_capSets.put("osgi.wiring.package", new CapabilitySet(indices, true));
        indices = new ArrayList();
        indices.add("osgi.wiring.host");
        this.m_capSets.put("osgi.wiring.host", new CapabilitySet(indices, true));
    }

    private Executor getExecutor() {
        String str = this.m_felix.getProperty("felix.resolver.parallelism");
        int parallelism = Runtime.getRuntime().availableProcessors();
        if (str != null) {
            try {
                parallelism = Integer.parseInt(str);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (parallelism <= 1) {
            return new Executor(){

                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(parallelism, parallelism, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory(){
            final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "FelixResolver-" + this.counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    void start() {
        this.m_serviceRegistration = this.m_registry.registerService(this.m_felix, new String[]{Resolver.class.getName()}, new ResolverImpl((org.apache.felix.resolver.Logger)this.m_logger, 1), null);
    }

    void stop() {
        ServiceRegistration<?> reg = this.m_serviceRegistration;
        if (reg != null) {
            reg.unregister();
            this.m_serviceRegistration = null;
        }
    }

    synchronized void addRevision(BundleRevision br) {
        this.removeRevision(br);
        this.m_revisions.add(br);
        boolean isSingleton = Util.isSingleton(br);
        if (isSingleton) {
            StatefulResolver.addToSingletonMap(this.m_singletons, br);
        }
        if (!isSingleton || br.getWiring() != null) {
            if (Util.isFragment(br)) {
                this.m_fragments.add(br);
            }
            this.indexCapabilities(br);
        }
    }

    synchronized void removeRevision(BundleRevision br) {
        if (this.m_revisions.remove(br)) {
            this.m_fragments.remove(br);
            this.deindexCapabilities(br);
            List<BundleRevision> revisions = this.m_singletons.get(br.getSymbolicName());
            if (revisions != null) {
                revisions.remove(br);
                if (revisions.isEmpty()) {
                    this.m_singletons.remove(br.getSymbolicName());
                }
            }
        }
    }

    boolean isEffective(Requirement req) {
        String effective = req.getDirectives().get("effective");
        return effective == null || effective.equals("resolve");
    }

    synchronized List<BundleCapability> findProviders(BundleRequirement req, boolean obeyMandatory) {
        ResolverHookRecord record = new ResolverHookRecord(Collections.emptyMap(), null);
        return this.findProvidersInternal(record, req, obeyMandatory, true);
    }

    synchronized List<BundleCapability> findProvidersInternal(ResolverHookRecord record, Requirement req, boolean obeyMandatory, boolean invokeHooksAndSecurity) {
        ArrayList<BundleCapability> result = new ArrayList<BundleCapability>();
        CapabilitySet capSet = this.m_capSets.get(req.getNamespace());
        if (capSet != null) {
            String filter;
            SimpleFilter sf = req instanceof BundleRequirementImpl ? ((BundleRequirementImpl)req).getFilter() : ((filter = req.getDirectives().get("filter")) == null ? new SimpleFilter(null, null, 0) : SimpleFilter.parse(filter));
            Set<Capability> matches = capSet.match(sf, obeyMandatory);
            for (Capability cap : matches) {
                if (!(cap instanceof BundleCapability)) continue;
                BundleCapability bcap = (BundleCapability)cap;
                if (invokeHooksAndSecurity && this.filteredBySecurity((BundleRequirement)req, bcap) || req.getNamespace().equals("osgi.wiring.host") && bcap.getRevision().getWiring() != null) continue;
                result.add(bcap);
            }
        }
        if (invokeHooksAndSecurity && !result.isEmpty() && !record.getResolverHookRefs().isEmpty()) {
            if (record.getBundleRevisionWhitelist() != null) {
                Iterator it = result.iterator();
                while (it.hasNext()) {
                    if (record.getBundleRevisionWhitelist().contains(((BundleCapability)it.next()).getRevision())) continue;
                    it.remove();
                }
            }
            ShrinkableCollection<BundleCapability> shrinkable = new ShrinkableCollection<BundleCapability>(result);
            for (ResolverHook hook : record.getResolverHooks()) {
                try {
                    Felix.m_secureAction.invokeResolverHookMatches(hook, (BundleRequirement)req, shrinkable);
                }
                catch (Throwable th) {
                    this.m_logger.log(2, "Resolver hook exception.", th);
                }
            }
        }
        Collections.sort(result, new CandidateComparator());
        return result;
    }

    private boolean filteredBySecurity(BundleRequirement req, BundleCapability cap) {
        if (System.getSecurityManager() != null) {
            BundleRevisionImpl reqRevision = (BundleRevisionImpl)req.getRevision();
            if (req.getNamespace().equals("osgi.wiring.package") ? (!((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(new PackagePermission((String)cap.getAttributes().get("osgi.wiring.package"), "exportonly")) || reqRevision != null && !((BundleProtectionDomain)reqRevision.getProtectionDomain()).impliesDirect(new PackagePermission((String)cap.getAttributes().get("osgi.wiring.package"), cap.getRevision().getBundle(), "import"))) && reqRevision != cap.getRevision() : (req.getNamespace().equals("osgi.wiring.bundle") ? !((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(new BundlePermission(cap.getRevision().getSymbolicName(), "provide")) || reqRevision != null && !((BundleProtectionDomain)reqRevision.getProtectionDomain()).impliesDirect(new BundlePermission(cap.getRevision().getSymbolicName(), "require")) : (req.getNamespace().equals("osgi.wiring.host") ? !((BundleProtectionDomain)reqRevision.getProtectionDomain()).impliesDirect(new BundlePermission(cap.getRevision().getSymbolicName(), "fragment")) || !((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(new BundlePermission(cap.getRevision().getSymbolicName(), "host")) : !req.getNamespace().equals("osgi.ee") && (!((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(new CapabilityPermission(req.getNamespace(), "provide")) || reqRevision != null && !((BundleProtectionDomain)reqRevision.getProtectionDomain()).impliesDirect(new CapabilityPermission(req.getNamespace(), cap.getAttributes(), cap.getRevision().getBundle(), "require")))))) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void resolve(Set<BundleRevision> mandatory, Set<BundleRevision> optional) throws ResolutionException, BundleException {
        boolean locked = this.m_felix.acquireGlobalLock();
        if (!locked) {
            throw new ResolveException("Unable to acquire global lock for resolve.", null, null);
        }
        if (this.m_isResolving) {
            this.m_felix.releaseGlobalLock();
            throw new IllegalStateException("Nested resolve operations not allowed.");
        }
        this.m_isResolving = true;
        Map<Resource, List<Wire>> wireMap = null;
        try {
            BundleImpl bundle;
            BundleRevision br;
            mandatory = mandatory.isEmpty() ? mandatory : new HashSet<BundleRevision>(mandatory);
            optional = optional.isEmpty() ? optional : new HashSet<BundleRevision>(optional);
            ResolverHookRecord record = this.prepareResolverHooks(mandatory, optional);
            this.selectSingletons(record);
            Iterator it = mandatory.iterator();
            while (it.hasNext()) {
                br = (BundleRevision)it.next();
                bundle = (BundleImpl)br.getBundle();
                if (bundle.isExtension()) {
                    it.remove();
                    continue;
                }
                if (!Util.isSingleton(br) || this.isSelectedSingleton(br)) continue;
                throw new ResolveException("Singleton conflict.", br, null);
            }
            it = optional.iterator();
            while (it.hasNext()) {
                br = (BundleRevision)it.next();
                bundle = (BundleImpl)br.getBundle();
                if (bundle.isExtension()) {
                    it.remove();
                    continue;
                }
                if (!Util.isSingleton(br) || this.isSelectedSingleton(br)) continue;
                it.remove();
            }
            ResolutionException rethrow = null;
            try {
                wireMap = this.m_resolver.resolve(new ResolveContextImpl(this, this.getWirings(), record, mandatory, optional, this.getFragments()));
            }
            catch (ResolutionException ex) {
                rethrow = ex;
            }
            this.releaseResolverHooks(record);
            if (rethrow != null) {
                throw rethrow;
            }
            this.markResolvedRevisions(wireMap);
        }
        finally {
            this.m_isResolving = false;
            this.m_felix.releaseGlobalLock();
        }
        this.fireResolvedEvents(wireMap);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    BundleRevision resolve(BundleRevision revision, String pkgName) throws ResolutionException, BundleException {
        BundleRevision provider = null;
        if (revision.getWiring() != null && this.isAllowedDynamicImport(revision, pkgName)) {
            Map wireMap;
            block14: {
                boolean locked = this.m_felix.acquireGlobalLock();
                if (!locked) {
                    throw new ResolveException("Unable to acquire global lock for resolve.", revision, null);
                }
                if (this.m_isResolving) {
                    this.m_felix.releaseGlobalLock();
                    throw new IllegalStateException("Nested resolve operations not allowed.");
                }
                this.m_isResolving = true;
                wireMap = null;
                try {
                    provider = ((BundleWiringImpl)revision.getWiring()).getImportedPackageSource(pkgName);
                    if (provider != null) break block14;
                    ResolverHookRecord record = this.prepareResolverHooks(Collections.singleton(revision), Collections.EMPTY_SET);
                    this.selectSingletons(record);
                    ResolutionException rethrow = null;
                    try {
                        List<BundleRequirement> dynamics = Util.getDynamicRequirements(revision.getWiring().getRequirements(null));
                        Map<String, Object> attrs = Collections.singletonMap("osgi.wiring.package", pkgName);
                        BundleRequirementImpl req = new BundleRequirementImpl(revision, "osgi.wiring.package", Collections.EMPTY_MAP, attrs);
                        final List<BundleCapability> candidates = this.findProvidersInternal(record, req, false, true);
                        final BundleRequirementImpl dynReq = this.findDynamicRequirement(dynamics, candidates);
                        if (dynReq != null) {
                            Iterator<BundleCapability> itCand = candidates.iterator();
                            while (itCand.hasNext()) {
                                Capability cap = itCand.next();
                                if (CapabilitySet.matches(cap, dynReq.getFilter())) continue;
                                itCand.remove();
                            }
                        } else {
                            candidates.clear();
                        }
                        Map<Resource, Wiring> wirings = this.getWirings();
                        wireMap = dynReq != null && wirings.containsKey(revision) ? this.m_resolver.resolveDynamic(new ResolveContextImpl(this, wirings, record, Collections.emptyList(), Collections.emptyList(), this.getFragments()){

                            @Override
                            public List<Capability> findProviders(Requirement br) {
                                return br == dynReq ? candidates : super.findProviders(br);
                            }
                        }, revision.getWiring(), dynReq) : Collections.emptyMap();
                    }
                    catch (ResolutionException ex) {
                        rethrow = ex;
                    }
                    this.releaseResolverHooks(record);
                    if (rethrow != null) {
                        throw rethrow;
                    }
                    if (wireMap != null && wireMap.containsKey(revision)) {
                        List dynamicWires = (List)wireMap.remove(revision);
                        Wire dynamicWire = (Wire)dynamicWires.get(0);
                        this.markResolvedRevisions(wireMap);
                        if (dynamicWire != null && dynamicWire.getRequirer() instanceof BundleRevision && dynamicWire.getRequirement() instanceof BundleRequirement && dynamicWire.getProvider() instanceof BundleRevision && dynamicWire.getCapability() instanceof BundleCapability) {
                            BundleRevision dwRequirer = (BundleRevision)dynamicWire.getRequirer();
                            BundleRequirement dwRequirement = (BundleRequirement)dynamicWire.getRequirement();
                            BundleRevision dwProvider = (BundleRevision)dynamicWire.getProvider();
                            BundleCapability dwCapability = (BundleCapability)dynamicWire.getCapability();
                            BundleWireImpl bw = new BundleWireImpl(dwRequirer, dwRequirement, dwProvider, dwCapability);
                            this.m_felix.getDependencies().addDependent(bw);
                            ((BundleWiringImpl)revision.getWiring()).addDynamicWire(bw);
                            this.m_felix.getLogger().log(4, "DYNAMIC WIRE: " + dynamicWire);
                            provider = ((BundleWiringImpl)revision.getWiring()).getImportedPackageSource(pkgName);
                        }
                    }
                }
                finally {
                    this.m_isResolving = false;
                    this.m_felix.releaseGlobalLock();
                }
            }
            this.fireResolvedEvents(wireMap);
        }
        return provider;
    }

    private BundleRequirementImpl findDynamicRequirement(List<BundleRequirement> dynamics, List<BundleCapability> candidates) {
        for (int dynIdx = 0; candidates.size() > 0 && dynIdx < dynamics.size(); ++dynIdx) {
            for (Capability capability : candidates) {
                if (!CapabilitySet.matches(capability, ((BundleRequirementImpl)dynamics.get(dynIdx)).getFilter())) continue;
                return (BundleRequirementImpl)dynamics.get(dynIdx);
            }
        }
        return null;
    }

    private ResolverHookRecord prepareResolverHooks(Set<BundleRevision> mandatory, Set<BundleRevision> optional) throws BundleException, ResolutionException {
        ShrinkableCollection<BundleRevision> whitelist;
        LinkedHashMap<ServiceReference<ResolverHookFactory>, ResolverHook> hookMap = new LinkedHashMap<ServiceReference<ResolverHookFactory>, ResolverHook>();
        Set<ServiceReference<ResolverHookFactory>> hookRefs = this.m_felix.getHookRegistry().getHooks(ResolverHookFactory.class);
        if (!hookRefs.isEmpty()) {
            Set<BundleRevision> triggers;
            if (!mandatory.isEmpty() && !optional.isEmpty()) {
                triggers = new HashSet<BundleRevision>(mandatory);
                triggers.addAll(optional);
            } else {
                triggers = mandatory.isEmpty() ? optional : mandatory;
            }
            triggers = Collections.unmodifiableSet(triggers);
            BundleException rethrow = null;
            for (ServiceReference<ResolverHookFactory> ref : hookRefs) {
                try {
                    ResolverHook hook;
                    ResolverHookFactory rhf = this.m_felix.getService(this.m_felix, ref, false);
                    if (rhf == null || (hook = Felix.m_secureAction.invokeResolverHookFactory(rhf, triggers)) == null) continue;
                    hookMap.put(ref, hook);
                }
                catch (Throwable ex) {
                    rethrow = new BundleException("Resolver hook exception: " + ex.getMessage(), 12, ex);
                    break;
                }
            }
            if (rethrow != null) {
                for (ResolverHook hook : hookMap.values()) {
                    try {
                        Felix.m_secureAction.invokeResolverHookEnd(hook);
                    }
                    catch (Exception ex) {
                        rethrow = new BundleException("Resolver hook exception: " + ex.getMessage(), 12, ex);
                    }
                }
                throw rethrow;
            }
            whitelist = new ShrinkableCollection<BundleRevision>(this.getUnresolvedRevisions());
            int originalSize = whitelist.size();
            for (ResolverHook hook : hookMap.values()) {
                try {
                    Felix.m_secureAction.invokeResolverHookResolvable(hook, whitelist);
                }
                catch (Throwable ex) {
                    rethrow = new BundleException("Resolver hook exception: " + ex.getMessage(), 12, ex);
                    break;
                }
            }
            if (rethrow != null) {
                for (ResolverHook hook : hookMap.values()) {
                    try {
                        Felix.m_secureAction.invokeResolverHookEnd(hook);
                    }
                    catch (Exception ex) {
                        rethrow = new BundleException("Resolver hook exception: " + ex.getMessage(), 12, ex);
                    }
                }
                throw rethrow;
            }
            if (whitelist.size() == originalSize) {
                whitelist = null;
            }
            if (whitelist != null && (mandatory.isEmpty() || !optional.isEmpty() || mandatory.iterator().next().getWiring() == null)) {
                mandatory.retainAll(whitelist);
                optional.retainAll(whitelist);
                if (mandatory.isEmpty() && optional.isEmpty()) {
                    throw new ResolveException("Resolver hook prevented resolution.", null, null);
                }
            }
        } else {
            whitelist = null;
        }
        return new ResolverHookRecord(hookMap, whitelist);
    }

    private void releaseResolverHooks(ResolverHookRecord record) throws BundleException {
        if (!record.getResolverHookRefs().isEmpty()) {
            for (ResolverHook hook : record.getResolverHooks()) {
                try {
                    Felix.m_secureAction.invokeResolverHookEnd(hook);
                }
                catch (Throwable th) {
                    this.m_logger.log(2, "Resolver hook exception.", th);
                }
            }
            boolean invalid = false;
            for (ServiceReference<ResolverHookFactory> ref : record.getResolverHookRefs()) {
                if (ref.getBundle() == null) {
                    invalid = true;
                }
                this.m_felix.ungetService(this.m_felix, ref, null);
            }
            if (invalid) {
                throw new BundleException("Resolver hook service unregistered during resolve.", 12);
            }
        }
    }

    boolean isAllowedDynamicImport(BundleRevision revision, String pkgName) {
        if (revision.getWiring() == null || pkgName.length() == 0) {
            return false;
        }
        List<BundleRequirement> dynamics = Util.getDynamicRequirements(revision.getWiring().getRequirements(null));
        if (dynamics == null || dynamics.isEmpty()) {
            return false;
        }
        for (BundleCapability cap : revision.getWiring().getCapabilities(null)) {
            if (!cap.getNamespace().equals("osgi.wiring.package") || !cap.getAttributes().get("osgi.wiring.package").equals(pkgName)) continue;
            return false;
        }
        if (((BundleWiringImpl)revision.getWiring()).hasPackageSource(pkgName)) {
            return false;
        }
        Map<String, Object> attrs = Collections.singletonMap("osgi.wiring.package", pkgName);
        BundleRequirementImpl req = new BundleRequirementImpl(revision, "osgi.wiring.package", Collections.EMPTY_MAP, attrs);
        List<BundleCapability> candidates = this.findProviders(req, false);
        BundleRequirementImpl dynReq = null;
        for (int dynIdx = 0; candidates.size() > 0 && dynReq == null && dynIdx < dynamics.size(); ++dynIdx) {
            Iterator<BundleCapability> itCand = candidates.iterator();
            while (dynReq == null && itCand.hasNext()) {
                Capability cap = itCand.next();
                if (!CapabilitySet.matches(cap, ((BundleRequirementImpl)dynamics.get(dynIdx)).getFilter())) continue;
                dynReq = (BundleRequirementImpl)dynamics.get(dynIdx);
            }
        }
        if (dynReq != null) {
            Iterator<BundleCapability> itCand = candidates.iterator();
            while (itCand.hasNext()) {
                Capability cap = itCand.next();
                if (CapabilitySet.matches(cap, dynReq.getFilter())) continue;
                itCand.remove();
            }
        } else {
            candidates.clear();
        }
        return !candidates.isEmpty();
    }

    private void markResolvedRevisions(Map<Resource, List<Wire>> wireMap) throws ResolveException {
        boolean debugLog;
        boolean bl = debugLog = this.m_felix.getLogger().getLogLevel() >= 4;
        if (wireMap != null) {
            HashMap hosts = new HashMap();
            for (Map.Entry<Resource, List<Wire>> entry : wireMap.entrySet()) {
                Resource resource = entry.getKey();
                List<Wire> wires = entry.getValue();
                if (!Util.isFragment(resource)) continue;
                for (Wire w : wires) {
                    ArrayList<BundleRevision> fragments = (ArrayList<BundleRevision>)hosts.get(w.getProvider());
                    if (fragments == null) {
                        fragments = new ArrayList<BundleRevision>();
                        hosts.put(w.getProvider(), fragments);
                    }
                    if (!(w.getRequirer() instanceof BundleRevision)) continue;
                    fragments.add((BundleRevision)w.getRequirer());
                }
            }
            HashMap<BundleRevision, BundleWiringImpl> wirings = new HashMap<BundleRevision, BundleWiringImpl>(wireMap.size());
            for (Map.Entry<Resource, List<Wire>> entry : wireMap.entrySet()) {
                Resource resource = entry.getKey();
                if (!(resource instanceof BundleRevision)) continue;
                BundleRevision revision = (BundleRevision)resource;
                List<Wire> resolverWires = entry.getValue();
                ArrayList<BundleWire> bundleWires = new ArrayList<BundleWire>(resolverWires.size());
                if (revision.getWiring() != null && Util.isFragment(revision)) {
                    bundleWires.addAll(revision.getWiring().getRequiredWires(null));
                }
                HashMap<String, BundleRevision> importedPkgs = new HashMap<String, BundleRevision>();
                HashMap<String, List<BundleRevision>> requiredPkgs = new HashMap<String, List<BundleRevision>>();
                for (Wire rw : resolverWires) {
                    if (!(rw.getRequirer() instanceof BundleRevision)) continue;
                    BundleRevision requirer = (BundleRevision)rw.getRequirer();
                    if (!(rw.getRequirement() instanceof BundleRequirement)) continue;
                    BundleRequirement bundleRequirement = (BundleRequirement)rw.getRequirement();
                    if (!(rw.getProvider() instanceof BundleRevision)) continue;
                    BundleRevision provider = (BundleRevision)rw.getProvider();
                    if (!(rw.getCapability() instanceof BundleCapability)) continue;
                    BundleCapability capability = (BundleCapability)rw.getCapability();
                    BundleWireImpl bw = new BundleWireImpl(requirer, bundleRequirement, provider, capability);
                    bundleWires.add(bw);
                    if (Util.isFragment(revision)) {
                        if (!debugLog) continue;
                        this.m_felix.getLogger().log(4, "FRAGMENT WIRE: " + rw.toString());
                        continue;
                    }
                    if (debugLog) {
                        this.m_felix.getLogger().log(4, "WIRE: " + rw.toString());
                    }
                    if (capability.getNamespace().equals("osgi.wiring.package")) {
                        importedPkgs.put((String)capability.getAttributes().get("osgi.wiring.package"), provider);
                        continue;
                    }
                    if (!capability.getNamespace().equals("osgi.wiring.bundle")) continue;
                    Set<String> pkgs = StatefulResolver.calculateExportedAndReexportedPackages(provider, wireMap, new HashSet<String>(), new HashSet<BundleRevision>());
                    for (String pkg : pkgs) {
                        ArrayList<BundleRevision> revs = (ArrayList<BundleRevision>)requiredPkgs.get(pkg);
                        if (revs == null) {
                            revs = new ArrayList<BundleRevision>();
                            requiredPkgs.put(pkg, revs);
                        }
                        revs.add(provider);
                    }
                }
                List fragments = (List)hosts.get(revision);
                try {
                    wirings.put(revision, new BundleWiringImpl(this.m_felix.getLogger(), this.m_felix.getConfig(), this, (BundleRevisionImpl)revision, fragments, bundleWires, importedPkgs, requiredPkgs));
                }
                catch (Exception ex) {
                    for (Map.Entry entry2 : wirings.entrySet()) {
                        try {
                            ((BundleWiringImpl)entry2.getValue()).dispose();
                        }
                        catch (Exception ex2) {
                            RuntimeException rte = new RuntimeException("Unable to clean up resolver failure.", ex2);
                            this.m_felix.getLogger().log(1, rte.getMessage(), ex2);
                            throw rte;
                        }
                    }
                    ResolveException re = new ResolveException("Unable to resolve " + revision, revision, null);
                    re.initCause(ex);
                    this.m_felix.getLogger().log(1, re.getMessage(), ex);
                    throw re;
                }
            }
            for (Map.Entry<Resource, List<Wire>> entry : wirings.entrySet()) {
                BundleRevisionImpl revision = (BundleRevisionImpl)entry.getKey();
                BundleWiring wiring = (BundleWiring)((Object)entry.getValue());
                revision.resolve((BundleWiringImpl)((Object)entry.getValue()));
                for (BundleWire bw : wiring.getRequiredWires(null)) {
                    this.m_felix.getDependencies().addDependent(bw);
                }
                this.addRevision(revision);
                this.markBundleResolved(revision);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void markBundleResolved(BundleRevision revision) {
        BundleImpl bundle = (BundleImpl)revision.getBundle();
        try {
            try {
                this.m_felix.acquireBundleLock(bundle, 38);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            if (bundle.adapt(BundleRevision.class) == revision) {
                if (bundle.getState() != 2) {
                    this.m_felix.getLogger().log(bundle, 2, "Received a resolve event for a bundle that has already been resolved.");
                } else {
                    this.m_felix.setBundleStateAndNotify(bundle, 4);
                }
            }
        }
        finally {
            this.m_felix.releaseBundleLock(bundle);
        }
    }

    private void fireResolvedEvents(Map<Resource, List<Wire>> wireMap) {
        if (wireMap != null) {
            for (Map.Entry<Resource, List<Wire>> entry : wireMap.entrySet()) {
                Resource resource = entry.getKey();
                if (!(resource instanceof BundleRevision)) continue;
                BundleRevision revision = (BundleRevision)resource;
                List<BundleRevision> fragments = Util.getFragments(revision.getWiring());
                for (int i = 0; i < fragments.size(); ++i) {
                    this.m_felix.fireBundleEvent(32, fragments.get(i).getBundle());
                }
                this.m_felix.fireBundleEvent(32, revision.getBundle());
            }
        }
    }

    private static Set<String> calculateExportedAndReexportedPackages(BundleRevision br, Map<Resource, List<Wire>> wireMap, Set<String> pkgs, Set<BundleRevision> cycles) {
        block5: {
            if (cycles.contains(br)) break block5;
            cycles.add(br);
            for (BundleCapability cap : br.getDeclaredCapabilities(null)) {
                if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                pkgs.add((String)cap.getAttributes().get("osgi.wiring.package"));
            }
            if (br.getWiring() == null) {
                for (Wire rw : wireMap.get(br)) {
                    String dir;
                    if (!rw.getCapability().getNamespace().equals("osgi.wiring.bundle") || (dir = rw.getRequirement().getDirectives().get("visibility")) == null || !dir.equals("reexport")) continue;
                    StatefulResolver.calculateExportedAndReexportedPackages((BundleRevision)rw.getProvider(), wireMap, pkgs, cycles);
                }
            } else {
                for (BundleWire bw : br.getWiring().getRequiredWires(null)) {
                    String dir;
                    if (!bw.getCapability().getNamespace().equals("osgi.wiring.bundle") || (dir = bw.getRequirement().getDirectives().get("visibility")) == null || !dir.equals("reexport")) continue;
                    StatefulResolver.calculateExportedAndReexportedPackages(bw.getProviderWiring().getRevision(), wireMap, pkgs, cycles);
                }
            }
        }
        return pkgs;
    }

    private synchronized void indexCapabilities(BundleRevision br) {
        List<BundleCapability> caps;
        List<BundleCapability> list = caps = Util.isFragment(br) || br.getWiring() == null ? br.getDeclaredCapabilities(null) : br.getWiring().getCapabilities(null);
        if (caps != null) {
            for (BundleCapability cap : caps) {
                if (cap.getRevision() != br) continue;
                CapabilitySet capSet = this.m_capSets.get(cap.getNamespace());
                if (capSet == null) {
                    capSet = new CapabilitySet(null, true);
                    this.m_capSets.put(cap.getNamespace(), capSet);
                }
                capSet.addCapability(cap);
            }
        }
    }

    private synchronized void deindexCapabilities(BundleRevision br) {
        List<BundleCapability> caps = br.getDeclaredCapabilities(null);
        if (caps != null) {
            for (BundleCapability cap : caps) {
                CapabilitySet capSet = this.m_capSets.get(cap.getNamespace());
                if (capSet == null) continue;
                capSet.removeCapability(cap);
            }
        }
    }

    private synchronized boolean isSelectedSingleton(BundleRevision br) {
        return this.m_selectedSingletons.contains(br);
    }

    private synchronized void selectSingletons(ResolverHookRecord record) throws BundleException {
        this.m_selectedSingletons.clear();
        for (Map.Entry<String, List<BundleRevision>> entry : this.m_singletons.entrySet()) {
            for (BundleRevision singleton : entry.getValue()) {
                if (singleton.getWiring() != null) continue;
                this.deindexCapabilities(singleton);
                this.m_fragments.remove(singleton);
            }
        }
        if (record.getResolverHookRefs().isEmpty()) {
            this.selectDefaultSingletons(record);
        } else {
            this.selectSingletonsUsingHooks(record);
        }
    }

    private void selectDefaultSingletons(ResolverHookRecord record) {
        for (Map.Entry<String, List<BundleRevision>> entry : this.m_singletons.entrySet()) {
            this.selectSingleton(record, entry.getValue());
        }
    }

    private void selectSingletonsUsingHooks(ResolverHookRecord record) throws BundleException {
        HashMap<BundleCapability, Collection<BundleCapability>> allCollisions = new HashMap<BundleCapability, Collection<BundleCapability>>();
        for (Map.Entry<String, List<BundleRevision>> entry : this.m_singletons.entrySet()) {
            ArrayList<BundleCapability> arrayList = new ArrayList<BundleCapability>();
            for (BundleRevision br : entry.getValue()) {
                List<BundleCapability> caps = br.getDeclaredCapabilities("osgi.wiring.bundle");
                if (caps.isEmpty()) continue;
                arrayList.add(caps.get(0));
            }
            for (BundleCapability bc : arrayList) {
                ShrinkableCollection capCopy = new ShrinkableCollection(new ArrayList(arrayList));
                capCopy.remove(bc);
                allCollisions.put(bc, capCopy);
            }
        }
        for (ResolverHook hook : record.getResolverHooks()) {
            for (Map.Entry entry : allCollisions.entrySet()) {
                try {
                    Felix.m_secureAction.invokeResolverHookSingleton(hook, (BundleCapability)entry.getKey(), (Collection)entry.getValue());
                }
                catch (Throwable ex) {
                    throw new BundleException("Resolver hook exception: " + ex.getMessage(), 12, ex);
                }
            }
        }
        ArrayList<List<BundleRevision>> groups = new ArrayList<List<BundleRevision>>();
        while (!allCollisions.isEmpty()) {
            BundleCapability target = (BundleCapability)allCollisions.entrySet().iterator().next().getKey();
            groups.add(this.groupSingletons(allCollisions, target, new ArrayList<BundleRevision>()));
        }
        for (List list : groups) {
            this.selectSingleton(record, list);
        }
    }

    private List<BundleRevision> groupSingletons(Map<BundleCapability, Collection<BundleCapability>> allCollisions, BundleCapability target, List<BundleRevision> group) {
        if (!group.contains(target.getRevision())) {
            boolean repeat;
            group.add(target.getRevision());
            Collection<BundleCapability> collisions = allCollisions.remove(target);
            for (BundleCapability collision : collisions) {
                this.groupSingletons(allCollisions, collision, group);
            }
            block1: do {
                repeat = false;
                for (Map.Entry<BundleCapability, Collection<BundleCapability>> entry : allCollisions.entrySet()) {
                    if (!entry.getValue().contains(target)) continue;
                    repeat = true;
                    this.groupSingletons(allCollisions, entry.getKey(), group);
                    continue block1;
                }
            } while (repeat);
        }
        return group;
    }

    private void selectSingleton(ResolverHookRecord record, List<BundleRevision> singletons) {
        BundleRevision selected = null;
        for (BundleRevision singleton : singletons) {
            if (singleton.getWiring() != null) {
                selected = null;
                break;
            }
            if (record.getBundleRevisionWhitelist() != null && !record.getBundleRevisionWhitelist().contains(singleton) || selected != null && selected.getVersion().compareTo(singleton.getVersion()) <= 0) continue;
            selected = singleton;
        }
        if (selected != null) {
            this.m_selectedSingletons.add(selected);
            this.indexCapabilities(selected);
            if (Util.isFragment(selected)) {
                this.m_fragments.add(selected);
            }
        }
    }

    private synchronized Set<BundleRevision> getFragments() {
        HashSet<BundleRevision> fragments = new HashSet<BundleRevision>(this.m_fragments);
        Iterator it = fragments.iterator();
        while (it.hasNext()) {
            BundleRevision currentFragmentRevision;
            BundleRevision fragment = (BundleRevision)it.next();
            if (fragment == (currentFragmentRevision = fragment.getBundle().adapt(BundleRevision.class))) continue;
            it.remove();
        }
        return fragments;
    }

    private synchronized Set<BundleRevision> getUnresolvedRevisions() {
        HashSet<BundleRevision> unresolved = new HashSet<BundleRevision>();
        for (BundleRevision revision : this.m_revisions) {
            if (revision.getWiring() != null) continue;
            unresolved.add(revision);
        }
        return unresolved;
    }

    private synchronized Map<Resource, Wiring> getWirings() {
        HashMap<Resource, Wiring> wirings = new HashMap<Resource, Wiring>();
        for (BundleRevision revision : this.m_revisions) {
            if (revision.getWiring() == null) continue;
            wirings.put(revision, revision.getWiring());
        }
        return wirings;
    }

    private static void addToSingletonMap(Map<String, List<BundleRevision>> singletons, BundleRevision br) {
        List<BundleRevision> revisions = singletons.get(br.getSymbolicName());
        if (revisions == null) {
            revisions = new ArrayList<BundleRevision>();
        }
        revisions.add(br);
        singletons.put(br.getSymbolicName(), revisions);
    }

    static class ResolverHookRecord {
        final Map<ServiceReference<ResolverHookFactory>, ResolverHook> m_resolveHookMap;
        final Collection<BundleRevision> m_brWhitelist;

        ResolverHookRecord(Map<ServiceReference<ResolverHookFactory>, ResolverHook> resolveHookMap, Collection<BundleRevision> brWhiteList) {
            this.m_resolveHookMap = resolveHookMap;
            this.m_brWhitelist = brWhiteList;
        }

        Collection<BundleRevision> getBundleRevisionWhitelist() {
            return this.m_brWhitelist;
        }

        Set<ServiceReference<ResolverHookFactory>> getResolverHookRefs() {
            return this.m_resolveHookMap.keySet();
        }

        Iterable<ResolverHook> getResolverHooks() {
            return new Iterable<ResolverHook>(){

                @Override
                public Iterator<ResolverHook> iterator() {
                    return new Iterator<ResolverHook>(){
                        private final Iterator<Map.Entry<ServiceReference<ResolverHookFactory>, ResolverHook>> it;
                        private Map.Entry<ServiceReference<ResolverHookFactory>, ResolverHook> next;
                        {
                            this.it = m_resolveHookMap.entrySet().iterator();
                            this.next = null;
                        }

                        @Override
                        public boolean hasNext() {
                            if (this.next == null) {
                                this.findNext();
                            }
                            return this.next != null;
                        }

                        @Override
                        public ResolverHook next() {
                            if (this.next == null) {
                                this.findNext();
                            }
                            if (this.next == null) {
                                throw new NoSuchElementException();
                            }
                            ResolverHook hook = this.next.getValue();
                            this.next = null;
                            return hook;
                        }

                        private void findNext() {
                            while (this.it.hasNext()) {
                                this.next = this.it.next();
                                if (this.next.getKey().getBundle() != null) {
                                    return;
                                }
                                this.next = null;
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }
    }
}

