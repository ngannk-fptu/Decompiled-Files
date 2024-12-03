/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import org.hibernate.HibernateException;
import org.hibernate.internal.util.xml.Origin;

@Deprecated
public class UnsupportedOrmXsdVersionException
extends HibernateException {
    public UnsupportedOrmXsdVersionException(String requestedVersion, Origin origin) {
        super(String.format("Encountered unsupported orm.xml xsd version [%s] in mapping document [type=%s, name=%s]", requestedVersion, origin.getType(), origin.getName()));
    }
}

