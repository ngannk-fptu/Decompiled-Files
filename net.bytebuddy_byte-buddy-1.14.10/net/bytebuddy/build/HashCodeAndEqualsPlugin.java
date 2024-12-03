/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Comparator;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.HashCodeMethod;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Enhance
public class HashCodeAndEqualsPlugin
implements Plugin,
Plugin.Factory,
MethodAttributeAppender.Factory,
MethodAttributeAppender {
    private static final MethodDescription.InDefinedShape ENHANCE_INVOKE_SUPER;
    private static final MethodDescription.InDefinedShape ENHANCE_SIMPLE_COMPARISON_FIRST;
    private static final MethodDescription.InDefinedShape ENHANCE_INCLUDE_SYNTHETIC_FIELDS;
    private static final MethodDescription.InDefinedShape ENHANCE_PERMIT_SUBCLASS_EQUALITY;
    private static final MethodDescription.InDefinedShape ENHANCE_USE_TYPE_HASH_CONSTANT;
    private static final MethodDescription.InDefinedShape VALUE_HANDLING_VALUE;
    private static final MethodDescription.InDefinedShape SORTED_VALUE;
    @MaybeNull
    @ValueHandling(value=ValueHandling.Sort.REVERSE_NULLABILITY)
    private final String annotationType;

    public HashCodeAndEqualsPlugin() {
        this(null);
    }

    public HashCodeAndEqualsPlugin(@MaybeNull String annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public Plugin make() {
        return this;
    }

    @Override
    public boolean matches(@MaybeNull TypeDescription target) {
        return target != null && target.getDeclaredAnnotations().isAnnotationPresent(Enhance.class);
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Annotation presence is required by matcher.")
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        AnnotationDescription.Loadable<Enhance> enhance = typeDescription.getDeclaredAnnotations().ofType(Enhance.class);
        if (((MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isHashCode())).isEmpty()) {
            builder = builder.method(ElementMatchers.isHashCode()).intercept(enhance.getValue(ENHANCE_INVOKE_SUPER).load(Enhance.class.getClassLoader()).resolve(Enhance.InvokeSuper.class).hashCodeMethod(typeDescription, enhance.getValue(ENHANCE_USE_TYPE_HASH_CONSTANT).resolve(Boolean.class), enhance.getValue(ENHANCE_PERMIT_SUBCLASS_EQUALITY).resolve(Boolean.class)).withIgnoredFields(enhance.getValue(ENHANCE_INCLUDE_SYNTHETIC_FIELDS).resolve(Boolean.class) != false ? ElementMatchers.none() : ElementMatchers.isSynthetic()).withIgnoredFields(new ValueMatcher(ValueHandling.Sort.IGNORE)).withNonNullableFields(this.nonNullable(new ValueMatcher(ValueHandling.Sort.REVERSE_NULLABILITY))));
        }
        if (((MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isEquals())).isEmpty()) {
            EqualsMethod equalsMethod = enhance.getValue(ENHANCE_INVOKE_SUPER).load(Enhance.class.getClassLoader()).resolve(Enhance.InvokeSuper.class).equalsMethod(typeDescription).withIgnoredFields(enhance.getValue(ENHANCE_INCLUDE_SYNTHETIC_FIELDS).resolve(Boolean.class) != false ? ElementMatchers.none() : ElementMatchers.isSynthetic()).withIgnoredFields(new ValueMatcher(ValueHandling.Sort.IGNORE)).withNonNullableFields(this.nonNullable(new ValueMatcher(ValueHandling.Sort.REVERSE_NULLABILITY))).withFieldOrder(AnnotationOrderComparator.INSTANCE);
            if (enhance.getValue(ENHANCE_SIMPLE_COMPARISON_FIRST).resolve(Boolean.class).booleanValue()) {
                equalsMethod = equalsMethod.withPrimitiveTypedFieldsFirst().withEnumerationTypedFieldsFirst().withPrimitiveWrapperTypedFieldsFirst().withStringTypedFieldsFirst();
            }
            builder = builder.method(ElementMatchers.isEquals()).intercept(enhance.getValue(ENHANCE_PERMIT_SUBCLASS_EQUALITY).resolve(Boolean.class) != false ? equalsMethod.withSubclassEquality() : equalsMethod).attribute(this);
        }
        return builder;
    }

    protected ElementMatcher<FieldDescription> nonNullable(ElementMatcher<FieldDescription> matcher) {
        return matcher;
    }

    @Override
    public void close() {
    }

    @Override
    public MethodAttributeAppender make(TypeDescription typeDescription) {
        return this;
    }

    @Override
    public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
        AnnotationVisitor annotationVisitor;
        if (this.annotationType != null && (annotationVisitor = methodVisitor.visitParameterAnnotation(0, "L" + this.annotationType.replace('.', '/') + ";", true)) != null) {
            annotationVisitor.visitEnd();
        }
    }

    static {
        MethodList<MethodDescription.InDefinedShape> enhanceMethods = TypeDescription.ForLoadedType.of(Enhance.class).getDeclaredMethods();
        ENHANCE_INVOKE_SUPER = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("invokeSuper"))).getOnly();
        ENHANCE_SIMPLE_COMPARISON_FIRST = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("simpleComparisonsFirst"))).getOnly();
        ENHANCE_INCLUDE_SYNTHETIC_FIELDS = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("includeSyntheticFields"))).getOnly();
        ENHANCE_PERMIT_SUBCLASS_EQUALITY = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("permitSubclassEquality"))).getOnly();
        ENHANCE_USE_TYPE_HASH_CONSTANT = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("useTypeHashConstant"))).getOnly();
        VALUE_HANDLING_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(ValueHandling.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
        SORTED_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Sorted.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
    }

    public boolean equals(@MaybeNull Object object) {
        block10: {
            block9: {
                String string;
                block8: {
                    String string2;
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    String string3 = ((HashCodeAndEqualsPlugin)object).annotationType;
                    string = string2 = this.annotationType;
                    if (string3 == null) break block8;
                    if (string == null) break block9;
                    if (!string2.equals(string3)) {
                        return false;
                    }
                    break block10;
                }
                if (string == null) break block10;
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int n = this.getClass().hashCode() * 31;
        String string = this.annotationType;
        if (string != null) {
            n = n + string.hashCode();
        }
        return n;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Enhance
    protected static class ValueMatcher
    extends ElementMatcher.Junction.ForNonNullValues<FieldDescription> {
        private final ValueHandling.Sort sort;

        protected ValueMatcher(ValueHandling.Sort sort) {
            this.sort = sort;
        }

        @Override
        protected boolean doMatch(FieldDescription target) {
            AnnotationDescription.Loadable<ValueHandling> annotation = target.getDeclaredAnnotations().ofType(ValueHandling.class);
            return annotation != null && annotation.getValue(VALUE_HANDLING_VALUE).load(ValueHandling.class.getClassLoader()).resolve(ValueHandling.Sort.class) == this.sort;
        }

        @Override
        public boolean equals(@MaybeNull Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.sort.equals((Object)((ValueMatcher)object).sort);
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + this.sort.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum AnnotationOrderComparator implements Comparator<FieldDescription.InDefinedShape>
    {
        INSTANCE;


        @Override
        public int compare(FieldDescription.InDefinedShape left, FieldDescription.InDefinedShape right) {
            int rightValue;
            AnnotationDescription.Loadable<Sorted> leftAnnotation = left.getDeclaredAnnotations().ofType(Sorted.class);
            AnnotationDescription.Loadable<Sorted> rightAnnotation = right.getDeclaredAnnotations().ofType(Sorted.class);
            int leftValue = leftAnnotation == null ? 0 : leftAnnotation.getValue(SORTED_VALUE).resolve(Integer.class);
            int n = rightValue = rightAnnotation == null ? 0 : rightAnnotation.getValue(SORTED_VALUE).resolve(Integer.class);
            if (leftValue > rightValue) {
                return -1;
            }
            if (leftValue < rightValue) {
                return 1;
            }
            return 0;
        }
    }

    @Documented
    @Target(value={ElementType.FIELD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Sorted {
        public static final int DEFAULT = 0;

        public int value();
    }

    @Documented
    @Target(value={ElementType.FIELD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface ValueHandling {
        public Sort value();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Sort {
            IGNORE,
            REVERSE_NULLABILITY;

        }
    }

    @Documented
    @Target(value={ElementType.TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Enhance {
        public InvokeSuper invokeSuper() default InvokeSuper.IF_DECLARED;

        public boolean simpleComparisonsFirst() default true;

        public boolean includeSyntheticFields() default false;

        public boolean permitSubclassEquality() default false;

        public boolean useTypeHashConstant() default true;

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum InvokeSuper {
            IF_DECLARED{

                protected HashCodeMethod hashCodeMethod(TypeDescription instrumentedType, boolean typeHash, boolean subclassEquality) {
                    for (TypeDescription.Generic typeDefinition = instrumentedType.getSuperClass(); typeDefinition != null && !typeDefinition.represents((Type)((Object)Object.class)); typeDefinition = typeDefinition.getSuperClass()) {
                        if (typeDefinition.asErasure().getDeclaredAnnotations().isAnnotationPresent(Enhance.class)) {
                            return HashCodeMethod.usingSuperClassOffset();
                        }
                        MethodList hashCode = (MethodList)typeDefinition.getDeclaredMethods().filter(ElementMatchers.isHashCode());
                        if (hashCode.isEmpty()) continue;
                        return ((MethodDescription)hashCode.getOnly()).isAbstract() ? (typeHash ? HashCodeMethod.usingTypeHashOffset(!subclassEquality) : HashCodeMethod.usingDefaultOffset()) : HashCodeMethod.usingSuperClassOffset();
                    }
                    return typeHash ? HashCodeMethod.usingTypeHashOffset(!subclassEquality) : HashCodeMethod.usingDefaultOffset();
                }

                protected EqualsMethod equalsMethod(TypeDescription instrumentedType) {
                    for (TypeDescription.Generic typeDefinition = instrumentedType.getSuperClass(); typeDefinition != null && !typeDefinition.represents((Type)((Object)Object.class)); typeDefinition = typeDefinition.getSuperClass()) {
                        if (typeDefinition.asErasure().getDeclaredAnnotations().isAnnotationPresent(Enhance.class)) {
                            return EqualsMethod.requiringSuperClassEquality();
                        }
                        MethodList hashCode = (MethodList)typeDefinition.getDeclaredMethods().filter(ElementMatchers.isHashCode());
                        if (hashCode.isEmpty()) continue;
                        return ((MethodDescription)hashCode.getOnly()).isAbstract() ? EqualsMethod.isolated() : EqualsMethod.requiringSuperClassEquality();
                    }
                    return EqualsMethod.isolated();
                }
            }
            ,
            IF_ANNOTATED{

                protected HashCodeMethod hashCodeMethod(TypeDescription instrumentedType, boolean typeHash, boolean subclassEquality) {
                    TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                    return superClass != null && superClass.asErasure().getDeclaredAnnotations().isAnnotationPresent(Enhance.class) ? HashCodeMethod.usingSuperClassOffset() : (typeHash ? HashCodeMethod.usingTypeHashOffset(!subclassEquality) : HashCodeMethod.usingDefaultOffset());
                }

                protected EqualsMethod equalsMethod(TypeDescription instrumentedType) {
                    TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                    return superClass != null && superClass.asErasure().getDeclaredAnnotations().isAnnotationPresent(Enhance.class) ? EqualsMethod.requiringSuperClassEquality() : EqualsMethod.isolated();
                }
            }
            ,
            ALWAYS{

                protected HashCodeMethod hashCodeMethod(TypeDescription instrumentedType, boolean typeHash, boolean subclassEquality) {
                    return HashCodeMethod.usingSuperClassOffset();
                }

                protected EqualsMethod equalsMethod(TypeDescription instrumentedType) {
                    return EqualsMethod.requiringSuperClassEquality();
                }
            }
            ,
            NEVER{

                protected HashCodeMethod hashCodeMethod(TypeDescription instrumentedType, boolean typeHash, boolean subclassEquality) {
                    return typeHash ? HashCodeMethod.usingTypeHashOffset(!subclassEquality) : HashCodeMethod.usingDefaultOffset();
                }

                protected EqualsMethod equalsMethod(TypeDescription instrumentedType) {
                    return EqualsMethod.isolated();
                }
            };


            protected abstract HashCodeMethod hashCodeMethod(TypeDescription var1, boolean var2, boolean var3);

            protected abstract EqualsMethod equalsMethod(TypeDescription var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Enhance
    public static class WithNonNullableFields
    extends HashCodeAndEqualsPlugin {
        public WithNonNullableFields() {
            this(null);
        }

        public WithNonNullableFields(@MaybeNull String annotationType) {
            super(annotationType);
        }

        @Override
        protected ElementMatcher<FieldDescription> nonNullable(ElementMatcher<FieldDescription> matcher) {
            return ElementMatchers.not(matcher);
        }

        @Override
        public boolean equals(@MaybeNull Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            return this.getClass() == object.getClass();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}

