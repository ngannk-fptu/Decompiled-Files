/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class Socket
extends ProjectComponent
implements Condition {
    private String server = null;
    private int port = 0;

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean eval() throws BuildException {
        boolean bl;
        if (this.server == null) {
            throw new BuildException("No server specified in socket condition");
        }
        if (this.port == 0) {
            throw new BuildException("No port specified in socket condition");
        }
        this.log("Checking for listener at " + this.server + ":" + this.port, 3);
        java.net.Socket s = new java.net.Socket(this.server, this.port);
        try {
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    s.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                return false;
            }
        }
        s.close();
        return bl;
    }
}

