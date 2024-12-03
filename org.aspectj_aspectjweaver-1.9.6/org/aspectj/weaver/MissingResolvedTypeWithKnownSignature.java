/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collections;
import java.util.List;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.context.CompilationAndWeavingContext;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;

public class MissingResolvedTypeWithKnownSignature
extends ResolvedType {
    private static ResolvedMember[] NO_MEMBERS = new ResolvedMember[0];
    private static ResolvedType[] NO_TYPES = new ResolvedType[0];
    private boolean issuedCantFindTypeError = false;
    private boolean issuedJoinPointWarning = false;
    private boolean issuedMissingInterfaceWarning = false;

    public MissingResolvedTypeWithKnownSignature(String signature, World world) {
        super(signature, world);
    }

    @Override
    public boolean isMissing() {
        return true;
    }

    public MissingResolvedTypeWithKnownSignature(String signature, String signatureErasure, World world) {
        super(signature, signatureErasure, world);
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        this.raiseCantFindType("cantFindTypeFields");
        return NO_MEMBERS;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        this.raiseCantFindType("cantFindTypeMethods");
        return NO_MEMBERS;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        this.raiseCantFindType("cantFindTypeAnnotation");
        return AnnotationAJ.EMPTY_ARRAY;
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        this.raiseCantFindType("cantFindTypeInterfaces");
        return NO_TYPES;
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        this.raiseCantFindType("cantFindTypePointcuts");
        return NO_MEMBERS;
    }

    @Override
    public ResolvedType getSuperclass() {
        this.raiseCantFindType("cantFindTypeSuperclass");
        return ResolvedType.MISSING;
    }

    @Override
    public int getModifiers() {
        this.raiseCantFindType("cantFindTypeModifiers");
        return 0;
    }

    @Override
    public ISourceContext getSourceContext() {
        return new ISourceContext(){

            @Override
            public ISourceLocation makeSourceLocation(IHasPosition position) {
                return null;
            }

            @Override
            public ISourceLocation makeSourceLocation(int line, int offset) {
                return null;
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public void tidy() {
            }
        };
    }

    @Override
    public boolean isAssignableFrom(ResolvedType other) {
        this.raiseCantFindType("cantFindTypeAssignable", other.getName());
        return false;
    }

    @Override
    public boolean isAssignableFrom(ResolvedType other, boolean allowMissing) {
        if (allowMissing) {
            return false;
        }
        return this.isAssignableFrom(other);
    }

    @Override
    public boolean isCoerceableFrom(ResolvedType other) {
        this.raiseCantFindType("cantFindTypeCoerceable", other.getName());
        return false;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        this.raiseCantFindType("cantFindTypeAnnotation");
        return false;
    }

    public List getInterTypeMungers() {
        return Collections.EMPTY_LIST;
    }

    public List getInterTypeMungersIncludingSupers() {
        return Collections.EMPTY_LIST;
    }

    public List getInterTypeParentMungers() {
        return Collections.EMPTY_LIST;
    }

    public List getInterTypeParentMungersIncludingSupers() {
        return Collections.EMPTY_LIST;
    }

    protected void collectInterTypeMungers(List collector) {
    }

    public void raiseWarningOnJoinPointSignature(String signature) {
        if (this.issuedJoinPointWarning) {
            return;
        }
        String message = WeaverMessages.format("cantFindTypeJoinPoint", this.getName(), signature);
        message = message + "\n" + CompilationAndWeavingContext.getCurrentContext();
        this.world.getLint().cantFindTypeAffectingJoinPointMatch.signal(message, null);
        this.issuedJoinPointWarning = true;
    }

    public void raiseWarningOnMissingInterfaceWhilstFindingMethods() {
        if (this.issuedMissingInterfaceWarning) {
            return;
        }
        String message = WeaverMessages.format("cantFindTypeInterfaceMethods", this.getName(), this.signature);
        message = message + "\n" + CompilationAndWeavingContext.getCurrentContext();
        this.world.getLint().cantFindTypeAffectingJoinPointMatch.signal(message, null);
        this.issuedMissingInterfaceWarning = true;
    }

    private void raiseCantFindType(String key) {
        if (!this.world.getLint().cantFindType.isEnabled()) {
            return;
        }
        if (this.issuedCantFindTypeError) {
            return;
        }
        String message = WeaverMessages.format(key, this.getName());
        message = message + "\n" + CompilationAndWeavingContext.getCurrentContext();
        this.world.getLint().cantFindType.signal(message, null);
        this.issuedCantFindTypeError = true;
    }

    private void raiseCantFindType(String key, String insert) {
        if (this.issuedCantFindTypeError) {
            return;
        }
        String message = WeaverMessages.format(key, this.getName(), insert);
        message = message + "\n" + CompilationAndWeavingContext.getCurrentContext();
        this.world.getLint().cantFindType.signal(message, null);
        this.issuedCantFindTypeError = true;
    }
}

