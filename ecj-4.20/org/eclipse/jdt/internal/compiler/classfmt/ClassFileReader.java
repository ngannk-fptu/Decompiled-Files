/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfo;
import org.eclipse.jdt.internal.compiler.classfmt.InnerClassInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ModuleInfo;
import org.eclipse.jdt.internal.compiler.classfmt.RecordComponentInfo;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IRecordComponent;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClassFileReader
extends ClassFileStruct
implements IBinaryType {
    private int accessFlags;
    private char[] classFileName;
    private char[] className;
    private int classNameIndex;
    private int constantPoolCount;
    private AnnotationInfo[] annotations;
    private TypeAnnotationInfo[] typeAnnotations;
    private FieldInfo[] fields;
    private ModuleInfo moduleDeclaration;
    public char[] moduleName;
    private int fieldsCount;
    private InnerClassInfo innerInfo;
    private InnerClassInfo[] innerInfos;
    private char[][] interfaceNames;
    private int interfacesCount;
    private char[][] permittedSubtypesNames;
    private int permittedSubtypesCount;
    private MethodInfo[] methods;
    private int methodsCount;
    private char[] signature;
    private char[] sourceName;
    private char[] sourceFileName;
    private char[] superclassName;
    private long tagBits;
    private long version;
    private char[] enclosingTypeName;
    private char[][][] missingTypeNames;
    private int enclosingNameAndTypeIndex;
    private char[] enclosingMethod;
    private char[] nestHost;
    private int nestMembersCount;
    private char[][] nestMembers;
    private boolean isRecord;
    private int recordComponentsCount;
    private RecordComponentInfo[] recordComponents;

    private static String printTypeModifiers(int modifiers) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter print = new PrintWriter(out);
        if ((modifiers & 1) != 0) {
            print.print("public ");
        }
        if ((modifiers & 2) != 0) {
            print.print("private ");
        }
        if ((modifiers & 0x10) != 0) {
            print.print("final ");
        }
        if ((modifiers & 0x20) != 0) {
            print.print("super ");
        }
        if ((modifiers & 0x200) != 0) {
            print.print("interface ");
        }
        if ((modifiers & 0x400) != 0) {
            print.print("abstract ");
        }
        if ((modifiers & 0x10000000) != 0) {
            print.print("sealed ");
        }
        print.flush();
        return out.toString();
    }

    public static ClassFileReader read(File file) throws ClassFormatException, IOException {
        return ClassFileReader.read(file, false);
    }

    public static ClassFileReader read(File file, boolean fullyInitialize) throws ClassFormatException, IOException {
        byte[] classFileBytes = Util.getFileByteContent(file);
        ClassFileReader classFileReader = new ClassFileReader(classFileBytes, file.getAbsolutePath().toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }

    public static ClassFileReader read(InputStream stream, String fileName) throws ClassFormatException, IOException {
        return ClassFileReader.read(stream, fileName, false);
    }

    public static ClassFileReader read(InputStream stream, String fileName, boolean fullyInitialize) throws ClassFormatException, IOException {
        byte[] classFileBytes = Util.getInputStreamAsByteArray(stream, -1);
        ClassFileReader classFileReader = new ClassFileReader(classFileBytes, fileName.toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }

    public static ClassFileReader read(ZipFile zip, String filename) throws ClassFormatException, IOException {
        return ClassFileReader.read(zip, filename, false);
    }

    public static ClassFileReader readFromJrt(File jrt, IModule module, String filename) throws ClassFormatException, IOException {
        return JRTUtil.getClassfile(jrt, filename, module);
    }

    public static ClassFileReader readFromModule(File jrt, String moduleName, String filename, Predicate<String> moduleNameFilter) throws ClassFormatException, IOException {
        return JRTUtil.getClassfile(jrt, filename, moduleName, moduleNameFilter);
    }

    public static ClassFileReader read(ZipFile zip, String filename, boolean fullyInitialize) throws ClassFormatException, IOException {
        ZipEntry ze = zip.getEntry(filename);
        if (ze == null) {
            return null;
        }
        byte[] classFileBytes = Util.getZipEntryByteContent(ze, zip);
        ClassFileReader classFileReader = new ClassFileReader(classFileBytes, filename.toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }

    public static ClassFileReader read(String fileName) throws ClassFormatException, IOException {
        return ClassFileReader.read(fileName, false);
    }

    public static ClassFileReader read(String fileName, boolean fullyInitialize) throws ClassFormatException, IOException {
        return ClassFileReader.read(new File(fileName), fullyInitialize);
    }

    public ClassFileReader(byte[] classFileBytes, char[] fileName) throws ClassFormatException {
        this(classFileBytes, fileName, false);
    }

    public ClassFileReader(byte[] classFileBytes, char[] fileName, boolean fullyInitialize) throws ClassFormatException {
        super(classFileBytes, null, 0);
        this.classFileName = fileName;
        int readOffset = 10;
        try {
            int i;
            this.version = ((long)this.u2At(6) << 16) + (long)this.u2At(4);
            this.constantPoolCount = this.u2At(8);
            this.constantPoolOffsets = new int[this.constantPoolCount];
            int i2 = 1;
            while (i2 < this.constantPoolCount) {
                int tag = this.u1At(readOffset);
                switch (tag) {
                    case 1: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += this.u2At(readOffset + 1);
                        readOffset += 3;
                        break;
                    }
                    case 3: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 4: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 5: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 9;
                        ++i2;
                        break;
                    }
                    case 6: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 9;
                        ++i2;
                        break;
                    }
                    case 7: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 8: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 9: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 10: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 11: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 12: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 15: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 4;
                        break;
                    }
                    case 16: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 17: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 18: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 19: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 20: {
                        this.constantPoolOffsets[i2] = readOffset;
                        readOffset += 3;
                    }
                }
                ++i2;
            }
            this.accessFlags = this.u2At(readOffset);
            this.classNameIndex = this.u2At(readOffset += 2);
            if (this.classNameIndex != 0) {
                this.className = this.getConstantClassNameAt(this.classNameIndex);
            }
            int superclassNameIndex = this.u2At(readOffset += 2);
            readOffset += 2;
            if (superclassNameIndex != 0) {
                this.superclassName = this.getConstantClassNameAt(superclassNameIndex);
                if (CharOperation.equals(this.superclassName, TypeConstants.CharArray_JAVA_LANG_RECORD_SLASH)) {
                    this.accessFlags |= 0x1000000;
                }
            }
            this.interfacesCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.interfacesCount != 0) {
                this.interfaceNames = new char[this.interfacesCount][];
                int i3 = 0;
                while (i3 < this.interfacesCount) {
                    this.interfaceNames[i3] = this.getConstantClassNameAt(this.u2At(readOffset));
                    readOffset += 2;
                    ++i3;
                }
            }
            this.fieldsCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.fieldsCount != 0) {
                this.fields = new FieldInfo[this.fieldsCount];
                i = 0;
                while (i < this.fieldsCount) {
                    FieldInfo field;
                    this.fields[i] = field = FieldInfo.createField(this.reference, this.constantPoolOffsets, readOffset, this.version);
                    readOffset += field.sizeInBytes();
                    ++i;
                }
            }
            this.methodsCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.methodsCount != 0) {
                this.methods = new MethodInfo[this.methodsCount];
                boolean isAnnotationType = (this.accessFlags & 0x2000) != 0;
                i = 0;
                while (i < this.methodsCount) {
                    this.methods[i] = isAnnotationType ? AnnotationMethodInfo.createAnnotationMethod(this.reference, this.constantPoolOffsets, readOffset, this.version) : MethodInfo.createMethod(this.reference, this.constantPoolOffsets, readOffset, this.version);
                    readOffset += this.methods[i].sizeInBytes();
                    ++i;
                }
            }
            int attributesCount = this.u2At(readOffset);
            readOffset += 2;
            i = 0;
            while (i < attributesCount) {
                int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)];
                char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (attributeName.length == 0) {
                    readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
                } else {
                    switch (attributeName[0]) {
                        case 'E': {
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.EnclosingMethodName)) break;
                            utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(readOffset + 6)] + 1)];
                            this.enclosingTypeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                            this.enclosingNameAndTypeIndex = this.u2At(readOffset + 8);
                            break;
                        }
                        case 'D': {
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) break;
                            this.accessFlags |= 0x100000;
                            break;
                        }
                        case 'I': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.InnerClassName)) {
                                char[] enclosingType;
                                int innerOffset = readOffset + 6;
                                int number_of_classes = this.u2At(innerOffset);
                                if (number_of_classes == 0) break;
                                innerOffset += 2;
                                this.innerInfos = new InnerClassInfo[number_of_classes];
                                int j = 0;
                                while (j < number_of_classes) {
                                    this.innerInfos[j] = new InnerClassInfo(this.reference, this.constantPoolOffsets, innerOffset);
                                    if (this.classNameIndex == this.innerInfos[j].innerClassNameIndex) {
                                        this.innerInfo = this.innerInfos[j];
                                    }
                                    innerOffset += 8;
                                    ++j;
                                }
                                if (this.innerInfo == null || (enclosingType = this.innerInfo.getEnclosingTypeName()) == null) break;
                                this.enclosingTypeName = enclosingType;
                                break;
                            }
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.InconsistentHierarchy)) break;
                            this.tagBits |= 0x20000L;
                            break;
                        }
                        case 'S': {
                            if (attributeName.length <= 2) break;
                            switch (attributeName[1]) {
                                case 'o': {
                                    if (!CharOperation.equals(attributeName, AttributeNamesConstants.SourceName)) break;
                                    utf8Offset = this.constantPoolOffsets[this.u2At(readOffset + 6)];
                                    this.sourceFileName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                    break;
                                }
                                case 'y': {
                                    if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
                                    this.accessFlags |= 0x1000;
                                    break;
                                }
                                case 'i': {
                                    if (!CharOperation.equals(attributeName, AttributeNamesConstants.SignatureName)) break;
                                    utf8Offset = this.constantPoolOffsets[this.u2At(readOffset + 6)];
                                    this.signature = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                }
                            }
                            break;
                        }
                        case 'R': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                                this.decodeAnnotations(readOffset, true);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                                this.decodeAnnotations(readOffset, false);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                                this.decodeTypeAnnotations(readOffset, true);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                                this.decodeTypeAnnotations(readOffset, false);
                                break;
                            }
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.RecordClass)) break;
                            this.decodeRecords(readOffset, attributeName);
                            break;
                        }
                        case 'M': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.MissingTypesName)) {
                                int missingTypeOffset = readOffset + 6;
                                int numberOfMissingTypes = this.u2At(missingTypeOffset);
                                if (numberOfMissingTypes == 0) break;
                                this.missingTypeNames = new char[numberOfMissingTypes][][];
                                missingTypeOffset += 2;
                                int j = 0;
                                while (j < numberOfMissingTypes) {
                                    utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(missingTypeOffset)] + 1)];
                                    char[] missingTypeConstantPoolName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                    this.missingTypeNames[j] = CharOperation.splitOn('/', missingTypeConstantPoolName);
                                    missingTypeOffset += 2;
                                    ++j;
                                }
                                break;
                            }
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.ModuleName)) break;
                            this.moduleDeclaration = ModuleInfo.createModule(this.reference, this.constantPoolOffsets, readOffset);
                            this.moduleName = this.moduleDeclaration.name();
                            break;
                        }
                        case 'N': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.NestHost)) {
                                utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(readOffset + 6)] + 1)];
                                this.nestHost = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                break;
                            }
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.NestMembers)) break;
                            int offset = readOffset + 6;
                            this.nestMembersCount = this.u2At(offset);
                            if (this.nestMembersCount == 0) break;
                            offset += 2;
                            this.nestMembers = new char[this.nestMembersCount][];
                            int j = 0;
                            while (j < this.nestMembersCount) {
                                utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(offset)] + 1)];
                                this.nestMembers[j] = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                offset += 2;
                                ++j;
                            }
                            break;
                        }
                        case 'P': {
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.PermittedSubclasses)) break;
                            int offset = readOffset + 6;
                            this.permittedSubtypesCount = this.u2At(offset);
                            if (this.permittedSubtypesCount == 0) break;
                            this.accessFlags |= 0x10000000;
                            offset += 2;
                            this.permittedSubtypesNames = new char[this.permittedSubtypesCount][];
                            int j = 0;
                            while (j < this.permittedSubtypesCount) {
                                utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(offset)] + 1)];
                                this.permittedSubtypesNames[j] = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                offset += 2;
                                ++j;
                            }
                            break;
                        }
                    }
                    readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
                }
                ++i;
            }
            if (this.moduleDeclaration != null && this.annotations != null) {
                this.moduleDeclaration.setAnnotations(this.annotations, this.tagBits, fullyInitialize);
                this.annotations = null;
            }
            if (fullyInitialize) {
                this.initialize();
            }
        }
        catch (ClassFormatException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ClassFormatException(e, this.classFileName, 21, readOffset);
        }
    }

    private void decodeRecords(int readOffset, char[] attributeName) {
        if (CharOperation.equals(attributeName, AttributeNamesConstants.RecordClass)) {
            this.isRecord = true;
            int offset = readOffset + 6;
            this.recordComponentsCount = this.u2At(offset);
            if (this.recordComponentsCount != 0) {
                offset += 2;
                this.recordComponents = new RecordComponentInfo[this.recordComponentsCount];
                int j = 0;
                while (j < this.recordComponentsCount) {
                    RecordComponentInfo component;
                    this.recordComponents[j] = component = RecordComponentInfo.createComponent(this.reference, this.constantPoolOffsets, offset, this.version);
                    offset += component.sizeInBytes();
                    ++j;
                }
            }
        }
    }

    public char[] getNestHost() {
        return this.nestHost;
    }

    @Override
    public BinaryTypeBinding.ExternalAnnotationStatus getExternalAnnotationStatus() {
        return BinaryTypeBinding.ExternalAnnotationStatus.NOT_EEA_CONFIGURED;
    }

    @Override
    public ITypeAnnotationWalker enrichWithExternalAnnotationsFor(ITypeAnnotationWalker walker, Object member, LookupEnvironment environment) {
        return walker;
    }

    public int accessFlags() {
        return this.accessFlags;
    }

    private void decodeAnnotations(int offset, boolean runtimeVisible) {
        block9: {
            int numberOfAnnotations = this.u2At(offset + 6);
            if (numberOfAnnotations <= 0) break block9;
            int readOffset = offset + 8;
            AnnotationInfo[] newInfos = null;
            int newInfoCount = 0;
            int i = 0;
            while (i < numberOfAnnotations) {
                block11: {
                    AnnotationInfo newInfo;
                    block10: {
                        newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
                        readOffset += newInfo.readOffset;
                        long standardTagBits = newInfo.standardAnnotationTagBits;
                        if (standardTagBits == 0L) break block10;
                        this.tagBits |= standardTagBits;
                        if ((this.version < 0x350000L || (standardTagBits & 0x400000000000L) == 0L) && (standardTagBits & 0x20600FF840000000L) == 0L) break block11;
                    }
                    if (newInfos == null) {
                        newInfos = new AnnotationInfo[numberOfAnnotations - i];
                    }
                    newInfos[newInfoCount++] = newInfo;
                }
                ++i;
            }
            if (newInfos == null) {
                return;
            }
            if (this.annotations == null) {
                if (newInfoCount != newInfos.length) {
                    AnnotationInfo[] annotationInfoArray = newInfos;
                    newInfos = new AnnotationInfo[newInfoCount];
                    System.arraycopy(annotationInfoArray, 0, newInfos, 0, newInfoCount);
                }
                this.annotations = newInfos;
            } else {
                int length = this.annotations.length;
                AnnotationInfo[] temp = new AnnotationInfo[length + newInfoCount];
                System.arraycopy(this.annotations, 0, temp, 0, length);
                System.arraycopy(newInfos, 0, temp, length, newInfoCount);
                this.annotations = temp;
            }
        }
    }

    private void decodeTypeAnnotations(int offset, boolean runtimeVisible) {
        int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            TypeAnnotationInfo[] newInfos = null;
            newInfos = new TypeAnnotationInfo[numberOfAnnotations];
            int i = 0;
            while (i < numberOfAnnotations) {
                TypeAnnotationInfo newInfo = new TypeAnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                newInfos[i] = newInfo;
                ++i;
            }
            if (this.typeAnnotations == null) {
                this.typeAnnotations = newInfos;
            } else {
                int length = this.typeAnnotations.length;
                TypeAnnotationInfo[] temp = new TypeAnnotationInfo[length + numberOfAnnotations];
                System.arraycopy(this.typeAnnotations, 0, temp, 0, length);
                System.arraycopy(newInfos, 0, temp, length, numberOfAnnotations);
                this.typeAnnotations = temp;
            }
        }
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.typeAnnotations;
    }

    private char[] getConstantClassNameAt(int constantPoolIndex) {
        int utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[constantPoolIndex] + 1)];
        return this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
    }

    public int[] getConstantPoolOffsets() {
        return this.constantPoolOffsets;
    }

    @Override
    public char[] getEnclosingMethod() {
        if (this.enclosingNameAndTypeIndex <= 0) {
            return null;
        }
        if (this.enclosingMethod == null) {
            StringBuffer buffer = new StringBuffer();
            int nameAndTypeOffset = this.constantPoolOffsets[this.enclosingNameAndTypeIndex];
            int utf8Offset = this.constantPoolOffsets[this.u2At(nameAndTypeOffset + 1)];
            buffer.append(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1)));
            utf8Offset = this.constantPoolOffsets[this.u2At(nameAndTypeOffset + 3)];
            buffer.append(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1)));
            this.enclosingMethod = String.valueOf(buffer).toCharArray();
        }
        return this.enclosingMethod;
    }

    @Override
    public char[] getEnclosingTypeName() {
        return this.enclosingTypeName;
    }

    @Override
    public IBinaryField[] getFields() {
        return this.fields;
    }

    @Override
    public char[] getModule() {
        return this.moduleName;
    }

    public IBinaryModule getModuleDeclaration() {
        return this.moduleDeclaration;
    }

    @Override
    public char[] getFileName() {
        return this.classFileName;
    }

    @Override
    public char[] getGenericSignature() {
        return this.signature;
    }

    public char[] getInnerSourceName() {
        if (this.innerInfo != null) {
            return this.innerInfo.getSourceName();
        }
        return null;
    }

    @Override
    public char[][] getInterfaceNames() {
        return this.interfaceNames;
    }

    @Override
    public char[][] getPermittedSubtypeNames() {
        return this.permittedSubtypesNames;
    }

    @Override
    public IBinaryNestedType[] getMemberTypes() {
        if (this.innerInfos == null) {
            return null;
        }
        int length = this.innerInfos.length - (this.innerInfo != null ? 1 : 0);
        if (length != 0) {
            IBinaryNestedType[] memberTypes = new IBinaryNestedType[length];
            int memberTypeIndex = 0;
            InnerClassInfo[] innerClassInfoArray = this.innerInfos;
            int n = this.innerInfos.length;
            int n2 = 0;
            while (n2 < n) {
                InnerClassInfo currentInnerInfo = innerClassInfoArray[n2];
                int outerClassNameIdx = currentInnerInfo.outerClassNameIndex;
                int innerNameIndex = currentInnerInfo.innerNameIndex;
                if (outerClassNameIdx != 0 && innerNameIndex != 0 && outerClassNameIdx == this.classNameIndex && currentInnerInfo.getSourceName().length != 0) {
                    memberTypes[memberTypeIndex++] = currentInnerInfo;
                }
                ++n2;
            }
            if (memberTypeIndex == 0) {
                return null;
            }
            if (memberTypeIndex != memberTypes.length) {
                IBinaryNestedType[] iBinaryNestedTypeArray = memberTypes;
                memberTypes = new IBinaryNestedType[memberTypeIndex];
                System.arraycopy(iBinaryNestedTypeArray, 0, memberTypes, 0, memberTypeIndex);
            }
            return memberTypes;
        }
        return null;
    }

    @Override
    public IBinaryMethod[] getMethods() {
        return this.methods;
    }

    @Override
    public char[][][] getMissingTypeNames() {
        return this.missingTypeNames;
    }

    @Override
    public int getModifiers() {
        int modifiers = this.innerInfo != null ? this.innerInfo.getModifiers() | this.accessFlags & 0x100000 | this.accessFlags & 0x1000 : this.accessFlags;
        if (this.permittedSubtypesCount > 0) {
            modifiers |= 0x10000000;
        }
        return modifiers;
    }

    @Override
    public char[] getName() {
        return this.className;
    }

    @Override
    public char[] getSourceName() {
        if (this.sourceName != null) {
            return this.sourceName;
        }
        char[] name = this.getInnerSourceName();
        if (name == null) {
            name = this.getName();
            int start = this.isAnonymous() ? CharOperation.indexOf('$', name, CharOperation.lastIndexOf('/', name) + 1) + 1 : CharOperation.lastIndexOf('/', name) + 1;
            if (start > 0) {
                char[] newName = new char[name.length - start];
                System.arraycopy(name, start, newName, 0, newName.length);
                name = newName;
            }
        }
        this.sourceName = name;
        return name;
    }

    @Override
    public char[] getSuperclassName() {
        return this.superclassName;
    }

    @Override
    public long getTagBits() {
        return this.tagBits;
    }

    public long getVersion() {
        return this.version;
    }

    /*
     * Unable to fully structure code
     */
    private boolean hasNonSyntheticFieldChanges(FieldInfo[] currentFieldInfos, FieldInfo[] otherFieldInfos) {
        length1 = currentFieldInfos == null ? 0 : currentFieldInfos.length;
        length2 = otherFieldInfos == null ? 0 : otherFieldInfos.length;
        index1 = 0;
        index2 = 0;
        ** GOTO lbl13
        block0: while (++index1 < length1) {
            while (!currentFieldInfos[index1].isSynthetic()) {
                while (otherFieldInfos[index2].isSynthetic()) {
                    if (++index2 >= length2) break block0;
                }
                if (this.hasStructuralFieldChanges(currentFieldInfos[index1++], otherFieldInfos[index2++])) {
                    return true;
                }
lbl13:
                // 3 sources

                if (index1 < length1 && index2 < length2) continue;
            }
        }
        while (index1 < length1) {
            if (currentFieldInfos[index1++].isSynthetic()) continue;
            return true;
        }
        while (index2 < length2) {
            if (otherFieldInfos[index2++].isSynthetic()) continue;
            return true;
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    private boolean hasNonSyntheticMethodChanges(MethodInfo[] currentMethodInfos, MethodInfo[] otherMethodInfos) {
        length1 = currentMethodInfos == null ? 0 : currentMethodInfos.length;
        length2 = otherMethodInfos == null ? 0 : otherMethodInfos.length;
        index1 = 0;
        index2 = 0;
        ** GOTO lbl13
        block0: while (++index1 < length1) {
            while (!(m = currentMethodInfos[index1]).isSynthetic() && !m.isClinit()) {
                while ((m = otherMethodInfos[index2]).isSynthetic() || m.isClinit()) {
                    if (++index2 >= length2) break block0;
                }
                if (this.hasStructuralMethodChanges(currentMethodInfos[index1++], otherMethodInfos[index2++])) {
                    return true;
                }
lbl13:
                // 3 sources

                if (index1 < length1 && index2 < length2) continue;
            }
        }
        while (index1 < length1) {
            if ((m = currentMethodInfos[index1++]).isSynthetic() || m.isClinit()) continue;
            return true;
        }
        while (index2 < length2) {
            if ((m = otherMethodInfos[index2++]).isSynthetic() || m.isClinit()) continue;
            return true;
        }
        return false;
    }

    public boolean hasStructuralChanges(byte[] newBytes) {
        return this.hasStructuralChanges(newBytes, true, true);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean hasStructuralChanges(byte[] newBytes, boolean orderRequired, boolean excludesSynthetic) {
        try {
            int i;
            Object[] otherMethodInfos;
            int i2;
            Object[] otherFieldInfos;
            IBinaryNestedType[] otherMemberTypes;
            IBinaryNestedType[] currentMemberTypes;
            char[][] newPermittedSubtypeNames;
            ClassFileReader newClassFile = new ClassFileReader(newBytes, this.classFileName);
            if (this.getModifiers() != newClassFile.getModifiers()) {
                return true;
            }
            long OnlyStructuralTagBits = 2333005311180406784L;
            if ((this.getTagBits() & OnlyStructuralTagBits) != (newClassFile.getTagBits() & OnlyStructuralTagBits)) {
                return true;
            }
            if (this.hasStructuralAnnotationChanges(this.getAnnotations(), newClassFile.getAnnotations())) {
                return true;
            }
            if (this.version >= 0x340000L && this.hasStructuralTypeAnnotationChanges(this.getTypeAnnotations(), newClassFile.getTypeAnnotations())) {
                return true;
            }
            if (!CharOperation.equals(this.getGenericSignature(), newClassFile.getGenericSignature())) {
                return true;
            }
            if (!CharOperation.equals(this.getSuperclassName(), newClassFile.getSuperclassName())) {
                return true;
            }
            char[][] newInterfacesNames = newClassFile.getInterfaceNames();
            if (this.interfaceNames != newInterfacesNames) {
                int newInterfacesLength;
                int n = newInterfacesLength = newInterfacesNames == null ? 0 : newInterfacesNames.length;
                if (newInterfacesLength != this.interfacesCount) {
                    return true;
                }
                int i3 = 0;
                int max = this.interfacesCount;
                while (i3 < max) {
                    if (!CharOperation.equals(this.interfaceNames[i3], newInterfacesNames[i3])) {
                        return true;
                    }
                    ++i3;
                }
            }
            if (this.permittedSubtypesNames != (newPermittedSubtypeNames = newClassFile.getPermittedSubtypeNames())) {
                int newPermittedSubtypesLength;
                int n = newPermittedSubtypesLength = newPermittedSubtypeNames == null ? 0 : newPermittedSubtypeNames.length;
                if (newPermittedSubtypesLength != this.permittedSubtypesCount) {
                    return true;
                }
                int i4 = 0;
                int max = this.permittedSubtypesCount;
                while (i4 < max) {
                    if (!CharOperation.equals(this.permittedSubtypesNames[i4], newPermittedSubtypeNames[i4])) {
                        return true;
                    }
                    ++i4;
                }
            }
            if ((currentMemberTypes = this.getMemberTypes()) != (otherMemberTypes = newClassFile.getMemberTypes())) {
                int otherMemberTypeLength;
                int currentMemberTypeLength = currentMemberTypes == null ? 0 : currentMemberTypes.length;
                int n = otherMemberTypeLength = otherMemberTypes == null ? 0 : otherMemberTypes.length;
                if (currentMemberTypeLength != otherMemberTypeLength) {
                    return true;
                }
                int i5 = 0;
                while (i5 < currentMemberTypeLength) {
                    if (!CharOperation.equals(currentMemberTypes[i5].getName(), otherMemberTypes[i5].getName())) return true;
                    if (currentMemberTypes[i5].getModifiers() != otherMemberTypes[i5].getModifiers()) {
                        return true;
                    }
                    ++i5;
                }
            }
            int otherFieldInfosLength = (otherFieldInfos = (FieldInfo[])newClassFile.getFields()) == null ? 0 : otherFieldInfos.length;
            boolean compareFields = true;
            if (this.fieldsCount == otherFieldInfosLength) {
                i2 = 0;
                while (i2 < this.fieldsCount && !this.hasStructuralFieldChanges(this.fields[i2], otherFieldInfos[i2])) {
                    ++i2;
                }
                compareFields = i2 != this.fieldsCount;
                if (compareFields && !orderRequired && !excludesSynthetic) {
                    return true;
                }
            }
            if (compareFields) {
                if (this.fieldsCount != otherFieldInfosLength && !excludesSynthetic) {
                    return true;
                }
                if (orderRequired) {
                    if (this.fieldsCount != 0) {
                        Arrays.sort(this.fields);
                    }
                    if (otherFieldInfosLength != 0) {
                        Arrays.sort(otherFieldInfos);
                    }
                }
                if (excludesSynthetic) {
                    if (this.hasNonSyntheticFieldChanges(this.fields, (FieldInfo[])otherFieldInfos)) {
                        return true;
                    }
                } else {
                    i2 = 0;
                    while (i2 < this.fieldsCount) {
                        if (this.hasStructuralFieldChanges(this.fields[i2], (FieldInfo)otherFieldInfos[i2])) {
                            return true;
                        }
                        ++i2;
                    }
                }
            }
            int otherMethodInfosLength = (otherMethodInfos = (MethodInfo[])newClassFile.getMethods()) == null ? 0 : otherMethodInfos.length;
            boolean compareMethods = true;
            if (this.methodsCount == otherMethodInfosLength) {
                i = 0;
                while (i < this.methodsCount && !this.hasStructuralMethodChanges(this.methods[i], otherMethodInfos[i])) {
                    ++i;
                }
                compareMethods = i != this.methodsCount;
                if (compareMethods && !orderRequired && !excludesSynthetic) {
                    return true;
                }
            }
            if (compareMethods) {
                if (this.methodsCount != otherMethodInfosLength && !excludesSynthetic) {
                    return true;
                }
                if (orderRequired) {
                    if (this.methodsCount != 0) {
                        Arrays.sort(this.methods);
                    }
                    if (otherMethodInfosLength != 0) {
                        Arrays.sort(otherMethodInfos);
                    }
                }
                if (excludesSynthetic) {
                    if (this.hasNonSyntheticMethodChanges(this.methods, (MethodInfo[])otherMethodInfos)) {
                        return true;
                    }
                } else {
                    i = 0;
                    while (i < this.methodsCount) {
                        if (this.hasStructuralMethodChanges(this.methods[i], (MethodInfo)otherMethodInfos[i])) {
                            return true;
                        }
                        ++i;
                    }
                }
            }
            char[][][] missingTypes = this.getMissingTypeNames();
            char[][][] newMissingTypes = newClassFile.getMissingTypeNames();
            if (missingTypes == null) {
                if (newMissingTypes == null) return false;
                return true;
            }
            if (newMissingTypes == null) {
                return true;
            }
            int length = missingTypes.length;
            if (length != newMissingTypes.length) {
                return true;
            }
            int i6 = 0;
            while (true) {
                if (i6 >= length) {
                    return false;
                }
                if (!CharOperation.equals(missingTypes[i6], newMissingTypes[i6])) {
                    return true;
                }
                ++i6;
            }
        }
        catch (ClassFormatException classFormatException) {
            return true;
        }
    }

    private boolean hasStructuralAnnotationChanges(IBinaryAnnotation[] currentAnnotations, IBinaryAnnotation[] otherAnnotations) {
        int otherAnnotationsLength;
        if (currentAnnotations == otherAnnotations) {
            return false;
        }
        int currentAnnotationsLength = currentAnnotations == null ? 0 : currentAnnotations.length;
        int n = otherAnnotationsLength = otherAnnotations == null ? 0 : otherAnnotations.length;
        if (currentAnnotationsLength != otherAnnotationsLength) {
            return true;
        }
        int i = 0;
        while (i < currentAnnotationsLength) {
            Boolean match = this.matchAnnotations(currentAnnotations[i], otherAnnotations[i]);
            if (match != null) {
                return match;
            }
            ++i;
        }
        return false;
    }

    private Boolean matchAnnotations(IBinaryAnnotation currentAnnotation, IBinaryAnnotation otherAnnotation) {
        int otherPairsLength;
        if (!CharOperation.equals(currentAnnotation.getTypeName(), otherAnnotation.getTypeName())) {
            return true;
        }
        IBinaryElementValuePair[] currentPairs = currentAnnotation.getElementValuePairs();
        IBinaryElementValuePair[] otherPairs = otherAnnotation.getElementValuePairs();
        int currentPairsLength = currentPairs == null ? 0 : currentPairs.length;
        int n = otherPairsLength = otherPairs == null ? 0 : otherPairs.length;
        if (currentPairsLength != otherPairsLength) {
            return Boolean.TRUE;
        }
        int j = 0;
        while (j < currentPairsLength) {
            if (!CharOperation.equals(currentPairs[j].getName(), otherPairs[j].getName())) {
                return Boolean.TRUE;
            }
            Object value = currentPairs[j].getValue();
            Object value2 = otherPairs[j].getValue();
            if (value instanceof Object[]) {
                Object[] currentValues = (Object[])value;
                if (value2 instanceof Object[]) {
                    int length = currentValues.length;
                    Object[] currentValues2 = (Object[])value2;
                    if (length != currentValues2.length) {
                        return Boolean.TRUE;
                    }
                    int n2 = 0;
                    while (n2 < length) {
                        if (!currentValues[n2].equals(currentValues2[n2])) {
                            return Boolean.TRUE;
                        }
                        ++n2;
                    }
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
            if (!value.equals(value2)) {
                return Boolean.TRUE;
            }
            ++j;
        }
        return null;
    }

    private boolean hasStructuralFieldChanges(FieldInfo currentFieldInfo, FieldInfo otherFieldInfo) {
        if (!CharOperation.equals(currentFieldInfo.getGenericSignature(), otherFieldInfo.getGenericSignature())) {
            return true;
        }
        if (currentFieldInfo.getModifiers() != otherFieldInfo.getModifiers()) {
            return true;
        }
        if ((currentFieldInfo.getTagBits() & 0x400000000000L) != (otherFieldInfo.getTagBits() & 0x400000000000L)) {
            return true;
        }
        if (this.hasStructuralAnnotationChanges(currentFieldInfo.getAnnotations(), otherFieldInfo.getAnnotations())) {
            return true;
        }
        if (this.version >= 0x340000L && this.hasStructuralTypeAnnotationChanges(currentFieldInfo.getTypeAnnotations(), otherFieldInfo.getTypeAnnotations())) {
            return true;
        }
        if (!CharOperation.equals(currentFieldInfo.getName(), otherFieldInfo.getName())) {
            return true;
        }
        if (!CharOperation.equals(currentFieldInfo.getTypeName(), otherFieldInfo.getTypeName())) {
            return true;
        }
        if (currentFieldInfo.hasConstant() != otherFieldInfo.hasConstant()) {
            return true;
        }
        if (currentFieldInfo.hasConstant()) {
            Constant currentConstant = currentFieldInfo.getConstant();
            Constant otherConstant = otherFieldInfo.getConstant();
            if (currentConstant.typeID() != otherConstant.typeID()) {
                return true;
            }
            if (!currentConstant.getClass().equals(otherConstant.getClass())) {
                return true;
            }
            switch (currentConstant.typeID()) {
                case 10: {
                    return currentConstant.intValue() != otherConstant.intValue();
                }
                case 3: {
                    return currentConstant.byteValue() != otherConstant.byteValue();
                }
                case 4: {
                    return currentConstant.shortValue() != otherConstant.shortValue();
                }
                case 2: {
                    return currentConstant.charValue() != otherConstant.charValue();
                }
                case 7: {
                    return currentConstant.longValue() != otherConstant.longValue();
                }
                case 9: {
                    return currentConstant.floatValue() != otherConstant.floatValue();
                }
                case 8: {
                    return currentConstant.doubleValue() != otherConstant.doubleValue();
                }
                case 5: {
                    return currentConstant.booleanValue() ^ otherConstant.booleanValue();
                }
                case 11: {
                    return !currentConstant.stringValue().equals(otherConstant.stringValue());
                }
            }
        }
        return false;
    }

    private boolean hasStructuralMethodChanges(MethodInfo currentMethodInfo, MethodInfo otherMethodInfo) {
        char[][] otherThrownExceptions;
        int otherAnnotatedParamsCount;
        if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature())) {
            return true;
        }
        if (currentMethodInfo.getModifiers() != otherMethodInfo.getModifiers()) {
            return true;
        }
        if ((currentMethodInfo.getTagBits() & 0x400000000000L) != (otherMethodInfo.getTagBits() & 0x400000000000L)) {
            return true;
        }
        if (this.hasStructuralAnnotationChanges(currentMethodInfo.getAnnotations(), otherMethodInfo.getAnnotations())) {
            return true;
        }
        int currentAnnotatedParamsCount = currentMethodInfo.getAnnotatedParametersCount();
        if (currentAnnotatedParamsCount != (otherAnnotatedParamsCount = otherMethodInfo.getAnnotatedParametersCount())) {
            return true;
        }
        int i = 0;
        while (i < currentAnnotatedParamsCount) {
            if (this.hasStructuralAnnotationChanges(currentMethodInfo.getParameterAnnotations(i, this.classFileName), otherMethodInfo.getParameterAnnotations(i, this.classFileName))) {
                return true;
            }
            ++i;
        }
        if (this.version >= 0x340000L && this.hasStructuralTypeAnnotationChanges(currentMethodInfo.getTypeAnnotations(), otherMethodInfo.getTypeAnnotations())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getSelector(), otherMethodInfo.getSelector())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getMethodDescriptor(), otherMethodInfo.getMethodDescriptor())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature())) {
            return true;
        }
        char[][] currentThrownExceptions = currentMethodInfo.getExceptionTypeNames();
        if (currentThrownExceptions != (otherThrownExceptions = otherMethodInfo.getExceptionTypeNames())) {
            int otherThrownExceptionsLength;
            int currentThrownExceptionsLength = currentThrownExceptions == null ? 0 : currentThrownExceptions.length;
            int n = otherThrownExceptionsLength = otherThrownExceptions == null ? 0 : otherThrownExceptions.length;
            if (currentThrownExceptionsLength != otherThrownExceptionsLength) {
                return true;
            }
            int k = 0;
            while (k < currentThrownExceptionsLength) {
                if (!CharOperation.equals(currentThrownExceptions[k], otherThrownExceptions[k])) {
                    return true;
                }
                ++k;
            }
        }
        return false;
    }

    private boolean hasStructuralTypeAnnotationChanges(IBinaryTypeAnnotation[] currentTypeAnnotations, IBinaryTypeAnnotation[] otherTypeAnnotations) {
        int n;
        int n2;
        IBinaryTypeAnnotation[] iBinaryTypeAnnotationArray;
        if (otherTypeAnnotations != null) {
            int len = otherTypeAnnotations.length;
            IBinaryTypeAnnotation[] iBinaryTypeAnnotationArray2 = otherTypeAnnotations;
            otherTypeAnnotations = new IBinaryTypeAnnotation[len];
            System.arraycopy(iBinaryTypeAnnotationArray2, 0, otherTypeAnnotations, 0, len);
        }
        if (currentTypeAnnotations != null) {
            iBinaryTypeAnnotationArray = currentTypeAnnotations;
            n2 = currentTypeAnnotations.length;
            n = 0;
            while (n < n2) {
                block10: {
                    IBinaryTypeAnnotation currentAnnotation = iBinaryTypeAnnotationArray[n];
                    if (this.affectsSignature(currentAnnotation)) {
                        if (otherTypeAnnotations == null) {
                            return true;
                        }
                        int i = 0;
                        while (i < otherTypeAnnotations.length) {
                            IBinaryTypeAnnotation otherAnnotation = otherTypeAnnotations[i];
                            if (otherAnnotation != null && this.matchAnnotations(currentAnnotation.getAnnotation(), otherAnnotation.getAnnotation()) == Boolean.TRUE) {
                                otherTypeAnnotations[i] = null;
                                break block10;
                            }
                            ++i;
                        }
                        return true;
                    }
                }
                ++n;
            }
        }
        if (otherTypeAnnotations != null) {
            iBinaryTypeAnnotationArray = otherTypeAnnotations;
            n2 = otherTypeAnnotations.length;
            n = 0;
            while (n < n2) {
                IBinaryTypeAnnotation otherAnnotation = iBinaryTypeAnnotationArray[n];
                if (this.affectsSignature(otherAnnotation)) {
                    return true;
                }
                ++n;
            }
        }
        return false;
    }

    private boolean affectsSignature(IBinaryTypeAnnotation typeAnnotation) {
        if (typeAnnotation == null) {
            return false;
        }
        int targetType = typeAnnotation.getTargetType();
        return targetType < 64 || targetType > 75;
    }

    private void initialize() throws ClassFormatException {
        try {
            int i = 0;
            int max = this.fieldsCount;
            while (i < max) {
                this.fields[i].initialize();
                ++i;
            }
            i = 0;
            max = this.methodsCount;
            while (i < max) {
                this.methods[i].initialize();
                ++i;
            }
            if (this.innerInfos != null) {
                i = 0;
                max = this.innerInfos.length;
                while (i < max) {
                    this.innerInfos[i].initialize();
                    ++i;
                }
            }
            if (this.annotations != null) {
                i = 0;
                max = this.annotations.length;
                while (i < max) {
                    this.annotations[i].initialize();
                    ++i;
                }
            }
            this.getEnclosingMethod();
            this.reset();
        }
        catch (RuntimeException e) {
            ClassFormatException exception = new ClassFormatException(e, this.classFileName);
            throw exception;
        }
    }

    @Override
    public boolean isAnonymous() {
        if (this.innerInfo == null) {
            return false;
        }
        char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName == null || innerSourceName.length == 0;
    }

    @Override
    public boolean isBinaryType() {
        return true;
    }

    @Override
    public boolean isLocal() {
        if (this.innerInfo == null) {
            return false;
        }
        if (this.innerInfo.getEnclosingTypeName() != null) {
            return false;
        }
        char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName != null && innerSourceName.length > 0;
    }

    @Override
    public boolean isMember() {
        if (this.innerInfo == null) {
            return false;
        }
        if (this.innerInfo.getEnclosingTypeName() == null) {
            return false;
        }
        char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName != null && innerSourceName.length > 0;
    }

    public boolean isNestedType() {
        return this.innerInfo != null;
    }

    @Override
    public char[] sourceFileName() {
        return this.sourceFileName;
    }

    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter print = new PrintWriter(out);
        print.println(String.valueOf(this.getClass().getName()) + "{");
        print.println(" this.className: " + new String(this.getName()));
        print.println(" this.superclassName: " + (this.getSuperclassName() == null ? "null" : new String(this.getSuperclassName())));
        if (this.moduleName != null) {
            print.println(" this.moduleName: " + new String(this.moduleName));
        }
        print.println(" access_flags: " + ClassFileReader.printTypeModifiers(this.accessFlags()) + "(" + this.accessFlags() + ")");
        print.flush();
        return out.toString();
    }

    @Override
    public boolean isRecord() {
        return this.isRecord;
    }

    @Override
    public IRecordComponent[] getRecordComponents() {
        return this.recordComponents;
    }
}

