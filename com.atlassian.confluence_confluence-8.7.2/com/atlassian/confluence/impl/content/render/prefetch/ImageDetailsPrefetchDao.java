/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.pages.Attachment;
import java.util.Collection;

public interface ImageDetailsPrefetchDao {
    public int prefetchImageDetails(Collection<Attachment> var1);
}

