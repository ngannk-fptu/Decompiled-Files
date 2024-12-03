/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.build;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface BuildLogger {
    public boolean isDebugEnabled();

    public void debug(String var1);

    public void debug(String var1, Throwable var2);

    public boolean isInfoEnabled();

    public void info(String var1);

    public void info(String var1, Throwable var2);

    public boolean isWarnEnabled();

    public void warn(String var1);

    public void warn(String var1, Throwable var2);

    public boolean isErrorEnabled();

    public void error(String var1);

    public void error(String var1, Throwable var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements BuildLogger {
        private final List<BuildLogger> buildLoggers = new ArrayList<BuildLogger>();

        public Compound(BuildLogger ... buildLogger) {
            this(Arrays.asList(buildLogger));
        }

        public Compound(List<? extends BuildLogger> buildLoggers) {
            for (BuildLogger buildLogger : buildLoggers) {
                if (buildLogger instanceof Compound) {
                    this.buildLoggers.addAll(((Compound)buildLogger).buildLoggers);
                    continue;
                }
                if (buildLogger instanceof NoOp) continue;
                this.buildLoggers.add(buildLogger);
            }
        }

        @Override
        public boolean isDebugEnabled() {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isDebugEnabled()) continue;
                return true;
            }
            return false;
        }

        @Override
        public void debug(String message) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isDebugEnabled()) continue;
                buildLogger.debug(message);
            }
        }

        @Override
        public void debug(String message, Throwable throwable) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isDebugEnabled()) continue;
                buildLogger.debug(message, throwable);
            }
        }

        @Override
        public boolean isInfoEnabled() {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isInfoEnabled()) continue;
                return true;
            }
            return false;
        }

        @Override
        public void info(String message) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isInfoEnabled()) continue;
                buildLogger.info(message);
            }
        }

        @Override
        public void info(String message, Throwable throwable) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isInfoEnabled()) continue;
                buildLogger.info(message, throwable);
            }
        }

        @Override
        public boolean isWarnEnabled() {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isWarnEnabled()) continue;
                return true;
            }
            return false;
        }

        @Override
        public void warn(String message) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isWarnEnabled()) continue;
                buildLogger.warn(message);
            }
        }

        @Override
        public void warn(String message, Throwable throwable) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isWarnEnabled()) continue;
                buildLogger.warn(message, throwable);
            }
        }

        @Override
        public boolean isErrorEnabled() {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isErrorEnabled()) continue;
                return true;
            }
            return false;
        }

        @Override
        public void error(String message) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isErrorEnabled()) continue;
                buildLogger.error(message);
            }
        }

        @Override
        public void error(String message, Throwable throwable) {
            for (BuildLogger buildLogger : this.buildLoggers) {
                if (!buildLogger.isErrorEnabled()) continue;
                buildLogger.error(message, throwable);
            }
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.buildLoggers).equals(((Compound)object).buildLoggers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.buildLoggers).hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class StreamWriting
    implements BuildLogger {
        private final PrintStream printStream;

        public StreamWriting(PrintStream printStream) {
            this.printStream = printStream;
        }

        public static BuildLogger toSystemOut() {
            return new StreamWriting(System.out);
        }

        public static BuildLogger toSystemError() {
            return new StreamWriting(System.err);
        }

        public boolean isDebugEnabled() {
            return true;
        }

        public void debug(String message) {
            this.printStream.print(message);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void debug(String message, Throwable throwable) {
            PrintStream printStream = this.printStream;
            synchronized (printStream) {
                this.printStream.print(message);
                throwable.printStackTrace(this.printStream);
            }
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public void info(String message) {
            this.printStream.print(message);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void info(String message, Throwable throwable) {
            PrintStream printStream = this.printStream;
            synchronized (printStream) {
                this.printStream.print(message);
                throwable.printStackTrace(this.printStream);
            }
        }

        public boolean isWarnEnabled() {
            return true;
        }

        public void warn(String message) {
            this.printStream.print(message);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void warn(String message, Throwable throwable) {
            PrintStream printStream = this.printStream;
            synchronized (printStream) {
                this.printStream.print(message);
                throwable.printStackTrace(this.printStream);
            }
        }

        public boolean isErrorEnabled() {
            return true;
        }

        public void error(String message) {
            this.printStream.print(message);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void error(String message, Throwable throwable) {
            PrintStream printStream = this.printStream;
            synchronized (printStream) {
                this.printStream.print(message);
                throwable.printStackTrace(this.printStream);
            }
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.printStream.equals(((StreamWriting)object).printStream);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.printStream.hashCode();
        }
    }

    public static abstract class Adapter
    implements BuildLogger {
        public boolean isDebugEnabled() {
            return false;
        }

        public void debug(String message) {
        }

        public void debug(String message, Throwable throwable) {
        }

        public boolean isInfoEnabled() {
            return false;
        }

        public void info(String message) {
        }

        public void info(String message, Throwable throwable) {
        }

        public boolean isWarnEnabled() {
            return false;
        }

        public void warn(String message) {
        }

        public void warn(String message, Throwable throwable) {
        }

        public boolean isErrorEnabled() {
            return false;
        }

        public void error(String message) {
        }

        public void error(String message, Throwable throwable) {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements BuildLogger
    {
        INSTANCE;


        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String message) {
        }

        @Override
        public void debug(String message, Throwable throwable) {
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void info(String message, Throwable throwable) {
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String message) {
        }

        @Override
        public void warn(String message, Throwable throwable) {
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String message) {
        }

        @Override
        public void error(String message, Throwable throwable) {
        }
    }
}

