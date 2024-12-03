/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.connection.settings;

import aQute.bnd.connection.settings.ProxyDTO;
import aQute.bnd.connection.settings.ServerDTO;
import aQute.bnd.connection.settings.SettingsDTO;
import aQute.lib.xpath.XPathParser;
import java.io.File;

public class SettingsParser
extends XPathParser {
    final SettingsDTO settings = new SettingsDTO();

    public SettingsParser(File file) throws Exception {
        super(file);
        this.parse("/settings/proxies/proxy", ProxyDTO.class, this.settings.proxies);
        this.parse("/settings/servers/server", ServerDTO.class, this.settings.servers);
    }

    public SettingsDTO getSettings() {
        return this.settings;
    }
}

