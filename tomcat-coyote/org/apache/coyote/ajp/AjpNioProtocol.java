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
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;

public class AjpNioProtocol
extends AbstractAjpProtocol<NioChannel> {
    private static final Log log = LogFactory.getLog(AjpNioProtocol.class);

    @Override
    protected Log getLog() {
        return log;
    }

    public AjpNioProtocol() {
        super(new NioEndpoint());
    }

    @Override
    protected String getNamePrefix() {
        return "ajp-nio";
    }
}

