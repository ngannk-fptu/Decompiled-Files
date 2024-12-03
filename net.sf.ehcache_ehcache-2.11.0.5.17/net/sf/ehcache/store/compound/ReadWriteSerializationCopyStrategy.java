/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementIdHelper;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.util.PreferredLoaderObjectInputStream;

public class ReadWriteSerializationCopyStrategy
implements ReadWriteCopyStrategy<Element> {
    private static final long serialVersionUID = 2659269742281205622L;

    @Override
    public Element copyForWrite(Element value, ClassLoader loader) {
        if (value == null) {
            return null;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        if (value.getObjectValue() == null) {
            return this.duplicateElementWithNewValue(value, null);
        }
        try {
            oos = new ObjectOutputStream(bout);
            oos.writeObject(value.getObjectValue());
        }
        catch (Exception e) {
            throw new CacheException("When configured copyOnRead or copyOnWrite, a Store will only accept Serializable values", e);
        }
        finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            }
            catch (Exception exception) {}
        }
        return this.duplicateElementWithNewValue(value, bout.toByteArray());
    }

    @Override
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        if (storedValue == null) {
            return null;
        }
        if (storedValue.getObjectValue() == null) {
            return this.duplicateElementWithNewValue(storedValue, null);
        }
        ByteArrayInputStream bin = new ByteArrayInputStream((byte[])storedValue.getObjectValue());
        PreferredLoaderObjectInputStream ois = null;
        try {
            ois = new PreferredLoaderObjectInputStream(bin, loader);
            Element element = this.duplicateElementWithNewValue(storedValue, ois.readObject());
            return element;
        }
        catch (Exception e) {
            throw new CacheException("When configured copyOnRead or copyOnWrite, a Store will only accept Serializable values", e);
        }
        finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            }
            catch (Exception exception) {}
        }
    }

    public Element duplicateElementWithNewValue(Element element, Object newValue) {
        Element newElement = element.usesCacheDefaultLifespan() ? new Element(element.getObjectKey(), newValue, element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getHitCount(), element.usesCacheDefaultLifespan(), Integer.MIN_VALUE, Integer.MIN_VALUE, element.getLastUpdateTime()) : new Element(element.getObjectKey(), newValue, element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getHitCount(), element.usesCacheDefaultLifespan(), element.getTimeToLive(), element.getTimeToIdle(), element.getLastUpdateTime());
        if (ElementIdHelper.hasId(element)) {
            ElementIdHelper.setId(newElement, ElementIdHelper.getId(element));
        }
        return newElement;
    }
}

