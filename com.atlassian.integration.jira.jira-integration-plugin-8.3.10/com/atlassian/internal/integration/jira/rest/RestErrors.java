/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.integration.jira.JiraErrors;
import com.atlassian.internal.integration.jira.rest.RestErrorMessage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class RestErrors
extends LinkedHashMap<String, Object> {
    public static final String ERRORS = "errors";

    public RestErrors() {
        this.put(ERRORS, new ArrayList());
    }

    public RestErrors(RestErrorMessage message) {
        this();
        this.addError(message);
    }

    public RestErrors(JiraErrors errors) {
        this();
        for (String string : errors.errorMessages) {
            this.addError(new RestErrorMessage(null, string, null));
        }
        for (Map.Entry entry : errors.errors.entrySet()) {
            this.addError(new RestErrorMessage((String)entry.getKey(), (String)entry.getValue(), null));
        }
    }

    public void addError(RestErrorMessage error) {
        this.getErrors().add(error);
    }

    public List<RestErrorMessage> getErrors() {
        return (List)this.get(ERRORS);
    }
}

