/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatatypeException
extends Exception {
    static final long serialVersionUID = 1940805832730465578L;
    protected final String key;
    protected final Object[] args;

    public DatatypeException(String string, Object[] objectArray) {
        super(string);
        this.key = string;
        this.args = objectArray;
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public String getMessage() {
        ResourceBundle resourceBundle = null;
        resourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLSchemaMessages");
        if (resourceBundle == null) {
            throw new MissingResourceException("Property file not found!", "org.apache.xerces.impl.msg.XMLSchemaMessages", this.key);
        }
        String string = resourceBundle.getString(this.key);
        if (string == null) {
            string = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(string, "org.apache.xerces.impl.msg.XMLSchemaMessages", this.key);
        }
        if (this.args != null) {
            try {
                string = MessageFormat.format(string, this.args);
            }
            catch (Exception exception) {
                string = resourceBundle.getString("FormatFailed");
                string = string + " " + resourceBundle.getString(this.key);
            }
        }
        return string;
    }
}

