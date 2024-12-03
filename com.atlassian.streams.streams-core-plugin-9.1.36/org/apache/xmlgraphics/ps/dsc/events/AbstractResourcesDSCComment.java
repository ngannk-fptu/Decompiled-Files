/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSProcSet;
import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public abstract class AbstractResourcesDSCComment
extends AbstractDSCComment {
    private Set resources;
    static final Set RESOURCE_TYPES = new HashSet();

    public AbstractResourcesDSCComment() {
    }

    public AbstractResourcesDSCComment(Collection resources) {
        this.addResources(resources);
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    private void prepareResourceSet() {
        if (this.resources == null) {
            this.resources = new TreeSet();
        }
    }

    public void addResource(PSResource res) {
        this.prepareResourceSet();
        this.resources.add(res);
    }

    public void addResources(Collection resources) {
        if (resources != null) {
            this.prepareResourceSet();
            this.resources.addAll(resources);
        }
    }

    public Set getResources() {
        return Collections.unmodifiableSet(this.resources);
    }

    @Override
    public void parseValue(String value) {
        List params = this.splitParams(value);
        String currentResourceType = null;
        Iterator iter = params.iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            if (RESOURCE_TYPES.contains(name)) {
                currentResourceType = name;
            }
            if (currentResourceType == null) {
                throw new IllegalArgumentException("<resources> must begin with a resource type. Found: " + name);
            }
            if ("font".equals(currentResourceType)) {
                String fontname = (String)iter.next();
                this.addResource(new PSResource(name, fontname));
                continue;
            }
            if ("form".equals(currentResourceType)) {
                String formname = (String)iter.next();
                this.addResource(new PSResource(name, formname));
                continue;
            }
            if ("procset".equals(currentResourceType)) {
                String procname = (String)iter.next();
                String version = (String)iter.next();
                String revision = (String)iter.next();
                this.addResource(new PSProcSet(procname, Float.parseFloat(version), Integer.parseInt(revision)));
                continue;
            }
            if ("file".equals(currentResourceType)) {
                String filename = (String)iter.next();
                this.addResource(new PSResource(name, filename));
                continue;
            }
            throw new IllegalArgumentException("Invalid resource type: " + currentResourceType);
        }
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        if (this.resources == null || this.resources.size() == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("%%").append(this.getName()).append(": ");
        boolean first = true;
        for (Object resource : this.resources) {
            if (!first) {
                gen.writeln(sb.toString());
                sb.setLength(0);
                sb.append("%%+ ");
            }
            PSResource res = (PSResource)resource;
            sb.append(res.getResourceSpecification());
            first = false;
        }
        gen.writeln(sb.toString());
    }

    static {
        RESOURCE_TYPES.add("font");
        RESOURCE_TYPES.add("procset");
        RESOURCE_TYPES.add("file");
        RESOURCE_TYPES.add("pattern");
        RESOURCE_TYPES.add("form");
        RESOURCE_TYPES.add("encoding");
    }
}

