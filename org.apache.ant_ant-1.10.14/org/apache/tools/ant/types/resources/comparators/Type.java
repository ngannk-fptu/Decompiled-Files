/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class Type
extends ResourceComparator {
    @Override
    protected int resourceCompare(Resource foo, Resource bar) {
        return Comparator.comparing(Resource::isDirectory).compare(foo, bar);
    }
}

