/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.validator.ValidatorConfig;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ValidatorFileParser {
    public List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory var1, InputStream var2, String var3);

    public void parseValidatorDefinitions(Map<String, String> var1, InputStream var2, String var3);
}

