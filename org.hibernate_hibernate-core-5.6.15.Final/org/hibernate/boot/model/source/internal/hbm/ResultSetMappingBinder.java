/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryCollectionLoadReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryJoinReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryPropertyReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryReturnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryScalarReturnType;
import org.hibernate.boot.jaxb.hbm.spi.NativeQueryNonScalarRootReturn;
import org.hibernate.boot.jaxb.hbm.spi.ResultSetMappingBindingDefinition;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.type.Type;

public abstract class ResultSetMappingBinder {
    public static ResultSetMappingDefinition bind(ResultSetMappingBindingDefinition resultSetMappingSource, HbmLocalMetadataBuildingContext context) {
        if (resultSetMappingSource.getName() == null) {
            throw new MappingException("ResultSet mapping did not specify name", context.getOrigin());
        }
        ResultSetMappingDefinition binding = new ResultSetMappingDefinition(resultSetMappingSource.getName());
        ResultSetMappingBinder.bind(resultSetMappingSource, binding, context);
        return binding;
    }

    public static ResultSetMappingDefinition bind(ResultSetMappingBindingDefinition resultSetMappingSource, HbmLocalMetadataBuildingContext context, String prefix) {
        if (StringHelper.isEmpty(prefix)) {
            throw new AssertionFailure("Passed prefix was null; perhaps you meant to call the alternate #bind form?");
        }
        String resultSetName = prefix + '.' + resultSetMappingSource.getName();
        ResultSetMappingDefinition binding = new ResultSetMappingDefinition(resultSetName);
        ResultSetMappingBinder.bind(resultSetMappingSource, binding, context);
        return binding;
    }

    private static void bind(ResultSetMappingBindingDefinition resultSetMappingSource, ResultSetMappingDefinition binding, HbmLocalMetadataBuildingContext context) {
        int cnt = 0;
        for (Object valueMappingSource : resultSetMappingSource.getValueMappingSources()) {
            if (JaxbHbmNativeQueryReturnType.class.isInstance(valueMappingSource)) {
                binding.addQueryReturn(ResultSetMappingBinder.extractReturnDescription((JaxbHbmNativeQueryReturnType)valueMappingSource, context, cnt++));
                continue;
            }
            if (JaxbHbmNativeQueryCollectionLoadReturnType.class.isInstance(valueMappingSource)) {
                binding.addQueryReturn(ResultSetMappingBinder.extractReturnDescription((JaxbHbmNativeQueryCollectionLoadReturnType)valueMappingSource, context, cnt++));
                continue;
            }
            if (JaxbHbmNativeQueryJoinReturnType.class.isInstance(valueMappingSource)) {
                binding.addQueryReturn(ResultSetMappingBinder.extractReturnDescription((JaxbHbmNativeQueryJoinReturnType)valueMappingSource, context, cnt++));
                continue;
            }
            if (!JaxbHbmNativeQueryScalarReturnType.class.isInstance(valueMappingSource)) continue;
            binding.addQueryReturn(ResultSetMappingBinder.extractReturnDescription((JaxbHbmNativeQueryScalarReturnType)valueMappingSource, context));
        }
    }

    public static NativeSQLQueryScalarReturn extractReturnDescription(JaxbHbmNativeQueryScalarReturnType rtnSource, HbmLocalMetadataBuildingContext context) {
        String column = rtnSource.getColumn();
        String typeName = rtnSource.getType();
        Type type = null;
        if (typeName != null && (type = context.getMetadataCollector().getTypeResolver().heuristicType(typeName)) == null) {
            throw new MappingException(String.format("Unable to resolve type [%s] specified for native query scalar return", typeName), context.getOrigin());
        }
        return new NativeSQLQueryScalarReturn(column, type);
    }

    public static NativeSQLQueryRootReturn extractReturnDescription(JaxbHbmNativeQueryReturnType rtnSource, HbmLocalMetadataBuildingContext context, int queryReturnPosition) {
        String alias = rtnSource.getAlias();
        if (StringHelper.isEmpty(alias)) {
            alias = "alias_" + queryReturnPosition;
        }
        String entityName = context.determineEntityName(rtnSource.getEntityName(), rtnSource.getClazz());
        PersistentClass pc = context.getMetadataCollector().getEntityBinding(entityName);
        return new NativeSQLQueryRootReturn(alias, entityName, ResultSetMappingBinder.extractPropertyResults(alias, rtnSource, pc, context), rtnSource.getLockMode());
    }

