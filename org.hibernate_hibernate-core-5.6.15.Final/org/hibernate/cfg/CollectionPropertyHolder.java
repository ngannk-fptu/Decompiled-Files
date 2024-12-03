/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Convert
 *  javax.persistence.Converts
 *  javax.persistence.Enumerated
 *  javax.persistence.JoinTable
 *  javax.persistence.ManyToMany
 *  javax.persistence.MapKeyClass
 *  javax.persistence.MapKeyEnumerated
 *  javax.persistence.MapKeyTemporal
 *  javax.persistence.OneToMany
 *  javax.persistence.Temporal
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AbstractPropertyHolder;
import org.hibernate.cfg.AttributeConversionInfo;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

public class CollectionPropertyHolder
extends AbstractPropertyHolder {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(CollectionPropertyHolder.class);
    private final Collection collection;
    private boolean canElementBeConverted = true;
    private boolean canKeyBeConverted = true;
    private Map<String, AttributeConversionInfo> elementAttributeConversionInfoMap;
    private Map<String, AttributeConversionInfo> keyAttributeConversionInfoMap;
    boolean prepared;

    public CollectionPropertyHolder(Collection collection, String path, XClass clazzToProcess, XProperty property, PropertyHolder parentPropertyHolder, MetadataBuildingContext context) {
        super(path, parentPropertyHolder, clazzToProcess, context);
        this.collection = collection;
        this.setCurrentProperty(property);
        this.elementAttributeConversionInfoMap = new HashMap<String, AttributeConversionInfo>();
        this.keyAttributeConversionInfoMap = new HashMap<String, AttributeConversionInfo>();
    }

    public Collection getCollectionBinding() {
        return this.collection;
    }

    private void buildAttributeConversionInfoMaps(XProperty collectionProperty, Map<String, AttributeConversionInfo> elementAttributeConversionInfoMap, Map<String, AttributeConversionInfo> keyAttributeConversionInfoMap) {
        Converts convertsAnnotation;
        if (collectionProperty == null) {
            return;
        }
        Convert convertAnnotation = (Convert)collectionProperty.getAnnotation(Convert.class);
        if (convertAnnotation != null) {
            this.applyLocalConvert(convertAnnotation, collectionProperty, elementAttributeConversionInfoMap, keyAttributeConversionInfoMap);
        }
        if ((convertsAnnotation = (Converts)collectionProperty.getAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation2 : convertsAnnotation.value()) {
                this.applyLocalConvert(convertAnnotation2, collectionProperty, elementAttributeConversionInfoMap, keyAttributeConversionInfoMap);
            }
        }
    }

    private void applyLocalConvert(Convert convertAnnotation, XProperty collectionProperty, Map<String, AttributeConversionInfo> elementAttributeConversionInfoMap, Map<String, AttributeConversionInfo> keyAttributeConversionInfoMap) {
        AttributeConversionInfo info = new AttributeConversionInfo(convertAnnotation, (XAnnotatedElement)collectionProperty);
        if (this.collection.isMap()) {
            boolean specCompliant;
            boolean bl = specCompliant = StringHelper.isNotEmpty(info.getAttributeName()) && (info.getAttributeName().startsWith("key") || info.getAttributeName().startsWith("value"));
            if (!specCompliant) {
                log.nonCompliantMapConversion(this.collection.getRole());
            }
        }
        if (StringHelper.isEmpty(info.getAttributeName())) {
            if (this.canElementBeConverted && this.canKeyBeConverted) {
                throw new IllegalStateException("@Convert placed on Map attribute [" + this.collection.getRole() + "] must define attributeName of 'key' or 'value'");
            }
            if (this.canKeyBeConverted) {
                keyAttributeConversionInfoMap.put("", info);
            } else if (this.canElementBeConverted) {
                elementAttributeConversionInfoMap.put("", info);
            }
        } else {
            String elementPath;
            String keyPath;
            if (this.canElementBeConverted && this.canKeyBeConverted) {
                keyPath = this.removePrefix(info.getAttributeName(), "key");
                elementPath = this.removePrefix(info.getAttributeName(), "value");
                if (keyPath == null && elementPath == null) {
                    throw new IllegalStateException("@Convert placed on Map attribute [" + this.collection.getRole() + "] must define attributeName of 'key' or 'value'");
                }
            } else if (this.canKeyBeConverted) {
                keyPath = this.removePrefix(info.getAttributeName(), "key", info.getAttributeName());
                elementPath = null;
            } else {
                keyPath = null;
                elementPath = this.removePrefix(info.getAttributeName(), "value", info.getAttributeName());
            }
            if (keyPath != null) {
                keyAttributeConversionInfoMap.put(keyPath, info);
            } else if (elementPath != null) {
                elementAttributeConversionInfoMap.put(elementPath, info);
            } else {
                throw new IllegalStateException(String.format(Locale.ROOT, "Could not determine how to apply @Convert(attributeName='%s') to collection [%s]", info.getAttributeName(), this.collection.getRole()));
            }
        }
    }

    private String removePrefix(String path, String prefix) {
        return this.removePrefix(path, prefix, null);
    }

    private String removePrefix(String path, String prefix, String defaultValue) {
        if (path.equals(prefix)) {
            return "";
        }
        if (path.startsWith(prefix + ".")) {
            return path.substring(prefix.length() + 1);
        }
        return defaultValue;
    }

    @Override
    protected String normalizeCompositePath(String attributeName) {
        return attributeName;
    }

    @Override
    protected String normalizeCompositePathForLogging(String attributeName) {
        return this.collection.getRole() + '.' + attributeName;
    }

    @Override
    public void startingProperty(XProperty property) {
        if (property == null) {
            return;
        }
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
        if (!(this.canElementBeConverted && this.canKeyBeConverted || this.canKeyBeConverted)) {
            return null;
        }
        return null;
    }

    @Override
    protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
        String key = this.removePrefix(path, "key");
        if (key != null) {
            return this.keyAttributeConversionInfoMap.get(key);
        }
        String element = this.removePrefix(path, "element");
        if (element != null) {
            return this.elementAttributeConversionInfoMap.get(element);
        }
        return this.elementAttributeConversionInfoMap.get(path);
    }

    @Override
    public String getClassName() {
        throw new AssertionFailure("Collection property holder does not have a class name");
    }

    @Override
    public String getEntityOwnerClassName() {
        return null;
    }

    @Override
    public Table getTable() {
        return this.collection.getCollectionTable();
    }

    @Override
    public void addProperty(Property prop, XClass declaringClass) {
        throw new AssertionFailure("Cannot add property to a collection");
    }

    @Override
    public KeyValue getIdentifier() {
        throw new AssertionFailure("Identifier collection not yet managed");
    }

    @Override
    public boolean isOrWithinEmbeddedId() {
        return false;
    }

    @Override
    public boolean isWithinElementCollection() {
        return false;
    }

    @Override
    public PersistentClass getPersistentClass() {
        return this.collection.getOwner();
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public boolean isEntity() {
        return false;
    }

    @Override
    public String getEntityName() {
        return this.collection.getOwner().getEntityName();
    }

    @Override
    public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
        throw new AssertionFailure("addProperty to a join table of a collection: does it make sense?");
    }

    @Override
    public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
        throw new AssertionFailure("Add a <join> in a second pass");
    }

    public String toString() {
        return super.toString() + "(" + this.collection.getRole() + ")";
    }

    public void prepare(XProperty collectionProperty) {
        if (this.prepared) {
            return;
        }
        if (collectionProperty == null) {
            return;
        }
        this.prepared = true;
        if (this.collection.isMap()) {
            if (collectionProperty.isAnnotationPresent(MapKeyEnumerated.class)) {
                this.canKeyBeConverted = false;
            } else if (collectionProperty.isAnnotationPresent(MapKeyTemporal.class)) {
                this.canKeyBeConverted = false;
            } else if (collectionProperty.isAnnotationPresent(MapKeyClass.class)) {
                this.canKeyBeConverted = false;
            } else if (collectionProperty.isAnnotationPresent(MapKeyType.class)) {
                this.canKeyBeConverted = false;
            }
        } else {
            this.canKeyBeConverted = false;
        }
        if (collectionProperty.isAnnotationPresent(ManyToAny.class)) {
            this.canElementBeConverted = false;
        } else if (collectionProperty.isAnnotationPresent(OneToMany.class)) {
            this.canElementBeConverted = false;
        } else if (collectionProperty.isAnnotationPresent(ManyToMany.class)) {
            this.canElementBeConverted = false;
        } else if (collectionProperty.isAnnotationPresent(Enumerated.class)) {
            this.canElementBeConverted = false;
        } else if (collectionProperty.isAnnotationPresent(Temporal.class)) {
            this.canElementBeConverted = false;
        } else if (collectionProperty.isAnnotationPresent(CollectionType.class)) {
            this.canElementBeConverted = false;
        }
        if (this.canKeyBeConverted || this.canElementBeConverted) {
            this.buildAttributeConversionInfoMaps(collectionProperty, this.elementAttributeConversionInfoMap, this.keyAttributeConversionInfoMap);
        }
    }

    public ConverterDescriptor resolveElementAttributeConverterDescriptor(XProperty collectionXProperty, XClass elementXClass) {
        AttributeConversionInfo info = this.locateAttributeConversionInfo("element");
        if (info != null) {
            if (info.isConversionDisabled()) {
                return null;
            }
            try {
                return this.makeAttributeConverterDescriptor(info);
            }
            catch (Exception e) {
                throw this.buildExceptionFromInstantiationError(info, e);
            }
        }
        log.debugf("Attempting to locate auto-apply AttributeConverter for collection element [%s]", this.collection.getRole());
        return this.getContext().getMetadataCollector().getAttributeConverterAutoApplyHandler().findAutoApplyConverterForCollectionElement(collectionXProperty, this.getContext());
    }

    public ConverterDescriptor mapKeyAttributeConverterDescriptor(XProperty mapXProperty, XClass keyXClass) {
        AttributeConversionInfo info = this.locateAttributeConversionInfo("key");
        if (info != null) {
            if (info.isConversionDisabled()) {
                return null;
            }
            try {
                return this.makeAttributeConverterDescriptor(info);
            }
            catch (Exception e) {
                throw this.buildExceptionFromInstantiationError(info, e);
            }
        }
        log.debugf("Attempting to locate auto-apply AttributeConverter for collection key [%s]", this.collection.getRole());
        return this.getContext().getMetadataCollector().getAttributeConverterAutoApplyHandler().findAutoApplyConverterForMapKey(mapXProperty, this.getContext());
    }
}

