/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.util.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;

public class StandaloneTubeAssembler
implements TubelineAssembler {
    public static final boolean dump;

    @Override
    @NotNull
    public Tube createClient(ClientTubeAssemblerContext context) {
        Tube head = context.createTransportTube();
        head = context.createSecurityTube(head);
        if (dump) {
            head = context.createDumpTube("client", System.out, head);
        }
        head = context.createWsaTube(head);
        head = context.createClientMUTube(head);
        head = context.createValidationTube(head);
        return context.createHandlerTube(head);
    }

    @Override
    public Tube createServer(ServerTubeAssemblerContext context) {
        Tube head = context.getTerminalTube();
        head = context.createValidationTube(head);
        head = context.createHandlerTube(head);
        head = context.createMonitoringTube(head);
        head = context.createServerMUTube(head);
        head = context.createWsaTube(head);
        if (dump) {
            head = context.createDumpTube("server", System.out, head);
        }
        head = context.createSecurityTube(head);
        return head;
    }

    static {
        boolean b = false;
        try {
            b = Boolean.getBoolean(StandaloneTubeAssembler.class.getName() + ".dump");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        dump = b;
    }
}

