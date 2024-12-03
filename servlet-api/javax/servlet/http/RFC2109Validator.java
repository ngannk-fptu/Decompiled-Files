/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.text.MessageFormat;
import javax.servlet.http.RFC6265Validator;

class RFC2109Validator
extends RFC6265Validator {
    RFC2109Validator(boolean allowSlash) {
        if (allowSlash) {
            this.allowed.set(47);
        }
    }

    @Override
    void validate(String name) {
        super.validate(name);
        if (name.charAt(0) == '$') {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            throw new IllegalArgumentException(MessageFormat.format(errMsg, name));
        }
    }
}

