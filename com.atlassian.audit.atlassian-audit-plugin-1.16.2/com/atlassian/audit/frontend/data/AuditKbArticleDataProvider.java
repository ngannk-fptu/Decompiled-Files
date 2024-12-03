/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.frontend.data.AuditKbArticleData;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@ParametersAreNonnullByDefault
@Nonnull
public class AuditKbArticleDataProvider
implements WebResourceDataProvider {
    @VisibleForTesting
    static final String HELP_PREFIX = "help.";
    @VisibleForTesting
    static final String FALLBACK_URL = "http://www.atlassian.com/";
    private static final String KB_ARTICLES_PROPERTIES_FILE_PATH = "/atlassian-audit-kb-articles.properties";
    private static final String PREFIX_KEY = "help.atlassian.audit.frontend.kb.";
    private static final String KB_FEATURE_KEY = "help.atlassian.audit.frontend.kb.feature";
    private static final String KB_REFERENCE_KEY = "help.atlassian.audit.frontend.kb.reference";
    private static final String KB_INTEGRATIONS_KEY = "help.atlassian.audit.frontend.kb.integrations";
    private static final String KB_DATABASE_RETENTION_KEY = "help.atlassian.audit.frontend.kb.databaseRetention";
    private final ApplicationProperties applicationProperties;
    private final HelpPathResolver helpPathResolver;
    private final ObjectMapper objectMapper;
    private final Properties kbArticles;

    public AuditKbArticleDataProvider(ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, ObjectMapper objectMapper) {
        this(applicationProperties, helpPathResolver, objectMapper, () -> {
            Properties properties = new Properties();
            try {
                properties.load(AuditKbArticleDataProvider.class.getResourceAsStream(KB_ARTICLES_PROPERTIES_FILE_PATH));
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return properties;
        });
    }

    @VisibleForTesting
    AuditKbArticleDataProvider(ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, ObjectMapper objectMapper, Supplier<Properties> properties) {
        this.applicationProperties = applicationProperties;
        this.helpPathResolver = helpPathResolver;
        this.objectMapper = objectMapper;
        this.kbArticles = properties.get();
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

    @VisibleForTesting
    AuditKbArticleData getData() {
        return new AuditKbArticleData(URI.create(this.getHelpPathForProduct(KB_FEATURE_KEY)), URI.create(this.getHelpPathForProduct(KB_REFERENCE_KEY)), URI.create(this.getHelpPathForProduct(KB_INTEGRATIONS_KEY)), URI.create(this.kbArticles.getProperty(KB_DATABASE_RETENTION_KEY, FALLBACK_URL)));
    }

    @VisibleForTesting
    String getHelpPathForProduct(String helpPathKey) {
        String productHelpPathKey = helpPathKey + "." + this.applicationProperties.getPlatformId();
        return Optional.ofNullable(this.helpPathResolver.getHelpPath(productHelpPathKey)).map(HelpPath::getUrl).orElse(FALLBACK_URL);
    }
}

