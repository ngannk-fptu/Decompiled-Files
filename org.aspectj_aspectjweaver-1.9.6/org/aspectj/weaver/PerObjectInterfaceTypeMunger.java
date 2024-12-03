/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.patterns.PerFromSuper;
import org.aspectj.weaver.patterns.PerObject;
import org.aspectj.weaver.patterns.PerThisOrTargetPointcutVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public class PerObjectInterfaceTypeMunger
extends ResolvedTypeMunger {
    private final UnresolvedType interfaceType;
    private final Pointcut testPointcut;
    private TypePattern lazyTestTypePattern;
    private volatile int hashCode = 0;

    public boolean equals(Object other) {
        if (other == null || !(other instanceof PerObjectInterfaceTypeMunger)) {
            return false;
        }
        PerObjectInterfaceTypeMunger o = (PerObjectInterfaceTypeMunger)other;
        return (this.testPointcut == null ? o.testPointcut == null : this.testPointcut.equals(o.testPointcut)) && (this.lazyTestTypePattern == null ? o.lazyTestTypePattern == null : this.lazyTestTypePattern.equals(o.lazyTestTypePattern));
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + (this.testPointcut == null ? 0 : this.testPointcut.hashCode());
            this.hashCode = result = 37 * result + (this.lazyTestTypePattern == null ? 0 : this.lazyTestTypePattern.hashCode());
        }
        return this.hashCode;
    }

    public PerObjectInterfaceTypeMunger(UnresolvedType aspectType, Pointcut testPointcut) {
        super(PerObjectInterface, null);
        this.testPointcut = testPointcut;
        this.interfaceType = AjcMemberMaker.perObjectInterfaceType(aspectType);
    }

    private TypePattern getTestTypePattern(ResolvedType aspectType) {
        if (this.lazyTestTypePattern == null) {
            boolean isPerThis;
            if (aspectType.getPerClause() instanceof PerFromSuper) {
                PerFromSuper ps = (PerFromSuper)aspectType.getPerClause();
                isPerThis = ((PerObject)ps.lookupConcretePerClause(aspectType)).isThis();
            } else {
                isPerThis = ((PerObject)aspectType.getPerClause()).isThis();
            }
            PerThisOrTargetPointcutVisitor v = new PerThisOrTargetPointcutVisitor(!isPerThis, aspectType);
            this.lazyTestTypePattern = v.getPerTypePointcut(this.testPointcut);
            this.hashCode = 0;
        }
        return this.lazyTestTypePattern;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("shouldn't be serialized");
    }

    public UnresolvedType getInterfaceType() {
        return this.interfaceType;
    }

    public Pointcut getTestPointcut() {
        return this.testPointcut;
    }

    @Override
    public boolean matches(ResolvedType matchType, ResolvedType aspectType) {
        if (matchType.isInterface()) {
            return false;
        }
        return this.getTestTypePattern(aspectType).matchesStatically(matchType);
    }

    @Override
    public boolean isLateMunger() {
        return true;
    }
}

