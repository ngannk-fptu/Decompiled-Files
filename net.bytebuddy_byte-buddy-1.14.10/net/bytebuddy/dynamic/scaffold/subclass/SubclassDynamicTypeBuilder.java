/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.subclass;

import java.util.Collections;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.scaffold.ClassWriterStrategy;
import net.bytebuddy.dynamic.scaffold.FieldRegistry;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.dynamic.scaffold.RecordComponentRegistry;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.SubclassImplementationTarget;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class SubclassDynamicTypeBuilder<T>
extends DynamicType.Builder.AbstractBase.Adapter<T> {
    private final ConstructorStrategy constructorStrategy;

    public SubclassDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, ConstructorStrategy constructorStrategy) {
        this(instrumentedType, new FieldRegistry.Default(), new MethodRegistry.Default(), new RecordComponentRegistry.Default(), TypeAttributeAppender.ForInstrumentedType.INSTANCE, AsmVisitorWrapper.NoOp.INSTANCE, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, Collections.emptyList(), constructorStrategy);
    }

    protected SubclassDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes, ConstructorStrategy constructorStrategy) {
        super(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes);
        this.constructorStrategy = constructorStrategy;
    }

    @Override
    protected DynamicType.Builder<T> materialize(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes) {
        return new SubclassDynamicTypeBuilder<T>(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes, this.constructorStrategy);
    }

    @Override
    protected TypeWriter<T> toTypeWriter() {
        return this.toTypeWriter(TypePool.ClassLoading.ofSystemLoader());
    }

    @Override
    protected TypeWriter<T> toTypeWriter(TypePool typePool) {
        MethodRegistry.Compiled methodRegistry = this.constructorStrategy.inject(this.instrumentedType, this.methodRegistry).prepare(this.applyConstructorStrategy(this.instrumentedType), this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, new InstrumentableMatcher(this.ignoredMethods)).compile(SubclassImplementationTarget.Factory.SUPER_CLASS, this.classFileVersion);
        return TypeWriter.Default.forCreation(methodRegistry, this.auxiliaryTypes, this.fieldRegistry.compile(methodRegistry.getInstrumentedType()), this.recordComponentRegistry.compile(methodRegistry.getInstrumentedType()), this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.annotationValueFilterFactory, this.annotationRetention, this.auxiliaryTypeNamingStrategy, this.implementationContextFactory, this.typeValidation, this.classWriterStrategy, TypePool.Explicit.wrap(this.instrumentedType, this.auxiliaryTypes, typePool));
    }

    private InstrumentedType applyConstructorStrategy(InstrumentedType instrumentedType) {
        if (!instrumentedType.isInterface()) {
            for (MethodDescription.Token token : this.constructorStrategy.extractConstructors(instrumentedType)) {
                instrumentedType = instrumentedType.withMethod(token);
            }
        }
        return instrumentedType;
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
        return this.constructorStrategy.equals(((SubclassDynamicTypeBuilder)object).constructorStrategy);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.constructorStrategy.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class InstrumentableMatcher
    implements LatentMatcher<MethodDescription> {
        private final LatentMatcher<? super MethodDescription> ignoredMethods;

        protected InstrumentableMatcher(LatentMatcher<? super MethodDescription> ignoredMethods) {
            this.ignoredMethods = ignoredMethods;
        }

        @Override
        public ElementMatcher<? super MethodDescription> resolve(TypeDescription typeDescription) {
            return ElementMatchers.isVirtual().and(ElementMatchers.not(ElementMatchers.isFinal())).and(ElementMatchers.isVisibleTo(typeDescription)).and(ElementMatchers.not(this.ignoredMethods.resolve(typeDescription))).or(ElementMatchers.isDeclaredBy(typeDescription));
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
            return this.ignoredMethods.equals(((InstrumentableMatcher)object).ignoredMethods);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.ignoredMethods.hashCode();
        }
    }
}

