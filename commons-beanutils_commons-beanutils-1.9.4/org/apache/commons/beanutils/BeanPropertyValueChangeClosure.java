/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Closure
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanPropertyValueChangeClosure
implements Closure {
    private final Log log = LogFactory.getLog(this.getClass());
    private String propertyName;
    private Object propertyValue;
    private boolean ignoreNull;

    public BeanPropertyValueChangeClosure(String propertyName, Object propertyValue) {
        this(propertyName, propertyValue, false);
    }

    public BeanPropertyValueChangeClosure(String propertyName, Object propertyValue, boolean ignoreNull) {
        if (propertyName == null || propertyName.length() <= 0) {
            throw new IllegalArgumentException("propertyName cannot be null or empty");
        }
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.ignoreNull = ignoreNull;
    }

    public void execute(Object object) {
        try {
            PropertyUtils.setProperty(object, this.propertyName, this.propertyValue);
        }
        catch (IllegalArgumentException e) {
            String errorMsg = "Unable to execute Closure. Null value encountered in property path...";
            if (this.ignoreNull) {
                this.log.warn((Object)("WARNING: Unable to execute Closure. Null value encountered in property path..." + e));
            }
            IllegalArgumentException iae = new IllegalArgumentException("Unable to execute Closure. Null value encountered in property path...");
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)"Unable to execute Closure. Null value encountered in property path...", (Throwable)e);
            }
            throw iae;
        }
        catch (IllegalAccessException e) {
            String errorMsg = "Unable to access the property provided.";
            IllegalArgumentException iae = new IllegalArgumentException("Unable to access the property provided.");
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)"Unable to access the property provided.", (Throwable)e);
            }
            throw iae;
        }
        catch (InvocationTargetException e) {
            String errorMsg = "Exception occurred in property's getter";
            IllegalArgumentException iae = new IllegalArgumentException("Exception occurred in property's getter");
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)"Exception occurred in property's getter", (Throwable)e);
            }
            throw iae;
        }
        catch (NoSuchMethodException e) {
            String errorMsg = "Property not found";
            IllegalArgumentException iae = new IllegalArgumentException("Property not found");
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)"Property not found", (Throwable)e);
            }
            throw iae;
        }
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Object getPropertyValue() {
        return this.propertyValue;
    }

    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }
}

