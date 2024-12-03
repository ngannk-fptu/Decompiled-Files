/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.WriteResource;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CombinedResource
extends WriteResource {
    final List<Resource> resources = new ArrayList<Resource>();
    long lastModified = 0L;

    @Override
    public void write(OutputStream out) throws IOException, Exception {
        FilterOutputStream unclosable = new FilterOutputStream(out){

            @Override
            public void close() {
            }
        };
        for (Resource r : this.resources) {
            r.write(unclosable);
            ((OutputStream)unclosable).flush();
        }
    }

    @Override
    public long lastModified() {
        return this.lastModified;
    }

    public void addResource(Resource r) {
        this.lastModified = Math.max(this.lastModified, r.lastModified());
        this.resources.add(r);
    }
}

