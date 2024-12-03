/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.support.DaemonConfiguration;

public class DaemonWrapper
implements Daemon {
    private static final String ARGS = "args";
    private static final String START_CLASS = "start";
    private static final String START_METHOD = "start.method";
    private static final String STOP_CLASS = "stop";
    private static final String STOP_METHOD = "stop.method";
    private static final String STOP_ARGS = "stop.args";
    private String configFileName;
    private final DaemonConfiguration config = new DaemonConfiguration();
    private final Invoker startup = new Invoker();
    private final Invoker shutdown = new Invoker();

    @Override
    public void init(DaemonContext context) throws Exception {
        String[] args = context.getArguments();
        if (args != null) {
            int i;
            block16: for (i = 0; i < args.length && !args[i].equals("--"); ++i) {
                switch (args[i]) {
                    case "-daemon-properties": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        this.configFileName = args[i];
                        continue block16;
                    }
                    case "-start": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        this.startup.setClassName(args[i]);
                        continue block16;
                    }
                    case "-start-method": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        this.startup.setMethodName(args[i]);
                        continue block16;
                    }
                    case "-stop": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        this.shutdown.setClassName(args[i]);
                        continue block16;
                    }
                    case "-stop-method": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        this.shutdown.setMethodName(args[i]);
                        continue block16;
                    }
                    case "-stop-argument": {
                        if (++i == args.length) {
                            throw new IllegalArgumentException(args[i - 1]);
                        }
                        String[] aa = new String[]{args[i]};
                        this.shutdown.addArguments(aa);
                        continue block16;
                    }
                }
            }
            if (args.length > i) {
                String[] copy = new String[args.length - i];
                System.arraycopy(args, i, copy, 0, copy.length);
                this.startup.addArguments(copy);
            }
        }
        if (this.config.load(this.configFileName)) {
            this.startup.setClassName(this.config.getProperty(START_CLASS));
            this.startup.setMethodName(this.config.getProperty(START_METHOD));
            this.startup.addArguments(this.config.getPropertyArray(ARGS));
            this.shutdown.setClassName(this.config.getProperty(STOP_CLASS));
            this.shutdown.setMethodName(this.config.getProperty(STOP_METHOD));
            this.shutdown.addArguments(this.config.getPropertyArray(STOP_ARGS));
        }
        this.startup.validate();
        this.shutdown.validate();
    }

    @Override
    public void start() throws Exception {
        this.startup.invoke();
    }

    @Override
    public void stop() throws Exception {
        this.shutdown.invoke();
    }

    @Override
    public void destroy() {
        System.err.println("DaemonWrapper: instance " + this.hashCode() + " destroy");
    }

    static class Invoker {
        private String name;
        private String call;
        private String[] args;
        private Method inst;
        private Class<?> main;

        protected Invoker() {
        }

        protected void setClassName(String name) {
            if (this.name == null) {
                this.name = name;
            }
        }

        protected void setMethodName(String name) {
            if (this.call == null) {
                this.call = name;
            }
        }

        protected void addArguments(String[] args) {
            if (args != null) {
                ArrayList<String> aa = new ArrayList<String>();
                if (this.args != null) {
                    aa.addAll(Arrays.asList(this.args));
                }
                aa.addAll(Arrays.asList(args));
                this.args = aa.toArray(DaemonConfiguration.EMPTY_STRING_ARRAY);
            }
        }

        protected void invoke() throws Exception {
            if (this.name.equals("System") && this.call.equals("exit")) {
                System.exit(0);
            } else {
                Object obj = null;
                if ((this.inst.getModifiers() & 8) == 0) {
                    obj = this.main.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                Object[] arg = new Object[]{this.args};
                this.inst.invoke(obj, arg);
            }
        }

        protected void validate() throws Exception {
            if (this.name == null) {
                this.name = "System";
                this.call = "exit";
                return;
            }
            if (this.args == null) {
                this.args = new String[0];
            }
            if (this.call == null) {
                this.call = "main";
            }
            ClassLoader classLoader = DaemonWrapper.class.getClassLoader();
            Objects.requireNonNull(classLoader, "classLoader");
            Class[] ca = new Class[]{this.args.getClass()};
            this.main = classLoader.loadClass(this.name);
            if (this.main == null) {
                throw new ClassNotFoundException(this.name);
            }
            this.inst = this.main.getMethod(this.call, ca);
        }
    }
}

