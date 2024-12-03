/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.daemon.support.DaemonWrapper;

public final class DaemonLoader {
    private static Controller controller;
    private static Object daemon;
    private static Method init;
    private static Method start;
    private static Method stop;
    private static Method destroy;
    private static Method signal;

    public static void version() {
        System.err.println("java version \"" + System.getProperty("java.version") + "\"");
        System.err.println(System.getProperty("java.runtime.name") + " (build " + System.getProperty("java.runtime.version") + ")");
        System.err.println(System.getProperty("java.vm.name") + " (build " + System.getProperty("java.vm.version") + ", " + System.getProperty("java.vm.info") + ")");
        System.err.println("commons daemon version \"" + System.getProperty("commons.daemon.version") + "\"");
        System.err.println("commons daemon process (id: " + System.getProperty("commons.daemon.process.id") + ", parent: " + System.getProperty("commons.daemon.process.parent") + ")");
    }

    public static boolean check(String className) {
        try {
            Objects.requireNonNull(className, "className");
            ClassLoader cl = DaemonLoader.class.getClassLoader();
            if (cl == null) {
                System.err.println("Cannot retrieve ClassLoader instance");
                return false;
            }
            Class<?> c = cl.loadClass(className);
            if (c == null) {
                throw new ClassNotFoundException(className);
            }
            c.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public static boolean signal() {
        try {
            if (signal != null) {
                signal.invoke(daemon, new Object[0]);
                return true;
            }
            System.out.println("Daemon doesn't support signaling");
        }
        catch (Throwable ex) {
            System.err.println("Cannot send signal: " + ex);
            ex.printStackTrace(System.err);
        }
        return false;
    }

    public static boolean load(String className, String[] args) {
        try {
            Class c;
            if (args == null) {
                args = new String[]{};
            }
            Objects.requireNonNull(className, "className");
            ClassLoader cl = DaemonLoader.class.getClassLoader();
            if (cl == null) {
                System.err.println("Cannot retrieve ClassLoader instance");
                return false;
            }
            if (className.charAt(0) == '@') {
                c = DaemonWrapper.class;
                String[] a = new String[args.length + 2];
                a[0] = "-start";
                a[1] = className.substring(1);
                System.arraycopy(args, 0, a, 2, args.length);
                args = a;
            } else {
                c = cl.loadClass(className);
            }
            if (c == null) {
                throw new ClassNotFoundException(className);
            }
            boolean isdaemon = false;
            try {
                Class<?> dclass = cl.loadClass("org.apache.commons.daemon.Daemon");
                isdaemon = dclass.isAssignableFrom(c);
            }
            catch (Exception dclass) {
                // empty catch block
            }
            Class[] myclass = new Class[]{isdaemon ? DaemonContext.class : args.getClass()};
            init = c.getMethod("init", myclass);
            start = c.getMethod("start", new Class[0]);
            stop = c.getMethod("stop", new Class[0]);
            destroy = c.getMethod("destroy", new Class[0]);
            try {
                signal = c.getMethod("signal", new Class[0]);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            daemon = c.getConstructor(new Class[0]).newInstance(new Object[0]);
            if (isdaemon) {
                controller = new Controller();
                DaemonLoader.controller.setAvailable(false);
                Context context = new Context();
                context.setArguments(args);
                context.setController(controller);
                Object[] arg = new Object[]{context};
                init.invoke(daemon, arg);
            } else {
                Object[] arg = new Object[]{args};
                init.invoke(daemon, arg);
            }
        }
        catch (InvocationTargetException e) {
            Throwable thrown = e.getTargetException();
            if (thrown instanceof DaemonInitException) {
                DaemonLoader.failed(((DaemonInitException)thrown).getMessageWithCause());
            } else {
                thrown.printStackTrace(System.err);
            }
            return false;
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public static boolean start() {
        try {
            start.invoke(daemon, new Object[0]);
            if (controller != null) {
                DaemonLoader.controller.setAvailable(true);
            }
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public static boolean stop() {
        try {
            if (controller != null) {
                DaemonLoader.controller.setAvailable(false);
            }
            stop.invoke(daemon, new Object[0]);
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public static boolean destroy() {
        try {
            destroy.invoke(daemon, new Object[0]);
            daemon = null;
            controller = null;
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    private static native void shutdown(boolean var0);

    private static native void failed(String var0);

    public static class Controller
    implements DaemonController {
        private boolean available;

        private Controller() {
            this.setAvailable(false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean isAvailable() {
            Controller controller = this;
            synchronized (controller) {
                return this.available;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void setAvailable(boolean available) {
            Controller controller = this;
            synchronized (controller) {
                this.available = available;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void shutdown() throws IllegalStateException {
            Controller controller = this;
            synchronized (controller) {
                if (!this.isAvailable()) {
                    throw new IllegalStateException();
                }
                this.setAvailable(false);
                DaemonLoader.shutdown(false);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void reload() throws IllegalStateException {
            Controller controller = this;
            synchronized (controller) {
                if (!this.isAvailable()) {
                    throw new IllegalStateException();
                }
                this.setAvailable(false);
                DaemonLoader.shutdown(true);
            }
        }

        @Override
        public void fail() {
            this.fail(null, null);
        }

        @Override
        public void fail(String message) {
            this.fail(message, null);
        }

        @Override
        public void fail(Exception exception) {
            this.fail(null, exception);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void fail(String message, Exception exception) {
            Controller controller = this;
            synchronized (controller) {
                this.setAvailable(false);
                String msg = message;
                if (exception != null) {
                    msg = msg != null ? msg + ": " + exception.toString() : exception.toString();
                }
                DaemonLoader.failed(msg);
            }
        }
    }

    public static class Context
    implements DaemonContext {
        private DaemonController daemonController;
        private String[] args;

        @Override
        public DaemonController getController() {
            return this.daemonController;
        }

        public void setController(DaemonController controller) {
            this.daemonController = controller;
        }

        @Override
        public String[] getArguments() {
            return this.args;
        }

        public void setArguments(String[] args) {
            this.args = args;
        }
    }
}

