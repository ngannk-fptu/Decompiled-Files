/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationClassRef;
import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefOrTypeVariableSignature;
import io.github.classgraph.ClassTypeSignature;
import io.github.classgraph.ClasspathElement;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.HierarchicalTypeSignature;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodTypeSignature;
import io.github.classgraph.ModuleInfo;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.ReferenceTypeSignature;
import io.github.classgraph.Resource;
import io.github.classgraph.Scanner;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.StringUtils;

class Classfile {
    private ClassfileReader reader;
    private final ClasspathElement classpathElement;
    private final List<ClasspathElement> classpathOrder;
    private final String relativePath;
    private final Resource classfileResource;
    private final ConcurrentHashMap<String, String> stringInternMap;
    private String className;
    private int minorVersion;
    private int majorVersion;
    private final boolean isExternalClass;
    private int classModifiers;
    private boolean isInterface;
    private boolean isRecord;
    private boolean isAnnotation;
    private String superclassName;
    private List<String> implementedInterfaces;
    private AnnotationInfoList classAnnotations;
    private String fullyQualifiedDefiningMethodName;
    private List<ClassContainment> classContainmentEntries;
    private AnnotationParameterValueList annotationParamDefaultValues;
    private Set<String> refdClassNames;
    private FieldInfoList fieldInfoList;
    private MethodInfoList methodInfoList;
    private String typeSignatureStr;
    private String sourceFile;
    private List<ClassTypeAnnotationDecorator> classTypeAnnotationDecorators;
    private final Set<String> acceptedClassNamesFound;
    private final Set<String> classNamesScheduledForExtendedScanning;
    private List<Scanner.ClassfileScanWorkUnit> additionalWorkUnits;
    private final ScanSpec scanSpec;
    private int cpCount;
    private int[] entryOffset;
    private int[] entryTag;
    private int[] indirectStringRefs;
    private static final AnnotationInfo[] NO_ANNOTATIONS = new AnnotationInfo[0];

    private void scheduleScanningIfExternalClass(String className, String relationship, LogNode log) {
        if (className != null && !className.equals("java.lang.Object") && !this.acceptedClassNamesFound.contains(className) && this.classNamesScheduledForExtendedScanning.add(className)) {
            if (this.scanSpec.classAcceptReject.isRejected(className)) {
                if (log != null) {
                    log.log("Cannot extend scanning upwards to external " + relationship + " " + className + ", since it is rejected");
                }
            } else {
                String classfilePath = JarUtils.classNameToClassfilePath(className);
                Resource classResource = this.classpathElement.getResource(classfilePath);
                ClasspathElement foundInClasspathElt = null;
                if (classResource != null) {
                    foundInClasspathElt = this.classpathElement;
                } else {
                    for (ClasspathElement classpathOrderElt : this.classpathOrder) {
                        if (classpathOrderElt == this.classpathElement || (classResource = classpathOrderElt.getResource(classfilePath)) == null) continue;
                        foundInClasspathElt = classpathOrderElt;
                        break;
                    }
                }
                if (classResource != null) {
                    if (log != null) {
                        classResource.scanLog = log.log("Extending scanning to external " + relationship + (foundInClasspathElt == this.classpathElement ? " in same classpath element" : " in classpath element " + foundInClasspathElt) + ": " + className);
                    }
                    if (this.additionalWorkUnits == null) {
                        this.additionalWorkUnits = new ArrayList<Scanner.ClassfileScanWorkUnit>();
                    }
                    this.additionalWorkUnits.add(new Scanner.ClassfileScanWorkUnit(foundInClasspathElt, classResource, true));
                } else if (log != null) {
                    log.log("External " + relationship + " " + className + " was not found in non-rejected packages -- cannot extend scanning to this class");
                }
            }
        }
    }

    private void extendScanningUpwardsFromAnnotationParameterValues(Object annotationParamVal, LogNode log) {
        block2: {
            block5: {
                block4: {
                    block3: {
                        if (annotationParamVal == null) break block2;
                        if (!(annotationParamVal instanceof AnnotationInfo)) break block3;
                        AnnotationInfo annotationInfo = (AnnotationInfo)annotationParamVal;
                        this.scheduleScanningIfExternalClass(annotationInfo.getClassName(), "annotation class", log);
                        for (AnnotationParameterValue apv : annotationInfo.getParameterValues()) {
                            this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
                        }
                        break block2;
                    }
                    if (!(annotationParamVal instanceof AnnotationEnumValue)) break block4;
                    this.scheduleScanningIfExternalClass(((AnnotationEnumValue)annotationParamVal).getClassName(), "enum class", log);
                    break block2;
                }
                if (!(annotationParamVal instanceof AnnotationClassRef)) break block5;
                this.scheduleScanningIfExternalClass(((AnnotationClassRef)annotationParamVal).getClassName(), "class ref", log);
                break block2;
            }
            if (!annotationParamVal.getClass().isArray()) break block2;
            int n = Array.getLength(annotationParamVal);
            for (int i = 0; i < n; ++i) {
                this.extendScanningUpwardsFromAnnotationParameterValues(Array.get(annotationParamVal, i), log);
            }
        }
    }

    private void extendScanningUpwards(LogNode log) {
        if (this.superclassName != null) {
            this.scheduleScanningIfExternalClass(this.superclassName, "superclass", log);
        }
        if (this.implementedInterfaces != null) {
            for (String interfaceName : this.implementedInterfaces) {
                this.scheduleScanningIfExternalClass(interfaceName, "interface", log);
            }
        }
        if (this.classAnnotations != null) {
            for (AnnotationInfo annotationInfo : this.classAnnotations) {
                this.scheduleScanningIfExternalClass(annotationInfo.getName(), "class annotation", log);
                this.extendScanningUpwardsFromAnnotationParameterValues(annotationInfo, log);
            }
        }
        if (this.annotationParamDefaultValues != null) {
            for (AnnotationParameterValue apv : this.annotationParamDefaultValues) {
                this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
            }
        }
        if (this.methodInfoList != null) {
            for (MethodInfo methodInfo : this.methodInfoList) {
                if (methodInfo.annotationInfo != null) {
                    for (AnnotationInfo methodAnnotationInfo : methodInfo.annotationInfo) {
                        this.scheduleScanningIfExternalClass(methodAnnotationInfo.getName(), "method annotation", log);
                        this.extendScanningUpwardsFromAnnotationParameterValues(methodAnnotationInfo, log);
                    }
                    if (methodInfo.parameterAnnotationInfo != null && methodInfo.parameterAnnotationInfo.length > 0) {
                        for (AnnotationInfo[] paramAnnInfoArr : methodInfo.parameterAnnotationInfo) {
                            if (paramAnnInfoArr == null || paramAnnInfoArr.length <= 0) continue;
                            for (Object object : paramAnnInfoArr) {
                                this.scheduleScanningIfExternalClass(((AnnotationInfo)object).getName(), "method parameter annotation", log);
                                this.extendScanningUpwardsFromAnnotationParameterValues(object, log);
                            }
                        }
                    }
                }
                if (methodInfo.getThrownExceptionNames() == null) continue;
                for (String thrownExceptionName : methodInfo.getThrownExceptionNames()) {
                    this.scheduleScanningIfExternalClass(thrownExceptionName, "method throws", log);
                }
            }
        }
        if (this.fieldInfoList != null) {
            for (FieldInfo fieldInfo : this.fieldInfoList) {
                if (fieldInfo.annotationInfo == null) continue;
                for (AnnotationInfo fieldAnnotationInfo : fieldInfo.annotationInfo) {
                    this.scheduleScanningIfExternalClass(fieldAnnotationInfo.getName(), "field annotation", log);
                    this.extendScanningUpwardsFromAnnotationParameterValues(fieldAnnotationInfo, log);
                }
            }
        }
        if (this.classContainmentEntries != null) {
            for (ClassContainment classContainmentEntry : this.classContainmentEntries) {
                if (!classContainmentEntry.innerClassName.equals(this.className)) continue;
                this.scheduleScanningIfExternalClass(classContainmentEntry.outerClassName, "outer class", log);
            }
        }
    }

