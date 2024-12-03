/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  org.hibernate.annotations.common.reflection.XClass
 */
package org.hibernate.boot.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.AttributeConverter;
import org.hibernate.DuplicateMappingException;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.convert.internal.InstanceBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterAutoApplyHandler;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.JPAIndexHolder;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.UniqueConstraintHolder;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

public interface InFlightMetadataCollector
extends Mapping,
MetadataImplementor {
    public BootstrapContext getBootstrapContext();

    public void addEntityBinding(PersistentClass var1) throws DuplicateMappingException;

    public Map<String, PersistentClass> getEntityBindingMap();

    public void addImport(String var1, String var2) throws DuplicateMappingException;

    public void addCollectionBinding(Collection var1) throws DuplicateMappingException;

    public Table addTable(String var1, String var2, String var3, String var4, boolean var5);

    public Table addDenormalizedTable(String var1, String var2, String var3, boolean var4, String var5, Table var6) throws DuplicateMappingException;

    public void addNamedQuery(NamedQueryDefinition var1) throws DuplicateMappingException;

    public void addNamedNativeQuery(NamedSQLQueryDefinition var1) throws DuplicateMappingException;

    public void addResultSetMapping(ResultSetMappingDefinition var1) throws DuplicateMappingException;

    public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition var1) throws DuplicateMappingException;

    public void addNamedEntityGraph(NamedEntityGraphDefinition var1);

    public void addTypeDefinition(TypeDefinition var1);

    public void addFilterDefinition(FilterDefinition var1);

    public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject var1);

    public void addFetchProfile(FetchProfile var1);

    public void addIdentifierGenerator(IdentifierGeneratorDefinition var1);

    @Deprecated
    default public void addAttributeConverter(AttributeConverterDefinition converter) {
        this.addAttributeConverter(new InstanceBasedConverterDescriptor(converter.getAttributeConverter(), this.getBootstrapContext().getClassmateContext()));
    }

    public void addAttributeConverter(ConverterDescriptor var1);

    public void addAttributeConverter(Class<? extends AttributeConverter> var1);

    public ConverterAutoApplyHandler getAttributeConverterAutoApplyHandler();

    public void addSecondPass(SecondPass var1);

    public void addSecondPass(SecondPass var1, boolean var2);

    public void addTableNameBinding(Identifier var1, Table var2);

    public void addTableNameBinding(String var1, String var2, String var3, String var4, Table var5);

    public String getLogicalTableName(Table var1);

    public String getPhysicalTableName(Identifier var1);

    public String getPhysicalTableName(String var1);

    public void addColumnNameBinding(Table var1, Identifier var2, Column var3);

    public void addColumnNameBinding(Table var1, String var2, Column var3);

    public String getPhysicalColumnName(Table var1, Identifier var2) throws MappingException;

    public String getPhysicalColumnName(Table var1, String var2) throws MappingException;

    public String getLogicalColumnName(Table var1, Identifier var2);

    public String getLogicalColumnName(Table var1, String var2);

    public void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition var1);

    public void addDefaultQuery(NamedQueryDefinition var1);

    public void addDefaultNamedNativeQuery(NamedSQLQueryDefinition var1);

    public void addDefaultResultSetMapping(ResultSetMappingDefinition var1);

    public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition var1);

    public void addAnyMetaDef(AnyMetaDef var1);

    public AnyMetaDef getAnyMetaDef(String var1);

    public AnnotatedClassType addClassType(XClass var1);

    public AnnotatedClassType getClassType(XClass var1);

    public void addMappedSuperclass(Class var1, MappedSuperclass var2);

    public MappedSuperclass getMappedSuperclass(Class var1);

    public PropertyData getPropertyAnnotatedWithMapsId(XClass var1, String var2);

    public void addPropertyAnnotatedWithMapsId(XClass var1, PropertyData var2);

    public void addPropertyAnnotatedWithMapsIdSpecj(XClass var1, PropertyData var2, String var3);

    public void addToOneAndIdProperty(XClass var1, PropertyData var2);

    public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass var1, String var2);

    public boolean isInSecondPass();

    public NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String var1);

    public void registerNaturalIdUniqueKeyBinder(String var1, NaturalIdUniqueKeyBinder var2);

    @Deprecated
    public ClassmateContext getClassmateContext();

    public void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler var1);

    public void addPropertyReference(String var1, String var2);

    public void addUniquePropertyReference(String var1, String var2);

    public void addPropertyReferencedAssociation(String var1, String var2, String var3);

    public String getPropertyReferencedAssociation(String var1, String var2);

    public void addMappedBy(String var1, String var2, String var3);

    public String getFromMappedBy(String var1, String var2);

    public void addUniqueConstraints(Table var1, List var2);

    public void addUniqueConstraintHolders(Table var1, List<UniqueConstraintHolder> var2);

    public void addJpaIndexHolders(Table var1, List<JPAIndexHolder> var2);

    public EntityTableXref getEntityTableXref(String var1);

    public EntityTableXref addEntityTableXref(String var1, Identifier var2, Table var3, EntityTableXref var4);

    public Map<String, Join> getJoins(String var1);

    public static class DuplicateSecondaryTableException
    extends HibernateException {
        private final Identifier tableName;

        public DuplicateSecondaryTableException(Identifier tableName) {
            super(String.format(Locale.ENGLISH, "Table with that name [%s] already associated with entity", tableName.render()));
            this.tableName = tableName;
        }
    }

    public static interface EntityTableXref {
        public void addSecondaryTable(LocalMetadataBuildingContext var1, Identifier var2, Join var3);

        public void addSecondaryTable(QualifiedTableName var1, Join var2);

        public Table resolveTable(Identifier var1);

        public Table getPrimaryTable();

        public Join locateJoin(Identifier var1);
    }

    public static interface DelayedPropertyReferenceHandler
    extends Serializable {
        public void process(InFlightMetadataCollector var1);
    }
}

