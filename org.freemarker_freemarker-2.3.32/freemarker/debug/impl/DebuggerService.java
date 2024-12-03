/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.core.Environment;
import freemarker.debug.impl.RmiDebuggerService;
import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

public abstract class DebuggerService {
    private static final DebuggerService instance = DebuggerService.createInstance();

    private static DebuggerService createInstance() {
        return SecurityUtilities.getSystemProperty("freemarker.debug.password", null) == null ? new NoOpDebuggerService() : new RmiDebuggerService();
    }

    public static List getBreakpoints(String templateName) {
        return instance.getBreakpointsSpi(templateName);
    }

    abstract List getBreakpointsSpi(String var1);

    public static void registerTemplate(Template template) {
        instance.registerTemplateSpi(template);
    }

    abstract void registerTemplateSpi(Template var1);

    public static boolean suspendEnvironment(Environment env, String templateName, int line) throws RemoteException {
        return instance.suspendEnvironmentSpi(env, templateName, line);
    }

    abstract boolean suspendEnvironmentSpi(Environment var1, String var2, int var3) throws RemoteException;

    abstract void shutdownSpi();

    public static void shutdown() {
        instance.shutdownSpi();
    }

    private static class NoOpDebuggerService
    extends DebuggerService {
        private NoOpDebuggerService() {
        }

        @Override
        List getBreakpointsSpi(String templateName) {
            return Collections.EMPTY_LIST;
        }

        @Override
        boolean suspendEnvironmentSpi(Environment env, String templateName, int line) {
            throw new UnsupportedOperationException();
        }

        @Override
        void registerTemplateSpi(Template template) {
        }

        @Override
        void shutdownSpi() {
        }
    }
}

