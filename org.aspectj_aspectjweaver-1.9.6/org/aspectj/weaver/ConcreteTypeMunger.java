/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.util.PartialOrder;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public abstract class ConcreteTypeMunger
implements PartialOrder.PartialComparable {
    protected ResolvedTypeMunger munger;
    protected ResolvedType aspectType;

    public ConcreteTypeMunger(ResolvedTypeMunger munger, ResolvedType aspectType) {
        this.munger = munger;
        this.aspectType = aspectType;
    }

    public boolean equivalentTo(Object other) {
        if (!(other instanceof ConcreteTypeMunger)) {
            return false;
        }
        ConcreteTypeMunger o = (ConcreteTypeMunger)other;
        ResolvedTypeMunger otherTypeMunger = o.getMunger();
        ResolvedTypeMunger thisTypeMunger = this.getMunger();
        if (thisTypeMunger instanceof NewConstructorTypeMunger && otherTypeMunger instanceof NewConstructorTypeMunger) {
            return ((NewConstructorTypeMunger)otherTypeMunger).equivalentTo(thisTypeMunger) && (o.getAspectType() == null ? this.getAspectType() == null : o.getAspectType().equals(this.getAspectType()));
        }
        return (otherTypeMunger == null ? thisTypeMunger == null : otherTypeMunger.equals(thisTypeMunger)) && (o.getAspectType() == null ? this.getAspectType() == null : o.getAspectType().equals(this.getAspectType()));
    }

    public ResolvedTypeMunger getMunger() {
        return this.munger;
    }

    public ResolvedType getAspectType() {
        return this.aspectType;
    }

    public ResolvedMember getSignature() {
        return this.munger.getSignature();
    }

    public World getWorld() {
        return this.aspectType.getWorld();
    }

    public ISourceLocation getSourceLocation() {
        if (this.munger == null) {
            return null;
        }
        return this.munger.getSourceLocation();
    }

    public boolean matches(ResolvedType onType) {
        if (this.munger == null) {
            throw new RuntimeException("huh: " + this);
        }
        return this.munger.matches(onType, this.aspectType);
    }

    public ResolvedMember getMatchingSyntheticMember(Member member) {
        return this.munger.getMatchingSyntheticMember(member, this.aspectType);
    }

    @Override
    public int compareTo(Object other) {
        ConcreteTypeMunger o = (ConcreteTypeMunger)other;
        ResolvedType otherAspect = o.aspectType;
        if (this.aspectType.equals(otherAspect)) {
            return this.getSignature().getStart() < o.getSignature().getStart() ? -1 : 1;
        }
        if (this.aspectType.isAssignableFrom(o.aspectType)) {
            return 1;
        }
        if (o.aspectType.isAssignableFrom(this.aspectType)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int fallbackCompareTo(Object other) {
        return 0;
    }

    public boolean isTargetTypeParameterized() {
        if (this.munger == null) {
            return false;
        }
        return this.munger.sharesTypeVariablesWithGenericType();
    }

    public abstract ConcreteTypeMunger parameterizedFor(ResolvedType var1);

    public boolean isLateMunger() {
        if (this.munger == null) {
            return false;
        }
        return this.munger.isLateMunger();
    }

    public abstract ConcreteTypeMunger parameterizeWith(Map<String, UnresolvedType> var1, World var2);

    public boolean existsToSupportShadowMunging() {
        if (this.munger != null) {
            return this.munger.existsToSupportShadowMunging();
        }
        return false;
    }

    public boolean shouldOverwrite() {
        return true;
    }
}

