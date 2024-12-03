/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RangeValidatorSupport<T extends Comparable>
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(RangeValidatorSupport.class);
    private final Class<T> type;
    private T min;
    private String minExpression;
    private T max;
    private String maxExpression;

    protected RangeValidatorSupport(Class<T> type) {
        this.type = type;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        Object obj = this.getFieldValue(this.getFieldName(), object);
        if (obj == null) {
            return;
        }
        T min = this.getMin();
        T max = this.getMax();
        if (obj.getClass().isArray()) {
            Object[] values;
            for (Object objValue : values = (Object[])obj) {
                this.validateValue(object, (Comparable)objValue, min, max);
            }
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            Collection values = (Collection)obj;
            for (Object objValue : values) {
                this.validateValue(object, (Comparable)objValue, min, max);
            }
        } else {
            this.validateValue(object, (Comparable)obj, min, max);
        }
    }

    protected void validateValue(Object object, Comparable<T> value, T min, T max) {
        this.setCurrentValue(value);
        if (min != null && value.compareTo(min) < 0) {
            this.addFieldError(this.getFieldName(), object);
        }
        if (max != null && value.compareTo(max) > 0) {
            this.addFieldError(this.getFieldName(), object);
        }
        this.setCurrentValue(null);
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMin() {
        return this.getT(this.min, this.minExpression, this.type);
    }

    public T getMax() {
        return this.getT(this.max, this.maxExpression, this.type);
    }

    public void setMinExpression(String minExpression) {
        LOG.debug("${minExpression} was defined as [{}]", (Object)minExpression);
        this.minExpression = minExpression;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public void setMaxExpression(String maxExpression) {
        LOG.debug("${maxExpression} was defined as [{}]", (Object)maxExpression);
        this.maxExpression = maxExpression;
    }

    protected T getT(T minMax, String minMaxExpression, Class<T> toType) {
        if (minMax != null) {
            return minMax;
        }
        if (StringUtils.isNotEmpty((CharSequence)minMaxExpression)) {
            return (T)((Comparable)this.parse(minMaxExpression, toType));
        }
        return null;
    }
}

