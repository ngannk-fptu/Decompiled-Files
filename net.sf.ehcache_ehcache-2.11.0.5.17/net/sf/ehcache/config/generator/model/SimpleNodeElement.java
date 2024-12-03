/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import net.sf.ehcache.config.generator.model.AbstractNodeElement;
import net.sf.ehcache.config.generator.model.NodeElement;

public class SimpleNodeElement
extends AbstractNodeElement {
    private final String name;

    public SimpleNodeElement(NodeElement parent, String name) {
        super(parent);
        this.name = name;
        this.optional = true;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

