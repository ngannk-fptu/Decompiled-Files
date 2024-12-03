/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal;

import javax.xml.transform.Source;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.jaxb.spi.XmlSource;

public class JaxpSourceXmlSource
extends XmlSource {
    private final Source jaxpSource;

    public JaxpSourceXmlSource(Origin origin, Source jaxpSource) {
        super(origin);
        this.jaxpSource = jaxpSource;
    }

    @Override
    public Binding doBind(Binder binder) {
        return binder.bind(this.jaxpSource, this.getOrigin());
    }
}

