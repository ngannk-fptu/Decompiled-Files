/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@Internal
public class DefaultSearchableContentExtractor
implements Extractor2 {
    private static final Logger log = LoggerFactory.getLogger(DefaultSearchableContentExtractor.class);

    @Override
    public StringBuilder extractText(Object searchable) {
        StringBuilder resultBuilder = new StringBuilder();
        if (searchable instanceof ContentEntityObject) {
            String content = "";
            ContentEntityObject ceo = (ContentEntityObject)searchable;
            try {
                BodyContent bodyContent = ceo.getBodyContent();
                content = bodyContent.getBody();
                if (BodyType.XHTML.equals(bodyContent.getBodyType())) {
                    content = HTMLSearchableTextUtil.stripTags(content);
                }
            }
            catch (SAXException ex) {
                log.warn("Failed to parse the HTML for the content '{}' during indexing. Exception: {}", (Object)ceo.getTitle(), (Object)ex.toString());
            }
            if (!content.isEmpty()) {
                resultBuilder.append(content);
            }
        }
        return resultBuilder;
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        return Collections.emptyList();
    }
}