    public static NativeSQLQueryJoinReturn extractReturnDescription(JaxbHbmNativeQueryJoinReturnType rtnSource, HbmLocalMetadataBuildingContext context, int queryReturnPosition) {
        int dot = rtnSource.getProperty().lastIndexOf(46);
        if (dot == -1) {
            throw new MappingException(String.format(Locale.ENGLISH, "Role attribute for sql query return [%s] not formatted correctly {owningAlias.propertyName}", rtnSource.getAlias()), context.getOrigin());
        }
        String roleOwnerAlias = rtnSource.getProperty().substring(0, dot);
        String roleProperty = rtnSource.getProperty().substring(dot + 1);
        return new NativeSQLQueryJoinReturn(rtnSource.getAlias(), roleOwnerAlias, roleProperty, ResultSetMappingBinder.extractPropertyResults(rtnSource.getAlias(), rtnSource, null, context), rtnSource.getLockMode());
    }

    public static NativeSQLQueryReturn extractReturnDescription(JaxbHbmNativeQueryCollectionLoadReturnType rtnSource, HbmLocalMetadataBuildingContext context, int queryReturnPosition) {
        int dot = rtnSource.getRole().lastIndexOf(46);
        if (dot == -1) {
            throw new MappingException(String.format(Locale.ENGLISH, "Collection attribute for sql query return [%s] not formatted correctly {OwnerClassName.propertyName}", rtnSource.getAlias()), context.getOrigin());
        }
        String ownerClassName = context.findEntityBinding(null, rtnSource.getRole().substring(0, dot)).getClassName();
        String ownerPropertyName = rtnSource.getRole().substring(dot + 1);
        return new NativeSQLQueryCollectionReturn(rtnSource.getAlias(), ownerClassName, ownerPropertyName, ResultSetMappingBinder.extractPropertyResults(rtnSource.getAlias(), rtnSource, null, context), rtnSource.getLockMode());
    }

    private static Map<String, String[]> extractPropertyResults(String alias, JaxbHbmNativeQueryReturnType rtnSource, PersistentClass pc, HbmLocalMetadataBuildingContext context) {
        Map<String, String[]> results = ResultSetMappingBinder.extractPropertyResults(alias, (NativeQueryNonScalarRootReturn)rtnSource, pc, context);
        if (rtnSource.getReturnDiscriminator() != null) {
            String column;
            if (results == null) {
                results = new HashMap<String, String[]>();
            }
            if ((column = rtnSource.getReturnDiscriminator().getColumn()) == null) {
                throw new MappingException(String.format(Locale.ENGLISH, "return-discriminator [%s (%s)] did not specify column", pc.getEntityName(), alias), context.getOrigin());
            }
            results.put("class", new String[]{column});
        }
        return results;
    }

