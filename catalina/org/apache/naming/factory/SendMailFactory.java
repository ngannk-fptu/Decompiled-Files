/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Session
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimePart
 *  javax.mail.internet.MimePartDataSource
 */
package org.apache.naming.factory;

import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class SendMailFactory
implements ObjectFactory {
    protected static final String DataSourceClassName = "javax.mail.internet.MimePartDataSource";

    @Override
    public Object getObjectInstance(Object refObj, Name name, Context ctx, Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference)refObj;
        if (ref.getClassName().equals(DataSourceClassName)) {
            return AccessController.doPrivileged(() -> {
                Properties props = new Properties();
                Enumeration<RefAddr> list = ref.getAll();
                props.put("mail.transport.protocol", "smtp");
                while (list.hasMoreElements()) {
                    RefAddr refaddr = list.nextElement();
                    props.put(refaddr.getType(), refaddr.getContent());
                }
                MimeMessage message = new MimeMessage(Session.getInstance((Properties)props));
                try {
                    RefAddr fromAddr = ref.get("mail.from");
                    String from = null;
                    if (fromAddr != null) {
                        from = (String)fromAddr.getContent();
                    }
                    if (from != null) {
                        message.setFrom(new InternetAddress(from));
                    }
                    message.setSubject("");
                }
                catch (Exception fromAddr) {
                    // empty catch block
                }
                MimePartDataSource mds = new MimePartDataSource((MimePart)message);
                return mds;
            });
        }
        return null;
    }
}

