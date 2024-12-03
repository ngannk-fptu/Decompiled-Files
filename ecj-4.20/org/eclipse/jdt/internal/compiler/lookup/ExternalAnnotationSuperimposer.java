/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipFile;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.AnnotatableTypeSystem;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.Messages;

class ExternalAnnotationSuperimposer
extends TypeBindingVisitor {
    private ITypeAnnotationWalker currentWalker;
    private TypeBinding typeReplacement;
    private LookupEnvironment environment;
    private boolean isReplacing;

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void apply(SourceTypeBinding typeBinding, String externalAnnotationPath) {
        block24: {
            block21: {
                block22: {
                    block23: {
                        block25: {
                            zipFile = null;
                            annotationBase = new File(externalAnnotationPath);
                            if (!annotationBase.exists()) break block21;
                            binaryTypeName = String.valueOf(typeBinding.constantPoolName());
                            relativeFileName = String.valueOf(binaryTypeName.replace('.', '/')) + ".eea";
                            if (!annotationBase.isDirectory()) break block25;
                            input /* !! */  = new FileInputStream(String.valueOf(externalAnnotationPath) + '/' + relativeFileName);
                            ** GOTO lbl25
                        }
                        zipFile = new ZipFile(externalAnnotationPath);
                        zipEntry = zipFile.getEntry(relativeFileName);
                        if (zipEntry != null) break block22;
                        if (zipFile == null) break block23;
                        try {
                            zipFile.close();
                        }
                        catch (IOException v0) {}
                    }
                    return;
                }
                try {
                    input /* !! */  = zipFile.getInputStream(zipEntry);
lbl25:
                    // 2 sources

                    ExternalAnnotationSuperimposer.annotateType(typeBinding, new ExternalAnnotationProvider(input /* !! */ , binaryTypeName), typeBinding.environment);
                }
                catch (FileNotFoundException v1) {
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        }
                        catch (IOException v2) {}
                    }
                    break block24;
                }
                catch (IOException e) {
                    try {
                        typeBinding.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_externaAnnotationFile, new String[]{String.valueOf(typeBinding.readableName()), externalAnnotationPath, e.getMessage()}));
                        ** if (zipFile == null) goto lbl-1000
                    }
                    catch (Throwable var8_9) {
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            }
                            catch (IOException v4) {}
                        }
                        throw var8_9;
                    }
lbl-1000:
                    // 1 sources

                    {
                        try {
                            zipFile.close();
                        }
                        catch (IOException v3) {}
                    }
