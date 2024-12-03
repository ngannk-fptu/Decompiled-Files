/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  com.opensymphony.util.EJBUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.database;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.util.Data;
import com.opensymphony.util.EJBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCPropertySet
extends AbstractPropertySet {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$database$JDBCPropertySet == null ? (class$com$opensymphony$module$propertyset$database$JDBCPropertySet = JDBCPropertySet.class$("com.opensymphony.module.propertyset.database.JDBCPropertySet")) : class$com$opensymphony$module$propertyset$database$JDBCPropertySet));
    DataSource ds;
    String colData;
    String colDate;
    String colFloat;
    String colGlobalKey;
    String colItemKey;
    String colItemType;
    String colNumber;
    String colString;
    String globalKey;
    String tableName;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$database$JDBCPropertySet;

    public Collection getKeys(String prefix, int type) throws PropertyException {
        if (prefix == null) {
            prefix = "";
        }
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = null;
            String sql = "SELECT " + this.colItemKey + " FROM " + this.tableName + " WHERE " + this.colItemKey + " LIKE ? AND " + this.colGlobalKey + " = ?";
            if (type == 0) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, prefix + "%");
                ps.setString(2, this.globalKey);
            } else {
                sql = sql + " AND " + this.colItemType + " = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, prefix + "%");
                ps.setString(2, this.globalKey);
                ps.setInt(3, type);
            }
            ArrayList<String> list = new ArrayList<String>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(this.colItemKey));
            }
            rs.close();
            ps.close();
            ArrayList<String> arrayList = list;
            return arrayList;
        }
        catch (SQLException e) {
            throw new PropertyException(e.getMessage());
        }
        finally {
            this.closeConnection(conn);
        }
    }

    public int getType(String key) throws PropertyException {
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            String sql = "SELECT " + this.colItemType + " FROM " + this.tableName + " WHERE " + this.colGlobalKey + " = ? AND " + this.colItemKey + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, this.globalKey);
            ps.setString(2, key);
            ResultSet rs = ps.executeQuery();
            int type = 0;
            if (rs.next()) {
                type = rs.getInt(this.colItemType);
            }
            rs.close();
            ps.close();
            int n = type;
            return n;
        }
        catch (SQLException e) {
            throw new PropertyException(e.getMessage());
        }
        finally {
            this.closeConnection(conn);
        }
    }

    public boolean exists(String key) throws PropertyException {
        return this.getType(key) != 0;
    }

    public void init(Map config, Map args) {
        this.globalKey = (String)args.get("globalKey");
        try {
            this.ds = (DataSource)EJBUtils.lookup((String)((String)config.get("datasource")));
        }
        catch (Exception e) {
            log.fatal((Object)"Could not get DataSource", (Throwable)e);
        }
        this.tableName = (String)config.get("table.name");
        this.colGlobalKey = (String)config.get("col.globalKey");
        this.colItemKey = (String)config.get("col.itemKey");
        this.colItemType = (String)config.get("col.itemType");
        this.colString = (String)config.get("col.string");
        this.colDate = (String)config.get("col.date");
        this.colData = (String)config.get("col.data");
        this.colFloat = (String)config.get("col.float");
        this.colNumber = (String)config.get("col.number");
    }

    public void remove(String key) throws PropertyException {
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            String sql = "DELETE FROM " + this.tableName + " WHERE " + this.colGlobalKey + " = ? AND " + this.colItemKey + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, this.globalKey);
            ps.setString(2, key);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            throw new PropertyException(e.getMessage());
        }
        finally {
            this.closeConnection(conn);
        }
    }

    protected void setImpl(int type, String key, Object value) throws PropertyException {
        if (value == null) {
            throw new PropertyException("JDBCPropertySet does not allow for null values to be stored");
        }
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            String sql = "UPDATE " + this.tableName + " SET " + this.colString + " = ?, " + this.colDate + " = ?, " + this.colData + " = ?, " + this.colFloat + " = ?, " + this.colNumber + " = ?, " + this.colItemType + " = ? " + " WHERE " + this.colGlobalKey + " = ? AND " + this.colItemKey + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            this.setValues(ps, type, key, value);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows != 1) {
                sql = "INSERT INTO " + this.tableName + " (" + this.colString + ", " + this.colDate + ", " + this.colData + ", " + this.colFloat + ", " + this.colNumber + ", " + this.colItemType + ", " + this.colGlobalKey + ", " + this.colItemKey + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(sql);
                this.setValues(ps, type, key, value);
                ps.executeUpdate();
                ps.close();
            }
        }
        catch (SQLException e) {
            throw new PropertyException(e.getMessage());
        }
        finally {
            this.closeConnection(conn);
        }
    }

    protected Object get(int type, String key) throws PropertyException {
        String sql = "SELECT " + this.colItemType + ", " + this.colString + ", " + this.colDate + ", " + this.colData + ", " + this.colFloat + ", " + this.colNumber + " FROM " + this.tableName + " WHERE " + this.colItemKey + " = ? AND " + this.colGlobalKey + " = ?";
        Object o = null;
        Connection conn = null;
        try {
            conn = this.ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, key);
            ps.setString(2, this.globalKey);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int propertyType = rs.getInt(this.colItemType);
                if (propertyType != type) {
                    throw new InvalidPropertyTypeException();
                }
                switch (type) {
                    case 1: {
                        int boolVal = rs.getInt(this.colNumber);
                        o = new Boolean(boolVal == 1);
                        break;
                    }
                    case 10: {
                        o = rs.getBytes(this.colData);
                        break;
                    }
                    case 7: {
                        o = rs.getTimestamp(this.colDate);
                        break;
                    }
                    case 4: {
                        o = new Double(rs.getDouble(this.colFloat));
                        break;
                    }
                    case 2: {
                        o = new Integer(rs.getInt(this.colNumber));
                        break;
                    }
                    case 3: {
                        o = new Long(rs.getLong(this.colNumber));
                        break;
                    }
                    case 5: {
                        o = rs.getString(this.colString);
                        break;
                    }
                    default: {
                        throw new InvalidPropertyTypeException("JDBCPropertySet doesn't support this type yet.");
                    }
                }
            }
            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            throw new PropertyException(e.getMessage());
        }
        catch (NumberFormatException e) {
            throw new PropertyException(e.getMessage());
        }
        finally {
            this.closeConnection(conn);
        }
        return o;
    }

    private void setValues(PreparedStatement ps, int type, String key, Object value) throws SQLException, PropertyException {
        String driverName;
        try {
            driverName = ps.getConnection().getMetaData().getDriverName().toUpperCase();
        }
        catch (Exception e) {
            driverName = "";
        }
        ps.setNull(1, 12);
        ps.setNull(2, 93);
        if (driverName.indexOf("SQLSERVER") >= 0 || driverName.indexOf("ORACLE") >= 0) {
            ps.setNull(3, -2);
        } else {
            ps.setNull(3, 2004);
        }
        ps.setNull(4, 6);
        ps.setNull(5, 2);
        ps.setInt(6, type);
        ps.setString(7, this.globalKey);
        ps.setString(8, key);
        switch (type) {
            case 1: {
                Boolean boolVal = (Boolean)value;
                ps.setInt(5, boolVal != false ? 1 : 0);
                break;
            }
            case 10: {
                Data data = (Data)value;
                ps.setBytes(3, data.getBytes());
                break;
            }
            case 7: {
                Date date = (Date)value;
                ps.setTimestamp(2, new Timestamp(date.getTime()));
                break;
            }
            case 4: {
                Double d = (Double)value;
                ps.setDouble(4, d);
                break;
            }
            case 2: {
                Integer i = (Integer)value;
                ps.setInt(5, i);
                break;
            }
            case 3: {
                Long l = (Long)value;
                ps.setLong(5, l);
                break;
            }
            case 5: {
                ps.setString(1, (String)value);
                break;
            }
            default: {
                throw new PropertyException("This type isn't supported!");
            }
        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        catch (SQLException e) {
            log.error((Object)"Could not close connection");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

