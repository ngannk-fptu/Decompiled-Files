/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.authenticator.SingleSignOnListener
 */
package org.apache.catalina.ha.authenticator;

import org.apache.catalina.authenticator.SingleSignOnListener;
import org.apache.catalina.ha.session.ReplicatedSessionListener;

public class ClusterSingleSignOnListener
extends SingleSignOnListener
implements ReplicatedSessionListener {
    private static final long serialVersionUID = 1L;

    public ClusterSingleSignOnListener(String ssoId) {
        super(ssoId);
    }
}

