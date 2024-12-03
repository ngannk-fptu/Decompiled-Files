/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.connection.settings;

import aQute.bnd.util.dto.DTO;
import aQute.libg.glob.Glob;
import java.net.Proxy;
import java.util.List;

public class ProxyDTO
extends DTO {
    public String id = "default";
    public boolean active = true;
    public String mask;
    public String protocol = Proxy.Type.HTTP.name();
    public String username;
    public String password;
    public int port = 8080;
    public String nonProxyHosts;
    public String host;
    List<Glob> globs;
}

