/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.ContextFactory
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package com.atlassian.lesscss;

import com.atlassian.lesscss.ScriptableCreationListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

class NotifyingContextFactory
extends ContextFactory {
    private final Set<ScriptableCreationListener> creationListeners = new CopyOnWriteArraySet<ScriptableCreationListener>();

    NotifyingContextFactory() {
    }

    protected Context makeContext() {
        return new ListenableContext();
    }

    void addListener(ScriptableCreationListener listener) {
        this.creationListeners.add(listener);
    }

    void removeListener(ScriptableCreationListener listener) {
        this.creationListeners.remove(listener);
    }

    private void notifyCreationListeners(Scriptable value) {
        for (ScriptableCreationListener listener : this.creationListeners) {
            listener.onNewScriptable(value);
        }
    }

    class ListenableContext
    extends Context {
        ListenableContext() {
            super((ContextFactory)NotifyingContextFactory.this);
        }

        public Scriptable newArray(Scriptable scope, Object[] elements) {
            return this.notifyCreationListeners(super.newArray(scope, elements));
        }

        public Scriptable newObject(Scriptable scope) {
            return this.notifyCreationListeners(super.newObject(scope));
        }

        public Scriptable newObject(Scriptable scope, String constructorName) {
            return this.notifyCreationListeners(super.newObject(scope, constructorName));
        }

        public Scriptable newObject(Scriptable scope, String constructorName, Object[] args) {
            return this.notifyCreationListeners(super.newObject(scope, constructorName, args));
        }

        public Scriptable newArray(Scriptable scope, int length) {
            return this.notifyCreationListeners(super.newArray(scope, length));
        }

        public ScriptableObject initStandardObjects(ScriptableObject scope, boolean sealed) {
            return this.notifyCreationListeners(super.initStandardObjects(scope, sealed));
        }

        private <T extends Scriptable> T notifyCreationListeners(T value) {
            NotifyingContextFactory.this.notifyCreationListeners(value);
            return value;
        }
    }
}

