/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.business.insights.core.frontend.data;

import com.atlassian.business.insights.core.frontend.data.KbArticleLinkData;
import com.atlassian.business.insights.core.frontend.data.KbArticleLinkResolver;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class KbArticleLinkDataProvider
implements WebResourceDataProvider {
    private static final String KB_FEATURE_KEY = "feature";
    private static final String KB_SCHEMA_KEY = "schema";
    private static final String KB_TROUBLESHOOTING_KEY = "troubleshooting";
    private final ObjectMapper objectMapper;
    private final KbArticleLinkResolver kbArticleLinkResolver;

    public KbArticleLinkDataProvider(@Nonnull ObjectMapper objectMapper, @Nonnull KbArticleLinkResolver kbArticleLinkResolver) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.kbArticleLinkResolver = Objects.requireNonNull(kbArticleLinkResolver);
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.objectMapper.writeValue(writer, (Object)this.getData());
            }
            catch (Exception e) {
                throw new JsonMappingException(e.getMessage(), (Throwable)e);
            }
        };
    }

    private KbArticleLinkData getData() {
        return new KbArticleLinkData(this.getUri(KB_FEATURE_KEY), this.getUri(KB_SCHEMA_KEY), this.getUri(KB_TROUBLESHOOTING_KEY));
    }

    private URI getUri(String key) {
        return URI.create(this.kbArticleLinkResolver.getKbLink(key));
    }
}

