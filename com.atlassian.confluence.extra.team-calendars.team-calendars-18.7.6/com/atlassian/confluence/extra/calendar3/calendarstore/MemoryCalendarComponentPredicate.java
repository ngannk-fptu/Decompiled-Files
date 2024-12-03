/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavProperties;
import com.atlassian.confluence.extra.calendar3.caldav.filter.AndFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import com.atlassian.confluence.extra.calendar3.caldav.filter.NotFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.OrFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PresenceFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.SupportedPropertyFilterBaseTransformer;
import com.atlassian.confluence.extra.calendar3.caldav.filter.TimeRangeFilter;
import com.google.common.annotations.VisibleForTesting;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.calendar.PropertyIndex;

class MemoryCalendarComponentPredicate
implements Predicate<CalendarComponent> {
    private final FilterBase filterBase;
    private static final Map<Class<? extends FilterBase>, CalendarComponentPredicate> predicateMap = new HashMap<Class<? extends FilterBase>, CalendarComponentPredicate>();

    @VisibleForTesting
    public static CalendarComponentPredicate getPredicate(Class<? extends FilterBase> clazz) {
        return predicateMap.get(clazz);
    }

    public MemoryCalendarComponentPredicate(FilterBase filterBase) {
        Objects.requireNonNull(filterBase);
        this.filterBase = filterBase;
    }

    @Override
    public boolean test(CalendarComponent calendarComponent) {
        Objects.requireNonNull(this.filterBase);
        return this.test(this.filterBase, calendarComponent);
    }

    private static Predicate<Object> getItemPredicate(PropertyValueFilter.MatchingConfiguration matchingConfiguration, String matchingValue) {
        Predicate<Object> itemMatcher = item -> {
            String compareString = matchingValue;
            if (!(item instanceof String)) {
                return false;
            }
            String stringItem = (String)item;
            if (matchingConfiguration.isExactMatch()) {
                if (matchingConfiguration.isCaseSensitive()) {
                    return stringItem.equals(compareString);
                }
                return stringItem.equalsIgnoreCase(compareString);
            }
            if (matchingConfiguration.isCaseSensitive()) {
                return stringItem.contains(compareString);
            }
            return stringItem.toLowerCase().contains(compareString.toLowerCase());
        };
        return itemMatcher;
    }

    private boolean test(FilterBase filterBase, CalendarComponent calendarComponent) {
        FilterType filterType = filterBase.getType();
        if (this.isOperation(filterType)) {
            if (filterType == FilterType.AND) {
                AndFilter andFilter = (AndFilter)filterBase;
                boolean result = false;
                for (FilterBase child : andFilter.getChildren()) {
                    result = true;
                    result = result && this.test(child, calendarComponent);
                    if (result) continue;
                    return false;
                }
                return result;
            }
            if (filterType == FilterType.OR) {
                OrFilter orFilter = (OrFilter)filterBase;
                boolean result = false;
                for (FilterBase child : orFilter.getChildren()) {
                    if (!(result = result || this.test(child, calendarComponent))) continue;
                    return true;
                }
                return result;
            }
            if (filterType == FilterType.NOT) {
                NotFilter notFilter = (NotFilter)filterBase;
                boolean result = false;
                for (FilterBase child : notFilter.getChildren()) {
                    result = true;
                    result = result && !this.test(child, calendarComponent);
                    if (result) continue;
                    return false;
                }
                return result;
            }
        } else {
            CalendarComponentPredicate predicate = MemoryCalendarComponentPredicate.getPredicate(filterBase.getClass());
            if (predicate == null) {
                return false;
            }
            return predicate.test(filterBase, calendarComponent);
        }
        return false;
    }

    private boolean isOperation(FilterType type) {
        return type == FilterType.AND || type == FilterType.OR || type == FilterType.NOT;
    }

    static {
        predicateMap.put(EntityTimeRangeFilter.class, (filterBase, calendarComponent) -> {
            if (!(calendarComponent instanceof VEvent)) {
                return false;
            }
            VEvent vEvent = (VEvent)calendarComponent;
            EntityTimeRangeFilter entityTimeRangeFilter = (EntityTimeRangeFilter)filterBase;
            TimeRange timeRange = (TimeRange)entityTimeRangeFilter.getEntity();
            long eventStartTime = vEvent.getStartDate().getDate().getTime();
            long eventEndTime = vEvent.getEndDate().getDate().getTime();
            long endTimeUTC = 0L;
            long startTimeUTC = 0L;
            if (timeRange.getStart() == null && timeRange.getEnd() != null) {
                endTimeUTC = timeRange.getEnd().getTime();
                return eventStartTime < endTimeUTC;
            }
            if (timeRange.getEnd() == null && timeRange.getStart() != null) {
                startTimeUTC = timeRange.getStart().getTime();
                return eventEndTime > startTimeUTC;
            }
            startTimeUTC = timeRange.getStart().getTime();
            endTimeUTC = timeRange.getEnd().getTime();
            return eventStartTime < endTimeUTC && eventEndTime > startTimeUTC || eventStartTime == eventEndTime && eventEndTime >= startTimeUTC;
        });
        predicateMap.put(TimeRangeFilter.class, (filterBase, calendarComponent) -> {
            if (!(calendarComponent instanceof VEvent)) {
                return false;
            }
            TimeRangeFilter timeRangeFilter = (TimeRangeFilter)filterBase;
            TimeRange timeRange = (TimeRange)timeRangeFilter.getEntity();
            PropertyIndex.PropertyInfoIndex propertyInfoIndex = timeRangeFilter.getPropertyInfoIndex();
            CalDavProperties calDavProperty = SupportedPropertyFilterBaseTransformer.propertiesMapper.get((Object)propertyInfoIndex);
            if (calDavProperty == null) {
                return false;
            }
            Object property = calendarComponent.getProperty(calDavProperty.getFieldName());
            if (property == null) {
                return false;
            }
            String stringValue = ((Content)property).getValue();
            try {
                DateTime dateTime = new DateTime(stringValue);
                boolean compareResult = true;
                if (timeRange.getStart() != null) {
                    boolean bl = compareResult = dateTime.compareTo(timeRange.getStart()) >= 0;
                }
                if (timeRange.getEnd() != null) {
                    compareResult = compareResult && dateTime.compareTo(timeRange.getEnd()) < 0;
                }
                return compareResult;
            }
            catch (ParseException e) {
                return false;
            }
        });
        predicateMap.put(PropertyValueFilter.class, (filterBase, calendarComponent) -> {
            if (!(calendarComponent instanceof VEvent)) {
                return false;
            }
            PropertyValueFilter propertyValueFilter = (PropertyValueFilter)filterBase;
            PropertyIndex.PropertyInfoIndex propertyInfoIndex = propertyValueFilter.getPropertyInfoIndex();
            CalDavProperties calDavProperty = SupportedPropertyFilterBaseTransformer.propertiesMapper.get((Object)propertyInfoIndex);
            if (calDavProperty == null) {
                return false;
            }
            Object property = calendarComponent.getProperty(calDavProperty.getFieldName());
            if (property == null) {
                return false;
            }
            Object entity = propertyValueFilter.getEntity();
            PropertyValueFilter.MatchingConfiguration matchingConfiguration = propertyValueFilter.getMatchingConfiguration();
            String propertyValue = ((Content)property).getValue();
            if (!(entity instanceof Collection)) {
                return MemoryCalendarComponentPredicate.getItemPredicate(matchingConfiguration, propertyValue).test(entity);
            }
            Collection entities = (Collection)entity;
            entities.stream().filter(MemoryCalendarComponentPredicate.getItemPredicate(matchingConfiguration, propertyValue));
            return false;
        });
        predicateMap.put(PresenceFilter.class, (filterBase, calendarComponent) -> {
            if (!(calendarComponent instanceof VEvent)) {
                return false;
            }
            PresenceFilter presenceFilter = (PresenceFilter)filterBase;
            PropertyIndex.PropertyInfoIndex propertyInfoIndex = presenceFilter.getPropertyInfoIndex();
            CalDavProperties calDavProperty = SupportedPropertyFilterBaseTransformer.propertiesMapper.get((Object)propertyInfoIndex);
            if (calDavProperty == null) {
                return false;
            }
            Object iCalProperty = calendarComponent.getProperty(calDavProperty.getFieldName());
            return iCalProperty != null;
        });
    }

    private static interface CalendarComponentPredicate {
        public boolean test(FilterBase var1, CalendarComponent var2);
    }
}

