/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.ShellService;
import org.apache.felix.shell.impl.BundleLevelCommandImpl;
import org.apache.felix.shell.impl.CdCommandImpl;
import org.apache.felix.shell.impl.ExportsCommandImpl;
import org.apache.felix.shell.impl.HeadersCommandImpl;
import org.apache.felix.shell.impl.HelpCommandImpl;
import org.apache.felix.shell.impl.ImportsCommandImpl;
import org.apache.felix.shell.impl.InstallCommandImpl;
import org.apache.felix.shell.impl.PsCommandImpl;
import org.apache.felix.shell.impl.RefreshCommandImpl;
import org.apache.felix.shell.impl.RequirersCommandImpl;
import org.apache.felix.shell.impl.RequiresCommandImpl;
import org.apache.felix.shell.impl.ResolveCommandImpl;
import org.apache.felix.shell.impl.ServicesCommandImpl;
import org.apache.felix.shell.impl.ShutdownCommandImpl;
import org.apache.felix.shell.impl.StartCommandImpl;
import org.apache.felix.shell.impl.StartLevelCommandImpl;
import org.apache.felix.shell.impl.StopCommandImpl;
import org.apache.felix.shell.impl.UninstallCommandImpl;
import org.apache.felix.shell.impl.UpdateCommandImpl;
import org.apache.felix.shell.impl.VersionCommandImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class Activator
implements BundleActivator {
    private transient BundleContext m_context = null;
    private transient ShellServiceImpl m_shell = null;
    static /* synthetic */ Class class$org$apache$felix$shell$ShellService;
    static /* synthetic */ Class class$org$ungoverned$osgi$service$shell$ShellService;
    static /* synthetic */ Class class$org$apache$felix$shell$Command;
    static /* synthetic */ Class class$org$ungoverned$osgi$service$shell$Command;
    static /* synthetic */ Class class$org$apache$felix$shell$CdCommand;

    public void start(BundleContext context) {
        this.m_context = context;
        String[] classes = new String[]{(class$org$apache$felix$shell$ShellService == null ? (class$org$apache$felix$shell$ShellService = Activator.class$("org.apache.felix.shell.ShellService")) : class$org$apache$felix$shell$ShellService).getName(), (class$org$ungoverned$osgi$service$shell$ShellService == null ? (class$org$ungoverned$osgi$service$shell$ShellService = Activator.class$("org.ungoverned.osgi.service.shell.ShellService")) : class$org$ungoverned$osgi$service$shell$ShellService).getName()};
        this.m_shell = new ShellServiceImpl();
        context.registerService(classes, (Object)this.m_shell, null);
        ServiceListener sl = new ServiceListener(){

            public void serviceChanged(ServiceEvent event) {
                if (event.getType() == 1) {
                    Activator.this.m_shell.addCommand(event.getServiceReference());
                } else if (event.getType() == 4) {
                    Activator.this.m_shell.removeCommand(event.getServiceReference());
                }
            }
        };
        try {
            this.m_context.addServiceListener(sl, "(|(objectClass=" + (class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName() + ")(objectClass=" + (class$org$ungoverned$osgi$service$shell$Command == null ? (class$org$ungoverned$osgi$service$shell$Command = Activator.class$("org.ungoverned.osgi.service.shell.Command")) : class$org$ungoverned$osgi$service$shell$Command).getName() + "))");
        }
        catch (InvalidSyntaxException ex) {
            System.err.println("Activator: Cannot register service listener.");
            System.err.println("Activator: " + (Object)((Object)ex));
        }
        this.initializeCommands();
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new BundleLevelCommandImpl(this.m_context), null);
        classes = new String[]{(class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (class$org$apache$felix$shell$CdCommand == null ? (class$org$apache$felix$shell$CdCommand = Activator.class$("org.apache.felix.shell.CdCommand")) : class$org$apache$felix$shell$CdCommand).getName()};
        context.registerService(classes, (Object)new CdCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ExportsCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new HeadersCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new HelpCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ImportsCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new InstallCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new PsCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new RefreshCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new RequiresCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new RequirersCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ResolveCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ServicesCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new StartLevelCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ShutdownCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new StartCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new StopCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new UninstallCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new UpdateCommandImpl(this.m_context), null);
        context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new VersionCommandImpl(this.m_context), null);
    }

    public void stop(BundleContext context) {
        this.m_shell.clearCommands();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeCommands() {
        ShellServiceImpl shellServiceImpl = this.m_shell;
        synchronized (shellServiceImpl) {
            try {
                ServiceReference[] refs = this.m_context.getServiceReferences((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), null);
                if (refs != null) {
                    for (int i = 0; i < refs.length; ++i) {
                        this.m_shell.addCommand(refs[i]);
                    }
                }
            }
            catch (Exception ex) {
                System.err.println("Activator: " + ex);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static class ExecutePrivileged
    implements PrivilegedExceptionAction {
        private Command m_command = null;
        private String m_commandLine = null;
        private PrintStream m_out = null;
        private PrintStream m_err = null;

        public ExecutePrivileged(Command command, String commandLine, PrintStream out, PrintStream err) throws Exception {
            this.m_command = command;
            this.m_commandLine = commandLine;
            this.m_out = out;
            this.m_err = err;
        }

        public Object run() throws Exception {
            this.m_command.execute(this.m_commandLine, this.m_out, this.m_err);
            return null;
        }
    }

    private static class OldCommandWrapper
    implements Command {
        private org.ungoverned.osgi.service.shell.Command m_oldCommand = null;

        public OldCommandWrapper(org.ungoverned.osgi.service.shell.Command oldCommand) {
            this.m_oldCommand = oldCommand;
        }

        public String getName() {
            return this.m_oldCommand.getName();
        }

        public String getUsage() {
            return this.m_oldCommand.getUsage();
        }

        public String getShortDescription() {
            return this.m_oldCommand.getShortDescription();
        }

        public void execute(String line, PrintStream out, PrintStream err) {
            this.m_oldCommand.execute(line, out, err);
        }
    }

    private class ShellServiceImpl
    implements ShellService,
    org.ungoverned.osgi.service.shell.ShellService {
        private HashMap m_commandRefMap = new HashMap();
        private TreeMap m_commandNameMap = new TreeMap();

        private ShellServiceImpl() {
        }

        public synchronized String[] getCommands() {
            Set ks = this.m_commandNameMap.keySet();
            String[] cmds = ks == null ? new String[]{} : ks.toArray(new String[ks.size()]);
            return cmds;
        }

        public synchronized String getCommandUsage(String name) {
            Command command = (Command)this.m_commandNameMap.get(name);
            return command == null ? null : command.getUsage();
        }

        public synchronized String getCommandDescription(String name) {
            Command command = (Command)this.m_commandNameMap.get(name);
            return command == null ? null : command.getShortDescription();
        }

        public synchronized ServiceReference getCommandReference(String name) {
            ServiceReference ref = null;
            Iterator itr = this.m_commandRefMap.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry entry = itr.next();
                if (!((Command)entry.getValue()).getName().equals(name)) continue;
                ref = (ServiceReference)entry.getKey();
                break;
            }
            return ref;
        }

        public synchronized void removeCommand(ServiceReference ref) {
            Command command = (Command)this.m_commandRefMap.remove(ref);
            if (command != null) {
                this.m_commandNameMap.remove(command.getName());
            }
        }

        public synchronized void executeCommand(String commandLine, PrintStream out, PrintStream err) throws Exception {
            String commandName = (commandLine = commandLine.trim()).indexOf(32) >= 0 ? commandLine.substring(0, commandLine.indexOf(32)) : commandLine;
            Command command = this.getCommand(commandName);
            if (command != null) {
                if (System.getSecurityManager() != null) {
                    try {
                        AccessController.doPrivileged(new ExecutePrivileged(command, commandLine, out, err));
                    }
                    catch (PrivilegedActionException ex) {
                        throw ex.getException();
                    }
                } else {
                    try {
                        command.execute(commandLine, out, err);
                    }
                    catch (Throwable ex) {
                        err.println("Unable to execute command: " + ex);
                        ex.printStackTrace(err);
                    }
                }
            } else {
                err.println("Command not found.");
            }
        }

        protected synchronized Command getCommand(String name) {
            Command command = (Command)this.m_commandNameMap.get(name);
            return command == null ? null : command;
        }

        protected synchronized void addCommand(ServiceReference ref) {
            Object cmdObj = Activator.this.m_context.getService(ref);
            Command command = cmdObj instanceof org.ungoverned.osgi.service.shell.Command ? new OldCommandWrapper((org.ungoverned.osgi.service.shell.Command)cmdObj) : (Command)cmdObj;
            this.m_commandRefMap.put(ref, command);
            this.m_commandNameMap.put(command.getName(), command);
        }

        protected synchronized void clearCommands() {
            this.m_commandRefMap.clear();
            this.m_commandNameMap.clear();
        }
    }
}

