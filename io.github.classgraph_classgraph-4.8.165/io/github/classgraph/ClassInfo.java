/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ArrayClassInfo;
import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.ClassTypeSignature;
import io.github.classgraph.Classfile;
import io.github.classgraph.ClasspathElement;
import io.github.classgraph.ClasspathElementModule;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.HasName;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ModuleInfo;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ScanResultObject;
import io.github.classgraph.TypeSignature;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.json.Id;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassInfo
extends ScanResultObject
implements Comparable<ClassInfo>,
HasName {
    @Id
    protected String name;
    private int modifiers;
    private boolean isRecord;
    boolean isInherited;
    private int classfileMinorVersion;
    private int classfileMajorVersion;
    protected String typeSignatureStr;
    private transient ClassTypeSignature typeSignature;
    private transient ClassTypeSignature typeDescriptor;
    private String sourceFile;
    private String fullyQualifiedDefiningMethodName;
    protected boolean isExternalClass = true;
    protected boolean isScannedClass;
    transient ClasspathElement classpathElement;
    protected transient Resource classfileResource;
    transient ClassLoader classLoader;
    ModuleInfo moduleInfo;
    PackageInfo packageInfo;
    AnnotationInfoList annotationInfo;
    FieldInfoList fieldInfo;
    MethodInfoList methodInfo;
    AnnotationParameterValueList annotationDefaultParamValues;
    transient List<Classfile.ClassTypeAnnotationDecorator> typeAnnotationDecorators;
    private Set<String> referencedClassNames;
    private ClassInfoList referencedClasses;
    transient boolean annotationDefaultParamValuesHasBeenConvertedToPrimitive;
    private Map<RelType, Set<ClassInfo>> relatedClasses;
    private transient List<ClassInfo> overrideOrder;
    private transient List<ClassInfo> methodOverrideOrder;
    private static final int ANNOTATION_CLASS_MODIFIER = 8192;
    private static final ReachableAndDirectlyRelatedClasses NO_REACHABLE_CLASSES = new ReachableAndDirectlyRelatedClasses(Collections.emptySet(), Collections.emptySet());

    ClassInfo() {
    }

    protected ClassInfo(String name, int classModifiers, Resource classfileResource) {
        this.name = name;
        if (name.endsWith(";")) {
            throw new IllegalArgumentException("Bad class name");
        }
        this.setModifiers(classModifiers);
        this.classfileResource = classfileResource;
        this.relatedClasses = new EnumMap<RelType, Set<ClassInfo>>(RelType.class);
    }

    boolean addRelatedClass(RelType relType, ClassInfo classInfo) {
        Set<ClassInfo> classInfoSet = this.relatedClasses.get((Object)relType);
        if (classInfoSet == null) {
            classInfoSet = new LinkedHashSet<ClassInfo>(4);
            this.relatedClasses.put(relType, classInfoSet);
        }
        return classInfoSet.add(classInfo);
    }

    static ClassInfo getOrCreateClassInfo(String className, Map<String, ClassInfo> classNameToClassInfo) {
        int numArrayDims = 0;
        String baseClassName = className;
        while (baseClassName.endsWith("[]")) {
            ++numArrayDims;
            baseClassName = baseClassName.substring(0, baseClassName.length() - 2);
        }
        while (baseClassName.startsWith("[")) {
            ++numArrayDims;
            baseClassName = baseClassName.substring(1);
        }
        if (baseClassName.endsWith(";")) {
            baseClassName = baseClassName.substring(baseClassName.length() - 1);
        }
        baseClassName = baseClassName.replace('/', '.');
        ClassInfo classInfo = classNameToClassInfo.get(className);
        if (classInfo == null) {
            if (numArrayDims == 0) {
                classInfo = new ClassInfo(baseClassName, 0, null);
            } else {
                TypeSignature elementTypeSignature;
                StringBuilder arrayTypeSigStrBuf = new StringBuilder();
                for (int i = 0; i < numArrayDims; ++i) {
                    arrayTypeSigStrBuf.append('[');
                }
                char baseTypeChar = BaseTypeSignature.getTypeChar(baseClassName);
                if (baseTypeChar != '\u0000') {
                    arrayTypeSigStrBuf.append(baseTypeChar);
                    elementTypeSignature = new BaseTypeSignature(baseTypeChar);
                } else {
                    String eltTypeSigStr = "L" + baseClassName.replace('.', '/') + ";";
                    arrayTypeSigStrBuf.append(eltTypeSigStr);
                    try {
                        elementTypeSignature = ClassRefTypeSignature.parse(new Parser(eltTypeSigStr), null);
                        if (elementTypeSignature == null) {
                            throw new IllegalArgumentException("Could not form array base type signature for class " + baseClassName);
                        }
                    }
                    catch (ParseException e) {
                        throw new IllegalArgumentException("Could not form array base type signature for class " + baseClassName);
                    }
                }
                classInfo = new ArrayClassInfo(new ArrayTypeSignature(elementTypeSignature, numArrayDims, arrayTypeSigStrBuf.toString()));
            }
            classNameToClassInfo.put(className, classInfo);
        }
        return classInfo;
    }

    void setClassfileVersion(int minorVersion, int majorVersion) {
        this.classfileMinorVersion = minorVersion;
        this.classfileMajorVersion = majorVersion;
    }

    void setModifiers(int modifiers) {
        this.modifiers |= modifiers;
    }

    void setIsInterface(boolean isInterface) {
        if (isInterface) {
            this.modifiers |= 0x200;
        }
    }

    void setIsAnnotation(boolean isAnnotation) {
        if (isAnnotation) {
            this.modifiers |= 0x2000;
        }
    }

    void setIsRecord(boolean isRecord) {
        if (isRecord) {
            this.isRecord = isRecord;
        }
    }

    void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    void addTypeDecorators(List<Classfile.ClassTypeAnnotationDecorator> classTypeAnnotationDecorators) {
        if (this.typeAnnotationDecorators == null) {
            this.typeAnnotationDecorators = new ArrayList<Classfile.ClassTypeAnnotationDecorator>();
        }
        this.typeAnnotationDecorators.addAll(classTypeAnnotationDecorators);
    }

    void addSuperclass(String superclassName, Map<String, ClassInfo> classNameToClassInfo) {
        if (superclassName != null && !superclassName.equals("java.lang.Object")) {
            ClassInfo superclassClassInfo = ClassInfo.getOrCreateClassInfo(superclassName, classNameToClassInfo);
            this.addRelatedClass(RelType.SUPERCLASSES, superclassClassInfo);
            superclassClassInfo.addRelatedClass(RelType.SUBCLASSES, this);
        }
    }

    void addImplementedInterface(String interfaceName, Map<String, ClassInfo> classNameToClassInfo) {
        ClassInfo interfaceClassInfo = ClassInfo.getOrCreateClassInfo(interfaceName, classNameToClassInfo);
        interfaceClassInfo.setIsInterface(true);
        this.addRelatedClass(RelType.IMPLEMENTED_INTERFACES, interfaceClassInfo);
        interfaceClassInfo.addRelatedClass(RelType.CLASSES_IMPLEMENTING, this);
    }

    static void addClassContainment(List<Classfile.ClassContainment> classContainmentEntries, Map<String, ClassInfo> classNameToClassInfo) {
        for (Classfile.ClassContainment classContainment : classContainmentEntries) {
            ClassInfo innerClassInfo = ClassInfo.getOrCreateClassInfo(classContainment.innerClassName, classNameToClassInfo);
            innerClassInfo.setModifiers(classContainment.innerClassModifierBits);
            ClassInfo outerClassInfo = ClassInfo.getOrCreateClassInfo(classContainment.outerClassName, classNameToClassInfo);
            innerClassInfo.addRelatedClass(RelType.CONTAINED_WITHIN_OUTER_CLASS, outerClassInfo);
            outerClassInfo.addRelatedClass(RelType.CONTAINS_INNER_CLASS, innerClassInfo);
        }
    }

    void addFullyQualifiedDefiningMethodName(String fullyQualifiedDefiningMethodName) {
        this.fullyQualifiedDefiningMethodName = fullyQualifiedDefiningMethodName;
    }

    void addClassAnnotation(AnnotationInfo classAnnotationInfo, Map<String, ClassInfo> classNameToClassInfo) {
        ClassInfo annotationClassInfo = ClassInfo.getOrCreateClassInfo(classAnnotationInfo.getName(), classNameToClassInfo);
        annotationClassInfo.setModifiers(8192);
        if (this.annotationInfo == null) {
            this.annotationInfo = new AnnotationInfoList(2);
        }
        this.annotationInfo.add(classAnnotationInfo);
        this.addRelatedClass(RelType.CLASS_ANNOTATIONS, annotationClassInfo);
        annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_ANNOTATION, this);
        if (classAnnotationInfo.getName().equals(Inherited.class.getName())) {
            this.isInherited = true;
        }
    }

    private void addFieldOrMethodAnnotationInfo(AnnotationInfoList annotationInfoList, boolean isField, int modifiers, Map<String, ClassInfo> classNameToClassInfo) {
        if (annotationInfoList != null) {
            for (AnnotationInfo fieldAnnotationInfo : annotationInfoList) {
                ClassInfo annotationClassInfo = ClassInfo.getOrCreateClassInfo(fieldAnnotationInfo.getName(), classNameToClassInfo);
                annotationClassInfo.setModifiers(8192);
                this.addRelatedClass(isField ? RelType.FIELD_ANNOTATIONS : RelType.METHOD_ANNOTATIONS, annotationClassInfo);
                annotationClassInfo.addRelatedClass(isField ? RelType.CLASSES_WITH_FIELD_ANNOTATION : RelType.CLASSES_WITH_METHOD_ANNOTATION, this);
                if (Modifier.isPrivate(modifiers)) continue;
                annotationClassInfo.addRelatedClass(isField ? RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION : RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION, this);
            }
        }
    }

    void addFieldInfo(FieldInfoList fieldInfoList, Map<String, ClassInfo> classNameToClassInfo) {
        for (FieldInfo fi : fieldInfoList) {
            this.addFieldOrMethodAnnotationInfo(fi.annotationInfo, true, fi.getModifiers(), classNameToClassInfo);
        }
        if (this.fieldInfo == null) {
            this.fieldInfo = fieldInfoList;
        } else {
            this.fieldInfo.addAll((Collection)fieldInfoList);
        }
    }

    void addMethodInfo(MethodInfoList methodInfoList, Map<String, ClassInfo> classNameToClassInfo) {
        for (MethodInfo mi : methodInfoList) {
            this.addFieldOrMethodAnnotationInfo(mi.annotationInfo, false, mi.getModifiers(), classNameToClassInfo);
            if (mi.parameterAnnotationInfo == null) continue;
            for (int i = 0; i < mi.parameterAnnotationInfo.length; ++i) {
                AnnotationInfo[] paramAnnotationInfoArr = mi.parameterAnnotationInfo[i];
                if (paramAnnotationInfoArr == null) continue;
                for (AnnotationInfo methodParamAnnotationInfo : paramAnnotationInfoArr) {
                    ClassInfo annotationClassInfo = ClassInfo.getOrCreateClassInfo(methodParamAnnotationInfo.getName(), classNameToClassInfo);
                    annotationClassInfo.setModifiers(8192);
                    this.addRelatedClass(RelType.METHOD_PARAMETER_ANNOTATIONS, annotationClassInfo);
                    annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, this);
                    if (Modifier.isPrivate(mi.getModifiers())) continue;
                    annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION, this);
                }
            }
        }
        if (this.methodInfo == null) {
            this.methodInfo = methodInfoList;
        } else {
            this.methodInfo.addAll((Collection)methodInfoList);
        }
    }

    void setTypeSignature(String typeSignatureStr) {
        this.typeSignatureStr = typeSignatureStr;
    }

    void addAnnotationParamDefaultValues(AnnotationParameterValueList paramNamesAndValues) {
        this.setIsAnnotation(true);
        if (this.annotationDefaultParamValues == null) {
            this.annotationDefaultParamValues = paramNamesAndValues;
        } else {
            this.annotationDefaultParamValues.addAll((Collection)paramNamesAndValues);
        }
    }

    static ClassInfo addScannedClass(String className, int classModifiers, boolean isExternalClass, Map<String, ClassInfo> classNameToClassInfo, ClasspathElement classpathElement, Resource classfileResource) {
        ClassInfo classInfo = classNameToClassInfo.get(className);
        if (classInfo == null) {
            classInfo = new ClassInfo(className, classModifiers, classfileResource);
            classNameToClassInfo.put(className, classInfo);
        } else {
            if (classInfo.isScannedClass) {
                throw new IllegalArgumentException("Class " + className + " should not have been encountered more than once due to classpath masking -- please report this bug at: https://github.com/classgraph/classgraph/issues");
            }
            classInfo.classfileResource = classfileResource;
            classInfo.modifiers |= classModifiers;
        }
        classInfo.isScannedClass = true;
        classInfo.isExternalClass = isExternalClass;
        classInfo.classpathElement = classpathElement;
        classInfo.classLoader = classpathElement.getClassLoader();
        return classInfo;
    }

    private static Set<ClassInfo> filterClassInfo(Collection<ClassInfo> classes, ScanSpec scanSpec, boolean strictAccept, ClassType ... classTypes) {
        if (classes == null) {
            return Collections.emptySet();
        }
        boolean includeAllTypes = classTypes.length == 0;
        boolean includeStandardClasses = false;
        boolean includeImplementedInterfaces = false;
        boolean includeAnnotations = false;
        boolean includeEnums = false;
        boolean includeRecords = false;
        block9: for (ClassType classType : classTypes) {
            switch (classType) {
                case ALL: {
                    includeAllTypes = true;
                    continue block9;
                }
                case STANDARD_CLASS: {
                    includeStandardClasses = true;
                    continue block9;
                }
                case IMPLEMENTED_INTERFACE: {
                    includeImplementedInterfaces = true;
                    continue block9;
                }
                case ANNOTATION: {
                    includeAnnotations = true;
                    continue block9;
                }
                case INTERFACE_OR_ANNOTATION: {
                    includeAnnotations = true;
                    includeImplementedInterfaces = true;
                    continue block9;
                }
                case ENUM: {
                    includeEnums = true;
                    continue block9;
                }
                case RECORD: {
                    includeRecords = true;
                    continue block9;
                }
                default: {
                    throw new IllegalArgumentException("Unknown ClassType: " + (Object)((Object)classType));
                }
            }
        }
        if (includeStandardClasses && includeImplementedInterfaces && includeAnnotations) {
            includeAllTypes = true;
        }
        LinkedHashSet<ClassInfo> classInfoSetFiltered = new LinkedHashSet<ClassInfo>(classes.size());
        for (ClassInfo classInfo : classes) {
            boolean acceptClass;
            boolean includeType = includeAllTypes || includeStandardClasses && classInfo.isStandardClass() || includeImplementedInterfaces && classInfo.isImplementedInterface() || includeAnnotations && classInfo.isAnnotation() || includeEnums && classInfo.isEnum() || includeRecords && classInfo.isRecord();
            boolean bl = acceptClass = !classInfo.isExternalClass || scanSpec.enableExternalClasses || !strictAccept;
            if (!includeType || !acceptClass || scanSpec.classOrPackageIsRejected(classInfo.name)) continue;
            classInfoSetFiltered.add(classInfo);
        }
        return classInfoSetFiltered;
    }

    private ReachableAndDirectlyRelatedClasses filterClassInfo(RelType relType, boolean strictAccept, ClassType ... classTypes) {
        Set<ClassInfo> directlyRelatedClasses = this.relatedClasses.get((Object)relType);
        if (directlyRelatedClasses == null) {
            return NO_REACHABLE_CLASSES;
        }
        directlyRelatedClasses = new LinkedHashSet<ClassInfo>(directlyRelatedClasses);
        LinkedHashSet<ClassInfo> reachableClasses = new LinkedHashSet<ClassInfo>(directlyRelatedClasses);
        if (relType == RelType.METHOD_ANNOTATIONS || relType == RelType.METHOD_PARAMETER_ANNOTATIONS || relType == RelType.FIELD_ANNOTATIONS) {
            for (ClassInfo annotation : directlyRelatedClasses) {
                reachableClasses.addAll(annotation.filterClassInfo((RelType)RelType.CLASS_ANNOTATIONS, (boolean)strictAccept, (ClassType[])new ClassType[0]).reachableClasses);
            }
        } else if (relType == RelType.CLASSES_WITH_METHOD_ANNOTATION || relType == RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION || relType == RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION || relType == RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION || relType == RelType.CLASSES_WITH_FIELD_ANNOTATION || relType == RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION) {
            for (ClassInfo subAnnotation : this.filterClassInfo((RelType)RelType.CLASSES_WITH_ANNOTATION, (boolean)strictAccept, (ClassType[])new ClassType[]{ClassType.ANNOTATION}).reachableClasses) {
                Set<ClassInfo> annotatedClasses = subAnnotation.relatedClasses.get((Object)relType);
                if (annotatedClasses == null) continue;
                reachableClasses.addAll(annotatedClasses);
            }
        } else {
            LinkedList<ClassInfo> queue = new LinkedList<ClassInfo>(directlyRelatedClasses);
            while (!queue.isEmpty()) {
                ClassInfo head = queue.removeFirst();
                Set<ClassInfo> headRelatedClasses = head.relatedClasses.get((Object)relType);
                if (headRelatedClasses == null) continue;
                for (ClassInfo directlyReachableFromHead : headRelatedClasses) {
                    if (!reachableClasses.add(directlyReachableFromHead)) continue;
                    queue.add(directlyReachableFromHead);
                }
            }
        }
        if (reachableClasses.isEmpty()) {
            return NO_REACHABLE_CLASSES;
        }
        if (relType == RelType.CLASS_ANNOTATIONS || relType == RelType.METHOD_ANNOTATIONS || relType == RelType.METHOD_PARAMETER_ANNOTATIONS || relType == RelType.FIELD_ANNOTATIONS) {
            LinkedHashSet<ClassInfo> reachableClassesToRemove = null;
            for (ClassInfo reachableClassInfo : reachableClasses) {
                if (!reachableClassInfo.getName().startsWith("java.lang.annotation.") || directlyRelatedClasses.contains(reachableClassInfo)) continue;
                if (reachableClassesToRemove == null) {
                    reachableClassesToRemove = new LinkedHashSet<ClassInfo>();
                }
                reachableClassesToRemove.add(reachableClassInfo);
            }
            if (reachableClassesToRemove != null) {
                reachableClasses.removeAll(reachableClassesToRemove);
            }
        }
        return new ReachableAndDirectlyRelatedClasses(ClassInfo.filterClassInfo(reachableClasses, this.scanResult.scanSpec, strictAccept, classTypes), ClassInfo.filterClassInfo(directlyRelatedClasses, this.scanResult.scanSpec, strictAccept, classTypes));
    }

    static ClassInfoList getAllClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.ALL), true);
    }

    static ClassInfoList getAllEnums(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.ENUM), true);
    }

    static ClassInfoList getAllRecords(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.RECORD), true);
    }

    static ClassInfoList getAllStandardClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.STANDARD_CLASS), true);
    }

    static ClassInfoList getAllImplementedInterfaceClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.IMPLEMENTED_INTERFACE), true);
    }

    static ClassInfoList getAllAnnotationClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.ANNOTATION), true);
    }

    static ClassInfoList getAllInterfacesOrAnnotationClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, true, ClassType.INTERFACE_OR_ANNOTATION), true);
    }

    @Override
    public String getName() {
        return this.name;
    }

    static String getSimpleName(String className) {
        return className.substring(Math.max(className.lastIndexOf(46), className.lastIndexOf(36)) + 1);
    }

    public String getSimpleName() {
        return ClassInfo.getSimpleName(this.name);
    }

    public ModuleInfo getModuleInfo() {
        return this.moduleInfo;
    }

    public PackageInfo getPackageInfo() {
        return this.packageInfo;
    }

    public String getPackageName() {
        return PackageInfo.getParentPackageName(this.name);
    }

    public boolean isExternalClass() {
        return this.isExternalClass;
    }

    public int getClassfileMinorVersion() {
        return this.classfileMinorVersion;
    }

    public int getClassfileMajorVersion() {
        return this.classfileMajorVersion;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public String getModifiersStr() {
        StringBuilder buf = new StringBuilder();
        TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.CLASS, false, buf);
        return buf.toString();
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.modifiers);
    }

    public boolean isPackageVisible() {
        return !this.isPublic() && !this.isPrivate() && !this.isProtected();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers);
    }

    public boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifiers);
    }

    public boolean isAnnotation() {
        return (this.modifiers & 0x2000) != 0;
    }

    public boolean isInterface() {
        return this.isInterfaceOrAnnotation() && !this.isAnnotation();
    }

    public boolean isInterfaceOrAnnotation() {
        return (this.modifiers & 0x200) != 0;
    }

    public boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    public boolean isRecord() {
        return this.isRecord;
    }

    public boolean isStandardClass() {
        return !this.isAnnotation() && !this.isInterface();
    }

    public boolean isArrayClass() {
        return this instanceof ArrayClassInfo;
    }

    public boolean extendsSuperclass(Class<?> superclass) {
        return this.extendsSuperclass(superclass.getName());
    }

    public boolean extendsSuperclass(String superclassName) {
        return superclassName.equals("java.lang.Object") && this.isStandardClass() || this.getSuperclasses().containsName(superclassName);
    }

    public boolean isInnerClass() {
        return !this.getOuterClasses().isEmpty();
    }

    public boolean isOuterClass() {
        return !this.getInnerClasses().isEmpty();
    }

    public boolean isAnonymousInnerClass() {
        return this.fullyQualifiedDefiningMethodName != null;
    }

    public boolean isImplementedInterface() {
        return this.relatedClasses.get((Object)RelType.CLASSES_IMPLEMENTING) != null || this.isInterface();
    }

    public boolean implementsInterface(Class<?> interfaceClazz) {
        Assert.isInterface(interfaceClazz);
        return this.implementsInterface(interfaceClazz.getName());
    }

    public boolean implementsInterface(String interfaceName) {
        return this.getInterfaces().containsName(interfaceName);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.hasAnnotation(annotation.getName());
    }

    public boolean hasAnnotation(String annotationName) {
        return this.getAnnotations().containsName(annotationName);
    }

    public boolean hasDeclaredField(String fieldName) {
        return this.getDeclaredFieldInfo().containsName(fieldName);
    }

    public boolean hasField(String fieldName) {
        for (ClassInfo ci : this.getFieldOverrideOrder()) {
            if (!ci.hasDeclaredField(fieldName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasDeclaredFieldAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.hasDeclaredFieldAnnotation(annotation.getName());
    }

    public boolean hasDeclaredFieldAnnotation(String fieldAnnotationName) {
        for (FieldInfo fi : this.getDeclaredFieldInfo()) {
            if (!fi.hasAnnotation(fieldAnnotationName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasFieldAnnotation(Class<? extends Annotation> fieldAnnotation) {
        Assert.isAnnotation(fieldAnnotation);
        return this.hasFieldAnnotation(fieldAnnotation.getName());
    }

    public boolean hasFieldAnnotation(String fieldAnnotationName) {
        for (ClassInfo ci : this.getFieldOverrideOrder()) {
            if (!ci.hasDeclaredFieldAnnotation(fieldAnnotationName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasDeclaredMethod(String methodName) {
        return this.getDeclaredMethodInfo().containsName(methodName);
    }

    public boolean hasMethod(String methodName) {
        for (ClassInfo ci : this.getMethodOverrideOrder()) {
            if (!ci.hasDeclaredMethod(methodName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasDeclaredMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
        Assert.isAnnotation(methodAnnotation);
        return this.hasDeclaredMethodAnnotation(methodAnnotation.getName());
    }

    public boolean hasDeclaredMethodAnnotation(String methodAnnotationName) {
        for (MethodInfo mi : this.getDeclaredMethodInfo()) {
            if (!mi.hasAnnotation(methodAnnotationName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
        Assert.isAnnotation(methodAnnotation);
        return this.hasMethodAnnotation(methodAnnotation.getName());
    }

    public boolean hasMethodAnnotation(String methodAnnotationName) {
        for (ClassInfo ci : this.getMethodOverrideOrder()) {
            if (!ci.hasDeclaredMethodAnnotation(methodAnnotationName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasDeclaredMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
        Assert.isAnnotation(methodParameterAnnotation);
        return this.hasDeclaredMethodParameterAnnotation(methodParameterAnnotation.getName());
    }

    public boolean hasDeclaredMethodParameterAnnotation(String methodParameterAnnotationName) {
        for (MethodInfo mi : this.getDeclaredMethodInfo()) {
            if (!mi.hasParameterAnnotation(methodParameterAnnotationName)) continue;
            return true;
        }
        return false;
    }

    public boolean hasMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
        Assert.isAnnotation(methodParameterAnnotation);
        return this.hasMethodParameterAnnotation(methodParameterAnnotation.getName());
    }

    public boolean hasMethodParameterAnnotation(String methodParameterAnnotationName) {
        for (ClassInfo ci : this.getMethodOverrideOrder()) {
            if (!ci.hasDeclaredMethodParameterAnnotation(methodParameterAnnotationName)) continue;
            return true;
        }
        return false;
    }

    private List<ClassInfo> getFieldOverrideOrder(Set<ClassInfo> visited, List<ClassInfo> overrideOrderOut) {
        if (visited.add(this)) {
            overrideOrderOut.add(this);
            for (ClassInfo iface : this.getInterfaces()) {
                iface.getFieldOverrideOrder(visited, overrideOrderOut);
            }
            ClassInfo superclass = this.getSuperclass();
            if (superclass != null) {
                superclass.getFieldOverrideOrder(visited, overrideOrderOut);
            }
        }
        return overrideOrderOut;
    }

    private List<ClassInfo> getFieldOverrideOrder() {
        if (this.overrideOrder == null) {
            this.overrideOrder = this.getFieldOverrideOrder(new HashSet<ClassInfo>(), new ArrayList<ClassInfo>());
        }
        return this.overrideOrder;
    }

    private List<ClassInfo> getMethodOverrideOrder(Set<ClassInfo> visited, List<ClassInfo> overrideOrderOut) {
        if (!visited.add(this)) {
            return overrideOrderOut;
        }
        if (!this.isInterfaceOrAnnotation()) {
            overrideOrderOut.add(this);
            ClassInfo superclass = this.getSuperclass();
            if (superclass != null) {
                superclass.getMethodOverrideOrder(visited, overrideOrderOut);
            }
            for (ClassInfo iface : this.getInterfaces()) {
                iface.getMethodOverrideOrder(visited, overrideOrderOut);
            }
            return overrideOrderOut;
        }
        ClassInfoList interfaces = this.getInterfaces();
        int minIndex = Integer.MAX_VALUE;
        for (ClassInfo iface : interfaces) {
            if (!visited.contains(iface)) continue;
            int currIdx = overrideOrderOut.indexOf(iface);
            minIndex = currIdx >= 0 && currIdx < minIndex ? currIdx : minIndex;
        }
        if (minIndex == Integer.MAX_VALUE) {
            overrideOrderOut.add(this);
        } else {
            overrideOrderOut.add(minIndex, this);
        }
        for (ClassInfo iface : interfaces) {
            iface.getMethodOverrideOrder(visited, overrideOrderOut);
        }
        return overrideOrderOut;
    }

    private List<ClassInfo> getMethodOverrideOrder() {
        if (this.methodOverrideOrder == null) {
            this.methodOverrideOrder = this.getMethodOverrideOrder(new HashSet<ClassInfo>(), new ArrayList<ClassInfo>());
        }
        return this.methodOverrideOrder;
    }

    public ClassInfoList getSubclasses() {
        if (this.getName().equals("java.lang.Object")) {
            return this.scanResult.getAllStandardClasses();
        }
        return new ClassInfoList(this.filterClassInfo(RelType.SUBCLASSES, !this.isExternalClass, new ClassType[0]), true);
    }

    public ClassInfoList getSuperclasses() {
        return new ClassInfoList(this.filterClassInfo(RelType.SUPERCLASSES, false, new ClassType[0]), false);
    }

    public ClassInfo getSuperclass() {
        Set<ClassInfo> superClasses = this.relatedClasses.get((Object)RelType.SUPERCLASSES);
        if (superClasses == null || superClasses.isEmpty()) {
            return null;
        }
        if (superClasses.size() > 2) {
            throw new IllegalArgumentException("More than one superclass: " + superClasses);
        }
        ClassInfo superclass = superClasses.iterator().next();
        if (superclass.getName().equals("java.lang.Object")) {
            return null;
        }
        return superclass;
    }

    public ClassInfoList getOuterClasses() {
        return new ClassInfoList(this.filterClassInfo(RelType.CONTAINED_WITHIN_OUTER_CLASS, false, new ClassType[0]), false);
    }

    public ClassInfoList getInnerClasses() {
        return new ClassInfoList(this.filterClassInfo(RelType.CONTAINS_INNER_CLASS, false, new ClassType[0]), true);
    }

    public String getFullyQualifiedDefiningMethodName() {
        return this.fullyQualifiedDefiningMethodName;
    }

    public ClassInfoList getInterfaces() {
        ReachableAndDirectlyRelatedClasses implementedInterfaces = this.filterClassInfo(RelType.IMPLEMENTED_INTERFACES, false, new ClassType[0]);
        LinkedHashSet<ClassInfo> allInterfaces = new LinkedHashSet<ClassInfo>(implementedInterfaces.reachableClasses);
        for (ClassInfo superclass : this.filterClassInfo((RelType)RelType.SUPERCLASSES, (boolean)false, (ClassType[])new ClassType[0]).reachableClasses) {
            Set<ClassInfo> superclassImplementedInterfaces = superclass.filterClassInfo((RelType)RelType.IMPLEMENTED_INTERFACES, (boolean)false, (ClassType[])new ClassType[0]).reachableClasses;
            allInterfaces.addAll(superclassImplementedInterfaces);
        }
        return new ClassInfoList(allInterfaces, implementedInterfaces.directlyRelatedClasses, false);
    }

    public ClassInfoList getClassesImplementing() {
        ReachableAndDirectlyRelatedClasses implementingClasses = this.filterClassInfo(RelType.CLASSES_IMPLEMENTING, !this.isExternalClass, new ClassType[0]);
        LinkedHashSet<ClassInfo> allImplementingClasses = new LinkedHashSet<ClassInfo>(implementingClasses.reachableClasses);
        for (ClassInfo implementingClass : implementingClasses.reachableClasses) {
            Set<ClassInfo> implementingSubclasses = implementingClass.filterClassInfo((RelType)RelType.SUBCLASSES, (boolean)(!implementingClass.isExternalClass ? true : false), (ClassType[])new ClassType[0]).reachableClasses;
            allImplementingClasses.addAll(implementingSubclasses);
        }
        return new ClassInfoList(allImplementingClasses, implementingClasses.directlyRelatedClasses, true);
    }

    public ClassInfoList getAnnotations() {
        if (!this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        ReachableAndDirectlyRelatedClasses annotationClasses = this.filterClassInfo(RelType.CLASS_ANNOTATIONS, false, new ClassType[0]);
        LinkedHashSet<ClassInfo> inheritedSuperclassAnnotations = null;
        for (ClassInfo superclass : this.getSuperclasses()) {
            for (ClassInfo superclassAnnotation : superclass.filterClassInfo((RelType)RelType.CLASS_ANNOTATIONS, (boolean)false, (ClassType[])new ClassType[0]).reachableClasses) {
                if (superclassAnnotation == null || !superclassAnnotation.isInherited) continue;
                if (inheritedSuperclassAnnotations == null) {
                    inheritedSuperclassAnnotations = new LinkedHashSet<ClassInfo>();
                }
                inheritedSuperclassAnnotations.add(superclassAnnotation);
            }
        }
        if (inheritedSuperclassAnnotations == null) {
            return new ClassInfoList(annotationClasses, true);
        }
        inheritedSuperclassAnnotations.addAll(annotationClasses.reachableClasses);
        return new ClassInfoList((Set<ClassInfo>)inheritedSuperclassAnnotations, annotationClasses.directlyRelatedClasses, true);
    }

    private ClassInfoList getFieldOrMethodAnnotations(RelType relType) {
        boolean isField;
        boolean bl = isField = relType == RelType.FIELD_ANNOTATIONS;
        if (!(isField ? this.scanResult.scanSpec.enableFieldInfo : this.scanResult.scanSpec.enableMethodInfo) || !this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enable" + (isField ? "Field" : "Method") + "Info() and #enableAnnotationInfo() before #scan()");
        }
        ReachableAndDirectlyRelatedClasses fieldOrMethodAnnotations = this.filterClassInfo(relType, false, ClassType.ANNOTATION);
        LinkedHashSet<ClassInfo> fieldOrMethodAnnotationsAndMetaAnnotations = new LinkedHashSet<ClassInfo>(fieldOrMethodAnnotations.reachableClasses);
        return new ClassInfoList(fieldOrMethodAnnotationsAndMetaAnnotations, fieldOrMethodAnnotations.directlyRelatedClasses, true);
    }

    private ClassInfoList getClassesWithFieldOrMethodAnnotation(RelType relType) {
        boolean isField;
        boolean bl = isField = relType == RelType.CLASSES_WITH_FIELD_ANNOTATION || relType == RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION;
        if (!(isField ? this.scanResult.scanSpec.enableFieldInfo : this.scanResult.scanSpec.enableMethodInfo) || !this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enable" + (isField ? "Field" : "Method") + "Info() and #enableAnnotationInfo() before #scan()");
        }
        ReachableAndDirectlyRelatedClasses classesWithDirectlyAnnotatedFieldsOrMethods = this.filterClassInfo(relType, !this.isExternalClass, new ClassType[0]);
        ReachableAndDirectlyRelatedClasses annotationsWithThisMetaAnnotation = this.filterClassInfo(RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass, ClassType.ANNOTATION);
        if (annotationsWithThisMetaAnnotation.reachableClasses.isEmpty()) {
            return new ClassInfoList(classesWithDirectlyAnnotatedFieldsOrMethods, true);
        }
        LinkedHashSet<ClassInfo> allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods = new LinkedHashSet<ClassInfo>(classesWithDirectlyAnnotatedFieldsOrMethods.reachableClasses);
        for (ClassInfo metaAnnotatedAnnotation : annotationsWithThisMetaAnnotation.reachableClasses) {
            allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods.addAll(metaAnnotatedAnnotation.filterClassInfo((RelType)relType, (boolean)(!metaAnnotatedAnnotation.isExternalClass ? true : false), (ClassType[])new ClassType[0]).reachableClasses);
        }
        return new ClassInfoList(allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods, classesWithDirectlyAnnotatedFieldsOrMethods.directlyRelatedClasses, true);
    }

    public AnnotationInfoList getAnnotationInfo() {
        if (!this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        return AnnotationInfoList.getIndirectAnnotations(this.annotationInfo, this);
    }

    public AnnotationInfo getAnnotationInfo(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getAnnotationInfo(annotation.getName());
    }

    public AnnotationInfo getAnnotationInfo(String annotationName) {
        return (AnnotationInfo)this.getAnnotationInfo().get(annotationName);
    }

    public AnnotationInfoList getAnnotationInfoRepeatable(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getAnnotationInfoRepeatable(annotation.getName());
    }

    public AnnotationInfoList getAnnotationInfoRepeatable(String annotationName) {
        return this.getAnnotationInfo().getRepeatable(annotationName);
    }

    public AnnotationParameterValueList getAnnotationDefaultParameterValues() {
        if (!this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        if (!this.isAnnotation()) {
            throw new IllegalArgumentException("Class is not an annotation: " + this.getName());
        }
        if (this.annotationDefaultParamValues == null) {
            return AnnotationParameterValueList.EMPTY_LIST;
        }
        if (!this.annotationDefaultParamValuesHasBeenConvertedToPrimitive) {
            this.annotationDefaultParamValues.convertWrapperArraysToPrimitiveArrays(this);
            this.annotationDefaultParamValuesHasBeenConvertedToPrimitive = true;
        }
        return this.annotationDefaultParamValues;
    }

    public ClassInfoList getClassesWithAnnotation() {
        if (!this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        ReachableAndDirectlyRelatedClasses classesWithAnnotation = this.filterClassInfo(RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass, new ClassType[0]);
        if (this.isInherited) {
            LinkedHashSet<ClassInfo> classesWithAnnotationAndTheirSubclasses = new LinkedHashSet<ClassInfo>(classesWithAnnotation.reachableClasses);
            for (ClassInfo classWithAnnotation : classesWithAnnotation.reachableClasses) {
                classesWithAnnotationAndTheirSubclasses.addAll(classWithAnnotation.getSubclasses());
            }
            return new ClassInfoList(classesWithAnnotationAndTheirSubclasses, classesWithAnnotation.directlyRelatedClasses, true);
        }
        return new ClassInfoList(classesWithAnnotation, true);
    }

    ClassInfoList getClassesWithAnnotationDirectOnly() {
        return new ClassInfoList(this.filterClassInfo(RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass, new ClassType[0]), true);
    }

    private MethodInfoList getDeclaredMethodInfo(String methodName, boolean getNormalMethods, boolean getConstructorMethods, boolean getStaticInitializerMethods) {
        if (!this.scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        if (this.methodInfo == null) {
            return MethodInfoList.EMPTY_LIST;
        }
        if (methodName == null) {
            MethodInfoList methodInfoList = new MethodInfoList();
            for (MethodInfo mi : this.methodInfo) {
                String miName = mi.getName();
                boolean isConstructor = "<init>".equals(miName);
                boolean isStaticInitializer = "<clinit>".equals(miName);
                if (!(isConstructor && getConstructorMethods || isStaticInitializer && getStaticInitializerMethods) && (isConstructor || isStaticInitializer || !getNormalMethods)) continue;
                methodInfoList.add(mi);
            }
            return methodInfoList;
        }
        boolean hasMethodWithName = false;
        for (MethodInfo f : this.methodInfo) {
            if (!f.getName().equals(methodName)) continue;
            hasMethodWithName = true;
            break;
        }
        if (!hasMethodWithName) {
            return MethodInfoList.EMPTY_LIST;
        }
        MethodInfoList methodInfoList = new MethodInfoList();
        for (MethodInfo mi : this.methodInfo) {
            if (!mi.getName().equals(methodName)) continue;
            methodInfoList.add(mi);
        }
        return methodInfoList;
    }

    private MethodInfoList getMethodInfo(String methodName, boolean getNormalMethods, boolean getConstructorMethods, boolean getStaticInitializerMethods) {
        if (!this.scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        MethodInfoList methodInfoList = new MethodInfoList();
        HashSet<AbstractMap.SimpleEntry<String, String>> nameAndTypeDescriptorSet = new HashSet<AbstractMap.SimpleEntry<String, String>>();
        for (ClassInfo ci : this.getMethodOverrideOrder()) {
            for (MethodInfo mi : ci.getDeclaredMethodInfo(methodName, getNormalMethods, getConstructorMethods, getStaticInitializerMethods)) {
                if (!nameAndTypeDescriptorSet.add(new AbstractMap.SimpleEntry<String, String>(mi.getName(), mi.getTypeDescriptorStr()))) continue;
                methodInfoList.add(mi);
            }
        }
        return methodInfoList;
    }

    public MethodInfoList getDeclaredMethodInfo() {
        return this.getDeclaredMethodInfo(null, true, false, false);
    }

    public MethodInfoList getMethodInfo() {
        return this.getMethodInfo(null, true, false, false);
    }

    public MethodInfoList getDeclaredConstructorInfo() {
        return this.getDeclaredMethodInfo(null, false, true, false);
    }

    public MethodInfoList getConstructorInfo() {
        return this.getMethodInfo(null, false, true, false);
    }

    public MethodInfoList getDeclaredMethodAndConstructorInfo() {
        return this.getDeclaredMethodInfo(null, true, true, false);
    }

    public MethodInfoList getMethodAndConstructorInfo() {
        return this.getMethodInfo(null, true, true, false);
    }

    public MethodInfoList getDeclaredMethodInfo(String methodName) {
        return this.getDeclaredMethodInfo(methodName, false, false, false);
    }

    public MethodInfoList getMethodInfo(String methodName) {
        return this.getMethodInfo(methodName, false, false, false);
    }

    public ClassInfoList getMethodAnnotations() {
        return this.getFieldOrMethodAnnotations(RelType.METHOD_ANNOTATIONS);
    }

    public ClassInfoList getMethodParameterAnnotations() {
        return this.getFieldOrMethodAnnotations(RelType.METHOD_PARAMETER_ANNOTATIONS);
    }

    public ClassInfoList getClassesWithMethodAnnotation() {
        HashSet<ClassInfo> classesWithMethodAnnotation = new HashSet<ClassInfo>(this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_METHOD_ANNOTATION));
        for (ClassInfo classWithNonprivateMethodAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION)) {
            classesWithMethodAnnotation.addAll(classWithNonprivateMethodAnnotationOrMetaAnnotation.getSubclasses());
        }
        return new ClassInfoList(classesWithMethodAnnotation, new HashSet<ClassInfo>(this.getClassesWithMethodAnnotationDirectOnly()), true);
    }

    public ClassInfoList getClassesWithMethodParameterAnnotation() {
        HashSet<ClassInfo> classesWithMethodParameterAnnotation = new HashSet<ClassInfo>(this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION));
        for (ClassInfo classWithNonprivateMethodParameterAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION)) {
            classesWithMethodParameterAnnotation.addAll(classWithNonprivateMethodParameterAnnotationOrMetaAnnotation.getSubclasses());
        }
        return new ClassInfoList(classesWithMethodParameterAnnotation, new HashSet<ClassInfo>(this.getClassesWithMethodParameterAnnotationDirectOnly()), true);
    }

    ClassInfoList getClassesWithMethodAnnotationDirectOnly() {
        return new ClassInfoList(this.filterClassInfo(RelType.CLASSES_WITH_METHOD_ANNOTATION, !this.isExternalClass, new ClassType[0]), true);
    }

    ClassInfoList getClassesWithMethodParameterAnnotationDirectOnly() {
        return new ClassInfoList(this.filterClassInfo(RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, !this.isExternalClass, new ClassType[0]), true);
    }

    public FieldInfoList getDeclaredFieldInfo() {
        if (!this.scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        return this.fieldInfo == null ? FieldInfoList.EMPTY_LIST : this.fieldInfo;
    }

    public FieldInfoList getFieldInfo() {
        if (!this.scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        FieldInfoList fieldInfoList = new FieldInfoList();
        HashSet<String> fieldNameSet = new HashSet<String>();
        for (ClassInfo ci : this.getFieldOverrideOrder()) {
            for (FieldInfo fi : ci.getDeclaredFieldInfo()) {
                if (!fieldNameSet.add(fi.getName())) continue;
                fieldInfoList.add(fi);
            }
        }
        return fieldInfoList;
    }

    public FieldInfoList getEnumConstants() {
        if (!this.isEnum()) {
            throw new IllegalArgumentException("Class " + this.getName() + " is not an enum");
        }
        return this.getFieldInfo().filter(new FieldInfoList.FieldInfoFilter(){

            @Override
            public boolean accept(FieldInfo fieldInfo) {
                return fieldInfo.isEnum();
            }
        });
    }

    public List<Object> getEnumConstantObjects() {
        if (!this.isEnum()) {
            throw new IllegalArgumentException("Class " + this.getName() + " is not an enum");
        }
        Class<?> enumClass = this.loadClass();
        FieldInfoList consts = this.getEnumConstants();
        ArrayList<Object> constObjs = new ArrayList<Object>(consts.size());
        ReflectionUtils reflectionUtils = this.scanResult == null ? new ReflectionUtils() : this.scanResult.reflectionUtils;
        for (FieldInfo constFieldInfo : consts) {
            Object constObj = reflectionUtils.getStaticFieldVal(true, enumClass, constFieldInfo.getName());
            if (constObj == null) {
                throw new IllegalArgumentException("Could not read enum constant objects");
            }
            constObjs.add(constObj);
        }
        return constObjs;
    }

    public FieldInfo getDeclaredFieldInfo(String fieldName) {
        if (!this.scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        if (this.fieldInfo == null) {
            return null;
        }
        for (FieldInfo fi : this.fieldInfo) {
            if (!fi.getName().equals(fieldName)) continue;
            return fi;
        }
        return null;
    }

    public FieldInfo getFieldInfo(String fieldName) {
        if (!this.scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        for (ClassInfo ci : this.getFieldOverrideOrder()) {
            FieldInfo fi = ci.getDeclaredFieldInfo(fieldName);
            if (fi == null) continue;
            return fi;
        }
        return null;
    }

    public ClassInfoList getFieldAnnotations() {
        return this.getFieldOrMethodAnnotations(RelType.FIELD_ANNOTATIONS);
    }

    public ClassInfoList getClassesWithFieldAnnotation() {
        HashSet<ClassInfo> classesWithMethodAnnotation = new HashSet<ClassInfo>(this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_FIELD_ANNOTATION));
        for (ClassInfo classWithNonprivateMethodAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION)) {
            classesWithMethodAnnotation.addAll(classWithNonprivateMethodAnnotationOrMetaAnnotation.getSubclasses());
        }
        return new ClassInfoList(classesWithMethodAnnotation, new HashSet<ClassInfo>(this.getClassesWithMethodAnnotationDirectOnly()), true);
    }

    ClassInfoList getClassesWithFieldAnnotationDirectOnly() {
        return new ClassInfoList(this.filterClassInfo(RelType.CLASSES_WITH_FIELD_ANNOTATION, !this.isExternalClass, new ClassType[0]), true);
    }

    public ClassTypeSignature getTypeSignature() {
        if (this.typeSignatureStr == null) {
            return null;
        }
        if (this.typeSignature == null) {
            try {
                this.typeSignature = ClassTypeSignature.parse(this.typeSignatureStr, this);
                this.typeSignature.setScanResult(this.scanResult);
                if (this.typeAnnotationDecorators != null) {
                    for (Classfile.ClassTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                        decorator.decorate(this.typeSignature);
                    }
                }
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("Invalid type signature for class " + this.getName() + " in classpath element " + this.getClasspathElementURI() + " : " + this.typeSignatureStr, e);
            }
        }
        return this.typeSignature;
    }

    public String getTypeSignatureStr() {
        return this.typeSignatureStr;
    }

    public ClassTypeSignature getTypeSignatureOrTypeDescriptor() {
        ClassTypeSignature typeSig = null;
        try {
            typeSig = this.getTypeSignature();
            if (typeSig != null) {
                return typeSig;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.getTypeDescriptor();
    }

    public ClassTypeSignature getTypeDescriptor() {
        if (this.typeDescriptor == null) {
            this.typeDescriptor = new ClassTypeSignature(this, this.getSuperclass(), this.getInterfaces());
            this.typeDescriptor.setScanResult(this.scanResult);
            if (this.typeAnnotationDecorators != null) {
                for (Classfile.ClassTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                    decorator.decorate(this.typeDescriptor);
                }
            }
        }
        return this.typeDescriptor;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public URI getClasspathElementURI() {
        return this.classfileResource.getClasspathElementURI();
    }

    public URL getClasspathElementURL() {
        try {
            return this.getClasspathElementURI().toURL();
        }
        catch (IllegalArgumentException | MalformedURLException e) {
            throw new IllegalArgumentException("Could not get classpath element URL", e);
        }
    }

    public File getClasspathElementFile() {
        if (this.classpathElement == null) {
            throw new IllegalArgumentException("Classpath element is not known for this classpath element");
        }
        return this.classpathElement.getFile();
    }

    public ModuleRef getModuleRef() {
        if (this.classpathElement == null) {
            throw new IllegalArgumentException("Classpath element is not known for this classpath element");
        }
        return this.classpathElement instanceof ClasspathElementModule ? ((ClasspathElementModule)this.classpathElement).getModuleRef() : null;
    }

    public Resource getResource() {
        return this.classfileResource;
    }

    @Override
    public <T> Class<T> loadClass(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
        return super.loadClass(superclassOrInterfaceType, ignoreExceptions);
    }

    @Override
    public <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
        return super.loadClass(superclassOrInterfaceType, false);
    }

    @Override
    public Class<?> loadClass(boolean ignoreExceptions) {
        return super.loadClass(ignoreExceptions);
    }

    @Override
    public Class<?> loadClass() {
        return super.loadClass(false);
    }

    @Override
    protected String getClassName() {
        return this.name;
    }

    @Override
    protected ClassInfo getClassInfo() {
        return this;
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
        if (this.annotationInfo != null) {
            for (AnnotationInfo ai : this.annotationInfo) {
                ai.setScanResult(scanResult);
            }
        }
        if (this.fieldInfo != null) {
            for (FieldInfo fi : this.fieldInfo) {
                fi.setScanResult(scanResult);
            }
        }
        if (this.methodInfo != null) {
            for (MethodInfo mi : this.methodInfo) {
                mi.setScanResult(scanResult);
            }
        }
        if (this.annotationDefaultParamValues != null) {
            for (AnnotationParameterValue apv : this.annotationDefaultParamValues) {
                apv.setScanResult(scanResult);
            }
        }
    }

    void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
        if (this.annotationInfo != null) {
            this.annotationInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames, this, RelType.CLASS_ANNOTATIONS, RelType.CLASSES_WITH_ANNOTATION, null);
        }
        if (this.fieldInfo != null) {
            for (FieldInfo fi : this.fieldInfo) {
                fi.handleRepeatableAnnotations(allRepeatableAnnotationNames);
            }
        }
        if (this.methodInfo != null) {
            for (MethodInfo mi : this.methodInfo) {
                mi.handleRepeatableAnnotations(allRepeatableAnnotationNames);
            }
        }
    }

    void addReferencedClassNames(Set<String> refdClassNames) {
        if (this.referencedClassNames == null) {
            this.referencedClassNames = refdClassNames;
        } else {
            this.referencedClassNames.addAll(refdClassNames);
        }
    }

    @Override
    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        block6: {
            super.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            if (this.referencedClassNames != null) {
                for (String refdClassName : this.referencedClassNames) {
                    ClassInfo classInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
                    classInfo.setScanResult(this.scanResult);
                    refdClassInfo.add(classInfo);
                }
            }
            this.getMethodInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            this.getFieldInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            this.getAnnotationInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            if (this.annotationDefaultParamValues != null) {
                this.annotationDefaultParamValues.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            }
            try {
                ClassTypeSignature classSig = this.getTypeSignature();
                if (classSig != null) {
                    classSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
                }
            }
            catch (IllegalArgumentException e) {
                if (log == null) break block6;
                log.log("Illegal type signature for class " + this.getClassName() + ": " + this.getTypeSignatureStr());
            }
        }
    }

    void setReferencedClasses(ClassInfoList refdClasses) {
        this.referencedClasses = refdClasses;
    }

    public ClassInfoList getClassDependencies() {
        if (!this.scanResult.scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
        }
        return this.referencedClasses == null ? ClassInfoList.EMPTY_LIST : this.referencedClasses;
    }

    @Override
    public int compareTo(ClassInfo o) {
        return this.name.compareTo(o.name);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClassInfo)) {
            return false;
        }
        ClassInfo other = (ClassInfo)obj;
        return this.name.equals(other.name);
    }

    public int hashCode() {
        return this.name == null ? 0 : this.name.hashCode();
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        boolean initialBufEmpty;
        boolean bl = initialBufEmpty = buf.length() == 0;
        if (this.annotationInfo != null) {
            for (AnnotationInfo annotation : this.annotationInfo) {
                if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ' && buf.charAt(buf.length() - 1) != '(') {
                    buf.append(' ');
                }
                annotation.toString(useSimpleNames, buf);
            }
        }
        ClassTypeSignature typeSig = null;
        try {
            typeSig = this.getTypeSignature();
        }
        catch (Exception annotation) {
            // empty catch block
        }
        if (typeSig != null) {
            typeSig.toStringInternal(useSimpleNames ? ClassInfo.getSimpleName(this.name) : this.name, false, this.modifiers, this.isAnnotation(), this.isInterface(), this.annotationInfo, buf);
        } else {
            Set<ClassInfo> interfaces;
            ClassInfo superclass;
            TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.CLASS, false, buf);
            if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ' && buf.charAt(buf.length() - 1) != '(') {
                buf.append(' ');
            }
            if (initialBufEmpty) {
                buf.append(this.isRecord() ? "record " : (this.isEnum() ? "enum " : (this.isAnnotation() ? "@interface " : (this.isInterface() ? "interface " : "class "))));
            }
            buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.name) : this.name);
            if (this.isRecord) {
                buf.append('(');
                boolean isFirstParam = true;
                for (FieldInfo fieldInfo : this.getFieldInfo()) {
                    if (!isFirstParam) {
                        buf.append(", ");
                    } else {
                        isFirstParam = false;
                    }
                    fieldInfo.toString(false, false, buf);
                }
                buf.append(')');
            }
            if ((superclass = this.getSuperclass()) != null && !superclass.getName().equals("java.lang.Object")) {
                buf.append(" extends ");
                superclass.toString(useSimpleNames, buf);
            }
            if (!(interfaces = this.filterClassInfo((RelType)RelType.IMPLEMENTED_INTERFACES, (boolean)false, (ClassType[])new ClassType[0]).directlyRelatedClasses).isEmpty()) {
                buf.append(this.isInterface() ? " extends " : " implements ");
                boolean first = true;
                for (ClassInfo iface : interfaces) {
                    if (first) {
                        first = false;
                    } else {
                        buf.append(", ");
                    }
                    iface.toString(useSimpleNames, buf);
                }
            }
        }
    }

    static class ReachableAndDirectlyRelatedClasses {
        final Set<ClassInfo> reachableClasses;
        final Set<ClassInfo> directlyRelatedClasses;

        private ReachableAndDirectlyRelatedClasses(Set<ClassInfo> reachableClasses, Set<ClassInfo> directlyRelatedClasses) {
            this.reachableClasses = reachableClasses;
            this.directlyRelatedClasses = directlyRelatedClasses;
        }
    }

    private static enum ClassType {
        ALL,
        STANDARD_CLASS,
        IMPLEMENTED_INTERFACE,
        ANNOTATION,
        INTERFACE_OR_ANNOTATION,
        ENUM,
        RECORD;

    }

    static enum RelType {
        SUPERCLASSES,
        SUBCLASSES,
        CONTAINS_INNER_CLASS,
        CONTAINED_WITHIN_OUTER_CLASS,
        IMPLEMENTED_INTERFACES,
        CLASSES_IMPLEMENTING,
        CLASS_ANNOTATIONS,
        CLASSES_WITH_ANNOTATION,
        METHOD_ANNOTATIONS,
        CLASSES_WITH_METHOD_ANNOTATION,
        CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION,
        METHOD_PARAMETER_ANNOTATIONS,
        CLASSES_WITH_METHOD_PARAMETER_ANNOTATION,
        CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION,
        FIELD_ANNOTATIONS,
        CLASSES_WITH_FIELD_ANNOTATION,
        CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION;

    }
}

