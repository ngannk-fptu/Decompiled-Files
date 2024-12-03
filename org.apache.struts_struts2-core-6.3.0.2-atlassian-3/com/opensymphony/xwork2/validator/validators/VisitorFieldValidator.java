/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.CompositeTextProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VisitorFieldValidator
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(VisitorFieldValidator.class);
    private String context;
    private boolean appendPrefix = true;
    private ActionValidatorManager actionValidatorManager;

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }

    public void setAppendPrefix(boolean appendPrefix) {
        this.appendPrefix = appendPrefix;
    }

    public boolean isAppendPrefix() {
        return this.appendPrefix;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return this.context;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        String visitorContext;
        String fieldName = this.getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        if (value == null) {
            LOG.warn("The visited object is null, VisitorValidator will not be able to handle validation properly. Please make sure the visited object is not null for VisitorValidator to function properly");
            return;
        }
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(object);
        String string = visitorContext = this.context == null ? ActionContext.getContext().getActionName() : this.context;
        if (value instanceof Collection) {
            Collection coll = (Collection)value;
            Object[] array = coll.toArray();
            this.validateArrayElements(array, fieldName, visitorContext);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[])value;
            this.validateArrayElements(array, fieldName, visitorContext);
        } else {
            this.validateObject(fieldName, value, visitorContext);
        }
        stack.pop();
    }

    private void validateArrayElements(Object[] array, String fieldName, String visitorContext) throws ValidationException {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            Object o = array[i];
            if (o == null) continue;
            this.validateObject(fieldName + "[" + i + "]", o, visitorContext);
        }
    }

    private void validateObject(String fieldName, Object o, String visitorContext) throws ValidationException {
        DelegatingValidatorContext validatorContext;
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(o);
        if (this.appendPrefix) {
            ValidatorContext parent = this.getValidatorContext();
            validatorContext = new AppendingValidatorContext(parent, this.createTextProvider(o, parent), fieldName, this.getMessage(o));
        } else {
            ValidatorContext parent = this.getValidatorContext();
            CompositeTextProvider textProvider = this.createTextProvider(o, parent);
            validatorContext = new DelegatingValidatorContext(parent, textProvider, parent);
        }
        this.actionValidatorManager.validate(o, visitorContext, validatorContext);
        stack.pop();
    }

    private CompositeTextProvider createTextProvider(Object o, ValidatorContext parent) {
        LinkedList<TextProvider> textProviders = new LinkedList<TextProvider>();
        if (o instanceof TextProvider) {
            textProviders.add((TextProvider)o);
        } else {
            textProviders.add(this.textProviderFactory.createInstance(o.getClass()));
        }
        textProviders.add(parent);
        return new CompositeTextProvider(textProviders);
    }

    public static class AppendingValidatorContext
    extends DelegatingValidatorContext {
        private String field;
        private String message;
        private ValidatorContext parent;

        public AppendingValidatorContext(ValidatorContext parent, TextProvider textProvider, String field, String message) {
            super(parent, textProvider, parent);
            this.field = field;
            this.message = message;
            this.parent = parent;
        }

        @Override
        public String getFullFieldName(String fieldName) {
            if (this.parent instanceof AppendingValidatorContext) {
                return this.parent.getFullFieldName(this.field + "." + fieldName);
            }
            return this.field + "." + fieldName;
        }

        public String getFieldNameWithField(String fieldName) {
            return this.field + "." + fieldName;
        }

        @Override
        public void addActionError(String anErrorMessage) {
            super.addFieldError(this.getFieldNameWithField(this.field), this.message + anErrorMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            super.addFieldError(this.getFieldNameWithField(fieldName), this.message + errorMessage);
        }
    }
}

