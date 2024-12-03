/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.eventmacro;

import com.atlassian.botocss.Botocss;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.eventmacro.EventMacroManager;
import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMacro
implements Macro {
    private final EventMacroManager eventMacroManager;
    private final SettingsManager settingsManager;
    private final PermissionManager permissionManager;
    private final VelocityHelperService velocityHelperService;
    private final I18nResolver i18nResolver;
    private static final String PARAMETER_HIDE_REPLIES = "hide_replies";
    private static final String PARAMETER_ANONYMOUS_ALLOWED = "anon_allowed";
    private static final String PARAMETER_REPLY_LIMIT = "limit";
    private static final String PARAMETER_WIDTH = "width";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_BUTTON = "button";
    private static final String PARAMETER_URL = "url";
    private static final String PARAMETER_OCCURRENCE = "id";
    private static final String PARAMETER_GUESTS = "allow_guests";
    private static final String PARAMETER_HAS_COMMENTS = "hasComments";
    private static final String PARAMETER_CUSTOM_COLUMNS = "custom_columns";
    private static final String PARAMETER_CUSTOM_CHECKBOXES = "custom_checkboxes";
    private static final String PARAMETER_ENABLE_WAITING_LIST = "enable_waiting_list";
    private static final Logger log = LoggerFactory.getLogger(EventMacro.class);

    public EventMacro(SettingsManager settingsManager, EventMacroManager eventMacroManager, PermissionManager permissionManager, VelocityHelperService velocityHelperService, I18nResolver i18nResolver) {
        this.settingsManager = settingsManager;
        this.eventMacroManager = eventMacroManager;
        this.permissionManager = permissionManager;
        this.velocityHelperService = velocityHelperService;
        this.i18nResolver = i18nResolver;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        LinkedHashSet<String> customColumnsSet = null;
        LinkedHashSet<String> customCheckboxesSet = null;
        String eventTitle = parameters.get("0");
        String title = (String)StringUtils.defaultIfBlank((CharSequence)parameters.get(PARAMETER_TITLE), (CharSequence)eventTitle);
        Boolean hideReplies = Boolean.valueOf(parameters.get(PARAMETER_HIDE_REPLIES));
        Boolean allowAnonymous = Boolean.valueOf(parameters.get(PARAMETER_ANONYMOUS_ALLOWED));
        int replyLimit = NumberUtils.toInt((String)parameters.get(PARAMETER_REPLY_LIMIT), (int)-1);
        String width = StringUtils.defaultString((String)parameters.get(PARAMETER_WIDTH), (String)"99%");
        String customButton = StringUtils.defaultString((String)parameters.get(PARAMETER_BUTTON), (String)this.i18nResolver.getText("calendar3.event.attend"));
        String hasUrl = parameters.get(PARAMETER_URL);
        String allowGuests = parameters.get(PARAMETER_GUESTS);
        String hasComments = parameters.get(PARAMETER_HAS_COMMENTS);
        String customColumns = parameters.get(PARAMETER_CUSTOM_COLUMNS);
        String customCheckboxes = parameters.get(PARAMETER_CUSTOM_CHECKBOXES);
        String rawOccurrence = parameters.get(PARAMETER_OCCURRENCE);
        String occurrenceHash = this.calculateOccurrenceHash(rawOccurrence, context.getPageContext());
        Boolean waitingListEnabled = Boolean.valueOf(parameters.get(PARAMETER_ENABLE_WAITING_LIST));
        ContentEntityObject content = context.getEntity();
        if (content instanceof Comment) {
            return RenderUtils.blockError((String)"The Event macro cannot be displayed in comments", (String)"");
        }
        if (allowGuests == null) {
            allowGuests = "true";
        }
        if (hasComments == null) {
            hasComments = "true";
        }
        if (customColumns != null) {
            customColumnsSet = new LinkedHashSet<String>(Arrays.asList(customColumns.trim().split("\\s*,\\s*")));
        }
        if (customCheckboxes != null) {
            customCheckboxesSet = new LinkedHashSet<String>(Arrays.asList(customCheckboxes.trim().split("\\s*,\\s*")));
        }
        this.persistMacroParameters(title, hideReplies, allowAnonymous, replyLimit, waitingListEnabled, occurrenceHash, content);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean isAdmin = this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)context.getEntity());
        List<Reply> replyList = this.eventMacroManager.getAndExpandReplyList(content, replyLimit, waitingListEnabled, occurrenceHash);
        ImmutableList waitingList = waitingListEnabled != false ? this.eventMacroManager.getReplyList(content, occurrenceHash, EventMacroManager.ReplyType.WAITING_LIST) : ImmutableList.of();
        Map<String, Integer> customCheckboxTotals = null;
        if (customCheckboxesSet != null) {
            customCheckboxTotals = this.calculateCustomCheckboxTotals(customCheckboxesSet, replyList);
        }
        int numRespondents = replyList.size();
        int numGuests = this.calculateNumberOfGuests(replyList);
        int totalAttendees = numRespondents + numGuests;
        boolean currentlyFull = replyLimit != -1 && this.eventMacroManager.getNumResponders(content, occurrenceHash) >= replyLimit;
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("currentUser", AuthenticatedUserThreadLocal.get());
        contextMap.put("eventTitle", StringUtils.defaultIfBlank((CharSequence)title, (CharSequence)eventTitle));
        contextMap.put(PARAMETER_WIDTH, width);
        contextMap.put("replyList", replyList);
        contextMap.put("waitingList", waitingList);
        contextMap.put("waitingListSize", waitingList.size());
        if (replyLimit > 0) {
            contextMap.put("spacesAvailable", replyLimit - replyList.size());
        }
        contextMap.put("replyLimit", replyLimit);
        contextMap.put("attendeeCount", replyList.size());
        contextMap.put("customButton", customButton);
        contextMap.put("eventManager", this.eventMacroManager);
        contextMap.put("content", content);
        contextMap.put("isAdmin", isAdmin);
        contextMap.put("hasUrl", Boolean.valueOf(hasUrl));
        contextMap.put("isEmailPrivateOrMasked", this.areEmailAddressesPublic());
        contextMap.put("shouldHideReplies", hideReplies);
        contextMap.put("canReply", this.canUserReply(allowAnonymous, user));
        contextMap.put("currentlyFull", currentlyFull);
        contextMap.put("occurrence", occurrenceHash);
        contextMap.put("customColumnsList", customColumnsSet);
        contextMap.put("customCheckboxesList", customCheckboxesSet);
        contextMap.put("allowGuests", Boolean.valueOf(allowGuests));
        contextMap.put(PARAMETER_HAS_COMMENTS, Boolean.valueOf(hasComments));
        contextMap.put("numRespondents", numRespondents);
        contextMap.put("numGuests", numGuests);
        contextMap.put("totalAttendees", totalAttendees);
        contextMap.put("customCheckboxTotals", customCheckboxTotals);
        contextMap.put("currentUserName", AuthenticatedUserThreadLocal.getUsername());
        contextMap.put("emailReplyUrl", this.settingsManager.getGlobalSettings().getBaseUrl() + context.getEntity().getUrlPath() + "#event-header-" + occurrenceHash);
        contextMap.put("waitingListEnabled", waitingListEnabled);
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request != null) {
            contextMap.put(PARAMETER_OCCURRENCE, occurrenceHash);
        }
        try {
            if (this.isPrint(request, context)) {
                return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/eventmacro/templates/eventprint.vm", contextMap);
            }
            if ("email".equals(context.getOutputType())) {
                return Botocss.inject((String)this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/eventmacro/templates/eventemail.vm", contextMap), (String[])new String[]{this.getCss("com/atlassian/confluence/extra/calendar3/eventmacro/css/eventmail.css")});
            }
            return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/eventmacro/templates/eventmacro.vm", contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to display Team Calendars Event macro!", (Throwable)e);
            throw new MacroExecutionException(e.getMessage());
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private String getCss(String path) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);){
            String string = IOUtils.toString((InputStream)is, (String)"UTF-8");
            return string;
        }
    }

    private void persistMacroParameters(String title, boolean hideReplies, boolean allowAnonymous, int replyLimit, boolean waitingListEnabled, String occurrence, ContentEntityObject content) {
        this.eventMacroManager.setEventTitle(content, occurrence, title);
        this.eventMacroManager.setAllowAnonymous(content, occurrence, allowAnonymous);
        this.eventMacroManager.setReplyLimit(content, occurrence, replyLimit);
        this.eventMacroManager.setHideReplies(content, occurrence, hideReplies);
        this.eventMacroManager.setWaitingListEnabled(content, occurrence, waitingListEnabled);
    }

    private boolean canUserReply(boolean allowAnonymous, ConfluenceUser user) {
        return user != null || allowAnonymous;
    }

    private boolean areEmailAddressesPublic() {
        return !"email.address.public".equals(this.settingsManager.getGlobalSettings().getEmailAddressVisibility());
    }

    private String calculateOccurrenceHash(String occurrence, PageContext pageContext) {
        if (occurrence == null || occurrence.isEmpty() || occurrence.equals("default")) {
            if (this.checkAndAddDefaultOccurrence(pageContext)) {
                return RenderUtils.blockError((String)"Event macro cannot render because the default \"id\" parameter is already in use in this page/blog. Please specify a unique \"id\" in the Macro Browser.", (String)"");
            }
            occurrence = "default";
        }
        int hash = 0;
        for (int i = 0; i < occurrence.length(); ++i) {
            hash += occurrence.charAt(i) * (i + 1);
        }
        return Integer.toString(Math.abs(hash));
    }

    private int calculateNumberOfGuests(List<Reply> replyList) {
        int numGuests = 0;
        for (Reply reply : replyList) {
            numGuests += reply.getGuests();
        }
        return numGuests;
    }

    private Map<String, Integer> calculateCustomCheckboxTotals(Set<String> customCheckboxesSet, List<Reply> replyList) {
        HashMap<String, Integer> customCheckboxTotals = new HashMap<String, Integer>();
        for (String customCheckbox : customCheckboxesSet) {
            customCheckboxTotals.put(customCheckbox, 0);
            for (Reply reply : replyList) {
                Boolean value = reply.getCustomCheckboxes().get(customCheckbox);
                if (value == null || !value.booleanValue()) continue;
                Integer tally = (Integer)customCheckboxTotals.get(customCheckbox);
                tally = tally + 1;
                customCheckboxTotals.put(customCheckbox, tally);
            }
        }
        return customCheckboxTotals;
    }

    private boolean isPrint(HttpServletRequest request, ConversionContext conversionContext) {
        String type;
        String decorator;
        if (request != null && StringUtils.isNotBlank((CharSequence)(decorator = request.getParameter("decorator"))) && decorator.equals("printable")) {
            return true;
        }
        return conversionContext != null && ("pdf".equals(type = conversionContext.getOutputType()) || "word".equals(type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean checkAndAddDefaultOccurrence(PageContext pageContext) {
        EventMacro eventMacro = this;
        synchronized (eventMacro) {
            if (pageContext.getParam((Object)"event.occurrence") != null) {
                return true;
            }
            pageContext.addParam((Object)"event.occurrence", (Object)Boolean.TRUE);
            return false;
        }
    }
}

