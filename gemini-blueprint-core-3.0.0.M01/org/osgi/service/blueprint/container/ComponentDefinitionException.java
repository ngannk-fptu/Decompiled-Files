/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

public class ComponentDefinitionException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ComponentDefinitionException() {
    }

    public ComponentDefinitionException(String explanation) {
        super(explanation);
    }

    public ComponentDefinitionException(String explanation, Throwable cause) {
        super(explanation, cause);
    }

    public ComponentDefinitionException(Throwable cause) {
        super(cause);
    }
}

