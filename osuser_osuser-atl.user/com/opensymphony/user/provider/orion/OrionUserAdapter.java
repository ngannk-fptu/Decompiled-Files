/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.Group
 *  com.evermind.security.User
 *  com.evermind.security.UserManager
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.orion;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.provider.orion.OrionGroupAdapter;
import java.math.BigInteger;
import java.security.Permission;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrionUserAdapter
implements com.evermind.security.User {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$orion$OrionUserAdapter == null ? (class$com$opensymphony$user$provider$orion$OrionUserAdapter = OrionUserAdapter.class$("com.opensymphony.user.provider.orion.OrionUserAdapter")) : class$com$opensymphony$user$provider$orion$OrionUserAdapter));
    private User user;
    private com.evermind.security.UserManager orionUserManager;
    private UserManager osUserManager = null;
    static /* synthetic */ Class class$com$opensymphony$user$provider$orion$OrionUserAdapter;

    public OrionUserAdapter(com.evermind.security.UserManager orionUserManager, UserManager osUserManager, User user) {
        this.orionUserManager = orionUserManager;
        this.osUserManager = osUserManager;
        this.user = user;
    }

    public void setCertificate(X509Certificate certificate) {
        this.setCertificate(certificate.getIssuerDN().getName(), certificate.getSerialNumber());
    }

    public void setCertificate(String issuer, BigInteger integer) throws UnsupportedOperationException {
        this.user.getPropertySet().setString("certificateSerial", integer.toString());
        this.user.getPropertySet().setString("certificateIssuerDN", issuer);
    }

    public String getCertificateIssuerDN() {
        return this.user.getPropertySet().getString("certificateIssuerDN");
    }

    public BigInteger getCertificateSerial() {
        return new BigInteger(this.user.getPropertySet().getString("certificateSerial"));
    }

    public void setDescription(String s) {
        this.user.getPropertySet().setString("description", s);
    }

    public String getDescription() {
        return this.user.getPropertySet().getString("description");
    }

    public Set getGroups() {
        HashSet<OrionGroupAdapter> hashset = new HashSet<OrionGroupAdapter>();
        List groups = this.user.getGroups();
        if (groups != null) {
            Iterator iterator = groups.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                Group group = null;
                if (obj instanceof String) {
                    try {
                        group = this.osUserManager.getGroup(obj.toString());
                    }
                    catch (EntityNotFoundException ex) {
                        log.warn((Object)("User " + this.user.getName() + " belongs to group " + obj + " which cannot be found! ignoring..."));
                    }
                } else {
                    group = (Group)iterator.next();
                }
                if (group == null) continue;
                hashset.add(new OrionGroupAdapter(group));
            }
        }
        return hashset;
    }

    public Locale getLocale() {
        String val = this.user.getPropertySet().getString("locale");
        String lang = "";
        String country = "";
        String variant = "";
        if (val == null || val.trim().length() == 0) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(val);
        if (tok.hasMoreTokens()) {
            lang = tok.nextToken();
        }
        if (tok.hasMoreTokens()) {
            country = tok.nextToken();
        }
        if (tok.hasMoreTokens()) {
            variant = tok.nextToken();
        }
        Locale locale = new Locale(lang, country, variant);
        return locale;
    }

    public boolean isMemberOf(com.evermind.security.Group group) {
        return this.user.getAccessProvider().inGroup(this.user.getName(), group.getName());
    }

    public String getName() {
        return this.user.getName();
    }

    public void setPassword(String s) {
        try {
            this.user.setPassword(s);
        }
        catch (ImmutableException ex) {
            log.error((Object)("entity " + this.user.getName() + " is immutable, setPassword is not supported"));
        }
    }

    public String getPassword() {
        return null;
    }

    public void addToGroup(com.evermind.security.Group group) throws UnsupportedOperationException {
        if (!this.isMemberOf(group)) {
            this.user.getAccessProvider().addToGroup(this.user.getName(), group.getName());
        }
    }

    public boolean authenticate(String s) {
        return this.user.authenticate(s);
    }

    public boolean hasPermission(Permission permission) {
        Set defaultGroups = this.orionUserManager.getDefaultGroups();
        Iterator iterator = defaultGroups.iterator();
        while (iterator.hasNext()) {
            com.evermind.security.Group group = (com.evermind.security.Group)iterator.next();
            if (!group.hasPermission(permission)) continue;
            return true;
        }
        List groups = this.user.getGroups();
        Iterator groupIterator = groups.iterator();
        while (groupIterator.hasNext()) {
            com.evermind.security.Group group = this.orionUserManager.getGroup(groupIterator.next().toString());
            if (group == null || !group.hasPermission(permission)) continue;
            return true;
        }
        return false;
    }

    public void removeFromGroup(com.evermind.security.Group group) throws UnsupportedOperationException {
        this.user.getAccessProvider().removeFromGroup(this.user.getName(), group.getName());
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

