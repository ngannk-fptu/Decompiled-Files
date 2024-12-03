/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.factory.InitialContentFactory;

public class CompileTimeTransformer {
    public static Content process(Globals globals, Resource resource, Content content) {
        return new InitialContentFactory(globals).lookup(resource).toContent(content);
    }
}

