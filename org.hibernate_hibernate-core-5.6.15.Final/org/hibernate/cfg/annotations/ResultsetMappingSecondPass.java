/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ColumnResult
 *  javax.persistence.EntityResult
 *  javax.persistence.FieldResult
 *  javax.persistence.SqlResultSetMapping
 */
package org.hibernate.cfg.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.QuerySecondPass;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class ResultsetMappingSecondPass
implements QuerySecondPass {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ResultsetMappingSecondPass.class);
    private final SqlResultSetMapping ann;
    private final MetadataBuildingContext context;
    private final boolean isDefault;

    public ResultsetMappingSecondPass(SqlResultSetMapping ann, MetadataBuildingContext context, boolean isDefault) {
        this.ann = ann;
        this.context = context;
        this.isDefault = isDefault;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        if (this.ann == null) {
            return;
        }
        ResultSetMappingDefinition definition = new ResultSetMappingDefinition(this.ann.name());
        LOG.debugf("Binding result set mapping: %s", definition.getName());
        int entityAliasIndex = 0;
        for (EntityResult entityResult : this.ann.entities()) {
            ArrayList<FieldResult> properties = new ArrayList<FieldResult>();
            ArrayList<String> propertyNames = new ArrayList<String>();
            for (FieldResult fieldResult : entityResult.fields()) {
                String name = fieldResult.name();
                if (name.indexOf(46) == -1) {
                    properties.add(fieldResult);
                    propertyNames.add(name);
                    continue;
                }
                PersistentClass pc = this.context.getMetadataCollector().getEntityBinding(entityResult.entityClass().getName());
                if (pc == null) {
                    throw new MappingException(String.format(Locale.ENGLISH, "Could not resolve entity [%s] referenced in SqlResultSetMapping [%s]", entityResult.entityClass().getName(), this.ann.name()));
                }
                int dotIndex = name.lastIndexOf(46);
                String reducedName = name.substring(0, dotIndex);
                Iterator parentPropItr = this.getSubPropertyIterator(pc, reducedName);
                List<String> followers = this.getFollowers(parentPropItr, reducedName, name);
                int index = propertyNames.size();
                for (String follower : followers) {
                    int currentIndex = ResultsetMappingSecondPass.getIndexOfFirstMatchingProperty(propertyNames, follower);
                    index = currentIndex != -1 && currentIndex < index ? currentIndex : index;
                }
                propertyNames.add(index, name);
                properties.add(index, fieldResult);
            }
            HashSet<String> uniqueReturnProperty = new HashSet<String>();
            HashMap<String, ArrayList<String>> propertyResultsTmp = new HashMap<String, ArrayList<String>>();
            for (Object e : properties) {
                FieldResult propertyresult = (FieldResult)e;
                String name = propertyresult.name();
                if ("class".equals(name)) {
                    throw new MappingException("class is not a valid property name to use in a @FieldResult, use @Entity(discriminatorColumn) instead");
                }
                if (uniqueReturnProperty.contains(name)) {
                    throw new MappingException("duplicate @FieldResult for property " + name + " on @Entity " + entityResult.entityClass().getName() + " in " + this.ann.name());
                }
                uniqueReturnProperty.add(name);
                String quotingNormalizedColumnName = this.normalizeColumnQuoting(propertyresult.column());
                String key = StringHelper.root(name);
                ArrayList<String> intermediateResults = (ArrayList<String>)propertyResultsTmp.get(key);
                if (intermediateResults == null) {
                    intermediateResults = new ArrayList<String>();
                    propertyResultsTmp.put(key, intermediateResults);
                }
                intermediateResults.add(quotingNormalizedColumnName);
            }
            Map<String, String[]> propertyResults = new HashMap<String, String[]>();
            for (Map.Entry entry : propertyResultsTmp.entrySet()) {
                propertyResults.put((String)entry.getKey(), ((ArrayList)entry.getValue()).toArray(new String[((ArrayList)entry.getValue()).size()]));
            }
            if (!BinderHelper.isEmptyAnnotationValue(entityResult.discriminatorColumn())) {
                String string = this.normalizeColumnQuoting(entityResult.discriminatorColumn());
                propertyResults.put("class", new String[]{string});
            }
            if (propertyResults.isEmpty()) {
                propertyResults = Collections.emptyMap();
            }
            NativeSQLQueryRootReturn nativeSQLQueryRootReturn = new NativeSQLQueryRootReturn("alias" + entityAliasIndex++, entityResult.entityClass().getName(), propertyResults, LockMode.READ);
            definition.addQueryReturn(nativeSQLQueryRootReturn);
        }
        for (EntityResult entityResult : this.ann.columns()) {
            definition.addQueryReturn(new NativeSQLQueryScalarReturn(this.normalizeColumnQuoting(entityResult.name()), entityResult.type() != null ? this.context.getMetadataCollector().getTypeResolver().heuristicType(entityResult.type().getName()) : null));
        }
        for (EntityResult entityResult : this.ann.classes()) {
            ArrayList<NativeSQLQueryScalarReturn> columnReturns = new ArrayList<NativeSQLQueryScalarReturn>();
            for (ColumnResult columnResult : entityResult.columns()) {
                columnReturns.add(new NativeSQLQueryScalarReturn(this.normalizeColumnQuoting(columnResult.name()), columnResult.type() != null ? this.context.getMetadataCollector().getTypeResolver().heuristicType(columnResult.type().getName()) : null));
            }
            definition.addQueryReturn(new NativeSQLQueryConstructorReturn(entityResult.targetClass(), columnReturns));
        }
        if (this.isDefault) {
            this.context.getMetadataCollector().addDefaultResultSetMapping(definition);
        } else {
            this.context.getMetadataCollector().addResultSetMapping(definition);
        }
    }

    private String normalizeColumnQuoting(String name) {
        return this.context.getMetadataCollector().getDatabase().toIdentifier(name).render();
    }

    private List<String> getFollowers(Iterator parentPropIter, String reducedName, String name) {
        boolean hasFollowers = false;
        ArrayList<String> followers = new ArrayList<String>();
        while (parentPropIter.hasNext()) {
            String currentPropertyName = ((Property)parentPropIter.next()).getName();
            String currentName = reducedName + '.' + currentPropertyName;
            if (hasFollowers) {
                followers.add(currentName);
            }
            if (!name.equals(currentName)) continue;
            hasFollowers = true;
        }
        return followers;
    }

    private Iterator getSubPropertyIterator(PersistentClass pc, String reducedName) {
        Iterator parentPropIter;
        Value value = pc.getRecursiveProperty(reducedName).getValue();
        if (value instanceof Component) {
            Component comp = (Component)value;
            parentPropIter = comp.getPropertyIterator();
        } else if (value instanceof ToOne) {
            ToOne toOne = (ToOne)value;
            PersistentClass referencedPc = this.context.getMetadataCollector().getEntityBinding(toOne.getReferencedEntityName());
            if (toOne.getReferencedPropertyName() != null) {
                try {
                    parentPropIter = ((Component)referencedPc.getRecursiveProperty(toOne.getReferencedPropertyName()).getValue()).getPropertyIterator();
                }
                catch (ClassCastException e) {
                    throw new MappingException("dotted notation reference neither a component nor a many/one to one", e);
                }
            } else {
                try {
                    if (referencedPc.getIdentifierMapper() == null) {
                        parentPropIter = ((Component)referencedPc.getIdentifierProperty().getValue()).getPropertyIterator();
                    }
                    parentPropIter = referencedPc.getIdentifierMapper().getPropertyIterator();
                }
                catch (ClassCastException e) {
                    throw new MappingException("dotted notation reference neither a component nor a many/one to one", e);
                }
            }
        } else {
            throw new MappingException("dotted notation reference neither a component nor a many/one to one");
        }
        return parentPropIter;
    }

    private static int getIndexOfFirstMatchingProperty(List propertyNames, String follower) {
        int propertySize = propertyNames.size();
        for (int propIndex = 0; propIndex < propertySize; ++propIndex) {
            if (!((String)propertyNames.get(propIndex)).startsWith(follower)) continue;
            return propIndex;
        }
        return -1;
    }
}

