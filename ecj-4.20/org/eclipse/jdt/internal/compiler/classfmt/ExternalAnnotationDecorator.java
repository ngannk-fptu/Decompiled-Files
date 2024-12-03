/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IRecordComponent;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class ExternalAnnotationDecorator
implements IBinaryType {
    private IBinaryType inputType;
    private ExternalAnnotationProvider annotationProvider;
    private boolean isFromSource;

    public ExternalAnnotationDecorator(IBinaryType toDecorate, ExternalAnnotationProvider externalAnnotationProvider) {
        if (toDecorate == null) {
            throw new NullPointerException("toDecorate");
        }
        this.inputType = toDecorate;
        this.annotationProvider = externalAnnotationProvider;
    }

    public ExternalAnnotationDecorator(IBinaryType toDecorate, boolean isFromSource) {
        if (toDecorate == null) {
            throw new NullPointerException("toDecorate");
        }
        this.isFromSource = isFromSource;
        this.inputType = toDecorate;
    }

    @Override
    public char[] getFileName() {
        return this.inputType.getFileName();
    }

    @Override
    public boolean isBinaryType() {
        return this.inputType.isBinaryType();
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return this.inputType.getAnnotations();
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.inputType.getTypeAnnotations();
    }

    @Override
    public char[] getEnclosingMethod() {
        return this.inputType.getEnclosingMethod();
    }

    @Override
    public char[] getEnclosingTypeName() {
        return this.inputType.getEnclosingTypeName();
    }

    @Override
    public IBinaryField[] getFields() {
        return this.inputType.getFields();
    }

    @Override
    public IRecordComponent[] getRecordComponents() {
        return this.inputType.getRecordComponents();
    }

    @Override
    public char[] getGenericSignature() {
        return this.inputType.getGenericSignature();
    }

    @Override
    public char[][] getInterfaceNames() {
        return this.inputType.getInterfaceNames();
    }

    @Override
    public IBinaryNestedType[] getMemberTypes() {
        return this.inputType.getMemberTypes();
    }

    @Override
    public IBinaryMethod[] getMethods() {
        return this.inputType.getMethods();
    }

    @Override
    public char[][][] getMissingTypeNames() {
        return this.inputType.getMissingTypeNames();
    }

    @Override
    public char[] getName() {
        return this.inputType.getName();
    }

    @Override
    public char[] getSourceName() {
        return this.inputType.getSourceName();
    }

    @Override
    public char[] getSuperclassName() {
        return this.inputType.getSuperclassName();
    }

    @Override
    public long getTagBits() {
        return this.inputType.getTagBits();
    }

    @Override
    public boolean isAnonymous() {
        return this.inputType.isAnonymous();
    }

    @Override
    public boolean isLocal() {
        return this.inputType.isLocal();
    }

    @Override
    public boolean isRecord() {
        return this.inputType.isRecord();
    }

    @Override
    public boolean isMember() {
        return this.inputType.isMember();
    }

    @Override
    public char[] sourceFileName() {
        return this.inputType.sourceFileName();
    }

    @Override
    public int getModifiers() {
        return this.inputType.getModifiers();
    }

    @Override
    public char[] getModule() {
        return this.inputType.getModule();
    }

    public static ZipFile getAnnotationZipFile(String basePath, ZipFileProducer producer) throws IOException {
        File annotationBase = new File(basePath);
        if (!annotationBase.isFile()) {
            return null;
        }
        return producer != null ? producer.produce() : new ZipFile(annotationBase);
    }

    public static ExternalAnnotationProvider externalAnnotationProvider(String basePath, String qualifiedBinaryTypeName, ZipFile zipFile) throws IOException {
        String qualifiedBinaryFileName = String.valueOf(qualifiedBinaryTypeName) + ".eea";
        if (zipFile == null) {
            File annotationBase = new File(basePath);
            if (annotationBase.isDirectory()) {
                String filePath = String.valueOf(annotationBase.getAbsolutePath()) + '/' + qualifiedBinaryFileName;
                try {
                    return new ExternalAnnotationProvider(new FileInputStream(filePath), qualifiedBinaryTypeName);
                }
                catch (FileNotFoundException fileNotFoundException) {
                    return null;
                }
            }
        } else {
            ZipEntry entry = zipFile.getEntry(qualifiedBinaryFileName);
            if (entry != null) {
                Throwable throwable = null;
                Object var6_9 = null;
                try (InputStream is = zipFile.getInputStream(entry);){
                    return new ExternalAnnotationProvider(is, qualifiedBinaryTypeName);
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
        }
        return null;
    }

    public static IBinaryType create(IBinaryType toDecorate, String basePath, String qualifiedBinaryTypeName, ZipFile zipFile) throws IOException {
        ExternalAnnotationProvider externalAnnotationProvider = ExternalAnnotationDecorator.externalAnnotationProvider(basePath, qualifiedBinaryTypeName, zipFile);
        if (externalAnnotationProvider == null) {
            return toDecorate;
        }
        return new ExternalAnnotationDecorator(toDecorate, externalAnnotationProvider);
    }

    @Override
    public ITypeAnnotationWalker enrichWithExternalAnnotationsFor(ITypeAnnotationWalker walker, Object member, LookupEnvironment environment) {
        if (walker == ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER && this.annotationProvider != null) {
            if (member == null) {
                return this.annotationProvider.forTypeHeader(environment);
            }
            if (member instanceof IBinaryField) {
                IBinaryField field = (IBinaryField)member;
                char[] fieldSignature = field.getGenericSignature();
                if (fieldSignature == null) {
                    fieldSignature = field.getTypeName();
                }
                return this.annotationProvider.forField(field.getName(), fieldSignature, environment);
            }
            if (member instanceof IBinaryMethod) {
                IBinaryMethod method = (IBinaryMethod)member;
                char[] methodSignature = method.getGenericSignature();
                if (methodSignature == null) {
                    methodSignature = method.getMethodDescriptor();
                }
                return this.annotationProvider.forMethod(method.isConstructor() ? TypeConstants.INIT : method.getSelector(), methodSignature, environment);
            }
        }
        return walker;
    }

    @Override
    public BinaryTypeBinding.ExternalAnnotationStatus getExternalAnnotationStatus() {
        if (this.annotationProvider == null) {
            if (this.isFromSource) {
                return BinaryTypeBinding.ExternalAnnotationStatus.FROM_SOURCE;
            }
            return BinaryTypeBinding.ExternalAnnotationStatus.NO_EEA_FILE;
        }
        return BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
    }

    public static interface ZipFileProducer {
        public ZipFile produce() throws IOException;
    }
}

