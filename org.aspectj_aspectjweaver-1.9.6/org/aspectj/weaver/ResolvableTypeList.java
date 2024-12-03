/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class ResolvableTypeList {
    public int length;
    private World world;
    private UnresolvedType[] types;

    public ResolvableTypeList(World world, UnresolvedType[] unresolvedTypes) {
        this.length = unresolvedTypes.length;
        this.types = unresolvedTypes;
        this.world = world;
    }

    public ResolvedType getResolved(int nameIndex) {
        UnresolvedType ut = this.types[nameIndex];
        if (!(ut instanceof ResolvedType)) {
            this.types[nameIndex] = this.world.resolve(ut);
            return (ResolvedType)this.types[nameIndex];
        }
        return (ResolvedType)ut;
    }
}

