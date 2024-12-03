/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.spi;

import java.io.InputStream;
import javax.xml.transform.Source;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.spi.Binding;

public interface Binder {
    public Binding bind(Source var1, Origin var2);

    public Binding bind(InputStream var1, Origin var2);
}

