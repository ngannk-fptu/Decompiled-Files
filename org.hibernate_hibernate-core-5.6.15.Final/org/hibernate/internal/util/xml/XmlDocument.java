/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import java.io.Serializable;
import org.hibernate.internal.util.xml.Origin;

@Deprecated
public interface XmlDocument
extends Serializable {
    public Origin getOrigin();
}

