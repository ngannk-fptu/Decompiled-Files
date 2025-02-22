/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentBeginDocument
extends AbstractDSCComment {
    private PSResource resource;
    private Float version;
    private String type;

    public DSCCommentBeginDocument() {
    }

    public DSCCommentBeginDocument(PSResource resource) {
        this.resource = resource;
        if (resource != null && !"file".equals(resource.getType())) {
            throw new IllegalArgumentException("Resource must be of type 'file'");
        }
    }

    public DSCCommentBeginDocument(PSResource resource, Float version, String type) {
        this(resource);
        this.version = version;
        this.type = type;
    }

    public Float getVersion() {
        return this.version;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return "BeginDocument";
    }

    public PSResource getResource() {
        return this.resource;
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public void parseValue(String value) {
        List params = this.splitParams(value);
        Iterator iter = params.iterator();
        String name = (String)iter.next();
        this.resource = new PSResource("file", name);
        if (iter.hasNext()) {
            this.version = Float.valueOf(iter.next().toString());
            this.type = null;
            if (iter.hasNext()) {
                this.type = (String)iter.next();
            }
        }
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(this.getResource().getName());
        if (this.getVersion() != null) {
            params.add(this.getVersion());
            if (this.getType() != null) {
                params.add(this.getType());
            }
        }
        gen.writeDSCComment(this.getName(), params.toArray());
    }
}

