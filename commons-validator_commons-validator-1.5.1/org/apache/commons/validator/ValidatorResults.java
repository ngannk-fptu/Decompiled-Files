/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorResult;

public class ValidatorResults
implements Serializable {
    private static final long serialVersionUID = -2709911078904924839L;
    protected Map<String, ValidatorResult> hResults = new HashMap<String, ValidatorResult>();

    public void merge(ValidatorResults results) {
        this.hResults.putAll(results.hResults);
    }

    public void add(Field field, String validatorName, boolean result) {
        this.add(field, validatorName, result, null);
    }

    public void add(Field field, String validatorName, boolean result, Object value) {
        ValidatorResult validatorResult = this.getValidatorResult(field.getKey());
        if (validatorResult == null) {
            validatorResult = new ValidatorResult(field);
            this.hResults.put(field.getKey(), validatorResult);
        }
        validatorResult.add(validatorName, result, value);
    }

    public void clear() {
        this.hResults.clear();
    }

    public boolean isEmpty() {
        return this.hResults.isEmpty();
    }

    public ValidatorResult getValidatorResult(String key) {
        return this.hResults.get(key);
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(this.hResults.keySet());
    }

    public Map<String, Object> getResultValueMap() {
        HashMap<String, Object> results = new HashMap<String, Object>();
        for (String propertyKey : this.hResults.keySet()) {
            ValidatorResult vr = this.getValidatorResult(propertyKey);
            Iterator<String> x = vr.getActions();
            while (x.hasNext()) {
                String actionKey = x.next();
                Object result = vr.getResult(actionKey);
                if (result == null || result instanceof Boolean) continue;
                results.put(propertyKey, result);
            }
        }
        return results;
    }
}

