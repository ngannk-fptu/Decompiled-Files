/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Predicate
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanPredicate
implements Predicate {
    private final Log log = LogFactory.getLog(this.getClass());
    private String propertyName;
    private Predicate predicate;

    public BeanPredicate(String propertyName, Predicate predicate) {
        this.propertyName = propertyName;
        this.predicate = predicate;
    }

    public boolean evaluate(Object object) {
        boolean evaluation = false;
        try {
            Object propValue = PropertyUtils.getProperty(object, this.propertyName);
            evaluation = this.predicate.evaluate(propValue);
        }
        catch (IllegalArgumentException e) {
            String errorMsg = "Problem during evaluation.";
            this.log.error((Object)"ERROR: Problem during evaluation.", (Throwable)e);
            throw e;
        }
        catch (IllegalAccessException e) {
            String errorMsg = "Unable to access the property provided.";
            this.log.error((Object)"Unable to access the property provided.", (Throwable)e);
            throw new IllegalArgumentException("Unable to access the property provided.");
        }
        catch (InvocationTargetException e) {
            String errorMsg = "Exception occurred in property's getter";
            this.log.error((Object)"Exception occurred in property's getter", (Throwable)e);
            throw new IllegalArgumentException("Exception occurred in property's getter");
        }
        catch (NoSuchMethodException e) {
            String errorMsg = "Property not found.";
            this.log.error((Object)"Property not found.", (Throwable)e);
            throw new IllegalArgumentException("Property not found.");
        }
        return evaluation;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }
}

