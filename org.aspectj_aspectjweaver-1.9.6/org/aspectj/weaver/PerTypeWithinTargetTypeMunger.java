/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.patterns.PerTypeWithin;
import org.aspectj.weaver.patterns.Pointcut;

public class PerTypeWithinTargetTypeMunger
extends ResolvedTypeMunger {
    private UnresolvedType aspectType;
    private PerTypeWithin testPointcut;
    private volatile int hashCode = 0;

    public PerTypeWithinTargetTypeMunger(UnresolvedType aspectType, PerTypeWithin testPointcut) {
        super(PerTypeWithinInterface, null);
        this.aspectType = aspectType;
        this.testPointcut = testPointcut;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerTypeWithinTargetTypeMunger)) {
            return false;
        }
        PerTypeWithinTargetTypeMunger o = (PerTypeWithinTargetTypeMunger)other;
        return (o.testPointcut == null ? this.testPointcut == null : this.testPointcut.equals(o.testPointcut)) && (o.aspectType == null ? this.aspectType == null : this.aspectType.equals(o.aspectType));
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + (this.testPointcut == null ? 0 : this.testPointcut.hashCode());
            this.hashCode = result = 37 * result + (this.aspectType == null ? 0 : this.aspectType.hashCode());
        }
        return this.hashCode;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("shouldn't be serialized");
    }

    public UnresolvedType getAspectType() {
        return this.aspectType;
    }

    public Pointcut getTestPointcut() {
        return this.testPointcut;
    }

    @Override
    public boolean matches(ResolvedType matchType, ResolvedType aspectType) {
        return this.isWithinType(matchType).alwaysTrue() && !matchType.isInterface() && (matchType.canBeSeenBy(aspectType) || aspectType.isPrivilegedAspect());
    }

    private FuzzyBoolean isWithinType(ResolvedType type) {
        while (type != null) {
            if (this.testPointcut.getTypePattern().matchesStatically(type)) {
                return FuzzyBoolean.YES;
            }
            type = type.getDeclaringType();
        }
        return FuzzyBoolean.NO;
    }
}

