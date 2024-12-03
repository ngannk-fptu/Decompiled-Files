/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class QueueFactory {
    private static final QueueFactory INSTANCE;
    private final Dispatcher dispatcher = QueueFactory.doPrivileged(JavaDispatcher.of(Dispatcher.class));
    private static final boolean ACCESS_CONTROLLER;

    private QueueFactory() {
    }

    public static <T> Queue<T> make() {
        LinkedList queue = QueueFactory.INSTANCE.dispatcher.arrayDeque();
        return queue == null ? new LinkedList() : queue;
    }

    public static <T> Queue<T> make(Collection<? extends T> elements) {
        LinkedList<T> queue = QueueFactory.INSTANCE.dispatcher.arrayDeque(elements);
        return queue == null ? new LinkedList<T>(elements) : queue;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        try {
            Class.forName("java.security.AccessController", false, null);
            ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            ACCESS_CONTROLLER = false;
        }
        catch (SecurityException securityException) {
            ACCESS_CONTROLLER = true;
        }
        INSTANCE = new QueueFactory();
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
        return this.dispatcher.equals(((QueueFactory)object).dispatcher);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.dispatcher.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JavaDispatcher.Defaults
    @JavaDispatcher.Proxied(value="java.util.ArrayDeque")
    protected static interface Dispatcher {
        @MaybeNull
        @JavaDispatcher.IsConstructor
        @JavaDispatcher.Proxied(value="arrayDeque")
        public <T> Queue<T> arrayDeque();

        @MaybeNull
        @JavaDispatcher.IsConstructor
        @JavaDispatcher.Proxied(value="arrayDeque")
        public <T> Queue<T> arrayDeque(Collection<? extends T> var1);
    }
}

