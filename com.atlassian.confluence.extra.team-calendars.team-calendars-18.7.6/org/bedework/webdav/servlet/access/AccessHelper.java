/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.access;

import java.util.Collection;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.bedework.access.Access;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.access.PrivilegeSet;
import org.bedework.webdav.servlet.access.AccessHelperI;
import org.bedework.webdav.servlet.access.AccessState;
import org.bedework.webdav.servlet.access.SharedEntity;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;

public class AccessHelper
implements AccessHelperI {
    private boolean debug;
    private Access access;
    private boolean superUser;
    private AccessPrincipal authPrincipal;
    private AccessHelperI.CallBack cb;
    private PrivilegeSet maxAllowedPrivs;
    private transient Logger log;

    @Override
    public void init(AccessHelperI.CallBack cb) throws WebdavException {
        this.cb = cb;
        this.debug = this.getLog().isDebugEnabled();
        try {
            this.access = new Access();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public void setSuperUser(boolean val) {
        this.superUser = val;
    }

    @Override
    public boolean getSuperUser() {
        return this.superUser;
    }

    public void setMaximumAllowedPrivs(PrivilegeSet val) {
        this.maxAllowedPrivs = val;
    }

    @Override
    public void setAuthPrincipal(AccessPrincipal val) {
        this.authPrincipal = val;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public SharedEntity getParent(SharedEntity val) throws WebdavException {
        if (val.getParentPath() == null) {
            return null;
        }
        return this.cb.getCollection(val.getParentPath());
    }

    @Override
    public String getDefaultPublicAccess() {
        return Access.getDefaultPublicAccess();
    }

    @Override
    public String getDefaultPersonalAccess() {
        return Access.getDefaultPersonalAccess();
    }

    @Override
    public void changeAccess(SharedEntity ent, Collection<Ace> aces, boolean replaceAll) throws WebdavException {
        try {
            Collection<Ace> allAces;
            Acl acl = this.checkAccess(ent, 6, false).getAcl();
            if (replaceAll) {
                allAces = aces;
            } else {
                allAces = acl.getAces();
                allAces.addAll(aces);
            }
            ent.setAccess(new Acl(allAces).encodeStr());
        }
        catch (WebdavException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public void defaultAccess(SharedEntity ent, AceWho who) throws WebdavException {
        try {
            Acl acl = this.checkAccess(ent, 6, false).getAcl();
            if (acl.removeWho(who) != null) {
                ent.setAccess(acl.encodeStr());
            }
        }
        catch (WebdavException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    @Override
    public Collection<? extends SharedEntity> checkAccess(Collection<? extends SharedEntity> ents, int desiredAccess, boolean alwaysReturn) throws WebdavException {
        TreeSet<SharedEntity> out = new TreeSet<SharedEntity>();
        for (SharedEntity sharedEntity : ents) {
            if (!this.checkAccess(sharedEntity, desiredAccess, alwaysReturn).getAccessAllowed()) continue;
            out.add(sharedEntity);
        }
        return out;
    }

    @Override
    public Acl.CurrentAccess checkAccess(SharedEntity ent, int desiredAccess, boolean alwaysReturnResult) throws WebdavException {
        Acl.CurrentAccess ca;
        if (ent == null) {
            return null;
        }
        AccessState as = ent.getAccessState();
        if (as != null && (ca = as.getCurrentAccess(desiredAccess)) != null) {
            if (!ca.getAccessAllowed() && !alwaysReturnResult) {
                throw new WebdavForbidden();
            }
            return ca;
        }
        try {
            ca = null;
            AccessPrincipal owner = this.cb.getPrincipal(ent.getOwnerHref());
            PrivilegeSet maxPrivs = null;
            if (ent.isCollection()) {
                String path = ent.getPath();
                if (!this.getSuperUser()) {
                    if (this.cb.getUserHomeRoot().equals(path)) {
                        ca = Acl.defaultNonOwnerAccess;
                    } else if (path.equals(this.cb.getUserHomeRoot() + owner.getAccount() + "/")) {
                        maxPrivs = PrivilegeSet.userHomeMaxPrivileges;
                    }
                }
            }
            if (maxPrivs == null) {
                maxPrivs = this.maxAllowedPrivs;
            } else if (this.maxAllowedPrivs != null) {
                maxPrivs = PrivilegeSet.filterPrivileges(maxPrivs, this.maxAllowedPrivs);
            }
            if (ca == null) {
                char[] aclChars = this.getAclChars(ent);
                if (this.debug) {
                    this.getLog().debug("aclChars = " + new String(aclChars));
                }
                ca = desiredAccess == 25 ? this.access.checkAny(this.cb, this.authPrincipal, owner, aclChars, maxPrivs) : (desiredAccess == 1 ? this.access.checkRead(this.cb, this.authPrincipal, owner, aclChars, maxPrivs) : (desiredAccess == 5 ? this.access.checkReadWrite(this.cb, this.authPrincipal, owner, aclChars, maxPrivs) : this.access.evaluateAccess((Access.AccessCb)this.cb, this.authPrincipal, owner, desiredAccess, aclChars, maxPrivs)));
            }
            if (this.authPrincipal != null && this.superUser) {
                if (this.debug && !ca.getAccessAllowed()) {
                    this.getLog().debug("Override for superuser");
                }
                ca = Acl.forceAccessAllowed(ca);
            }
            if (ent.isCollection()) {
                if (as == null) {
                    as = new AccessState(ent);
                    ent.setAccessState(as);
                }
                as.setCurrentAccess(ca, desiredAccess);
            }
            if (!ca.getAccessAllowed() && !alwaysReturnResult) {
                throw new WebdavForbidden();
            }
            return ca;
        }
        catch (WebdavException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private char[] getAclChars(SharedEntity ent) throws WebdavException {
        String aclStr;
        SharedEntity container = ent.isCollection() ? ent : this.getParent(ent);
        String path = container.getPath();
        SharedEntity parent = this.getParent(container);
        if (parent != null) {
            aclStr = new String(this.merged(this.getAclChars(parent), parent.getPath(), container.getAccess()));
        } else if (container.getAccess() != null) {
            aclStr = container.getAccess();
        } else {
            throw new WebdavException("Collections must have default access set at root");
        }
        char[] aclChars = aclStr.toCharArray();
        if (ent.isCollection()) {
            return aclChars;
        }
        return this.merged(aclChars, path, ent.getAccess());
    }

    private char[] merged(char[] parentAccess, String path, String access) throws WebdavException {
        try {
            Acl acl = null;
            if (access != null) {
                acl = Acl.decode(access.toCharArray());
            }
            acl = acl == null ? Acl.decode(parentAccess, path) : acl.merge(parentAccess, path);
            return acl.encodeAll();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    private Logger getLog() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }
}

