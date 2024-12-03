/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.PeopleDirectoryEnabledCondition
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.extra.userlister;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.extra.userlister.UserListManager;
import com.atlassian.confluence.extra.userlister.model.UserList;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.descriptor.web.conditions.PeopleDirectoryEnabledCondition;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserLister
extends BaseMacro
implements Macro {
    static final String USER_LISTER_LIMIT_PROPERTY = "confluence.extra.userlister.limit";
    private static final Logger logger = LoggerFactory.getLogger(UserLister.class);
    private static final int DEFAULT_USER_LISTER_LIMIT = 10000;
    private final UserAccessor userAccessor;
    private final UserListManager userListManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final VelocityHelperService velocityHelperService;
    private final PermissionManager permissionManager;
    private final TransactionalExecutorFactory transactionalExecutorFactory;

    @Autowired
    public UserLister(@ComponentImport UserAccessor userAccessor, UserListManager userListManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport PermissionManager permissionManager, @ComponentImport TransactionalExecutorFactory transactionalExecutorFactory) {
        this.userAccessor = userAccessor;
        this.userListManager = userListManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.velocityHelperService = velocityHelperService;
        this.permissionManager = permissionManager;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    private String getText(String key) {
        return this.getI18NBean().getText(key);
    }

    private String getText(String key, List params) {
        return this.getI18NBean().getText(key, params);
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    private String createCSVList(List<String> emptyGroups) {
        return this.getText("userlister.noresultsfoundforgroups", Collections.singletonList(StringUtils.join(emptyGroups, (char)',')));
    }

    private Set<String> getGroups(String groupNames) {
        String[] groupNameArray = StringUtils.split((String)groupNames, (char)',');
        Set<String> groups = Arrays.stream(groupNameArray).map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (groups.contains("*")) {
            this.userAccessor.getGroups().forEach(group -> groups.add(group.getName()));
            groups.remove("*");
        }
        return groups;
    }

    private Set<String> getAllowedGroups(Set<String> groups) {
        TreeSet<String> allowedGroups = new TreeSet<String>(groups);
        allowedGroups.removeAll(this.userListManager.getGroupBlackList());
        return allowedGroups;
    }

    private Set<String> getDeniedGroups(Set<String> groups) {
        TreeSet<String> deniedGroups = new TreeSet<String>(groups);
        deniedGroups.retainAll(this.userListManager.getGroupBlackList());
        return deniedGroups;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        if (this.isPeopleDirectoryDisabled()) {
            return RenderUtils.blockError((String)this.getText("userlister.notpermitted.viewuserprofile"), (String)"");
        }
        String groupNames = StringUtils.defaultString((String)parameters.get("groups"), (String)parameters.get("group"));
        boolean returnOnlineUsers = Boolean.parseBoolean(StringUtils.trim((String)parameters.get("online")));
        Boolean showWarning = Boolean.valueOf(StringUtils.defaultString((String)StringUtils.trim((String)parameters.get("showWarning")), (String)"true"));
        Set<String> blackListedGroups = this.userListManager.getGroupBlackList();
        if (StringUtils.isBlank((CharSequence)groupNames)) {
            return this.getText("userlister.no.groups.specified");
        }
        if (ArrayUtils.contains((Object[])StringUtils.split((String)groupNames, (char)','), (Object)"*") && blackListedGroups.contains("*")) {
            return this.getText("userlister.group.name.list.contains.asterisk");
        }
        Set<String> groups = this.getGroups(groupNames);
        Set<String> allowedGroups = this.getAllowedGroups(groups);
        Set<String> deniedGroups = this.getDeniedGroups(groups);
        Set<String> loggedInUsernames = this.userListManager.getLoggedInUsers();
        ArrayList groupList = new ArrayList();
        ArrayList<String> emptyGroups = new ArrayList<String>();
        ListMode listMode = !parameters.containsKey("online") ? ListMode.ALL : (returnOnlineUsers ? ListMode.ONLINE_ONLY : ListMode.OFFLINE_ONLY);
        int userLimit = Integer.getInteger(USER_LISTER_LIMIT_PROPERTY, 10000);
        String macroText = (String)this.transactionalExecutorFactory.createExecutor(true, true).execute(connection -> {
            int userCount = 0;
            ArrayList<UserList> usersToBeLoaded = new ArrayList<UserList>();
            for (String currentGroup : allowedGroups) {
                List<String> usernames = this.getUserNames(currentGroup, loggedInUsernames, listMode);
                if ((userCount += usernames.size()) >= userLimit) {
                    logger.warn(String.format("There are too many users in the specified groups. The limit is %s.", userLimit));
                    return this.getText("userlister.too.many.users", Collections.singletonList(userLimit));
                }
                usersToBeLoaded.add(new UserList(currentGroup, this.userAccessor, usernames, loggedInUsernames));
            }
            for (UserList userList : usersToBeLoaded) {
                if (!userList.getUsers().isEmpty()) {
                    groupList.add(userList);
                    continue;
                }
                emptyGroups.add(userList.getGroup());
            }
            return null;
        });
        if (StringUtils.isNotBlank((CharSequence)macroText)) {
            return macroText;
        }
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        if (!deniedGroups.isEmpty()) {
            contextMap.put("deniedGroups", deniedGroups);
        }
        contextMap.put("showWarning", showWarning);
        contextMap.put("userlists", groupList);
        if (listMode != ListMode.ALL) {
            contextMap.put("online", returnOnlineUsers);
        } else {
            contextMap.put("allUserStatuses", true);
        }
        if (emptyGroups.size() > 0) {
            contextMap.put("emptyGroups", this.createCSVList(emptyGroups));
        }
        try {
            return this.velocityHelperService.getRenderedTemplate("templates/extra/userlister/userlistermacro.vm", contextMap);
        }
        catch (Exception e) {
            logger.error("Error while trying to display UserList!", (Throwable)e);
            return this.getText("userlister.unable.to.render.result", Collections.singletonList(e.toString()));
        }
    }

    private List<String> getUserNames(String groupName, Collection loggedInUsers, ListMode listMode) {
        List<String> usernames = this.getUserNamesByGroup(groupName);
        if (listMode == ListMode.ALL) {
            return usernames;
        }
        return usernames.stream().filter(username -> {
            boolean online = loggedInUsers.contains(username);
            return listMode == ListMode.ONLINE_ONLY == online;
        }).collect(Collectors.toList());
    }

    private List<String> getUserNamesByGroup(String groupName) {
        Pager usernames = null;
        Group group = this.userAccessor.getGroup(groupName);
        if (group != null) {
            usernames = this.userAccessor.getMemberNames(group);
        }
        return usernames == null ? Collections.emptyList() : PagerUtils.toList(usernames);
    }

    private boolean isPeopleDirectoryDisabled() {
        PeopleDirectoryEnabledCondition peopleDirectoryEnabledCondition = new PeopleDirectoryEnabledCondition();
        peopleDirectoryEnabledCondition.setPermissionManager(this.permissionManager);
        return peopleDirectoryEnabledCondition.isPeopleDirectoryDisabled((User)AuthenticatedUserThreadLocal.get());
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private static enum ListMode {
        ALL,
        ONLINE_ONLY,
        OFFLINE_ONLY;

    }
}

