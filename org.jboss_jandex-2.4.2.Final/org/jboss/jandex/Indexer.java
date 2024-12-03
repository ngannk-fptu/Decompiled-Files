/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassExtendsTypeTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.EmptyTypeTarget;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.GenericSignatureParser;
import org.jboss.jandex.Index;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.MethodParameterTypeTarget;
import org.jboss.jandex.ModuleInfo;
import org.jboss.jandex.NameTable;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.ThrowsTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeParameterBoundTypeTarget;
import org.jboss.jandex.TypeParameterTypeTarget;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.Utils;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

public final class Indexer {
    private static final int CONSTANT_CLASS = 7;
    private static final int CONSTANT_FIELDREF = 9;
    private static final int CONSTANT_METHODREF = 10;
    private static final int CONSTANT_INTERFACEMETHODREF = 11;
    private static final int CONSTANT_STRING = 8;
    private static final int CONSTANT_INTEGER = 3;
    private static final int CONSTANT_FLOAT = 4;
    private static final int CONSTANT_LONG = 5;
    private static final int CONSTANT_DOUBLE = 6;
    private static final int CONSTANT_NAMEANDTYPE = 12;
    private static final int CONSTANT_UTF8 = 1;
    private static final int CONSTANT_INVOKEDYNAMIC = 18;
    private static final int CONSTANT_METHODHANDLE = 15;
    private static final int CONSTANT_METHODTYPE = 16;
    private static final int CONSTANT_MODULE = 19;
    private static final int CONSTANT_PACKAGE = 20;
    private static final int CONSTANT_DYNAMIC = 17;
    private static final byte[] RUNTIME_ANNOTATIONS = new byte[]{82, 117, 110, 116, 105, 109, 101, 86, 105, 115, 105, 98, 108, 101, 65, 110, 110, 111, 116, 97, 116, 105, 111, 110, 115};
    private static final byte[] RUNTIME_PARAM_ANNOTATIONS = new byte[]{82, 117, 110, 116, 105, 109, 101, 86, 105, 115, 105, 98, 108, 101, 80, 97, 114, 97, 109, 101, 116, 101, 114, 65, 110, 110, 111, 116, 97, 116, 105, 111, 110, 115};
    private static final byte[] RUNTIME_TYPE_ANNOTATIONS = new byte[]{82, 117, 110, 116, 105, 109, 101, 86, 105, 115, 105, 98, 108, 101, 84, 121, 112, 101, 65, 110, 110, 111, 116, 97, 116, 105, 111, 110, 115};
    private static final byte[] ANNOTATION_DEFAULT = new byte[]{65, 110, 110, 111, 116, 97, 116, 105, 111, 110, 68, 101, 102, 97, 117, 108, 116};
    private static final byte[] SIGNATURE = new byte[]{83, 105, 103, 110, 97, 116, 117, 114, 101};
    private static final byte[] EXCEPTIONS = new byte[]{69, 120, 99, 101, 112, 116, 105, 111, 110, 115};
    private static final byte[] INNER_CLASSES = new byte[]{73, 110, 110, 101, 114, 67, 108, 97, 115, 115, 101, 115};
    private static final byte[] ENCLOSING_METHOD = new byte[]{69, 110, 99, 108, 111, 115, 105, 110, 103, 77, 101, 116, 104, 111, 100};
    private static final byte[] METHOD_PARAMETERS = new byte[]{77, 101, 116, 104, 111, 100, 80, 97, 114, 97, 109, 101, 116, 101, 114, 115};
    private static final byte[] LOCAL_VARIABLE_TABLE = new byte[]{76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101};
    private static final byte[] CODE = new byte[]{67, 111, 100, 101};
    private static final byte[] MODULE = new byte[]{77, 111, 100, 117, 108, 101};
    private static final byte[] MODULE_PACKAGES = new byte[]{77, 111, 100, 117, 108, 101, 80, 97, 99, 107, 97, 103, 101, 115};
    private static final byte[] MODULE_MAIN_CLASS = new byte[]{77, 111, 100, 117, 108, 101, 77, 97, 105, 110, 67, 108, 97, 115, 115};
    private static final byte[] RECORD = new byte[]{82, 101, 99, 111, 114, 100};
    private static final int RUNTIME_ANNOTATIONS_LEN = RUNTIME_ANNOTATIONS.length;
    private static final int RUNTIME_PARAM_ANNOTATIONS_LEN = RUNTIME_PARAM_ANNOTATIONS.length;
    private static final int RUNTIME_TYPE_ANNOTATIONS_LEN = RUNTIME_TYPE_ANNOTATIONS.length;
    private static final int ANNOTATION_DEFAULT_LEN = ANNOTATION_DEFAULT.length;
    private static final int SIGNATURE_LEN = SIGNATURE.length;
    private static final int EXCEPTIONS_LEN = EXCEPTIONS.length;
    private static final int INNER_CLASSES_LEN = INNER_CLASSES.length;
    private static final int ENCLOSING_METHOD_LEN = ENCLOSING_METHOD.length;
    private static final int METHOD_PARAMETERS_LEN = METHOD_PARAMETERS.length;
    private static final int LOCAL_VARIABLE_TABLE_LEN = LOCAL_VARIABLE_TABLE.length;
    private static final int CODE_LEN = CODE.length;
    private static final int MODULE_LEN = MODULE.length;
    private static final int MODULE_PACKAGES_LEN = MODULE_PACKAGES.length;
    private static final int MODULE_MAIN_CLASS_LEN = MODULE_MAIN_CLASS.length;
    private static final int RECORD_LEN = RECORD.length;
    private static final int HAS_RUNTIME_ANNOTATION = 1;
    private static final int HAS_RUNTIME_PARAM_ANNOTATION = 2;
    private static final int HAS_RUNTIME_TYPE_ANNOTATION = 3;
    private static final int HAS_SIGNATURE = 4;
    private static final int HAS_EXCEPTIONS = 5;
    private static final int HAS_INNER_CLASSES = 6;
    private static final int HAS_ENCLOSING_METHOD = 7;
    private static final int HAS_ANNOTATION_DEFAULT = 8;
    private static final int HAS_METHOD_PARAMETERS = 9;
    private static final int HAS_LOCAL_VARIABLE_TABLE = 10;
    private static final int HAS_CODE = 11;
    private static final int HAS_MODULE = 12;
    private static final int HAS_MODULE_PACKAGES = 13;
    private static final int HAS_MODULE_MAIN_CLASS = 14;
    private static final int HAS_RECORD = 15;
    private static final byte[] INIT_METHOD_NAME = Utils.toUTF8("<init>");
    private byte[] constantPool;
    private int[] constantPoolOffsets;
    private byte[] constantPoolAnnoAttrributes;
    private ClassInfo currentClass;
    private HashMap<DotName, List<AnnotationInstance>> classAnnotations;
    private ArrayList<AnnotationInstance> elementAnnotations;
    private IdentityHashMap<AnnotationTarget, Object> signaturePresent;
    private List<Object> signatures;
    private int classSignatureIndex;
    private Map<DotName, InnerClassInfo> innerClasses;
    private IdentityHashMap<AnnotationTarget, List<TypeAnnotationState>> typeAnnotations;
    private List<MethodInfo> methods;
    private List<FieldInfo> fields;
    private List<RecordComponentInfo> recordComponents;
    private byte[][] debugParameterNames;
    private byte[][] methodParameterNames;
    private Map<DotName, List<AnnotationInstance>> masterAnnotations;
    private Map<DotName, List<ClassInfo>> subclasses;
    private Map<DotName, List<ClassInfo>> implementors;
    private Map<DotName, ClassInfo> classes;
    private Map<DotName, ModuleInfo> modules;
    private Map<DotName, List<ClassInfo>> users;
    private NameTable names;
    private GenericSignatureParser signatureParser;

    private static boolean match(byte[] target, int offset, byte[] expected) {
        if (target.length - offset < expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; ++i) {
            if (target[offset + i] == expected[i]) continue;
            return false;
        }
        return true;
    }

    private static byte[] sizeToFit(byte[] buf, int needed, int offset, int remainingEntries) {
        if (offset + needed > buf.length) {
            buf = Arrays.copyOf(buf, buf.length + Math.max(needed, (remainingEntries + 1) * 20));
        }
        return buf;
    }

    private static void skipFully(InputStream s, long n) throws IOException {
        long total = 0L;
        while (total < n) {
            long skipped = s.skip(n - total);
            if (skipped < 0L) {
                throw new EOFException();
            }
            total += skipped;
            if (skipped != 0L) continue;
            if (s.read() < 0) {
                throw new EOFException();
            }
            ++total;
        }
    }

    private void initIndexMaps() {
        if (this.masterAnnotations == null) {
            this.masterAnnotations = new HashMap<DotName, List<AnnotationInstance>>();
        }
        if (this.subclasses == null) {
            this.subclasses = new HashMap<DotName, List<ClassInfo>>();
        }
        if (this.implementors == null) {
            this.implementors = new HashMap<DotName, List<ClassInfo>>();
        }
        if (this.classes == null) {
            this.classes = new HashMap<DotName, ClassInfo>();
        }
        if (this.modules == null) {
            this.modules = new HashMap<DotName, ModuleInfo>();
        }
        if (this.users == null) {
            this.users = new HashMap<DotName, List<ClassInfo>>();
        }
        if (this.names == null) {
            this.names = new NameTable();
        }
        if (this.signatureParser == null) {
            this.signatureParser = new GenericSignatureParser(this.names);
        }
    }

