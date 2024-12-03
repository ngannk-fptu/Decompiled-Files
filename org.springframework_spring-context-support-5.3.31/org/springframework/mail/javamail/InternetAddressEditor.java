/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  org.springframework.util.StringUtils
 */
package org.springframework.mail.javamail;

import java.beans.PropertyEditorSupport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.springframework.util.StringUtils;

public class InternetAddressEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText((String)text)) {
            try {
                this.setValue(new InternetAddress(text));
            }
            catch (AddressException ex) {
                throw new IllegalArgumentException("Could not parse mail address: " + ex.getMessage());
            }
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        InternetAddress value = (InternetAddress)this.getValue();
        return value != null ? value.toUnicodeString() : "";
    }
}

