/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.dgmimpl.arrays;

import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayMetaMethod;

public abstract class ArrayPutAtMetaMethod
extends ArrayMetaMethod {
    @Override
    public String getName() {
        return "putAt";
    }

    @Override
    public Class getReturnType() {
        return Void.class;
    }
}

