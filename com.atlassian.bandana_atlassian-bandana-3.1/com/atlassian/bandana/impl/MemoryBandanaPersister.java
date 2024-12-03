/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.XStream
 */
package com.atlassian.bandana.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.bandana.impl.PersisterKey;
import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MemoryBandanaPersister
implements BandanaPersister {
    Map store = new HashMap();
    private XStream xStream = new XStream();

    public void setxStream(XStream xStream) {
        this.xStream = xStream;
    }

    @Override
    public Object retrieve(BandanaContext context, String key) {
        String xml = (String)this.store.get(new PersisterKey(context, key));
        if (xml == null) {
            return null;
        }
        return this.xStream.fromXML(xml);
    }

    public Map retrieve(BandanaContext context) {
        HashMap m = new HashMap();
        for (Map.Entry e : this.store.entrySet()) {
            PersisterKey pk = (PersisterKey)e.getKey();
            if (!context.equals(pk.getContext())) continue;
            m.put(pk.getKey(), e.getValue());
        }
        return m;
    }

    @Override
    public void store(BandanaContext context, String key, Object value) {
        this.store.put(new PersisterKey(context, key), this.xStream.toXML(value));
    }

    @Override
    public void flushCaches() {
    }

    @Override
    public void remove(BandanaContext context) {
        Iterator iterator = this.store.keySet().iterator();
        while (iterator.hasNext()) {
            PersisterKey persisterKey = (PersisterKey)iterator.next();
            if (!persisterKey.getContext().equals(context)) continue;
            iterator.remove();
        }
    }

    @Override
    public void remove(BandanaContext context, String key) {
        this.store.remove(new PersisterKey(context, key));
    }

    @Override
    public Iterable<String> retrieveKeys(BandanaContext context) {
        ArrayList<String> keys = new ArrayList<String>();
        for (Map.Entry e : this.store.entrySet()) {
            PersisterKey pk = (PersisterKey)e.getKey();
            if (!context.equals(pk.getContext())) continue;
            keys.add(pk.getKey());
        }
        return keys;
    }
}

