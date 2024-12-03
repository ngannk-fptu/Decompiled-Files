/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.repository.BaseRepository;
import aQute.lib.collections.MultiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.repository.Repository;

public class AggregateRepository
extends BaseRepository {
    private final Repository[] repositories;

    public AggregateRepository(Collection<? extends Repository> repositories) {
        this(repositories.toArray(new Repository[0]));
    }

    public AggregateRepository(Repository ... repositories) {
        this.repositories = new Repository[repositories.length];
        System.arraycopy(repositories, 0, this.repositories, 0, repositories.length);
    }

    @Override
    public Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> requirements) {
        MultiMap<Requirement, Capability> result = new MultiMap<Requirement, Capability>();
        for (Repository repository : this.repositories) {
            Map<Requirement, Collection<Capability>> capabilities = repository.findProviders(requirements);
            result.addAll(capabilities);
        }
        return result;
    }

    public Collection<Capability> findProviders(Requirement req) {
        if (req == null) {
            return Collections.emptyList();
        }
        Collection<Capability> capabilities = this.findProviders(Collections.singleton(req)).get(req);
        assert (capabilities != null) : "findProviders must return a map containing the collection";
        return capabilities;
    }
}

