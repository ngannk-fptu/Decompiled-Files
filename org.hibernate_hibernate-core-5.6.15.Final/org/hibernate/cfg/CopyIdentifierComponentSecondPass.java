/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.jboss.logging.Logger;

public class CopyIdentifierComponentSecondPass
extends FkSecondPass {
    private static final Logger log = Logger.getLogger(CopyIdentifierComponentSecondPass.class);
    private final String referencedEntityName;
    private final Component component;
    private final MetadataBuildingContext buildingContext;
    private final Ejb3JoinColumn[] joinColumns;

    public CopyIdentifierComponentSecondPass(Component comp, String referencedEntityName, Ejb3JoinColumn[] joinColumns, MetadataBuildingContext buildingContext) {
        super(comp, joinColumns);
        this.component = comp;
        this.referencedEntityName = referencedEntityName;
        this.buildingContext = buildingContext;
        this.joinColumns = joinColumns;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    @Override
    public boolean isInPrimaryKey() {
        return true;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        Ejb3JoinColumn joinColumn;
        String referencedColumnName;
        PersistentClass referencedPersistentClass = (PersistentClass)persistentClasses.get(this.referencedEntityName);
        if (referencedPersistentClass == null) {
            throw new AnnotationException("Unknown entity name: " + this.referencedEntityName);
        }
        if (!(referencedPersistentClass.getIdentifier() instanceof Component)) {
            throw new AssertionFailure("Unexpected identifier type on the referenced entity when mapping a @MapsId: " + this.referencedEntityName);
        }
        Component referencedComponent = (Component)referencedPersistentClass.getIdentifier();
        Iterator properties = referencedComponent.getPropertyIterator();
        boolean isExplicitReference = true;
        HashMap<String, Ejb3JoinColumn> columnByReferencedName = new HashMap<String, Ejb3JoinColumn>(this.joinColumns.length);
        Ejb3JoinColumn[] ejb3JoinColumnArray = this.joinColumns;
        int n = ejb3JoinColumnArray.length;
        for (int i = 0; i < n && (referencedColumnName = (joinColumn = ejb3JoinColumnArray[i]).getReferencedColumn()) != null && !BinderHelper.isEmptyAnnotationValue(referencedColumnName); ++i) {
            columnByReferencedName.put(referencedColumnName.toLowerCase(Locale.ROOT), joinColumn);
        }
        AtomicInteger index = new AtomicInteger(0);
        if (columnByReferencedName.isEmpty()) {
            isExplicitReference = false;
            for (Ejb3JoinColumn joinColumn2 : this.joinColumns) {
                columnByReferencedName.put(String.valueOf(index.get()), joinColumn2);
                index.getAndIncrement();
            }
            index.set(0);
        }
        while (properties.hasNext()) {
            Property property;
            Property referencedProperty = (Property)properties.next();
            if (referencedProperty.isComposite()) {
                property = this.createComponentProperty(referencedPersistentClass, isExplicitReference, columnByReferencedName, index, referencedProperty);
                this.component.addProperty(property);
                continue;
            }
            property = this.createSimpleProperty(referencedPersistentClass, isExplicitReference, columnByReferencedName, index, referencedProperty);
            this.component.addProperty(property);
        }
    }

    private Property createComponentProperty(PersistentClass referencedPersistentClass, boolean isExplicitReference, Map<String, Ejb3JoinColumn> columnByReferencedName, AtomicInteger index, Property referencedProperty) {
        Property property = new Property();
        property.setName(referencedProperty.getName());
        property.setPersistentClass(this.component.getOwner());
        property.setPropertyAccessorName(referencedProperty.getPropertyAccessorName());
        Component value = new Component(this.buildingContext, this.component.getOwner());
        property.setValue(value);
        Component referencedValue = (Component)referencedProperty.getValue();
        value.setTypeName(referencedValue.getTypeName());
        value.setTypeParameters(referencedValue.getTypeParameters());
        value.setComponentClassName(referencedValue.getComponentClassName());
        Iterator propertyIterator = referencedValue.getPropertyIterator();
        while (propertyIterator.hasNext()) {
            Property componentProperty;
            Property referencedComponentProperty = (Property)propertyIterator.next();
            if (referencedComponentProperty.isComposite()) {
                componentProperty = this.createComponentProperty(referencedValue.getOwner(), isExplicitReference, columnByReferencedName, index, referencedComponentProperty);
                value.addProperty(componentProperty);
                continue;
            }
            componentProperty = this.createSimpleProperty(referencedValue.getOwner(), isExplicitReference, columnByReferencedName, index, referencedComponentProperty);
            value.addProperty(componentProperty);
        }
        return property;
    }

    private Property createSimpleProperty(PersistentClass referencedPersistentClass, boolean isExplicitReference, Map<String, Ejb3JoinColumn> columnByReferencedName, AtomicInteger index, Property referencedProperty) {
        Property property = new Property();
        property.setName(referencedProperty.getName());
        property.setPersistentClass(this.component.getOwner());
        property.setPropertyAccessorName(referencedProperty.getPropertyAccessorName());
        SimpleValue value = new SimpleValue(this.buildingContext, this.component.getTable());
        property.setValue(value);
        SimpleValue referencedValue = (SimpleValue)referencedProperty.getValue();
        value.setTypeName(referencedValue.getTypeName());
        value.setTypeParameters(referencedValue.getTypeParameters());
        Iterator<Selectable> columns = referencedValue.getColumnIterator();
        if (this.joinColumns[0].isNameDeferred()) {
            this.joinColumns[0].copyReferencedStructureAndCreateDefaultJoinColumns(referencedPersistentClass, columns, value);
        } else {
            while (columns.hasNext()) {
                Ejb3JoinColumn joinColumn;
                Selectable selectable = columns.next();
                if (!Column.class.isInstance(selectable)) {
                    log.debug((Object)"Encountered formula definition; skipping");
                    continue;
                }
                Column column = (Column)selectable;
                String logicalColumnName = null;
                if (isExplicitReference) {
                    logicalColumnName = column.getName();
                    joinColumn = columnByReferencedName.get(logicalColumnName.toLowerCase(Locale.ROOT));
                } else {
                    joinColumn = columnByReferencedName.get(String.valueOf(index.get()));
                    index.getAndIncrement();
                }
                if (joinColumn == null && !this.joinColumns[0].isNameDeferred()) {
                    throw new AnnotationException(isExplicitReference ? "Unable to find column reference in the @MapsId mapping: " + logicalColumnName : "Implicit column reference in the @MapsId mapping fails, try to use explicit referenceColumnNames: " + this.referencedEntityName);
                }
                String columnName = joinColumn == null || joinColumn.isNameDeferred() ? "tata_" + column.getName() : joinColumn.getName();
                Database database = this.buildingContext.getMetadataCollector().getDatabase();
                PhysicalNamingStrategy physicalNamingStrategy = this.buildingContext.getBuildingOptions().getPhysicalNamingStrategy();
                Identifier explicitName = database.toIdentifier(columnName);
                Identifier physicalName = physicalNamingStrategy.toPhysicalColumnName(explicitName, database.getJdbcEnvironment());
                value.addColumn(new Column(physicalName.render(database.getDialect())));
                if (joinColumn != null) {
                    this.applyComponentColumnSizeValueToJoinColumn(column, joinColumn);
                    joinColumn.linkWithValue(value);
                }
                column.setValue(value);
            }
        }
        return property;
    }

    private void applyComponentColumnSizeValueToJoinColumn(Column column, Ejb3JoinColumn joinColumn) {
        Column mappingColumn = joinColumn.getMappingColumn();
        mappingColumn.setLength(column.getLength());
        mappingColumn.setPrecision(column.getPrecision());
        mappingColumn.setScale(column.getScale());
    }
}

