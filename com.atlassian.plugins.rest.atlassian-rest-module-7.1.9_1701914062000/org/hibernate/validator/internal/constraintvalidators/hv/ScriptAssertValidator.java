/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.constraintvalidators.hv.AbstractScriptAssertValidator;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;

public class ScriptAssertValidator
extends AbstractScriptAssertValidator<ScriptAssert, Object> {
    private String alias;
    private String reportOn;
    private String message;

    @Override
    public void initialize(ConstraintDescriptor<ScriptAssert> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        ScriptAssert constraintAnnotation = (ScriptAssert)constraintDescriptor.getAnnotation();
        this.validateParameters(constraintAnnotation);
        this.initialize(constraintAnnotation.lang(), constraintAnnotation.script(), initializationContext);
        this.alias = constraintAnnotation.alias();
        this.reportOn = constraintAnnotation.reportOn();
        this.message = constraintAnnotation.message();
    }

    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        boolean validationResult;
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext)constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("script", this.escapedScript);
        }
        if (!(validationResult = this.scriptAssertContext.evaluateScriptAssertExpression(value, this.alias)) && !this.reportOn.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(this.message).addPropertyNode(this.reportOn).addConstraintViolation();
        }
        return validationResult;
    }

    private void validateParameters(ScriptAssert constraintAnnotation) {
        Contracts.assertNotEmpty(constraintAnnotation.script(), Messages.MESSAGES.parameterMustNotBeEmpty("script"));
        Contracts.assertNotEmpty(constraintAnnotation.lang(), Messages.MESSAGES.parameterMustNotBeEmpty("lang"));
        Contracts.assertNotEmpty(constraintAnnotation.alias(), Messages.MESSAGES.parameterMustNotBeEmpty("alias"));
    }
}

