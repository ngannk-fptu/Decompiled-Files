/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.metadata.ParameterDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ParameterDescriptor;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder;
import org.hibernate.validator.internal.metadata.aggregated.ParameterMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ReturnValueMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ValidatableParametersMetaData;
import org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ExecutableDescriptorImpl;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

public class ExecutableMetaData
extends AbstractConstraintMetaData {
    private final Class<?>[] parameterTypes;
    private final List<ParameterMetaData> parameterMetaDataList;
    private final ValidatableParametersMetaData validatableParametersMetaData;
    private final Set<MetaConstraint<?>> crossParameterConstraints;
    private final boolean isGetter;
    private final Set<String> signatures;
    private final ReturnValueMetaData returnValueMetaData;
    private final ElementKind kind;

    private ExecutableMetaData(String name, Type returnType, Class<?>[] parameterTypes, ElementKind kind, Set<String> signatures, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> returnValueContainerElementConstraints, List<ParameterMetaData> parameterMetaDataList, Set<MetaConstraint<?>> crossParameterConstraints, CascadingMetaData cascadingMetaData, boolean isConstrained, boolean isGetter) {
        super(name, returnType, returnValueConstraints, returnValueContainerElementConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), isConstrained);
        this.parameterTypes = parameterTypes;
        this.parameterMetaDataList = CollectionHelper.toImmutableList(parameterMetaDataList);
        this.validatableParametersMetaData = new ValidatableParametersMetaData(parameterMetaDataList);
        this.crossParameterConstraints = CollectionHelper.toImmutableSet(crossParameterConstraints);
        this.signatures = signatures;
        this.returnValueMetaData = new ReturnValueMetaData(returnType, returnValueConstraints, returnValueContainerElementConstraints, cascadingMetaData);
        this.isGetter = isGetter;
        this.kind = kind;
    }

    public ParameterMetaData getParameterMetaData(int parameterIndex) {
        return this.parameterMetaDataList.get(parameterIndex);
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public Set<String> getSignatures() {
        return this.signatures;
    }

    public Set<MetaConstraint<?>> getCrossParameterConstraints() {
        return this.crossParameterConstraints;
    }

    public ValidatableParametersMetaData getValidatableParametersMetaData() {
        return this.validatableParametersMetaData;
    }

    public ReturnValueMetaData getReturnValueMetaData() {
        return this.returnValueMetaData;
    }

    @Override
    public ExecutableDescriptorImpl asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ExecutableDescriptorImpl(this.getType(), this.getName(), this.asDescriptors(this.getCrossParameterConstraints()), this.returnValueMetaData.asDescriptor(defaultGroupSequenceRedefined, defaultGroupSequence), this.parametersAsDescriptors(defaultGroupSequenceRedefined, defaultGroupSequence), defaultGroupSequenceRedefined, this.isGetter, defaultGroupSequence);
    }

    private List<ParameterDescriptor> parametersAsDescriptors(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        ArrayList<ParameterDescriptor> parameterDescriptorList = CollectionHelper.newArrayList();
        for (ParameterMetaData parameterMetaData : this.parameterMetaDataList) {
            parameterDescriptorList.add(parameterMetaData.asDescriptor(defaultGroupSequenceRedefined, defaultGroupSequence));
        }
        return parameterDescriptorList;
    }

    @Override
    public ElementKind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        StringBuilder parameterBuilder = new StringBuilder();
        for (Class<?> oneParameterType : this.getParameterTypes()) {
            parameterBuilder.append(oneParameterType.getSimpleName());
            parameterBuilder.append(", ");
        }
        String parameters = parameterBuilder.length() > 0 ? parameterBuilder.substring(0, parameterBuilder.length() - 2) : parameterBuilder.toString();
        return "ExecutableMetaData [executable=" + this.getType() + " " + this.getName() + "(" + parameters + "), isCascading=" + this.isCascading() + ", isConstrained=" + this.isConstrained() + "]";
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.parameterTypes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ExecutableMetaData other = (ExecutableMetaData)obj;
        return Arrays.equals(this.parameterTypes, other.parameterTypes);
    }

    public static class Builder
    extends MetaDataBuilder {
        private final Set<String> signatures = CollectionHelper.newHashSet();
        private final ConstrainedElement.ConstrainedElementKind kind;
        private final Set<ConstrainedExecutable> constrainedExecutables = CollectionHelper.newHashSet();
        private Executable executable;
        private final boolean isGetterMethod;
        private final Set<MetaConstraint<?>> crossParameterConstraints = CollectionHelper.newHashSet();
        private final Set<MethodConfigurationRule> rules;
        private boolean isConstrained = false;
        private CascadingMetaDataBuilder cascadingMetaDataBuilder;
        private final Map<Class<?>, ConstrainedExecutable> executablesByDeclaringType = CollectionHelper.newHashMap();
        private final ExecutableHelper executableHelper;
        private final ExecutableParameterNameProvider parameterNameProvider;

        public Builder(Class<?> beanClass, ConstrainedExecutable constrainedExecutable, ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider, MethodValidationConfiguration methodValidationConfiguration) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.executableHelper = executableHelper;
            this.parameterNameProvider = parameterNameProvider;
            this.kind = constrainedExecutable.getKind();
            this.executable = constrainedExecutable.getExecutable();
            this.rules = methodValidationConfiguration.getConfiguredRuleSet();
            this.isGetterMethod = constrainedExecutable.isGetterMethod();
            this.add(constrainedExecutable);
        }

        @Override
        public boolean accepts(ConstrainedElement constrainedElement) {
            if (this.kind != constrainedElement.getKind()) {
                return false;
            }
            Executable candidate = ((ConstrainedExecutable)constrainedElement).getExecutable();
            return this.executable.equals(candidate) || this.overrides(this.executable, candidate) || this.overrides(candidate, this.executable);
        }

        private boolean overrides(Executable first, Executable other) {
            if (first instanceof Constructor || other instanceof Constructor) {
                return false;
            }
            return this.executableHelper.overrides((Method)first, (Method)other);
        }

        @Override
        public final void add(ConstrainedElement constrainedElement) {
            super.add(constrainedElement);
            ConstrainedExecutable constrainedExecutable = (ConstrainedExecutable)constrainedElement;
            this.signatures.add(ExecutableHelper.getSignature(constrainedExecutable.getExecutable()));
            this.constrainedExecutables.add(constrainedExecutable);
            this.isConstrained = this.isConstrained || constrainedExecutable.isConstrained();
            this.crossParameterConstraints.addAll(constrainedExecutable.getCrossParameterConstraints());
            this.cascadingMetaDataBuilder = this.cascadingMetaDataBuilder == null ? constrainedExecutable.getCascadingMetaDataBuilder() : this.cascadingMetaDataBuilder.merge(constrainedExecutable.getCascadingMetaDataBuilder());
            this.addToExecutablesByDeclaringType(constrainedExecutable);
            if (this.executable != null && this.overrides(constrainedExecutable.getExecutable(), this.executable)) {
                this.executable = constrainedExecutable.getExecutable();
            }
        }

        private void addToExecutablesByDeclaringType(ConstrainedExecutable executable) {
            Class<?> beanClass = executable.getExecutable().getDeclaringClass();
            ConstrainedExecutable mergedExecutable = this.executablesByDeclaringType.get(beanClass);
            mergedExecutable = mergedExecutable != null ? mergedExecutable.merge(executable) : executable;
            this.executablesByDeclaringType.put(beanClass, mergedExecutable);
        }

        @Override
        public ExecutableMetaData build() {
            this.assertCorrectnessOfConfiguration();
            return new ExecutableMetaData(this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR ? this.executable.getDeclaringClass().getSimpleName() : this.executable.getName(), ReflectionHelper.typeOf(this.executable), this.executable.getParameterTypes(), this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR ? ElementKind.CONSTRUCTOR : ElementKind.METHOD, this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR ? Collections.singleton(ExecutableHelper.getSignature(this.executable)) : CollectionHelper.toImmutableSet(this.signatures), this.adaptOriginsAndImplicitGroups(this.getDirectConstraints()), this.adaptOriginsAndImplicitGroups(this.getContainerElementConstraints()), this.findParameterMetaData(), this.adaptOriginsAndImplicitGroups(this.crossParameterConstraints), this.cascadingMetaDataBuilder.build(this.valueExtractorManager, this.executable), this.isConstrained, this.isGetterMethod);
        }

        private List<ParameterMetaData> findParameterMetaData() {
            ArrayList<ParameterMetaData.Builder> parameterBuilders = null;
            for (ConstrainedExecutable oneExecutable : this.constrainedExecutables) {
                if (parameterBuilders == null) {
                    parameterBuilders = CollectionHelper.newArrayList();
                    for (ConstrainedParameter oneParameter : oneExecutable.getAllParameterMetaData()) {
                        parameterBuilders.add(new ParameterMetaData.Builder(this.executable.getDeclaringClass(), oneParameter, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider));
                    }
                    continue;
                }
                int i = 0;
                for (ConstrainedParameter oneParameter : oneExecutable.getAllParameterMetaData()) {
                    ((ParameterMetaData.Builder)parameterBuilders.get(i)).add(oneParameter);
                    ++i;
                }
            }
            ArrayList<ParameterMetaData> parameterMetaDatas = CollectionHelper.newArrayList();
            for (ParameterMetaData.Builder oneBuilder : parameterBuilders) {
                parameterMetaDatas.add(oneBuilder.build());
            }
            return parameterMetaDatas;
        }

        private void assertCorrectnessOfConfiguration() {
            for (Map.Entry<Class<?>, ConstrainedExecutable> entry : this.executablesByDeclaringType.entrySet()) {
                for (Map.Entry<Class<?>, ConstrainedExecutable> otherEntry : this.executablesByDeclaringType.entrySet()) {
                    for (MethodConfigurationRule rule : this.rules) {
                        rule.apply(entry.getValue(), otherEntry.getValue());
                    }
                }
            }
        }
    }
}

