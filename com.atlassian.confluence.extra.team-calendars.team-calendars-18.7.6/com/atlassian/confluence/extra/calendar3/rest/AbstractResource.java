/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.StreamingOutput
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.extra.calendar3.util.JSONUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResource {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);
    public static final String CHARSET_DEFAULT = "UTF-8";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final int USER_PROFILE_PIC_DOWNLOAD_PATH_CACHE_MAX_ENTRIES = Integer.getInteger("com.atlassian.confluence.extra.calendar3.user.profile.download.path.max.entries", 100);
    protected final I18NBeanFactory i18NBeanFactory;
    protected final LocaleManager localeManager;
    protected final CalendarManager calendarManager;
    protected final UserAccessor userAccessor;
    protected final CalendarPermissionManager calendarPermissionManager;

    protected AbstractResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.userAccessor = userAccessor;
    }

    protected String getText(String s) {
        return this.getI18nBean().getText(s);
    }

    protected String getText(String i18nKey, List substitutions) {
        return this.getI18nBean().getText(i18nKey, substitutions);
    }

    protected I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale());
    }

    protected Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    protected Response getResponseError(Exception e, String loggedMessage, String ... errorMessages) {
        Response response = Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)this.toJsonArray(StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage()).toString()).build();
        LOG.error(loggedMessage, (Throwable)e);
        throw new WebApplicationException(response);
    }

    protected Response getResponseError(CalendarException e, String loggedMessage, String ... errorMessages) {
        LOG.debug(loggedMessage, (Throwable)e);
        if (e.isCustomError() && errorMessages.length == 1) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)this.toJsonArray(e.getStatus() == CalendarException.StatusError.JQL_WRONG.getStatusNum() ? "error-jql-wrong" : "html-error-type", errorMessages[0]).toString()).build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)this.toJsonArray(errorMessages).toString()).build();
    }

    protected Response createErrorResponse(Map<String, List<String>> fieldErrorsMap) {
        GeneralResponseEntity generalResponseEntity = new GeneralResponseEntity();
        generalResponseEntity.setSuccess(false);
        generalResponseEntity.setFieldErrors(fieldErrorsMap.entrySet().stream().map(entry -> new GeneralResponseEntity.FieldError((String)entry.getKey(), (List)entry.getValue())).collect(Collectors.toList()));
        return Response.status((int)200).entity((Object)generalResponseEntity).build();
    }

    protected String getNextSubCalendarColor(String color, List<String> subCalendarColors) {
        int indexOfColor = subCalendarColors.indexOf(StringUtils.defaultString(color));
        return -1 == indexOfColor ? subCalendarColors.get(0) : subCalendarColors.get((indexOfColor + 1) % subCalendarColors.size());
    }

    public Set<ConfluenceUser> getIdsAsUsers(Set<String> userIds) {
        return userIds.stream().map(this::getUserById).collect(Collectors.toSet());
    }

    public ConfluenceUser getUserById(String userId) {
        return this.userAccessor.getUserByKey(new UserKey(userId));
    }

    protected JSONArray toJsonArray(String ... errorMessages) {
        return JSONUtil.toJsonArray(errorMessages);
    }

    protected JSONArray toJsonArray(JsonSerializable ... jsonSerializables) {
        return JSONUtil.toJsonArray(jsonSerializables);
    }

    public static class JsonSerializablesStreamingOutput
    implements StreamingOutput {
        private final Collection<? extends JsonSerializable> jsonSerializables;
        private final String encodingCharacterSet;

        public JsonSerializablesStreamingOutput(Collection<? extends JsonSerializable> jsonSerializables, String encodingCharacterSet) {
            this.jsonSerializables = jsonSerializables;
            this.encodingCharacterSet = StringUtils.defaultIfEmpty(encodingCharacterSet, AbstractResource.CHARSET_DEFAULT);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, this.encodingCharacterSet);
            UtilTimerStack.push((String)"JsonSerializablesStreamingOutput");
            try {
                int jsonSerializableIdx = 0;
                int nJsonSerializables = this.jsonSerializables.size();
                outputStreamWriter.write(91);
                for (JsonSerializable jsonSerializable : this.jsonSerializables) {
                    jsonSerializable.toJson().write((Writer)outputStreamWriter);
                    if (++jsonSerializableIdx >= nJsonSerializables) continue;
                    outputStreamWriter.write(44);
                }
                outputStreamWriter.write(93);
                outputStreamWriter.flush();
            }
            catch (JSONException jsonError) {
                throw new IOException(jsonError);
            }
            catch (IOException ex) {
                LOG.error("Could not stream TC JSON objects to client");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Number of object to be serialize is {}", (Object)this.jsonSerializables.size());
                    LOG.debug("Could not stream TC JSON objects to client. Exception detail is ", (Throwable)ex);
                }
            }
            finally {
                UtilTimerStack.pop((String)"JsonSerializablesStreamingOutput");
            }
        }
    }

    public static class UserToPermittedUserTransformer
    implements Function<ConfluenceUser, SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser> {
        private static final String CACHE_NAME = UserToPermittedUserTransformer.class.getName();
        private final UserAccessor userAccessor;
        private final SettingsManager settingsManager;
        private final Cache<String, String> cache;

        public UserToPermittedUserTransformer(UserAccessor userAccessor, SettingsManager settingsManager, CacheFactory cacheFactory) {
            this.userAccessor = userAccessor;
            this.settingsManager = settingsManager;
            CacheSettings cacheSettings = new CacheSettingsBuilder().maxEntries(USER_PROFILE_PIC_DOWNLOAD_PATH_CACHE_MAX_ENTRIES).replicateViaInvalidation().replicateAsynchronously().build();
            this.cache = cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
        }

        public SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser apply(ConfluenceUser user) {
            String userProfilePictureDownloadPath = (String)this.cache.get((Object)user.getKey().getStringValue(), () -> this.userAccessor.getUserProfilePicture((User)user).getDownloadPath());
            return new SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser(user.getKey().toString(), user.getName(), user.getFullName(), this.settingsManager.getGlobalSettings().getBaseUrl() + userProfilePictureDownloadPath);
        }
    }
}

