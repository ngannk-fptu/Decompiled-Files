/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;

public class EpicAutoCompleteItem
extends AutoCompleteItem {
    private static final String LIST = "list";

    public EpicAutoCompleteItem(String id, String name, String list) {
        super(id, name);
        this.put(LIST, list);
    }

    public String getList() {
        return (String)this.get(LIST);
    }
}

