/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;

public class ExtractorContainerException
extends ContainerException {
    public ExtractorContainerException() {
    }

    public ExtractorContainerException(String message) {
        super(message);
    }

    public ExtractorContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractorContainerException(Throwable cause) {
        super(cause);
    }
}

