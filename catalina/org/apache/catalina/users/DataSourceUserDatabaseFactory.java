/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import org.apache.catalina.users.DataSourceUserDatabase;

public class DataSourceUserDatabaseFactory
implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference)obj;
        if (!"org.apache.catalina.UserDatabase".equals(ref.getClassName())) {
            return null;
        }
        DataSource dataSource = null;
        String dataSourceName = null;
        RefAddr ra = null;
        ra = ref.get("dataSourceName");
        if (ra != null) {
            dataSourceName = ra.getContent().toString();
            dataSource = (DataSource)nameCtx.lookup(dataSourceName);
        }
        DataSourceUserDatabase database = new DataSourceUserDatabase(dataSource, name.toString());
        database.setDataSourceName(dataSourceName);
        ra = ref.get("readonly");
        if (ra != null) {
            database.setReadonly(Boolean.parseBoolean(ra.getContent().toString()));
        }
        if ((ra = ref.get("userTable")) != null) {
            database.setUserTable(ra.getContent().toString());
        }
        if ((ra = ref.get("groupTable")) != null) {
            database.setGroupTable(ra.getContent().toString());
        }
        if ((ra = ref.get("roleTable")) != null) {
            database.setRoleTable(ra.getContent().toString());
        }
        if ((ra = ref.get("userRoleTable")) != null) {
            database.setUserRoleTable(ra.getContent().toString());
        }
        if ((ra = ref.get("userGroupTable")) != null) {
            database.setUserGroupTable(ra.getContent().toString());
        }
        if ((ra = ref.get("groupRoleTable")) != null) {
            database.setGroupRoleTable(ra.getContent().toString());
        }
        if ((ra = ref.get("roleNameCol")) != null) {
            database.setRoleNameCol(ra.getContent().toString());
        }
        if ((ra = ref.get("roleAndGroupDescriptionCol")) != null) {
            database.setRoleAndGroupDescriptionCol(ra.getContent().toString());
        }
        if ((ra = ref.get("groupNameCol")) != null) {
            database.setGroupNameCol(ra.getContent().toString());
        }
        if ((ra = ref.get("userCredCol")) != null) {
            database.setUserCredCol(ra.getContent().toString());
        }
        if ((ra = ref.get("userFullNameCol")) != null) {
            database.setUserFullNameCol(ra.getContent().toString());
        }
        if ((ra = ref.get("userNameCol")) != null) {
            database.setUserNameCol(ra.getContent().toString());
        }
        database.open();
        return database;
    }
}