    void link(Map<String, ClassInfo> classNameToClassInfo, Map<String, PackageInfo> packageNameToPackageInfo, Map<String, ModuleInfo> moduleNameToModuleInfo) {
        String moduleName;
        boolean isModuleDescriptor = false;
        boolean isPackageDescriptor = false;
        ClassInfo classInfo = null;
        if (this.className.equals("module-info")) {
            isModuleDescriptor = true;
        } else if (this.className.equals("package-info") || this.className.endsWith(".package-info")) {
            isPackageDescriptor = true;
        } else {
            classInfo = ClassInfo.addScannedClass(this.className, this.classModifiers, this.isExternalClass, classNameToClassInfo, this.classpathElement, this.classfileResource);
            classInfo.setClassfileVersion(this.minorVersion, this.majorVersion);
            classInfo.setModifiers(this.classModifiers);
            classInfo.setIsInterface(this.isInterface);
            classInfo.setIsAnnotation(this.isAnnotation);
            classInfo.setIsRecord(this.isRecord);
            classInfo.setSourceFile(this.sourceFile);
            if (this.superclassName != null) {
                classInfo.addSuperclass(this.superclassName, classNameToClassInfo);
            }
            if (this.implementedInterfaces != null) {
                for (String interfaceName : this.implementedInterfaces) {
                    classInfo.addImplementedInterface(interfaceName, classNameToClassInfo);
                }
            }
            if (this.classAnnotations != null) {
                for (AnnotationInfo classAnnotation : this.classAnnotations) {
                    classInfo.addClassAnnotation(classAnnotation, classNameToClassInfo);
                }
            }
            if (this.classContainmentEntries != null) {
                ClassInfo.addClassContainment(this.classContainmentEntries, classNameToClassInfo);
            }
            if (this.annotationParamDefaultValues != null) {
                classInfo.addAnnotationParamDefaultValues(this.annotationParamDefaultValues);
            }
            if (this.fullyQualifiedDefiningMethodName != null) {
                classInfo.addFullyQualifiedDefiningMethodName(this.fullyQualifiedDefiningMethodName);
            }
            if (this.fieldInfoList != null) {
                classInfo.addFieldInfo(this.fieldInfoList, classNameToClassInfo);
            }
            if (this.methodInfoList != null) {
                classInfo.addMethodInfo(this.methodInfoList, classNameToClassInfo);
            }
            if (this.typeSignatureStr != null) {
                classInfo.setTypeSignature(this.typeSignatureStr);
            }
            if (this.refdClassNames != null) {
                classInfo.addReferencedClassNames(this.refdClassNames);
            }
            if (this.classTypeAnnotationDecorators != null) {
                classInfo.addTypeDecorators(this.classTypeAnnotationDecorators);
            }
        }
        PackageInfo packageInfo = null;
        if (!isModuleDescriptor) {
            String packageName = PackageInfo.getParentPackageName(this.className);
            packageInfo = PackageInfo.getOrCreatePackage(packageName, packageNameToPackageInfo, this.scanSpec);
            if (isPackageDescriptor) {
                packageInfo.addAnnotations(this.classAnnotations);
            } else if (classInfo != null) {
                packageInfo.addClassInfo(classInfo);
                classInfo.packageInfo = packageInfo;
            }
        }
        if ((moduleName = this.classpathElement.getModuleName()) != null) {
            ModuleInfo moduleInfo = moduleNameToModuleInfo.get(moduleName);
            if (moduleInfo == null) {
                moduleInfo = new ModuleInfo(this.classfileResource.getModuleRef(), this.classpathElement);
                moduleNameToModuleInfo.put(moduleName, moduleInfo);
            }
            if (isModuleDescriptor) {
                moduleInfo.addAnnotations(this.classAnnotations);
            }
            if (classInfo != null) {
                moduleInfo.addClassInfo(classInfo);
                classInfo.moduleInfo = moduleInfo;
            }
            if (packageInfo != null) {
                moduleInfo.addPackageInfo(packageInfo);
            }
        }
    }

    private String intern(String str) {
        if (str == null) {
            return null;
        }
        String interned = this.stringInternMap.putIfAbsent(str, str);
        if (interned != null) {
            return interned;
        }
        return str;
    }

