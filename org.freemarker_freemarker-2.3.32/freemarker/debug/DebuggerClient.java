/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug;

import freemarker.debug.Breakpoint;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerListener;
import freemarker.debug.impl.RmiDebuggerListenerImpl;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.List;

public class DebuggerClient {
    private DebuggerClient() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Debugger getDebugger(InetAddress host, int port, String password) throws IOException {
        try (Socket s = new Socket(host, port);){
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            int protocolVersion = in.readInt();
            if (protocolVersion > 220) {
                throw new IOException("Incompatible protocol version " + protocolVersion + ". At most 220 was expected.");
            }
            byte[] challenge = (byte[])in.readObject();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(password.getBytes("UTF-8"));
            md.update(challenge);
            out.writeObject(md.digest());
            LocalDebuggerProxy localDebuggerProxy = new LocalDebuggerProxy((Debugger)in.readObject());
            return localDebuggerProxy;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    private static class LocalDebuggerProxy
    implements Debugger {
        private final Debugger remoteDebugger;

        LocalDebuggerProxy(Debugger remoteDebugger) {
            this.remoteDebugger = remoteDebugger;
        }

        @Override
        public void addBreakpoint(Breakpoint breakpoint) throws RemoteException {
            this.remoteDebugger.addBreakpoint(breakpoint);
        }

        @Override
        public Object addDebuggerListener(DebuggerListener listener) throws RemoteException {
            if (listener instanceof RemoteObject) {
                return this.remoteDebugger.addDebuggerListener(listener);
            }
            RmiDebuggerListenerImpl remotableListener = new RmiDebuggerListenerImpl(listener);
            return this.remoteDebugger.addDebuggerListener(remotableListener);
        }

        @Override
        public List getBreakpoints() throws RemoteException {
            return this.remoteDebugger.getBreakpoints();
        }

        @Override
        public List getBreakpoints(String templateName) throws RemoteException {
            return this.remoteDebugger.getBreakpoints(templateName);
        }

        @Override
        public Collection getSuspendedEnvironments() throws RemoteException {
            return this.remoteDebugger.getSuspendedEnvironments();
        }

        @Override
        public void removeBreakpoint(Breakpoint breakpoint) throws RemoteException {
            this.remoteDebugger.removeBreakpoint(breakpoint);
        }

        @Override
        public void removeBreakpoints(String templateName) throws RemoteException {
            this.remoteDebugger.removeBreakpoints(templateName);
        }

        @Override
        public void removeBreakpoints() throws RemoteException {
            this.remoteDebugger.removeBreakpoints();
        }

        @Override
        public void removeDebuggerListener(Object id) throws RemoteException {
            this.remoteDebugger.removeDebuggerListener(id);
        }
    }
}

