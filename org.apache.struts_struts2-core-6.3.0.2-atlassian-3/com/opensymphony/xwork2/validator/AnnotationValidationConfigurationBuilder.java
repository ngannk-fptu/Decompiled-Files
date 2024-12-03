/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.validator.ValidatorConfig;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CreditCardValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.DoubleRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.LongRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ShortRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class AnnotationValidationConfigurationBuilder {
    private ValidatorFactory validatorFactory;

    public AnnotationValidationConfigurationBuilder(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    private List<ValidatorConfig> processAnnotations(Object o) {
        ArrayList<ValidatorConfig> result = new ArrayList<ValidatorConfig>();
        String fieldName = null;
        String methodName = null;
        Annotation[] annotations = null;
        if (o instanceof Class) {
            Class clazz = (Class)o;
            annotations = clazz.getAnnotations();
        }
        if (o instanceof Method) {
            Method method = (Method)o;
            fieldName = AnnotationUtils.resolvePropertyName(method);
            methodName = method.getName();
            annotations = method.getAnnotations();
        }
        if (annotations != null) {
            for (Annotation a : annotations) {
                ValidatorConfig temp;
                Annotation v;
                if (a instanceof Validations) {
                    this.processValidationAnnotation(a, fieldName, methodName, result);
                    continue;
                }
                if (a instanceof ExpressionValidator) {
                    v = (ExpressionValidator)a;
                    temp = this.processExpressionValidatorAnnotation((ExpressionValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof CustomValidator) {
                    v = (CustomValidator)a;
                    temp = this.processCustomValidatorAnnotation((CustomValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof ConversionErrorFieldValidator) {
                    v = (ConversionErrorFieldValidator)a;
                    temp = this.processConversionErrorFieldValidatorAnnotation((ConversionErrorFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof DateRangeFieldValidator) {
                    v = (DateRangeFieldValidator)a;
                    temp = this.processDateRangeFieldValidatorAnnotation((DateRangeFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof EmailValidator) {
                    v = (EmailValidator)a;
                    temp = this.processEmailValidatorAnnotation((EmailValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof CreditCardValidator) {
                    v = (CreditCardValidator)a;
                    temp = this.processCreditCardValidatorAnnotation((CreditCardValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof FieldExpressionValidator) {
                    v = (FieldExpressionValidator)a;
                    temp = this.processFieldExpressionValidatorAnnotation((FieldExpressionValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof IntRangeFieldValidator) {
                    v = (IntRangeFieldValidator)a;
                    temp = this.processIntRangeFieldValidatorAnnotation((IntRangeFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof LongRangeFieldValidator) {
                    v = (LongRangeFieldValidator)a;
                    temp = this.processLongRangeFieldValidatorAnnotation((LongRangeFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof ShortRangeFieldValidator) {
                    v = (ShortRangeFieldValidator)a;
                    temp = this.processShortRangeFieldValidatorAnnotation((ShortRangeFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof DoubleRangeFieldValidator) {
                    v = (DoubleRangeFieldValidator)a;
                    temp = this.processDoubleRangeFieldValidatorAnnotation((DoubleRangeFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof RequiredFieldValidator) {
                    v = (RequiredFieldValidator)a;
                    temp = this.processRequiredFieldValidatorAnnotation((RequiredFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof RequiredStringValidator) {
                    v = (RequiredStringValidator)a;
                    temp = this.processRequiredStringValidatorAnnotation((RequiredStringValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof StringLengthFieldValidator) {
                    v = (StringLengthFieldValidator)a;
                    temp = this.processStringLengthFieldValidatorAnnotation((StringLengthFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof UrlValidator) {
                    v = (UrlValidator)a;
                    temp = this.processUrlValidatorAnnotation((UrlValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof ConditionalVisitorFieldValidator) {
                    v = (ConditionalVisitorFieldValidator)a;
                    temp = this.processConditionalVisitorFieldValidatorAnnotation((ConditionalVisitorFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (a instanceof VisitorFieldValidator) {
                    v = (VisitorFieldValidator)a;
                    temp = this.processVisitorFieldValidatorAnnotation((VisitorFieldValidator)v, fieldName, methodName);
                    if (temp == null) continue;
                    result.add(temp);
                    continue;
                }
                if (!(a instanceof RegexFieldValidator) || (temp = this.processRegexFieldValidatorAnnotation((RegexFieldValidator)(v = (RegexFieldValidator)a), fieldName, methodName)) == null) continue;
                result.add(temp);
            }
        }
        return result;
    }

    private void processValidationAnnotation(Annotation a, String fieldName, String methodName, List<ValidatorConfig> result) {
        VisitorFieldValidator[] vfv;
        ConditionalVisitorFieldValidator[] cvfv;
        UrlValidator[] uv;
        StringLengthFieldValidator[] slfv;
        RequiredStringValidator[] rsv;
        RequiredFieldValidator[] rv;
        RegexFieldValidator[] rfv;
        IntRangeFieldValidator[] irfv;
        FieldExpressionValidator[] fev;
        CreditCardValidator[] ccv;
        EmailValidator[] emv;
        DateRangeFieldValidator[] drfv;
        ConversionErrorFieldValidator[] cef;
        ExpressionValidator[] ev;
        Validations validations = (Validations)a;
        CustomValidator[] cv = validations.customValidators();
        if (cv != null) {
            for (CustomValidator v : cv) {
                ValidatorConfig temp = this.processCustomValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((ev = validations.expressions()) != null) {
            for (ExpressionValidator v : ev) {
                ValidatorConfig temp = this.processExpressionValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((cef = validations.conversionErrorFields()) != null) {
            for (ConversionErrorFieldValidator v : cef) {
                ValidatorConfig temp = this.processConversionErrorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((drfv = validations.dateRangeFields()) != null) {
            for (DateRangeFieldValidator v : drfv) {
                ValidatorConfig temp = this.processDateRangeFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((emv = validations.emails()) != null) {
            for (EmailValidator v : emv) {
                ValidatorConfig temp = this.processEmailValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((ccv = validations.creditCards()) != null) {
            for (CreditCardValidator v : ccv) {
                ValidatorConfig temp = this.processCreditCardValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((fev = validations.fieldExpressions()) != null) {
            for (FieldExpressionValidator v : fev) {
                ValidatorConfig temp = this.processFieldExpressionValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((irfv = validations.intRangeFields()) != null) {
            for (IntRangeFieldValidator v : irfv) {
                ValidatorConfig temp = this.processIntRangeFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        LongRangeFieldValidator[] lrfv = validations.longRangeFields();
        if (irfv != null) {
            for (LongRangeFieldValidator v : lrfv) {
                ValidatorConfig temp = this.processLongRangeFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((rfv = validations.regexFields()) != null) {
            for (RegexFieldValidator v : rfv) {
                ValidatorConfig temp = this.processRegexFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((rv = validations.requiredFields()) != null) {
            for (RequiredFieldValidator v : rv) {
                ValidatorConfig temp = this.processRequiredFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((rsv = validations.requiredStrings()) != null) {
            for (RequiredStringValidator v : rsv) {
                ValidatorConfig temp = this.processRequiredStringValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((slfv = validations.stringLengthFields()) != null) {
            for (StringLengthFieldValidator v : slfv) {
                ValidatorConfig temp = this.processStringLengthFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((uv = validations.urls()) != null) {
            for (UrlValidator v : uv) {
                ValidatorConfig temp = this.processUrlValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((cvfv = validations.conditionalVisitorFields()) != null) {
            for (ConditionalVisitorFieldValidator v : cvfv) {
                ValidatorConfig temp = this.processConditionalVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
        if ((vfv = validations.visitorFields()) != null) {
            for (VisitorFieldValidator v : vfv) {
                ValidatorConfig temp = this.processVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp == null) continue;
                result.add(temp);
            }
        }
    }

    private ValidatorConfig processExpressionValidatorAnnotation(ExpressionValidator v, String fieldName, String methodName) {
        String validatorType = "expression";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        }
        params.put("expression", v.expression());
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processCustomValidatorAnnotation(CustomValidator v, String fieldName, String methodName) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        String validatorType = v.type();
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidationParameter[] recursedAnnotations = v.parameters();
        if (recursedAnnotations != null) {
            for (ValidationParameter a2 : recursedAnnotations) {
                if (!(a2 instanceof ValidationParameter)) continue;
                ValidationParameter parameter = a2;
                String parameterName = parameter.name();
                String parameterValue = parameter.value();
                params.put(parameterName, parameterValue);
            }
        }
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processRegexFieldValidatorAnnotation(RegexFieldValidator v, String fieldName, String methodName) {
        String validatorType = "regex";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        params.put("regex", v.regex());
        params.put("regexExpression", v.regexExpression());
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).addParam("trim", v.trim()).addParam("trimExpression", v.trimExpression()).addParam("caseSensitive", v.caseSensitive()).addParam("caseSensitiveExpression", v.caseSensitiveExpression()).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processConditionalVisitorFieldValidatorAnnotation(ConditionalVisitorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "conditionalvisitor";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        params.put("expression", v.expression());
        params.put("context", v.context());
        params.put("appendPrefix", String.valueOf(v.appendPrefix()));
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processVisitorFieldValidatorAnnotation(VisitorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "visitor";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        params.put("context", v.context());
        params.put("appendPrefix", String.valueOf(v.appendPrefix()));
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processUrlValidatorAnnotation(UrlValidator v, String fieldName, String methodName) {
        String validatorType = "url";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.urlRegex())) {
            params.put("urlRegex", v.urlRegex());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.urlRegexExpression())) {
            params.put("urlRegexExpression", v.urlRegexExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processStringLengthFieldValidatorAnnotation(StringLengthFieldValidator v, String fieldName, String methodName) {
        String validatorType = "stringlength";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxLength())) {
            params.put("maxLength", v.maxLength());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minLength())) {
            params.put("minLength", v.minLength());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxLengthExpression())) {
            params.put("maxLengthExpression", v.maxLengthExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minLengthExpression())) {
            params.put("minLengthExpression", v.minLengthExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.trimExpression())) {
            params.put("trimExpression", v.trimExpression());
        } else {
            params.put("trim", String.valueOf(v.trim()));
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private Date parseDateString(String value, String format) {
        SimpleDateFormat[] dfs;
        SimpleDateFormat[] simpleDateFormatArray;
        SimpleDateFormat d0 = null;
        if (StringUtils.isNotEmpty((CharSequence)format)) {
            d0 = new SimpleDateFormat(format);
        }
        SimpleDateFormat d1 = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 1, Locale.getDefault());
        SimpleDateFormat d2 = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 2, Locale.getDefault());
        SimpleDateFormat d3 = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 3, Locale.getDefault());
        if (d0 != null) {
            SimpleDateFormat[] simpleDateFormatArray2 = new SimpleDateFormat[4];
            simpleDateFormatArray2[0] = d0;
            simpleDateFormatArray2[1] = d1;
            simpleDateFormatArray2[2] = d2;
            simpleDateFormatArray = simpleDateFormatArray2;
            simpleDateFormatArray2[3] = d3;
        } else {
            SimpleDateFormat[] simpleDateFormatArray3 = new SimpleDateFormat[3];
            simpleDateFormatArray3[0] = d1;
            simpleDateFormatArray3[1] = d2;
            simpleDateFormatArray = simpleDateFormatArray3;
            simpleDateFormatArray3[2] = d3;
        }
        for (SimpleDateFormat df : dfs = simpleDateFormatArray) {
            try {
                Date check = df.parse(value);
                if (check == null) continue;
                return check;
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        return null;
    }

    private ValidatorConfig processRequiredStringValidatorAnnotation(RequiredStringValidator v, String fieldName, String methodName) {
        String validatorType = "requiredstring";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        params.put("trim", String.valueOf(v.trim()));
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageParams(v.messageParams()).messageKey(v.key()).build();
    }

    private ValidatorConfig processRequiredFieldValidatorAnnotation(RequiredFieldValidator v, String fieldName, String methodName) {
        String validatorType = "required";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processIntRangeFieldValidatorAnnotation(IntRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "int";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        if (v.min() != null && v.min().length() > 0) {
            params.put("min", v.min());
        }
        if (v.max() != null && v.max().length() > 0) {
            params.put("max", v.max());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processLongRangeFieldValidatorAnnotation(LongRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "long";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        if (v.min() != null && v.min().length() > 0) {
            params.put("min", v.min());
        }
        if (v.max() != null && v.max().length() > 0) {
            params.put("max", v.max());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processShortRangeFieldValidatorAnnotation(ShortRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "short";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.min())) {
            params.put("min", v.min());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.max())) {
            params.put("max", v.max());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processDoubleRangeFieldValidatorAnnotation(DoubleRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "double";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }
        if (v.minInclusive() != null && v.minInclusive().length() > 0) {
            params.put("minInclusive", v.minInclusive());
        }
        if (v.maxInclusive() != null && v.maxInclusive().length() > 0) {
            params.put("maxInclusive", v.maxInclusive());
        }
        if (v.minExclusive() != null && v.minExclusive().length() > 0) {
            params.put("minExclusive", v.minExclusive());
        }
        if (v.maxExclusive() != null && v.maxExclusive().length() > 0) {
            params.put("maxExclusive", v.maxExclusive());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minInclusiveExpression())) {
            params.put("minInclusiveExpression", v.minInclusiveExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxInclusiveExpression())) {
            params.put("maxInclusiveExpression", v.maxInclusiveExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minExclusiveExpression())) {
            params.put("minExclusiveExpression", v.minExclusiveExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxExclusiveExpression())) {
            params.put("maxExclusiveExpression", v.maxExclusiveExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processFieldExpressionValidatorAnnotation(FieldExpressionValidator v, String fieldName, String methodName) {
        String validatorType = "fieldexpression";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        params.put("expression", v.expression());
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processEmailValidatorAnnotation(EmailValidator v, String fieldName, String methodName) {
        String validatorType = "email";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processCreditCardValidatorAnnotation(CreditCardValidator v, String fieldName, String methodName) {
        String validatorType = "creditcard";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processDateRangeFieldValidatorAnnotation(DateRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "date";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }
        if (v.min() != null && v.min().length() > 0) {
            Date minDate = this.parseDateString(v.min(), v.dateFormat());
            params.put("min", minDate == null ? v.min() : minDate);
        }
        if (v.max() != null && v.max().length() > 0) {
            Date maxDate = this.parseDateString(v.max(), v.dateFormat());
            params.put("max", maxDate == null ? v.max() : maxDate);
        }
        if (StringUtils.isNotEmpty((CharSequence)v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }
        if (StringUtils.isNotEmpty((CharSequence)v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    private ValidatorConfig processConversionErrorFieldValidatorAnnotation(ConversionErrorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "conversion";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty((CharSequence)v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }
        this.validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType).addParams(params).addParam("methodName", methodName).addParam("repopulateField", v.repopulateField()).shortCircuit(v.shortCircuit()).defaultMessage(v.message()).messageKey(v.key()).messageParams(v.messageParams()).build();
    }

    public List<ValidatorConfig> buildAnnotationClassValidatorConfigs(Class aClass) {
        Method[] methods;
        ArrayList<ValidatorConfig> result = new ArrayList<ValidatorConfig>();
        List<ValidatorConfig> temp = this.processAnnotations(aClass);
        if (temp != null) {
            result.addAll(temp);
        }
        if ((methods = aClass.getDeclaredMethods()) != null) {
            for (Method method : methods) {
                temp = this.processAnnotations(method);
                if (temp == null) continue;
                result.addAll(temp);
            }
        }
        return result;
    }
}

