/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;

public interface AppendableResourceCollection
extends ResourceCollection {
    public void add(ResourceCollection var1) throws BuildException;
}

