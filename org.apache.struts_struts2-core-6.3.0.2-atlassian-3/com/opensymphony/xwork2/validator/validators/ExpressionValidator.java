/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExpressionValidator
extends ValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(ExpressionValidator.class);
    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return this.expression;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        Boolean answer = Boolean.FALSE;
        Object obj = null;
        try {
            obj = this.getFieldValue(this.expression, object);
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (obj != null && obj instanceof Boolean) {
            answer = (Boolean)obj;
        } else {
            LOG.warn("Got result of [{}] when trying to get Boolean.", obj);
        }
        if (!answer.booleanValue()) {
            LOG.debug("Validation failed on expression [{}] with validated object [{}]", (Object)this.expression, object);
            this.addActionError(object);
        }
    }
}

