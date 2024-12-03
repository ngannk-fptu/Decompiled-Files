/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.util.Collection;

public interface AccessPrincipal
extends Serializable {
    public int getKind();

    public void setUnauthenticated(boolean var1);

    public boolean getUnauthenticated();

    public void setAccount(String var1);

    public String getAccount();

    public String getAclAccount();

    public void setPrincipalRef(String var1);

    public String getPrincipalRef();

    public void setGroupNames(Collection<String> var1);

    public Collection<String> getGroupNames();

    public void setDescription(String var1);

    public String getDescription();
}

