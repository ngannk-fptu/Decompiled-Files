/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.access;

import java.io.Serializable;
import java.util.Collection;
import org.bedework.access.Access;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.access.PrivilegeDefs;
import org.bedework.webdav.servlet.access.SharedEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public interface AccessHelperI
extends PrivilegeDefs,
Serializable {
    public void init(CallBack var1) throws WebdavException;

    public void setSuperUser(boolean var1);

    public boolean getSuperUser();

    public void setAuthPrincipal(AccessPrincipal var1);

    public void open();

    public void close();

    public SharedEntity getParent(SharedEntity var1) throws WebdavException;

    public String getDefaultPublicAccess();

    public String getDefaultPersonalAccess();

    public void changeAccess(SharedEntity var1, Collection<Ace> var2, boolean var3) throws WebdavException;

    public void defaultAccess(SharedEntity var1, AceWho var2) throws WebdavException;

    public Collection<? extends SharedEntity> checkAccess(Collection<? extends SharedEntity> var1, int var2, boolean var3) throws WebdavException;

    public Acl.CurrentAccess checkAccess(SharedEntity var1, int var2, boolean var3) throws WebdavException;

    public static abstract class CallBack
    implements Access.AccessCb,
    Serializable {
        public abstract AccessPrincipal getPrincipal(String var1) throws WebdavException;

        public abstract String getUserHomeRoot() throws WebdavException;

        public abstract SharedEntity getCollection(String var1) throws WebdavException;
    }
}