    private int getConstantPoolStringOffset(int cpIdx, int subFieldIdx) throws ClassfileFormatException {
        int cpIdxToUse;
        if (cpIdx < 1 || cpIdx >= this.cpCount) {
            throw new ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        int t = this.entryTag[cpIdx];
        if (t != 12 && subFieldIdx != 0 || t == 12 && subFieldIdx != 0 && subFieldIdx != 1) {
            throw new ClassfileFormatException("Bad subfield index " + subFieldIdx + " for tag " + t + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        if (t == 0) {
            return 0;
        }
        if (t == 1) {
            cpIdxToUse = cpIdx;
        } else if (t == 7 || t == 8 || t == 19) {
            int indirIdx = this.indirectStringRefs[cpIdx];
            if (indirIdx == -1) {
                throw new ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
            }
            if (indirIdx == 0) {
                return 0;
            }
            cpIdxToUse = indirIdx;
        } else if (t == 12) {
            int compoundIndirIdx = this.indirectStringRefs[cpIdx];
            if (compoundIndirIdx == -1) {
                throw new ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
            }
            int indirIdx = (subFieldIdx == 0 ? compoundIndirIdx >> 16 : compoundIndirIdx) & 0xFFFF;
            if (indirIdx == 0) {
                throw new ClassfileFormatException("Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
            }
            cpIdxToUse = indirIdx;
        } else {
            throw new ClassfileFormatException("Wrong tag number " + t + " at constant pool index " + cpIdx + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        if (cpIdxToUse < 1 || cpIdxToUse >= this.cpCount) {
            throw new ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        return this.entryOffset[cpIdxToUse];
    }

    private String getConstantPoolString(int cpIdx, boolean replaceSlashWithDot, boolean stripLSemicolon) throws ClassfileFormatException, IOException {
        int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
        if (constantPoolStringOffset == 0) {
            return null;
        }
        int utfLen = this.reader.readUnsignedShort(constantPoolStringOffset);
        if (utfLen == 0) {
            return "";
        }
        return this.intern(this.reader.readString((long)constantPoolStringOffset + 2L, utfLen, replaceSlashWithDot, stripLSemicolon));
    }

    private String getConstantPoolString(int cpIdx, int subFieldIdx) throws ClassfileFormatException, IOException {
        int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, subFieldIdx);
        if (constantPoolStringOffset == 0) {
            return null;
        }
        int utfLen = this.reader.readUnsignedShort(constantPoolStringOffset);
        if (utfLen == 0) {
            return "";
        }
        return this.intern(this.reader.readString((long)constantPoolStringOffset + 2L, utfLen, false, false));
    }

    private String getConstantPoolString(int cpIdx) throws ClassfileFormatException, IOException {
        return this.getConstantPoolString(cpIdx, 0);
    }

    private byte getConstantPoolStringFirstByte(int cpIdx) throws ClassfileFormatException, IOException {
        int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
        if (constantPoolStringOffset == 0) {
            return 0;
        }
        int utfLen = this.reader.readUnsignedShort(constantPoolStringOffset);
        if (utfLen == 0) {
            return 0;
        }
        return this.reader.readByte((long)constantPoolStringOffset + 2L);
    }

    private String getConstantPoolClassName(int cpIdx) throws ClassfileFormatException, IOException {
        return this.getConstantPoolString(cpIdx, true, false);
    }

    private String getConstantPoolClassDescriptor(int cpIdx) throws ClassfileFormatException, IOException {
        return this.getConstantPoolString(cpIdx, true, true);
    }

    private boolean constantPoolStringEquals(int cpIdx, String asciiStr) throws ClassfileFormatException, IOException {
        int asciiStrLen;
        int cpStrOffset = this.getConstantPoolStringOffset(cpIdx, 0);
        if (cpStrOffset == 0) {
            return asciiStr == null;
        }
        if (asciiStr == null) {
            return false;
        }
        int cpStrLen = this.reader.readUnsignedShort(cpStrOffset);
        if (cpStrLen != (asciiStrLen = asciiStr.length())) {
            return false;
        }
        int cpStrStart = cpStrOffset + 2;
        this.reader.bufferTo(cpStrStart + cpStrLen);
        byte[] buf = this.reader.buf();
        for (int i = 0; i < cpStrLen; ++i) {
            if ((char)(buf[cpStrStart + i] & 0xFF) == asciiStr.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private int cpReadInt(int cpIdx) throws IOException {
        if (cpIdx < 1 || cpIdx >= this.cpCount) {
            throw new ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        return this.reader.readInt(this.entryOffset[cpIdx]);
    }

    private long cpReadLong(int cpIdx) throws IOException {
        if (cpIdx < 1 || cpIdx >= this.cpCount) {
            throw new ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
        }
        return this.reader.readLong(this.entryOffset[cpIdx]);
    }

    private Object getFieldConstantPoolValue(int tag, char fieldTypeDescriptorFirstChar, int cpIdx) throws ClassfileFormatException, IOException {
        switch (tag) {
            case 1: 
            case 7: 
            case 8: {
                return this.getConstantPoolString(cpIdx);
            }
            case 3: {
                int intVal = this.cpReadInt(cpIdx);
                switch (fieldTypeDescriptorFirstChar) {
                    case 'I': {
                        return intVal;
                    }
                    case 'S': {
                        return (short)intVal;
                    }
                    case 'C': {
                        return Character.valueOf((char)intVal);
                    }
                    case 'B': {
                        return (byte)intVal;
                    }
                    case 'Z': {
                        return intVal != 0;
                    }
                }
                throw new ClassfileFormatException("Unknown Constant_INTEGER type " + fieldTypeDescriptorFirstChar + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
            }
            case 4: {
                return Float.valueOf(Float.intBitsToFloat(this.cpReadInt(cpIdx)));
            }
            case 5: {
                return this.cpReadLong(cpIdx);
            }
            case 6: {
                return Double.longBitsToDouble(this.cpReadLong(cpIdx));
            }
        }
        throw new ClassfileFormatException("Unknown field constant pool tag " + tag + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
    }

    private AnnotationInfo readAnnotation() throws IOException {
        String annotationClassName = this.getConstantPoolClassDescriptor(this.reader.readUnsignedShort());
        int numElementValuePairs = this.reader.readUnsignedShort();
        AnnotationParameterValueList paramVals = null;
        if (numElementValuePairs > 0) {
            paramVals = new AnnotationParameterValueList(numElementValuePairs);
            for (int i = 0; i < numElementValuePairs; ++i) {
                String paramName = this.getConstantPoolString(this.reader.readUnsignedShort());
                Object paramValue = this.readAnnotationElementValue();
                paramVals.add(new AnnotationParameterValue(paramName, paramValue));
            }
        }
        return new AnnotationInfo(annotationClassName, paramVals);
    }

    private Object readAnnotationElementValue() throws IOException {
        char tag = (char)this.reader.readUnsignedByte();
        switch (tag) {
            case 'B': {
                return (byte)this.cpReadInt(this.reader.readUnsignedShort());
            }
            case 'C': {
                return Character.valueOf((char)this.cpReadInt(this.reader.readUnsignedShort()));
            }
            case 'D': {
                return Double.longBitsToDouble(this.cpReadLong(this.reader.readUnsignedShort()));
            }
            case 'F': {
                return Float.valueOf(Float.intBitsToFloat(this.cpReadInt(this.reader.readUnsignedShort())));
            }
            case 'I': {
                return this.cpReadInt(this.reader.readUnsignedShort());
            }
            case 'J': {
                return this.cpReadLong(this.reader.readUnsignedShort());
            }
            case 'S': {
                return (short)this.cpReadInt(this.reader.readUnsignedShort());
            }
            case 'Z': {
                return this.cpReadInt(this.reader.readUnsignedShort()) != 0;
            }
            case 's': {
                return this.getConstantPoolString(this.reader.readUnsignedShort());
            }
            case 'e': {
                String annotationClassName = this.getConstantPoolClassDescriptor(this.reader.readUnsignedShort());
                String annotationConstName = this.getConstantPoolString(this.reader.readUnsignedShort());
                return new AnnotationEnumValue(annotationClassName, annotationConstName);
            }
            case 'c': {
                String classRefTypeDescriptor = this.getConstantPoolString(this.reader.readUnsignedShort());
                return new AnnotationClassRef(classRefTypeDescriptor);
            }
            case '@': {
                return this.readAnnotation();
            }
            case '[': {
                int count = this.reader.readUnsignedShort();
                Object[] arr = new Object[count];
                for (int i = 0; i < count; ++i) {
                    arr[i] = this.readAnnotationElementValue();
                }
                return arr;
            }
        }
        throw new ClassfileFormatException("Class " + this.className + " has unknown annotation element type tag '" + (char)tag + "': element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
    }

    private List<TypePathNode> readTypePath() throws IOException {
        int typePathLength = this.reader.readUnsignedByte();
        if (typePathLength == 0) {
            return Collections.emptyList();
        }
        ArrayList<TypePathNode> list = new ArrayList<TypePathNode>(typePathLength);
        for (int i = 0; i < typePathLength; ++i) {
            int typePathKind = this.reader.readUnsignedByte();
            int typeArgumentIdx = this.reader.readUnsignedByte();
            list.add(new TypePathNode(typePathKind, typeArgumentIdx));
        }
        return list;
    }

    private void readConstantPoolEntries(LogNode log) throws IOException {
        int cpIdx;
        ArrayList<Integer> classNameCpIdxs = null;
        ArrayList<Integer> typeSignatureIdxs = null;
        if (this.scanSpec.enableInterClassDependencies) {
            classNameCpIdxs = new ArrayList<Integer>();
            typeSignatureIdxs = new ArrayList<Integer>();
        }
        this.cpCount = this.reader.readUnsignedShort();
        this.entryOffset = new int[this.cpCount];
        this.entryTag = new int[this.cpCount];
        this.indirectStringRefs = new int[this.cpCount];
        Arrays.fill(this.indirectStringRefs, 0, this.cpCount, -1);
        boolean skipSlot = false;
        block22: for (int i = 1; i < this.cpCount; ++i) {
            if (skipSlot) {
                skipSlot = false;
                continue;
            }
            this.entryTag[i] = this.reader.readUnsignedByte();
            this.entryOffset[i] = this.reader.currPos();
            switch (this.entryTag[i]) {
                case 0: {
                    throw new ClassfileFormatException("Invalid constant pool tag 0 in classfile " + this.relativePath + " (possible buffer underflow issue). Please report this at https://github.com/classgraph/classgraph/issues");
                }
                case 1: {
                    int strLen = this.reader.readUnsignedShort();
                    this.reader.skip(strLen);
                    continue block22;
                }
                case 3: 
                case 4: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 5: 
                case 6: {
                    this.reader.skip(8);
                    skipSlot = true;
                    continue block22;
                }
                case 7: {
                    this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                    if (classNameCpIdxs == null) continue block22;
                    classNameCpIdxs.add(this.indirectStringRefs[i]);
                    continue block22;
                }
                case 8: {
                    this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                    continue block22;
                }
                case 9: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 10: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 11: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 12: {
                    int nameRef = this.reader.readUnsignedShort();
                    int typeRef = this.reader.readUnsignedShort();
                    if (typeSignatureIdxs != null) {
                        typeSignatureIdxs.add(typeRef);
                    }
                    this.indirectStringRefs[i] = nameRef << 16 | typeRef;
                    continue block22;
                }
                case 15: {
                    this.reader.skip(3);
                    continue block22;
                }
                case 16: {
                    this.reader.skip(2);
                    continue block22;
                }
                case 17: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 18: {
                    this.reader.skip(4);
                    continue block22;
                }
                case 19: {
                    this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                    continue block22;
                }
                case 20: {
                    this.reader.skip(2);
                    continue block22;
                }
                default: {
                    throw new ClassfileFormatException("Unknown constant pool tag " + this.entryTag[i] + " (element size unknown, cannot continue reading class). Please report this at https://github.com/classgraph/classgraph/issues");
                }
            }
        }
        if (classNameCpIdxs != null) {
            this.refdClassNames = new HashSet<String>();
            Iterator iterator = classNameCpIdxs.iterator();
            while (iterator.hasNext()) {
                cpIdx = (Integer)iterator.next();
                String refdClassName = this.getConstantPoolString(cpIdx, true, false);
                if (refdClassName == null) continue;
                if (refdClassName.startsWith("[")) {
                    try {
                        TypeSignature typeSig = TypeSignature.parse(refdClassName.replace('.', '/'), null);
                        typeSig.findReferencedClassNames(this.refdClassNames);
                        continue;
                    }
                    catch (ParseException e) {
                        throw new ClassfileFormatException("Could not parse class name: " + refdClassName, e);
                    }
                }
                this.refdClassNames.add(refdClassName);
            }
        }
        if (typeSignatureIdxs != null) {
            Iterator iterator = typeSignatureIdxs.iterator();
            while (iterator.hasNext()) {
                cpIdx = (Integer)iterator.next();
                String typeSigStr = this.getConstantPoolString(cpIdx);
                if (typeSigStr == null) continue;
                try {
                    HierarchicalTypeSignature typeSig;
                    if (typeSigStr.startsWith("L") && typeSigStr.endsWith(";")) {
                        typeSig = TypeSignature.parse(typeSigStr, null);
                        ((TypeSignature)typeSig).findReferencedClassNames(this.refdClassNames);
                        continue;
                    }
                    if (typeSigStr.indexOf(40) >= 0 || "<init>".equals(typeSigStr)) {
                        typeSig = MethodTypeSignature.parse(typeSigStr, null);
                        ((MethodTypeSignature)typeSig).findReferencedClassNames(this.refdClassNames);
                        continue;
                    }
                    if (log == null) continue;
                    log.log("Could not extract referenced class names from constant pool string: " + typeSigStr);
                }
                catch (ParseException e) {
                    if (log == null) continue;
                    log.log("Could not extract referenced class names from constant pool string: " + typeSigStr + " : " + e);
                }
            }
        }
    }

    private void readBasicClassInfo() throws IOException, ClassfileFormatException, SkipClassException {
        this.classModifiers = this.reader.readUnsignedShort();
        this.isInterface = (this.classModifiers & 0x200) != 0;
        this.isAnnotation = (this.classModifiers & 0x2000) != 0;
        String classNamePath = this.getConstantPoolString(this.reader.readUnsignedShort());
        if (classNamePath == null) {
            throw new ClassfileFormatException("Class name is null");
        }
        this.className = classNamePath.replace('/', '.');
        if ("java.lang.Object".equals(this.className)) {
            throw new SkipClassException("No need to scan java.lang.Object");
        }
        boolean isModule = (this.classModifiers & 0x8000) != 0;
        boolean isPackage = this.relativePath.regionMatches(this.relativePath.lastIndexOf(47) + 1, "package-info.class", 0, 18);
        if (!(this.scanSpec.ignoreClassVisibility || Modifier.isPublic(this.classModifiers) || isModule || isPackage)) {
            throw new SkipClassException("Class is not public, and ignoreClassVisibility() was not called");
        }
        if (!this.relativePath.endsWith(".class")) {
            throw new SkipClassException("Classfile filename " + this.relativePath + " does not end in \".class\"");
        }
        int len = classNamePath.length();
        if (this.relativePath.length() != len + 6 || !classNamePath.regionMatches(0, this.relativePath, 0, len)) {
            throw new SkipClassException("Relative path " + this.relativePath + " does not match class name " + this.className);
        }
        int superclassNameCpIdx = this.reader.readUnsignedShort();
        if (superclassNameCpIdx > 0) {
            this.superclassName = this.getConstantPoolClassName(superclassNameCpIdx);
        }
    }

    private void readInterfaces() throws IOException {
        int interfaceCount = this.reader.readUnsignedShort();
        for (int i = 0; i < interfaceCount; ++i) {
            String interfaceName = this.getConstantPoolClassName(this.reader.readUnsignedShort());
            if (this.implementedInterfaces == null) {
                this.implementedInterfaces = new ArrayList<String>();
            }
            this.implementedInterfaces.add(interfaceName);
        }
    }

    private void readFields() throws IOException, ClassfileFormatException {
        int fieldCount = this.reader.readUnsignedShort();
        for (int i = 0; i < fieldCount; ++i) {
            int fieldModifierFlags = this.reader.readUnsignedShort();
            boolean isPublicField = (fieldModifierFlags & 1) == 1;
            boolean fieldIsVisible = isPublicField || this.scanSpec.ignoreFieldVisibility;
            boolean getStaticFinalFieldConstValue = this.scanSpec.enableStaticFinalFieldConstantInitializerValues && fieldIsVisible;
            ArrayList<1> fieldTypeAnnotationDecorators = null;
            if (!fieldIsVisible || !this.scanSpec.enableFieldInfo && !getStaticFinalFieldConstValue) {
                this.reader.readUnsignedShort();
                this.reader.readUnsignedShort();
                int attributesCount = this.reader.readUnsignedShort();
                for (int j = 0; j < attributesCount; ++j) {
                    this.reader.readUnsignedShort();
                    int attributeLength = this.reader.readInt();
                    this.reader.skip(attributeLength);
                }
                continue;
            }
            int fieldNameCpIdx = this.reader.readUnsignedShort();
            String fieldName = this.getConstantPoolString(fieldNameCpIdx);
            int fieldTypeDescriptorCpIdx = this.reader.readUnsignedShort();
            char fieldTypeDescriptorFirstChar = (char)this.getConstantPoolStringFirstByte(fieldTypeDescriptorCpIdx);
            String fieldTypeSignatureStr = null;
            String fieldTypeDescriptor = this.getConstantPoolString(fieldTypeDescriptorCpIdx);
            Object fieldConstValue = null;
            AnnotationInfoList fieldAnnotationInfo = null;
            int attributesCount = this.reader.readUnsignedShort();
            for (int j = 0; j < attributesCount; ++j) {
                int attributeNameCpIdx = this.reader.readUnsignedShort();
                int attributeLength = this.reader.readInt();
                if (getStaticFinalFieldConstValue && this.constantPoolStringEquals(attributeNameCpIdx, "ConstantValue")) {
                    int cpIdx = this.reader.readUnsignedShort();
                    if (cpIdx < 1 || cpIdx >= this.cpCount) {
                        throw new ClassfileFormatException("Constant pool index " + cpIdx + ", should be in range [1, " + (this.cpCount - 1) + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                    }
                    fieldConstValue = this.getFieldConstantPoolValue(this.entryTag[cpIdx], fieldTypeDescriptorFirstChar, cpIdx);
                    continue;
                }
                if (fieldIsVisible && this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                    fieldTypeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
                    continue;
                }
                if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
                    int fieldAnnotationCount = this.reader.readUnsignedShort();
                    if (fieldAnnotationCount <= 0) continue;
                    if (fieldAnnotationInfo == null) {
                        fieldAnnotationInfo = new AnnotationInfoList(1);
                    }
                    for (int k = 0; k < fieldAnnotationCount; ++k) {
                        AnnotationInfo fieldAnnotation = this.readAnnotation();
                        fieldAnnotationInfo.add(fieldAnnotation);
                    }
                    continue;
                }
                if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations"))) {
                    int annotationCount = this.reader.readUnsignedShort();
                    if (annotationCount <= 0) continue;
                    fieldTypeAnnotationDecorators = new ArrayList<1>();
                    for (int m = 0; m < annotationCount; ++m) {
                        int targetType = this.reader.readUnsignedByte();
                        if (targetType != 19) {
                            throw new ClassfileFormatException("Class " + this.className + " has unknown field type annotation target 0x" + Integer.toHexString(targetType) + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                        }
                        final List<TypePathNode> typePath = this.readTypePath();
                        final AnnotationInfo annotationInfo = this.readAnnotation();
                        fieldTypeAnnotationDecorators.add(new TypeAnnotationDecorator(){

                            @Override
                            public void decorate(TypeSignature typeSignature) {
                                typeSignature.addTypeAnnotation(typePath, annotationInfo);
                            }
                        });
                    }
                    continue;
                }
                this.reader.skip(attributeLength);
            }
            if (!this.scanSpec.enableFieldInfo || !fieldIsVisible) continue;
            if (this.fieldInfoList == null) {
                this.fieldInfoList = new FieldInfoList();
            }
            this.fieldInfoList.add(new FieldInfo(this.className, fieldName, fieldModifierFlags, fieldTypeDescriptor, fieldTypeSignatureStr, fieldConstValue, fieldAnnotationInfo, fieldTypeAnnotationDecorators));
        }
    }

    private void readMethods() throws IOException, ClassfileFormatException {
        int methodCount = this.reader.readUnsignedShort();
        for (int i = 0; i < methodCount; ++i) {
            int j;
            boolean enableMethodInfo;
            int methodModifierFlags = this.reader.readUnsignedShort();
            boolean isPublicMethod = (methodModifierFlags & 1) == 1;
            boolean methodIsVisible = isPublicMethod || this.scanSpec.ignoreMethodVisibility;
            ArrayList<2> methodTypeAnnotationDecorators = null;
            String methodName = null;
            String methodTypeDescriptor = null;
            String methodTypeSignatureStr = null;
            boolean bl = enableMethodInfo = this.scanSpec.enableMethodInfo || this.isAnnotation;
            if (enableMethodInfo || this.isAnnotation) {
                int methodNameCpIdx = this.reader.readUnsignedShort();
                methodName = this.getConstantPoolString(methodNameCpIdx);
                int methodTypeDescriptorCpIdx = this.reader.readUnsignedShort();
                methodTypeDescriptor = this.getConstantPoolString(methodTypeDescriptorCpIdx);
            } else {
                this.reader.skip(4);
            }
            int attributesCount = this.reader.readUnsignedShort();
            String[] methodParameterNames = null;
            String[] thrownExceptionNames = null;
            int[] methodParameterModifiers = null;
            AnnotationInfo[][] methodParameterAnnotations = null;
            AnnotationInfoList methodAnnotationInfo = null;
            boolean methodHasBody = false;
            int minLineNum = 0;
            int maxLineNum = 0;
            if (!methodIsVisible || !enableMethodInfo && !this.isAnnotation) {
                for (j = 0; j < attributesCount; ++j) {
                    this.reader.skip(2);
                    int attributeLength = this.reader.readInt();
                    this.reader.skip(attributeLength);
                }
                continue;
            }
            for (j = 0; j < attributesCount; ++j) {
                int k;
                int attributeNameCpIdx = this.reader.readUnsignedShort();
                int attributeLength = this.reader.readInt();
                if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
                    int methodAnnotationCount = this.reader.readUnsignedShort();
                    if (methodAnnotationCount <= 0) continue;
                    if (methodAnnotationInfo == null) {
                        methodAnnotationInfo = new AnnotationInfoList(1);
                    }
                    for (k = 0; k < methodAnnotationCount; ++k) {
                        AnnotationInfo annotationInfo = this.readAnnotation();
                        methodAnnotationInfo.add(annotationInfo);
                    }
                    continue;
                }
                if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleParameterAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleParameterAnnotations"))) {
                    int numParams = this.reader.readUnsignedByte();
                    if (methodParameterAnnotations == null) {
                        methodParameterAnnotations = new AnnotationInfo[numParams][];
                    } else if (methodParameterAnnotations.length != numParams) {
                        throw new ClassfileFormatException("Mismatch in number of parameters between RuntimeVisibleParameterAnnotations and RuntimeInvisibleParameterAnnotations");
                    }
                    for (int paramIdx = 0; paramIdx < numParams; ++paramIdx) {
                        int numAnnotations = this.reader.readUnsignedShort();
                        if (numAnnotations > 0) {
                            int annStartIdx = 0;
                            if (methodParameterAnnotations[paramIdx] != null) {
                                annStartIdx = methodParameterAnnotations[paramIdx].length;
                                methodParameterAnnotations[paramIdx] = Arrays.copyOf(methodParameterAnnotations[paramIdx], annStartIdx + numAnnotations);
                            } else {
                                methodParameterAnnotations[paramIdx] = new AnnotationInfo[numAnnotations];
                            }
                            for (int annIdx = 0; annIdx < numAnnotations; ++annIdx) {
                                methodParameterAnnotations[paramIdx][annStartIdx + annIdx] = this.readAnnotation();
                            }
                            continue;
                        }
                        if (methodParameterAnnotations[paramIdx] != null) continue;
                        methodParameterAnnotations[paramIdx] = NO_ANNOTATIONS;
                    }
                    continue;
                }
                if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations"))) {
                    int annotationCount = this.reader.readUnsignedShort();
                    if (annotationCount <= 0) continue;
                    methodTypeAnnotationDecorators = new ArrayList<2>(annotationCount);
                    for (int m = 0; m < annotationCount; ++m) {
                        int throwsTypeIndex;
                        int formalParameterIndex;
                        int boundIndex;
                        int typeParameterIndex;
                        final int targetType = this.reader.readUnsignedByte();
                        if (targetType == 1) {
                            typeParameterIndex = this.reader.readUnsignedByte();
                            boundIndex = -1;
                            formalParameterIndex = -1;
                            throwsTypeIndex = -1;
                        } else if (targetType == 18) {
                            typeParameterIndex = this.reader.readUnsignedByte();
                            boundIndex = this.reader.readUnsignedByte();
                            formalParameterIndex = -1;
                            throwsTypeIndex = -1;
                        } else if (targetType == 19) {
                            typeParameterIndex = -1;
                            boundIndex = -1;
                            formalParameterIndex = -1;
                            throwsTypeIndex = -1;
                        } else if (targetType == 20) {
                            typeParameterIndex = -1;
                            boundIndex = -1;
                            formalParameterIndex = -1;
                            throwsTypeIndex = -1;
                        } else if (targetType == 21) {
                            typeParameterIndex = -1;
                            boundIndex = -1;
                            formalParameterIndex = -1;
                            throwsTypeIndex = -1;
                        } else if (targetType == 22) {
                            typeParameterIndex = -1;
                            boundIndex = -1;
                            formalParameterIndex = this.reader.readUnsignedByte();
                            throwsTypeIndex = -1;
                        } else if (targetType == 23) {
                            typeParameterIndex = -1;
                            boundIndex = -1;
                            formalParameterIndex = -1;
                            throwsTypeIndex = this.reader.readUnsignedShort();
                        } else {
                            throw new ClassfileFormatException("Class " + this.className + " has unknown method type annotation target 0x" + Integer.toHexString(targetType) + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                        }
                        final List<TypePathNode> typePath = this.readTypePath();
                        final AnnotationInfo annotationInfo = this.readAnnotation();
                        methodTypeAnnotationDecorators.add(new MethodTypeAnnotationDecorator(){

                            @Override
                            public void decorate(MethodTypeSignature methodTypeSignature) {
                                List<ClassRefOrTypeVariableSignature> throwsSignatures;
                                if (targetType == 1) {
                                    List<TypeParameter> typeParameters = methodTypeSignature.getTypeParameters();
                                    if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                                        typeParameters.get(typeParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                                    }
                                } else if (targetType == 18) {
                                    List<TypeParameter> typeParameters = methodTypeSignature.getTypeParameters();
                                    if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                                        TypeParameter typeParameter = typeParameters.get(typeParameterIndex);
                                        if (boundIndex == 0) {
                                            ReferenceTypeSignature classBound = typeParameter.getClassBound();
                                            if (classBound != null) {
                                                classBound.addTypeAnnotation(typePath, annotationInfo);
                                            }
                                        } else {
                                            List<ReferenceTypeSignature> interfaceBounds = typeParameter.getInterfaceBounds();
                                            if (interfaceBounds != null && boundIndex - 1 < interfaceBounds.size()) {
                                                interfaceBounds.get(boundIndex - 1).addTypeAnnotation(typePath, annotationInfo);
                                            }
                                        }
                                    }
                                } else if (targetType == 20) {
                                    methodTypeSignature.getResultType().addTypeAnnotation(typePath, annotationInfo);
                                } else if (targetType == 21) {
                                    methodTypeSignature.addRecieverTypeAnnotation(annotationInfo);
                                } else if (targetType == 22) {
                                    List<TypeSignature> parameterTypeSignatures = methodTypeSignature.getParameterTypeSignatures();
                                    if (formalParameterIndex < parameterTypeSignatures.size()) {
                                        parameterTypeSignatures.get(formalParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                                    }
                                } else if (targetType == 23 && (throwsSignatures = methodTypeSignature.getThrowsSignatures()) != null && throwsTypeIndex < throwsSignatures.size()) {
                                    throwsSignatures.get(throwsTypeIndex).addTypeAnnotation(typePath, annotationInfo);
                                }
                            }
                        });
                    }
                    continue;
                }
                if (this.constantPoolStringEquals(attributeNameCpIdx, "MethodParameters")) {
                    int paramCount = this.reader.readUnsignedByte();
                    methodParameterNames = new String[paramCount];
                    methodParameterModifiers = new int[paramCount];
                    for (k = 0; k < paramCount; ++k) {
                        int cpIdx = this.reader.readUnsignedShort();
                        methodParameterNames[k] = cpIdx == 0 ? null : this.getConstantPoolString(cpIdx);
                        methodParameterModifiers[k] = this.reader.readUnsignedShort();
                    }
                    continue;
                }
                if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                    methodTypeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
                    continue;
                }
                if (this.constantPoolStringEquals(attributeNameCpIdx, "AnnotationDefault")) {
                    if (this.annotationParamDefaultValues == null) {
                        this.annotationParamDefaultValues = new AnnotationParameterValueList();
                    }
                    this.annotationParamDefaultValues.add(new AnnotationParameterValue(methodName, this.readAnnotationElementValue()));
                    continue;
                }
                if (this.constantPoolStringEquals(attributeNameCpIdx, "Exceptions")) {
                    int exceptionCount = this.reader.readUnsignedShort();
                    thrownExceptionNames = new String[exceptionCount];
                    for (k = 0; k < exceptionCount; ++k) {
                        int cpIdx = this.reader.readUnsignedShort();
                        thrownExceptionNames[k] = this.getConstantPoolClassName(cpIdx);
                    }
                    continue;
                }
                if (this.constantPoolStringEquals(attributeNameCpIdx, "Code")) {
                    methodHasBody = true;
                    this.reader.skip(4);
                    int codeLength = this.reader.readInt();
                    this.reader.skip(codeLength);
                    int exceptionTableLength = this.reader.readUnsignedShort();
                    this.reader.skip(8 * exceptionTableLength);
                    int codeAttrCount = this.reader.readUnsignedShort();
                    for (int k2 = 0; k2 < codeAttrCount; ++k2) {
                        int codeAttrCpIdx = this.reader.readUnsignedShort();
                        int codeAttrLen = this.reader.readInt();
                        if (this.constantPoolStringEquals(codeAttrCpIdx, "LineNumberTable")) {
                            int lineNumTableLen = this.reader.readUnsignedShort();
                            for (int l = 0; l < lineNumTableLen; ++l) {
                                this.reader.skip(2);
                                int lineNum = this.reader.readUnsignedShort();
                                minLineNum = minLineNum == 0 ? lineNum : Math.min(minLineNum, lineNum);
                                maxLineNum = maxLineNum == 0 ? lineNum : Math.max(maxLineNum, lineNum);
                            }
                            continue;
                        }
                        this.reader.skip(codeAttrLen);
                    }
                    continue;
                }
                this.reader.skip(attributeLength);
            }
            if (!enableMethodInfo) continue;
            if (this.methodInfoList == null) {
                this.methodInfoList = new MethodInfoList();
            }
            this.methodInfoList.add(new MethodInfo(this.className, methodName, methodAnnotationInfo, methodModifierFlags, methodTypeDescriptor, methodTypeSignatureStr, methodParameterNames, methodParameterModifiers, methodParameterAnnotations, methodHasBody, minLineNum, maxLineNum, methodTypeAnnotationDecorators, thrownExceptionNames));
        }
    }

    private void readClassAttributes() throws IOException, ClassfileFormatException {
        int attributesCount = this.reader.readUnsignedShort();
        for (int i = 0; i < attributesCount; ++i) {
            int m;
            int attributeNameCpIdx = this.reader.readUnsignedShort();
            int attributeLength = this.reader.readInt();
            if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))) {
                int annotationCount = this.reader.readUnsignedShort();
                if (annotationCount <= 0) continue;
                if (this.classAnnotations == null) {
                    this.classAnnotations = new AnnotationInfoList();
                }
                for (m = 0; m < annotationCount; ++m) {
                    this.classAnnotations.add(this.readAnnotation());
                }
                continue;
            }
            if (this.scanSpec.enableAnnotationInfo && (this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations") || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations"))) {
                int annotationCount = this.reader.readUnsignedShort();
                if (annotationCount <= 0) continue;
                this.classTypeAnnotationDecorators = new ArrayList<ClassTypeAnnotationDecorator>(annotationCount);
                for (m = 0; m < annotationCount; ++m) {
                    int boundIndex;
                    int supertypeIndex;
                    int typeParameterIndex;
                    final int targetType = this.reader.readUnsignedByte();
                    if (targetType == 0) {
                        typeParameterIndex = this.reader.readUnsignedByte();
                        supertypeIndex = -1;
                        boundIndex = -1;
                    } else if (targetType == 16) {
                        supertypeIndex = this.reader.readUnsignedShort();
                        typeParameterIndex = -1;
                        boundIndex = -1;
                    } else if (targetType == 17) {
                        typeParameterIndex = this.reader.readUnsignedByte();
                        boundIndex = this.reader.readUnsignedByte();
                        supertypeIndex = -1;
                    } else {
                        throw new ClassfileFormatException("Class " + this.className + " has unknown class type annotation target 0x" + Integer.toHexString(targetType) + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues");
                    }
                    final List<TypePathNode> typePath = this.readTypePath();
                    final AnnotationInfo annotationInfo = this.readAnnotation();
                    this.classTypeAnnotationDecorators.add(new ClassTypeAnnotationDecorator(){

                        @Override
                        public void decorate(ClassTypeSignature classTypeSignature) {
                            List<TypeParameter> typeParameters;
                            if (targetType == 0) {
                                List<TypeParameter> typeParameters2 = classTypeSignature.getTypeParameters();
                                if (typeParameters2 != null && typeParameterIndex < typeParameters2.size()) {
                                    typeParameters2.get(typeParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                                }
                            } else if (targetType == 16) {
                                if (supertypeIndex == 65535) {
                                    classTypeSignature.getSuperclassSignature().addTypeAnnotation(typePath, annotationInfo);
                                } else {
                                    classTypeSignature.getSuperinterfaceSignatures().get(supertypeIndex).addTypeAnnotation(typePath, annotationInfo);
                                }
                            } else if (targetType == 17 && (typeParameters = classTypeSignature.getTypeParameters()) != null && typeParameterIndex < typeParameters.size()) {
                                TypeParameter typeParameter = typeParameters.get(typeParameterIndex);
                                if (boundIndex == 0) {
                                    ReferenceTypeSignature classBound = typeParameter.getClassBound();
                                    if (classBound != null) {
                                        classBound.addTypeAnnotation(typePath, annotationInfo);
                                    }
                                } else {
                                    List<ReferenceTypeSignature> interfaceBounds = typeParameter.getInterfaceBounds();
                                    if (interfaceBounds != null && boundIndex - 1 < interfaceBounds.size()) {
                                        typeParameter.getInterfaceBounds().get(boundIndex - 1).addTypeAnnotation(typePath, annotationInfo);
                                    }
                                }
                            }
                        }
                    });
                }
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "Record")) {
                this.isRecord = true;
                this.reader.skip(attributeLength);
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "InnerClasses")) {
                int numInnerClasses = this.reader.readUnsignedShort();
                for (int j = 0; j < numInnerClasses; ++j) {
                    int innerClassInfoCpIdx = this.reader.readUnsignedShort();
                    int outerClassInfoCpIdx = this.reader.readUnsignedShort();
                    this.reader.skip(2);
                    int innerClassAccessFlags = this.reader.readUnsignedShort();
                    if (innerClassInfoCpIdx == 0 || outerClassInfoCpIdx == 0) continue;
                    String innerClassName = this.getConstantPoolClassName(innerClassInfoCpIdx);
                    String outerClassName = this.getConstantPoolClassName(outerClassInfoCpIdx);
                    if (innerClassName == null || outerClassName == null) {
                        throw new ClassfileFormatException("Inner and/or outer class name is null");
                    }
                    if (innerClassName.equals(outerClassName)) {
                        throw new ClassfileFormatException("Inner and outer class name cannot be the same");
                    }
                    if ("java.lang.invoke.MethodHandles$Lookup".equals(innerClassName) && "java.lang.invoke.MethodHandles".equals(outerClassName)) continue;
                    if (this.classContainmentEntries == null) {
                        this.classContainmentEntries = new ArrayList<ClassContainment>();
                    }
                    this.classContainmentEntries.add(new ClassContainment(innerClassName, innerClassAccessFlags, outerClassName));
                }
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                this.typeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "SourceFile")) {
                this.sourceFile = this.getConstantPoolString(this.reader.readUnsignedShort());
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "EnclosingMethod")) {
                String innermostEnclosingClassName = this.getConstantPoolClassName(this.reader.readUnsignedShort());
                int enclosingMethodCpIdx = this.reader.readUnsignedShort();
                String definingMethodName = enclosingMethodCpIdx == 0 ? "<clinit>" : this.getConstantPoolString(enclosingMethodCpIdx, 0);
                if (this.classContainmentEntries == null) {
                    this.classContainmentEntries = new ArrayList<ClassContainment>();
                }
                this.classContainmentEntries.add(new ClassContainment(this.className, this.classModifiers, innermostEnclosingClassName));
                this.fullyQualifiedDefiningMethodName = innermostEnclosingClassName + "." + definingMethodName;
                continue;
            }
            if (this.constantPoolStringEquals(attributeNameCpIdx, "Module")) {
                int moduleNameCpIdx = this.reader.readUnsignedShort();
                this.classpathElement.moduleNameFromModuleDescriptor = this.getConstantPoolString(moduleNameCpIdx);
                this.reader.skip(attributeLength - 2);
                continue;
            }
            this.reader.skip(attributeLength);
        }
    }

    Classfile(ClasspathElement classpathElement, List<ClasspathElement> classpathOrder, Set<String> acceptedClassNamesFound, Set<String> classNamesScheduledForExtendedScanning, String relativePath, Resource classfileResource, boolean isExternalClass, ConcurrentHashMap<String, String> stringInternMap, WorkQueue<Scanner.ClassfileScanWorkUnit> workQueue, ScanSpec scanSpec, LogNode log) throws IOException, ClassfileFormatException, SkipClassException {
        LogNode subLog;
        this.classpathElement = classpathElement;
        this.classpathOrder = classpathOrder;
        this.relativePath = relativePath;
        this.acceptedClassNamesFound = acceptedClassNamesFound;
        this.classNamesScheduledForExtendedScanning = classNamesScheduledForExtendedScanning;
        this.classfileResource = classfileResource;
        this.isExternalClass = isExternalClass;
        this.stringInternMap = stringInternMap;
        this.scanSpec = scanSpec;
        try (ClassfileReader classfileReader = classfileResource.openClassfile();){
            this.reader = classfileReader;
            if (this.reader.readInt() != -889275714) {
                throw new ClassfileFormatException("Classfile does not have correct magic number");
            }
            this.minorVersion = this.reader.readUnsignedShort();
            this.majorVersion = this.reader.readUnsignedShort();
            this.readConstantPoolEntries(log);
            this.readBasicClassInfo();
            this.readInterfaces();
            this.readFields();
            this.readMethods();
            this.readClassAttributes();
            this.reader = null;
        }
        LogNode logNode = log == null ? null : (subLog = log.log("Found " + (this.isAnnotation ? "annotation class" : (this.isInterface ? "interface class" : "class")) + " " + this.className));
        if (subLog != null) {
            String modifierStr;
            if (this.superclassName != null) {
                subLog.log("Super" + (this.isInterface && !this.isAnnotation ? "interface" : "class") + ": " + this.superclassName);
            }
            if (this.implementedInterfaces != null) {
                subLog.log("Interfaces: " + StringUtils.join(", ", this.implementedInterfaces));
            }
            if (this.classAnnotations != null) {
                subLog.log("Class annotations: " + StringUtils.join(", ", this.classAnnotations));
            }
            if (this.annotationParamDefaultValues != null) {
                for (AnnotationParameterValue apv : this.annotationParamDefaultValues) {
                    subLog.log("Annotation default param value: " + apv);
                }
            }
            if (this.fieldInfoList != null) {
                for (FieldInfo fieldInfo : this.fieldInfoList) {
                    modifierStr = fieldInfo.getModifiersStr();
                    subLog.log("Field: " + modifierStr + (modifierStr.isEmpty() ? "" : " ") + fieldInfo.getName());
                }
            }
            if (this.methodInfoList != null) {
                for (MethodInfo methodInfo : this.methodInfoList) {
                    modifierStr = methodInfo.getModifiersStr();
                    subLog.log("Method: " + modifierStr + (modifierStr.isEmpty() ? "" : " ") + methodInfo.getName());
                }
            }
            if (this.typeSignatureStr != null) {
                subLog.log("Class type signature: " + this.typeSignatureStr);
            }
            if (this.refdClassNames != null) {
                ArrayList<String> refdClassNamesSorted = new ArrayList<String>(this.refdClassNames);
                CollectionUtils.sortIfNotEmpty(refdClassNamesSorted);
                subLog.log("Additional referenced class names: " + StringUtils.join(", ", refdClassNamesSorted));
            }
        }
        if (scanSpec.extendScanningUpwardsToExternalClasses) {
            this.extendScanningUpwards(subLog);
            if (this.additionalWorkUnits != null) {
                workQueue.addWorkUnits(this.additionalWorkUnits);
            }
        }
    }

    static class TypePathNode {
        short typePathKind;
        short typeArgumentIdx;

        public TypePathNode(int typePathKind, int typeArgumentIdx) {
            this.typePathKind = (short)typePathKind;
            this.typeArgumentIdx = (short)typeArgumentIdx;
        }

        public String toString() {
            return "(" + this.typePathKind + "," + this.typeArgumentIdx + ")";
        }
    }

    static interface TypeAnnotationDecorator {
        public void decorate(TypeSignature var1);
    }

    static interface MethodTypeAnnotationDecorator {
        public void decorate(MethodTypeSignature var1);
    }

    static interface ClassTypeAnnotationDecorator {
        public void decorate(ClassTypeSignature var1);
    }

    static class SkipClassException
    extends IOException {
        static final long serialVersionUID = 1L;

        public SkipClassException(String message) {
            super(message);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    static class ClassfileFormatException
    extends IOException {
        static final long serialVersionUID = 1L;

        public ClassfileFormatException(String message) {
            super(message);
        }

        public ClassfileFormatException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    static class ClassContainment {
        public final String innerClassName;
        public final int innerClassModifierBits;
        public final String outerClassName;

        public ClassContainment(String innerClassName, int innerClassModifierBits, String outerClassName) {
            this.innerClassName = innerClassName;
            this.innerClassModifierBits = innerClassModifierBits;
            this.outerClassName = outerClassName;
        }
    }
}

