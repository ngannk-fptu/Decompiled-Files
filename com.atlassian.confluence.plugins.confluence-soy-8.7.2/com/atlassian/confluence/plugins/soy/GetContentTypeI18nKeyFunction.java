/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class GetContentTypeI18nKeyFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> VALID_ARGUMENT_SIZES = ImmutableSet.of((Object)1);
    private final ContentUiSupport<ContentEntityObject> contentUiSupport;

    public GetContentTypeI18nKeyFunction(ContentUiSupport<ContentEntityObject> contentUiSupport) {
        this.contentUiSupport = contentUiSupport;
    }

    public String getName() {
        return "getContentTypeI18NKey";
    }

    public String apply(Object ... args) {
        ContentEntityObject page = (ContentEntityObject)args[0];
        return this.contentUiSupport.getContentTypeI18NKey((ConfluenceEntityObject)page);
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARGUMENT_SIZES;
    }
}

