/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.macro.query.params.MultiValueParameter;
import java.util.List;

public class AuthorParameter
extends MultiValueParameter {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"author"};
    private static final String DELIMITER = ",";

    public AuthorParameter() {
        super(DEFAULT_PARAM_NAMES, null, DELIMITER);
    }

    public AuthorParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue, DELIMITER);
    }

    public AuthorParameter(List<String> names, String defaultValue) {
        super(names, defaultValue, DELIMITER);
    }
}

