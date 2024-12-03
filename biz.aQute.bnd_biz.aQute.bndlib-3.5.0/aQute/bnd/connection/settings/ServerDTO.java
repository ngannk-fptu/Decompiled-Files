/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.connection.settings;

import aQute.bnd.util.dto.DTO;

public class ServerDTO
extends DTO {
    public String id = "default";
    public String username;
    public String password;
    public String privateKey;
    public String passphrase;
    public String match;
    public boolean verify = true;
    public String trust;
}

