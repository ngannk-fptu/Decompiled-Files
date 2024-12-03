/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.plugin.descriptor.TransformerModuleDescriptor;
import com.google.common.base.MoreObjects;
import java.util.Comparator;

public class TransformerWeight {
    private final Transformer transformer;
    private final int weight;
    static final Comparator<TransformerWeight> SORT_BY_WEIGHT = (tw1, tw2) -> tw1.getWeight() - tw2.getWeight();

    static TransformerWeight create(TransformerModuleDescriptor moduleDescriptor) {
        return new TransformerWeight(moduleDescriptor.getModule(), moduleDescriptor.getTransformerWeight());
    }

    public TransformerWeight(Transformer transformer, int weight) {
        this.transformer = transformer;
        this.weight = weight;
    }

    public Transformer getTransformer() {
        return this.transformer;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("transformer", (Object)this.transformer).add("weight", this.weight).toString();
    }
}

