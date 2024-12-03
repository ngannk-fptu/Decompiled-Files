/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell;

import java.util.List;
import jline.console.completer.Completer;

public interface Command {
    public String getName();

    public String getShortcut();

    public Completer getCompleter();

    public String getDescription();

    public String getUsage();

    public String getHelp();

    public List getAliases();

    public Object execute(List<String> var1);

    public boolean getHidden();
}

