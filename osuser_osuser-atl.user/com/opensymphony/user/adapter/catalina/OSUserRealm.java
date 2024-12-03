/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Container
 *  org.apache.catalina.Realm
 */
package com.opensymphony.user.adapter.catalina;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.Realm;

public class OSUserRealm
implements Realm {
    private Container container;
    private List changeListeners = new ArrayList();
    private UserManager um = UserManager.getInstance();

    public void setContainer(Container container) {
        this.container = container;
    }

    public Container getContainer() {
        return this.container;
    }

    public String getInfo() {
        return "OSUserRealm/1.0";
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changeListeners.add(propertyChangeListener);
    }

    public Principal authenticate(String username, String password) {
        try {
            User user = this.um.getUser(username);
            if (user.authenticate(password)) {
                return user;
            }
        }
        catch (EntityNotFoundException entityNotFoundException) {
            // empty catch block
        }
        return null;
    }

    public Principal authenticate(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        return null;
    }

    public Principal authenticate(String username, byte[] password) {
        return this.authenticate(username, new String(password));
    }

    public Principal authenticate(X509Certificate[] x509Certificates) {
        return null;
    }

    public boolean hasRole(Principal userPrincipal, String role) {
        try {
            User user = this.um.getUser(userPrincipal.getName());
            return user.inGroup(role);
        }
        catch (EntityNotFoundException e) {
            return false;
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.changeListeners.remove(propertyChangeListener);
    }
}