    private static Map<String, String[]> extractPropertyResults(String alias, NativeQueryNonScalarRootReturn rtnSource, PersistentClass pc, HbmLocalMetadataBuildingContext context) {
        if (CollectionHelper.isEmpty(rtnSource.getReturnProperty())) {
            return null;
        }
        HashMap<String, String[]> results = new HashMap<String, String[]>();
        ArrayList<JaxbHbmNativeQueryPropertyReturnType> propertyReturnSources = new ArrayList<JaxbHbmNativeQueryPropertyReturnType>();
        ArrayList<String> propertyNames = new ArrayList<String>();
        for (JaxbHbmNativeQueryPropertyReturnType propertyReturnSource : rtnSource.getReturnProperty()) {
            Iterator parentPropItr;
            int n = propertyReturnSource.getName().lastIndexOf(46);
            if (pc == null || n == -1) {
                propertyReturnSources.add(propertyReturnSource);
                propertyNames.add(propertyReturnSource.getName());
                continue;
            }
            String reducedName = propertyReturnSource.getName().substring(0, n);
            Value value = pc.getRecursiveProperty(reducedName).getValue();
            if (value instanceof Component) {
                Component comp = (Component)value;
                parentPropItr = comp.getPropertyIterator();
            } else if (value instanceof ToOne) {
                ToOne toOne = (ToOne)value;
                PersistentClass referencedPc = context.getMetadataCollector().getEntityBinding(toOne.getReferencedEntityName());
                if (toOne.getReferencedPropertyName() != null) {
                    try {
                        parentPropItr = ((Component)referencedPc.getRecursiveProperty(toOne.getReferencedPropertyName()).getValue()).getPropertyIterator();
                    }
                    catch (ClassCastException e) {
                        throw new MappingException("dotted notation reference neither a component nor a many/one to one", e, context.getOrigin());
                    }
                } else {
                    try {
                        if (referencedPc.getIdentifierMapper() == null) {
                            parentPropItr = ((Component)referencedPc.getIdentifierProperty().getValue()).getPropertyIterator();
                        }
                        parentPropItr = referencedPc.getIdentifierMapper().getPropertyIterator();
                    }
                    catch (ClassCastException e) {
                        throw new MappingException("dotted notation reference neither a component nor a many/one to one", e, context.getOrigin());
                    }
                }
            } else {
                throw new MappingException("dotted notation reference neither a component nor a many/one to one", context.getOrigin());
            }
            boolean hasFollowers = false;
            ArrayList<String> followers = new ArrayList<String>();
            while (parentPropItr.hasNext()) {
                Property parentProperty = (Property)parentPropItr.next();
                String currentPropertyName = parentProperty.getName();
                String currentName = reducedName + '.' + currentPropertyName;
                if (hasFollowers) {
                    followers.add(currentName);
                }
                if (!propertyReturnSource.getName().equals(currentName)) continue;
                hasFollowers = true;
            }
            int index = propertyNames.size();
            for (String follower : followers) {
                int currentIndex = ResultSetMappingBinder.getIndexOfFirstMatchingProperty(propertyNames, follower);
                index = currentIndex != -1 && currentIndex < index ? currentIndex : index;
            }
            propertyNames.add(index, propertyReturnSource.getName());
            propertyReturnSources.add(index, propertyReturnSource);
        }
        HashSet<String> uniqueReturnProperty = new HashSet<String>();
        for (JaxbHbmNativeQueryPropertyReturnType jaxbHbmNativeQueryPropertyReturnType : propertyReturnSources) {
            String name = jaxbHbmNativeQueryPropertyReturnType.getName();
            if ("class".equals(name)) {
                throw new MappingException("class is not a valid property name to use in a <return-property>, use <return-discriminator> instead", context.getOrigin());
            }
            ArrayList<String> allResultColumns = ResultSetMappingBinder.extractResultColumns(jaxbHbmNativeQueryPropertyReturnType);
            if (allResultColumns.isEmpty()) {
                throw new MappingException(String.format(Locale.ENGLISH, "return-property [alias=%s, property=%s] must specify at least one column or return-column name", alias, jaxbHbmNativeQueryPropertyReturnType.getName()), context.getOrigin());
            }
            if (uniqueReturnProperty.contains(name)) {
                throw new MappingException(String.format(Locale.ENGLISH, "Duplicate return-property [alias=%s] : %s", alias, jaxbHbmNativeQueryPropertyReturnType.getName()), context.getOrigin());
            }
            uniqueReturnProperty.add(name);
            ArrayList intermediateResults = (ArrayList)results.get(name);
            if (intermediateResults == null) {
                results.put(name, (String[])allResultColumns);
                continue;
            }
            intermediateResults.addAll(allResultColumns);
        }
        for (Object object : results.entrySet()) {
            Map.Entry entry = (Map.Entry)object;
            if (!(entry.getValue() instanceof ArrayList)) continue;
            ArrayList list = (ArrayList)entry.getValue();
            entry.setValue(list.toArray(new String[list.size()]));
        }
        return results.isEmpty() ? Collections.EMPTY_MAP : results;
    }

    private static int getIndexOfFirstMatchingProperty(List propertyNames, String follower) {
        int propertySize = propertyNames.size();
        for (int propIndex = 0; propIndex < propertySize; ++propIndex) {
            if (!((String)propertyNames.get(propIndex)).startsWith(follower)) continue;
            return propIndex;
        }
        return -1;
    }

    private static ArrayList<String> extractResultColumns(JaxbHbmNativeQueryPropertyReturnType propertyReturnSource) {
        String column = ResultSetMappingBinder.unquote(propertyReturnSource.getColumn());
        ArrayList<String> allResultColumns = new ArrayList<String>();
        if (column != null) {
            allResultColumns.add(column);
        }
        for (JaxbHbmNativeQueryPropertyReturnType.JaxbHbmReturnColumn returnColumnSource : propertyReturnSource.getReturnColumn()) {
            allResultColumns.add(ResultSetMappingBinder.unquote(returnColumnSource.getName()));
        }
        return allResultColumns;
    }

    private static String unquote(String name) {
        if (name != null && name.charAt(0) == '`') {
            name = name.substring(1, name.length() - 1);
        }
        return name;
    }
}

