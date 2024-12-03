/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.compact;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import javax.jcr.NamespaceException;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefWriter;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.commons.conversion.DefaultNamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;
import org.apache.jackrabbit.spi.commons.namespace.SessionNamespaceResolver;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.value.QValueFactoryImpl;
import org.apache.jackrabbit.spi.commons.value.ValueFactoryQImpl;

public class CompactNodeTypeDefWriter
extends org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefWriter {
    private final NamePathResolver npResolver;

    public CompactNodeTypeDefWriter(Writer out, Session s, boolean includeNS) {
        this(out, new SessionNamespaceResolver(s), new DefaultNamePathResolver(s), includeNS);
    }

    public CompactNodeTypeDefWriter(Writer out, NamespaceResolver r, boolean includeNS) {
        this(out, r, new DefaultNamePathResolver(r), includeNS);
    }

    public CompactNodeTypeDefWriter(Writer out, NamespaceResolver r, NamePathResolver npResolver) {
        this(out, r, npResolver, false);
    }

    public CompactNodeTypeDefWriter(Writer out, NamespaceResolver r, NamePathResolver npResolver, boolean includeNS) {
        super(out, CompactNodeTypeDefWriter.createNsMapping(r), includeNS);
        this.npResolver = npResolver;
    }

    public static void write(Collection<? extends QNodeTypeDefinition> defs, NamespaceResolver r, NamePathResolver npResolver, Writer out) throws IOException {
        CompactNodeTypeDefWriter w = new CompactNodeTypeDefWriter(out, r, npResolver, true);
        for (QNodeTypeDefinition qNodeTypeDefinition : defs) {
            w.write(qNodeTypeDefinition);
        }
        w.close();
    }

    public void write(QNodeTypeDefinition ntd) throws IOException {
        NodeTypeDefinitionImpl def = new NodeTypeDefinitionImpl(ntd, this.npResolver, new ValueFactoryQImpl(QValueFactoryImpl.getInstance(), this.npResolver));
        super.write(def);
    }

    public void write(Collection<? extends QNodeTypeDefinition> defs) throws IOException {
        for (QNodeTypeDefinition qNodeTypeDefinition : defs) {
            this.write(qNodeTypeDefinition);
        }
    }

    private static CompactNodeTypeDefWriter.NamespaceMapping createNsMapping(final NamespaceResolver namespaceResolver) {
        return new CompactNodeTypeDefWriter.NamespaceMapping(){

            @Override
            public String getNamespaceURI(String prefix) {
                try {
                    return namespaceResolver.getURI(prefix);
                }
                catch (NamespaceException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}

