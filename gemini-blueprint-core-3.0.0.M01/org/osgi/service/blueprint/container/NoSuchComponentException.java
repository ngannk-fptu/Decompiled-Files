/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

public class NoSuchComponentException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String componentId;

    public NoSuchComponentException(String msg, String id) {
        super(msg);
        this.componentId = id;
    }

    public NoSuchComponentException(String id) {
        super("No component with id '" + (id == null ? "<null>" : id) + "' could be found");
        this.componentId = id;
    }

    public String getComponentId() {
        return this.componentId;
    }
}

