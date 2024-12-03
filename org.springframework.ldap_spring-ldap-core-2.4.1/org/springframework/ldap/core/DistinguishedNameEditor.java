/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.beans.PropertyEditorSupport;
import org.springframework.ldap.core.DistinguishedName;

public class DistinguishedNameEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            this.setValue(null);
        } else {
            this.setValue(new DistinguishedName(text).immutableDistinguishedName());
        }
    }

    @Override
    public String getAsText() {
        Object theValue = this.getValue();
        if (theValue == null) {
            return null;
        }
        return ((DistinguishedName)theValue).toString();
    }
}

