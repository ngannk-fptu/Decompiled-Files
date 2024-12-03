/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.WebResourceDependentSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.ExternalInvitee;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.ResourceDataSourceAware;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.notification.DefaultCalendarNotificationManager;
import com.atlassian.confluence.extra.calendar3.notification.ProfilePictureConst;
import com.atlassian.confluence.extra.calendar3.notification.ResourceDataHandler;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class SubCalendarEventSupportingInviteesTransformer
extends WebResourceDependentSubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> {
    private final SettingsManager settingsManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final ResourceDataHandler resourceDataHandler;

    public SubCalendarEventSupportingInviteesTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, SettingsManager settingsManager, WebResourceUrlProvider webResourceUrlProvider, ResourceDataHandler resourceDataHandler) {
        super(localeManager, i18NBeanFactory, buildInformationManager);
        this.settingsManager = settingsManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.resourceDataHandler = resourceDataHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SubCalendarEvent transform(SubCalendarEvent toTransform, ConfluenceUser forUser, SubCalendarEventTransformerFactory.TransformParameters transformParameters) {
        String methodSignature = "SubCalendarEventSupportingInviteesTransformer.transform(SubCalendarEvent, SubCalendarEventTransformerFactory.TransformParameters)";
        UtilTimerStack.push((String)methodSignature);
        try {
            Set<Invitee> invitees = toTransform.getInvitees();
            String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
            StringBuilder stringBuilder = new StringBuilder();
            boolean isResourceDataSourceAware = toTransform instanceof ResourceDataSourceAware;
            if (null == invitees || invitees.isEmpty()) {
                if (isResourceDataSourceAware) {
                    DefaultCalendarNotificationManager.IdentifiableContentDataHandler dataHandler = this.resourceDataHandler.createDefaultProfilePictureDataHandler(null);
                    ((ResourceDataSourceAware)((Object)toTransform)).setResourceDataHandler(dataHandler);
                } else {
                    String anonymousAvatarUrl = stringBuilder.append(baseUrl).append(ProfilePictureConst.ANONYMOUS_PROFILE.getDownloadPath()).toString();
                    toTransform.setIconUrl(anonymousAvatarUrl);
                    toTransform.setMediumIconUrl(anonymousAvatarUrl);
                }
            } else if (invitees.size() == 1) {
                Invitee onlyInvitee = invitees.iterator().next();
                if (isResourceDataSourceAware) {
                    DefaultCalendarNotificationManager.IdentifiableContentDataHandler dataHandler = this.resourceDataHandler.createAvatarDataHandler(onlyInvitee.getName(), null);
                    ((ResourceDataSourceAware)((Object)toTransform)).setResourceDataHandler(dataHandler);
                }
                String avatarUrl = onlyInvitee.getAvatarIconUrl();
                toTransform.setName(this.getInviteeDisplayNamesAsEventName(invitees));
                toTransform.setIconUrl(avatarUrl);
                toTransform.setMediumIconUrl(avatarUrl);
                if (onlyInvitee instanceof ConfluenceUserInvitee) {
                    toTransform.setIconLink(stringBuilder.append(baseUrl).append("/display/~").append(GeneralUtil.urlEncode((String)onlyInvitee.getId())).toString());
                } else if (onlyInvitee instanceof ExternalInvitee) {
                    toTransform.setIconLink(stringBuilder.append(baseUrl).append("mailto:").append(onlyInvitee.getEmail()).toString());
                }
                if (!StringUtils.isBlank(toTransform.getDescription())) {
                    stringBuilder.setLength(0);
                    stringBuilder.append(onlyInvitee.getDisplayName());
                    this.appendDescription(toTransform, stringBuilder);
                    toTransform.setShortName(stringBuilder.toString());
                }
            } else {
                toTransform.setName(this.getInviteeDisplayNamesAsEventName(invitees));
                List<String> inviteeFirstNames = this.getInviteeFirstNames(forUser, invitees);
                stringBuilder.setLength(0);
                stringBuilder.append(StringUtils.join(inviteeFirstNames.subList(0, inviteeFirstNames.size() - 1), ", ")).append(" & ").append(inviteeFirstNames.get(inviteeFirstNames.size() - 1));
                this.appendDescription(toTransform, stringBuilder);
                toTransform.setShortName(stringBuilder.toString());
                String displayName = this.getDisplayName(forUser, invitees.iterator().next().getDisplayName());
                toTransform.setNameCollapseIndex(displayName.length());
                stringBuilder.setLength(0);
                toTransform.setNameCollapseText(this.getText(forUser, stringBuilder.append("calendar3.moreinvitees.").append(invitees.size() > 2 ? "plural" : "singular").toString(), Arrays.asList(invitees.size() - 1)));
                toTransform.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/people_multiple_48.png", UrlMode.ABSOLUTE));
                toTransform.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/people_multiple_24.png", UrlMode.ABSOLUTE));
            }
            SubCalendarEvent subCalendarEvent = toTransform;
            return subCalendarEvent;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private String getDisplayName(ConfluenceUser forUser, String displayName) {
        return StringUtils.defaultIfEmpty(displayName, this.getText(forUser, "calendar3.error.unknownuser"));
    }

    private String getInviteeDisplayNamesAsEventName(Set<Invitee> invitees) {
        return StringUtils.join(Collections2.transform(invitees, Invitee::getDisplayName), ", ");
    }

    protected List<String> getInviteeFirstNames(ConfluenceUser forUser, Set<Invitee> invitees) {
        return new ArrayList<String>(Collections2.transform((Collection)Collections2.transform(invitees, invitee -> this.getDisplayName(forUser, StringUtils.trim(invitee.getDisplayName()))), displayName -> {
            int indexOfWhiteSpace = StringUtils.trim(displayName).indexOf(32);
            return indexOfWhiteSpace > 0 ? displayName.substring(0, indexOfWhiteSpace) : displayName;
        }));
    }

    protected void appendDescription(SubCalendarEvent transformed, StringBuilder stringBuilder) {
        if (transformed.getEventType().equals("other") || transformed.getEventType().equals("custom")) {
            return;
        }
        if (!StringUtils.isBlank(transformed.getDescription())) {
            stringBuilder.append(": ").append(GeneralUtil.shortenString((String)StringUtils.trim(transformed.getDescription()), (int)30));
        }
    }
}

