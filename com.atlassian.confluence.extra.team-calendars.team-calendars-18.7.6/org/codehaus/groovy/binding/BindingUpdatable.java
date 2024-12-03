/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

public interface BindingUpdatable {
    public void bind();

    public void unbind();

    public void rebind();

    public void update();

    public void reverseUpdate();
}

