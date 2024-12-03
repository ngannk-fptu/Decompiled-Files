/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class TypeBindingVisitor {
    private SimpleLookupTable visitedCache;

    public void reset() {
        this.visitedCache = null;
    }

    public boolean visit(BaseTypeBinding baseTypeBinding) {
        return true;
    }

    public boolean visit(ArrayBinding arrayBinding) {
        return true;
    }

    public boolean visit(TypeVariableBinding typeVariable) {
        return true;
    }

    public boolean visit(ReferenceBinding referenceBinding) {
        return true;
    }

    public boolean visit(WildcardBinding wildcardBinding) {
        return true;
    }

    public boolean visit(ParameterizedTypeBinding parameterizedTypeBinding) {
        return true;
    }

    public boolean visit(IntersectionTypeBinding18 intersectionTypeBinding18) {
        return true;
    }

    public boolean visit(RawTypeBinding rawTypeBinding) {
        return true;
    }

    public boolean visit(PolyTypeBinding polyTypeBinding) {
        return true;
    }

    public static void visit(TypeBindingVisitor visitor, ReferenceBinding[] types) {
        int i = 0;
        int length = types == null ? 0 : types.length;
        while (i < length) {
            TypeBindingVisitor.visit(visitor, types[i]);
            ++i;
        }
    }

    public static void visit(TypeBindingVisitor visitor, TypeBinding type) {
        Object result;
        if (type == null) {
            return;
        }
        SimpleLookupTable visitedCache = visitor.visitedCache;
        if (visitedCache == null) {
            visitedCache = visitor.visitedCache = new SimpleLookupTable(3);
        }
        if ((result = visitedCache.get(type)) == Boolean.TRUE) {
            return;
        }
        visitedCache.put(type, Boolean.TRUE);
        switch (type.kind()) {
            case 4100: {
                TypeVariableBinding typeVariableBinding = (TypeVariableBinding)type;
                if (!visitor.visit(typeVariableBinding)) break;
                TypeBindingVisitor.visit(visitor, typeVariableBinding.firstBound);
                TypeBindingVisitor.visit(visitor, typeVariableBinding.superclass);
                TypeBindingVisitor.visit(visitor, typeVariableBinding.superInterfaces);
                break;
            }
            case 260: {
                ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)type;
                if (!visitor.visit(parameterizedTypeBinding)) break;
                TypeBindingVisitor.visit(visitor, parameterizedTypeBinding.enclosingType());
                TypeBindingVisitor.visit(visitor, parameterizedTypeBinding.arguments);
                break;
            }
            case 68: {
                ArrayBinding arrayBinding = (ArrayBinding)type;
                if (!visitor.visit(arrayBinding)) break;
                TypeBindingVisitor.visit(visitor, arrayBinding.leafComponentType);
                break;
            }
            case 516: 
            case 8196: {
                WildcardBinding wildcard = (WildcardBinding)type;
                if (!visitor.visit(wildcard) || wildcard.boundKind == 0) break;
                TypeBindingVisitor.visit(visitor, wildcard.bound);
                TypeBindingVisitor.visit(visitor, wildcard.otherBounds);
                break;
            }
            case 132: {
                visitor.visit((BaseTypeBinding)type);
                break;
            }
            case 1028: {
                visitor.visit((RawTypeBinding)type);
                break;
            }
            case 4: 
            case 2052: {
                ReferenceBinding referenceBinding = (ReferenceBinding)type;
                if (!visitor.visit(referenceBinding)) break;
                TypeBindingVisitor.visit(visitor, referenceBinding.enclosingType());
                TypeBindingVisitor.visit(visitor, referenceBinding.typeVariables());
                break;
            }
            case 32772: {
                IntersectionTypeBinding18 intersectionTypeBinding18 = (IntersectionTypeBinding18)type;
                if (!visitor.visit(intersectionTypeBinding18)) break;
                TypeBindingVisitor.visit(visitor, intersectionTypeBinding18.intersectingTypes);
                break;
            }
            case 65540: {
                visitor.visit((PolyTypeBinding)type);
                break;
            }
            default: {
                throw new InternalError("Unexpected binding type");
            }
        }
    }

    public static void visit(TypeBindingVisitor visitor, TypeBinding[] types) {
        int i = 0;
        int length = types == null ? 0 : types.length;
        while (i < length) {
            TypeBindingVisitor.visit(visitor, types[i]);
            ++i;
        }
    }
}