    private void initClassFields() {
        this.elementAnnotations = new ArrayList();
        this.signaturePresent = new IdentityHashMap();
        this.signatures = new ArrayList<Object>();
        this.classSignatureIndex = -1;
        this.typeAnnotations = new IdentityHashMap();
        this.recordComponents = new ArrayList<RecordComponentInfo>();
    }

    private void processMethodInfo(DataInputStream data) throws IOException {
        int numMethods = data.readUnsignedShort();
        ArrayList<MethodInfo> methods = numMethods > 0 ? new ArrayList<MethodInfo>(numMethods) : Collections.emptyList();
        for (int i = 0; i < numMethods; ++i) {
            short flags = (short)data.readUnsignedShort();
            byte[] name = this.intern(this.decodeUtf8EntryAsBytes(data.readUnsignedShort()));
            String descriptor = this.decodeUtf8Entry(data.readUnsignedShort());
            IntegerHolder pos = new IntegerHolder();
            Type[] parameters = this.intern(this.parseMethodArgs(descriptor, pos));
            Type returnType = this.parseType(descriptor, pos);
            MethodInfo method = new MethodInfo(this.currentClass, name, MethodInternal.EMPTY_PARAMETER_NAMES, parameters, returnType, flags);
            if (parameters.length == 0 && Arrays.equals(INIT_METHOD_NAME, name)) {
                this.currentClass.setHasNoArgsConstructor(true);
            }
            this.debugParameterNames = null;
            this.methodParameterNames = this.debugParameterNames;
            this.processAttributes(data, method);
            method.setAnnotations(this.elementAnnotations);
            this.elementAnnotations.clear();
            if (this.methodParameterNames != null) {
                method.methodInternal().setParameterNames(this.methodParameterNames);
            } else if (this.debugParameterNames != null) {
                method.methodInternal().setParameterNames(this.debugParameterNames);
            }
            methods.add(method);
        }
        this.methods = methods;
    }

    private void processFieldInfo(DataInputStream data) throws IOException {
        int numFields = data.readUnsignedShort();
        ArrayList<FieldInfo> fields = numFields > 0 ? new ArrayList<FieldInfo>(numFields) : Collections.emptyList();
        for (int i = 0; i < numFields; ++i) {
            short flags = (short)data.readUnsignedShort();
            byte[] name = this.intern(this.decodeUtf8EntryAsBytes(data.readUnsignedShort()));
            Type type = this.parseType(this.decodeUtf8Entry(data.readUnsignedShort()));
            FieldInfo field = new FieldInfo(this.currentClass, name, type, flags);
            this.processAttributes(data, field);
            field.setAnnotations(this.elementAnnotations);
            this.elementAnnotations.clear();
            fields.add(field);
        }
        this.fields = fields;
    }

    private void processRecordComponents(DataInputStream data) throws IOException {
        int numComponents = data.readUnsignedShort();
        ArrayList<RecordComponentInfo> recordComponents = numComponents > 0 ? new ArrayList<RecordComponentInfo>(numComponents) : Collections.emptyList();
        for (int i = 0; i < numComponents; ++i) {
            byte[] name = this.intern(this.decodeUtf8EntryAsBytes(data.readUnsignedShort()));
            Type type = this.intern(this.parseType(this.decodeUtf8Entry(data.readUnsignedShort())));
            RecordComponentInfo component = new RecordComponentInfo(this.currentClass, name, type);
            this.processAttributes(data, component);
            component.setAnnotations(this.elementAnnotations);
            this.elementAnnotations.clear();
            recordComponents.add(component);
        }
        this.recordComponents = recordComponents;
    }

    private void processAttributes(DataInputStream data, AnnotationTarget target) throws IOException {
        int numAttrs = data.readUnsignedShort();
        for (int a = 0; a < numAttrs; ++a) {
            int index = data.readUnsignedShort();
            long attributeLen = (long)data.readInt() & 0xFFFFFFFFL;
            byte annotationAttribute = this.constantPoolAnnoAttrributes[index - 1];
            if (annotationAttribute == 1) {
                this.processAnnotations(data, target);
                continue;
            }
            if (annotationAttribute == 2) {
                if (!(target instanceof MethodInfo)) {
                    throw new IllegalStateException("RuntimeVisibleParameterAnnotations appeared on a non-method");
                }
                short s = data.readUnsignedByte();
                for (short p = 0; p < s; p = (short)(p + 1)) {
                    this.processAnnotations(data, new MethodParameterInfo((MethodInfo)target, p));
                }
                continue;
            }
            if (annotationAttribute == 3) {
                this.processTypeAnnotations(data, target);
                continue;
            }
            if (annotationAttribute == 4) {
                this.processSignature(data, target);
                continue;
            }
            if (annotationAttribute == 5 && target instanceof MethodInfo) {
                this.processExceptions(data, (MethodInfo)target);
                continue;
            }
            if (annotationAttribute == 6 && target instanceof ClassInfo) {
                this.processInnerClasses(data, (ClassInfo)target);
                continue;
            }
            if (annotationAttribute == 7 && target instanceof ClassInfo) {
                this.processEnclosingMethod(data, (ClassInfo)target);
                continue;
            }
            if (annotationAttribute == 8 && target instanceof MethodInfo) {
                this.processAnnotationDefault(data, (MethodInfo)target);
                continue;
            }
            if (annotationAttribute == 9 && target instanceof MethodInfo) {
                this.processMethodParameters(data, (MethodInfo)target);
                continue;
            }
            if (annotationAttribute == 11 && target instanceof MethodInfo) {
                this.processCode(data, (MethodInfo)target);
                continue;
            }
            if (annotationAttribute == 12 && target instanceof ClassInfo) {
                this.processModule(data, (ClassInfo)target);
                continue;
            }
            if (annotationAttribute == 13 && target instanceof ClassInfo) {
                this.processModulePackages(data, (ClassInfo)target);
                continue;
            }
            if (annotationAttribute == 14 && target instanceof ClassInfo) {
                this.processModuleMainClass(data, (ClassInfo)target);
                continue;
            }
            if (annotationAttribute == 15 && target instanceof ClassInfo) {
                this.processRecordComponents(data);
                continue;
            }
            Indexer.skipFully(data, attributeLen);
        }
    }

    private void processModule(DataInputStream data, ClassInfo target) throws IOException {
        if (!target.isModule()) {
            throw new IllegalStateException("Module attribute appeared in a non-module class file");
        }
        DotName moduleName = this.decodeModuleEntry(data.readUnsignedShort());
        int flags = data.readUnsignedShort();
        String version = this.decodeOptionalUtf8Entry(data.readUnsignedShort());
        ModuleInfo module = new ModuleInfo(target, moduleName, (short)flags, version);
        module.setRequires(this.processModuleRequires(data));
        module.setExports(this.processModuleExports(data));
        module.setOpens(this.processModuleOpens(data));
        module.setUses(this.processModuleUses(data));
        module.setProvides(this.processModuleProvides(data));
        this.modules.put(moduleName, module);
    }

    private List<ModuleInfo.RequiredModuleInfo> processModuleRequires(DataInputStream data) throws IOException {
        int requiresCount = data.readUnsignedShort();
        List<ModuleInfo.RequiredModuleInfo> requires = Utils.listOfCapacity(requiresCount);
        for (int i = 0; i < requiresCount; ++i) {
            DotName name = this.decodeModuleEntry(data.readUnsignedShort());
            int flags = data.readUnsignedShort();
            String version = this.decodeOptionalUtf8Entry(data.readUnsignedShort());
            requires.add(new ModuleInfo.RequiredModuleInfo(name, flags, version));
        }
        return requires;
    }

    private List<ModuleInfo.ExportedPackageInfo> processModuleExports(DataInputStream data) throws IOException {
        int exportsCount = data.readUnsignedShort();
        List<ModuleInfo.ExportedPackageInfo> exports = Utils.listOfCapacity(exportsCount);
        for (int i = 0; i < exportsCount; ++i) {
            DotName source = this.decodePackageEntry(data.readUnsignedShort());
            int flags = data.readUnsignedShort();
            int targetCount = data.readUnsignedShort();
            List<DotName> targets = Utils.listOfCapacity(targetCount);
            for (int j = 0; j < targetCount; ++j) {
                targets.add(this.decodeModuleEntry(data.readUnsignedShort()));
            }
            exports.add(new ModuleInfo.ExportedPackageInfo(source, flags, targets));
        }
        return exports;
    }

