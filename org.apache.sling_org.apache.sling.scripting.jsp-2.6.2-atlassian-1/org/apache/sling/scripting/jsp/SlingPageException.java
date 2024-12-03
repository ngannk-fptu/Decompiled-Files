/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.sling.api.SlingException
 */
package org.apache.sling.scripting.jsp;

import org.apache.sling.api.SlingException;

public class SlingPageException
extends SlingException {
    private final String errorPage;

    public SlingPageException(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getErrorPage() {
        return this.errorPage;
    }
}

