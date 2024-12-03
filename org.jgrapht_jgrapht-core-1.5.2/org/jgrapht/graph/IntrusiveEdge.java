/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;

class IntrusiveEdge
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 3258408452177932855L;
    Object source;
    Object target;

    IntrusiveEdge() {
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}

