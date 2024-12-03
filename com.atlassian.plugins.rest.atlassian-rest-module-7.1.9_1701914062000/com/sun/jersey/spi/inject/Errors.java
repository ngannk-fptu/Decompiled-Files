/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Errors {
    private final ArrayList<ErrorMessage> messages = new ArrayList(0);
    private int mark = -1;
    private int stack = 0;
    private boolean fieldReporting = true;
    private static final Logger LOGGER = Logger.getLogger(Errors.class.getName());
    private static ThreadLocal<Errors> errors = new ThreadLocal();

    private void _mark() {
        this.mark = this.messages.size();
    }

    private void _unmark() {
        this.mark = -1;
    }

    private void _reset() {
        if (this.mark >= 0 && this.mark < this.messages.size()) {
            this.messages.subList(this.mark, this.messages.size()).clear();
            this._unmark();
        }
    }

    private void preProcess() {
        ++this.stack;
    }

    private void postProcess(boolean throwException) {
        --this.stack;
        this.fieldReporting = true;
        if (this.stack == 0) {
            try {
                if (!this.messages.isEmpty()) {
                    Errors.processErrorMessages(throwException, this.messages);
                }
            }
            finally {
                errors.remove();
            }
        }
    }

    private static void processErrorMessages(boolean throwException, List<ErrorMessage> messages) {
        StringBuilder sb = new StringBuilder();
        boolean isFatal = false;
        for (ErrorMessage em : messages) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("  ");
            if (em.isFatal) {
                sb.append("SEVERE: ");
            } else {
                sb.append("WARNING: ");
            }
            isFatal |= em.isFatal;
            sb.append(em.message);
        }
        String message = sb.toString();
        if (isFatal) {
            LOGGER.severe("The following errors and warnings have been detected with resource and/or provider classes:\n" + message);
            if (throwException) {
                throw new ErrorMessagesException(new ArrayList<ErrorMessage>(messages));
            }
        } else {
            LOGGER.warning("The following warnings have been detected with resource and/or provider classes:\n" + message);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T processWithErrors(Closure<T> c) {
        T t;
        Errors e = errors.get();
        if (e == null) {
            e = new Errors();
            errors.set(e);
        }
        e.preProcess();
        RuntimeException caught = null;
        try {
            t = c.f();
            e.postProcess(caught == null);
        }
        catch (RuntimeException re) {
            try {
                caught = re;
                e.postProcess(caught == null);
            }
            catch (Throwable throwable) {
                e.postProcess(caught == null);
                throw throwable;
            }
        }
        return t;
        throw caught;
    }

    private static Errors getInstance() {
        Errors e = errors.get();
        if (e == null) {
            throw new IllegalStateException("There is no error processing in scope");
        }
        if (e.stack == 0) {
            errors.remove();
            throw new IllegalStateException("There is no error processing in scope");
        }
        return e;
    }

    public static void mark() {
        Errors.getInstance()._mark();
    }

    public static void unmark() {
        Errors.getInstance()._unmark();
    }

    public static void reset() {
        Errors.getInstance()._reset();
    }

    public static void error(String message) {
        Errors.error(message, true);
    }

    public static void error(String message, boolean isFatal) {
        ErrorMessage em = new ErrorMessage(message, isFatal);
        Errors.getInstance().messages.add(em);
    }

    public int numberOfErrors() {
        return Errors.getInstance().messages.size();
    }

    public static void innerClass(Class c) {
        Errors.error("The inner class " + c.getName() + " is not a static inner class and cannot be instantiated.");
    }

    public static void nonPublicClass(Class c) {
        Errors.error("The class " + c.getName() + " is a not a public class and cannot be instantiated.");
    }

    public static void nonPublicConstructor(Class c) {
        Errors.error("The class " + c.getName() + " does not have a public constructor and cannot be instantiated.");
    }

    public static void abstractClass(Class c) {
        Errors.error("The class " + c.getName() + " is an abstract class and cannot be instantiated.");
    }

    public static void interfaceClass(Class c) {
        Errors.error("The class " + c.getName() + " is an interface and cannot be instantiated.");
    }

    public static void missingDependency(Constructor ctor, int i) {
        Errors.error("Missing dependency for constructor " + ctor + " at parameter index " + i);
    }

    public static void setReportMissingDependentFieldOrMethod(boolean fieldReporting) {
        Errors.getInstance().fieldReporting = fieldReporting;
    }

    public static boolean getReportMissingDependentFieldOrMethod() {
        return Errors.getInstance().fieldReporting;
    }

    public static void missingDependency(Field f) {
        if (Errors.getReportMissingDependentFieldOrMethod()) {
            Errors.error("Missing dependency for field: " + f.toGenericString());
        }
    }

    public static void missingDependency(Method m, int i) {
        if (Errors.getReportMissingDependentFieldOrMethod()) {
            Errors.error("Missing dependency for method " + m + " at parameter at index " + i);
        }
    }

    public static interface Closure<T> {
        public T f();
    }

    public static class ErrorMessage {
        final String message;
        final boolean isFatal;

        private ErrorMessage(String message, boolean isFatal) {
            this.message = message;
            this.isFatal = isFatal;
        }

        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.message != null ? this.message.hashCode() : 0);
            hash = 37 * hash + (this.isFatal ? 1 : 0);
            return hash;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ErrorMessage other = (ErrorMessage)obj;
            if (this.message == null ? other.message != null : !this.message.equals(other.message)) {
                return false;
            }
            return this.isFatal == other.isFatal;
        }
    }

    public static class ErrorMessagesException
    extends RuntimeException {
        public final List<ErrorMessage> messages;

        private ErrorMessagesException(List<ErrorMessage> messages) {
            this.messages = messages;
        }
    }
}

