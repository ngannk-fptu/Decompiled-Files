/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.xml.bind.annotation.XmlElement
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="localCalendarDataStore")
public class LocalCalendarDataStore
extends BaseCacheableCalendarDataStore<LocalSubCalendar>
implements RefreshableCalendarDataStore<LocalSubCalendar>,
DelegatableCalendarDataStore<LocalSubCalendar> {
    public static final String SUB_CALENDAR_TYPE = "local";

    @Autowired
    public LocalCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        super(dataStoreCommonPropertyAccessor);
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new SubCalendarSummary(subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    public SubCalendarEvent transform(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
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
        SubCalendarEventTransformerFactory subCalendarEventTransformerFactory = this.getSubCalendarEventTransformerFactory();
        return subCalendarEventTransformerFactory.getDescriptionHtmlCleaningTransformer().transform(subCalendarEventTransformerFactory.getNoInviteesTransformer().transform(super.transform(toBeTransformed, raw), currentUser, transformParameters), currentUser, transformParameters);
    }

    @Override
    protected String getStoreKey() {
        return "com.atlassian.confluence.extra.calendar3.calendarstore.LocalCalendarDataStore";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected LocalSubCalendar fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"LocalCalendarDataStore.fromStorageFormat()");
        try {
            LocalSubCalendar localSubCalendar = new LocalSubCalendar();
            localSubCalendar.setId(subCalendarEntity.getID());
            localSubCalendar.setName(subCalendarEntity.getName());
            localSubCalendar.setDescription(subCalendarEntity.getDescription());
            localSubCalendar.setColor(subCalendarEntity.getColour());
            localSubCalendar.setCreator(subCalendarEntity.getCreator());
            localSubCalendar.setSpaceKey(subCalendarEntity.getSpaceKey());
            localSubCalendar.setSpaceName(this.getSpaceName(localSubCalendar.getSpaceKey()));
            localSubCalendar.setStoreKey(this.getStoreKey());
            localSubCalendar.setDisableEventTypes(this.getDisableEventType(subCalendarEntity));
            localSubCalendar.setCustomEventTypes(this.getCustomEventType(subCalendarEntity));
            localSubCalendar.setCreatedDate(subCalendarEntity.getCreated());
            localSubCalendar.setLastUpdateDate(subCalendarEntity.getLastModified());
            localSubCalendar.setTimeZoneId(subCalendarEntity.getTimeZoneId());
            if (this.getJodaIcal4jTimeZoneMapper().isTimeZoneIdAnAlias(localSubCalendar.getTimeZoneId())) {
                localSubCalendar.setTimeZoneId(this.getJodaIcal4jTimeZoneMapper().getTimeZoneIdForAlias(localSubCalendar.getTimeZoneId()));
            }
            LocalSubCalendar localSubCalendar2 = localSubCalendar;
            return localSubCalendar2;
        }
        finally {
            UtilTimerStack.pop((String)"LocalCalendarDataStore.fromStorageFormat()");
        }
    }

    protected String getText(String key) {
        return this.getI18NBean().getText(key);
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        if (StringUtils.isBlank(subCalendar.getTimeZoneId()) || null == this.getJodaIcal4jTimeZoneMapper().getTimeZoneIdForAlias(subCalendar.getTimeZoneId())) {
            this.addFieldError(fieldErrors, "timeZoneId", this.getText("calendar3.error.invalidfield"));
        }
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    @Override
    public boolean hasReloadEventsPrivilege(LocalSubCalendar subCalendar, ConfluenceUser user) {
        return this.hasViewEventPrivilege(subCalendar.getId(), user);
    }

    @Override
    public void refresh(LocalSubCalendar localSubCalendar) {
    }

    public static class LocalSubCalendar
    extends LocallyManagedSubCalendar
    implements Cloneable {
        private String id;
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
            return LocalCalendarDataStore.SUB_CALENDAR_TYPE;
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
        public boolean isWatchable() {
            return true;
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
        public Object clone() {
            LocalSubCalendar copy = new LocalSubCalendar();
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
            return copy;
        }
    }
}

