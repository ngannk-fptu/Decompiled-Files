/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import java.beans.PropertyEditorSupport;
import org.springframework.ldap.filter.HardcodedFilter;

public class FilterEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(new HardcodedFilter(text));
    }
}

