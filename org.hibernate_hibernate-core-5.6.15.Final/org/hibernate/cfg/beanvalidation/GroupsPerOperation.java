/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.groups.Default
 */
package org.hibernate.cfg.beanvalidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.validation.groups.Default;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.ClassLoaderAccess;

public class GroupsPerOperation {
    private static final String JPA_GROUP_PREFIX = "javax.persistence.validation.group.";
    private static final String JAKARTA_JPA_GROUP_PREFIX = "javax.persistence.validation.group.";
    private static final String HIBERNATE_GROUP_PREFIX = "org.hibernate.validator.group.";
    private static final Class<?>[] DEFAULT_GROUPS = new Class[]{Default.class};
    private static final Class<?>[] EMPTY_GROUPS = new Class[0];
    private Map<Operation, Class<?>[]> groupsPerOperation = new HashMap<Operation, Class<?>[]>(4);

    private GroupsPerOperation() {
    }

    public static GroupsPerOperation from(Map settings, ClassLoaderAccess classLoaderAccess) {
        GroupsPerOperation groupsPerOperation = new GroupsPerOperation();
        GroupsPerOperation.applyOperationGrouping(groupsPerOperation, Operation.INSERT, settings, classLoaderAccess);
        GroupsPerOperation.applyOperationGrouping(groupsPerOperation, Operation.UPDATE, settings, classLoaderAccess);
        GroupsPerOperation.applyOperationGrouping(groupsPerOperation, Operation.DELETE, settings, classLoaderAccess);
        GroupsPerOperation.applyOperationGrouping(groupsPerOperation, Operation.DDL, settings, classLoaderAccess);
        return groupsPerOperation;
    }

    private static void applyOperationGrouping(GroupsPerOperation groupsPerOperation, Operation operation, Map settings, ClassLoaderAccess classLoaderAccess) {
        groupsPerOperation.groupsPerOperation.put(operation, GroupsPerOperation.buildGroupsForOperation(operation, settings, classLoaderAccess));
    }

    public static Class<?>[] buildGroupsForOperation(Operation operation, Map settings, ClassLoaderAccess classLoaderAccess) {
        Object property = settings.get(operation.getGroupPropertyName());
        if (property == null) {
            property = settings.get(operation.getJakartaGroupPropertyName());
        }
        if (property == null) {
            return operation == Operation.DELETE ? EMPTY_GROUPS : DEFAULT_GROUPS;
        }
        if (property instanceof Class[]) {
            return (Class[])property;
        }
        if (property instanceof String) {
            String stringProperty = (String)property;
            String[] groupNames = stringProperty.split(",");
            if (groupNames.length == 1 && groupNames[0].isEmpty()) {
                return EMPTY_GROUPS;
            }
            ArrayList groupsList = new ArrayList(groupNames.length);
            for (String groupName : groupNames) {
                String cleanedGroupName = groupName.trim();
                if (cleanedGroupName.length() <= 0) continue;
                try {
                    groupsList.add(classLoaderAccess.classForName(cleanedGroupName));
                }
                catch (ClassLoadingException e) {
                    throw new HibernateException("Unable to load class " + cleanedGroupName, (Throwable)((Object)e));
                }
            }
            return groupsList.toArray(new Class[groupsList.size()]);
        }
        throw new HibernateException("javax.persistence.validation.group." + operation.getGroupPropertyName() + " is of unknown type: String or Class<?>[] only");
    }

    public Class<?>[] get(Operation operation) {
        return this.groupsPerOperation.get((Object)operation);
    }

    public static enum Operation {
        INSERT("persist", "javax.persistence.validation.group.pre-persist", "javax.persistence.validation.group.pre-persist"),
        UPDATE("update", "javax.persistence.validation.group.pre-update", "javax.persistence.validation.group.pre-update"),
        DELETE("remove", "javax.persistence.validation.group.pre-remove", "javax.persistence.validation.group.pre-remove"),
        DDL("ddl", "org.hibernate.validator.group.ddl", "org.hibernate.validator.group.ddl");

        private final String exposedName;
        private final String groupPropertyName;
        private final String jakartaGroupPropertyName;

        private Operation(String exposedName, String groupProperty, String jakartaGroupPropertyName) {
            this.exposedName = exposedName;
            this.groupPropertyName = groupProperty;
            this.jakartaGroupPropertyName = jakartaGroupPropertyName;
        }

        public String getName() {
            return this.exposedName;
        }

        public String getGroupPropertyName() {
            return this.groupPropertyName;
        }

        public String getJakartaGroupPropertyName() {
            return this.jakartaGroupPropertyName;
        }
    }
}

