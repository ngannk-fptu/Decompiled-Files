/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.net.openssl.OpenSSLConfCmd;

public class OpenSSLConf
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<OpenSSLConfCmd> commands = new ArrayList<OpenSSLConfCmd>();

    public void addCmd(OpenSSLConfCmd cmd) {
        this.commands.add(cmd);
    }

    public List<OpenSSLConfCmd> getCommands() {
        return this.commands;
    }
}

