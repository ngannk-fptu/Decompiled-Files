/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

class DefaultResourceTransformerChain
implements ResourceTransformerChain {
    private final ResourceResolverChain resolverChain;
    @Nullable
    private final ResourceTransformer transformer;
    @Nullable
    private final ResourceTransformerChain nextChain;

    public DefaultResourceTransformerChain(ResourceResolverChain resolverChain, @Nullable List<ResourceTransformer> transformers) {
        Assert.notNull((Object)resolverChain, (String)"ResourceResolverChain is required");
        this.resolverChain = resolverChain;
        transformers = transformers != null ? transformers : Collections.emptyList();
        DefaultResourceTransformerChain chain = this.initTransformerChain(resolverChain, new ArrayList<ResourceTransformer>(transformers));
        this.transformer = chain.transformer;
        this.nextChain = chain.nextChain;
    }

    private DefaultResourceTransformerChain initTransformerChain(ResourceResolverChain resolverChain, ArrayList<ResourceTransformer> transformers) {
        DefaultResourceTransformerChain chain = new DefaultResourceTransformerChain(resolverChain, null, null);
        ListIterator<ResourceTransformer> it = transformers.listIterator(transformers.size());
        while (it.hasPrevious()) {
            chain = new DefaultResourceTransformerChain(resolverChain, it.previous(), chain);
        }
        return chain;
    }

    public DefaultResourceTransformerChain(ResourceResolverChain resolverChain, @Nullable ResourceTransformer transformer, @Nullable ResourceTransformerChain chain) {
        Assert.isTrue((transformer == null && chain == null || transformer != null && chain != null ? 1 : 0) != 0, (String)"Both transformer and transformer chain must be null, or neither is");
        this.resolverChain = resolverChain;
        this.transformer = transformer;
        this.nextChain = chain;
    }

    @Override
    public ResourceResolverChain getResolverChain() {
        return this.resolverChain;
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
        return this.transformer != null && this.nextChain != null ? this.transformer.transform(request, resource, this.nextChain) : resource;
    }
}

