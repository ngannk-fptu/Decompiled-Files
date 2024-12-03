/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.inline;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
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
import net.bytebuddy.dynamic.scaffold.inline.AbstractInliningDynamicTypeBuilder;
import net.bytebuddy.dynamic.scaffold.inline.InliningImplementationMatcher;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class RebaseDynamicTypeBuilder<T>
extends AbstractInliningDynamicTypeBuilder<T> {
    private final MethodNameTransformer methodNameTransformer;

    public RebaseDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, TypeDescription originalType, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
        this(instrumentedType, new FieldRegistry.Default(), new MethodRegistry.Default(), new RecordComponentRegistry.Default(), annotationRetention.isEnabled() ? new TypeAttributeAppender.ForInstrumentedType.Differentiating(originalType) : TypeAttributeAppender.ForInstrumentedType.INSTANCE, AsmVisitorWrapper.NoOp.INSTANCE, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, Collections.emptyList(), originalType, classFileLocator, methodNameTransformer);
    }

    protected RebaseDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes, TypeDescription originalType, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
        super(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes, originalType, classFileLocator);
        this.methodNameTransformer = methodNameTransformer;
    }

    @Override
    protected DynamicType.Builder<T> materialize(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes) {
        return new RebaseDynamicTypeBuilder<T>(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes, this.originalType, this.classFileLocator, this.methodNameTransformer);
    }

    @Override
    protected TypeWriter<T> toTypeWriter(TypePool typePool) {
        MethodRegistry.Prepared methodRegistry = this.methodRegistry.prepare(this.instrumentedType, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, InliningImplementationMatcher.of(this.ignoredMethods, this.originalType));
        HashSet<MethodDescription.SignatureToken> rebaseables = new HashSet<MethodDescription.SignatureToken>(this.originalType.getDeclaredMethods().asSignatureTokenList(ElementMatchers.is(this.originalType), this.instrumentedType));
        rebaseables.retainAll(methodRegistry.getInstrumentedMethods().asSignatureTokenList());
        MethodRebaseResolver methodRebaseResolver = MethodRebaseResolver.Default.make(methodRegistry.getInstrumentedType(), rebaseables, this.classFileVersion, this.auxiliaryTypeNamingStrategy, this.methodNameTransformer);
        return TypeWriter.Default.forRebasing(methodRegistry, this.auxiliaryTypes, this.fieldRegistry.compile(methodRegistry.getInstrumentedType()), this.recordComponentRegistry.compile(methodRegistry.getInstrumentedType()), this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.annotationValueFilterFactory, this.annotationRetention, this.auxiliaryTypeNamingStrategy, this.implementationContextFactory, this.typeValidation, this.classWriterStrategy, TypePool.Explicit.wrap(this.instrumentedType, this.auxiliaryTypes, typePool), this.originalType, this.classFileLocator, methodRebaseResolver);
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
        return this.methodNameTransformer.equals(((RebaseDynamicTypeBuilder)object).methodNameTransformer);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.methodNameTransformer.hashCode();
    }
}

