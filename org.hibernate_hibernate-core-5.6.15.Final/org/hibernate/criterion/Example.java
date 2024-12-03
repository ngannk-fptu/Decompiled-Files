/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.NullExpression;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class Example
implements Criterion {
    private final Object exampleEntity;
    private PropertySelector selector;
    private boolean isLikeEnabled;
    private Character escapeCharacter;
    private boolean isIgnoreCaseEnabled;
    private MatchMode matchMode;
    private final Set<String> excludedProperties = new HashSet<String>();

    public static Example create(Object exampleEntity) {
        if (exampleEntity == null) {
            throw new NullPointerException("null example entity");
        }
        return new Example(exampleEntity, NotNullPropertySelector.INSTANCE);
    }

    protected Example(Object exampleEntity, PropertySelector selector) {
        this.exampleEntity = exampleEntity;
        this.selector = selector;
    }

    public Example setEscapeCharacter(Character escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
        return this;
    }

    public Example enableLike() {
        return this.enableLike(MatchMode.EXACT);
    }

    public Example enableLike(MatchMode matchMode) {
        this.isLikeEnabled = true;
        this.matchMode = matchMode;
        return this;
    }

    public Example ignoreCase() {
        this.isIgnoreCaseEnabled = true;
        return this;
    }

    public Example setPropertySelector(PropertySelector selector) {
        this.selector = selector;
        return this;
    }

    public Example excludeZeroes() {
        this.setPropertySelector(NotNullOrZeroPropertySelector.INSTANCE);
        return this;
    }

    public Example excludeNone() {
        this.setPropertySelector(AllPropertySelector.INSTANCE);
        return this;
    }

    public Example excludeProperty(String name) {
        this.excludedProperties.add(name);
        return this;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        StringBuilder buf = new StringBuilder().append('(');
        EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(criteriaQuery.getEntityName(criteria));
        String[] propertyNames = meta.getPropertyNames();
        Type[] propertyTypes = meta.getPropertyTypes();
        Object[] propertyValues = meta.getPropertyValues(this.exampleEntity);
        for (int i = 0; i < propertyNames.length; ++i) {
            boolean isVersionProperty;
            Object propertyValue = propertyValues[i];
            String propertyName = propertyNames[i];
            boolean bl = isVersionProperty = i == meta.getVersionProperty();
            if (isVersionProperty || !this.isPropertyIncluded(propertyValue, propertyName, propertyTypes[i])) continue;
            if (propertyTypes[i].isComponentType()) {
                this.appendComponentCondition(propertyName, propertyValue, (CompositeType)propertyTypes[i], criteria, criteriaQuery, buf);
                continue;
            }
            this.appendPropertyCondition(propertyName, propertyValue, criteria, criteriaQuery, buf);
        }
        if (buf.length() == 1) {
            buf.append("1=1");
        }
        return buf.append(')').toString();
    }

    private boolean isPropertyIncluded(Object value, String name, Type type) {
        if (this.excludedProperties.contains(name)) {
            return false;
        }
        if (type.isAssociationType()) {
            return false;
        }
        return this.selector.include(value, name, type);
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(criteriaQuery.getEntityName(criteria));
        String[] propertyNames = meta.getPropertyNames();
        Type[] propertyTypes = meta.getPropertyTypes();
        Object[] values = meta.getPropertyValues(this.exampleEntity);
        ArrayList<TypedValue> list = new ArrayList<TypedValue>();
        for (int i = 0; i < propertyNames.length; ++i) {
            boolean isVersionProperty;
            Object value = values[i];
            Type type = propertyTypes[i];
            String name = propertyNames[i];
            boolean bl = isVersionProperty = i == meta.getVersionProperty();
            if (isVersionProperty || !this.isPropertyIncluded(value, name, type)) continue;
            if (propertyTypes[i].isComponentType()) {
                this.addComponentTypedValues(name, value, (CompositeType)type, list, criteria, criteriaQuery);
                continue;
            }
            this.addPropertyTypedValue(value, type, list);
        }
        return list.toArray(new TypedValue[list.size()]);
    }

    protected void addPropertyTypedValue(Object value, Type type, List<TypedValue> list) {
        if (value != null) {
            if (value instanceof String) {
                String string = (String)value;
                if (this.isIgnoreCaseEnabled) {
                    string = string.toLowerCase(Locale.ROOT);
                }
                if (this.isLikeEnabled) {
                    string = this.matchMode.toMatchString(string);
                }
                value = string;
            }
            list.add(new TypedValue(type, value));
        }
    }

    protected void addComponentTypedValues(String path, Object component, CompositeType type, List<TypedValue> list, Criteria criteria, CriteriaQuery criteriaQuery) {
        if (component != null) {
            String[] propertyNames = type.getPropertyNames();
            Type[] subtypes = type.getSubtypes();
            Object[] values = type.getPropertyValues(component, this.getEntityMode(criteria, criteriaQuery));
            for (int i = 0; i < propertyNames.length; ++i) {
                Object value = values[i];
                Type subtype = subtypes[i];
                String subpath = StringHelper.qualify(path, propertyNames[i]);
                if (!this.isPropertyIncluded(value, subpath, subtype)) continue;
                if (subtype.isComponentType()) {
                    this.addComponentTypedValues(subpath, value, (CompositeType)subtype, list, criteria, criteriaQuery);
                    continue;
                }
                this.addPropertyTypedValue(value, subtype, list);
            }
        }
    }

    private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {
        EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(criteriaQuery.getEntityName(criteria));
        EntityMode result = meta.getEntityMode();
        if (!meta.getEntityMetamodel().getTuplizer().isInstance(this.exampleEntity)) {
            throw new ClassCastException(this.exampleEntity.getClass().getName());
        }
        return result;
    }

    protected void appendPropertyCondition(String propertyName, Object propertyValue, Criteria criteria, CriteriaQuery cq, StringBuilder buf) {
        Criterion condition;
        if (propertyValue != null) {
            boolean isString = propertyValue instanceof String;
            condition = this.isLikeEnabled && isString ? new LikeExpression(propertyName, (String)propertyValue, this.matchMode, this.escapeCharacter, this.isIgnoreCaseEnabled) : new SimpleExpression(propertyName, propertyValue, "=", this.isIgnoreCaseEnabled && isString);
        } else {
            condition = new NullExpression(propertyName);
        }
        String conditionFragment = condition.toSqlString(criteria, cq);
        if (!StringHelper.isBlank(conditionFragment)) {
            if (buf.length() > 1) {
                buf.append(" and ");
            }
            buf.append(conditionFragment);
        }
    }

    protected void appendComponentCondition(String path, Object component, CompositeType type, Criteria criteria, CriteriaQuery criteriaQuery, StringBuilder buf) {
        if (component != null) {
            String[] propertyNames = type.getPropertyNames();
            Object[] values = type.getPropertyValues(component, this.getEntityMode(criteria, criteriaQuery));
            Type[] subtypes = type.getSubtypes();
            for (int i = 0; i < propertyNames.length; ++i) {
                Object value = values[i];
                String subPath = StringHelper.qualify(path, propertyNames[i]);
                if (!this.isPropertyIncluded(value, subPath, subtypes[i])) continue;
                Type subtype = subtypes[i];
                if (subtype.isComponentType()) {
                    this.appendComponentCondition(subPath, value, (CompositeType)subtype, criteria, criteriaQuery, buf);
                    continue;
                }
                this.appendPropertyCondition(subPath, value, criteria, criteriaQuery, buf);
            }
        }
    }

    public String toString() {
        return "example (" + this.exampleEntity + ')';
    }

    public static final class NotNullOrZeroPropertySelector
    implements PropertySelector {
        public static final NotNullOrZeroPropertySelector INSTANCE = new NotNullOrZeroPropertySelector();

        @Override
        public boolean include(Object object, String propertyName, Type type) {
            return object != null && (!(object instanceof Number) || ((Number)object).longValue() != 0L);
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static final class NotNullPropertySelector
    implements PropertySelector {
        public static final NotNullPropertySelector INSTANCE = new NotNullPropertySelector();

        @Override
        public boolean include(Object object, String propertyName, Type type) {
            return object != null;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static final class AllPropertySelector
    implements PropertySelector {
        public static final AllPropertySelector INSTANCE = new AllPropertySelector();

        @Override
        public boolean include(Object object, String propertyName, Type type) {
            return true;
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    public static interface PropertySelector
    extends Serializable {
        public boolean include(Object var1, String var2, Type var3);
    }
}

