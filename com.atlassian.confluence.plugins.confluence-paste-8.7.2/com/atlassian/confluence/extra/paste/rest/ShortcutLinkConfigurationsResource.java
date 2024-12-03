/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.ShortcutLinkConfig
 *  com.atlassian.confluence.renderer.ShortcutLinksManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.extra.paste.rest;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path(value="/shortcutlinkconfigurations")
public class ShortcutLinkConfigurationsResource {
    private final ShortcutLinksManager shortcutLinksManager;
    private final PermissionManager permissionManager;

    public ShortcutLinkConfigurationsResource(ShortcutLinksManager shortcutLinksManager, @ComponentImport PermissionManager permissionManager) {
        this.shortcutLinksManager = shortcutLinksManager;
        this.permissionManager = permissionManager;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getConfigurations() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        Map shortcutLinks = this.shortcutLinksManager.getShortcutLinks();
        if (shortcutLinks == null || shortcutLinks.isEmpty()) {
            return Response.noContent().build();
        }
        ShortcutLinkConfigurationEntities result = this.convert(shortcutLinks);
        return Response.ok((Object)result).build();
    }

    private ShortcutLinkConfigurationEntities convert(Map<String, ShortcutLinkConfig> from) {
        ArrayList<ShortcutLinkConfigurationEntity> entities = new ArrayList<ShortcutLinkConfigurationEntity>(from.size());
        for (Map.Entry<String, ShortcutLinkConfig> entry : from.entrySet()) {
            entities.add(new ShortcutLinkConfigurationEntity(entry.getKey(), entry.getValue().getExpandedValue(), entry.getValue().getDefaultAlias()));
        }
        ShortcutLinkConfigurationEntities result = new ShortcutLinkConfigurationEntities(entities);
        return result;
    }

    @XmlRootElement(name="configuration")
    public static final class ShortcutLinkConfigurationEntity {
        @XmlAttribute
        private final String key;
        @XmlAttribute
        private final String expandedValue;
        @XmlAttribute
        private final String defaultAlias;
        private final String regex;

        public ShortcutLinkConfigurationEntity(String key, String expandedValue, String defaultAlias) {
            this.key = key;
            this.expandedValue = expandedValue;
            this.defaultAlias = defaultAlias;
            this.regex = ShortcutLinkConfigurationEntity.translateToRegex(expandedValue, defaultAlias);
        }

        private static String translateToRegex(String expandedValue, String defaultAlias) {
            if (expandedValue.contains("%s")) {
                return "\\Q" + expandedValue.replace(defaultAlias, "\\E(.*)\\Q") + "\\E";
            }
            return "\\Q" + expandedValue + "\\E(.*)";
        }

        public String getKey() {
            return this.key;
        }

        public String getExpandedValue() {
            return this.expandedValue;
        }

        public String getDefaultAlias() {
            return this.defaultAlias;
        }

        public String getRegex() {
            return this.regex;
        }
    }

    @XmlRootElement(name="configurations")
    public static final class ShortcutLinkConfigurationEntities {
        @XmlElement
        private final List<ShortcutLinkConfigurationEntity> configurations;

        public ShortcutLinkConfigurationEntities(List<ShortcutLinkConfigurationEntity> configurations) {
            this.configurations = configurations;
        }

        public List<ShortcutLinkConfigurationEntity> getConfigurations() {
            return this.configurations;
        }
    }
}

