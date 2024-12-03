/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.coyote.ajp;

import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AprEndpoint;

@Deprecated
public class AjpAprProtocol
extends AbstractAjpProtocol<Long> {
    private static final Log log = LogFactory.getLog(AjpAprProtocol.class);

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    public boolean isAprRequired() {
        return true;
    }

    public AjpAprProtocol() {
        super(new AprEndpoint());
    }

    public int getPollTime() {
        return ((AprEndpoint)this.getEndpoint()).getPollTime();
    }

    public void setPollTime(int pollTime) {
        ((AprEndpoint)this.getEndpoint()).setPollTime(pollTime);
    }

    @Override
    protected String getNamePrefix() {
        return "ajp-apr";
    }
}

