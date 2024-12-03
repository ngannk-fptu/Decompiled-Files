/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Transformer
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanToPropertyValueTransformer
implements Transformer {
    private final Log log = LogFactory.getLog(this.getClass());
    private String propertyName;
    private boolean ignoreNull;

    public BeanToPropertyValueTransformer(String propertyName) {
        this(propertyName, false);
    }

    public BeanToPropertyValueTransformer(String propertyName, boolean ignoreNull) {
        if (propertyName == null || propertyName.length() <= 0) {
            throw new IllegalArgumentException("propertyName cannot be null or empty");
        }
        this.propertyName = propertyName;
        this.ignoreNull = ignoreNull;
    }

    public Object transform(Object object) {
        Object propertyValue = null;
        try {
            propertyValue = PropertyUtils.getProperty(object, this.propertyName);
        }
        catch (IllegalArgumentException e) {
            String errorMsg = "Problem during transformation. Null value encountered in property path...";
            if (this.ignoreNull) {
                this.log.warn((Object)("WARNING: Problem during transformation. Null value encountered in property path..." + e));
            }
            IllegalArgumentException iae = new IllegalArgumentException("Problem during transformation. Null value encountered in property path...");
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)"Problem during transformation. Null value encountered in property path...", (Throwable)e);
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
            String errorMsg = "No property found for name [" + this.propertyName + "]";
            IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e)) {
                this.log.error((Object)errorMsg, (Throwable)e);
            }
            throw iae;
        }
        return propertyValue;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }
}

