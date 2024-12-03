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
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;

public class AjpNio2Protocol
extends AbstractAjpProtocol<Nio2Channel> {
    private static final Log log = LogFactory.getLog(AjpNio2Protocol.class);

    @Override
    protected Log getLog() {
        return log;
    }

    public AjpNio2Protocol() {
        super(new Nio2Endpoint());
    }

    @Override
    protected String getNamePrefix() {
        return "ajp-nio2";
    }
}

