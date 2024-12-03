/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.connection.settings;

import aQute.bnd.connection.settings.ProxyDTO;
import aQute.bnd.connection.settings.ServerDTO;
import aQute.bnd.util.dto.DTO;
import java.util.ArrayList;
import java.util.List;

public class SettingsDTO
extends DTO {
    public List<ProxyDTO> proxies = new ArrayList<ProxyDTO>();
    public List<ServerDTO> servers = new ArrayList<ServerDTO>();
}

