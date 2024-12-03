/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class KeyToGroupMap
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -2228169345475318082L;
    private Comparable defaultGroup;
    private List groups;
    private Map keyToGroupMap;

    public KeyToGroupMap() {
        this((Comparable)((Object)"Default Group"));
    }

    public KeyToGroupMap(Comparable defaultGroup) {
        if (defaultGroup == null) {
            throw new IllegalArgumentException("Null 'defaultGroup' argument.");
        }
        this.defaultGroup = defaultGroup;
        this.groups = new ArrayList();
        this.keyToGroupMap = new HashMap();
    }

    public int getGroupCount() {
        return this.groups.size() + 1;
    }

    public List getGroups() {
        ArrayList<Comparable> result = new ArrayList<Comparable>();
        result.add(this.defaultGroup);
        Iterator iterator = this.groups.iterator();
        while (iterator.hasNext()) {
            Comparable group = (Comparable)iterator.next();
            if (result.contains(group)) continue;
            result.add(group);
        }
        return result;
    }

    public int getGroupIndex(Comparable group) {
        int result = this.groups.indexOf(group);
        if (result < 0) {
            if (this.defaultGroup.equals(group)) {
                result = 0;
            }
        } else {
            ++result;
        }
        return result;
    }

    public Comparable getGroup(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        Comparable result = this.defaultGroup;
        Comparable group = (Comparable)this.keyToGroupMap.get(key);
        if (group != null) {
            result = group;
        }
        return result;
    }

    public void mapKeyToGroup(Comparable key, Comparable group) {
        int count;
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        Comparable currentGroup = this.getGroup(key);
        if (!currentGroup.equals(this.defaultGroup) && !currentGroup.equals(group) && (count = this.getKeyCount(currentGroup)) == 1) {
            this.groups.remove(currentGroup);
        }
        if (group == null) {
            this.keyToGroupMap.remove(key);
        } else {
            if (!this.groups.contains(group) && !this.defaultGroup.equals(group)) {
                this.groups.add(group);
            }
            this.keyToGroupMap.put(key, group);
        }
    }

    public int getKeyCount(Comparable group) {
        if (group == null) {
            throw new IllegalArgumentException("Null 'group' argument.");
        }
        int result = 0;
        Iterator iterator = this.keyToGroupMap.values().iterator();
        while (iterator.hasNext()) {
            Comparable g = (Comparable)iterator.next();
            if (!group.equals(g)) continue;
            ++result;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyToGroupMap)) {
            return false;
        }
        KeyToGroupMap that = (KeyToGroupMap)obj;
        if (!ObjectUtilities.equal(this.defaultGroup, that.defaultGroup)) {
            return false;
        }
        return ((Object)this.keyToGroupMap).equals(that.keyToGroupMap);
    }

    public Object clone() throws CloneNotSupportedException {
        KeyToGroupMap result = (KeyToGroupMap)super.clone();
        result.defaultGroup = (Comparable)KeyToGroupMap.clone(this.defaultGroup);
        result.groups = (List)KeyToGroupMap.clone(this.groups);
        result.keyToGroupMap = (Map)KeyToGroupMap.clone(this.keyToGroupMap);
        return result;
    }

    private static Object clone(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> c = object.getClass();
        Object result = null;
        try {
            Method m = c.getMethod("clone", null);
            if (Modifier.isPublic(m.getModifiers())) {
                try {
                    result = m.invoke(object, (Object[])null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (NoSuchMethodException e) {
            result = object;
        }
        return result;
    }

    private static Collection clone(Collection list) throws CloneNotSupportedException {
        List result = null;
        if (list != null) {
            try {
                List clone = (List)list.getClass().newInstance();
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    clone.add(KeyToGroupMap.clone(iterator.next()));
                }
                result = clone;
            }
            catch (Exception e) {
                throw new CloneNotSupportedException("Exception.");
            }
        }
        return result;
    }
}

