/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.shell;

import java.io.PrintStream;
import org.osgi.framework.ServiceReference;

public interface ShellService {
    public String[] getCommands();

    public String getCommandUsage(String var1);

    public String getCommandDescription(String var1);

    public ServiceReference getCommandReference(String var1);

    public void executeCommand(String var1, PrintStream var2, PrintStream var3) throws Exception;
}

