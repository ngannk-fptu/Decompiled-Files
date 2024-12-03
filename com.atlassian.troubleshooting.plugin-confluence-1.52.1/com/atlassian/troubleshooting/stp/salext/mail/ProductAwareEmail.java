/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;

public class ProductAwareEmail
extends Email {
    private static final String PRODUCT_HEADER = "Atlassian-Product";

    public ProductAwareEmail(String to) {
        super(to);
    }

    public ProductAwareEmail addProductHeader(String applicationName) {
        this.addHeader(PRODUCT_HEADER, applicationName);
        return this;
    }
}

