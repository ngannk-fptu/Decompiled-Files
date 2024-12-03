/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jpa.event.spi.CallbackDefinition;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class Property
implements Serializable,
MetaAttributable {
    private String name;
    private Value value;
    private String cascade;
    private boolean updateable = true;
    private boolean insertable = true;
    private boolean selectable = true;
    private boolean optimisticLocked = true;
    private ValueGeneration valueGenerationStrategy;
    private String propertyAccessorName;
    private boolean lazy;
    private String lazyGroup;
    private boolean optional;
    private Map metaAttributes;
    private PersistentClass persistentClass;
    private boolean naturalIdentifier;
    private boolean lob;
    private List<CallbackDefinition> callbackDefinitions;

    public boolean isBackRef() {
        return false;
    }

    public boolean isSynthetic() {
        return false;
    }

    public Type getType() throws MappingException {
        return this.value.getType();
    }

    public int getColumnSpan() {
        return this.value.getColumnSpan();
    }

    public Iterator getColumnIterator() {
        return this.value.getColumnIterator();
    }

    public String getName() {
        return this.name;
    }

    public boolean isComposite() {
        return this.value instanceof Component;
    }

    public Value getValue() {
        return this.value;
    }

    public boolean isPrimitive(Class clazz) {
        return this.getGetter(clazz).getReturnType().isPrimitive();
    }

    public CascadeStyle getCascadeStyle() throws MappingException {
        Type type = this.value.getType();
        if (type.isComponentType()) {
            return Property.getCompositeCascadeStyle((CompositeType)type, this.cascade);
        }
        if (type.isCollectionType()) {
            return Property.getCollectionCascadeStyle(((Collection)this.value).getElement().getType(), this.cascade);
        }
        return Property.getCascadeStyle(this.cascade);
    }

    private static CascadeStyle getCompositeCascadeStyle(CompositeType compositeType, String cascade) {
        if (compositeType.isAnyType()) {
            return Property.getCascadeStyle(cascade);
        }
        int length = compositeType.getSubtypes().length;
        for (int i = 0; i < length; ++i) {
            if (compositeType.getCascadeStyle(i) == CascadeStyles.NONE) continue;
            return CascadeStyles.ALL;
        }
        return Property.getCascadeStyle(cascade);
    }

    private static CascadeStyle getCollectionCascadeStyle(Type elementType, String cascade) {
        if (elementType.isComponentType()) {
            return Property.getCompositeCascadeStyle((CompositeType)elementType, cascade);
        }
        return Property.getCascadeStyle(cascade);
    }

    private static CascadeStyle getCascadeStyle(String cascade) {
        if (cascade == null || cascade.equals("none")) {
            return CascadeStyles.NONE;
        }
        StringTokenizer tokens = new StringTokenizer(cascade, ", ");
        CascadeStyle[] styles = new CascadeStyle[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            styles[i++] = CascadeStyles.getCascadeStyle(tokens.nextToken());
        }
        return new CascadeStyles.MultipleCascadeStyle(styles);
    }

    public String getCascade() {
        return this.cascade;
    }

    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.intern();
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isUpdateable() {
        return this.updateable && !ArrayHelper.isAllFalse(this.value.getColumnUpdateability());
    }

    public boolean isInsertable() {
        boolean[] columnInsertability = this.value.getColumnInsertability();
        return this.insertable && (columnInsertability.length == 0 || !ArrayHelper.isAllFalse(columnInsertability));
    }

    public ValueGeneration getValueGenerationStrategy() {
        return this.valueGenerationStrategy;
    }

    public void setValueGenerationStrategy(ValueGeneration valueGenerationStrategy) {
        this.valueGenerationStrategy = valueGenerationStrategy;
    }

    public void setUpdateable(boolean mutable) {
        this.updateable = mutable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public String getPropertyAccessorName() {
        return this.propertyAccessorName;
    }

    public void setPropertyAccessorName(String string) {
        this.propertyAccessorName = string;
    }

    boolean isNullable() {
        return this.value == null || this.value.isNullable();
    }

    public boolean isBasicPropertyAccessor() {
        return this.propertyAccessorName == null || "property".equals(this.propertyAccessorName);
    }

    @Override
    public Map getMetaAttributes() {
        return this.metaAttributes;
    }

    @Override
    public MetaAttribute getMetaAttribute(String attributeName) {
        return this.metaAttributes == null ? null : (MetaAttribute)this.metaAttributes.get(attributeName);
    }

    @Override
    public void setMetaAttributes(Map metas) {
        this.metaAttributes = metas;
    }

    public boolean isValid(Mapping mapping) throws MappingException {
        return this.getValue().isValid(mapping);
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.name + ')';
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isLazy() {
        if (this.value instanceof ToOne) {
            return false;
        }
        return this.lazy;
    }

    public String getLazyGroup() {
        return this.lazyGroup;
    }

    public void setLazyGroup(String lazyGroup) {
        this.lazyGroup = lazyGroup;
    }

    public boolean isOptimisticLocked() {
        return this.optimisticLocked;
    }

    public void setOptimisticLocked(boolean optimisticLocked) {
        this.optimisticLocked = optimisticLocked;
    }

    public boolean isOptional() {
        return this.optional || this.isNullable();
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public PersistentClass getPersistentClass() {
        return this.persistentClass;
    }

    public void setPersistentClass(PersistentClass persistentClass) {
        this.persistentClass = persistentClass;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public String getAccessorPropertyName(EntityMode mode) {
        return this.getName();
    }

    public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {
        return this.getPropertyAccessStrategy(clazz).buildPropertyAccess(clazz, this.name).getGetter();
    }

    public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {
        return this.getPropertyAccessStrategy(clazz).buildPropertyAccess(clazz, this.name).getSetter();
    }

    public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
        String accessName = this.getPropertyAccessorName();
        if (accessName == null) {
            accessName = clazz == null || Map.class.equals((Object)clazz) ? "map" : "property";
        }
        EntityMode entityMode = clazz == null || Map.class.equals((Object)clazz) ? EntityMode.MAP : EntityMode.POJO;
        return this.resolveServiceRegistry().getService(PropertyAccessStrategyResolver.class).resolvePropertyAccessStrategy(clazz, accessName, entityMode);
    }

    protected ServiceRegistry resolveServiceRegistry() {
        if (this.getPersistentClass() != null) {
            return this.getPersistentClass().getServiceRegistry();
        }
        if (this.getValue() != null) {
            return this.getValue().getServiceRegistry();
        }
        throw new HibernateException("Could not resolve ServiceRegistry");
    }

    public boolean isNaturalIdentifier() {
        return this.naturalIdentifier;
    }

    public void setNaturalIdentifier(boolean naturalIdentifier) {
        this.naturalIdentifier = naturalIdentifier;
    }

    public boolean isLob() {
        return this.lob;
    }

    public void setLob(boolean lob) {
        this.lob = lob;
    }

    public void addCallbackDefinitions(List<CallbackDefinition> callbackDefinitions) {
        if (callbackDefinitions == null || callbackDefinitions.isEmpty()) {
            return;
        }
        if (this.callbackDefinitions == null) {
            this.callbackDefinitions = new ArrayList<CallbackDefinition>();
        }
        this.callbackDefinitions.addAll(callbackDefinitions);
    }

    public List<CallbackDefinition> getCallbackDefinitions() {
        if (this.callbackDefinitions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.callbackDefinitions);
    }
}

