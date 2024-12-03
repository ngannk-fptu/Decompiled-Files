/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resolvers;

import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

@Deprecated
public class DelegatingURIResolver
implements URIResolver {
    private List uriResolvers;

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        for (URIResolver uriResolver : this.uriResolvers) {
            Source result = uriResolver.resolve(href, base);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public void setUriResolvers(List uriResolvers) {
        this.uriResolvers = uriResolvers;
    }
}

