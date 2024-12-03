/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import org.codehaus.groovy.ast.ClassNode;

class TraitHelpersTuple {
    private final ClassNode helper;
    private final ClassNode fieldHelper;

    public TraitHelpersTuple(ClassNode helper, ClassNode fieldHelper) {
        this.helper = helper;
        this.fieldHelper = fieldHelper;
    }

    public ClassNode getHelper() {
        return this.helper;
    }

    public ClassNode getFieldHelper() {
        return this.fieldHelper;
    }
}

