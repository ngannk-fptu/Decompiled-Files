/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IGenericType;
import org.eclipse.jdt.internal.compiler.env.IRecordComponent;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;

public interface IBinaryType
extends IGenericType {
    public static final char[][] NoInterface = CharOperation.NO_CHAR_CHAR;
    public static final IBinaryNestedType[] NoNestedType = new IBinaryNestedType[0];
    public static final IBinaryField[] NoField = new IBinaryField[0];
    public static final IBinaryMethod[] NoMethod = new IBinaryMethod[0];

    public IBinaryAnnotation[] getAnnotations();

    public IBinaryTypeAnnotation[] getTypeAnnotations();

    public char[] getEnclosingMethod();

    public char[] getEnclosingTypeName();

    public IBinaryField[] getFields();

    public IRecordComponent[] getRecordComponents();

    public char[] getModule();

    public char[] getGenericSignature();

    public char[][] getInterfaceNames();

    default public char[][] getPermittedSubtypeNames() {
        return null;
    }

    public IBinaryNestedType[] getMemberTypes();

    public IBinaryMethod[] getMethods();

    public char[][][] getMissingTypeNames();

    public char[] getName();

    public char[] getSourceName();

    public char[] getSuperclassName();

    public long getTagBits();

    public boolean isAnonymous();

    public boolean isLocal();

    public boolean isRecord();

    public boolean isMember();

    public char[] sourceFileName();

    public ITypeAnnotationWalker enrichWithExternalAnnotationsFor(ITypeAnnotationWalker var1, Object var2, LookupEnvironment var3);

    public BinaryTypeBinding.ExternalAnnotationStatus getExternalAnnotationStatus();
}

