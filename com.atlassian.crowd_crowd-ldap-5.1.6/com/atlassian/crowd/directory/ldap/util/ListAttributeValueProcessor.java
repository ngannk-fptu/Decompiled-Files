/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.directory.ldap.util.AttributeValueProcessor;
import java.util.ArrayList;
import java.util.List;

public class ListAttributeValueProcessor
implements AttributeValueProcessor {
    private List<String> _values = new ArrayList<String>();

    @Override
    public void process(Object value) {
        this._values.add((String)value);
    }

    public List<String> getValues() {
        return this._values;
    }
}

