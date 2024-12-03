/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Source;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;

public abstract class SourceImpl
extends AbstractQOMNode
implements Source {
    public SourceImpl(NamePathResolver resolver) {
        super(resolver);
    }

    public abstract SelectorImpl[] getSelectors();
}

