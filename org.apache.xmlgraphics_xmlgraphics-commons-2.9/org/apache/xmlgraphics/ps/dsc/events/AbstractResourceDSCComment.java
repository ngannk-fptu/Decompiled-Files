/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSProcSet;
import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public abstract class AbstractResourceDSCComment
extends AbstractDSCComment {
    private PSResource resource;

    public AbstractResourceDSCComment() {
    }

    public AbstractResourceDSCComment(PSResource resource) {
        this.resource = resource;
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
        if ("font".equals(name)) {
            String fontname = (String)iter.next();
            this.resource = new PSResource(name, fontname);
        } else if ("procset".equals(name)) {
            String procname = (String)iter.next();
            String version = (String)iter.next();
            String revision = (String)iter.next();
            this.resource = new PSProcSet(procname, Float.parseFloat(version), Integer.parseInt(revision));
        } else if ("file".equals(name)) {
            String filename = (String)iter.next();
            this.resource = new PSResource(name, filename);
        } else if ("form".equals(name)) {
            String formname = (String)iter.next();
            this.resource = new PSResource(name, formname);
        } else if ("pattern".equals(name)) {
            String patternname = (String)iter.next();
            this.resource = new PSResource(name, patternname);
        } else if ("encoding".equals(name)) {
            String encodingname = (String)iter.next();
            this.resource = new PSResource(name, encodingname);
        } else {
            throw new IllegalArgumentException("Invalid resource type: " + name);
        }
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeDSCComment(this.getName(), this.getResource());
    }
}

