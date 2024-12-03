/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface DavProperty<T>
extends XmlSerializable,
DavConstants,
PropEntry {
    public DavPropertyName getName();

    public T getValue();

    public boolean isInvisibleInAllprop();
}

