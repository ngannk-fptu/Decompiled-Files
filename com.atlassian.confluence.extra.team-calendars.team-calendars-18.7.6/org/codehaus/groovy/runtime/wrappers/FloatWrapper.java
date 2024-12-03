/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import org.codehaus.groovy.runtime.wrappers.PojoWrapper;

public class FloatWrapper
extends PojoWrapper {
    public FloatWrapper(float wrapped) {
        super(Float.valueOf(wrapped), Float.TYPE);
    }
}

