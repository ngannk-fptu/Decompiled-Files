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
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoubleRangeFieldValidator
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(DoubleRangeFieldValidator.class);
    private Double maxInclusive = null;
    private Double minInclusive = null;
    private Double minExclusive = null;
    private Double maxExclusive = null;
    private String minInclusiveExpression;
    private String maxInclusiveExpression;
    private String minExclusiveExpression;
    private String maxExclusiveExpression;

    @Override
    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        Object obj = this.getFieldValue(fieldName, object);
        if (obj == null) {
            return;
        }
        Double maxInclusiveToUse = this.getMaxInclusive();
        Double minInclusiveToUse = this.getMinInclusive();
        Double maxExclusiveToUse = this.getMaxExclusive();
        Double minExclusiveToUse = this.getMinExclusive();
        if (obj.getClass().isArray()) {
            Object[] values = (Object[])obj;
            this.validateCollection(maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse, Arrays.asList(values));
        } else if (Collection.class.isAssignableFrom(obj.getClass())) {
            Collection values = (Collection)obj;
            this.validateCollection(maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse, values);
        } else {
            this.validateValue(obj, maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse);
        }
    }

    protected void validateCollection(Double maxInclusiveToUse, Double minInclusiveToUse, Double maxExclusiveToUse, Double minExclusiveToUse, Collection values) {
        for (Object objValue : values) {
            this.validateValue(objValue, maxInclusiveToUse, minInclusiveToUse, maxExclusiveToUse, minExclusiveToUse);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void validateValue(Object obj, Double maxInclusiveToUse, Double minInclusiveToUse, Double maxExclusiveToUse, Double minExclusiveToUse) {
        try {
            this.setCurrentValue(obj);
            Double value = Double.valueOf(obj.toString());
            if (maxInclusiveToUse != null && value.compareTo(maxInclusiveToUse) > 0 || minInclusiveToUse != null && value.compareTo(minInclusiveToUse) < 0 || maxExclusiveToUse != null && value.compareTo(maxExclusiveToUse) >= 0 || minExclusiveToUse != null && value.compareTo(minExclusiveToUse) <= 0) {
                this.addFieldError(this.getFieldName(), value);
            }
        }
        catch (NumberFormatException e) {
            LOG.debug("Cannot validate value {} - not a Double", (Throwable)e);
        }
        finally {
            this.setCurrentValue(null);
        }
    }

    public void setMaxInclusive(Double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Double getMaxInclusive() {
        if (this.maxInclusive != null) {
            return this.maxInclusive;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.maxInclusiveExpression)) {
            return (Double)this.parse(this.maxInclusiveExpression, Double.class);
        }
        return this.maxInclusive;
    }

    public void setMinInclusive(Double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Double getMinInclusive() {
        if (this.minInclusive != null) {
            return this.minInclusive;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.minInclusiveExpression)) {
            return (Double)this.parse(this.minInclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinExclusive(Double minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Double getMinExclusive() {
        if (this.minExclusive != null) {
            return this.minExclusive;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.minExclusiveExpression)) {
            return (Double)this.parse(this.minExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMaxExclusive(Double maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public Double getMaxExclusive() {
        if (this.maxExclusive != null) {
            return this.maxExclusive;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.maxExclusiveExpression)) {
            return (Double)this.parse(this.maxExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinInclusiveExpression(String minInclusiveExpression) {
        this.minInclusiveExpression = minInclusiveExpression;
    }

    public void setMaxInclusiveExpression(String maxInclusiveExpression) {
        this.maxInclusiveExpression = maxInclusiveExpression;
    }

    public void setMinExclusiveExpression(String minExclusiveExpression) {
        this.minExclusiveExpression = minExclusiveExpression;
    }

    public void setMaxExclusiveExpression(String maxExclusiveExpression) {
        this.maxExclusiveExpression = maxExclusiveExpression;
    }
}

