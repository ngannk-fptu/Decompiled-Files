/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.reporter;

import aQute.libg.reporter.Message;
import aQute.service.reporter.Messages;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.IllegalFormatException;

public class ReporterMessages {
    public static <T> T base(final Reporter reporter, Class<T> messages) {
        return (T)Proxy.newProxyInstance(messages.getClassLoader(), new Class[]{messages}, new InvocationHandler(){

            @Override
            public Object invoke(Object target, Method method, Object[] args) throws Throwable {
                String format;
                Message d = method.getAnnotation(Message.class);
                if (d == null) {
                    String name = method.getName();
                    StringBuilder sb = new StringBuilder();
                    sb.append(name.charAt(0));
                    int n = 0;
                    block6: for (int i = 1; i < name.length(); ++i) {
                        char c = name.charAt(i);
                        switch (c) {
                            case '_': {
                                sb.append(" %s, ");
                                ++n;
                                continue block6;
                            }
                            case '$': {
                                sb.append(" ");
                                continue block6;
                            }
                            default: {
                                if (Character.isUpperCase(c)) {
                                    sb.append(" ");
                                    c = Character.toLowerCase(c);
                                }
                                sb.append(c);
                            }
                        }
                    }
                    while (n < method.getParameterTypes().length) {
                        sb.append(": %s");
                        ++n;
                    }
                    format = sb.toString();
                } else {
                    format = d.value();
                }
                try {
                    if (method.getReturnType() == Messages.ERROR.class) {
                        return new ERRORImpl(reporter.error(format, args));
                    }
                    if (method.getReturnType() == Messages.WARNING.class) {
                        return new WARNINGImpl(reporter.warning(format, args));
                    }
                    reporter.trace(format, args);
                }
                catch (IllegalFormatException e) {
                    reporter.error("Formatter failed: %s %s %s", method.getName(), format, Arrays.toString(args));
                }
                return null;
            }
        });
    }

    static class ERRORImpl
    extends WARNINGImpl
    implements Messages.ERROR {
        public ERRORImpl(Reporter.SetLocation e) {
            super(e);
        }
    }

    static class WARNINGImpl
    implements Messages.WARNING {
        Reporter.SetLocation loc;

        @Override
        public Reporter.SetLocation file(String file) {
            return this.loc.file(file);
        }

        @Override
        public Reporter.SetLocation header(String header) {
            return this.loc.header(header);
        }

        @Override
        public Reporter.SetLocation context(String context) {
            return this.loc.context(context);
        }

        @Override
        public Reporter.SetLocation method(String methodName) {
            return this.loc.method(methodName);
        }

        @Override
        public Reporter.SetLocation line(int n) {
            return this.loc.line(n);
        }

        @Override
        public Reporter.SetLocation reference(String reference) {
            return this.loc.reference(reference);
        }

        public WARNINGImpl(Reporter.SetLocation loc) {
            this.loc = loc;
        }

        @Override
        public Reporter.SetLocation details(Object details) {
            return this.loc.details(details);
        }

        @Override
        public Report.Location location() {
            return this.loc.location();
        }

        @Override
        public Reporter.SetLocation length(int length) {
            this.loc.length(length);
            return this;
        }
    }
}

