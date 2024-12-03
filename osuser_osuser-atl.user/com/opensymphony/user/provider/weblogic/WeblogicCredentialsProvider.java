/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.management.security.authentication.UserPasswordEditorMBean
 *  weblogic.management.security.authentication.UserReaderMBean
 *  weblogic.management.utils.NotFoundException
 */
package com.opensymphony.user.provider.weblogic;

import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.weblogic.WeblogicProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.management.security.authentication.UserPasswordEditorMBean;
import weblogic.management.security.authentication.UserReaderMBean;
import weblogic.management.utils.NotFoundException;

public class WeblogicCredentialsProvider
extends WeblogicProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$weblogic$WeblogicCredentialsProvider == null ? (class$com$opensymphony$user$provider$weblogic$WeblogicCredentialsProvider = WeblogicCredentialsProvider.class$("com.opensymphony.user.provider.weblogic.WeblogicCredentialsProvider")) : class$com$opensymphony$user$provider$weblogic$WeblogicCredentialsProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$weblogic$WeblogicCredentialsProvider;

    public boolean authenticate(String name, String password) {
        try {
            this.findHome();
            boolean valid = false;
            Iterator i = this.userPasswordEditors.iterator();
            while (i.hasNext()) {
                UserPasswordEditorMBean userPasswordEditor = (UserPasswordEditorMBean)i.next();
                try {
                    userPasswordEditor.changeUserPassword(name, password, password);
                    valid = true;
                }
                catch (NotFoundException e) {}
            }
            return valid;
        }
        catch (Exception ex) {
            log.warn((Object)("unable to authenticate for user " + name), (Throwable)ex);
            return false;
        }
    }

    public boolean changePassword(String name, String password) {
        try {
            boolean changed = false;
            Iterator i = this.userPasswordEditors.iterator();
            while (i.hasNext()) {
                try {
                    UserPasswordEditorMBean userPasswordEditor = (UserPasswordEditorMBean)i.next();
                    userPasswordEditor.resetUserPassword(name, password);
                    changed = true;
                }
                catch (NotFoundException e) {}
            }
            return changed;
        }
        catch (Exception ex) {
            log.warn((Object)("unable to change password for user " + name), (Throwable)ex);
            return false;
        }
    }

    public boolean create(String name) {
        return false;
    }

    public boolean handles(String name) {
        try {
            Iterator i = this.userReaders.iterator();
            while (i.hasNext()) {
                if (!((UserReaderMBean)i.next()).userExists(name)) continue;
                return true;
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List list() {
        try {
            ArrayList<String> result = new ArrayList<String>();
            Iterator i = this.userReaders.iterator();
            while (i.hasNext()) {
                UserReaderMBean u = (UserReaderMBean)i.next();
                String cursor = u.listUsers("*", this.maxRecords);
                while (u.haveCurrent(cursor)) {
                    String uName = u.getCurrentName(cursor);
                    result.add(uName);
                    u.advance(cursor);
                }
                u.close(cursor);
            }
            return Collections.unmodifiableList(result);
        }
        catch (Exception ex) {
            log.error((Object)"Error getting list of users", (Throwable)ex);
            return null;
        }
    }

    public boolean remove(String name) {
        return false;
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

