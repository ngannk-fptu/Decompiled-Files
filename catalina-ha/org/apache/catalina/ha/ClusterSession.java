/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.apache.catalina.Session
 */
package org.apache.catalina.ha;

import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;

public interface ClusterSession
extends Session,
HttpSession {
    public boolean isPrimarySession();

    public void setPrimarySession(boolean var1);
}

