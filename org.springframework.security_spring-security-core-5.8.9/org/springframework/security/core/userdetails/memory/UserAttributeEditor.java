/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.core.userdetails.memory;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.util.StringUtils;

public class UserAttributeEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String s) throws IllegalArgumentException {
        if (!StringUtils.hasText((String)s)) {
            this.setValue(null);
            return;
        }
        String[] tokens = StringUtils.commaDelimitedListToStringArray((String)s);
        UserAttribute userAttrib = new UserAttribute();
        ArrayList<String> authoritiesAsStrings = new ArrayList<String>();
        for (int i = 0; i < tokens.length; ++i) {
            String currentToken = tokens[i].trim();
            if (i == 0) {
                userAttrib.setPassword(currentToken);
                continue;
            }
            if (currentToken.toLowerCase().equals("enabled")) {
                userAttrib.setEnabled(true);
                continue;
            }
            if (currentToken.toLowerCase().equals("disabled")) {
                userAttrib.setEnabled(false);
                continue;
            }
            authoritiesAsStrings.add(currentToken);
        }
        userAttrib.setAuthoritiesAsString(authoritiesAsStrings);
        this.setValue(userAttrib.isValid() ? userAttrib : null);
    }
}

