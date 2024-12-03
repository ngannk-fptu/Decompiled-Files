/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;

public class NewParentTypeMunger
extends ResolvedTypeMunger {
    ResolvedType newParent;
    ResolvedType declaringType;
    private boolean isMixin;
    private volatile int hashCode = 0;

    public NewParentTypeMunger(ResolvedType newParent, ResolvedType declaringType) {
        super(Parent, null);
        this.newParent = newParent;
        this.declaringType = declaringType;
        this.isMixin = false;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("unimplemented");
    }

    public ResolvedType getNewParent() {
        return this.newParent;
    }

    public boolean equals(Object other) {
        if (!(other instanceof NewParentTypeMunger)) {
            return false;
        }
        NewParentTypeMunger o = (NewParentTypeMunger)other;
        return this.newParent.equals(o.newParent) && this.isMixin == o.isMixin;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + this.newParent.hashCode();
            this.hashCode = result = 37 * result + (this.isMixin ? 0 : 1);
        }
        return this.hashCode;
    }

    @Override
    public ResolvedType getDeclaringType() {
        return this.declaringType;
    }

    public void setIsMixin(boolean b) {
        this.isMixin = true;
    }

    public boolean isMixin() {
        return this.isMixin;
    }
}

