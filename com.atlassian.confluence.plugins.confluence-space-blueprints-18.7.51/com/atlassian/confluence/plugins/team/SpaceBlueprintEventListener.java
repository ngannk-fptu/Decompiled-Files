/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.team;

import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceBlueprintEventListener {
    private static final int MAXIMUM_TWO_LINES = 14;
    private static final int MINIMUM_TWO_LINES = 6;
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final SpacePermissionManager spacePermissionManager;

    @Autowired
    public SpaceBlueprintEventListener(@ComponentImport EventPublisher eventPublisher, @ComponentImport UserAccessor userAccessor, @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
        this.spacePermissionManager = spacePermissionManager;
    }

    @PostConstruct
    public void initialise() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onSpaceBlueprintCreate(SpaceBlueprintCreateEvent event) {
        if (!"com.atlassian.confluence.plugins.confluence-space-blueprints:team-space-blueprint".equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }
        String members = (String)event.getContext().get("members");
        Space space = event.getSpace();
        String[] userNames = members.split(",");
        String teamGrid = this.generateTeamGrid(userNames);
        this.grantPermissionsToTeamMembers(Arrays.asList(userNames), space);
        event.getContext().put("team", teamGrid);
    }

    private void grantPermissionsToTeamMembers(List<String> usernames, Space space) {
        for (String username : usernames) {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            if (user == null) continue;
            for (String permission : SpacePermission.GENERIC_SPACE_PERMISSIONS) {
                this.spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission((String)permission, (Space)space, (ConfluenceUser)user));
            }
        }
    }

    String generateTeamGrid(String ... userNames) {
        StringBuilder sb = new StringBuilder();
        int numUsers = userNames.length;
        int MAX_COL_NUM = numUsers < 6 ? numUsers : (numUsers < 14 ? (int)Math.ceil((float)numUsers / 2.0f) : 7);
        int colIdx = MAX_COL_NUM;
        for (String username : userNames) {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            if (user == null) continue;
            if (colIdx == MAX_COL_NUM) {
                sb.append("<tr>\n");
            }
            SpaceBlueprintEventListener.fillUserTemplate(sb, user);
            if (--colIdx != 0) continue;
            sb.append("</tr>\n");
            colIdx = MAX_COL_NUM;
        }
        if (numUsers > 6 && colIdx < MAX_COL_NUM) {
            while (colIdx-- > 0) {
                sb.append("<td></td>\n");
            }
            sb.append("</tr>\n");
        }
        return sb.toString();
    }

    static void fillUserTemplate(StringBuilder sb, ConfluenceUser user) {
        sb.append("<td><p style=\"text-align: center;\">");
        sb.append(String.format("<ac:structured-macro ac:name=\"profile-picture\"><ac:parameter ac:name=\"User\"><ri:user ri:userkey=\"%1$s\" /></ac:parameter></ac:structured-macro>", HtmlEscaper.escapeAll((String)user.getKey().getStringValue(), (boolean)false)));
        sb.append("</p><p style=\"text-align: center;\">");
        sb.append(String.format("<strong><a href=\"mailto:%1$s\">%2$s</a></strong>", HtmlEscaper.escapeAll((String)user.getEmail(), (boolean)false), HtmlEscaper.escapeAll((String)user.getFullName(), (boolean)false)));
        sb.append("</p></td>\n");
    }
}

