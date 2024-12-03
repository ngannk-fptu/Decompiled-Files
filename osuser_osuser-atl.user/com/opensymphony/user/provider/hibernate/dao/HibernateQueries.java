/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.dao;

public class HibernateQueries {
    public static final String USER_BY_USERNAME = "select distinct hibuser from hibuser in class " + (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser = HibernateQueries.class$("com.opensymphony.user.provider.hibernate.entity.HibernateUser")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateUser).getName() + " where hibuser.name = ? ";
    public static final String USER_BY_USERNAME_AND_GROUPNAME = "select distinct hibuser from " + (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser = HibernateQueries.class$("com.opensymphony.user.provider.hibernate.entity.HibernateUser")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateUser).getName() + " as hibuser join hibuser.groups as hibgroup where hibuser.name = ? and hibgroup.name = ?";
    public static final String ALL_USERS = "from hibusers in class " + (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser = HibernateQueries.class$("com.opensymphony.user.provider.hibernate.entity.HibernateUser")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateUser).getName();
    public static final String GROUP_BY_GROUPNAME = "select distinct hibgroup from " + (class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup = HibernateQueries.class$("com.opensymphony.user.provider.hibernate.entity.HibernateGroup")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup).getName() + " as hibgroup where hibgroup.name = ? ";
    public static final String ALL_GROUPS = "from hibgroups in class " + (class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup = HibernateQueries.class$("com.opensymphony.user.provider.hibernate.entity.HibernateGroup")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup).getName();
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$entity$HibernateUser;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

