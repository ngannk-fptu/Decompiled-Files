/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

class CloneBase
implements Cloneable {
    protected CloneBase() {
    }

    protected CloneBase clone() {
        try {
            return (CloneBase)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(String.format("Unable to clone class %s which should always support it.", this.getClass().getName()), e);
        }
    }
}

