/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.access;

import java.util.HashMap;
import java.util.Map;
import org.bedework.access.Acl;
import org.bedework.webdav.servlet.access.SharedEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public class AccessState {
    private SharedEntity entity;
    private Acl.CurrentAccess currentAccess;
    private Map<Integer, Acl.CurrentAccess> caMap = new HashMap<Integer, Acl.CurrentAccess>(20);
    private int lastDesiredAccess;

    public AccessState(SharedEntity entity) {
        this.entity = entity;
    }

    public SharedEntity fetchEntity() {
        return this.entity;
    }

    public void clearCurrentAccess() throws WebdavException {
        this.caMap.clear();
    }

    public Acl.CurrentAccess getCurrentAccess() throws WebdavException {
        if (this.currentAccess != null) {
            return this.currentAccess;
        }
        return this.getCurrentAccess(25);
    }

    public Acl.CurrentAccess getCurrentAccess(int desiredAccess) throws WebdavException {
        if (desiredAccess == this.lastDesiredAccess && this.currentAccess != null) {
            return this.currentAccess;
        }
        this.currentAccess = this.caMap.get(desiredAccess);
        this.lastDesiredAccess = desiredAccess;
        return this.currentAccess;
    }

    public void setCurrentAccess(Acl.CurrentAccess ca, int desiredAccess) {
        this.currentAccess = ca;
        this.lastDesiredAccess = desiredAccess;
        this.caMap.put(desiredAccess, ca);
    }

    public int getLastDesiredAccess() {
        return this.lastDesiredAccess;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("AccessState{");
        sb.append(this.entity.toString());
        try {
            if (this.getCurrentAccess() != null) {
                sb.append(", currentAccess=");
                sb.append(this.getCurrentAccess());
            }
        }
        catch (WebdavException cfe) {
            sb.append("exception");
            sb.append(cfe.getMessage());
        }
        sb.append("}");
        return sb.toString();
    }
}

