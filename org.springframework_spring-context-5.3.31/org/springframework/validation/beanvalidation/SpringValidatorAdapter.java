/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintViolation
 *  javax.validation.ElementKind
 *  javax.validation.Path
 *  javax.validation.Path$Node
 *  javax.validation.ValidationException
 *  javax.validation.Validator
 *  javax.validation.executable.ExecutableValidator
 *  javax.validation.metadata.BeanDescriptor
 *  javax.validation.metadata.ConstraintDescriptor
 *  org.springframework.beans.NotReadablePropertyException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.validation.beanvalidation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

public class SpringValidatorAdapter
implements SmartValidator,
Validator {
    private static final Set<String> internalAnnotationAttributes = new HashSet<String>(4);
    @Nullable
    private Validator targetValidator;

    public SpringValidatorAdapter(Validator targetValidator) {
        Assert.notNull((Object)targetValidator, (String)"Target Validator must not be null");
        this.targetValidator = targetValidator;
    }

    SpringValidatorAdapter() {
    }

    void setTargetValidator(Validator targetValidator) {
        this.targetValidator = targetValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.targetValidator != null;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (this.targetValidator != null) {
            this.processConstraintViolations(this.targetValidator.validate(target, new Class[0]), errors);
        }
    }

    @Override
    public void validate(Object target, Errors errors, Object ... validationHints) {
        if (this.targetValidator != null) {
            this.processConstraintViolations(this.targetValidator.validate(target, (Class[])this.asValidationGroups(validationHints)), errors);
        }
    }

    @Override
    public void validateValue(Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object ... validationHints) {
        if (this.targetValidator != null) {
            this.processConstraintViolations(this.targetValidator.validateValue(targetType, fieldName, value, (Class[])this.asValidationGroups(validationHints)), errors);
        }
    }

    private Class<?>[] asValidationGroups(Object ... validationHints) {
        LinkedHashSet<Class> groups = new LinkedHashSet<Class>(4);
        for (Object hint : validationHints) {
            if (!(hint instanceof Class)) continue;
            groups.add((Class)hint);
        }
        return ClassUtils.toClassArray(groups);
    }

    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = this.determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError != null && fieldError.isBindingFailure()) continue;
            try {
                ConstraintDescriptor cd = violation.getConstraintDescriptor();
                String errorCode = this.determineErrorCode(cd);
                Object[] errorArgs = this.getArgumentsForConstraint(errors.getObjectName(), field, cd);
                if (errors instanceof BindingResult) {
                    BindingResult bindingResult = (BindingResult)errors;
                    String nestedField = bindingResult.getNestedPath() + field;
                    if (nestedField.isEmpty()) {
                        String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                        ViolationObjectError error = new ViolationObjectError(errors.getObjectName(), errorCodes, errorArgs, violation, this);
                        bindingResult.addError(error);
                        continue;
                    }
                    Object rejectedValue = this.getRejectedValue(field, violation, bindingResult);
                    String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
                    ViolationFieldError error = new ViolationFieldError(errors.getObjectName(), nestedField, rejectedValue, errorCodes, errorArgs, violation, this);
                    bindingResult.addError(error);
                    continue;
                }
                errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
            }
            catch (NotReadablePropertyException ex) {
                throw new IllegalStateException("JSR-303 validated property '" + field + "' does not have a corresponding accessor for Spring data binding - check your DataBinder's configuration (bean property versus direct field access)", ex);
            }
        }
    }

    protected String determineField(ConstraintViolation<Object> violation) {
        Path path = violation.getPropertyPath();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Path.Node node : path) {
            String name;
            if (node.isInIterable()) {
                sb.append('[');
                Object index = node.getIndex();
                if (index == null) {
                    index = node.getKey();
                }
                if (index != null) {
                    sb.append(index);
                }
                sb.append(']');
            }
            if ((name = node.getName()) == null || node.getKind() != ElementKind.PROPERTY || name.startsWith("<")) continue;
            if (!first) {
                sb.append('.');
            }
            first = false;
            sb.append(name);
        }
        return sb.toString();
    }

    protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
        return descriptor.getAnnotation().annotationType().getSimpleName();
    }

    protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
        ArrayList<MessageSourceResolvable> arguments = new ArrayList<MessageSourceResolvable>();
        arguments.add(this.getResolvableField(objectName, field));
        TreeMap attributesToExpose = new TreeMap();
        descriptor.getAttributes().forEach((attributeName, attributeValue) -> {
            if (!internalAnnotationAttributes.contains(attributeName)) {
                if (attributeValue instanceof String) {
                    attributeValue = new ResolvableAttribute(attributeValue.toString());
                }
                attributesToExpose.put(attributeName, attributeValue);
            }
        });
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray();
    }

    protected MessageSourceResolvable getResolvableField(String objectName, String field) {
        String[] codes = new String[]{objectName + "." + field, field};
        return new DefaultMessageSourceResolvable(codes, field);
    }

    @Nullable
    protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        if (!field.isEmpty() && !field.contains("[]") && (invalidValue == violation.getLeafBean() || field.contains("[") || field.contains("."))) {
            invalidValue = bindingResult.getRawFieldValue(field);
        }
        return invalidValue;
    }

    protected boolean requiresMessageFormat(ConstraintViolation<?> violation) {
        return SpringValidatorAdapter.containsSpringStylePlaceholder(violation.getMessage());
    }

    private static boolean containsSpringStylePlaceholder(@Nullable String message) {
        return message != null && message.contains("{0}");
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?> ... groups) {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        return this.targetValidator.validate(object, (Class[])groups);
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?> ... groups) {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        return this.targetValidator.validateProperty(object, propertyName, (Class[])groups);
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?> ... groups) {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        return this.targetValidator.validateValue(beanType, propertyName, value, (Class[])groups);
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        return this.targetValidator.getConstraintsForClass(clazz);
    }

    public <T> T unwrap(@Nullable Class<T> type) {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        try {
            return (T)(type != null ? this.targetValidator.unwrap(type) : this.targetValidator);
        }
        catch (ValidationException ex) {
            if (Validator.class == type) {
                return (T)this.targetValidator;
            }
            throw ex;
        }
    }

    public ExecutableValidator forExecutables() {
        Assert.state((this.targetValidator != null ? 1 : 0) != 0, (String)"No target Validator set");
        return this.targetValidator.forExecutables();
    }

    static {
        internalAnnotationAttributes.add("message");
        internalAnnotationAttributes.add("groups");
        internalAnnotationAttributes.add("payload");
    }

    private static class ViolationFieldError
    extends FieldError
    implements Serializable {
        @Nullable
        private transient SpringValidatorAdapter adapter;
        @Nullable
        private transient ConstraintViolation<?> violation;

        public ViolationFieldError(String objectName, String field, @Nullable Object rejectedValue, String[] codes, Object[] arguments, ConstraintViolation<?> violation, SpringValidatorAdapter adapter) {
            super(objectName, field, rejectedValue, false, codes, arguments, violation.getMessage());
            this.adapter = adapter;
            this.violation = violation;
            this.wrap(violation);
        }

        @Override
        public boolean shouldRenderDefaultMessage() {
            return this.adapter != null && this.violation != null ? this.adapter.requiresMessageFormat(this.violation) : SpringValidatorAdapter.containsSpringStylePlaceholder(this.getDefaultMessage());
        }
    }

    private static class ViolationObjectError
    extends ObjectError
    implements Serializable {
        @Nullable
        private transient SpringValidatorAdapter adapter;
        @Nullable
        private transient ConstraintViolation<?> violation;

        public ViolationObjectError(String objectName, String[] codes, Object[] arguments, ConstraintViolation<?> violation, SpringValidatorAdapter adapter) {
            super(objectName, codes, arguments, violation.getMessage());
            this.adapter = adapter;
            this.violation = violation;
            this.wrap(violation);
        }

        @Override
        public boolean shouldRenderDefaultMessage() {
            return this.adapter != null && this.violation != null ? this.adapter.requiresMessageFormat(this.violation) : SpringValidatorAdapter.containsSpringStylePlaceholder(this.getDefaultMessage());
        }
    }

    private static class ResolvableAttribute
    implements MessageSourceResolvable,
    Serializable {
        private final String resolvableString;

        public ResolvableAttribute(String resolvableString) {
            this.resolvableString = resolvableString;
        }

        @Override
        public String[] getCodes() {
            return new String[]{this.resolvableString};
        }

        @Override
        @Nullable
        public Object[] getArguments() {
            return null;
        }

        @Override
        public String getDefaultMessage() {
            return this.resolvableString;
        }

        public String toString() {
            return this.resolvableString;
        }
    }
}

