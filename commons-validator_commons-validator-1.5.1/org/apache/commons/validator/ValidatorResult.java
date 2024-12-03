/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.validator.Field;

public class ValidatorResult
implements Serializable {
    private static final long serialVersionUID = -3713364681647250531L;
    protected Map<String, ResultStatus> hAction = new HashMap<String, ResultStatus>();
    protected Field field = null;

    public ValidatorResult(Field field) {
        this.field = field;
    }

    public void add(String validatorName, boolean result) {
        this.add(validatorName, result, null);
    }

    public void add(String validatorName, boolean result, Object value) {
        this.hAction.put(validatorName, new ResultStatus(result, value));
    }

    public boolean containsAction(String validatorName) {
        return this.hAction.containsKey(validatorName);
    }

    public boolean isValid(String validatorName) {
        ResultStatus status = this.hAction.get(validatorName);
        return status == null ? false : status.isValid();
    }

    public Object getResult(String validatorName) {
        ResultStatus status = this.hAction.get(validatorName);
        return status == null ? null : status.getResult();
    }

    public Iterator<String> getActions() {
        return Collections.unmodifiableMap(this.hAction).keySet().iterator();
    }

    @Deprecated
    public Map<String, ResultStatus> getActionMap() {
        return Collections.unmodifiableMap(this.hAction);
    }

    public Field getField() {
        return this.field;
    }

    protected static class ResultStatus
    implements Serializable {
        private static final long serialVersionUID = 4076665918535320007L;
        private boolean valid = false;
        private Object result = null;

        public ResultStatus(boolean valid, Object result) {
            this.valid = valid;
            this.result = result;
        }

        @Deprecated
        public ResultStatus(ValidatorResult ignored, boolean valid, Object result) {
            this(valid, result);
        }

        public boolean isValid() {
            return this.valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public Object getResult() {
            return this.result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }
}

