/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;

public class DowngradeActionMap {
    private final HashMap actionMap = new HashMap();
    private final ArrayList actionList = new ArrayList();
    private DowngradeActionMap parent;

    public void setParent(DowngradeActionMap map) {
        this.parent = map;
    }

    public DowngradeActionMap getParent() {
        return this.parent;
    }

    public void put(Object key, Action action) {
        if (action == null) {
            this.remove(key);
        } else {
            if (this.actionMap.containsKey(key)) {
                this.remove(key);
            }
            this.actionMap.put(key, action);
            this.actionList.add(key);
        }
    }

    public Action get(Object key) {
        Action retval = (Action)this.actionMap.get(key);
        if (retval != null) {
            return retval;
        }
        if (this.parent != null) {
            return this.parent.get(key);
        }
        return null;
    }

    public void remove(Object key) {
        this.actionMap.remove(key);
        this.actionList.remove(key);
    }

    public void clear() {
        this.actionMap.clear();
        this.actionList.clear();
    }

    public Object[] keys() {
        return this.actionList.toArray();
    }

    public int size() {
        return this.actionMap.size();
    }

    public Object[] allKeys() {
        if (this.parent == null) {
            return this.keys();
        }
        Object[] parentKeys = this.parent.allKeys();
        Object[] key = this.keys();
        Object[] retval = new Object[parentKeys.length + key.length];
        System.arraycopy(key, 0, retval, 0, key.length);
        System.arraycopy(retval, 0, retval, key.length, retval.length);
        return retval;
    }
}

