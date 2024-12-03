/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationTargetFilterCollection;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.FieldInfoGenerator;
import org.jboss.jandex.FieldInternal;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInfoGenerator;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.Modifiers;
import org.jboss.jandex.ModuleInfo;
import org.jboss.jandex.NameTable;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.RecordComponentInfoGenerator;
import org.jboss.jandex.RecordComponentInternal;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.Utils;
import org.jboss.jandex.VoidType;

public final class ClassInfo
implements AnnotationTarget {
    private static final int MODULE = 32768;
    private static final int MAX_POSITIONS = 256;
    private static final byte[] EMPTY_POSITIONS = new byte[0];
    private final DotName name;
    private Map<DotName, List<AnnotationInstance>> annotations;
    private short flags;
    private Type[] interfaceTypes;
    private Type superClassType;
    private Type[] typeParameters;
    private MethodInternal[] methods;
    private FieldInternal[] fields;
    private RecordComponentInternal[] recordComponents;
    private byte[] methodPositions = EMPTY_POSITIONS;
    private byte[] fieldPositions = EMPTY_POSITIONS;
    private byte[] recordComponentPositions = EMPTY_POSITIONS;
    private boolean hasNoArgsConstructor;
    private NestingInfo nestingInfo;

    ClassInfo(DotName name, Type superClassType, short flags, Type[] interfaceTypes) {
        this(name, superClassType, flags, interfaceTypes, false);
    }

    ClassInfo(DotName name, Type superClassType, short flags, Type[] interfaceTypes, boolean hasNoArgsConstructor) {
        this.name = name;
        this.superClassType = superClassType;
        this.flags = flags;
        this.interfaceTypes = interfaceTypes.length == 0 ? Type.EMPTY_ARRAY : interfaceTypes;
        this.hasNoArgsConstructor = hasNoArgsConstructor;
        this.typeParameters = Type.EMPTY_ARRAY;
        this.methods = MethodInternal.EMPTY_ARRAY;
        this.fields = FieldInternal.EMPTY_ARRAY;
    }

    @Deprecated
    public static ClassInfo create(DotName name, DotName superName, short flags, DotName[] interfaces, Map<DotName, List<AnnotationInstance>> annotations, boolean hasNoArgsConstructor) {
        Type[] interfaceTypes = new Type[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfaceTypes[i] = new ClassType(interfaces[i]);
        }
        ClassType superClassType = superName == null ? null : new ClassType(superName);
        ClassInfo clazz = new ClassInfo(name, superClassType, flags, interfaceTypes, hasNoArgsConstructor);
        clazz.setAnnotations(annotations);
        return clazz;
    }

    @Override
    public final AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.CLASS;
    }

    public String toString() {
        return this.name.toString();
    }

    public final DotName name() {
        return this.name;
    }

    public final short flags() {
        return this.flags;
    }

    public final boolean isSynthetic() {
        return Modifiers.isSynthetic(this.flags);
    }

    public final boolean isEnum() {
        return (this.flags & 0x4000) != 0 && DotName.ENUM_NAME.equals(this.superName());
    }

    public final boolean isAnnotation() {
        return (this.flags & 0x2000) != 0;
    }

    public final boolean isRecord() {
        return DotName.RECORD_NAME.equals(this.superName());
    }

    public final boolean isModule() {
        return (this.flags & 0x8000) != 0;
    }

    public final DotName superName() {
        return this.superClassType == null ? null : this.superClassType.name();
    }

    @Deprecated
    public final DotName[] interfaces() {
        DotName[] interfaces = new DotName[this.interfaceTypes.length];
        for (int i = 0; i < this.interfaceTypes.length; ++i) {
            interfaces[i] = this.interfaceTypes[i].name();
        }
        return interfaces;
    }

    public final Map<DotName, List<AnnotationInstance>> annotations() {
        return Collections.unmodifiableMap(this.annotations);
    }

    final void setAnnotations(Map<DotName, List<AnnotationInstance>> annotations) {
        this.annotations = annotations;
    }

    public final Collection<AnnotationInstance> classAnnotations() {
        return new AnnotationTargetFilterCollection<ClassInfo>(this.annotations, ClassInfo.class);
    }

    public final AnnotationInstance classAnnotation(DotName name) {
        List<AnnotationInstance> instances = this.annotations.get(name);
        if (instances != null) {
            for (AnnotationInstance instance : instances) {
                if (instance.target() != this) continue;
                return instance;
            }
        }
        return null;
    }

    public final List<AnnotationInstance> classAnnotationsWithRepeatable(DotName name, IndexView index) {
        AnnotationInstance ret = this.classAnnotation(name);
        if (ret != null) {
            return Collections.singletonList(ret);
        }
        ClassInfo annotationClass = index.getClassByName(name);
        if (annotationClass == null) {
            throw new IllegalArgumentException("Index does not contain the annotation definition: " + name);
        }
        if (!annotationClass.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation type: " + annotationClass);
        }
        AnnotationInstance repeatable = annotationClass.classAnnotation(Index.REPEATABLE);
        if (repeatable == null) {
            return Collections.emptyList();
        }
        Type containingType = repeatable.value().asClass();
        AnnotationInstance containing = this.classAnnotation(containingType.name());
        if (containing == null) {
            return Collections.emptyList();
        }
        AnnotationInstance[] values = containing.value().asNestedArray();
        ArrayList<AnnotationInstance> instances = new ArrayList<AnnotationInstance>(values.length);
        for (AnnotationInstance nestedInstance : values) {
            instances.add(nestedInstance);
        }
        return instances;
    }

    public final List<MethodInfo> methods() {
        return new MethodInfoGenerator(this, this.methods, EMPTY_POSITIONS);
    }

    public final List<MethodInfo> unsortedMethods() {
        return new MethodInfoGenerator(this, this.methods, this.methodPositions);
    }

    public final List<MethodInfo> constructors() {
        ArrayList<MethodInfo> constructors = new ArrayList<MethodInfo>(1);
        for (MethodInfo method : this.methods()) {
            if (!"<init>".equals(method.name())) continue;
            constructors.add(method);
        }
        return constructors;
    }

    final MethodInternal[] methodArray() {
        return this.methods;
    }

    final byte[] methodPositionArray() {
        return this.methodPositions;
    }

    public final MethodInfo method(String name, Type ... parameters) {
        MethodInternal key = new MethodInternal(Utils.toUTF8(name), MethodInternal.EMPTY_PARAMETER_NAMES, parameters, null, 0);
        int i = Arrays.binarySearch(this.methods, key, MethodInternal.NAME_AND_PARAMETER_COMPONENT_COMPARATOR);
        return i >= 0 ? new MethodInfo(this, this.methods[i]) : null;
    }

    public final MethodInfo firstMethod(String name) {
        MethodInternal key = new MethodInternal(Utils.toUTF8(name), MethodInternal.EMPTY_PARAMETER_NAMES, Type.EMPTY_ARRAY, null, 0);
        int i = Arrays.binarySearch(this.methods, key, MethodInternal.NAME_AND_PARAMETER_COMPONENT_COMPARATOR);
        if (i < -this.methods.length) {
            return null;
        }
        MethodInfo method = new MethodInfo(this, i >= 0 ? this.methods[i] : this.methods[++i * -1]);
        return method.name().equals(name) ? method : null;
    }

    public final FieldInfo field(String name) {
        FieldInternal key = new FieldInternal(Utils.toUTF8(name), VoidType.VOID, 0);
        int i = Arrays.binarySearch(this.fields, key, FieldInternal.NAME_COMPARATOR);
        if (i < 0) {
            return null;
        }
        return new FieldInfo(this, this.fields[i]);
    }

    public final List<FieldInfo> fields() {
        return new FieldInfoGenerator(this, this.fields, EMPTY_POSITIONS);
    }

    public final List<FieldInfo> unsortedFields() {
        return new FieldInfoGenerator(this, this.fields, this.fieldPositions);
    }

    final FieldInternal[] fieldArray() {
        return this.fields;
    }

    final byte[] fieldPositionArray() {
        return this.fieldPositions;
    }

    public final RecordComponentInfo recordComponent(String name) {
        RecordComponentInternal key = new RecordComponentInternal(Utils.toUTF8(name), VoidType.VOID);
        int i = Arrays.binarySearch(this.recordComponents, key, RecordComponentInternal.NAME_COMPARATOR);
        if (i < 0) {
            return null;
        }
        return new RecordComponentInfo(this, this.recordComponents[i]);
    }

    public final List<RecordComponentInfo> recordComponents() {
        return new RecordComponentInfoGenerator(this, this.recordComponents, EMPTY_POSITIONS);
    }

    public final List<RecordComponentInfo> unsortedRecordComponents() {
        return new RecordComponentInfoGenerator(this, this.recordComponents, this.recordComponentPositions);
    }

    final RecordComponentInternal[] recordComponentArray() {
        return this.recordComponents;
    }

    final byte[] recordComponentPositionArray() {
        return this.recordComponentPositions;
    }

    public final List<DotName> interfaceNames() {
        return new AbstractList<DotName>(){

            @Override
            public DotName get(int i) {
                return ClassInfo.this.interfaceTypes[i].name();
            }

            @Override
            public int size() {
                return ClassInfo.this.interfaceTypes.length;
            }
        };
    }

    public final List<Type> interfaceTypes() {
        return Collections.unmodifiableList(Arrays.asList(this.interfaceTypes));
    }

    final Type[] interfaceTypeArray() {
        return this.interfaceTypes;
    }

    final Type[] copyInterfaceTypes() {
        return (Type[])this.interfaceTypes.clone();
    }

    public final Type superClassType() {
        return this.superClassType;
    }

    public final List<TypeVariable> typeParameters() {
        List<Type> list = Arrays.asList(this.typeParameters);
        return Collections.unmodifiableList(list);
    }

    final Type[] typeParameterArray() {
        return this.typeParameters;
    }

    public final boolean hasNoArgsConstructor() {
        return this.hasNoArgsConstructor;
    }

    public NestingType nestingType() {
        if (this.nestingInfo == null || this.nestingInfo.module != null) {
            return NestingType.TOP_LEVEL;
        }
        if (this.nestingInfo.enclosingClass != null) {
            return NestingType.INNER;
        }
        if (this.nestingInfo.simpleName != null) {
            return NestingType.LOCAL;
        }
        return NestingType.ANONYMOUS;
    }

    public String simpleName() {
        return this.nestingInfo != null ? this.nestingInfo.simpleName : this.name.local();
    }

    String nestingSimpleName() {
        return this.nestingInfo != null ? this.nestingInfo.simpleName : null;
    }

    public DotName enclosingClass() {
        return this.nestingInfo != null ? this.nestingInfo.enclosingClass : null;
    }

    public EnclosingMethodInfo enclosingMethod() {
        return this.nestingInfo != null ? this.nestingInfo.enclosingMethod : null;
    }

    public ModuleInfo module() {
        return this.nestingInfo != null ? this.nestingInfo.module : null;
    }

    @Override
    public ClassInfo asClass() {
        return this;
    }

    @Override
    public FieldInfo asField() {
        throw new IllegalArgumentException("Not a field");
    }

    @Override
    public MethodInfo asMethod() {
        throw new IllegalArgumentException("Not a method");
    }

    @Override
    public MethodParameterInfo asMethodParameter() {
        throw new IllegalArgumentException("Not a method parameter");
    }

    @Override
    public TypeTarget asType() {
        throw new IllegalArgumentException("Not a type");
    }

    @Override
    public RecordComponentInfo asRecordComponent() {
        throw new IllegalArgumentException("Not a record component");
    }

    void setHasNoArgsConstructor(boolean hasNoArgsConstructor) {
        this.hasNoArgsConstructor = hasNoArgsConstructor;
    }

    void setFields(List<FieldInfo> fields, NameTable names) {
        int size = fields.size();
        if (size == 0) {
            this.fields = FieldInternal.EMPTY_ARRAY;
            return;
        }
        this.fields = new FieldInternal[size];
        for (int i = 0; i < size; ++i) {
            FieldInfo fieldInfo = fields.get(i);
            FieldInternal internal = names.intern(fieldInfo.fieldInternal());
            fieldInfo.setFieldInternal(internal);
            this.fields[i] = internal;
        }
        this.fieldPositions = ClassInfo.sortAndGetPositions(this.fields, FieldInternal.NAME_COMPARATOR, names);
    }

    void setFieldArray(FieldInternal[] fields) {
        this.fields = fields;
    }

    void setFieldPositionArray(byte[] fieldPositions) {
        this.fieldPositions = fieldPositions;
    }

    void setMethodArray(MethodInternal[] methods) {
        this.methods = methods;
    }

    void setMethodPositionArray(byte[] methodPositions) {
        this.methodPositions = methodPositions;
    }

    void setMethods(List<MethodInfo> methods, NameTable names) {
        int size = methods.size();
        if (size == 0) {
            this.methods = MethodInternal.EMPTY_ARRAY;
            return;
        }
        this.methods = new MethodInternal[size];
        for (int i = 0; i < size; ++i) {
            MethodInfo methodInfo = methods.get(i);
            MethodInternal internal = names.intern(methodInfo.methodInternal());
            methodInfo.setMethodInternal(internal);
            this.methods[i] = internal;
        }
        this.methodPositions = ClassInfo.sortAndGetPositions(this.methods, MethodInternal.NAME_AND_PARAMETER_COMPONENT_COMPARATOR, names);
    }

    void setRecordComponentArray(RecordComponentInternal[] recordComponents) {
        this.recordComponents = recordComponents;
    }

    void setRecordComponentPositionArray(byte[] recordComponentPositions) {
        this.recordComponentPositions = recordComponentPositions;
    }

    void setRecordComponents(List<RecordComponentInfo> recordComponents, NameTable names) {
        int size = recordComponents.size();
        if (size == 0) {
            this.recordComponents = RecordComponentInternal.EMPTY_ARRAY;
            return;
        }
        this.recordComponents = new RecordComponentInternal[size];
        for (int i = 0; i < size; ++i) {
            RecordComponentInfo recordComponentInfo = recordComponents.get(i);
            RecordComponentInternal internal = names.intern(recordComponentInfo.recordComponentInternal());
            recordComponentInfo.setRecordComponentInternal(internal);
            this.recordComponents[i] = internal;
        }
        this.recordComponentPositions = ClassInfo.sortAndGetPositions(this.recordComponents, RecordComponentInternal.NAME_COMPARATOR, names);
    }

    static <T> byte[] sortAndGetPositions(T[] internals, Comparator<T> comparator, NameTable names) {
        byte[] positions;
        int i;
        IdentityHashMap<T, Integer> originalPositions;
        boolean storePositions;
        int size = internals.length;
        boolean bl = storePositions = size > 1 && size <= 256;
        if (storePositions) {
            originalPositions = new IdentityHashMap<T, Integer>(size);
            for (i = 0; i < size; ++i) {
                originalPositions.put(internals[i], i);
            }
        } else {
            originalPositions = null;
        }
        Arrays.sort(internals, comparator);
        if (storePositions) {
            positions = new byte[size];
            for (i = 0; i < size; ++i) {
                positions[((Integer)originalPositions.get(internals[i])).intValue()] = (byte)i;
            }
        } else {
            positions = EMPTY_POSITIONS;
        }
        return names.intern(positions);
    }

    void setSuperClassType(Type superClassType) {
        this.superClassType = superClassType;
    }

    void setInterfaceTypes(Type[] interfaceTypes) {
        this.interfaceTypes = interfaceTypes.length == 0 ? Type.EMPTY_ARRAY : interfaceTypes;
    }

    void setTypeParameters(Type[] typeParameters) {
        this.typeParameters = typeParameters.length == 0 ? Type.EMPTY_ARRAY : typeParameters;
    }

    void setInnerClassInfo(DotName enclosingClass, String simpleName, boolean knownInnerClass) {
        boolean setValues;
        boolean bl = setValues = enclosingClass != null || simpleName != null;
        if (this.nestingInfo == null && (knownInnerClass || setValues)) {
            this.nestingInfo = new NestingInfo();
        }
        if (!setValues) {
            return;
        }
        this.nestingInfo.enclosingClass = enclosingClass;
        this.nestingInfo.simpleName = simpleName;
    }

    void setEnclosingMethod(EnclosingMethodInfo enclosingMethod) {
        if (enclosingMethod == null) {
            return;
        }
        if (this.nestingInfo == null) {
            this.nestingInfo = new NestingInfo();
        }
        this.nestingInfo.enclosingMethod = enclosingMethod;
    }

    void setModule(ModuleInfo module) {
        if (module == null) {
            return;
        }
        if (this.nestingInfo == null) {
            this.nestingInfo = new NestingInfo();
        }
        this.nestingInfo.module = module;
    }

    void setFlags(short flags) {
        this.flags = flags;
    }

    public static final class EnclosingMethodInfo {
        private String name;
        private Type returnType;
        private Type[] parameters;
        private DotName enclosingClass;

        public String name() {
            return this.name;
        }

        public Type returnType() {
            return this.returnType;
        }

        public List<Type> parameters() {
            return Collections.unmodifiableList(Arrays.asList(this.parameters));
        }

        Type[] parametersArray() {
            return this.parameters;
        }

        public DotName enclosingClass() {
            return this.enclosingClass;
        }

        EnclosingMethodInfo(String name, Type returnType, Type[] parameters, DotName enclosingClass) {
            this.name = name;
            this.returnType = returnType;
            this.parameters = parameters;
            this.enclosingClass = enclosingClass;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.returnType).append(' ').append(this.enclosingClass).append('.').append(this.name).append('(');
            for (int i = 0; i < this.parameters.length; ++i) {
                builder.append(this.parameters[i]);
                if (i + 1 >= this.parameters.length) continue;
                builder.append(", ");
            }
            builder.append(')');
            return builder.toString();
        }
    }

    private static final class NestingInfo {
        private DotName enclosingClass;
        private String simpleName;
        private EnclosingMethodInfo enclosingMethod;
        private ModuleInfo module;

        private NestingInfo() {
        }
    }

    public static enum NestingType {
        TOP_LEVEL,
        INNER,
        LOCAL,
        ANONYMOUS;

    }
}

