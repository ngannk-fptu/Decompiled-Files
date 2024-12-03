/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.getopt;

import aQute.lib.getopt.CommandLine;
import java.util.List;
import java.util.Map;

public interface Options {
    public List<String> _arguments();

    public CommandLine _command();

    public Map<String, String> _properties();

    public boolean _ok();

    public boolean _help();
}

