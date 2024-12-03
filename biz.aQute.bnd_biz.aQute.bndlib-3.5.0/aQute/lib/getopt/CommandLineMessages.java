/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.getopt;

import aQute.service.reporter.Messages;
import java.util.List;

public interface CommandLineMessages
extends Messages {
    public Messages.ERROR Option__WithArgumentNotLastInAbbreviation_(String var1, char var2, String var3);

    public Messages.ERROR MissingArgument__(String var1, char var2);

    public Messages.ERROR OptionCanOnlyOccurOnce_(String var1);

    public Messages.ERROR NoSuchCommand_(String var1);

    public Messages.ERROR TooManyArguments_(List<String> var1);

    public Messages.ERROR MissingArgument_(String var1);

    public Messages.ERROR UnrecognizedOption_(String var1);

    public Messages.ERROR OptionNotSet_(String var1);
}

