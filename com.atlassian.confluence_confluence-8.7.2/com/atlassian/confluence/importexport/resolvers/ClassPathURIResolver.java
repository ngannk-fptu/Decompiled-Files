/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 */
package com.atlassian.confluence.importexport.resolvers;

import com.atlassian.core.util.ClassLoaderUtils;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

@Deprecated
public class ClassPathURIResolver
implements URIResolver {
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        URL resourceURL = ClassLoaderUtils.getResource((String)href, this.getClass());
        if (resourceURL != null) {
            InputStream resourceStream = ClassLoaderUtils.getResourceAsStream((String)href, this.getClass());
            return new StreamSource(resourceStream, resourceURL.toExternalForm());
        }
        return null;
    }
}

