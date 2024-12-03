/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Authenticator
 *  javax.mail.PasswordAuthentication
 *  javax.mail.Session
 */
package org.apache.naming.factory;

import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class MailSessionFactory
implements ObjectFactory {
    protected static final String factoryType = "javax.mail.Session";

    @Override
    public Object getObjectInstance(Object refObj, Name name, Context context, Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference)refObj;
        if (!ref.getClassName().equals(factoryType)) {
            return null;
        }
        return AccessController.doPrivileged(() -> {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "localhost");
            String password = null;
            Enumeration<RefAddr> attrs = ref.getAll();
            while (attrs.hasMoreElements()) {
                RefAddr attr = attrs.nextElement();
                if ("factory".equals(attr.getType())) continue;
                if ("password".equals(attr.getType())) {
                    password = (String)attr.getContent();
                    continue;
                }
                props.put(attr.getType(), attr.getContent());
            }
            Authenticator auth = null;
            if (password != null) {
                String user = props.getProperty("mail.smtp.user");
                if (user == null) {
                    user = props.getProperty("mail.user");
                }
                if (user != null) {
                    final PasswordAuthentication pa = new PasswordAuthentication(user, password);
                    auth = new Authenticator(){

                        protected PasswordAuthentication getPasswordAuthentication() {
                            return pa;
                        }
                    };
                }
            }
            Session session = Session.getInstance((Properties)props, auth);
            return session;
        });
    }
}

