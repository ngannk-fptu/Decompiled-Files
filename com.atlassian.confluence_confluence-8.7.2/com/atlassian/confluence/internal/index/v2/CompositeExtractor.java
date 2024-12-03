/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.index.v2.ContentBodyFieldCombiner;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.util.HtmlEntityEscapeUtil;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class CompositeExtractor {
    @Deprecated
    public static final String CONTENT_BODY = "contentBody";
    private static final Logger log = LoggerFactory.getLogger(CompositeExtractor.class);

    public Collection<FieldDescriptor> extract(Object searchable, Iterable<Extractor2> extractors) {
        ArrayList<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
        ContentBodyFieldCombiner contentBodyFieldCombiner = new ContentBodyFieldCombiner();
        for (Extractor2 extractor : extractors) {
            log.debug("Using {} to extract fields from {}", (Object)extractor, searchable);
            try {
                StringBuilder extractedText;
                Collection<FieldDescriptor> fields = extractor.extractFields(searchable);
                if (fields != null) {
                    for (FieldDescriptor f : fields) {
                        String fieldDescriptorValue = this.unEscapeHtmlIfRequired(searchable, f.getValue());
                        if (contentBodyFieldCombiner.offerField(f.getName(), fieldDescriptorValue)) continue;
                        result.add(f);
                    }
                }
                if ((extractedText = extractor.extractText(searchable)) == null) continue;
                contentBodyFieldCombiner.offerField(CONTENT_BODY, this.unEscapeHtmlIfRequired(searchable, extractedText.toString()));
            }
            catch (RuntimeException e) {
                log.warn("{} failed to extract fields from {}: {}", new Object[]{extractor, searchable, e.getMessage()});
                log.debug("{} failed to extract fields from {}", new Object[]{extractor, searchable, e});
            }
        }
        result.addAll(contentBodyFieldCombiner.getContentBodyFields());
        return result;
    }

    @VisibleForTesting
    protected String unEscapeHtmlIfRequired(Object searchable, String extractedText) {
        if (StringUtils.isBlank((CharSequence)extractedText)) {
            return extractedText;
        }
        if (searchable instanceof ContentEntityObject) {
            Attachment attachment;
            if (searchable instanceof Attachment && ("plain/text".equals((attachment = (Attachment)searchable).getMediaType()) || "txt".equals(attachment.getFileExtension()))) {
                log.debug("skipping unescape on plain/text searchable of type {} with {} characters", (Object)searchable.getClass().getName(), (Object)extractedText.length());
                return extractedText;
            }
            log.debug("applying unescape on searchable of type {} with {} characters", (Object)searchable.getClass().getName(), (Object)extractedText.length());
            StringBuffer unescapedText = new StringBuffer(extractedText);
            HtmlEntityEscapeUtil.unEscapeHtmlEntities(unescapedText);
            return unescapedText.toString();
        }
        return extractedText;
    }
}

