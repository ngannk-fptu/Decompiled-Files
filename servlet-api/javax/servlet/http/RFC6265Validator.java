/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import javax.servlet.http.CookieNameValidator;

class RFC6265Validator
extends CookieNameValidator {
    private static final String RFC2616_SEPARATORS = "()<>@,;:\\\"/[]?={} \t";

    RFC6265Validator() {
        super(RFC2616_SEPARATORS);
    }
}

