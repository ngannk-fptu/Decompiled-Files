/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.PrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarSubscribed;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.util.PairType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPrivateCalendarUrlManager
implements PrivateCalendarUrlManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPrivateCalendarUrlManager.class);
    public static final ConfluenceBandanaContext USER_MAP_CONTEXT = new ConfluenceBandanaContext(DigestUtils.sha1Hex(DefaultPrivateCalendarUrlManager.class.getName()) + ".userMap");
    public static final ConfluenceBandanaContext TOKEN_MAP_CONTEXT = new ConfluenceBandanaContext(DigestUtils.sha1Hex(DefaultPrivateCalendarUrlManager.class.getName()) + ".tokenMap");
    private final BandanaManager bandanaManager;
    private final UserAccessor userAccessor;
    private final CalendarManager calendarManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultPrivateCalendarUrlManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport UserAccessor userAccessor, CalendarManager calendarManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.bandanaManager = bandanaManager;
        this.userAccessor = userAccessor;
        this.calendarManager = calendarManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized String getTokenFor(ConfluenceUser user, String calendarId) {
        if (!this.calendarManager.hasSubCalendar(calendarId)) {
            return null;
        }
        LinkedList<String> tokens = (LinkedList<String>)this.bandanaManager.getValue((BandanaContext)USER_MAP_CONTEXT, user.getKey().toString());
        if (tokens == null) {
            tokens = new LinkedList<String>();
        }
        for (String token : tokens) {
            PairType calendarIdUserPair = this.getCalendarIdUserPairType(token);
            if (null == calendarIdUserPair || !StringUtils.equals((String)((Object)calendarIdUserPair.getKey()), calendarId)) continue;
            return token;
        }
        String newToken = DefaultSecureTokenGenerator.getInstance().generateToken();
        tokens.add(newToken);
        String userId = user.getKey().toString();
        this.bandanaManager.setValue((BandanaContext)TOKEN_MAP_CONTEXT, newToken, (Object)new PairType((Serializable)((Object)calendarId), (Serializable)((Object)userId)));
        this.bandanaManager.setValue((BandanaContext)USER_MAP_CONTEXT, userId, tokens);
        this.eventPublisher.publish((Object)new SubCalendarSubscribed(this, user));
        return newToken;
    }

    @Override
    public ConfluenceUser getUserFor(String token) {
        PairType calendarIdUserPair = this.getCalendarIdUserPairType(token);
        if (calendarIdUserPair == null) {
            return null;
        }
        return this.userAccessor.getUserByKey(new UserKey((String)((Object)calendarIdUserPair.getValue())));
    }

    @Override
    public String getCalendarId(String token) {
        PairType calendarIdUserPair = this.getCalendarIdUserPairType(token);
        if (calendarIdUserPair == null) {
            return null;
        }
        String userId = (String)((Object)calendarIdUserPair.getValue());
        if (this.isUserDeactivated(userId)) {
            LOG.warn(String.format("Attempt to access calendar subscription for deactivated user: %s", userId));
            return null;
        }
        return (String)((Object)calendarIdUserPair.getKey());
    }

    private boolean isUserDeactivated(String userId) {
        return this.userAccessor.isDeactivated((User)this.userAccessor.getUserByKey(new UserKey(userId)));
    }

    private PairType getCalendarIdUserPairType(String token) {
        return (PairType)this.bandanaManager.getValue((BandanaContext)TOKEN_MAP_CONTEXT, token);
    }

    @Override
    public synchronized void resetPrivateUrlsFor(ConfluenceUser user, String subCalendarId) {
        List tokens = (List)this.bandanaManager.getValue((BandanaContext)USER_MAP_CONTEXT, user.getKey().toString());
        if (tokens != null && !tokens.isEmpty()) {
            Collection tokensToRemove = Collections2.filter((Collection)tokens, token -> {
                PairType calendarIdUserPair = this.getCalendarIdUserPairType((String)token);
                return null != calendarIdUserPair && StringUtils.equals((String)((Object)calendarIdUserPair.getKey()), subCalendarId);
            });
            for (String tokenToRemove : tokensToRemove) {
                this.bandanaManager.removeValue((BandanaContext)TOKEN_MAP_CONTEXT, tokenToRemove);
            }
            if (new HashSet(tokens).equals(new HashSet(tokensToRemove))) {
                this.bandanaManager.removeValue((BandanaContext)USER_MAP_CONTEXT, user.getKey().toString());
            }
        }
    }

    @Override
    public synchronized void resetAllPrivateUrls() {
        for (String token : this.bandanaManager.getKeys((BandanaContext)TOKEN_MAP_CONTEXT)) {
            this.bandanaManager.removeValue((BandanaContext)TOKEN_MAP_CONTEXT, token);
        }
        for (String user : this.bandanaManager.getKeys((BandanaContext)USER_MAP_CONTEXT)) {
            this.bandanaManager.removeValue((BandanaContext)USER_MAP_CONTEXT, user);
        }
    }
}

