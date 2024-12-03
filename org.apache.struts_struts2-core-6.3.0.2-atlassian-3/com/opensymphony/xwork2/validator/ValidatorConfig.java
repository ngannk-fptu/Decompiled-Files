/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidatorConfig
extends Located {
    private String type;
    private Map<String, Object> params;
    private String defaultMessage;
    private String messageKey;
    private boolean shortCircuit;
    private String[] messageParams;

    protected ValidatorConfig(String validatorType) {
        this.type = validatorType;
        this.params = new LinkedHashMap<String, Object>();
    }

    protected ValidatorConfig(ValidatorConfig orig) {
        this.type = orig.type;
        this.params = new LinkedHashMap<String, Object>(orig.params);
        this.defaultMessage = orig.defaultMessage;
        this.messageKey = orig.messageKey;
        this.shortCircuit = orig.shortCircuit;
        this.messageParams = orig.messageParams;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public boolean isShortCircuit() {
        return this.shortCircuit;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public String getType() {
        return this.type;
    }

    public String[] getMessageParams() {
        return this.messageParams;
    }

    static /* synthetic */ String[] access$202(ValidatorConfig x0, String[] x1) {
        x0.messageParams = x1;
        return x1;
    }

    public static final class Builder {
        private ValidatorConfig target;

        public Builder(String validatorType) {
            this.target = new ValidatorConfig(validatorType);
        }

        public Builder(ValidatorConfig config) {
            this.target = new ValidatorConfig(config);
        }

        public Builder shortCircuit(boolean shortCircuit) {
            this.target.shortCircuit = shortCircuit;
            return this;
        }

        public Builder defaultMessage(String msg) {
            if (msg != null && msg.trim().length() > 0) {
                this.target.defaultMessage = msg;
            }
            return this;
        }

        public Builder messageParams(String[] msgParams) {
            ValidatorConfig.access$202(this.target, msgParams);
            return this;
        }

        public Builder messageKey(String key) {
            if (key != null && key.trim().length() > 0) {
                this.target.messageKey = key;
            }
            return this;
        }

        public Builder addParam(String name, Object value) {
            if (value != null && name != null) {
                this.target.params.put(name, value);
            }
            return this;
        }

        public Builder addParams(Map<String, Object> params) {
            this.target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            this.target.location = loc;
            return this;
        }

        public ValidatorConfig build() {
            this.target.params = Collections.unmodifiableMap(this.target.params);
            ValidatorConfig result = this.target;
            this.target = new ValidatorConfig(this.target);
            return result;
        }

        public Builder removeParam(String key) {
            this.target.params.remove(key);
            return this;
        }
    }
}