    private List<ModuleInfo.OpenedPackageInfo> processModuleOpens(DataInputStream data) throws IOException {
        int opensCount = data.readUnsignedShort();
        List<ModuleInfo.OpenedPackageInfo> opens = Utils.listOfCapacity(opensCount);
        for (int i = 0; i < opensCount; ++i) {
            DotName source = this.decodePackageEntry(data.readUnsignedShort());
            int flags = data.readUnsignedShort();
            int targetCount = data.readUnsignedShort();
            List<DotName> targets = Utils.listOfCapacity(targetCount);
            for (int j = 0; j < targetCount; ++j) {
                targets.add(this.decodeModuleEntry(data.readUnsignedShort()));
            }
            opens.add(new ModuleInfo.OpenedPackageInfo(source, flags, targets));
        }
        return opens;
    }

    private List<DotName> processModuleUses(DataInputStream data) throws IOException {
        int usesCount = data.readUnsignedShort();
        List<DotName> usesServices = Utils.listOfCapacity(usesCount);
        for (int j = 0; j < usesCount; ++j) {
            usesServices.add(this.decodeClassEntry(data.readUnsignedShort()));
        }
        return usesServices;
    }

    private List<ModuleInfo.ProvidedServiceInfo> processModuleProvides(DataInputStream data) throws IOException {
        int providesCount = data.readUnsignedShort();
        List<ModuleInfo.ProvidedServiceInfo> provides = Utils.listOfCapacity(providesCount);
        for (int i = 0; i < providesCount; ++i) {
            DotName service = this.decodeClassEntry(data.readUnsignedShort());
            int providerCount = data.readUnsignedShort();
            List<DotName> providers = Utils.listOfCapacity(providerCount);
            for (int j = 0; j < providerCount; ++j) {
                providers.add(this.decodeClassEntry(data.readUnsignedShort()));
            }
            provides.add(new ModuleInfo.ProvidedServiceInfo(service, providers));
        }
        return provides;
    }

    private void processModulePackages(DataInputStream data, ClassInfo target) throws IOException {
        if (!target.isModule()) {
            throw new IllegalStateException("ModulePackages attribute appeared in a non-module class file");
        }
        int packagesCount = data.readUnsignedShort();
        List<DotName> packages = Utils.listOfCapacity(packagesCount);
        for (int j = 0; j < packagesCount; ++j) {
            packages.add(this.decodePackageEntry(data.readUnsignedShort()));
        }
        target.module().setPackages(packages);
    }

    private void processModuleMainClass(DataInputStream data, ClassInfo target) throws IOException {
        if (!target.isModule()) {
            throw new IllegalStateException("ModuleMainClass attribute appeared in a non-module class file");
        }
        target.module().setMainClass(this.decodeClassEntry(data.readUnsignedShort()));
    }

    private void processCode(DataInputStream data, MethodInfo target) throws IOException {
        int maxStack = data.readUnsignedShort();
        int maxLocals = data.readUnsignedShort();
        long h = data.readUnsignedShort();
        long l = data.readUnsignedShort();
        long codeLength = h << 16 | l;
        Indexer.skipFully(data, codeLength);
        int exceptionTableLength = data.readUnsignedShort();
        Indexer.skipFully(data, exceptionTableLength * 8);
        int numAttrs = data.readUnsignedShort();
        for (int a = 0; a < numAttrs; ++a) {
            int index = data.readUnsignedShort();
            long attributeLen = (long)data.readInt() & 0xFFFFFFFFL;
            byte annotationAttribute = this.constantPoolAnnoAttrributes[index - 1];
            if (annotationAttribute == 10 && target instanceof MethodInfo) {
                this.processLocalVariableTable(data, target);
                continue;
            }
            Indexer.skipFully(data, attributeLen);
        }
    }

    private void processAnnotationDefault(DataInputStream data, MethodInfo target) throws IOException {
        target.setDefaultValue(this.processAnnotationElementValue(target.name(), data));
    }

    private void processAnnotations(DataInputStream data, AnnotationTarget target) throws IOException {
        int numAnnotations = data.readUnsignedShort();
        while (numAnnotations-- > 0) {
            this.processAnnotation(data, target);
        }
    }

    private void processInnerClasses(DataInputStream data, ClassInfo target) throws IOException {
        int numClasses = data.readUnsignedShort();
        this.innerClasses = numClasses > 0 ? new HashMap(numClasses) : Collections.emptyMap();
        for (int i = 0; i < numClasses; ++i) {
            DotName innerClass = this.decodeClassEntry(data.readUnsignedShort());
            int outerIndex = data.readUnsignedShort();
            DotName outerClass = outerIndex == 0 ? null : this.decodeClassEntry(outerIndex);
            int simpleIndex = data.readUnsignedShort();
            String simpleName = simpleIndex == 0 ? null : this.decodeUtf8Entry(simpleIndex);
            int flags = data.readUnsignedShort();
            if (innerClass.equals(target.name())) {
                target.setInnerClassInfo(outerClass, simpleName, true);
                target.setFlags((short)flags);
            }
            this.innerClasses.put(innerClass, new InnerClassInfo(innerClass, outerClass, simpleName, flags));
        }
    }

    private void processMethodParameters(DataInputStream data, MethodInfo target) throws IOException {
        Object realParameterNames;
        int numParameters = data.readUnsignedByte();
        Object parameterNames = numParameters > 0 ? (Object)new byte[numParameters][] : MethodInternal.EMPTY_PARAMETER_NAMES;
        int filledParameters = 0;
        for (int i = 0; i < numParameters; ++i) {
            int nameIndex = data.readUnsignedShort();
            byte[] parameterName = nameIndex == 0 ? null : this.decodeUtf8EntryAsBytes(nameIndex);
            int flags = data.readUnsignedShort();
            if ((flags & 0x9000) != 0) continue;
            parameterNames[filledParameters++] = parameterName;
        }
        Object object = realParameterNames = filledParameters > 0 ? (Object)new byte[filledParameters][] : MethodInternal.EMPTY_PARAMETER_NAMES;
        if (filledParameters > 0) {
            System.arraycopy(parameterNames, 0, realParameterNames, 0, filledParameters);
        }
        this.methodParameterNames = realParameterNames;
    }

    private void processLocalVariableTable(DataInputStream data, MethodInfo target) throws IOException {
        Object parameterNames;
        int numVariables = data.readUnsignedShort();
        Object variableNames = numVariables > 0 ? (Object)new byte[numVariables][] : MethodInternal.EMPTY_PARAMETER_NAMES;
        int numParameters = 0;
        for (int i = 0; i < numVariables; ++i) {
            byte[] parameterName;
            int startPc = data.readUnsignedShort();
            int length = data.readUnsignedShort();
            int nameIndex = data.readUnsignedShort();
            int descriptorIndex = data.readUnsignedShort();
            int index = data.readUnsignedShort();
            if (startPc != 0) continue;
            byte[] byArray = parameterName = nameIndex == 0 ? null : this.decodeUtf8EntryAsBytes(nameIndex);
            if (numParameters == 0 && parameterName != null && parameterName.length == 4 && parameterName[0] == 116 && parameterName[1] == 104 && parameterName[2] == 105 && parameterName[3] == 115 || numParameters == 0 && parameterName != null && parameterName.length > 5 && parameterName[0] == 116 && parameterName[1] == 104 && parameterName[2] == 105 && parameterName[3] == 115 && parameterName[4] == 36) continue;
            variableNames[numParameters++] = parameterName;
        }
        Object object = parameterNames = numParameters > 0 ? (Object)new byte[numParameters][] : MethodInternal.EMPTY_PARAMETER_NAMES;
        if (numParameters > 0) {
            System.arraycopy(variableNames, 0, parameterNames, 0, numParameters);
        }
        this.debugParameterNames = parameterNames;
    }

    private void processEnclosingMethod(DataInputStream data, ClassInfo target) throws IOException {
        int classIndex = data.readUnsignedShort();
        int index = data.readUnsignedShort();
        if (index == 0) {
            return;
        }
        DotName enclosingClass = this.decodeClassEntry(classIndex);
        NameAndType nameAndType = this.decodeNameAndTypeEntry(index);
        IntegerHolder pos = new IntegerHolder();
        Type[] parameters = this.intern(this.parseMethodArgs(nameAndType.descriptor, pos));
        Type returnType = this.parseType(nameAndType.descriptor, pos);
        ClassInfo.EnclosingMethodInfo method = new ClassInfo.EnclosingMethodInfo(nameAndType.name, returnType, parameters, enclosingClass);
        target.setEnclosingMethod(method);
    }

    private void processTypeAnnotations(DataInputStream data, AnnotationTarget target) throws IOException {
        int numAnnotations = data.readUnsignedShort();
        ArrayList<TypeAnnotationState> annotations = new ArrayList<TypeAnnotationState>(numAnnotations);
        for (int i = 0; i < numAnnotations; ++i) {
            TypeAnnotationState annotation = this.processTypeAnnotation(data, target);
            if (annotation == null) continue;
            annotations.add(annotation);
        }
        this.typeAnnotations.put(target, annotations);
    }

