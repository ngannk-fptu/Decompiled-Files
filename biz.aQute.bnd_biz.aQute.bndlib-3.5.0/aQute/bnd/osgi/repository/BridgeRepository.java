/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.version.Version;
import aQute.libg.glob.Glob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;

public class BridgeRepository {
    private static final Requirement allRq = ResourceUtils.createWildcardRequirement();
    private static final SortedSet<Version> EMPTY_VERSIONS = new TreeSet<Version>();
    private final Repository repository;
    private final Map<String, Map<Version, ResourceInfo>> index = new HashMap<String, Map<Version, ResourceInfo>>();

    public BridgeRepository(Repository repository) throws Exception {
        this.repository = repository;
        this.index();
    }

    private void index() throws Exception {
        Map<Requirement, Collection<Capability>> all = this.repository.findProviders(Collections.singleton(allRq));
        for (Capability capability : all.get(allRq)) {
            Resource r = capability.getResource();
            this.index(r);
        }
    }

    private void index(Resource r) throws Exception {
        ResourceUtils.IdentityCapability bc = ResourceUtils.getIdentityCapability(r);
        String bsn = bc.osgi_identity();
        Version version = bc.version();
        Map<Version, ResourceInfo> map = this.index.get(bsn);
        if (map == null) {
            map = new HashMap<Version, ResourceInfo>();
            this.index.put(bsn, map);
        }
        map.put(version, new ResourceInfo(r));
    }

    public Resource get(String bsn, Version version) throws Exception {
        ResourceInfo resourceInfo = this.getInfo(bsn, version);
        if (resourceInfo == null) {
            return null;
        }
        return resourceInfo.resource;
    }

    public ResourceInfo getInfo(String bsn, Version version) throws Exception {
        Map<Version, ResourceInfo> map = this.index.get(bsn);
        if (map == null) {
            return null;
        }
        return map.get(version);
    }

    public List<String> list(String pattern) throws Exception {
        ArrayList<String> bsns = new ArrayList<String>();
        if (pattern == null || pattern.equals("*") || pattern.equals("")) {
            bsns.addAll(this.index.keySet());
        } else {
            String[] split = pattern.split("\\s+");
            Glob[] globs = new Glob[split.length];
            for (int i = 0; i < split.length; ++i) {
                globs[i] = new Glob(split[i]);
            }
            block1: for (String bsn : this.index.keySet()) {
                for (Glob g : globs) {
                    if (!g.matcher(bsn).find()) continue;
                    bsns.add(bsn);
                    continue block1;
                }
            }
        }
        return bsns;
    }

    public SortedSet<Version> versions(String bsn) throws Exception {
        Map<Version, ResourceInfo> map = this.index.get(bsn);
        if (map == null || map.isEmpty()) {
            return EMPTY_VERSIONS;
        }
        return new TreeSet<Version>(map.keySet());
    }

    public Repository getRepository() {
        return this.repository;
    }

    public static void addInformationCapability(ResourceBuilder rb, String name, String from, Throwable error) {
        try {
            CapabilityBuilder c = new CapabilityBuilder("bnd.info");
            c.addAttribute("name", name);
            if (from != null) {
                c.addAttribute("from", from);
            }
            if (error != null) {
                c.addAttribute("error", error.toString());
            }
            rb.addCapability(c);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String tooltip(Object ... target) throws Exception {
        if (target.length == 2) {
            ResourceInfo ri = this.getInfo((String)target[0], (Version)target[1]);
            return ri.getTooltip();
        }
        return null;
    }

    public String title(Object ... target) throws Exception {
        if (target.length == 2) {
            ResourceInfo ri = this.getInfo((String)target[0], (Version)target[1]);
            return ri.getTitle();
        }
        if (target.length == 1) {
            String bsn = (String)target[0];
            Map<Version, ResourceInfo> map = this.index.get(bsn);
            for (ResourceInfo ri : map.values()) {
                if (!ri.isError()) continue;
                return bsn + " [!]";
            }
            return bsn;
        }
        return null;
    }

    public static class ResourceInfo {
        boolean inited;
        Resource resource;
        boolean error;
        String tooltip;
        private String title;

        public ResourceInfo(Resource resource) {
            this.resource = resource;
        }

        public String getTooltip() {
            this.init();
            return this.tooltip;
        }

        private synchronized void init() {
            if (this.inited) {
                return;
            }
            this.inited = true;
            ResourceUtils.IdentityCapability ic = ResourceUtils.getIdentityCapability(this.resource);
            ResourceUtils.ContentCapability cc = ResourceUtils.getContentCapability(this.resource);
            String bsn = ic.osgi_identity();
            Version version = ic.version();
            InfoCapability info = this.getInfo();
            String sha256 = cc == null ? "<>" : cc.osgi_content();
            String error = null;
            String name = null;
            String from = null;
            if (info != null) {
                error = info.error();
                name = info.name();
                from = info.from();
            }
            if (error != null) {
                this.error = true;
                this.title = version + " [" + error + "]";
            } else {
                this.title = version.toString();
            }
            StringBuilder tsb = new StringBuilder();
            if (this.error) {
                tsb.append("ERROR: ").append(error).append("\n");
            }
            tsb.append(bsn).append("\n");
            if (ic.description(null) != null) {
                tsb.append(ic.description(null));
                tsb.append("\n");
            }
            tsb.append(bsn).append("\n");
            tsb.append("Coordinates: ").append(name).append("\n");
            tsb.append("SHA-256: ").append(sha256).append("\n");
            if (from != null) {
                tsb.append("From: ");
                tsb.append(from);
                tsb.append("\n");
            }
            this.tooltip = tsb.toString();
        }

        public InfoCapability getInfo() {
            List<Capability> capabilities = this.resource.getCapabilities("bnd.info");
            InfoCapability info = capabilities.size() >= 1 ? ResourceUtils.as(capabilities.get(0), InfoCapability.class) : null;
            return info;
        }

        public String getTitle() {
            this.init();
            return this.title;
        }

        public boolean isError() {
            this.init();
            return this.error;
        }
    }

    public static interface InfoCapability
    extends Capability {
        public String error();

        public String name();

        public String from();
    }
}

