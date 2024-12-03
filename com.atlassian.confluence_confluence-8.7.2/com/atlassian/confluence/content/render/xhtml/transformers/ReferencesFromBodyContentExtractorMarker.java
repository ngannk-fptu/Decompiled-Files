/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.transformers.ExportingReference;
import java.util.Collection;

public interface ReferencesFromBodyContentExtractorMarker {
    public Collection<ExportingReference> getReferences();
}

