/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.internal.integration.jira.autocomplete;

import java.util.LinkedHashMap;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class AutoCompleteItem
extends LinkedHashMap<String, Object> {
    private static final String ID = "id";
    private static final String TEXT = "text";

    public AutoCompleteItem(String id, String name) {
        this.put(ID, id);
        this.put(TEXT, name);
    }

    public String getId() {
        return (String)this.get(ID);
    }

    public String getName() {
        return (String)this.get(TEXT);
    }
}

