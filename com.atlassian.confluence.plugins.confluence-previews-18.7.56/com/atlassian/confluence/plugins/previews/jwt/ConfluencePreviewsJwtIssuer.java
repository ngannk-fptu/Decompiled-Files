/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jwt.JwtIssuer
 *  com.atlassian.jwt.SigningAlgorithm
 *  com.atlassian.jwt.internal.security.SecretGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.confluence.plugins.previews.PreviewsConstants;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.internal.security.SecretGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluencePreviewsJwtIssuer
implements Supplier<JwtIssuer> {
    private static final String JWT_SHARED_SECRET = ":jwt-shared-secret";
    private PluginSettings pluginSettings;

    @Autowired
    public ConfluencePreviewsJwtIssuer(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PreviewsConstants.PLUGIN_KEY + JWT_SHARED_SECRET);
    }

    @Nonnull
    public String getName() {
        return PreviewsConstants.PLUGIN_KEY;
    }

    @Nullable
    String getSharedSecret() {
        String sharedSecret = (String)this.pluginSettings.get(JWT_SHARED_SECRET);
        if (StringUtils.isEmpty((CharSequence)sharedSecret)) {
            sharedSecret = SecretGenerator.generateUrlSafeSharedSecret((SigningAlgorithm)SigningAlgorithm.HS256);
            this.pluginSettings.put(JWT_SHARED_SECRET, (Object)sharedSecret);
        }
        return sharedSecret;
    }

    @Override
    public JwtIssuer get() {
        return new SimpleJwtIssuer(PreviewsConstants.PLUGIN_KEY, this.getSharedSecret());
    }

    private static class SimpleJwtIssuer
    implements JwtIssuer {
        private final String name;
        private final String sharedSecret;

        private SimpleJwtIssuer(String name, String sharedSecret) {
            this.name = name;
            this.sharedSecret = sharedSecret;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }

        @Nullable
        public String getSharedSecret() {
            return this.sharedSecret;
        }
    }
}

