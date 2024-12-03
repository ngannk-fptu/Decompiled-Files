/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.ImmutableSortedSet$Builder
 */
package com.google.template.soy.types.aggregate;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSortedSet;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public final class UnionType
implements SoyType {
    private static final Comparator<SoyType> MEMBER_ORDER = new Comparator<SoyType>(){

        @Override
        public int compare(SoyType st1, SoyType st2) {
            return st1.toString().compareTo(st2.toString());
        }
    };
    private final ImmutableSortedSet<SoyType> members;

    private UnionType(Collection<SoyType> members) {
        this.members = UnionType.flatten(members);
    }

    public static UnionType of(SoyType ... members) {
        return new UnionType(Arrays.asList(members));
    }

    public static UnionType of(Collection<SoyType> members) {
        return new UnionType(members);
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.UNION;
    }

    public Set<SoyType> getMembers() {
        return this.members;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        if (srcType.getKind() == SoyType.Kind.UNION) {
            UnionType fromUnion = (UnionType)srcType;
            for (SoyType fromMember : fromUnion.members) {
                if (this.isAssignableFrom(fromMember)) continue;
                return false;
            }
            return true;
        }
        for (SoyType memberType : this.members) {
            if (!memberType.isAssignableFrom(srcType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        for (SoyType memberType : this.members) {
            if (!memberType.isInstance(value)) continue;
            return true;
        }
        return false;
    }

    public boolean isNullable() {
        for (SoyType memberType : this.members) {
            if (memberType.getKind() != SoyType.Kind.NULL) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return Joiner.on((char)'|').join(this.members);
    }

    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass() && ((UnionType)other).members.equals(this.members);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.members});
    }

    private static ImmutableSortedSet<SoyType> flatten(Collection<SoyType> members) {
        ImmutableSortedSet.Builder builder = new ImmutableSortedSet.Builder(MEMBER_ORDER);
        for (SoyType type : members) {
            if (type.getKind() == SoyType.Kind.UNION) {
                builder.addAll(((UnionType)type).members);
                continue;
            }
            builder.add((Object)type);
        }
        return builder.build();
    }
}

