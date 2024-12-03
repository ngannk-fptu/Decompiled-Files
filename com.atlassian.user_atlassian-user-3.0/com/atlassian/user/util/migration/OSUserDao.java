/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.RowCallbackHandler
 *  org.springframework.jdbc.core.support.JdbcDaoSupport
 */
package com.atlassian.user.util.migration;

import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OSUserDao
extends JdbcDaoSupport {
    private static final String USERNAME_COL = "username";
    private static final String ID_COL = "id";
    private static final String PASSWORD_COL = "passwd";
    private static final String KEY_COL = "entity_key";
    private static final String VALUE_COL = "string_val";
    private static final String OSUSER_TABLE = "os_user";
    private static final String OSGROUP_TABLE = "os_group";
    private static final String OS_USER_GROUP_TABLE = "os_user_group";
    private static final String OSPROPERTYSET_TABLE = "OS_PROPERTYENTRY";
    private static final String GROUP_NAME_CAL = "groupname";
    private static final String GROUP_ID_COL = "group_id";
    private static final String USER_ID_COL = "user_id";

    OSUserDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    Map<Long, DefaultUser> findAllUsers() {
        final HashMap<Long, DefaultUser> users = new HashMap<Long, DefaultUser>();
        this.getJdbcTemplate().query("select * from os_user", new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                Long id = rs.getLong(OSUserDao.ID_COL);
                DefaultUser user = new DefaultUser(rs.getString(OSUserDao.USERNAME_COL));
                user.setPassword(rs.getString(OSUserDao.PASSWORD_COL));
                users.put(id, user);
            }
        });
        this.getJdbcTemplate().query("SELECT * FROM OS_PROPERTYENTRY WHERE entity_name='OSUser_user' AND ( entity_key='fullName' OR entity_key='email')", new RowCallbackHandler(){

            public void processRow(ResultSet resultSet) throws SQLException {
                Long id = resultSet.getLong("entity_id");
                DefaultUser user = (DefaultUser)users.get(id);
                if (user != null) {
                    String key = resultSet.getString(OSUserDao.KEY_COL);
                    String value = resultSet.getString(OSUserDao.VALUE_COL);
                    if ("fullName".equals(key)) {
                        user.setFullName(value);
                    } else if ("email".equals(key)) {
                        user.setEmail(value);
                    }
                }
            }
        });
        return users;
    }

    public Map<String, List<String>> findAllUserGroups(final Map users) {
        final HashMap<String, List<String>> userGroups = new HashMap<String, List<String>>();
        final HashMap groups = new HashMap();
        this.getJdbcTemplate().query("select * from os_group", new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                Long groupId = rs.getLong(OSUserDao.ID_COL);
                groups.put(groupId, rs.getString(OSUserDao.GROUP_NAME_CAL));
            }
        });
        this.getJdbcTemplate().query("select * from os_user_group", new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                Long groupId = rs.getLong(OSUserDao.GROUP_ID_COL);
                Long userId = rs.getLong(OSUserDao.USER_ID_COL);
                String userName = ((User)users.get(userId)).getName();
                if (userName != null) {
                    String groupName;
                    LinkedList<String> gr = (LinkedList<String>)userGroups.get(userName);
                    if (gr == null) {
                        gr = new LinkedList<String>();
                        userGroups.put(userName, gr);
                    }
                    if ((groupName = (String)groups.get(groupId)) != null) {
                        gr.add(groupName);
                    }
                }
            }
        });
        return userGroups;
    }
}

