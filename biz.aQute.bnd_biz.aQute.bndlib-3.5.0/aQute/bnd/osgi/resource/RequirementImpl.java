/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.CapReq;
import aQute.bnd.osgi.resource.FilterParser;
import java.util.Map;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class RequirementImpl
extends CapReq
implements Requirement {
    static FilterParser fp = new FilterParser();
    private String msg = null;

    RequirementImpl(String namespace, Resource resource, Map<String, String> directives, Map<String, Object> attributes) {
        super(CapReq.MODE.Requirement, namespace, resource, directives, attributes);
    }

    @Override
    public String toString() {
        String m = this.msg;
        if (m != null) {
            return m;
        }
        try {
            this.msg = fp.parse(this).toString();
            return this.msg;
        }
        catch (Exception e) {
            return e.toString();
        }
    }
}

