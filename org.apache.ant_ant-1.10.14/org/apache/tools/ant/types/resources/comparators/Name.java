/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class Name
extends ResourceComparator {
    @Override
    protected int resourceCompare(Resource foo, Resource bar) {
        return foo.getName().compareTo(bar.getName());
    }
}

