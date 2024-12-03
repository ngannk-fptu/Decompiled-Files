/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.repository.BaseRepository;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.lib.collections.MultiMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ResourcesRepository
extends BaseRepository {
    final Set<Resource> resources = new LinkedHashSet<Resource>();

    public ResourcesRepository(Resource resource) {
        this.add(resource);
    }

    public ResourcesRepository(Collection<? extends Resource> resource) {
        this.addAll(resource);
    }

    public ResourcesRepository() {
    }

    @Override
    public Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> requirements) {
        MultiMap<Requirement, Capability> result = new MultiMap<Requirement, Capability>();
        for (Requirement requirement : requirements) {
            List<Capability> capabilities = this.findProvider(requirement);
            result.put(requirement, (Capability)((Object)capabilities));
        }
        return result;
    }

    public List<Capability> findProvider(Requirement requirement) {
        ArrayList<Capability> result = new ArrayList<Capability>();
        String namespace = requirement.getNamespace();
        for (Resource resource : this.resources) {
            for (Capability capability : resource.getCapabilities(namespace)) {
                if (!ResourceUtils.matches(requirement, capability)) continue;
                result.add(capability);
            }
        }
        return result;
    }

    public void add(Resource resource) {
        this.resources.add(resource);
    }

    public void addAll(Collection<? extends Resource> resources) {
        this.resources.addAll(resources);
    }

    protected void set(Collection<? extends Resource> resources) {
        this.resources.clear();
        this.resources.addAll(resources);
    }

    public List<Resource> getResources() {
        return new ArrayList<Resource>(this.resources);
    }
}

