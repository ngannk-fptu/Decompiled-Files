/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AssociationOverride
 *  javax.persistence.AssociationOverrides
 *  javax.persistence.AttributeOverride
 *  javax.persistence.AttributeOverrides
 *  javax.persistence.Column
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinTable
 *  javax.persistence.MappedSuperclass
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AttributeConversionInfo;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public abstract class AbstractPropertyHolder
implements PropertyHolder {
    private static final Logger log = CoreLogging.logger(AbstractPropertyHolder.class);
    protected AbstractPropertyHolder parent;
    private Map<String, Column[]> holderColumnOverride;
    private Map<String, Column[]> currentPropertyColumnOverride;
    private Map<String, JoinColumn[]> holderJoinColumnOverride;
    private Map<String, JoinColumn[]> currentPropertyJoinColumnOverride;
    private Map<String, JoinTable> holderJoinTableOverride;
    private Map<String, JoinTable> currentPropertyJoinTableOverride;
    private Map<String, ForeignKey> holderForeignKeyOverride;
    private Map<String, ForeignKey> currentPropertyForeignKeyOverride;
    private String path;
    private MetadataBuildingContext context;
    private Boolean isInIdClass;

    AbstractPropertyHolder(String path, PropertyHolder parent, XClass clazzToProcess, MetadataBuildingContext context) {
        this.path = path;
        this.parent = (AbstractPropertyHolder)parent;
        this.context = context;
        this.buildHierarchyColumnOverride(clazzToProcess);
    }

    protected abstract String normalizeCompositePathForLogging(String var1);

    protected abstract String normalizeCompositePath(String var1);

    protected abstract AttributeConversionInfo locateAttributeConversionInfo(XProperty var1);

    protected abstract AttributeConversionInfo locateAttributeConversionInfo(String var1);

    @Override
    public ConverterDescriptor resolveAttributeConverterDescriptor(XProperty property) {
        AttributeConversionInfo info = this.locateAttributeConversionInfo(property);
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
        log.debugf("Attempting to locate auto-apply AttributeConverter for property [%s:%s]", (Object)this.path, (Object)property.getName());
        return this.context.getMetadataCollector().getAttributeConverterAutoApplyHandler().findAutoApplyConverterForAttribute(property, this.context);
    }

    protected IllegalStateException buildExceptionFromInstantiationError(AttributeConversionInfo info, Exception e) {
        if (Void.TYPE.equals(info.getConverterClass())) {
            return new IllegalStateException("Unable to instantiate AttributeConverter: you left @Convert.converter to its default value void.", e);
        }
        return new IllegalStateException(String.format("Unable to instantiate AttributeConverter [%s]", info.getConverterClass().getName()), e);
    }

    protected ConverterDescriptor makeAttributeConverterDescriptor(AttributeConversionInfo conversion) {
        try {
            return new ClassBasedConverterDescriptor(conversion.getConverterClass(), false, this.context.getBootstrapContext().getClassmateContext());
        }
        catch (Exception e) {
            throw new AnnotationException("Unable to create AttributeConverter instance", e);
        }
    }

    @Override
    public boolean isInIdClass() {
        return this.isInIdClass != null ? this.isInIdClass : (this.parent != null ? this.parent.isInIdClass() : false);
    }

    @Override
    public void setInIdClass(Boolean isInIdClass) {
        this.isInIdClass = isInIdClass;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    protected MetadataBuildingContext getContext() {
        return this.context;
    }

    protected void setCurrentProperty(XProperty property) {
        if (property == null) {
            this.currentPropertyColumnOverride = null;
            this.currentPropertyJoinColumnOverride = null;
            this.currentPropertyJoinTableOverride = null;
            this.currentPropertyForeignKeyOverride = null;
        } else {
            this.currentPropertyColumnOverride = AbstractPropertyHolder.buildColumnOverride((XAnnotatedElement)property, this.getPath());
            if (this.currentPropertyColumnOverride.size() == 0) {
                this.currentPropertyColumnOverride = null;
            }
            this.currentPropertyJoinColumnOverride = AbstractPropertyHolder.buildJoinColumnOverride((XAnnotatedElement)property, this.getPath());
            if (this.currentPropertyJoinColumnOverride.size() == 0) {
                this.currentPropertyJoinColumnOverride = null;
            }
            this.currentPropertyJoinTableOverride = AbstractPropertyHolder.buildJoinTableOverride((XAnnotatedElement)property, this.getPath());
            if (this.currentPropertyJoinTableOverride.size() == 0) {
                this.currentPropertyJoinTableOverride = null;
            }
            this.currentPropertyForeignKeyOverride = AbstractPropertyHolder.buildForeignKeyOverride((XAnnotatedElement)property, this.getPath());
            if (this.currentPropertyForeignKeyOverride.size() == 0) {
                this.currentPropertyForeignKeyOverride = null;
            }
        }
    }

    @Override
    public Column[] getOverriddenColumn(String propertyName) {
        Column[] result = this.getExactOverriddenColumn(propertyName);
        if (result == null && result == null && propertyName.contains(".collection&&element.")) {
            result = this.getExactOverriddenColumn(propertyName.replace(".collection&&element.", "."));
        }
        return result;
    }

    private Column[] getExactOverriddenColumn(String propertyName) {
        Column[] override = null;
        if (this.parent != null) {
            override = this.parent.getExactOverriddenColumn(propertyName);
        }
        if (override == null && this.currentPropertyColumnOverride != null) {
            override = this.currentPropertyColumnOverride.get(propertyName);
        }
        if (override == null && this.holderColumnOverride != null) {
            override = this.holderColumnOverride.get(propertyName);
        }
        return override;
    }

    @Override
    public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
        JoinColumn[] result = this.getExactOverriddenJoinColumn(propertyName);
        if (result == null && propertyName.contains(".collection&&element.")) {
            result = this.getExactOverriddenJoinColumn(propertyName.replace(".collection&&element.", "."));
        }
        return result;
    }

    private JoinColumn[] getExactOverriddenJoinColumn(String propertyName) {
        JoinColumn[] override = null;
        if (this.parent != null) {
            override = this.parent.getExactOverriddenJoinColumn(propertyName);
        }
        if (override == null && this.currentPropertyJoinColumnOverride != null) {
            override = this.currentPropertyJoinColumnOverride.get(propertyName);
        }
        if (override == null && this.holderJoinColumnOverride != null) {
            override = this.holderJoinColumnOverride.get(propertyName);
        }
        return override;
    }

    @Override
    public ForeignKey getOverriddenForeignKey(String propertyName) {
        ForeignKey result = this.getExactOverriddenForeignKey(propertyName);
        if (result == null && propertyName.contains(".collection&&element.")) {
            result = this.getExactOverriddenForeignKey(propertyName.replace(".collection&&element.", "."));
        }
        return result;
    }

    private ForeignKey getExactOverriddenForeignKey(String propertyName) {
        ForeignKey override = null;
        if (this.parent != null) {
            override = this.parent.getExactOverriddenForeignKey(propertyName);
        }
        if (override == null && this.currentPropertyForeignKeyOverride != null) {
            override = this.currentPropertyForeignKeyOverride.get(propertyName);
        }
        if (override == null && this.holderForeignKeyOverride != null) {
            override = this.holderForeignKeyOverride.get(propertyName);
        }
        return override;
    }

    @Override
    public JoinTable getJoinTable(XProperty property) {
        String propertyName = StringHelper.qualify(this.getPath(), property.getName());
        JoinTable result = this.getOverriddenJoinTable(propertyName);
        if (result == null) {
            result = (JoinTable)property.getAnnotation(JoinTable.class);
        }
        return result;
    }

    public JoinTable getOverriddenJoinTable(String propertyName) {
        JoinTable result = this.getExactOverriddenJoinTable(propertyName);
        if (result == null && propertyName.contains(".collection&&element.")) {
            result = this.getExactOverriddenJoinTable(propertyName.replace(".collection&&element.", "."));
        }
        return result;
    }

    private JoinTable getExactOverriddenJoinTable(String propertyName) {
        JoinTable override = null;
        if (this.parent != null) {
            override = this.parent.getExactOverriddenJoinTable(propertyName);
        }
        if (override == null && this.currentPropertyJoinTableOverride != null) {
            override = this.currentPropertyJoinTableOverride.get(propertyName);
        }
        if (override == null && this.holderJoinTableOverride != null) {
            override = this.holderJoinTableOverride.get(propertyName);
        }
        return override;
    }

    private void buildHierarchyColumnOverride(XClass element) {
        Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
        Map<String, JoinColumn[]> joinColumnOverride = new HashMap<String, JoinColumn[]>();
        Map<String, JoinTable> joinTableOverride = new HashMap<String, JoinTable>();
        Map<String, ForeignKey> foreignKeyOverride = new HashMap<String, ForeignKey>();
        for (XClass current = element; current != null && !this.context.getBootstrapContext().getReflectionManager().toXClass(Object.class).equals(current); current = current.getSuperclass()) {
            if (!current.isAnnotationPresent(Entity.class) && !current.isAnnotationPresent(MappedSuperclass.class) && !current.isAnnotationPresent(Embeddable.class)) continue;
            Map<String, Column[]> currentOverride = AbstractPropertyHolder.buildColumnOverride((XAnnotatedElement)current, this.getPath());
            Map<String, JoinColumn[]> currentJoinOverride = AbstractPropertyHolder.buildJoinColumnOverride((XAnnotatedElement)current, this.getPath());
            Map<String, JoinTable> currentJoinTableOverride = AbstractPropertyHolder.buildJoinTableOverride((XAnnotatedElement)current, this.getPath());
            Map<String, ForeignKey> currentForeignKeyOverride = AbstractPropertyHolder.buildForeignKeyOverride((XAnnotatedElement)current, this.getPath());
            currentOverride.putAll(columnOverride);
            currentJoinOverride.putAll(joinColumnOverride);
            currentJoinTableOverride.putAll(joinTableOverride);
            currentForeignKeyOverride.putAll(foreignKeyOverride);
            columnOverride = currentOverride;
            joinColumnOverride = currentJoinOverride;
            joinTableOverride = currentJoinTableOverride;
            foreignKeyOverride = currentForeignKeyOverride;
        }
        this.holderColumnOverride = columnOverride.size() > 0 ? columnOverride : null;
        this.holderJoinColumnOverride = joinColumnOverride.size() > 0 ? joinColumnOverride : null;
        this.holderJoinTableOverride = joinTableOverride.size() > 0 ? joinTableOverride : null;
        this.holderForeignKeyOverride = foreignKeyOverride.size() > 0 ? foreignKeyOverride : null;
    }

    private static Map<String, Column[]> buildColumnOverride(XAnnotatedElement element, String path) {
        HashMap<String, Column[]> columnOverride = new HashMap<String, Column[]>();
        if (element != null) {
            AttributeOverride singleOverride = (AttributeOverride)element.getAnnotation(AttributeOverride.class);
            AttributeOverrides multipleOverrides = (AttributeOverrides)element.getAnnotation(AttributeOverrides.class);
            AttributeOverride[] overrides = singleOverride != null ? new AttributeOverride[]{singleOverride} : (multipleOverrides != null ? multipleOverrides.value() : null);
            if (overrides != null) {
                HashMap<String, ArrayList<Column>> columnOverrideList = new HashMap<String, ArrayList<Column>>();
                for (AttributeOverride depAttr : overrides) {
                    String qualifiedName = StringHelper.qualify(path, depAttr.name());
                    if (columnOverrideList.containsKey(qualifiedName)) {
                        ((List)columnOverrideList.get(qualifiedName)).add(depAttr.column());
                        continue;
                    }
                    columnOverrideList.put(qualifiedName, new ArrayList<Column>(Arrays.asList(depAttr.column())));
                }
                for (Map.Entry entry : columnOverrideList.entrySet()) {
                    columnOverride.put((String)entry.getKey(), ((List)entry.getValue()).toArray(new Column[((List)entry.getValue()).size()]));
                }
            }
        }
        return columnOverride;
    }

    private static Map<String, JoinColumn[]> buildJoinColumnOverride(XAnnotatedElement element, String path) {
        AssociationOverride[] overrides;
        HashMap<String, JoinColumn[]> columnOverride = new HashMap<String, JoinColumn[]>();
        if (element != null && (overrides = AbstractPropertyHolder.buildAssociationOverrides(element, path)) != null) {
            for (AssociationOverride depAttr : overrides) {
                columnOverride.put(StringHelper.qualify(path, depAttr.name()), depAttr.joinColumns());
            }
        }
        return columnOverride;
    }

    private static Map<String, ForeignKey> buildForeignKeyOverride(XAnnotatedElement element, String path) {
        AssociationOverride[] overrides;
        HashMap<String, ForeignKey> foreignKeyOverride = new HashMap<String, ForeignKey>();
        if (element != null && (overrides = AbstractPropertyHolder.buildAssociationOverrides(element, path)) != null) {
            for (AssociationOverride depAttr : overrides) {
                foreignKeyOverride.put(StringHelper.qualify(path, depAttr.name()), depAttr.foreignKey());
            }
        }
        return foreignKeyOverride;
    }

    private static AssociationOverride[] buildAssociationOverrides(XAnnotatedElement element, String path) {
        AssociationOverride singleOverride = (AssociationOverride)element.getAnnotation(AssociationOverride.class);
        AssociationOverrides pluralOverrides = (AssociationOverrides)element.getAnnotation(AssociationOverrides.class);
        AssociationOverride[] overrides = singleOverride != null ? new AssociationOverride[]{singleOverride} : (pluralOverrides != null ? pluralOverrides.value() : null);
        return overrides;
    }

    private static Map<String, JoinTable> buildJoinTableOverride(XAnnotatedElement element, String path) {
        AssociationOverride[] overrides;
        HashMap<String, JoinTable> tableOverride = new HashMap<String, JoinTable>();
        if (element != null && (overrides = AbstractPropertyHolder.buildAssociationOverrides(element, path)) != null) {
            for (AssociationOverride depAttr : overrides) {
                if (depAttr.joinColumns().length != 0) continue;
                tableOverride.put(StringHelper.qualify(path, depAttr.name()), depAttr.joinTable());
            }
        }
        return tableOverride;
    }

    @Override
    public void setParentProperty(String parentProperty) {
        throw new AssertionFailure("Setting the parent property to a non component");
    }
}

