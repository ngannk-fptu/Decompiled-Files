/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.uri;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.xmlgraphics.util.Service;

public class CommonURIResolver
implements URIResolver {
    private final List uriResolvers = new LinkedList();

    public CommonURIResolver() {
        Iterator<Object> iter = Service.providers(URIResolver.class);
        while (iter.hasNext()) {
            URIResolver resolver = (URIResolver)iter.next();
            this.register(resolver);
        }
    }

    public static CommonURIResolver getDefaultURIResolver() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Source resolve(String href, String base) {
        List list = this.uriResolvers;
        synchronized (list) {
            for (Object uriResolver : this.uriResolvers) {
                URIResolver currentResolver = (URIResolver)uriResolver;
                try {
                    Source result = currentResolver.resolve(href, base);
                    if (result == null) continue;
                    return result;
                }
                catch (TransformerException transformerException) {
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void register(URIResolver uriResolver) {
        List list = this.uriResolvers;
        synchronized (list) {
            this.uriResolvers.add(uriResolver);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregister(URIResolver uriResolver) {
        List list = this.uriResolvers;
        synchronized (list) {
            this.uriResolvers.remove(uriResolver);
        }
    }

    private static final class DefaultInstanceHolder {
        private static final CommonURIResolver INSTANCE = new CommonURIResolver();

        private DefaultInstanceHolder() {
        }
    }
}

