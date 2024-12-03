/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.terracotta.context;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.context.ContextCreationListener;
import org.terracotta.context.ContextElement;
import org.terracotta.context.ContextListener;
import org.terracotta.context.MutableTreeNode;
import org.terracotta.context.RootNode;
import org.terracotta.context.TreeNode;
import org.terracotta.context.WeakIdentityHashMap;
import org.terracotta.context.annotations.ContextChild;
import org.terracotta.context.annotations.ContextParent;
import org.terracotta.context.extractor.ObjectContextExtractor;
import org.terracotta.context.query.Query;
import org.terracotta.context.query.QueryBuilder;

public class ContextManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextManager.class);
    private static final WeakIdentityHashMap<Object, MutableTreeNode> CONTEXT_OBJECTS = new WeakIdentityHashMap();
    private static final Collection<ContextCreationListener> contextCreationListeners = new CopyOnWriteArrayList<ContextCreationListener>();
    private final RootNode root = new RootNode();

    public static Association associate(final Object object) {
        return new Association(){

            @Override
            public Association withChild(Object child) {
                ContextManager.associate(child, object);
                return this;
            }

            @Override
            public Association withParent(Object parent) {
                ContextManager.associate(object, parent);
                return this;
            }
        };
    }

    public static Dissociation dissociate(final Object object) {
        return new Dissociation(){

            @Override
            public Dissociation fromChild(Object child) {
                ContextManager.dissociate(child, object);
                return this;
            }

            @Override
            public Dissociation fromParent(Object parent) {
                ContextManager.dissociate(object, parent);
                return this;
            }
        };
    }

    public static TreeNode nodeFor(Object object) {
        return ContextManager.getTreeNode(object);
    }

    public static void registerContextCreationListener(ContextCreationListener listener) {
        contextCreationListeners.add(listener);
    }

    public static void deregisterContextCreationListener(ContextCreationListener listener) {
        contextCreationListeners.remove(listener);
    }

    private static void associate(Object child, Object parent) {
        ContextManager.getOrCreateTreeNode(parent).addChild(ContextManager.getOrCreateTreeNode(child));
    }

    private static void dissociate(Object child, Object parent) {
        ContextManager.getTreeNode(parent).removeChild(ContextManager.getTreeNode(child));
    }

    private static MutableTreeNode getTreeNode(Object object) {
        return CONTEXT_OBJECTS.get(object);
    }

    private static MutableTreeNode getOrCreateTreeNode(Object object) {
        MutableTreeNode node = CONTEXT_OBJECTS.get(object);
        if (node == null) {
            ContextElement context = ObjectContextExtractor.extract(object);
            node = new MutableTreeNode(context);
            MutableTreeNode racer = CONTEXT_OBJECTS.putIfAbsent(object, node);
            if (racer != null) {
                return racer;
            }
            ContextManager.discoverAssociations(object);
            ContextManager.contextCreated(object);
            return node;
        }
        return node;
    }

    private static void discoverAssociations(Object origin) {
        for (Class<?> c = origin.getClass(); c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                Object parent;
                if (f.isAnnotationPresent(ContextChild.class)) {
                    Object child;
                    f.setAccessible(true);
                    try {
                        child = f.get(origin);
                    }
                    catch (IllegalArgumentException ex) {
                        throw new AssertionError((Object)ex);
                    }
                    catch (IllegalAccessException ex) {
                        LOGGER.warn("Failed to traverse {} due to: {}", (Object)f, (Object)ex);
                        continue;
                    }
                    if (child != null) {
                        ContextManager.associate(child, origin);
                    }
                }
                if (!f.isAnnotationPresent(ContextParent.class)) continue;
                f.setAccessible(true);
                try {
                    parent = f.get(origin);
                }
                catch (IllegalArgumentException ex) {
                    throw new AssertionError((Object)ex);
                }
                catch (IllegalAccessException ex) {
                    LOGGER.warn("Failed to traverse {} due to: {}", (Object)f, (Object)ex);
                    continue;
                }
                if (parent == null) continue;
                ContextManager.associate(origin, parent);
            }
        }
    }

    private static void contextCreated(Object object) {
        for (ContextCreationListener listener : contextCreationListeners) {
            listener.contextCreated(object);
        }
    }

    public void root(Object object) {
        this.root.addChild(ContextManager.getOrCreateTreeNode(object));
    }

    public void uproot(Object object) {
        this.root.removeChild(ContextManager.getTreeNode(object));
    }

    public Set<TreeNode> query(Query query) {
        return query.execute(Collections.singleton(this.root));
    }

    public TreeNode queryForSingleton(Query query) throws IllegalStateException {
        return this.query(QueryBuilder.queryBuilder().chain(query).ensureUnique().build()).iterator().next();
    }

    public void registerContextListener(ContextListener listener) {
        this.root.addListener(listener);
    }

    public void deregisterContextListener(ContextListener listener) {
        this.root.removeListener(listener);
    }

    public static interface Dissociation {
        public Dissociation fromChild(Object var1);

        public Dissociation fromParent(Object var1);
    }

    public static interface Association {
        public Association withChild(Object var1);

        public Association withParent(Object var1);
    }
}

