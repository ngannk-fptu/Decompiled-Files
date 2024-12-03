/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavProperties;
import com.atlassian.confluence.extra.calendar3.caldav.filter.AbstractPropertyFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBaseTransformer;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bedework.util.calendar.PropertyIndex;
import org.springframework.stereotype.Component;

@Component
public class SupportedPropertyFilterBaseTransformer
implements FilterBaseTransformer<FilterBase> {
    public static final Map<PropertyIndex.PropertyInfoIndex, CalDavProperties> propertiesMapper = new HashMap<PropertyIndex.PropertyInfoIndex, CalDavProperties>();

    @Override
    public Optional<FilterBase> transform(FilterBase toBeTransformed) {
        if (toBeTransformed == null) {
            return Optional.empty();
        }
        FilterType filterType = toBeTransformed.getType();
        if (this.isOperation(toBeTransformed.getType())) {
            FilterBase parentFilter = toBeTransformed.clone();
            for (FilterBase childFilter : toBeTransformed.getChildren()) {
                Optional<FilterBase> transformedFilter = this.transform(childFilter);
                if (!transformedFilter.isPresent()) continue;
                parentFilter.addChild(transformedFilter.get());
            }
            return this.processOperationNode(toBeTransformed, parentFilter);
        }
        if (filterType == FilterType.PROPERTY) {
            AbstractPropertyFilter propertyFilter = (AbstractPropertyFilter)toBeTransformed;
            PropertyIndex.PropertyInfoIndex propertyInfoIndex = propertyFilter.getPropertyInfoIndex();
            boolean isSupport = propertiesMapper.containsKey((Object)propertyInfoIndex);
            return isSupport ? Optional.of(toBeTransformed) : Optional.empty();
        }
        return Optional.of(toBeTransformed);
    }

    private Optional<FilterBase> processOperationNode(FilterBase toBeTransformed, FilterBase transformed) {
        Objects.requireNonNull(toBeTransformed);
        Objects.requireNonNull(transformed);
        FilterType filterType = toBeTransformed.getType();
        if (transformed.getChildren().size() == 0) {
            return Optional.empty();
        }
        switch (filterType) {
            case OR: {
                if (transformed.getChildren().size() == 1) {
                    return transformed.getChildren().stream().findFirst();
                }
                return Optional.of(transformed);
            }
            case AND: {
                if (transformed.getChildren().size() != toBeTransformed.getChildren().size()) {
                    return Optional.empty();
                }
                return Optional.of(transformed);
            }
        }
        return Optional.of(transformed);
    }

    private boolean isOperation(FilterType type) {
        return type == FilterType.AND || type == FilterType.OR || type == FilterType.NOT;
    }

    static {
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.DTSTART, CalDavProperties.DTSTART);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.DTEND, CalDavProperties.DTEND);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.DTEND, CalDavProperties.DTEND);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.SUMMARY, CalDavProperties.SUMMARY);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.URL, CalDavProperties.URL);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.LOCATION, CalDavProperties.LOCATION);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.DESCRIPTION, CalDavProperties.DESCRIPTION);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.SEQUENCE, CalDavProperties.SEQUENCE);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.LAST_MODIFIED, CalDavProperties.LAST_MODIFIED);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.ORGANIZER, CalDavProperties.ORGANIZER);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.UID, CalDavProperties.UID);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.RECURRENCE_ID, CalDavProperties.RECURRENCE_ID);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.CREATED, CalDavProperties.CREATED);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.RRULE, CalDavProperties.RRULE);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.DURATION, CalDavProperties.DURATION);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.ENTITY_TYPE, CalDavProperties.NONE);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.ATTENDEE, CalDavProperties.NONE);
        propertiesMapper.put(PropertyIndex.PropertyInfoIndex.RRULE, CalDavProperties.NONE);
    }
}

