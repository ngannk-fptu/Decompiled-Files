/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.template;

import com.sun.jersey.api.container.ContainerException;

public class TemplateContextException
extends ContainerException {
    public TemplateContextException() {
    }

    public TemplateContextException(String message) {
        super(message);
    }

    public TemplateContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateContextException(Throwable cause) {
        super(cause);
    }
}

