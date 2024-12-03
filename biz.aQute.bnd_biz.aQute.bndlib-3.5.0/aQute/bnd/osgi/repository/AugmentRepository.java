/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.repository.BaseRepository;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.lib.collections.MultiMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;

public class AugmentRepository
extends BaseRepository {
    private final Repository repository;
    private final Map<Capability, Capability> wrapped = new HashMap<Capability, Capability>();
    private final List<Capability> augmentedCapabilities = new ArrayList<Capability>();
    private final List<Resource> augmentedBundles = new ArrayList<Resource>();

    public AugmentRepository(Parameters augments, Repository repository) throws Exception {
        this.repository = repository;
        this.init(augments);
    }

    @Override
    public Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> requirements) {
        Map<Requirement, Collection<Capability>> fromRepos = this.repository.findProviders(requirements);
        for (Requirement requirement : requirements) {
            ArrayList<Capability> provided = new ArrayList<Capability>();
            boolean replaced = false;
            for (Capability originalCapability : fromRepos.get(requirement)) {
                if (!this.isValid(originalCapability)) continue;
                Capability wrappedCapability = this.wrapped.get(originalCapability);
                if (wrappedCapability != null) {
                    provided.add(wrappedCapability);
                    replaced = true;
                    continue;
                }
                provided.add(originalCapability);
            }
            List<Capability> additional = ResourceUtils.findProviders(requirement, this.augmentedCapabilities);
            if (!(replaced |= provided.addAll(additional))) continue;
            fromRepos.put(requirement, provided);
        }
        return fromRepos;
    }

    public boolean isValid(Capability capability) {
        return true;
    }

    private void init(Parameters augments) throws Exception {
        MultiMap<Requirement, Augment> operations = new MultiMap<Requirement, Augment>();
        for (Map.Entry<String, Attrs> e : augments.entrySet()) {
            String bsn = e.getKey();
            Attrs attrs = e.getValue();
            this.createAugmentOperation(operations, bsn, attrs);
        }
        Map<Requirement, Collection<Capability>> allBundles = this.repository.findProviders(operations.keySet());
        for (Map.Entry e : operations.entrySet()) {
            this.executeAugmentOperations(allBundles, (Requirement)e.getKey(), (List)e.getValue());
        }
    }

    private void createAugmentOperation(MultiMap<Requirement, Augment> operations, String bsn, Attrs attrs) {
        String range = attrs.getVersion();
        Requirement bundleRequirement = CapReqBuilder.createBundleRequirement(bsn, range).buildSyntheticRequirement();
        Augment augment = new Augment();
        augment.additionalCapabilities = new Parameters(attrs.get("capability:"));
        augment.additionalRequirements = new Parameters(attrs.get("requirement:"));
        operations.add(bundleRequirement, augment);
    }

    private void executeAugmentOperations(Map<Requirement, Collection<Capability>> allBundles, Requirement bundleRequirement, List<Augment> augments) throws Exception {
        Collection<Capability> matchedBundleCapabilities = allBundles.get(bundleRequirement);
        Set<Resource> bundles = ResourceUtils.getResources(matchedBundleCapabilities);
        for (Resource bundle : bundles) {
            ResourceBuilder wrappedBundleBuilder = new ResourceBuilder();
            Map<Capability, Capability> originalToWrapper = wrappedBundleBuilder.from(bundle);
            this.wrapped.putAll(originalToWrapper);
            List<Augment> bundleAugments = augments;
            for (Augment augment : bundleAugments) {
                List<Capability> addedCapabilities = this.augment(augment, wrappedBundleBuilder);
                this.augmentedCapabilities.addAll(addedCapabilities);
            }
            Resource wrappedBundle = wrappedBundleBuilder.build();
            this.augmentedBundles.add(wrappedBundle);
        }
    }

    private List<Capability> augment(Augment augment, ResourceBuilder builder) throws Exception {
        builder.addRequireCapabilities(augment.additionalRequirements);
        return builder.addProvideCapabilities(augment.additionalCapabilities);
    }

    static class Augment {
        Parameters additionalRequirements;
        Parameters additionalCapabilities;

        Augment() {
        }
    }
}

