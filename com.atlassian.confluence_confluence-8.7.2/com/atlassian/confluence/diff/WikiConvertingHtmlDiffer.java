/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.util.concurrent.ConcurrentOperationMap
 *  com.atlassian.util.concurrent.ConcurrentOperationMapImpl
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.diff.DaisyHtmlDiffer;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.util.concurrent.ConcurrentOperationMap;
import com.atlassian.util.concurrent.ConcurrentOperationMapImpl;
import com.google.common.base.Throwables;
import java.util.concurrent.ExecutionException;

public class WikiConvertingHtmlDiffer
implements Differ {
    private final ConcurrentOperationMap<Pair<Option<ContentId>, Option<ContentId>>, String> operationMap = new ConcurrentOperationMapImpl();
    private final WikiToStorageConverter wikiToStorageConverter;
    private final DaisyHtmlDiffer delegateDiffer;

    public WikiConvertingHtmlDiffer(WikiToStorageConverter wikiToStorageConverter, DaisyHtmlDiffer delegateDiffer) {
        this.wikiToStorageConverter = wikiToStorageConverter;
        this.delegateDiffer = delegateDiffer;
    }

    @Deprecated
    public WikiConvertingHtmlDiffer(XhtmlContent xhtmlContent, DaisyHtmlDiffer delegateDiffer) {
        this.wikiToStorageConverter = xhtmlContent;
        this.delegateDiffer = delegateDiffer;
    }

    @Override
    public String diff(ContentEntityObject left, ContentEntityObject right) {
        Pair operationKey = Pair.pair((Object)Option.option((Object)left.getContentId()), (Object)Option.option((Object)right.getContentId()));
        try {
            return (String)this.operationMap.runOperation((Object)operationKey, () -> {
                ContentEntityObject convertedLeft = this.wikiToStorageConverter.convertWikiBodyToStorage(left);
                ContentEntityObject convertedRight = this.wikiToStorageConverter.convertWikiBodyToStorage(right);
                return this.delegateDiffer.diff(convertedLeft, convertedRight);
            });
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }
}

