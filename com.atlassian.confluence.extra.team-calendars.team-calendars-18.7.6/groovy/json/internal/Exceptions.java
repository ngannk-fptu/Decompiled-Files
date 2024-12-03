/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.JsonException;
import groovy.json.internal.CharBuf;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class Exceptions {
    public static boolean die() {
        throw new JsonInternalException("died");
    }

    public static boolean die(String message) {
        throw new JsonInternalException(message);
    }

    public static <T> T die(Class<T> clazz, String message) {
        throw new JsonInternalException(message);
    }

    public static void handle(Exception e) {
        throw new JsonInternalException(e);
    }

    public static <T> T handle(Class<T> clazz, Exception e) {
        if (e instanceof JsonInternalException) {
            throw (JsonInternalException)e;
        }
        throw new JsonInternalException(e);
    }

    public static <T> T handle(Class<T> clazz, String message, Throwable e) {
        throw new JsonInternalException(message, e);
    }

    public static void handle(String message, Throwable e) {
        throw new JsonInternalException(message, e);
    }

    public static String toString(Exception ex) {
        StackTraceElement[] stackTrace;
        CharBuf buffer = CharBuf.create(255);
        buffer.addLine(ex.getLocalizedMessage());
        for (StackTraceElement element : stackTrace = ex.getStackTrace()) {
            buffer.add(element.getClassName());
            Exceptions.sputs(buffer, "class", element.getClassName(), "method", element.getMethodName(), "line", element.getLineNumber());
        }
        return buffer.toString();
    }

    public static String sputs(CharBuf buf, Object ... messages) {
        int index = 0;
        for (Object message : messages) {
            if (index != 0) {
                buf.add(' ');
            }
            ++index;
            if (message == null) {
                buf.add("<NULL>");
                continue;
            }
            if (message.getClass().isArray()) {
                buf.add(Arrays.asList(message).toString());
                continue;
            }
            buf.add(message.toString());
        }
        buf.add('\n');
        return buf.toString();
    }

    public static String sputs(Object ... messages) {
        CharBuf buf = CharBuf.create(100);
        return Exceptions.sputs(buf, messages);
    }

    public static class JsonInternalException
    extends JsonException {
        public JsonInternalException(String message) {
            super(message);
        }

        public JsonInternalException(String message, Throwable cause) {
            super(message, cause);
        }

        public JsonInternalException(Throwable cause) {
            super("Wrapped Exception", cause);
        }

        @Override
        public void printStackTrace(PrintStream s) {
            s.println(this.getMessage());
            if (this.getCause() != null) {
                s.println("This Exception was wrapped, the original exception\nstack trace is:\n");
                this.getCause().printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        @Override
        public String getMessage() {
            return super.getMessage() + (this.getCause() == null ? "" : this.getCauseMessage());
        }

        private String getCauseMessage() {
            return "\n CAUSE " + this.getCause().getClass().getName() + " :: " + this.getCause().getMessage();
        }

        @Override
        public String getLocalizedMessage() {
            return this.getMessage();
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            if (this.getCause() != null) {
                return this.getCause().getStackTrace();
            }
            return super.getStackTrace();
        }

        @Override
        public Throwable getCause() {
            return super.getCause();
        }

        @Override
        public void printStackTrace(PrintWriter s) {
            s.println(this.getMessage());
            if (this.getCause() != null) {
                s.println("This Exception was wrapped, the original exception\nstack trace is:\n");
                this.getCause().printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        @Override
        public void printStackTrace() {
            System.err.println(this.getMessage());
            if (this.getCause() != null) {
                System.err.println("This Exception was wrapped, the original exception\nstack trace is:\n");
                this.getCause().printStackTrace();
            } else {
                super.printStackTrace();
            }
        }
    }
}

