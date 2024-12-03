/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.service.repository;

import aQute.bnd.service.repository.Phase;
import aQute.bnd.version.Version;
import java.net.URI;
import java.util.Set;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;

@ProviderType
public interface SearchableRepository {
    public Set<ResourceDescriptor> getResources(URI var1, boolean var2) throws Exception;

    public Set<ResourceDescriptor> query(String var1) throws Exception;

    public boolean addResource(ResourceDescriptor var1) throws Exception;

    public Set<ResourceDescriptor> findResources(Requirement var1, boolean var2) throws Exception;

    public URI browse(String var1) throws Exception;

    public static class ResourceDescriptor {
        public byte[] id;
        public byte[] sha256;
        public String description;
        public String bsn;
        public Version version;
        public Phase phase;
        public boolean included;
        public boolean dependency;
        public URI url;
        public String owner;
    }
}

