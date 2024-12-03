/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.dgmimpl.arrays;

import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayMetaMethod;

public abstract class ArrayGetAtMetaMethod
extends ArrayMetaMethod {
    protected ArrayGetAtMetaMethod() {
        this.parameterTypes = INTEGER_CLASS_ARR;
    }

    @Override
    public String getName() {
        return "getAt";
    }
}

