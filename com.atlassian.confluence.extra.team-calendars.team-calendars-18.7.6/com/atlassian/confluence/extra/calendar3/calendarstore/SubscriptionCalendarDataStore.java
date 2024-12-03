/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Throwables
 *  javax.ws.rs.core.UriBuilder
 *  javax.xml.bind.annotation.XmlElement
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ExternalCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.CalendarImportUtil;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.EncryptKeyHolder;
import com.atlassian.confluence.extra.calendar3.util.EncryptionException;
import com.atlassian.confluence.extra.calendar3.util.EncryptionUtils;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlElement;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.component.VEvent;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="subscriptionCalendarDataStore")
public class SubscriptionCalendarDataStore
extends ExternalCalendarDataStore<UrlSubscriptionCalendar>
implements DelegatableCalendarDataStore<UrlSubscriptionCalendar> {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCalendarDataStore.class);
    public static final String SUBSCRIPTION_CALENDAR_DATASTORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.SubscriptionCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "subscription";
    private static final String PROTOCOL_TYPE_HTTP = "http://";
    private static final String PROTOCOL_TYPE_HTTPS = "https://";
    private static final String PROTOCOL_TYPE_WEBCAL = "webcal://";
    private static final String PROTOCOL_TYPE_WEBCALS = "webcals://";
    private final RequestFactory<?> requestFactory;
    private final CalendarImportUtil calendarImportUtil;
    private final EncryptKeyHolder keyHolder;
    private final OutboundWhitelist outboundWhitelist;

    @Autowired
    public SubscriptionCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport RequestFactory<?> requestFactory, CalendarImportUtil calendarImportUtil, EncryptKeyHolder keyHolder, @ComponentImport OutboundWhitelist outboundWhitelist) {
        super(dataStoreCommonPropertyAccessor);
        this.requestFactory = requestFactory;
        this.calendarImportUtil = calendarImportUtil;
        this.keyHolder = keyHolder;
        this.outboundWhitelist = outboundWhitelist;
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new SubCalendarSummary(subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    protected String getStoreKey() {
        return SubscriptionCalendarDataStore.class.getName();
    }

    @Override
    protected UrlSubscriptionCalendar fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UrlSubscriptionCalendar urlSubscriptionCalendar = new UrlSubscriptionCalendar();
        urlSubscriptionCalendar.setId(subCalendarEntity.getID());
        urlSubscriptionCalendar.setName(subCalendarEntity.getName());
        urlSubscriptionCalendar.setDescription(subCalendarEntity.getDescription());
        urlSubscriptionCalendar.setColor(subCalendarEntity.getColour());
        urlSubscriptionCalendar.setCreator(subCalendarEntity.getCreator());
        urlSubscriptionCalendar.setSpaceKey(subCalendarEntity.getSpaceKey());
        urlSubscriptionCalendar.setSpaceName(this.getSpaceName(urlSubscriptionCalendar.getSpaceKey()));
        urlSubscriptionCalendar.setStoreKey(this.getStoreKey());
        urlSubscriptionCalendar.setDisableEventTypes(this.getDisableEventType(subCalendarEntity));
        urlSubscriptionCalendar.setCustomEventTypes(this.getCustomEventType(subCalendarEntity));
        urlSubscriptionCalendar.setCreatedDate(subCalendarEntity.getCreated());
        urlSubscriptionCalendar.setLastUpdateDate(subCalendarEntity.getLastModified());
        urlSubscriptionCalendar.setSourceLocation(this.getSubCalendarEntityPropertyValue(subCalendarEntity, "sourceLocation"));
        urlSubscriptionCalendar.setUserName(this.getSubCalendarEntityPropertyValue(subCalendarEntity, "userName"));
        String encryptPassword = this.getSubCalendarEntityPropertyValue(subCalendarEntity, "password");
        String rawPassword = "";
        try {
            rawPassword = EncryptionUtils.isEncrypted(encryptPassword) ? EncryptionUtils.decrypt(this.keyHolder.getKey(), encryptPassword) : encryptPassword;
        }
        catch (EncryptionException e) {
            LOG.error("Could not decrypt password", (Throwable)e);
        }
        urlSubscriptionCalendar.setPassword(rawPassword);
        return urlSubscriptionCalendar;
    }

    private String getText(String key) {
        return this.getI18NBean().getText(key);
    }

    @Override
    protected SubCalendarEntity toStorageFormat(SubCalendar subCalendar) {
        SubCalendarEntity subCalendarEntity = super.toStorageFormat(subCalendar);
        this.getActiveObjects().delete((RawEntity[])subCalendarEntity.getExtraProperties());
        this.createSubCalendarEntityProperty(subCalendarEntity, "sourceLocation", subCalendar.getSourceLocation());
        if (StringUtils.isNotBlank((CharSequence)subCalendar.getUserName())) {
            this.createSubCalendarEntityProperty(subCalendarEntity, "userName", subCalendar.getUserName());
        }
        if (StringUtils.isNotBlank((CharSequence)subCalendar.getPassword())) {
            String encryptPassword = "";
            try {
                encryptPassword = EncryptionUtils.encrypt(this.keyHolder.getKey(), subCalendar.getPassword());
            }
            catch (EncryptionException e) {
                LOG.error("Could not encrypt password", (Throwable)e);
            }
            this.createSubCalendarEntityProperty(subCalendarEntity, "password", encryptPassword);
        }
        return this.getSubCalendarEntity(subCalendarEntity.getID());
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals((CharSequence)this.getType(), (CharSequence)subCalendar.getType());
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        if (StringUtils.isBlank((CharSequence)subCalendar.getSourceLocation())) {
            this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.blank"));
        } else {
            String userName = subCalendar.getUserName();
            String password = subCalendar.getPassword();
            String modifiedUrl = this.getRequestUrlFromLocationString(subCalendar.getSourceLocation());
            URI locationURI = URI.create(modifiedUrl);
            if (!this.outboundWhitelist.isAllowed(locationURI)) {
                String urlOnly = UriBuilder.fromUri((URI)locationURI).replaceQueryParam("", (Object[])null).build(new Object[0]).toString();
                this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.outboundwhitelist", Arrays.asList(urlOnly)));
                return;
            }
            Request httpRequest = this.requestFactory.createRequest(Request.MethodType.GET, modifiedUrl);
            if (StringUtils.isNotBlank((CharSequence)userName)) {
                httpRequest.addBasicAuthentication(null, userName, password);
            }
            try {
                httpRequest.execute(httpResponse -> {
                    block12: {
                        try {
                            if (httpResponse.getStatusCode() == 404) {
                                this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.notfound"));
                                break block12;
                            }
                            if (httpResponse.getStatusCode() == 403) {
                                this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.notpermitted"));
                                break block12;
                            }
                            if (!httpResponse.isSuccessful()) {
                                this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.cannotread"));
                                break block12;
                            }
                            try (InputStream httpResponseBody = httpResponse.getResponseBodyAsStream();){
                                Ical4jIoUtil.newCalendarBuilder().build(new InputStreamReader(httpResponseBody, Ical4jIoUtil.getContentTypeCharset(httpResponse.getHeader("Content-Type"), "UTF-8")));
                            }
                        }
                        catch (IOException ioe) {
                            this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.ioerror"));
                        }
                        catch (ParserException notIcalendar) {
                            LOG.debug(String.format("Unable to interpret response from %s as iCalendar", subCalendar.getSourceLocation()), (Throwable)notIcalendar);
                            this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.notical"));
                        }
                    }
                });
            }
            catch (ResponseException e) {
                LOG.error("error get response data {}", (Object)e.getMessage());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("error get response data", (Throwable)e);
                }
                this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.subscription.create.notfound"));
            }
        }
    }

    private String getText(String key, List substitutions) {
        return this.getI18NBean().getText(key, substitutions);
    }

    @Override
    protected Calendar getSubCalendarContentInternal(UrlSubscriptionCalendar subCalendar) throws IOException, ParserException {
        try {
            return (Calendar)this.createHttpRequest(subCalendar.getSourceLocation(), subCalendar.getUserName(), subCalendar.getPassword()).executeAndReturn(httpResponse -> {
                if (httpResponse.getStatusCode() == 403) {
                    throw new CalendarException("calendar3.error.subscription.notpermitted", subCalendar.getName(), subCalendar.getId());
                }
                if (!httpResponse.isSuccessful()) {
                    throw new CalendarException("calendar3.error.subscription.cannotread", subCalendar.getName(), subCalendar.getId(), httpResponse.getStatusCode());
                }
                try {
                    Calendar calendar = this.readHttpResponseAsIcalendarText(httpResponse.getResponseBodyAsStream(), Ical4jIoUtil.getContentTypeCharset(httpResponse.getHeader("Content-Type"), "UTF-8"));
                    return this.calendarImportUtil.normalize(calendar, this.getTimeZoneId(calendar, subCalendar), subCalendar.getName(), subCalendar.getDescription());
                }
                catch (IOException | ParserException ex) {
                    throw new ResponseException((Throwable)ex);
                }
            });
        }
        catch (ResponseException ex) {
            Throwables.propagateIfPossible((Throwable)ex.getCause(), ParserException.class, IOException.class);
            throw new RuntimeException(ex);
        }
    }

    private Calendar readHttpResponseAsIcalendarText(InputStream httpResponseInput, String charset) throws IOException, ParserException {
        return Ical4jIoUtil.newCalendarBuilder().build(new InputStreamReader(httpResponseInput, charset));
    }

    private String getRequestUrlFromLocationString(String url) {
        if (url.startsWith(PROTOCOL_TYPE_WEBCAL)) {
            return url.replaceFirst(PROTOCOL_TYPE_WEBCAL, PROTOCOL_TYPE_HTTP);
        }
        if (url.startsWith(PROTOCOL_TYPE_WEBCALS)) {
            return url.replaceFirst(PROTOCOL_TYPE_WEBCALS, PROTOCOL_TYPE_HTTPS);
        }
        return url;
    }

    private Request<?, ?> createHttpRequest(String sourceLocation, String userName, String password) {
        String modifiedUrl = this.getRequestUrlFromLocationString(sourceLocation);
        Request httpRequest = this.requestFactory.createRequest(Request.MethodType.GET, modifiedUrl);
        if (StringUtils.isNotBlank((CharSequence)userName)) {
            httpRequest.addBasicAuthentication(null, userName, password);
        }
        return httpRequest;
    }

    @Override
    public SubCalendarEvent transform(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        SubCalendarEvent subCalendarEvent = super.transform(toBeTransformed, raw);
        SubCalendarEventTransformerFactory.TransformParameters transformParameters = new SubCalendarEventTransformerFactory.TransformParameters(){

            @Override
            public VEvent getRawEvent() {
                return raw;
            }

            @Override
            public boolean isReadOnly() {
                return !toBeTransformed.isEditable();
            }
        };
        Object subCalendarType = transformParameters.getRawEvent().getProperty("X-CONFLUENCE-SUBCALENDAR-TYPE");
        if (subCalendarType != null && CalendarUtil.isJiraSubCalendarType(((Content)subCalendarType).getValue())) {
            return subCalendarEvent;
        }
        return this.getSubCalendarEventTransformerFactory().getDescriptionHtmlCleaningTransformer().transform(subCalendarEvent, currentUser, transformParameters);
    }

    private String getTimeZoneId(Calendar calendar, UrlSubscriptionCalendar subCalendar) {
        String timeZoneId = this.calendarImportUtil.getTimeZoneFromCalendar(calendar);
        if (timeZoneId == null) {
            LOG.warn("Could not determine calendar timezone for calendar: " + subCalendar.getName() + ". Timezone set to: " + timeZoneId);
            return "Etc/UTC";
        }
        timeZoneId = this.getJodaIcal4jTimeZoneMapper().getTimeZoneIdForAlias(timeZoneId);
        if (timeZoneId == null) {
            LOG.warn("Could not determine calendar timezone for calendar: " + subCalendar.getName() + ". Timezone set to: " + timeZoneId);
            return "Etc/UTC";
        }
        return timeZoneId;
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    public static class UrlSubscriptionCalendar
    extends PersistedSubCalendar
    implements Cloneable {
        private static final Logger LOG = LoggerFactory.getLogger(UrlSubscriptionCalendar.class);
        private String id;
        private String typeName;
        private String creator;
        private String spaceName;

        @Override
        @XmlElement
        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        @XmlElement
        public String getType() {
            return SubscriptionCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        @XmlElement
        public String getCreator() {
            return this.creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        @Override
        @XmlElement
        public String getSpaceName() {
            return this.spaceName;
        }

        public void setSpaceName(String spaceName) {
            this.spaceName = spaceName;
        }

        @Override
        @XmlElement
        public String getSourceLocation() {
            return super.getSourceLocation();
        }

        @Override
        @XmlElement
        public String getUserName() {
            return super.getUserName();
        }

        @Override
        @XmlElement
        public boolean isWatchable() {
            return false;
        }

        @Override
        @XmlElement
        public boolean isRestrictable() {
            return true;
        }

        @Override
        @XmlElement
        public boolean isEventInviteesSupported() {
            return false;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = super.toJson();
            try {
                thisObject.put("sourceLocation", (Object)this.getSourceLocation());
                thisObject.put("userName", (Object)this.getUserName());
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
            return thisObject;
        }

        @Override
        public Object clone() {
            UrlSubscriptionCalendar copy = new UrlSubscriptionCalendar();
            copy.setId(this.getId());
            copy.setName(this.getName());
            copy.setDescription(this.getDescription());
            copy.setColor(this.getColor());
            copy.setCreator(this.getCreator());
            copy.setSpaceKey(this.getSpaceKey());
            copy.setSpaceName(this.getSpaceName());
            copy.setTimeZoneId(this.getTimeZoneId());
            copy.setDisableEventTypes(this.getDisableEventTypes());
            copy.setCustomEventTypes(this.getCustomEventTypes());
            copy.setSourceLocation(this.getSourceLocation());
            copy.setUserName(this.getUserName());
            copy.setPassword(this.getPassword());
            return copy;
        }
    }
}

