/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AsmVisitorWrapper {
    public static final int NO_FLAGS = 0;

    public int mergeWriter(int var1);

    public int mergeReader(int var1);

    public ClassVisitor wrap(TypeDescription var1, ClassVisitor var2, Implementation.Context var3, TypePool var4, FieldList<FieldDescription.InDefinedShape> var5, MethodList<?> var6, int var7, int var8);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements AsmVisitorWrapper {
        private final List<AsmVisitorWrapper> asmVisitorWrappers = new ArrayList<AsmVisitorWrapper>();

        public Compound(AsmVisitorWrapper ... asmVisitorWrapper) {
            this(Arrays.asList(asmVisitorWrapper));
        }

        public Compound(List<? extends AsmVisitorWrapper> asmVisitorWrappers) {
            for (AsmVisitorWrapper asmVisitorWrapper : asmVisitorWrappers) {
                if (asmVisitorWrapper instanceof Compound) {
                    this.asmVisitorWrappers.addAll(((Compound)asmVisitorWrapper).asmVisitorWrappers);
                    continue;
                }
                if (asmVisitorWrapper instanceof NoOp) continue;
                this.asmVisitorWrappers.add(asmVisitorWrapper);
            }
        }

        @Override
        public int mergeWriter(int flags) {
            for (AsmVisitorWrapper asmVisitorWrapper : this.asmVisitorWrappers) {
                flags = asmVisitorWrapper.mergeWriter(flags);
            }
            return flags;
        }

        @Override
        public int mergeReader(int flags) {
            for (AsmVisitorWrapper asmVisitorWrapper : this.asmVisitorWrappers) {
                flags = asmVisitorWrapper.mergeReader(flags);
            }
            return flags;
        }

        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            for (AsmVisitorWrapper asmVisitorWrapper : this.asmVisitorWrappers) {
                classVisitor = asmVisitorWrapper.wrap(instrumentedType, classVisitor, implementationContext, typePool, fields, methods, writerFlags, readerFlags);
            }
            return classVisitor;
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.asmVisitorWrappers).equals(((Compound)object).asmVisitorWrappers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.asmVisitorWrappers).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForDeclaredMethods
    implements AsmVisitorWrapper {
        private final List<Entry> entries;
        private final int writerFlags;
        private final int readerFlags;

        public ForDeclaredMethods() {
            this(Collections.emptyList(), 0, 0);
        }

        protected ForDeclaredMethods(List<Entry> entries, int writerFlags, int readerFlags) {
            this.entries = entries;
            this.writerFlags = writerFlags;
            this.readerFlags = readerFlags;
        }

        public ForDeclaredMethods method(ElementMatcher<? super MethodDescription> matcher, MethodVisitorWrapper ... methodVisitorWrapper) {
            return this.method(matcher, Arrays.asList(methodVisitorWrapper));
        }

        public ForDeclaredMethods method(ElementMatcher<? super MethodDescription> matcher, List<? extends MethodVisitorWrapper> methodVisitorWrappers) {
            return this.invokable(ElementMatchers.isMethod().and(matcher), methodVisitorWrappers);
        }

        public ForDeclaredMethods constructor(ElementMatcher<? super MethodDescription> matcher, MethodVisitorWrapper ... methodVisitorWrapper) {
            return this.constructor(matcher, Arrays.asList(methodVisitorWrapper));
        }

        public ForDeclaredMethods constructor(ElementMatcher<? super MethodDescription> matcher, List<? extends MethodVisitorWrapper> methodVisitorWrappers) {
            return this.invokable(ElementMatchers.isConstructor().and(matcher), methodVisitorWrappers);
        }

        public ForDeclaredMethods invokable(ElementMatcher<? super MethodDescription> matcher, MethodVisitorWrapper ... methodVisitorWrapper) {
            return this.invokable(matcher, Arrays.asList(methodVisitorWrapper));
        }

        public ForDeclaredMethods invokable(ElementMatcher<? super MethodDescription> matcher, List<? extends MethodVisitorWrapper> methodVisitorWrappers) {
            return new ForDeclaredMethods(CompoundList.of(this.entries, new Entry(matcher, methodVisitorWrappers)), this.writerFlags, this.readerFlags);
        }

        public ForDeclaredMethods writerFlags(int flags) {
            return new ForDeclaredMethods(this.entries, this.writerFlags | flags, this.readerFlags);
        }

        public ForDeclaredMethods readerFlags(int flags) {
            return new ForDeclaredMethods(this.entries, this.writerFlags, this.readerFlags | flags);
        }

        @Override
        public int mergeWriter(int flags) {
            return flags | this.writerFlags;
        }

        @Override
        public int mergeReader(int flags) {
            return flags | this.readerFlags;
        }

        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            HashMap<String, MethodDescription> mapped = new HashMap<String, MethodDescription>();
            for (MethodDescription methodDescription : CompoundList.of(methods, new MethodDescription.Latent.TypeInitializer(instrumentedType))) {
                mapped.put(methodDescription.getInternalName() + methodDescription.getDescriptor(), methodDescription);
            }
            return new DispatchingVisitor(classVisitor, instrumentedType, implementationContext, typePool, mapped, writerFlags, readerFlags);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            if (this.writerFlags != ((ForDeclaredMethods)object).writerFlags) {
                return false;
            }
            if (this.readerFlags != ((ForDeclaredMethods)object).readerFlags) {
                return false;
            }
            return ((Object)this.entries).equals(((ForDeclaredMethods)object).entries);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + ((Object)this.entries).hashCode()) * 31 + this.writerFlags) * 31 + this.readerFlags;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected class DispatchingVisitor
        extends ClassVisitor {
            private final TypeDescription instrumentedType;
            private final Implementation.Context implementationContext;
            private final TypePool typePool;
            private final int writerFlags;
            private final int readerFlags;
            private final Map<String, MethodDescription> methods;

            protected DispatchingVisitor(ClassVisitor classVisitor, TypeDescription instrumentedType, Implementation.Context implementationContext, TypePool typePool, Map<String, MethodDescription> methods, int writerFlags, int readerFlags) {
                super(OpenedClassReader.ASM_API, classVisitor);
                this.instrumentedType = instrumentedType;
                this.implementationContext = implementationContext;
                this.typePool = typePool;
                this.methods = methods;
                this.writerFlags = writerFlags;
                this.readerFlags = readerFlags;
            }

            @Override
            @MaybeNull
            public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(modifiers, internalName, descriptor, signature, exceptions);
                MethodDescription methodDescription = this.methods.get(internalName + descriptor);
                if (methodVisitor != null && methodDescription != null) {
                    for (Entry entry : ForDeclaredMethods.this.entries) {
                        if (!entry.matches(methodDescription)) continue;
                        methodVisitor = entry.wrap(this.instrumentedType, methodDescription, methodVisitor, this.implementationContext, this.typePool, this.writerFlags, this.readerFlags);
                    }
                }
                return methodVisitor;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Entry
        implements ElementMatcher<MethodDescription>,
        MethodVisitorWrapper {
            private final ElementMatcher<? super MethodDescription> matcher;
            private final List<? extends MethodVisitorWrapper> methodVisitorWrappers;

            protected Entry(ElementMatcher<? super MethodDescription> matcher, List<? extends MethodVisitorWrapper> methodVisitorWrappers) {
                this.matcher = matcher;
                this.methodVisitorWrappers = methodVisitorWrappers;
            }

            @Override
            public boolean matches(@MaybeNull MethodDescription target) {
                return this.matcher.matches(target);
            }

            @Override
            public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
                for (MethodVisitorWrapper methodVisitorWrapper : this.methodVisitorWrappers) {
                    methodVisitor = methodVisitorWrapper.wrap(instrumentedType, instrumentedMethod, methodVisitor, implementationContext, typePool, writerFlags, readerFlags);
                }
                return methodVisitor;
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.matcher.equals(((Entry)object).matcher)) {
                    return false;
                }
                return ((Object)this.methodVisitorWrappers).equals(((Entry)object).methodVisitorWrappers);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + ((Object)this.methodVisitorWrappers).hashCode();
            }
        }

        public static interface MethodVisitorWrapper {
            public MethodVisitor wrap(TypeDescription var1, MethodDescription var2, MethodVisitor var3, Implementation.Context var4, TypePool var5, int var6, int var7);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForDeclaredFields
    extends AbstractBase {
        private final List<Entry> entries;

        public ForDeclaredFields() {
            this(Collections.emptyList());
        }

        protected ForDeclaredFields(List<Entry> entries) {
            this.entries = entries;
        }

        public ForDeclaredFields field(ElementMatcher<? super FieldDescription.InDefinedShape> matcher, FieldVisitorWrapper ... fieldVisitorWrapper) {
            return this.field(matcher, Arrays.asList(fieldVisitorWrapper));
        }

        public ForDeclaredFields field(ElementMatcher<? super FieldDescription.InDefinedShape> matcher, List<? extends FieldVisitorWrapper> fieldVisitorWrappers) {
            return new ForDeclaredFields(CompoundList.of(this.entries, new Entry(matcher, fieldVisitorWrappers)));
        }

        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            HashMap<String, FieldDescription.InDefinedShape> mapped = new HashMap<String, FieldDescription.InDefinedShape>();
            for (FieldDescription.InDefinedShape fieldDescription : fields) {
                mapped.put(fieldDescription.getInternalName() + fieldDescription.getDescriptor(), fieldDescription);
            }
            return new DispatchingVisitor(classVisitor, instrumentedType, mapped);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.entries).equals(((ForDeclaredFields)object).entries);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.entries).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected class DispatchingVisitor
        extends ClassVisitor {
            private final TypeDescription instrumentedType;
            private final Map<String, FieldDescription.InDefinedShape> fields;

            protected DispatchingVisitor(ClassVisitor classVisitor, TypeDescription instrumentedType, Map<String, FieldDescription.InDefinedShape> fields) {
                super(OpenedClassReader.ASM_API, classVisitor);
                this.instrumentedType = instrumentedType;
                this.fields = fields;
            }

            @Override
            @MaybeNull
            public FieldVisitor visitField(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
                FieldVisitor fieldVisitor = super.visitField(modifiers, internalName, descriptor, signature, value);
                FieldDescription.InDefinedShape fieldDescription = this.fields.get(internalName + descriptor);
                if (fieldVisitor != null && fieldDescription != null) {
                    for (Entry entry : ForDeclaredFields.this.entries) {
                        if (!entry.matches(fieldDescription)) continue;
                        fieldVisitor = entry.wrap(this.instrumentedType, fieldDescription, fieldVisitor);
                    }
                }
                return fieldVisitor;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Entry
        implements ElementMatcher<FieldDescription.InDefinedShape>,
        FieldVisitorWrapper {
            private final ElementMatcher<? super FieldDescription.InDefinedShape> matcher;
            private final List<? extends FieldVisitorWrapper> fieldVisitorWrappers;

            protected Entry(ElementMatcher<? super FieldDescription.InDefinedShape> matcher, List<? extends FieldVisitorWrapper> fieldVisitorWrappers) {
                this.matcher = matcher;
                this.fieldVisitorWrappers = fieldVisitorWrappers;
            }

            @Override
            public boolean matches(@MaybeNull FieldDescription.InDefinedShape target) {
                return this.matcher.matches(target);
            }

            @Override
            public FieldVisitor wrap(TypeDescription instrumentedType, FieldDescription.InDefinedShape fieldDescription, FieldVisitor fieldVisitor) {
                for (FieldVisitorWrapper fieldVisitorWrapper : this.fieldVisitorWrappers) {
                    fieldVisitor = fieldVisitorWrapper.wrap(instrumentedType, fieldDescription, fieldVisitor);
                }
                return fieldVisitor;
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.matcher.equals(((Entry)object).matcher)) {
                    return false;
                }
                return ((Object)this.fieldVisitorWrappers).equals(((Entry)object).fieldVisitorWrappers);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + ((Object)this.fieldVisitorWrappers).hashCode();
            }
        }

        public static interface FieldVisitorWrapper {
            public FieldVisitor wrap(TypeDescription var1, FieldDescription.InDefinedShape var2, FieldVisitor var3);
        }
    }

    public static abstract class AbstractBase
    implements AsmVisitorWrapper {
        public int mergeWriter(int flags) {
            return flags;
        }

        public int mergeReader(int flags) {
            return flags;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements AsmVisitorWrapper
    {
        INSTANCE;


        @Override
        public int mergeWriter(int flags) {
            return flags;
        }

        @Override
        public int mergeReader(int flags) {
            return flags;
        }

        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return classVisitor;
        }
    }
}

