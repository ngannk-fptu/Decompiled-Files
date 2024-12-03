/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.osgi.resource.TypedAttribute;
import aQute.lib.io.IO;
import aQute.lib.tag.Tag;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;

public class XMLResourceGenerator {
    private Tag repository = new Tag("repository", new Object[0]);
    private Set<Resource> visited = new HashSet<Resource>();
    private int indent = 2;
    private boolean compress = false;

    public XMLResourceGenerator() {
        this.repository.addAttribute("xmlns", "http://www.osgi.org/xmlns/repository/v1.0.0");
    }

    public void save(File location) throws IOException {
        if (location.getName().endsWith(".gz")) {
            this.compress = true;
        }
        IO.mkdirs(location.getParentFile());
        File tmp = IO.createTempFile(location.getParentFile(), "index", ".xml");
        try (OutputStream out = IO.outputStream(tmp);){
            this.save(out);
        }
        IO.rename(tmp, location);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(OutputStream out) throws IOException {
        try {
            if (this.compress) {
                out = new GZIPOutputStream(out);
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                 PrintWriter pw = new PrintWriter(writer);){
                pw.printf("<?xml version='1.0' encoding='UTF-8'?>\n", new Object[0]);
                this.repository.print(this.indent, pw);
            }
        }
        finally {
            out.close();
        }
    }

    public XMLResourceGenerator name(String name) {
        this.repository.addAttribute("name", name);
        this.repository.addAttribute("increment", System.currentTimeMillis());
        return this;
    }

    public XMLResourceGenerator referral(URI reference, int depth) {
        Tag referall = new Tag(this.repository, "referral", new Object[0]);
        referall.addAttribute("url", reference);
        if (depth > 0) {
            referall.addAttribute("depth", depth);
        }
        return this;
    }

    public XMLResourceGenerator repository(Repository repository) throws Exception {
        Requirement wildcard = ResourceUtils.createWildcardRequirement();
        Map<Requirement, Collection<Capability>> findProviders = repository.findProviders(Collections.singleton(wildcard));
        for (Capability capability : findProviders.get(wildcard)) {
            this.resource(capability.getResource());
        }
        return this;
    }

    public XMLResourceGenerator resources(Collection<? extends Resource> resources) throws Exception {
        for (Resource resource : resources) {
            this.resource(resource);
        }
        return this;
    }

    public XMLResourceGenerator resource(Resource resource) throws Exception {
        if (!this.visited.contains(resource)) {
            Tag cr;
            this.visited.add(resource);
            Tag r = new Tag(this.repository, "resource", new Object[0]);
            for (Capability cap : resource.getCapabilities(null)) {
                cr = new Tag(r, "capability", new Object[0]);
                cr.addAttribute("namespace", cap.getNamespace());
                this.directives(cr, cap.getDirectives());
                this.attributes(cr, cap.getAttributes());
            }
            for (Requirement req : resource.getRequirements(null)) {
                cr = new Tag(r, "requirement", new Object[0]);
                cr.addAttribute("namespace", req.getNamespace());
                this.directives(cr, req.getDirectives());
                this.attributes(cr, req.getAttributes());
            }
        }
        return this;
    }

    private void directives(Tag cr, Map<String, String> directives) {
        for (Map.Entry<String, String> e : directives.entrySet()) {
            Tag d = new Tag(cr, "directive", new Object[0]);
            d.addAttribute("name", e.getKey());
            d.addAttribute("value", e.getValue());
        }
    }

    private void attributes(Tag cr, Map<String, Object> atrributes) throws Exception {
        for (Map.Entry<String, Object> e : atrributes.entrySet()) {
            TypedAttribute ta;
            Object value = e.getValue();
            if (value == null || (ta = TypedAttribute.getTypedAttribute(value)) == null) continue;
            Tag d = new Tag(cr, "attribute", new Object[0]);
            d.addAttribute("name", e.getKey());
            d.addAttribute("value", ta.value);
            if (ta.type == null) continue;
            d.addAttribute("type", ta.type);
        }
    }

    public XMLResourceGenerator indent(int n) {
        this.indent = n;
        return this;
    }

    public XMLResourceGenerator compress() {
        this.compress = true;
        return this;
    }
}

