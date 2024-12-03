/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class JtdsObjectFactory
implements ObjectFactory {
    public Object getObjectInstance(Object refObj, Name name, Context nameCtx, Hashtable env) throws Exception {
        Reference ref = (Reference)refObj;
        if (ref.getClassName().equals(JtdsDataSource.class.getName())) {
            HashMap props = this.loadProps(ref, new String[]{"description", "prop.appname", "prop.autocommit", "prop.batchsize", "prop.bindaddress", "prop.bufferdir", "prop.buffermaxmemory", "prop.bufferminpackets", "prop.cachemetadata", "prop.charset", "prop.databasename", "prop.domain", "prop.instance", "prop.language", "prop.lastupdatecount", "prop.lobbuffer", "prop.logfile", "prop.logintimeout", "prop.macaddress", "prop.maxstatements", "prop.namedpipe", "prop.packetsize", "prop.password", "prop.portnumber", "prop.preparesql", "prop.progname", "prop.servername", "prop.servertype", "prop.sotimeout", "prop.sokeepalive", "prop.processid", "prop.ssl", "prop.tcpnodelay", "prop.tds", "prop.usecursors", "prop.usejcifs", "prop.usentlmv2", "prop.usekerberos", "prop.uselobs", "prop.user", "prop.useunicode", "prop.wsid", "prop.xaemulation"});
            return new JtdsDataSource(props);
        }
        return null;
    }

    private HashMap loadProps(Reference ref, String[] props) {
        HashMap<String, String> config = new HashMap<String, String>();
        HashMap<String, Object> values = new HashMap<String, Object>();
        Enumeration<RefAddr> c = ref.getAll();
        while (c.hasMoreElements()) {
            RefAddr ra = c.nextElement();
            values.put(ra.getType().toLowerCase(), ra.getContent());
        }
        for (int i = 0; i < props.length; ++i) {
            String value = (String)values.get(props[i].toLowerCase());
            String string = value = value == null ? (String)values.get(Messages.get(props[i].toLowerCase())) : value;
            if (value == null) continue;
            config.put(props[i], value);
        }
        return config;
    }
}

