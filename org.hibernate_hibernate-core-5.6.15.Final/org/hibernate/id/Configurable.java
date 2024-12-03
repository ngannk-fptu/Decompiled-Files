/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

@Deprecated
public interface Configurable {
    public void configure(Type var1, Properties var2, ServiceRegistry var3) throws MappingException;
}

