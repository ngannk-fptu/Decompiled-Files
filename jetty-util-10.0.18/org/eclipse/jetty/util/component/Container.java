/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public interface Container {
    public boolean addBean(Object var1);

    public boolean addBean(Object var1, boolean var2);

    public Collection<Object> getBeans();

    public <T> Collection<T> getBeans(Class<T> var1);

    default public <T> Collection<T> getCachedBeans(Class<T> clazz) {
        return this.getBeans(clazz);
    }

    public <T> T getBean(Class<T> var1);

    public boolean removeBean(Object var1);

    public boolean addEventListener(EventListener var1);

    public boolean removeEventListener(EventListener var1);

    public void unmanage(Object var1);

    public void manage(Object var1);

    public boolean isManaged(Object var1);

    public <T> Collection<T> getContainedBeans(Class<T> var1);

    default public List<EventListener> getEventListeners() {
        return Collections.unmodifiableList(new ArrayList<EventListener>(this.getBeans(EventListener.class)));
    }

    public static boolean addBean(Object parent, Object child) {
        if (parent instanceof Container) {
            return ((Container)parent).addBean(child);
        }
        return false;
    }

    public static boolean addBean(Object parent, Object child, boolean managed) {
        if (parent instanceof Container) {
            return ((Container)parent).addBean(child, managed);
        }
        return false;
    }

    public static boolean removeBean(Object parent, Object child) {
        if (parent instanceof Container) {
            return ((Container)parent).removeBean(child);
        }
        return false;
    }

    public static interface InheritedListener
    extends Listener {
    }

    public static interface Listener
    extends EventListener {
        public void beanAdded(Container var1, Object var2);

        public void beanRemoved(Container var1, Object var2);
    }
}

