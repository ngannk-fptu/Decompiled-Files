/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.version.Version;
import aQute.lib.collections.Logic;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.RepositoryContent;

class ResourceImpl
implements Resource,
Comparable<Resource>,
RepositoryContent {
    private volatile List<Capability> allCapabilities;
    private volatile Map<String, List<Capability>> capabilityMap;
    private volatile List<Requirement> allRequirements;
    private volatile Map<String, List<Requirement>> requirementMap;
    private transient Map<URI, String> locations;

    ResourceImpl() {
    }

    void setCapabilities(List<Capability> capabilities) {
        HashMap<String, List<Capability>> prepare = new HashMap<String, List<Capability>>();
        for (Capability capability : capabilities) {
            LinkedList<Capability> list = (LinkedList<Capability>)prepare.get(capability.getNamespace());
            if (list == null) {
                list = new LinkedList<Capability>();
                prepare.put(capability.getNamespace(), list);
            }
            list.add(capability);
        }
        for (Map.Entry entry : prepare.entrySet()) {
            entry.setValue(Collections.unmodifiableList(new ArrayList((Collection)entry.getValue())));
        }
        this.allCapabilities = Collections.unmodifiableList(new ArrayList<Capability>(capabilities));
        this.capabilityMap = prepare;
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        List<Capability> caps = namespace != null ? this.capabilityMap.get(namespace) : this.allCapabilities;
        return caps != null ? caps : Collections.emptyList();
    }

    void setRequirements(List<Requirement> requirements) {
        HashMap<String, List<Requirement>> prepare = new HashMap<String, List<Requirement>>();
        for (Requirement requirement : requirements) {
            LinkedList<Requirement> list = (LinkedList<Requirement>)prepare.get(requirement.getNamespace());
            if (list == null) {
                list = new LinkedList<Requirement>();
                prepare.put(requirement.getNamespace(), list);
            }
            list.add(requirement);
        }
        for (Map.Entry entry : prepare.entrySet()) {
            entry.setValue(Collections.unmodifiableList(new ArrayList((Collection)entry.getValue())));
        }
        this.allRequirements = Collections.unmodifiableList(new ArrayList<Requirement>(requirements));
        this.requirementMap = prepare;
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        List<Requirement> reqs = namespace != null ? this.requirementMap.get(namespace) : this.allRequirements;
        return reqs != null ? reqs : Collections.emptyList();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<Capability> identities = this.getCapabilities("osgi.identity");
        if (identities.size() == 1) {
            Capability idCap = identities.get(0);
            Object id = idCap.getAttributes().get("osgi.identity");
            Object version = idCap.getAttributes().get("version");
            builder.append(id).append(" version=").append(version);
        } else {
            builder.append("ResourceImpl [caps=");
            builder.append(this.allCapabilities);
            builder.append(", reqs=");
            builder.append(this.allRequirements);
            builder.append("]");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Resource o) {
        Version theirVersion;
        String theirName;
        ResourceUtils.IdentityCapability me = ResourceUtils.getIdentityCapability(this);
        ResourceUtils.IdentityCapability them = ResourceUtils.getIdentityCapability(o);
        String myName = me.osgi_identity();
        if (myName == (theirName = them.osgi_identity())) {
            return 0;
        }
        if (myName == null) {
            return -1;
        }
        if (theirName == null) {
            return 1;
        }
        int n = myName.compareTo(theirName);
        if (n != 0) {
            return n;
        }
        Version myVersion = me.version();
        if (myVersion == (theirVersion = them.version())) {
            return 0;
        }
        if (myVersion == null) {
            return -1;
        }
        if (theirVersion == null) {
            return 1;
        }
        return myVersion.compareTo(theirVersion);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof Resource)) {
            return false;
        }
        Map<URI, String> thisLocations = this.getContentURIs();
        Map<URI, String> otherLocations = other instanceof ResourceImpl ? ((ResourceImpl)other).getContentURIs() : ResourceUtils.getLocations((Resource)other);
        Collection<URI> overlap = Logic.retain(thisLocations.keySet(), otherLocations.keySet());
        for (URI uri : overlap) {
            String otherSha;
            String thisSha = thisLocations.get(uri);
            if (thisSha == (otherSha = otherLocations.get(uri))) {
                return true;
            }
            if (thisSha == null || otherSha == null || !thisSha.equals(otherSha)) continue;
            return true;
        }
        return false;
    }

    public Map<URI, String> getContentURIs() {
        if (this.locations == null) {
            this.locations = ResourceUtils.getLocations(this);
        }
        return this.locations;
    }

    @Override
    public int hashCode() {
        return this.getContentURIs().hashCode();
    }

    @Override
    public InputStream getContent() {
        try {
            ResourceUtils.ContentCapability c = ResourceUtils.getContentCapability(this);
            URI url = c.url();
            return url.toURL().openStream();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

