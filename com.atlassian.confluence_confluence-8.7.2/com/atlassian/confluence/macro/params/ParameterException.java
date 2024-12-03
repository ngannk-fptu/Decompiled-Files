/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.macro.params;

import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Set;

public class ParameterException
extends MacroException {
    private String name;
    private String value;
    private Set<String> acceptableValues;

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(Throwable cause, String name, String value, Set<String> acceptableValues) {
        super(cause);
        this.name = name;
        this.value = value;
        this.acceptableValues = acceptableValues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAcceptableValues(Set<String> acceptableValues) {
        this.acceptableValues = acceptableValues;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public Set<String> getAcceptableValues() {
        return this.acceptableValues;
    }
}

