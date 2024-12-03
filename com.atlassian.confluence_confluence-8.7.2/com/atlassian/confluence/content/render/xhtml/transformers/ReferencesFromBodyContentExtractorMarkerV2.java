/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.transformers.ExportingReference;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.Collection;

public interface ReferencesFromBodyContentExtractorMarkerV2 {
    public Collection<ExportingReference> getReferences();

    public FragmentTransformer createNewInstance();
}

