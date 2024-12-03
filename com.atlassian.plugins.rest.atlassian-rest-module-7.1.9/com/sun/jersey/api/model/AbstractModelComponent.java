/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractModelVisitor;
import java.util.List;

public interface AbstractModelComponent {
    public void accept(AbstractModelVisitor var1);

    public List<AbstractModelComponent> getComponents();
}