    private TypeAnnotationState processTypeAnnotation(DataInputStream data, AnnotationTarget target) throws IOException {
        int targetType = data.readUnsignedByte();
        TypeTarget typeTarget = null;
        switch (targetType) {
            case 0: 
            case 1: {
                typeTarget = new TypeParameterTypeTarget(target, data.readUnsignedByte());
                break;
            }
            case 16: {
                int position = data.readUnsignedShort();
                if (!(target instanceof ClassInfo)) break;
                typeTarget = new ClassExtendsTypeTarget((ClassInfo)target, position);
                break;
            }
            case 17: 
            case 18: {
                typeTarget = new TypeParameterBoundTypeTarget(target, data.readUnsignedByte(), data.readUnsignedByte());
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                typeTarget = new EmptyTypeTarget(target, targetType == 21);
                break;
            }
            case 22: {
                int position = data.readUnsignedByte();
                if (!(target instanceof MethodInfo)) break;
                typeTarget = new MethodParameterTypeTarget((MethodInfo)target, position);
                break;
            }
            case 23: {
                int position = data.readUnsignedShort();
                if (!(target instanceof MethodInfo)) break;
                typeTarget = new ThrowsTypeTarget((MethodInfo)target, position);
                break;
            }
            case 64: 
            case 65: {
                Indexer.skipFully(data, data.readUnsignedShort() * 6);
                break;
            }
            case 66: {
                Indexer.skipFully(data, 2L);
                break;
            }
            case 67: 
            case 68: 
            case 69: 
            case 70: {
                Indexer.skipFully(data, 2L);
                break;
            }
            case 71: 
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                Indexer.skipFully(data, 3L);
                break;
            }
            default: {
                throw new IllegalStateException("Invalid type annotation target type");
            }
        }
        if (typeTarget == null) {
            this.skipTargetPath(data);
            this.processAnnotation(data, null);
            return null;
        }
        BooleanHolder genericsRequired = new BooleanHolder();
        BooleanHolder bridgeIncompatible = new BooleanHolder();
        if (typeTarget.usage() == TypeTarget.Usage.TYPE_PARAMETER || typeTarget.usage() == TypeTarget.Usage.TYPE_PARAMETER_BOUND) {
            genericsRequired.bool = true;
        }
        ArrayList<PathElement> pathElements = this.processTargetPath(data, genericsRequired, bridgeIncompatible);
        AnnotationInstance annotation = this.processAnnotation(data, typeTarget);
        return new TypeAnnotationState(typeTarget, annotation, pathElements, genericsRequired.bool, bridgeIncompatible.bool);
    }

    private void resolveTypeAnnotations() {
        for (Map.Entry<AnnotationTarget, List<TypeAnnotationState>> entry : this.typeAnnotations.entrySet()) {
            AnnotationTarget key = entry.getKey();
            List<TypeAnnotationState> annotations = entry.getValue();
            for (TypeAnnotationState annotation : annotations) {
                this.resolveTypeAnnotation(key, annotation);
            }
        }
    }

    private void resolveUsers() throws IOException {
        int[] offsets;
        byte[] pool = this.constantPool;
        for (int offset : offsets = this.constantPoolOffsets) {
            int nameIndex;
            DotName usedClass;
            List<ClassInfo> usersOfClass;
            if (pool[offset] != 7) continue;
            if ((usersOfClass = this.users.get(usedClass = this.names.convertToName(this.decodeUtf8Entry(nameIndex = (pool[++offset] & 0xFF) << 8 | pool[++offset] & 0xFF), '/'))) == null) {
                usersOfClass = new ArrayList<ClassInfo>();
                this.users.put(usedClass, usersOfClass);
            }
            usersOfClass.add(this.currentClass);
        }
    }

    private void updateTypeTargets() {
        for (Map.Entry<AnnotationTarget, List<TypeAnnotationState>> entry : this.typeAnnotations.entrySet()) {
            AnnotationTarget key = entry.getKey();
            List<TypeAnnotationState> annotations = entry.getValue();
            for (TypeAnnotationState annotation : annotations) {
                this.updateTypeTarget(key, annotation);
            }
        }
    }

    private static Type[] getTypeParameters(AnnotationTarget target) {
        if (target instanceof ClassInfo) {
            return ((ClassInfo)target).typeParameterArray();
        }
        if (target instanceof MethodInfo) {
            return ((MethodInfo)target).typeParameterArray();
        }
        throw new IllegalStateException("Type annotation referred to type parameters on an invalid target: " + target);
    }

    private static Type[] copyTypeParameters(AnnotationTarget target) {
        if (target instanceof ClassInfo) {
            return (Type[])((ClassInfo)target).typeParameterArray().clone();
        }
        if (target instanceof MethodInfo) {
            return (Type[])((MethodInfo)target).typeParameterArray().clone();
        }
        throw new IllegalStateException("Type annotation referred to type parameters on an invalid target: " + target);
    }

    private void setTypeParameters(AnnotationTarget target, Type[] typeParameters) {
        if (target instanceof ClassInfo) {
            ((ClassInfo)target).setTypeParameters(typeParameters);
            return;
        }
        if (target instanceof MethodInfo) {
            ((MethodInfo)target).setTypeParameters(typeParameters);
            return;
        }
        throw new IllegalStateException("Type annotation referred to type parameters on an invalid target: " + target);
    }

    private static boolean isInnerConstructor(MethodInfo method) {
        ClassInfo klass = method.declaringClass();
        return klass.nestingType() != ClassInfo.NestingType.TOP_LEVEL && !Modifier.isStatic(klass.flags()) && "<init>".equals(method.name());
    }

    private void resolveTypeAnnotation(AnnotationTarget target, TypeAnnotationState typeAnnotationState) {
        if (typeAnnotationState.genericsRequired && !this.signaturePresent.containsKey(target)) {
            typeAnnotationState.target.setTarget(VoidType.VOID);
            return;
        }
        TypeTarget typeTarget = typeAnnotationState.target;
        if (typeTarget.usage() == TypeTarget.Usage.TYPE_PARAMETER_BOUND) {
            int boundIndex;
            TypeParameterBoundTypeTarget bound = (TypeParameterBoundTypeTarget)typeTarget;
            Type[] types = Indexer.copyTypeParameters(target);
            int index = bound.position();
            if (index >= types.length) {
                return;
            }
            TypeVariable type = types[index].asTypeVariable();
            if (type.hasImplicitObjectBound()) {
                bound.adjustBoundDown();
            }
            if ((boundIndex = bound.boundPosition()) >= type.boundArray().length) {
                return;
            }
            type = type.copyType(boundIndex, this.resolveTypePath(type.boundArray()[boundIndex], typeAnnotationState));
            types[index] = this.intern(type);
            this.setTypeParameters(target, this.intern(types));
        } else if (typeTarget.usage() == TypeTarget.Usage.TYPE_PARAMETER) {
            TypeParameterTypeTarget parameter = (TypeParameterTypeTarget)typeTarget;
            Type[] types = Indexer.copyTypeParameters(target);
            int index = parameter.position();
            if (index >= types.length) {
                return;
            }
            types[index] = this.resolveTypePath(types[index], typeAnnotationState);
            this.setTypeParameters(target, this.intern(types));
        } else if (typeTarget.usage() == TypeTarget.Usage.CLASS_EXTENDS) {
            ClassInfo clazz = (ClassInfo)target;
            ClassExtendsTypeTarget extendsTarget = (ClassExtendsTypeTarget)typeTarget;
            int index = extendsTarget.position();
            if (index == 65535) {
                clazz.setSuperClassType(this.resolveTypePath(clazz.superClassType(), typeAnnotationState));
            } else if (index < clazz.interfaceTypes().size()) {
                Type[] types = clazz.copyInterfaceTypes();
                types[index] = this.resolveTypePath(types[index], typeAnnotationState);
                clazz.setInterfaceTypes(this.intern(types));
            }
        } else if (typeTarget.usage() == TypeTarget.Usage.METHOD_PARAMETER) {
            Type[] types;
            int index;
            MethodInfo method = (MethodInfo)target;
            if (this.skipBridge(typeAnnotationState, method)) {
                return;
            }
            MethodParameterTypeTarget parameter = (MethodParameterTypeTarget)typeTarget;
            if (Indexer.isInnerConstructor(method) && !this.signaturePresent.containsKey(method)) {
                parameter.adjustUp();
            }
            if ((index = parameter.position()) >= (types = method.copyParameters()).length) {
                return;
            }
            types[index] = this.resolveTypePath(types[index], typeAnnotationState);
            method.setParameters(this.intern(types));
        } else if (typeTarget.usage() == TypeTarget.Usage.EMPTY && target instanceof FieldInfo) {
            FieldInfo field = (FieldInfo)target;
            field.setType(this.resolveTypePath(field.type(), typeAnnotationState));
        } else if (typeTarget.usage() == TypeTarget.Usage.EMPTY && target instanceof MethodInfo) {
            MethodInfo method = (MethodInfo)target;
            if (((EmptyTypeTarget)typeTarget).isReceiver()) {
                method.setReceiverType(this.resolveTypePath(method.receiverType(), typeAnnotationState));
            } else {
                Type returnType = method.returnType();
                if (this.skipBridge(typeAnnotationState, method)) {
                    return;
                }
                method.setReturnType(this.resolveTypePath(returnType, typeAnnotationState));
            }
        } else if (typeTarget.usage() == TypeTarget.Usage.EMPTY && target instanceof RecordComponentInfo) {
            RecordComponentInfo recordComponent = (RecordComponentInfo)target;
            recordComponent.setType(this.resolveTypePath(recordComponent.type(), typeAnnotationState));
        } else if (typeTarget.usage() == TypeTarget.Usage.THROWS && target instanceof MethodInfo) {
            Type[] exceptions;
            MethodInfo method = (MethodInfo)target;
            int position = ((ThrowsTypeTarget)typeTarget).position();
            if (position >= (exceptions = method.copyExceptions()).length) {
                return;
            }
            exceptions[position] = this.resolveTypePath(exceptions[position], typeAnnotationState);
            method.setExceptions(this.intern(exceptions));
        }
    }

    private boolean skipBridge(TypeAnnotationState typeAnnotationState, MethodInfo method) {
        return typeAnnotationState.bridgeIncompatible && this.isBridge(method);
    }

    private boolean isBridge(MethodInfo methodInfo) {
        int bridgeModifiers = 4160;
        return (methodInfo.flags() & bridgeModifiers) == bridgeModifiers;
    }

    private boolean targetsArray(TypeAnnotationState typeAnnotationState) {
        if (typeAnnotationState.pathElements.size() == 0) {
            return false;
        }
        PathElement pathElement = typeAnnotationState.pathElements.peek();
        return pathElement != null && pathElement.kind == PathElement.Kind.ARRAY;
    }

    private Type resolveTypePath(Type type, TypeAnnotationState typeAnnotationState) {
        PathElementStack elements = typeAnnotationState.pathElements;
        PathElement element = elements.pop();
        if (element == null) {
            type = this.intern(type.addAnnotation(new AnnotationInstance(typeAnnotationState.annotation, null)));
            typeAnnotationState.target.setTarget(type);
            return type;
        }
        switch (element.kind) {
            case ARRAY: {
                ArrayType arrayType = type.asArrayType();
                int dimensions = arrayType.dimensions();
                while (--dimensions > 0 && elements.size() > 0 && elements.peek().kind == PathElement.Kind.ARRAY) {
                    elements.pop();
                }
                Type nested = dimensions > 0 ? new ArrayType(arrayType.component(), dimensions) : arrayType.component();
                nested = this.resolveTypePath(nested, typeAnnotationState);
                return this.intern(arrayType.copyType(nested, arrayType.dimensions() - dimensions));
            }
            case PARAMETERIZED: {
                ParameterizedType parameterizedType = type.asParameterizedType();
                Type[] arguments = (Type[])parameterizedType.argumentsArray().clone();
                int pos = element.pos;
                if (pos >= arguments.length) {
                    throw new IllegalStateException("Type annotation referred to a type argument that does not exist");
                }
                arguments[pos] = this.resolveTypePath(arguments[pos], typeAnnotationState);
                return this.intern(parameterizedType.copyType(arguments));
            }
            case WILDCARD_BOUND: {
                WildcardType wildcardType = type.asWildcardType();
                Type bound = this.resolveTypePath(wildcardType.bound(), typeAnnotationState);
                return this.intern(wildcardType.copyType(bound));
            }
            case NESTED: {
                int depth = this.popNestedDepth(elements);
                return this.rebuildNestedType(type, depth, typeAnnotationState);
            }
        }
        throw new IllegalStateException("Unknown path element");
    }

    private int popNestedDepth(PathElementStack elements) {
        int depth = 1;
        while (elements.size() > 0 && elements.peek().kind == PathElement.Kind.NESTED) {
            elements.pop();
            ++depth;
        }
        return depth;
    }

    private void updateTypeTarget(AnnotationTarget enclosingTarget, TypeAnnotationState typeAnnotationState) {
        Type type;
        if (typeAnnotationState.genericsRequired && !this.signaturePresent.containsKey(enclosingTarget)) {
            return;
        }
        typeAnnotationState.pathElements.reset();
        TypeTarget target = typeAnnotationState.target;
        switch (target.usage()) {
            case EMPTY: {
                if (enclosingTarget instanceof FieldInfo) {
                    type = ((FieldInfo)enclosingTarget).type();
                    break;
                }
                if (enclosingTarget instanceof RecordComponentInfo) {
                    type = ((RecordComponentInfo)enclosingTarget).type();
                    break;
                }
                MethodInfo method = (MethodInfo)enclosingTarget;
                Type type2 = type = target.asEmpty().isReceiver() ? method.receiverType() : method.returnType();
                if (!this.skipBridge(typeAnnotationState, method)) break;
                return;
            }
            case CLASS_EXTENDS: {
                ClassInfo clazz = (ClassInfo)enclosingTarget;
                int position = target.asClassExtends().position();
                type = position == 65535 ? clazz.superClassType() : clazz.interfaceTypeArray()[position];
                break;
            }
            case METHOD_PARAMETER: {
                MethodInfo method = (MethodInfo)enclosingTarget;
                if (this.skipBridge(typeAnnotationState, method)) {
                    return;
                }
                type = method.methodInternal().parameterArray()[target.asMethodParameterType().position()];
                break;
            }
            case TYPE_PARAMETER: {
                type = Indexer.getTypeParameters(enclosingTarget)[target.asTypeParameter().position()];
                break;
            }
            case TYPE_PARAMETER_BOUND: {
                TypeParameterBoundTypeTarget boundTarget = target.asTypeParameterBound();
                type = Indexer.getTypeParameters(enclosingTarget)[boundTarget.position()].asTypeVariable().boundArray()[boundTarget.boundPosition()];
                break;
            }
            case THROWS: {
                type = ((MethodInfo)enclosingTarget).methodInternal().exceptionArray()[target.asThrows().position()];
                break;
            }
            default: {
                throw new IllegalStateException("Unknown type target: " + (Object)((Object)target.usage()));
            }
        }
        type = this.searchTypePath(type, typeAnnotationState);
        target.setTarget(type);
    }

    private Type searchTypePath(Type type, TypeAnnotationState typeAnnotationState) {
        PathElementStack elements = typeAnnotationState.pathElements;
        PathElement element = elements.pop();
        if (element == null) {
            return type;
        }
        switch (element.kind) {
            case ARRAY: {
                ArrayType arrayType = type.asArrayType();
                int dimensions = arrayType.dimensions();
                while (--dimensions > 0 && elements.size() > 0 && elements.peek().kind == PathElement.Kind.ARRAY) {
                    elements.pop();
                }
                assert (dimensions == 0);
                return this.searchTypePath(arrayType.component(), typeAnnotationState);
            }
            case PARAMETERIZED: {
                ParameterizedType parameterizedType = type.asParameterizedType();
                return this.searchTypePath(parameterizedType.argumentsArray()[element.pos], typeAnnotationState);
            }
            case WILDCARD_BOUND: {
                return this.searchTypePath(type.asWildcardType().bound(), typeAnnotationState);
            }
            case NESTED: {
                int depth = this.popNestedDepth(elements);
                return this.searchNestedType(type, depth, typeAnnotationState);
            }
        }
        throw new IllegalStateException("Unknown path element");
    }

    private Type rebuildNestedType(Type type, int depth, TypeAnnotationState typeAnnotationState) {
        DotName name = type.name();
        Map<DotName, Type> ownerMap = this.buildOwnerMap(type);
        ArrayDeque<InnerClassInfo> classes = this.buildClassesQueue(name);
        Type last = null;
        for (InnerClassInfo current : classes) {
            DotName currentName = current.innnerClass;
            Type oType = ownerMap.get(currentName);
            if (depth > 0 && !Modifier.isStatic(current.flags)) {
                --depth;
            }
            if (last != null) {
                last = this.intern(oType != null ? this.convertParameterized(oType).copyType(last) : new ParameterizedType(currentName, null, last));
            } else if (oType != null) {
                last = oType;
            }
            if (depth != 0) continue;
            if (last == null) {
                last = this.intern(new ClassType(currentName));
            }
            last = this.resolveTypePath(last, typeAnnotationState);
            --depth;
        }
        if (depth > 0 && this.hasAnonymousEncloser(typeAnnotationState)) {
            return this.resolveTypePath(type, typeAnnotationState);
        }
        if (last == null) {
            throw new IllegalStateException("Required class information is missing on: " + typeAnnotationState.target.enclosingTarget().asClass().name().toString());
        }
        return last;
    }

    private ParameterizedType convertParameterized(Type oType) {
        return oType instanceof ClassType ? oType.asClassType().toParameterizedType() : oType.asParameterizedType();
    }

    private Type searchNestedType(Type type, int depth, TypeAnnotationState typeAnnotationState) {
        DotName name = type.name();
        Map<DotName, Type> ownerMap = this.buildOwnerMap(type);
        ArrayDeque<InnerClassInfo> classes = this.buildClassesQueue(name);
        for (InnerClassInfo current : classes) {
            DotName currentName = current.innnerClass;
            if (depth > 0 && !Modifier.isStatic(current.flags)) {
                --depth;
            }
            if (depth != 0) continue;
            Type owner = ownerMap.get(currentName);
            return this.searchTypePath(owner == null ? type : owner, typeAnnotationState);
        }
        if (this.hasAnonymousEncloser(typeAnnotationState)) {
            return this.searchTypePath(type, typeAnnotationState);
        }
        throw new IllegalStateException("Required class information is missing");
    }

    private boolean hasAnonymousEncloser(TypeAnnotationState typeAnnotationState) {
        return typeAnnotationState.target instanceof ClassExtendsTypeTarget && typeAnnotationState.target.enclosingTarget().asClass().nestingType() == ClassInfo.NestingType.ANONYMOUS;
    }

    private ArrayDeque<InnerClassInfo> buildClassesQueue(DotName name) {
        ArrayDeque<InnerClassInfo> classes = new ArrayDeque<InnerClassInfo>();
        InnerClassInfo info = this.innerClasses.get(name);
        while (info != null) {
            classes.addFirst(info);
            name = info.enclosingClass;
            info = name != null ? this.innerClasses.get(name) : null;
        }
        return classes;
    }

    private Map<DotName, Type> buildOwnerMap(Type type) {
        HashMap<DotName, Type> pTypeTree = new HashMap<DotName, Type>();
        Type nextType = type;
        do {
            pTypeTree.put(nextType.name(), nextType);
        } while ((nextType = nextType instanceof ParameterizedType ? nextType.asParameterizedType().owner() : null) != null);
        return pTypeTree;
    }

    private ArrayList<PathElement> processTargetPath(DataInputStream data, BooleanHolder genericsRequired, BooleanHolder bridgeIncompatible) throws IOException {
        int numElements = data.readUnsignedByte();
        ArrayList<PathElement> elements = new ArrayList<PathElement>(numElements);
        for (int i = 0; i < numElements; ++i) {
            int kindIndex = data.readUnsignedByte();
            int pos = data.readUnsignedByte();
            PathElement.Kind kind = PathElement.KINDS[kindIndex];
            if (kind == PathElement.Kind.WILDCARD_BOUND || kind == PathElement.Kind.PARAMETERIZED) {
                genericsRequired.bool = true;
            } else if (kind == PathElement.Kind.ARRAY || kind == PathElement.Kind.NESTED) {
                bridgeIncompatible.bool = true;
            }
            elements.add(new PathElement(kind, pos));
        }
        return elements;
    }

    private void skipTargetPath(DataInputStream data) throws IOException {
        int numElements = data.readUnsignedByte();
        Indexer.skipFully(data, numElements * 2);
    }

    private void processExceptions(DataInputStream data, MethodInfo target) throws IOException {
        int numExceptions = data.readUnsignedShort();
        Type[] exceptions = numExceptions <= 0 ? Type.EMPTY_ARRAY : new Type[numExceptions];
        for (int i = 0; i < numExceptions; ++i) {
            exceptions[i] = this.intern(new ClassType(this.decodeClassEntry(data.readUnsignedShort())));
        }
        if (numExceptions > 0 && target.exceptions().size() == 0) {
            target.setExceptions(exceptions);
        }
    }

    private void processSignature(DataInputStream data, AnnotationTarget target) throws IOException {
        String signature = this.decodeUtf8Entry(data.readUnsignedShort());
        if (target instanceof ClassInfo) {
            this.classSignatureIndex = this.signatures.size();
        }
        this.signatures.add(signature);
        this.signatures.add(target);
        this.signaturePresent.put(target, null);
    }

    private void parseClassSignature(String signature, ClassInfo clazz) {
        GenericSignatureParser.ClassSignature classSignature = this.signatureParser.parseClassSignature(signature);
        clazz.setInterfaceTypes(classSignature.interfaces());
        clazz.setSuperClassType(classSignature.superClass());
        clazz.setTypeParameters(classSignature.parameters());
    }

    private void applySignatures() {
        int end = this.signatures.size();
        if (this.classSignatureIndex >= 0) {
            String elementSignature = (String)this.signatures.get(this.classSignatureIndex);
            Object element = this.signatures.get(this.classSignatureIndex + 1);
            this.parseClassSignature(elementSignature, (ClassInfo)element);
        }
        for (int i = 0; i < end; i += 2) {
            if (i == this.classSignatureIndex) continue;
            String elementSignature = (String)this.signatures.get(i);
            Object element = this.signatures.get(i + 1);
            if (element instanceof FieldInfo) {
                this.parseFieldSignature(elementSignature, (FieldInfo)element);
                continue;
            }
            if (element instanceof MethodInfo) {
                this.parseMethodSignature(elementSignature, (MethodInfo)element);
                continue;
            }
            if (!(element instanceof RecordComponentInfo)) continue;
            this.parseRecordComponentSignature(elementSignature, (RecordComponentInfo)element);
        }
    }

    private void parseFieldSignature(String signature, FieldInfo field) {
        Type type = this.signatureParser.parseFieldSignature(signature);
        field.setType(type);
    }

    private void parseMethodSignature(String signature, MethodInfo method) {
        GenericSignatureParser.MethodSignature methodSignature = this.signatureParser.parseMethodSignature(signature);
        method.setParameters(methodSignature.methodParameters());
        method.setReturnType(methodSignature.returnType());
        method.setTypeParameters(methodSignature.typeParameters());
        if (methodSignature.throwables().length > 0) {
            method.setExceptions(methodSignature.throwables());
        }
    }

    private void parseRecordComponentSignature(String signature, RecordComponentInfo recordComponent) {
        Type type = this.signatureParser.parseFieldSignature(signature);
        recordComponent.setType(type);
    }

    private AnnotationInstance processAnnotation(DataInputStream data, AnnotationTarget target) throws IOException {
        String annotation = Indexer.convertClassFieldDescriptor(this.decodeUtf8Entry(data.readUnsignedShort()));
        int valuePairs = data.readUnsignedShort();
        AnnotationValue[] values = new AnnotationValue[valuePairs];
        for (int v = 0; v < valuePairs; ++v) {
            String name = this.intern(this.decodeUtf8Entry(data.readUnsignedShort()));
            values[v] = this.processAnnotationElementValue(name, data);
        }
        Arrays.sort(values, new Comparator<AnnotationValue>(){

            @Override
            public int compare(AnnotationValue o1, AnnotationValue o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        DotName annotationName = this.names.convertToName(annotation);
        AnnotationInstance instance = new AnnotationInstance(annotationName, target, values);
        if (target != null) {
            this.recordAnnotation(this.classAnnotations, annotationName, instance);
            this.recordAnnotation(this.masterAnnotations, annotationName, instance);
            if (target instanceof FieldInfo || target instanceof MethodInfo || target instanceof MethodParameterInfo || target instanceof RecordComponentInfo || target instanceof TypeTarget && ((TypeTarget)target).enclosingTarget().kind() != AnnotationTarget.Kind.CLASS) {
                this.elementAnnotations.add(instance);
            }
        }
        return instance;
    }

    private void recordAnnotation(Map<DotName, List<AnnotationInstance>> classAnnotations, DotName annotation, AnnotationInstance instance) {
        List<AnnotationInstance> list = classAnnotations.get(annotation);
        if (list == null) {
            list = new ArrayList<AnnotationInstance>();
            classAnnotations.put(annotation, list);
        }
        list.add(instance);
    }

    private String intern(String string) {
        return this.names.intern(string);
    }

    private byte[] intern(byte[] bytes) {
        return this.names.intern(bytes);
    }

    private Type intern(Type type) {
        return this.names.intern(type);
    }

    private Type[] intern(Type[] type) {
        return this.names.intern(type);
    }

    private AnnotationValue processAnnotationElementValue(String name, DataInputStream data) throws IOException {
        int tag = data.readUnsignedByte();
        switch (tag) {
            case 66: {
                return new AnnotationValue.ByteValue(name, (byte)this.decodeIntegerEntry(data.readUnsignedShort()));
            }
            case 67: {
                return new AnnotationValue.CharacterValue(name, (char)this.decodeIntegerEntry(data.readUnsignedShort()));
            }
            case 73: {
                return new AnnotationValue.IntegerValue(name, this.decodeIntegerEntry(data.readUnsignedShort()));
            }
            case 83: {
                return new AnnotationValue.ShortValue(name, (short)this.decodeIntegerEntry(data.readUnsignedShort()));
            }
            case 90: {
                return new AnnotationValue.BooleanValue(name, this.decodeIntegerEntry(data.readUnsignedShort()) > 0);
            }
            case 70: {
                return new AnnotationValue.FloatValue(name, this.decodeFloatEntry(data.readUnsignedShort()));
            }
            case 68: {
                return new AnnotationValue.DoubleValue(name, this.decodeDoubleEntry(data.readUnsignedShort()));
            }
            case 74: {
                return new AnnotationValue.LongValue(name, this.decodeLongEntry(data.readUnsignedShort()));
            }
            case 115: {
                return new AnnotationValue.StringValue(name, this.decodeUtf8Entry(data.readUnsignedShort()));
            }
            case 99: {
                return new AnnotationValue.ClassValue(name, this.parseType(this.decodeUtf8Entry(data.readUnsignedShort())));
            }
            case 101: {
                DotName type = this.parseType(this.decodeUtf8Entry(data.readUnsignedShort())).name();
                String value = this.decodeUtf8Entry(data.readUnsignedShort());
                return new AnnotationValue.EnumValue(name, type, value);
            }
            case 64: {
                return new AnnotationValue.NestedAnnotation(name, this.processAnnotation(data, null));
            }
            case 91: {
                int numValues = data.readUnsignedShort();
                AnnotationValue[] values = new AnnotationValue[numValues];
                for (int i = 0; i < numValues; ++i) {
                    values[i] = this.processAnnotationElementValue("", data);
                }
                return new AnnotationValue.ArrayValue(name, values);
            }
        }
        throw new IllegalStateException("Invalid tag value: " + tag);
    }

    private void processClassInfo(DataInputStream data) throws IOException {
        short flags = (short)data.readUnsignedShort();
        DotName thisName = this.decodeClassEntry(data.readUnsignedShort());
        int superIndex = data.readUnsignedShort();
        DotName superName = superIndex != 0 ? this.decodeClassEntry(superIndex) : null;
        int numInterfaces = data.readUnsignedShort();
        ArrayList<Type> interfaces = new ArrayList<Type>(numInterfaces);
        for (int i = 0; i < numInterfaces; ++i) {
            interfaces.add(this.intern(new ClassType(this.decodeClassEntry(data.readUnsignedShort()))));
        }
        Type[] interfaceTypes = this.intern(interfaces.toArray(new Type[interfaces.size()]));
        Type superClassType = superName == null ? null : this.intern(new ClassType(superName));
        this.classAnnotations = new HashMap();
        this.currentClass = new ClassInfo(thisName, superClassType, flags, interfaceTypes);
        if (superName != null) {
            this.addSubclass(superName, this.currentClass);
        }
        for (int i = 0; i < numInterfaces; ++i) {
            this.addImplementor(((Type)interfaces.get(i)).name(), this.currentClass);
        }
        if (!this.currentClass.isModule()) {
            this.classes.put(this.currentClass.name(), this.currentClass);
        }
    }

    private void addSubclass(DotName superName, ClassInfo currentClass) {
        List<ClassInfo> list = this.subclasses.get(superName);
        if (list == null) {
            list = new ArrayList<ClassInfo>();
            this.subclasses.put(superName, list);
        }
        list.add(currentClass);
    }

    private void addImplementor(DotName interfaceName, ClassInfo currentClass) {
        List<ClassInfo> list = this.implementors.get(interfaceName);
        if (list == null) {
            list = new ArrayList<ClassInfo>();
            this.implementors.put(interfaceName, list);
        }
        list.add(currentClass);
    }

    private boolean isJDK11OrNewer(DataInputStream stream) throws IOException {
        int minor = stream.readUnsignedShort();
        int major = stream.readUnsignedShort();
        return major > 45 || major == 45 && minor >= 3;
    }

    private void verifyMagic(DataInputStream stream) throws IOException {
        byte[] buf = new byte[4];
        stream.readFully(buf);
        if (buf[0] != -54 || buf[1] != -2 || buf[2] != -70 || buf[3] != -66) {
            throw new IOException("Invalid Magic");
        }
    }

    private DotName decodeClassEntry(int index) throws IOException {
        return index == 0 ? null : this.decodeDotNameEntry(index, 7, "Class_info", '/');
    }

    private DotName decodeModuleEntry(int index) throws IOException {
        return index == 0 ? null : this.decodeDotNameEntry(index, 19, "Module_info", '.');
    }

    private DotName decodePackageEntry(int index) throws IOException {
        return index == 0 ? null : this.decodeDotNameEntry(index, 20, "Package_info", '/');
    }

    private DotName decodeDotNameEntry(int index, int constantType, String typeName, char delim) throws IOException {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != constantType) {
            throw new IllegalStateException(String.format(Locale.ROOT, "Constant pool entry is not a %s type: %d:%d", typeName, index, pos));
        }
        int nameIndex = (pool[++pos] & 0xFF) << 8 | pool[++pos] & 0xFF;
        return this.names.convertToName(this.decodeUtf8Entry(nameIndex), delim);
    }

    private String decodeOptionalUtf8Entry(int index) throws IOException {
        return index == 0 ? null : this.decodeUtf8Entry(index);
    }

    private String decodeUtf8Entry(int index) throws IOException {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 1) {
            throw new IllegalStateException("Constant pool entry is not a utf8 info type: " + index + ":" + pos);
        }
        int len = (pool[++pos] & 0xFF) << 8 | pool[pos + 1] & 0xFF;
        return new DataInputStream(new ByteArrayInputStream(pool, pos, len + 2)).readUTF();
    }

    private byte[] decodeUtf8EntryAsBytes(int index) {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 1) {
            throw new IllegalStateException("Constant pool entry is not a utf8 info type: " + index + ":" + pos);
        }
        int len = (pool[++pos] & 0xFF) << 8 | pool[++pos] & 0xFF;
        return Arrays.copyOfRange(pool, ++pos, len + pos);
    }

    private NameAndType decodeNameAndTypeEntry(int index) throws IOException {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 12) {
            throw new IllegalStateException("Constant pool entry is not a name and type type: " + index + ":" + pos);
        }
        int nameIndex = (pool[++pos] & 0xFF) << 8 | pool[++pos] & 0xFF;
        int descriptorIndex = (pool[++pos] & 0xFF) << 8 | pool[++pos] & 0xFF;
        return new NameAndType(this.intern(this.decodeUtf8Entry(nameIndex)), this.decodeUtf8Entry(descriptorIndex));
    }

    private int bitsToInt(byte[] pool, int pos) {
        return (pool[++pos] & 0xFF) << 24 | (pool[++pos] & 0xFF) << 16 | (pool[++pos] & 0xFF) << 8 | pool[++pos] & 0xFF;
    }

    private long bitsToLong(byte[] pool, int pos) {
        return (long)(pool[++pos] & 0xFF) << 56 | (long)(pool[++pos] & 0xFF) << 48 | (long)(pool[++pos] & 0xFF) << 40 | (long)(pool[++pos] & 0xFF) << 32 | (long)(pool[++pos] & 0xFF) << 24 | (long)(pool[++pos] & 0xFF) << 16 | (long)(pool[++pos] & 0xFF) << 8 | (long)(pool[++pos] & 0xFF);
    }

    private int decodeIntegerEntry(int index) {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 3) {
            throw new IllegalStateException("Constant pool entry is not an integer info type: " + index + ":" + pos);
        }
        return this.bitsToInt(pool, pos);
    }

    private long decodeLongEntry(int index) {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 5) {
            throw new IllegalStateException("Constant pool entry is not an long info type: " + index + ":" + pos);
        }
        return this.bitsToLong(pool, pos);
    }

    private float decodeFloatEntry(int index) {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 4) {
            throw new IllegalStateException("Constant pool entry is not an float info type: " + index + ":" + pos);
        }
        return Float.intBitsToFloat(this.bitsToInt(pool, pos));
    }

    private double decodeDoubleEntry(int index) {
        byte[] pool = this.constantPool;
        int[] offsets = this.constantPoolOffsets;
        int pos = offsets[index - 1];
        if (pool[pos] != 6) {
            throw new IllegalStateException("Constant pool entry is not an double info type: " + index + ":" + pos);
        }
        return Double.longBitsToDouble(this.bitsToLong(pool, pos));
    }

    private static String convertClassFieldDescriptor(String descriptor) {
        if (descriptor.charAt(0) != 'L') {
            throw new IllegalArgumentException("Non class descriptor: " + descriptor);
        }
        return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
    }

    private Type[] parseMethodArgs(String descriptor, IntegerHolder pos) {
        if (descriptor.charAt(pos.i) != '(') {
            throw new IllegalArgumentException("Invalid descriptor: " + descriptor);
        }
        ArrayList<Type> types = new ArrayList<Type>();
        while (descriptor.charAt(++pos.i) != ')') {
            types.add(this.parseType(descriptor, pos));
        }
        pos.i++;
        return types.toArray(new Type[types.size()]);
    }

    private Type parseType(String descriptor) {
        return this.parseType(descriptor, new IntegerHolder());
    }

    private Type parseType(String descriptor, IntegerHolder pos) {
        int start = pos.i;
        char c = descriptor.charAt(start);
        Type type = PrimitiveType.decode(c);
        if (type != null) {
            return type;
        }
        switch (c) {
            case 'V': {
                return VoidType.VOID;
            }
            case 'L': {
                int end = start;
                while (descriptor.charAt(++end) != ';') {
                }
                DotName name = this.names.convertToName(descriptor.substring(start + 1, end), '/');
                pos.i = end;
                return this.names.intern(new ClassType(name));
            }
            case '[': {
                int end = start;
                while (descriptor.charAt(++end) == '[') {
                }
                int depth = end - start;
                pos.i = end;
                type = this.parseType(descriptor, pos);
                return this.names.intern(new ArrayType(type, depth));
            }
        }
        throw new IllegalArgumentException("Invalid descriptor: " + descriptor + " pos " + start);
    }

    private boolean processConstantPool(DataInputStream stream) throws IOException {
        int poolCount = stream.readUnsignedShort() - 1;
        byte[] buf = new byte[20 * poolCount];
        byte[] annoAttributes = new byte[poolCount];
        int[] offsets = new int[poolCount];
        boolean hasAnnotations = false;
        int offset = 0;
        block7: for (int pos = 0; pos < poolCount; ++pos) {
            int tag = stream.readUnsignedByte();
            offsets[pos] = offset;
            switch (tag) {
                case 7: 
                case 8: 
                case 16: 
                case 19: 
                case 20: {
                    buf = Indexer.sizeToFit(buf, 3, offset, poolCount - pos);
                    buf[offset++] = (byte)tag;
                    stream.readFully(buf, offset, 2);
                    offset += 2;
                    continue block7;
                }
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 17: 
                case 18: {
                    buf = Indexer.sizeToFit(buf, 5, offset, poolCount - pos);
                    buf[offset++] = (byte)tag;
                    stream.readFully(buf, offset, 4);
                    offset += 4;
                    continue block7;
                }
                case 5: 
                case 6: {
                    buf = Indexer.sizeToFit(buf, 9, offset, poolCount - pos);
                    buf[offset++] = (byte)tag;
                    stream.readFully(buf, offset, 8);
                    offset += 8;
                    ++pos;
                    continue block7;
                }
                case 15: {
                    buf = Indexer.sizeToFit(buf, 4, offset, poolCount - pos);
                    buf[offset++] = (byte)tag;
                    stream.readFully(buf, offset, 3);
                    offset += 3;
                    continue block7;
                }
                case 1: {
                    int len = stream.readUnsignedShort();
                    buf = Indexer.sizeToFit(buf, len + 3, offset, poolCount - pos);
                    buf[offset++] = (byte)tag;
                    buf[offset++] = (byte)(len >>> 8);
                    buf[offset++] = (byte)len;
                    stream.readFully(buf, offset, len);
                    if (len == RUNTIME_ANNOTATIONS_LEN && Indexer.match(buf, offset, RUNTIME_ANNOTATIONS)) {
                        annoAttributes[pos] = 1;
                        hasAnnotations = true;
                    } else if (len == RUNTIME_PARAM_ANNOTATIONS_LEN && Indexer.match(buf, offset, RUNTIME_PARAM_ANNOTATIONS)) {
                        annoAttributes[pos] = 2;
                        hasAnnotations = true;
                    } else if (len == RUNTIME_TYPE_ANNOTATIONS_LEN && Indexer.match(buf, offset, RUNTIME_TYPE_ANNOTATIONS)) {
                        annoAttributes[pos] = 3;
                    } else if (len == SIGNATURE_LEN && Indexer.match(buf, offset, SIGNATURE)) {
                        annoAttributes[pos] = 4;
                    } else if (len == EXCEPTIONS_LEN && Indexer.match(buf, offset, EXCEPTIONS)) {
                        annoAttributes[pos] = 5;
                    } else if (len == INNER_CLASSES_LEN && Indexer.match(buf, offset, INNER_CLASSES)) {
                        annoAttributes[pos] = 6;
                    } else if (len == ENCLOSING_METHOD_LEN && Indexer.match(buf, offset, ENCLOSING_METHOD)) {
                        annoAttributes[pos] = 7;
                    } else if (len == ANNOTATION_DEFAULT_LEN && Indexer.match(buf, offset, ANNOTATION_DEFAULT)) {
                        annoAttributes[pos] = 8;
                    } else if (len == METHOD_PARAMETERS_LEN && Indexer.match(buf, offset, METHOD_PARAMETERS)) {
                        annoAttributes[pos] = 9;
                    } else if (len == LOCAL_VARIABLE_TABLE_LEN && Indexer.match(buf, offset, LOCAL_VARIABLE_TABLE)) {
                        annoAttributes[pos] = 10;
                    } else if (len == CODE_LEN && Indexer.match(buf, offset, CODE)) {
                        annoAttributes[pos] = 11;
                    } else if (len == MODULE_LEN && Indexer.match(buf, offset, MODULE)) {
                        annoAttributes[pos] = 12;
                    } else if (len == MODULE_PACKAGES_LEN && Indexer.match(buf, offset, MODULE_PACKAGES)) {
                        annoAttributes[pos] = 13;
                    } else if (len == MODULE_MAIN_CLASS_LEN && Indexer.match(buf, offset, MODULE_MAIN_CLASS)) {
                        annoAttributes[pos] = 14;
                    } else if (len == RECORD_LEN && Indexer.match(buf, offset, RECORD)) {
                        annoAttributes[pos] = 15;
                    }
                    offset += len;
                    continue block7;
                }
                default: {
                    throw new IllegalStateException(String.format(Locale.ROOT, "Unknown tag %s! pos = %s poolCount = %s", tag, pos, poolCount));
                }
            }
        }
        this.constantPool = buf;
        this.constantPoolOffsets = offsets;
        this.constantPoolAnnoAttrributes = annoAttributes;
        return hasAnnotations;
    }

    public ClassInfo indexClass(Class<?> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }
        String resourceName = '/' + clazz.getName().replace('.', '/') + ".class";
        InputStream resource = clazz.getResourceAsStream(resourceName);
        return this.index(resource);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ClassInfo index(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        try {
            DataInputStream data = new DataInputStream(new BufferedInputStream(stream));
            this.verifyMagic(data);
            if (!this.isJDK11OrNewer(data)) {
                ClassInfo classInfo = null;
                return classInfo;
            }
            this.initIndexMaps();
            this.initClassFields();
            this.processConstantPool(data);
            this.processClassInfo(data);
            this.processFieldInfo(data);
            this.processMethodInfo(data);
            this.processAttributes(data, this.currentClass);
            this.applySignatures();
            this.resolveTypeAnnotations();
            this.updateTypeTargets();
            this.resolveUsers();
            this.currentClass.setMethods(this.methods, this.names);
            this.currentClass.setFields(this.fields, this.names);
            this.currentClass.setRecordComponents(this.recordComponents, this.names);
            this.currentClass.setAnnotations(this.classAnnotations);
            ClassInfo classInfo = this.currentClass;
            return classInfo;
        }
        finally {
            this.constantPool = null;
            this.constantPoolOffsets = null;
            this.constantPoolAnnoAttrributes = null;
            this.currentClass = null;
            this.classAnnotations = null;
            this.elementAnnotations = null;
            this.innerClasses = null;
            this.signatures = null;
            this.classSignatureIndex = -1;
            this.signaturePresent = null;
        }
    }

    public Index complete() {
        this.initIndexMaps();
        try {
            Index index = new Index(this.masterAnnotations, this.subclasses, this.implementors, this.classes, this.modules, this.users);
            return index;
        }
        finally {
            this.masterAnnotations = null;
            this.subclasses = null;
            this.classes = null;
            this.signatureParser = null;
            this.names = null;
            this.modules = null;
            this.users = null;
        }
    }

    private static class IntegerHolder {
        private int i;

        private IntegerHolder() {
        }
    }

    private static class NameAndType {
        private String name;
        private String descriptor;

        private NameAndType(String name, String descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }
    }

    private static class BooleanHolder {
        boolean bool;

        private BooleanHolder() {
        }
    }

    private static class TypeAnnotationState {
        private final TypeTarget target;
        private final AnnotationInstance annotation;
        private final boolean genericsRequired;
        private final boolean bridgeIncompatible;
        private final PathElementStack pathElements;

        TypeAnnotationState(TypeTarget target, AnnotationInstance annotation, ArrayList<PathElement> pathElements, boolean genericsRequired, boolean bridgeIncompatible) {
            this.target = target;
            this.annotation = annotation;
            this.pathElements = new PathElementStack(pathElements);
            this.genericsRequired = genericsRequired;
            this.bridgeIncompatible = bridgeIncompatible;
        }
    }

    private static class PathElementStack {
        private int elementPos;
        private final ArrayList<PathElement> pathElements;

        PathElementStack(ArrayList<PathElement> pathElements) {
            this.pathElements = pathElements;
        }

        PathElement pop() {
            if (this.elementPos >= this.pathElements.size()) {
                return null;
            }
            return this.pathElements.get(this.elementPos++);
        }

        PathElement peek() {
            return this.pathElements.get(this.elementPos);
        }

        int size() {
            return this.pathElements.size() - this.elementPos;
        }

        void reset() {
            this.elementPos = 0;
        }
    }

    private static class PathElement {
        private static Kind[] KINDS = Kind.values();
        private Kind kind;
        private int pos;

        private PathElement(Kind kind, int pos) {
            this.kind = kind;
            this.pos = pos;
        }

        private static enum Kind {
            ARRAY,
            NESTED,
            WILDCARD_BOUND,
            PARAMETERIZED;

        }
    }

    private static class InnerClassInfo {
        private final DotName innnerClass;
        private DotName enclosingClass;
        private String simpleName;
        private int flags;

        private InnerClassInfo(DotName innerClass, DotName enclosingClass, String simpleName, int flags) {
            this.innnerClass = innerClass;
            this.enclosingClass = enclosingClass;
            this.simpleName = simpleName;
            this.flags = flags;
        }
    }
}

