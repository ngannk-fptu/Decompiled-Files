/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.core.IType
 *  org.eclipse.jdt.core.JavaModelException
 */
package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;

public final class ExtraFlags {
    public static final int HasNonPrivateStaticMemberTypes = 1;
    public static final int IsMemberType = 2;
    public static final int IsLocalType = 4;
    public static final int ParameterTypesStoredAsSignature = 16;

    public static int getExtraFlags(ClassFileReader reader) {
        IBinaryNestedType[] memberTypes;
        int memberTypeCounter;
        int extraFlags = 0;
        if (reader.isNestedType()) {
            extraFlags |= 2;
        }
        if (reader.isLocal()) {
            extraFlags |= 4;
        }
        int n = memberTypeCounter = (memberTypes = reader.getMemberTypes()) == null ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            int i = 0;
            while (i < memberTypeCounter) {
                int modifiers = memberTypes[i].getModifiers();
                if ((modifiers & 8) != 0 && (modifiers & 2) == 0) {
                    extraFlags |= 1;
                    break;
                }
                ++i;
            }
        }
        return extraFlags;
    }

    public static int getExtraFlags(IType type) throws JavaModelException {
        IType[] memberTypes;
        int memberTypeCounter;
        int extraFlags = 0;
        if (type.isMember()) {
            extraFlags |= 2;
        }
        if (type.isLocal()) {
            extraFlags |= 4;
        }
        int n = memberTypeCounter = (memberTypes = type.getTypes()) == null ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            int i = 0;
            while (i < memberTypeCounter) {
                int flags = memberTypes[i].getFlags();
                if ((flags & 8) != 0 && (flags & 2) == 0) {
                    extraFlags |= 1;
                    break;
                }
                ++i;
            }
        }
        return extraFlags;
    }

    public static int getExtraFlags(TypeDeclaration typeDeclaration) {
        TypeDeclaration[] memberTypes;
        int memberTypeCounter;
        int extraFlags = 0;
        if (typeDeclaration.enclosingType != null) {
            extraFlags |= 2;
        }
        int n = memberTypeCounter = (memberTypes = typeDeclaration.memberTypes) == null ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            int i = 0;
            while (i < memberTypeCounter) {
                int modifiers = memberTypes[i].modifiers;
                if ((modifiers & 8) != 0 && (modifiers & 2) == 0) {
                    extraFlags |= 1;
                    break;
                }
                ++i;
            }
        }
        return extraFlags;
    }
}

