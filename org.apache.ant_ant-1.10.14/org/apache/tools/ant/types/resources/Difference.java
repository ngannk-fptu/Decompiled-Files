/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.BaseResourceCollectionContainer;

public class Difference
extends BaseResourceCollectionContainer {
    @Override
    protected Collection<Resource> getCollection() {
        List<ResourceCollection> rcs = this.getResourceCollections();
        int size = rcs.size();
        if (size < 2) {
            throw new BuildException("The difference of %d resource %s is undefined.", size, size == 1 ? "collection" : "collections");
        }
        HashSet<Resource> hs = new HashSet<Resource>();
        ArrayList<Resource> al = new ArrayList<Resource>();
        for (ResourceCollection rc : rcs) {
            for (Resource r : rc) {
                if (hs.add(r)) {
                    al.add(r);
                    continue;
                }
                al.remove(r);
            }
        }
        return al;
    }
}

