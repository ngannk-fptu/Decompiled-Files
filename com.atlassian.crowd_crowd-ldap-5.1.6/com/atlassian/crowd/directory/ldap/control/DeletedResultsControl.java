/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.control.AbstractRequestControlDirContextProcessor
 */
package com.atlassian.crowd.directory.ldap.control;

import com.atlassian.crowd.directory.ldap.control.ldap.DeletedControl;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import org.springframework.ldap.control.AbstractRequestControlDirContextProcessor;

public class DeletedResultsControl
extends AbstractRequestControlDirContextProcessor {
    private static final DeletedControl DELETED_RESULTS_CONTROL = new DeletedControl();

    public Control createRequestControl() {
        return DELETED_RESULTS_CONTROL;
    }

    public void postProcess(DirContext ctx) throws NamingException {
    }
}

