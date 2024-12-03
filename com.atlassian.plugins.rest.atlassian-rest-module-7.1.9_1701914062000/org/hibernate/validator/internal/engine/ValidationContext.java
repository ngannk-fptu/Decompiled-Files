/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ClockProvider
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.ConstraintViolation
 *  javax.validation.MessageInterpolator
 *  javax.validation.MessageInterpolator$Context
 *  javax.validation.Path
 *  javax.validation.Path$Node
 *  javax.validation.TraversableResolver
 *  javax.validation.ValidationException
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.time.Duration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public class ValidationContext<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidationOperation validationOperation;
    private final ConstraintValidatorManager constraintValidatorManager;
    private final T rootBean;
    private final Class<T> rootBeanClass;
    private final BeanMetaData<T> rootBeanMetaData;
    private final Executable executable;
    private final Object[] executableParameters;
    private final Object executableReturnValue;
    private final Optional<ExecutableMetaData> executableMetaData;
    private final Set<BeanPathMetaConstraintProcessedUnit> processedPathUnits;
    private final Set<BeanGroupProcessedUnit> processedGroupUnits;
    private final Map<Object, Set<PathImpl>> processedPathsPerBean;
    private final Set<ConstraintViolation<T>> failingConstraintViolations;
    private final ConstraintValidatorFactory constraintValidatorFactory;
    private final ValidatorScopedContext validatorScopedContext;
    private final TraversableResolver traversableResolver;
    private final HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
    private final boolean disableAlreadyValidatedBeanTracking;
    private String validatedProperty;

    private ValidationContext(ValidationOperation validationOperation, ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext, T rootBean, Class<T> rootBeanClass, BeanMetaData<T> rootBeanMetaData, Executable executable, Object[] executableParameters, Object executableReturnValue, Optional<ExecutableMetaData> executableMetaData) {
        this.validationOperation = validationOperation;
        this.constraintValidatorManager = constraintValidatorManager;
        this.validatorScopedContext = validatorScopedContext;
        this.constraintValidatorFactory = constraintValidatorFactory;
        this.traversableResolver = traversableResolver;
        this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
        this.rootBean = rootBean;
        this.rootBeanClass = rootBeanClass;
        this.rootBeanMetaData = rootBeanMetaData;
        this.executable = executable;
        this.executableParameters = executableParameters;
        this.executableReturnValue = executableReturnValue;
        this.processedGroupUnits = new HashSet<BeanGroupProcessedUnit>();
        this.processedPathUnits = new HashSet<BeanPathMetaConstraintProcessedUnit>();
        this.processedPathsPerBean = new IdentityHashMap<Object, Set<PathImpl>>();
        this.failingConstraintViolations = CollectionHelper.newHashSet();
        this.executableMetaData = executableMetaData;
        this.disableAlreadyValidatedBeanTracking = ValidationContext.buildDisableAlreadyValidatedBeanTracking(validationOperation, rootBeanMetaData, executableMetaData);
    }

    public static ValidationContextBuilder getValidationContextBuilder(ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
        return new ValidationContextBuilder(constraintValidatorManager, constraintValidatorFactory, validatorScopedContext, traversableResolver, constraintValidatorInitializationContext);
    }

    public T getRootBean() {
        return this.rootBean;
    }

    public Class<T> getRootBeanClass() {
        return this.rootBeanClass;
    }

    public BeanMetaData<T> getRootBeanMetaData() {
        return this.rootBeanMetaData;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Optional<ExecutableMetaData> getExecutableMetaData() {
        return this.executableMetaData;
    }

    public TraversableResolver getTraversableResolver() {
        return this.traversableResolver;
    }

    public boolean isFailFastModeEnabled() {
        return this.validatorScopedContext.isFailFast();
    }

    public ConstraintValidatorManager getConstraintValidatorManager() {
        return this.constraintValidatorManager;
    }

    public List<String> getParameterNames() {
        if (!ValidationOperation.PARAMETER_VALIDATION.equals((Object)this.validationOperation)) {
            return null;
        }
        return this.validatorScopedContext.getParameterNameProvider().getParameterNames(this.executable);
    }

    public ClockProvider getClockProvider() {
        return this.validatorScopedContext.getClockProvider();
    }

    public Object getConstraintValidatorPayload() {
        return this.validatorScopedContext.getConstraintValidatorPayload();
    }

    public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
        return this.constraintValidatorInitializationContext;
    }

    public Set<ConstraintViolation<T>> createConstraintViolations(ValueContext<?, ?> localContext, ConstraintValidatorContextImpl constraintValidatorContext) {
        return constraintValidatorContext.getConstraintViolationCreationContexts().stream().map(c -> this.createConstraintViolation(localContext, (ConstraintViolationCreationContext)c, constraintValidatorContext.getConstraintDescriptor())).collect(Collectors.toSet());
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorFactory;
    }

    public boolean isBeanAlreadyValidated(Object value, Class<?> group, PathImpl path) {
        if (this.disableAlreadyValidatedBeanTracking) {
            return false;
        }
        boolean alreadyValidated = this.isAlreadyValidatedForCurrentGroup(value, group);
        if (alreadyValidated) {
            alreadyValidated = this.isAlreadyValidatedForPath(value, path);
        }
        return alreadyValidated;
    }

    public void markCurrentBeanAsProcessed(ValueContext<?, ?> valueContext) {
        if (this.disableAlreadyValidatedBeanTracking) {
            return;
        }
        this.markCurrentBeanAsProcessedForCurrentGroup(valueContext.getCurrentBean(), valueContext.getCurrentGroup());
        this.markCurrentBeanAsProcessedForCurrentPath(valueContext.getCurrentBean(), valueContext.getPropertyPath());
    }

    public void addConstraintFailures(Set<ConstraintViolation<T>> failingConstraintViolations) {
        this.failingConstraintViolations.addAll(failingConstraintViolations);
    }

    public Set<ConstraintViolation<T>> getFailingConstraints() {
        return this.failingConstraintViolations;
    }

    public ConstraintViolation<T> createConstraintViolation(ValueContext<?, ?> localContext, ConstraintViolationCreationContext constraintViolationCreationContext, ConstraintDescriptor<?> descriptor) {
        String messageTemplate = constraintViolationCreationContext.getMessage();
        String interpolatedMessage = this.interpolate(messageTemplate, localContext.getCurrentValidatedValue(), descriptor, constraintViolationCreationContext.getPath(), constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables());
        PathImpl path = PathImpl.createCopy(constraintViolationCreationContext.getPath());
        Object dynamicPayload = constraintViolationCreationContext.getDynamicPayload();
        switch (this.validationOperation) {
            case PARAMETER_VALIDATION: {
                return ConstraintViolationImpl.forParameterValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, this.getRootBeanClass(), this.getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), this.executableParameters, dynamicPayload);
            }
            case RETURN_VALUE_VALIDATION: {
                return ConstraintViolationImpl.forReturnValueValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, this.getRootBeanClass(), this.getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), this.executableReturnValue, dynamicPayload);
            }
        }
        return ConstraintViolationImpl.forBeanValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, this.getRootBeanClass(), this.getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), dynamicPayload);
    }

    public boolean hasMetaConstraintBeenProcessed(Object bean, Path path, MetaConstraint<?> metaConstraint) {
        if (metaConstraint.isDefinedForOneGroupOnly()) {
            return false;
        }
        return this.processedPathUnits.contains(new BeanPathMetaConstraintProcessedUnit(bean, path, metaConstraint));
    }

    public void markConstraintProcessed(Object bean, Path path, MetaConstraint<?> metaConstraint) {
        if (metaConstraint.isDefinedForOneGroupOnly()) {
            return;
        }
        this.processedPathUnits.add(new BeanPathMetaConstraintProcessedUnit(bean, path, metaConstraint));
    }

    public String getValidatedProperty() {
        return this.validatedProperty;
    }

    public void setValidatedProperty(String validatedProperty) {
        this.validatedProperty = validatedProperty;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationContext");
        sb.append("{rootBean=").append(this.rootBean);
        sb.append('}');
        return sb.toString();
    }

    private static boolean buildDisableAlreadyValidatedBeanTracking(ValidationOperation validationOperation, BeanMetaData<?> rootBeanMetaData, Optional<ExecutableMetaData> executableMetaData) {
        Validatable validatable;
        switch (validationOperation) {
            case BEAN_VALIDATION: 
            case PROPERTY_VALIDATION: 
            case VALUE_VALIDATION: {
                validatable = rootBeanMetaData;
                break;
            }
            case PARAMETER_VALIDATION: {
                if (!executableMetaData.isPresent()) {
                    return false;
                }
                validatable = executableMetaData.get().getValidatableParametersMetaData();
                break;
            }
            case RETURN_VALUE_VALIDATION: {
                if (!executableMetaData.isPresent()) {
                    return false;
                }
                validatable = executableMetaData.get().getReturnValueMetaData();
                break;
            }
            default: {
                return false;
            }
        }
        return !validatable.hasCascadables();
    }

    private String interpolate(String messageTemplate, Object validatedValue, ConstraintDescriptor<?> descriptor, Path path, Map<String, Object> messageParameters, Map<String, Object> expressionVariables) {
        MessageInterpolatorContext context = new MessageInterpolatorContext(descriptor, validatedValue, this.getRootBeanClass(), path, messageParameters, expressionVariables);
        try {
            return this.validatorScopedContext.getMessageInterpolator().interpolate(messageTemplate, (MessageInterpolator.Context)context);
        }
        catch (ValidationException ve) {
            throw ve;
        }
        catch (Exception e) {
            throw LOG.getExceptionOccurredDuringMessageInterpolationException(e);
        }
    }

    private boolean isAlreadyValidatedForPath(Object value, PathImpl path) {
        Set<PathImpl> pathSet = this.processedPathsPerBean.get(value);
        if (pathSet == null) {
            return false;
        }
        for (PathImpl p : pathSet) {
            if (!path.isRootPath() && !p.isRootPath() && !this.isSubPathOf(path, p) && !this.isSubPathOf(p, path)) continue;
            return true;
        }
        return false;
    }

    private boolean isSubPathOf(Path p1, Path p2) {
        Iterator p1Iter = p1.iterator();
        Iterator p2Iter = p2.iterator();
        while (p1Iter.hasNext()) {
            Path.Node p1Node = (Path.Node)p1Iter.next();
            if (!p2Iter.hasNext()) {
                return false;
            }
            Path.Node p2Node = (Path.Node)p2Iter.next();
            if (p1Node.equals(p2Node)) continue;
            return false;
        }
        return true;
    }

    private boolean isAlreadyValidatedForCurrentGroup(Object value, Class<?> group) {
        return this.processedGroupUnits.contains(new BeanGroupProcessedUnit(value, group));
    }

    private void markCurrentBeanAsProcessedForCurrentPath(Object bean, PathImpl path) {
        this.processedPathsPerBean.computeIfAbsent(bean, b -> new HashSet()).add(PathImpl.createCopy(path));
    }

    private void markCurrentBeanAsProcessedForCurrentGroup(Object bean, Class<?> group) {
        this.processedGroupUnits.add(new BeanGroupProcessedUnit(bean, group));
    }

    private static enum ValidationOperation {
        BEAN_VALIDATION,
        PROPERTY_VALIDATION,
        VALUE_VALIDATION,
        PARAMETER_VALIDATION,
        RETURN_VALUE_VALIDATION;

    }

    static class ValidatorScopedContext {
        private final MessageInterpolator messageInterpolator;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ClockProvider clockProvider;
        private final Duration temporalValidationTolerance;
        private final ScriptEvaluatorFactory scriptEvaluatorFactory;
        private final boolean failFast;
        private final boolean traversableResolverResultCacheEnabled;
        private final Object constraintValidatorPayload;

        ValidatorScopedContext(ValidatorFactoryImpl.ValidatorFactoryScopedContext validatorFactoryScopedContext) {
            this.messageInterpolator = validatorFactoryScopedContext.getMessageInterpolator();
            this.parameterNameProvider = validatorFactoryScopedContext.getParameterNameProvider();
            this.clockProvider = validatorFactoryScopedContext.getClockProvider();
            this.temporalValidationTolerance = validatorFactoryScopedContext.getTemporalValidationTolerance();
            this.scriptEvaluatorFactory = validatorFactoryScopedContext.getScriptEvaluatorFactory();
            this.failFast = validatorFactoryScopedContext.isFailFast();
            this.traversableResolverResultCacheEnabled = validatorFactoryScopedContext.isTraversableResolverResultCacheEnabled();
            this.constraintValidatorPayload = validatorFactoryScopedContext.getConstraintValidatorPayload();
        }

        public MessageInterpolator getMessageInterpolator() {
            return this.messageInterpolator;
        }

        public ExecutableParameterNameProvider getParameterNameProvider() {
            return this.parameterNameProvider;
        }

        public ClockProvider getClockProvider() {
            return this.clockProvider;
        }

        public Duration getTemporalValidationTolerance() {
            return this.temporalValidationTolerance;
        }

        public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
            return this.scriptEvaluatorFactory;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public boolean isTraversableResolverResultCacheEnabled() {
            return this.traversableResolverResultCacheEnabled;
        }

        public Object getConstraintValidatorPayload() {
            return this.constraintValidatorPayload;
        }
    }

    private static final class BeanPathMetaConstraintProcessedUnit {
        private Object bean;
        private Path path;
        private MetaConstraint<?> metaConstraint;
        private int hashCode;

        private BeanPathMetaConstraintProcessedUnit(Object bean, Path path, MetaConstraint<?> metaConstraint) {
            this.bean = bean;
            this.path = path;
            this.metaConstraint = metaConstraint;
            this.hashCode = this.createHashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            BeanPathMetaConstraintProcessedUnit that = (BeanPathMetaConstraintProcessedUnit)o;
            if (this.bean != that.bean) {
                return false;
            }
            if (this.metaConstraint != that.metaConstraint) {
                return false;
            }
            return this.path.equals(that.path);
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = System.identityHashCode(this.bean);
            result = 31 * result + this.path.hashCode();
            result = 31 * result + System.identityHashCode(this.metaConstraint);
            return result;
        }
    }

    private static final class BeanGroupProcessedUnit {
        private Object bean;
        private Class<?> group;
        private int hashCode;

        private BeanGroupProcessedUnit(Object bean, Class<?> group) {
            this.bean = bean;
            this.group = group;
            this.hashCode = this.createHashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            BeanGroupProcessedUnit that = (BeanGroupProcessedUnit)o;
            if (this.bean != that.bean) {
                return false;
            }
            return this.group.equals(that.group);
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = System.identityHashCode(this.bean);
            result = 31 * result + this.group.hashCode();
            return result;
        }
    }

    public static class ValidationContextBuilder {
        private final ConstraintValidatorManager constraintValidatorManager;
        private final ConstraintValidatorFactory constraintValidatorFactory;
        private final TraversableResolver traversableResolver;
        private final HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
        private final ValidatorScopedContext validatorScopedContext;

        private ValidationContextBuilder(ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
            this.constraintValidatorManager = constraintValidatorManager;
            this.constraintValidatorFactory = constraintValidatorFactory;
            this.traversableResolver = traversableResolver;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
            this.validatorScopedContext = validatorScopedContext;
        }

        public <T> ValidationContext<T> forValidate(BeanMetaData<T> rootBeanMetaData, T rootBean) {
            return new ValidationContext(ValidationOperation.BEAN_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, rootBeanMetaData.getBeanClass(), rootBeanMetaData, null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateProperty(BeanMetaData<T> rootBeanMetaData, T rootBean) {
            return new ValidationContext(ValidationOperation.PROPERTY_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, rootBeanMetaData.getBeanClass(), rootBeanMetaData, null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateValue(BeanMetaData<T> rootBeanMetaData) {
            return new ValidationContext(ValidationOperation.VALUE_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, null, rootBeanMetaData.getBeanClass(), rootBeanMetaData, null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateParameters(ExecutableParameterNameProvider parameterNameProvider, BeanMetaData<T> rootBeanMetaData, T rootBean, Executable executable, Object[] executableParameters) {
            return new ValidationContext(ValidationOperation.PARAMETER_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, rootBeanMetaData.getBeanClass(), rootBeanMetaData, executable, executableParameters, null, rootBeanMetaData.getMetaDataFor(executable));
        }

        public <T> ValidationContext<T> forValidateReturnValue(BeanMetaData<T> rootBeanMetaData, T rootBean, Executable executable, Object executableReturnValue) {
            return new ValidationContext(ValidationOperation.RETURN_VALUE_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, rootBeanMetaData.getBeanClass(), rootBeanMetaData, executable, null, executableReturnValue, rootBeanMetaData.getMetaDataFor(executable));
        }
    }
}

