/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.tld;

import java.util.HashMap;
import java.util.Map;

public class ValidatorXml {
    private String validatorClass;
    private final Map<String, String> initParams = new HashMap<String, String>();

    public String getValidatorClass() {
        return this.validatorClass;
    }

    public void setValidatorClass(String validatorClass) {
        this.validatorClass = validatorClass;
    }

    public void addInitParam(String name, String value) {
        this.initParams.put(name, value);
    }

    public Map<String, String> getInitParams() {
        return this.initParams;
    }
}

