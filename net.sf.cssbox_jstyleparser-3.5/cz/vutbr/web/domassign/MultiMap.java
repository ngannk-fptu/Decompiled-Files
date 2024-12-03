/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public abstract class MultiMap<E, P, D> {
    private HashMap<E, D> mainMap;
    private HashMap<E, HashMap<P, D>> pseudoMaps;

    public MultiMap() {
        this.mainMap = new HashMap();
        this.pseudoMaps = new HashMap();
    }

    public MultiMap(int initialSize) {
        this.mainMap = new HashMap(initialSize);
        this.pseudoMaps = new HashMap();
    }

    protected abstract D createDataInstance();

    public int size() {
        return this.mainMap.size();
    }

    public D get(E el, P pseudo) {
        HashMap<P, D> map;
        Object ret = pseudo == null ? this.mainMap.get(el) : ((map = this.pseudoMaps.get(el)) == null ? null : map.get(pseudo));
        return ret;
    }

    public D get(E el) {
        return this.mainMap.get(el);
    }

    public D getOrCreate(E el, P pseudo) {
        D ret;
        if (pseudo == null) {
            ret = this.mainMap.get(el);
            if (ret == null) {
                ret = this.createDataInstance();
                this.mainMap.put(el, ret);
            }
        } else {
            HashMap<Object, Object> map = this.pseudoMaps.get(el);
            if (map == null) {
                map = new HashMap();
                this.pseudoMaps.put(el, map);
            }
            if ((ret = map.get(pseudo)) == null) {
                ret = this.createDataInstance();
                map.put(pseudo, ret);
            }
        }
        return ret;
    }

    public void put(E el, P pseudo, D data) {
        if (pseudo == null) {
            this.mainMap.put(el, data);
        } else {
            HashMap<Object, Object> map = this.pseudoMaps.get(el);
            if (map == null) {
                map = new HashMap();
                this.pseudoMaps.put(el, map);
            }
            map.put(pseudo, data);
        }
    }

    public Set<E> keySet() {
        return this.mainMap.keySet();
    }

    public Set<P> pseudoSet(E el) {
        HashMap<P, D> map = this.pseudoMaps.get(el);
        if (map == null) {
            return Collections.emptySet();
        }
        return map.keySet();
    }

    public boolean hasPseudo(E el, P pseudo) {
        HashMap<P, D> map = this.pseudoMaps.get(el);
        if (map == null) {
            return false;
        }
        return map.containsKey(pseudo);
    }
}

