/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.annotations;

import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CreditCardValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.LongRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Validations {
    public CustomValidator[] customValidators() default {};

    public ConversionErrorFieldValidator[] conversionErrorFields() default {};

    public DateRangeFieldValidator[] dateRangeFields() default {};

    public EmailValidator[] emails() default {};

    public CreditCardValidator[] creditCards() default {};

    public FieldExpressionValidator[] fieldExpressions() default {};

    public IntRangeFieldValidator[] intRangeFields() default {};

    public LongRangeFieldValidator[] longRangeFields() default {};

    public RequiredFieldValidator[] requiredFields() default {};

    public RequiredStringValidator[] requiredStrings() default {};

    public StringLengthFieldValidator[] stringLengthFields() default {};

    public UrlValidator[] urls() default {};

    public ConditionalVisitorFieldValidator[] conditionalVisitorFields() default {};

    public VisitorFieldValidator[] visitorFields() default {};

    public RegexFieldValidator[] regexFields() default {};

    public ExpressionValidator[] expressions() default {};
}

