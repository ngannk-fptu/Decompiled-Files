/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ShortCircuitableValidator;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorContext;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ValidatorSupport
implements Validator,
ShortCircuitableValidator {
    private static final Logger LOG = LogManager.getLogger(ValidatorSupport.class);
    public static final String EMPTY_STRING = "";
    private ValidatorContext validatorContext;
    private boolean shortCircuit;
    private String type;
    private String[] messageParameters;
    protected String defaultMessage = "";
    protected String messageKey;
    protected ValueStack stack;
    protected TextProviderFactory textProviderFactory;

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    @Override
    public void setValueStack(ValueStack stack) {
        this.stack = stack;
    }

    @Override
    public void setDefaultMessage(String message) {
        if (StringUtils.isNotEmpty((CharSequence)message)) {
            this.defaultMessage = message;
        }
    }

    @Override
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    @Override
    public String getMessage(Object object) {
        String message;
        boolean pop = false;
        if (!this.stack.getRoot().contains(object)) {
            this.stack.push(object);
            pop = true;
        }
        this.stack.push(this);
        if (this.messageKey != null) {
            if (this.defaultMessage == null || EMPTY_STRING.equals(this.defaultMessage.trim())) {
                this.defaultMessage = this.messageKey;
            }
            if (this.validatorContext == null) {
                this.validatorContext = new DelegatingValidatorContext(object, this.textProviderFactory);
            }
            ArrayList<Object> parsedMessageParameters = null;
            if (this.messageParameters != null) {
                parsedMessageParameters = new ArrayList<Object>();
                for (String messageParameter : this.messageParameters) {
                    if (messageParameter == null) continue;
                    try {
                        Object val = this.stack.findValue(messageParameter);
                        parsedMessageParameters.add(val);
                    }
                    catch (Exception e) {
                        LOG.warn("exception while parsing message parameter [{}]", (Object)messageParameter, (Object)e);
                        parsedMessageParameters.add(messageParameter);
                    }
                }
            }
            message = this.validatorContext.getText(this.messageKey, this.defaultMessage, parsedMessageParameters);
        } else {
            message = this.defaultMessage;
        }
        if (StringUtils.isNotBlank((CharSequence)message)) {
            message = TextParseUtil.translateVariables(message, this.stack);
        }
        this.stack.pop();
        if (pop) {
            this.stack.pop();
        }
        return message;
    }

    @Override
    public void setMessageKey(String key) {
        this.messageKey = key;
    }

    @Override
    public String getMessageKey() {
        return this.messageKey;
    }

    @Override
    public String[] getMessageParameters() {
        return this.messageParameters;
    }

    @Override
    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    @Override
    public void setShortCircuit(boolean shortcircuit) {
        this.shortCircuit = shortcircuit;
    }

    @Override
    public boolean isShortCircuit() {
        return this.shortCircuit;
    }

    @Override
    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    @Override
    public ValidatorContext getValidatorContext() {
        return this.validatorContext;
    }

    @Override
    public void setValidatorType(String type) {
        this.type = type;
    }

    @Override
    public String getValidatorType() {
        return this.type;
    }

    protected Object parse(String expression, Class type) {
        if (expression == null) {
            return null;
        }
        return TextParseUtil.translateVariables('$', expression, this.stack, type);
    }

    protected Object getFieldValue(String name, Object object) throws ValidationException {
        boolean pop = false;
        if (!this.stack.getRoot().contains(object)) {
            this.stack.push(object);
            pop = true;
        }
        Object retVal = this.stack.findValue(name);
        if (pop) {
            this.stack.pop();
        }
        return retVal;
    }

    protected void addActionError(Object object) {
        this.validatorContext.addActionError(this.getMessage(object));
    }

    protected void addFieldError(String propertyName, Object object) {
        this.validatorContext.addFieldError(propertyName, this.getMessage(object));
    }
}

