/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.SizeLimitCollection;

public class Last
extends SizeLimitCollection {
    @Override
    protected Collection<Resource> getCollection() {
        int count = this.getValidCount();
        ResourceCollection rc = this.getResourceCollection();
        int size = rc.size();
        int skip = Math.max(0, size - count);
        List<Resource> result = rc.stream().skip(skip).collect(Collectors.toList());
        int found = result.size();
        if (found == count || size < count && found == size) {
            return result;
        }
        String msg = String.format("Resource collection %s reports size %d but returns %d elements.", rc, size, found + skip);
        if (found > count) {
            this.log(msg, 1);
            return result.subList(found - count, found);
        }
        throw new BuildException(msg);
    }
}

