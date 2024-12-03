/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import org.codehaus.groovy.runtime.wrappers.PojoWrapper;

public class CharWrapper
extends PojoWrapper {
    public CharWrapper(char wrapped) {
        super(Character.valueOf(wrapped), Character.TYPE);
    }
}

