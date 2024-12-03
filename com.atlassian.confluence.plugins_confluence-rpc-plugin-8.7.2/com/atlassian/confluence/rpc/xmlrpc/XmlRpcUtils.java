/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  org.apache.commons.beanutils.BeanUtils
 */
package com.atlassian.confluence.rpc.xmlrpc;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.core.exception.InfrastructureException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;

public class XmlRpcUtils {
    public static final String __PARANAMER_DATA = "convertDate java.util.Hashtable,java.lang.String table,key \nconvertInteger java.util.Hashtable,java.lang.String table,key \nconvertLong java.util.Hashtable,java.lang.String table,key \ncreateRemotePageFromPageStruct java.util.Hashtable pageStruct \n";

    public static RemotePage createRemotePageFromPageStruct(Hashtable pageStruct) {
        RemotePage rpage = new RemotePage();
        XmlRpcUtils.convertLong(pageStruct, "id");
        if (pageStruct.containsKey("parentId")) {
            pageStruct.put("parentId", Long.valueOf((String)pageStruct.get("parentId")));
        }
        if (pageStruct.containsKey("version")) {
            pageStruct.put("version", Integer.valueOf((String)pageStruct.get("version")));
        }
        pageStruct.remove("modified");
        pageStruct.remove("created");
        try {
            BeanUtils.populate((Object)rpage, (Map)pageStruct);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new InfrastructureException("Unable to create a remotePage object from given hashtable ", (Throwable)e);
        }
        return rpage;
    }

    public static void convertLong(Hashtable table, String key) {
        if (table.containsKey(key)) {
            table.put(key, Long.valueOf((String)table.get(key)));
        }
    }

    public static void convertDate(Hashtable table, String key) {
        if (table.containsKey(key)) {
            String dateString = (String)table.get(key);
            try {
                Date d = SimpleDateFormat.getDateInstance().parse(dateString);
                table.put(key, d);
            }
            catch (ParseException pe) {
                throw new RuntimeException("Error parsing date '" + dateString + "'", pe);
            }
        }
    }

    public static void convertInteger(Hashtable table, String key) {
        if (table.containsKey(key)) {
            table.put(key, Integer.valueOf((String)table.get(key)));
        }
    }
}

