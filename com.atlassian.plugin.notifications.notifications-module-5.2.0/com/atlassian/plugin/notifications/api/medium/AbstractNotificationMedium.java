/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.Maps
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.SimpleMessageBuilder;
import com.atlassian.plugin.notifications.api.medium.TemplateManager;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public abstract class AbstractNotificationMedium
implements NotificationMedium {
    private static final Logger log = Logger.getLogger(AbstractNotificationMedium.class);
    private final TemplateManager templateManager;
    private final TemplateRenderer templateRenderer;
    private String key;
    protected String configTemplatePath;

    protected AbstractNotificationMedium(TemplateManager templateManager, TemplateRenderer templateRenderer, MacroResolver macroResolver, UserNotificationPreferencesManager userNotificationPreferencesManager) {
        this.templateManager = templateManager;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void init(ModuleDescriptor moduleDescriptor) {
        this.key = moduleDescriptor.getKey();
        this.configTemplatePath = "templates/" + this.key + "/config-" + this.key + ".vm";
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getServerConfigurationTemplate(ServerConfiguration serverConfiguration) {
        StringWriter out = new StringWriter();
        try {
            HashMap context = Maps.newHashMap();
            context.put("config", serverConfiguration);
            this.templateRenderer.render(this.configTemplatePath, (Map)context, (Writer)out);
        }
        catch (IOException e) {
            log.error((Object)("Error rendering " + this.configTemplatePath), (Throwable)e);
            return "Unable to render configuration form for '" + this.getKey() + "'. Consult your server logs or administrator.";
        }
        return out.toString();
    }

    @Override
    public Message renderMessage(RecipientType type, Map<String, Object> context, ServerConfiguration config) {
        SimpleMessageBuilder messageBuilder = SimpleMessageBuilder.create(this.templateManager.renderSubject(type, context, config), this.templateManager.renderMessage(type, context, config));
        return messageBuilder.messageId((String)context.get("messageId")).originatingUser((UserProfile)context.get("originatingUser")).metadata((Map)context.get("messageMetadata")).build();
    }

    @Override
    public Option<ServerConfiguration> getStaticConfiguration() {
        return Option.none();
    }

    @Override
    public boolean isUserConfigured(UserKey userKey) {
        return true;
    }
}

