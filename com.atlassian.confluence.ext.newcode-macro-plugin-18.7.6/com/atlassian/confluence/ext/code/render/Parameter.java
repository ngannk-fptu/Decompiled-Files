/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.ext.code.render.InvalidValueException;
import java.util.Map;

interface Parameter {
    public String getName();

    public String getMacroName();

    public String getValue(Map<String, String> var1) throws InvalidValueException;
}

