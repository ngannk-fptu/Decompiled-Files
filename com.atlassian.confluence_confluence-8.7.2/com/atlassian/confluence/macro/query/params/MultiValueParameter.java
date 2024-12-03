/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.BaseParameter;
import com.atlassian.confluence.macro.params.ParameterException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class MultiValueParameter
extends BaseParameter<Set<String>> {
    private final String delimiter;

    public MultiValueParameter(String name, String defaultValue, String delimiter) {
        super(name, defaultValue);
        this.delimiter = delimiter;
    }

    public MultiValueParameter(String[] names, String defaultValue, String delimiter) {
        super(names, defaultValue);
        this.delimiter = delimiter;
    }

    public MultiValueParameter(List<String> names, String defaultValue, String delimiter) {
        super(names, defaultValue);
        this.delimiter = delimiter;
    }

    @Override
    protected Set<String> findObject(String paramValue, MacroExecutionContext ctx) throws ParameterException {
        if (StringUtils.isBlank((CharSequence)paramValue)) {
            return Collections.emptySet();
        }
        HashSet<String> uniqueValues = new HashSet<String>();
        StringTokenizer tokenizer = new StringTokenizer(paramValue, this.delimiter);
        while (tokenizer.hasMoreTokens()) {
            String token = StringUtils.trimToNull((String)tokenizer.nextToken());
            if (token == null) continue;
            uniqueValues.add(token);
        }
        return uniqueValues;
    }
}

