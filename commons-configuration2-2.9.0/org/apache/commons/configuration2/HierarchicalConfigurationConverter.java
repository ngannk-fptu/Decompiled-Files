/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.tree.DefaultConfigurationKey;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;

abstract class HierarchicalConfigurationConverter {
    HierarchicalConfigurationConverter() {
    }

    public void process(Configuration config) {
        if (config != null) {
            DefaultConfigurationKey keyEmpty;
            DefaultExpressionEngine exprEngine = DefaultExpressionEngine.INSTANCE;
            DefaultConfigurationKey keyLast = keyEmpty = new DefaultConfigurationKey(exprEngine);
            HashSet<String> keySet = new HashSet<String>();
            Iterator<String> it = config.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                if (keySet.contains(key)) continue;
                DefaultConfigurationKey keyAct = new DefaultConfigurationKey(exprEngine, key);
                this.closeElements(keyLast, keyAct);
                String elem = this.openElements(keyLast, keyAct, config, keySet);
                this.fireValue(elem, config.getProperty(key));
                keyLast = keyAct;
            }
            this.closeElements(keyLast, keyEmpty);
        }
    }

    protected abstract void elementStart(String var1, Object var2);

    protected abstract void elementEnd(String var1);

    protected void closeElements(DefaultConfigurationKey keyLast, DefaultConfigurationKey keyAct) {
        DefaultConfigurationKey keyDiff = keyAct.differenceKey(keyLast);
        Iterator<String> it = this.reverseIterator(keyDiff);
        if (it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            this.elementEnd(it.next());
        }
    }

    protected Iterator<String> reverseIterator(DefaultConfigurationKey key) {
        ArrayList<String> list = new ArrayList<String>();
        DefaultConfigurationKey.KeyIterator it = key.iterator();
        while (it.hasNext()) {
            list.add(it.nextKey());
        }
        Collections.reverse(list);
        return list.iterator();
    }

    protected String openElements(DefaultConfigurationKey keyLast, DefaultConfigurationKey keyAct, Configuration config, Set<String> keySet) {
        DefaultConfigurationKey.KeyIterator it = keyLast.differenceKey(keyAct).iterator();
        DefaultConfigurationKey k = keyLast.commonKey(keyAct);
        it.nextKey();
        while (it.hasNext()) {
            k.append(it.currentKey(true));
            this.elementStart(it.currentKey(true), config.getProperty(k.toString()));
            keySet.add(k.toString());
            it.nextKey();
        }
        return it.currentKey(true);
    }

    protected void fireValue(String name, Object value) {
        if (value instanceof Collection) {
            Collection valueCol = (Collection)value;
            valueCol.forEach(v -> this.fireValue(name, v));
        } else {
            this.elementStart(name, value);
            this.elementEnd(name);
        }
    }
}

