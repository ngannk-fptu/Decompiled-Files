/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.inline;

import java.util.Collections;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
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
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RedefinitionDynamicTypeBuilder<T>
extends AbstractInliningDynamicTypeBuilder<T> {
    public RedefinitionDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, TypeDescription originalType, ClassFileLocator classFileLocator) {
        this(instrumentedType, new FieldRegistry.Default(), new MethodRegistry.Default(), new RecordComponentRegistry.Default(), annotationRetention.isEnabled() ? new TypeAttributeAppender.ForInstrumentedType.Differentiating(originalType) : TypeAttributeAppender.ForInstrumentedType.INSTANCE, AsmVisitorWrapper.NoOp.INSTANCE, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, Collections.emptyList(), originalType, classFileLocator);
    }

    protected RedefinitionDynamicTypeBuilder(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes, TypeDescription originalType, ClassFileLocator classFileLocator) {
        super(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes, originalType, classFileLocator);
    }

    @Override
    protected DynamicType.Builder<T> materialize(InstrumentedType.WithFlexibleName instrumentedType, FieldRegistry fieldRegistry, MethodRegistry methodRegistry, RecordComponentRegistry recordComponentRegistry, TypeAttributeAppender typeAttributeAppender, AsmVisitorWrapper asmVisitorWrapper, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, AnnotationValueFilter.Factory annotationValueFilterFactory, AnnotationRetention annotationRetention, Implementation.Context.Factory implementationContextFactory, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, ClassWriterStrategy classWriterStrategy, LatentMatcher<? super MethodDescription> ignoredMethods, List<? extends DynamicType> auxiliaryTypes) {
        return new RedefinitionDynamicTypeBuilder<T>(instrumentedType, fieldRegistry, methodRegistry, recordComponentRegistry, typeAttributeAppender, asmVisitorWrapper, classFileVersion, auxiliaryTypeNamingStrategy, annotationValueFilterFactory, annotationRetention, implementationContextFactory, methodGraphCompiler, typeValidation, visibilityBridgeStrategy, classWriterStrategy, ignoredMethods, auxiliaryTypes, this.originalType, this.classFileLocator);
    }

    @Override
    protected TypeWriter<T> toTypeWriter(TypePool typePool) {
        MethodRegistry.Prepared methodRegistry = this.methodRegistry.prepare(this.instrumentedType, this.methodGraphCompiler, this.typeValidation, this.visibilityBridgeStrategy, InliningImplementationMatcher.of(this.ignoredMethods, this.originalType));
        return TypeWriter.Default.forRedefinition(methodRegistry, this.auxiliaryTypes, this.fieldRegistry.compile(methodRegistry.getInstrumentedType()), this.recordComponentRegistry.compile(methodRegistry.getInstrumentedType()), this.typeAttributeAppender, this.asmVisitorWrapper, this.classFileVersion, this.annotationValueFilterFactory, this.annotationRetention, this.auxiliaryTypeNamingStrategy, this.implementationContextFactory, this.typeValidation, this.classWriterStrategy, TypePool.Explicit.wrap(this.instrumentedType, this.auxiliaryTypes, typePool), this.originalType, this.classFileLocator);
    }
}

