/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResults;

public class Validator
implements Serializable {
    private static final long serialVersionUID = -7119418755208731611L;
    public static final String BEAN_PARAM = "java.lang.Object";
    public static final String VALIDATOR_ACTION_PARAM = "org.apache.commons.validator.ValidatorAction";
    public static final String VALIDATOR_RESULTS_PARAM = "org.apache.commons.validator.ValidatorResults";
    public static final String FORM_PARAM = "org.apache.commons.validator.Form";
    public static final String FIELD_PARAM = "org.apache.commons.validator.Field";
    public static final String VALIDATOR_PARAM = "org.apache.commons.validator.Validator";
    public static final String LOCALE_PARAM = "java.util.Locale";
    protected ValidatorResources resources = null;
    protected String formName = null;
    protected String fieldName = null;
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    protected int page = 0;
    protected transient ClassLoader classLoader = null;
    protected boolean useContextClassLoader = false;
    protected boolean onlyReturnErrors = false;

    public Validator(ValidatorResources resources) {
        this(resources, null);
    }

    public Validator(ValidatorResources resources, String formName) {
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }
        this.resources = resources;
        this.formName = formName;
    }

    public Validator(ValidatorResources resources, String formName, String fieldName) {
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }
        this.resources = resources;
        this.formName = formName;
        this.fieldName = fieldName;
    }

    public void setParameter(String parameterClassName, Object parameterValue) {
        this.parameters.put(parameterClassName, parameterValue);
    }

    public Object getParameterValue(String parameterClassName) {
        return this.parameters.get(parameterClassName);
    }

    public String getFormName() {
        return this.formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void clear() {
        this.formName = null;
        this.fieldName = null;
        this.parameters = new HashMap<String, Object>();
        this.page = 0;
    }

    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    public void setUseContextClassLoader(boolean use) {
        this.useContextClassLoader = use;
    }

    public ClassLoader getClassLoader() {
        ClassLoader contextLoader;
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.useContextClassLoader && (contextLoader = Thread.currentThread().getContextClassLoader()) != null) {
            return contextLoader;
        }
        return this.getClass().getClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ValidatorResults validate() throws ValidatorException {
        Locale locale = (Locale)this.getParameterValue(LOCALE_PARAM);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.setParameter(VALIDATOR_PARAM, this);
        Form form = this.resources.getForm(locale, this.formName);
        if (form != null) {
            this.setParameter(FORM_PARAM, form);
            return form.validate(this.parameters, this.resources.getValidatorActions(), this.page, this.fieldName);
        }
        return new ValidatorResults();
    }

    public boolean getOnlyReturnErrors() {
        return this.onlyReturnErrors;
    }

    public void setOnlyReturnErrors(boolean onlyReturnErrors) {
        this.onlyReturnErrors = onlyReturnErrors;
    }
}

