/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.UnresolvedType;

public class FormalBinding
implements IHasPosition {
    private final UnresolvedType type;
    private final String name;
    private final int index;
    private final int start;
    private final int end;
    public static final FormalBinding[] NONE = new FormalBinding[0];

    public FormalBinding(UnresolvedType type, String name, int index, int start, int end) {
        this.type = type;
        this.name = name;
        this.index = index;
        this.start = start;
        this.end = end;
    }

    public FormalBinding(UnresolvedType type, int index) {
        this(type, "unknown", index, 0, 0);
    }

    public FormalBinding(UnresolvedType type, String name, int index) {
        this(type, name, index, 0, 0);
    }

    public String toString() {
        return this.type.toString() + ":" + this.index;
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public UnresolvedType getType() {
        return this.type;
    }

    public static class ImplicitFormalBinding
    extends FormalBinding {
        public ImplicitFormalBinding(UnresolvedType type, String name, int index) {
            super(type, name, index);
        }
    }
}