lbl-1000:
                    // 2 sources

                    {
                        break block24;
                    }
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                }
                catch (IOException v5) {}
            }
        }
    }

    static void annotateType(SourceTypeBinding binding, ExternalAnnotationProvider provider, LookupEnvironment environment) {
        ITypeAnnotationWalker typeWalker = provider.forTypeHeader(environment);
        if (typeWalker != null && typeWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            ExternalAnnotationSuperimposer visitor = new ExternalAnnotationSuperimposer(environment);
            TypeVariableBinding[] typeParameters = binding.typeVariables();
            int i = 0;
            while (i < typeParameters.length) {
                if (visitor.go(typeWalker.toTypeParameter(true, i))) {
                    typeParameters[i] = visitor.superimpose(typeParameters[i], TypeVariableBinding.class);
                }
                ++i;
            }
        }
        binding.externalAnnotationProvider = provider;
    }

    public static void annotateComponentBinding(RecordComponentBinding componentBinding, ExternalAnnotationProvider provider, LookupEnvironment environment) {
        ITypeAnnotationWalker walker;
        ExternalAnnotationSuperimposer visitor;
        char[] componentSignature = componentBinding.genericSignature();
        if (componentSignature == null && componentBinding.type != null) {
            componentSignature = componentBinding.type.signature();
        }
        if ((visitor = new ExternalAnnotationSuperimposer(environment)).go(walker = provider.forField(componentBinding.name, componentSignature, environment))) {
            componentBinding.type = visitor.superimpose(componentBinding.type, TypeBinding.class);
        }
    }

    public static void annotateFieldBinding(FieldBinding field, ExternalAnnotationProvider provider, LookupEnvironment environment) {
        ITypeAnnotationWalker walker;
        ExternalAnnotationSuperimposer visitor;
        char[] fieldSignature = field.genericSignature();
        if (fieldSignature == null && field.type != null) {
            fieldSignature = field.type.signature();
        }
        if ((visitor = new ExternalAnnotationSuperimposer(environment)).go(walker = provider.forField(field.name, fieldSignature, environment))) {
            field.type = visitor.superimpose(field.type, TypeBinding.class);
        }
    }

    public static void annotateMethodBinding(MethodBinding method, Argument[] arguments, ExternalAnnotationProvider provider, LookupEnvironment environment) {
        ITypeAnnotationWalker walker;
        char[] methodSignature = method.genericSignature();
        if (methodSignature == null) {
            methodSignature = method.signature();
        }
        if ((walker = provider.forMethod(method.selector, methodSignature, environment)) != null && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            ExternalAnnotationSuperimposer visitor = new ExternalAnnotationSuperimposer(environment);
            TypeVariableBinding[] typeParams = method.typeVariables;
            int i = 0;
            while (i < typeParams.length) {
                if (visitor.go(walker.toTypeParameter(false, i))) {
                    typeParams[i] = visitor.superimpose(typeParams[i], TypeVariableBinding.class);
                }
                i = (short)(i + 1);
            }
            if (!method.isConstructor() && visitor.go(walker.toMethodReturn())) {
                method.returnType = visitor.superimpose(method.returnType, TypeBinding.class);
            }
            TypeBinding[] parameters = method.parameters;
            short i2 = 0;
            while (i2 < parameters.length) {
                if (visitor.go(walker.toMethodParameter(i2))) {
                    parameters[i2] = visitor.superimpose(parameters[i2], TypeBinding.class);
                    if (arguments != null && i2 < arguments.length) {
                        arguments[i2].binding.type = parameters[i2];
                    }
                }
                i2 = (short)(i2 + 1);
            }
        }
    }

    ExternalAnnotationSuperimposer(LookupEnvironment environment) {
        this.environment = environment;
    }

    private ExternalAnnotationSuperimposer(TypeBinding typeReplacement, boolean isReplacing, ITypeAnnotationWalker walker) {
        this.typeReplacement = typeReplacement;
        this.isReplacing = isReplacing;
        this.currentWalker = walker;
    }

    private ExternalAnnotationSuperimposer snapshot() {
        ExternalAnnotationSuperimposer memento = new ExternalAnnotationSuperimposer(this.typeReplacement, this.isReplacing, this.currentWalker);
        this.typeReplacement = null;
        this.isReplacing = false;
        return memento;
    }

    private void restore(ExternalAnnotationSuperimposer memento) {
        this.isReplacing = memento.isReplacing;
        this.currentWalker = memento.currentWalker;
    }

    boolean go(ITypeAnnotationWalker walker) {
        this.reset();
        this.typeReplacement = null;
        this.isReplacing = false;
        this.currentWalker = walker;
        return walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }

    <T extends TypeBinding> T superimpose(T type, Class<? extends T> cl) {
        TypeBindingVisitor.visit((TypeBindingVisitor)this, type);
        if (cl.isInstance(this.typeReplacement)) {
            return (T)((TypeBinding)cl.cast(this.typeReplacement));
        }
        return type;
    }

    private TypeBinding goAndSuperimpose(ITypeAnnotationWalker walker, TypeBinding type) {
        if (walker == ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            return type;
        }
        this.currentWalker = walker;
        TypeBindingVisitor.visit((TypeBindingVisitor)this, type);
        if (this.typeReplacement == null) {
            return type;
        }
        this.isReplacing = true;
        TypeBinding answer = this.typeReplacement;
        this.typeReplacement = null;
        return answer;
    }

    @Override
    public boolean visit(ArrayBinding arrayBinding) {
        ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            int dims = arrayBinding.dimensions;
            AnnotationBinding[][] annotsOnDims = new AnnotationBinding[dims][];
            ITypeAnnotationWalker walker = this.currentWalker;
            int i = 0;
            while (i < dims) {
                IBinaryAnnotation[] binaryAnnotations = walker.getAnnotationsAtCursor(arrayBinding.id, false);
                if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                    annotsOnDims[i] = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                    this.isReplacing = true;
                } else {
                    annotsOnDims[i] = Binding.NO_ANNOTATIONS;
                }
                walker = walker.toNextArrayDimension();
                ++i;
            }
            TypeBinding leafComponentType = this.goAndSuperimpose(walker, arrayBinding.leafComponentType());
            if (this.isReplacing) {
                this.typeReplacement = this.environment.createArrayType(leafComponentType, dims, AnnotatableTypeSystem.flattenedAnnotations(annotsOnDims));
            }
        }
        finally {
            this.restore(memento);
        }
        return false;
    }

    @Override
    public boolean visit(BaseTypeBinding baseTypeBinding) {
        return false;
    }

    @Override
    public boolean visit(IntersectionTypeBinding18 intersectionTypeBinding18) {
        return false;
    }

    @Override
    public boolean visit(ParameterizedTypeBinding parameterizedTypeBinding) {
        ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            IBinaryAnnotation[] binaryAnnotations = this.currentWalker.getAnnotationsAtCursor(parameterizedTypeBinding.id, false);
            AnnotationBinding[] annotations = Binding.NO_ANNOTATIONS;
            if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                annotations = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                this.isReplacing = true;
            }
            TypeBinding[] typeArguments = parameterizedTypeBinding.typeArguments();
            TypeBinding[] newArguments = new TypeBinding[typeArguments.length];
            int i = 0;
            while (i < typeArguments.length) {
                newArguments[i] = this.goAndSuperimpose(memento.currentWalker.toTypeArgument(i), typeArguments[i]);
                ++i;
            }
            if (this.isReplacing) {
                this.typeReplacement = this.environment.createParameterizedType(parameterizedTypeBinding.genericType(), newArguments, parameterizedTypeBinding.enclosingType(), annotations);
            }
            return false;
        }
        finally {
            this.restore(memento);
        }
    }

    @Override
    public boolean visit(RawTypeBinding rawTypeBinding) {
        return this.visit((ReferenceBinding)rawTypeBinding);
    }

    @Override
    public boolean visit(ReferenceBinding referenceBinding) {
        IBinaryAnnotation[] binaryAnnotations = this.currentWalker.getAnnotationsAtCursor(referenceBinding.id, false);
        if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
            this.typeReplacement = this.environment.createAnnotatedType((TypeBinding)referenceBinding, BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null));
        }
        return false;
    }

    @Override
    public boolean visit(TypeVariableBinding typeVariable) {
        return this.visit((ReferenceBinding)typeVariable);
    }

    @Override
    public boolean visit(WildcardBinding wildcardBinding) {
        TypeBinding bound = wildcardBinding.bound;
        ExternalAnnotationSuperimposer memento = this.snapshot();
        try {
            if (bound != null) {
                bound = this.goAndSuperimpose(memento.currentWalker.toWildcardBound(), bound);
            }
            IBinaryAnnotation[] binaryAnnotations = memento.currentWalker.getAnnotationsAtCursor(-1, false);
            if (this.isReplacing || binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                TypeBinding[] otherBounds = wildcardBinding.otherBounds;
                if (binaryAnnotations != ITypeAnnotationWalker.NO_ANNOTATIONS) {
                    AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(binaryAnnotations, this.environment, null);
                    this.typeReplacement = this.environment.createWildcard(wildcardBinding.genericType, wildcardBinding.rank, bound, otherBounds, wildcardBinding.boundKind, annotations);
                } else {
                    this.typeReplacement = this.environment.createWildcard(wildcardBinding.genericType, wildcardBinding.rank, bound, otherBounds, wildcardBinding.boundKind);
                }
            }
        }
        finally {
            this.restore(memento);
        }
        return false;
    }
}

