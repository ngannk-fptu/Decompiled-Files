/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.List;

public interface DefaultFragmentTransformerFactory {
    public DefaultFragmentTransformer createDefault();

    public DefaultFragmentTransformer createWithCustomFragmentTransformers(List<? extends FragmentTransformer> var1);
}

