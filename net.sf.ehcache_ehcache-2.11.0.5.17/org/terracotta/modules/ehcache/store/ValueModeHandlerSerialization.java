/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store;

import java.io.IOException;
import java.io.Serializable;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementData;
import net.sf.ehcache.EternalElementData;
import net.sf.ehcache.NonEternalElementData;
import org.terracotta.modules.ehcache.collections.SerializationHelper;
import org.terracotta.modules.ehcache.store.ValueModeHandler;

public class ValueModeHandlerSerialization
implements ValueModeHandler {
    @Override
    public Object getRealKeyObject(String portableKey) {
        try {
            return SerializationHelper.deserializeFromString(portableKey, null);
        }
        catch (IOException e) {
            return null;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object getRealKeyObject(String portableKey, ClassLoader loader) {
        try {
            return SerializationHelper.deserializeFromString(portableKey, loader);
        }
        catch (IOException e) {
            return null;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public String createPortableKey(Object key) throws IOException {
        return SerializationHelper.serializeToString(key);
    }

    @Override
    public ElementData createElementData(Element element) {
        if (element.isEternal()) {
            return new EternalElementData(element);
        }
        return new NonEternalElementData(element);
    }

    @Override
    public Element createElement(Object key, Serializable value) {
        return value == null ? null : ((ElementData)value).createElement(key);
    }
}

