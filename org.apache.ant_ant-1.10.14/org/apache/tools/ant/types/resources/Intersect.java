/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.BaseResourceCollectionContainer;

public class Intersect
extends BaseResourceCollectionContainer {
    @Override
    protected Collection<Resource> getCollection() {
        List<ResourceCollection> rcs = this.getResourceCollections();
        int size = rcs.size();
        if (size < 2) {
            throw new BuildException("The intersection of %d resource %s is undefined.", size, size == 1 ? "collection" : "collections");
        }
        Function<ResourceCollection, Set> toSet = c -> c.stream().collect(Collectors.toSet());
        Iterator<ResourceCollection> rc = rcs.iterator();
        LinkedHashSet<Resource> s = new LinkedHashSet<Resource>(toSet.apply(rc.next()));
        rc.forEachRemaining(c -> s.retainAll((Collection)toSet.apply((ResourceCollection)c)));
        return s;
    }
}

