/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyImpl
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.plugin.rest.entity.BooleanRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.NullRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.PropertyEntity;
import com.atlassian.crowd.plugin.rest.entity.PropertyRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.SearchRestrictionEntity;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchRestrictionEntityTranslator {
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static BooleanRestrictionEntity toBooleanRestrictionEntity(BooleanRestriction booleanRestriction) {
        ArrayList<SearchRestrictionEntity> restrictionEntities = new ArrayList<SearchRestrictionEntity>();
        for (SearchRestriction sr : booleanRestriction.getRestrictions()) {
            restrictionEntities.add(SearchRestrictionEntityTranslator.toSearchRestrictionEntity(sr));
        }
        return new BooleanRestrictionEntity(booleanRestriction.getBooleanLogic().name(), restrictionEntities);
    }

    public static BooleanRestriction toBooleanRestriction(BooleanRestrictionEntity booleanRestrictionEntity) {
        BooleanRestriction.BooleanLogic booleanLogic = BooleanRestriction.BooleanLogic.valueOf((String)booleanRestrictionEntity.getBooleanLogic().toUpperCase());
        ArrayList<SearchRestriction> restrictions = new ArrayList<SearchRestriction>();
        for (SearchRestrictionEntity searchRestrictionEntity : booleanRestrictionEntity.getRestrictions()) {
            restrictions.add(SearchRestrictionEntityTranslator.toSearchRestriction(searchRestrictionEntity));
        }
        switch (booleanLogic) {
            case AND: {
                return Combine.allOf(restrictions);
            }
            case OR: {
                return Combine.anyOf(restrictions);
            }
        }
        throw new AssertionError((Object)("Unknown BooleanLogic type: " + booleanLogic));
    }

    public static PropertyRestrictionEntity toPropertyRestrictionEntity(PropertyRestriction propertyRestriction) {
        PropertyEntity propertyEntity = SearchRestrictionEntityTranslator.toPropertyEntity(propertyRestriction.getProperty());
        MatchMode mm = propertyRestriction.getMatchMode();
        String valueString = mm == MatchMode.NULL ? null : SearchRestrictionEntityTranslator.valueToString(propertyRestriction.getValue());
        return new PropertyRestrictionEntity(propertyEntity, mm.name(), valueString);
    }

    public static PropertyRestriction toPropertyRestriction(PropertyRestrictionEntity propertyRestrictionEntity) {
        Property property = SearchRestrictionEntityTranslator.toProperty(propertyRestrictionEntity.getProperty());
        MatchMode matchMode = MatchMode.valueOf((String)propertyRestrictionEntity.getMatchMode().toUpperCase());
        SupportedType supportedType = SupportedType.of(property.getPropertyType());
        return new TermRestriction(property, matchMode, SearchRestrictionEntityTranslator.valueFromString(supportedType, propertyRestrictionEntity.getValue()));
    }

    public static PropertyEntity toPropertyEntity(Property property) {
        SupportedType supportedType = SupportedType.of(property.getPropertyType());
        return new PropertyEntity(property.getPropertyName(), supportedType.name());
    }

    public static Property toProperty(PropertyEntity propertyEntity) {
        String typeString = propertyEntity.getType();
        SupportedType supportedType = SupportedType.of(typeString);
        return new PropertyImpl(propertyEntity.getName(), supportedType.getType());
    }

    public static SearchRestrictionEntity toSearchRestrictionEntity(SearchRestriction searchRestriction) {
        if (searchRestriction instanceof BooleanRestriction) {
            return SearchRestrictionEntityTranslator.toBooleanRestrictionEntity((BooleanRestriction)searchRestriction);
        }
        if (searchRestriction instanceof PropertyRestriction) {
            return SearchRestrictionEntityTranslator.toPropertyRestrictionEntity((PropertyRestriction)searchRestriction);
        }
        if (searchRestriction instanceof NullRestriction) {
            return NullRestrictionEntity.INSTANCE;
        }
        throw new IllegalArgumentException("Unknown search restriction type");
    }

    public static SearchRestriction toSearchRestriction(SearchRestrictionEntity searchRestrictionEntity) {
        if (searchRestrictionEntity instanceof BooleanRestrictionEntity) {
            return SearchRestrictionEntityTranslator.toBooleanRestriction((BooleanRestrictionEntity)searchRestrictionEntity);
        }
        if (searchRestrictionEntity instanceof PropertyRestrictionEntity) {
            return SearchRestrictionEntityTranslator.toPropertyRestriction((PropertyRestrictionEntity)searchRestrictionEntity);
        }
        if (searchRestrictionEntity instanceof NullRestrictionEntity) {
            return NullRestrictionImpl.INSTANCE;
        }
        throw new IllegalArgumentException("Unknown search restriction entity type");
    }

    public static String valueToString(Object value) {
        if (value instanceof Enum) {
            return ((Enum)value).name();
        }
        if (value instanceof Date) {
            return SearchRestrictionEntityTranslator.asTimeString((Date)value);
        }
        return value.toString();
    }

    public static Object valueFromString(SupportedType supportedType, String value) {
        switch (supportedType) {
            case BOOLEAN: {
                return Boolean.valueOf(value);
            }
            case DATE: {
                return SearchRestrictionEntityTranslator.fromTimeString(value);
            }
            case STRING: {
                return value;
            }
        }
        throw new AssertionError((Object)("Unknown supported type: " + (Object)((Object)supportedType)));
    }

    public static String asTimeString(Date date) {
        return new SimpleDateFormat(TIME_FORMAT).format(date);
    }

    public static Date fromTimeString(String time) throws IllegalArgumentException {
        try {
            return new SimpleDateFormat(TIME_FORMAT).parse(time);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing time: " + time, e);
        }
    }

    public static enum SupportedType {
        BOOLEAN(Boolean.class),
        DATE(Date.class),
        STRING(String.class);

        private final Class type;

        private SupportedType(Class type) {
            this.type = type;
        }

        public Class getType() {
            return this.type;
        }

        public static SupportedType of(String supportedType) {
            return SupportedType.valueOf(supportedType.toUpperCase());
        }

        public static SupportedType of(Class type) {
            for (SupportedType supportedType : SupportedType.values()) {
                if (!supportedType.getType().equals(type)) continue;
                return supportedType;
            }
            throw new IllegalArgumentException(type.getCanonicalName() + " is an unsupported type.");
        }
    }
}

