/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.CrossParameterConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.ParameterConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.ReturnValueConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

abstract class ExecutableConstraintMappingContextImpl {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected final TypeConstraintMappingContextImpl<?> typeContext;
    protected final Executable executable;
    private final ParameterConstraintMappingContextImpl[] parameterContexts;
    private ReturnValueConstraintMappingContextImpl returnValueContext;
    private CrossParameterConstraintMappingContextImpl crossParameterContext;

    protected ExecutableConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Executable executable) {
        this.typeContext = typeContext;
        this.executable = executable;
        this.parameterContexts = new ParameterConstraintMappingContextImpl[executable.getParameterTypes().length];
    }

    public ParameterConstraintMappingContext parameter(int index) {
        if (index < 0 || index >= this.executable.getParameterTypes().length) {
            throw LOG.getInvalidExecutableParameterIndexException(this.executable, index);
        }
        ParameterConstraintMappingContextImpl context = this.parameterContexts[index];
        if (context != null) {
            throw LOG.getParameterHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable, index);
        }
        this.parameterContexts[index] = context = new ParameterConstraintMappingContextImpl(this, index);
        return context;
    }

    public CrossParameterConstraintMappingContext crossParameter() {
        if (this.crossParameterContext != null) {
            throw LOG.getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable);
        }
        this.crossParameterContext = new CrossParameterConstraintMappingContextImpl(this);
        return this.crossParameterContext;
    }

    public ReturnValueConstraintMappingContext returnValue() {
        if (this.returnValueContext != null) {
            throw LOG.getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException(this.typeContext.getBeanClass(), this.executable);
        }
        this.returnValueContext = new ReturnValueConstraintMappingContextImpl(this);
        return this.returnValueContext;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public TypeConstraintMappingContextImpl<?> getTypeContext() {
        return this.typeContext;
    }

    public ConstrainedElement build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return new ConstrainedExecutable(ConfigurationSource.API, this.executable, this.getParameters(constraintHelper, typeResolutionHelper, valueExtractorManager), this.crossParameterContext != null ? this.crossParameterContext.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager) : Collections.emptySet(), this.returnValueContext != null ? this.returnValueContext.getCascadingMetaDataBuilder() : CascadingMetaDataBuilder.nonCascading());
    }

    private List<ConstrainedParameter> getParameters(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        ArrayList<ConstrainedParameter> constrainedParameters = CollectionHelper.newArrayList();
        for (int i = 0; i < this.parameterContexts.length; ++i) {
            ParameterConstraintMappingContextImpl parameter = this.parameterContexts[i];
            if (parameter != null) {
                constrainedParameters.add(parameter.build(constraintHelper, typeResolutionHelper, valueExtractorManager));
                continue;
            }
            constrainedParameters.add(new ConstrainedParameter(ConfigurationSource.API, this.executable, ReflectionHelper.typeOf(this.executable, i), i));
        }
        return constrainedParameters;
    }
}

