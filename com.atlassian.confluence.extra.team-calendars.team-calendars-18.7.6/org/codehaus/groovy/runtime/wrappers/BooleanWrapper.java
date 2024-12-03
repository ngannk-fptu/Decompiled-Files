/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import org.codehaus.groovy.runtime.wrappers.PojoWrapper;

public class BooleanWrapper
extends PojoWrapper {
    public BooleanWrapper(boolean wrapped) {
        super(wrapped ? Boolean.TRUE : Boolean.FALSE, Boolean.TYPE);
    }
}

