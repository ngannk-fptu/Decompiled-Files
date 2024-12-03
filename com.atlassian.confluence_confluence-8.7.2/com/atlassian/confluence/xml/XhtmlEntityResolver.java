/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.xml;

import com.atlassian.confluence.xml.XMLEntityResolver;
import com.atlassian.core.util.ClassLoaderUtils;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XhtmlEntityResolver
implements XMLEntityResolver {
    private final Map<String, String> publicIdToEntityMap;
    private final String xhtmlEntities;

    public XhtmlEntityResolver() {
        try {
            this.publicIdToEntityMap = ImmutableMap.of((Object)"-//W3C//ENTITIES Latin 1 for XHTML//EN", (Object)this.readResource("xhtml-lat1.ent"), (Object)"-//W3C//ENTITIES Symbols for XHTML//EN", (Object)this.readResource("xhtml-symbol.ent"), (Object)"-//W3C//ENTITIES Special for XHTML//EN", (Object)this.readResource("xhtml-special.ent"));
            this.xhtmlEntities = this.readResource("xhtml.ent");
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public @Nullable Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        String resource = this.resolveEntityString(publicID, systemID);
        if (resource == null) {
            return null;
        }
        return new ByteArrayInputStream(resource.getBytes());
    }

    @Override
    public @Nullable InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        String resource = this.resolveEntityString(publicId, systemId);
        if (resource == null) {
            return null;
        }
        return new InputSource(new StringReader(resource));
    }

    private @Nullable String resolveEntityString(String publicID, String systemID) {
        if (publicID != null) {
            return this.publicIdToEntityMap.get(publicID);
        }
        if ("xhtml.ent".equals(systemID)) {
            return this.xhtmlEntities;
        }
        return null;
    }

    @Override
    @Deprecated
    public String createDTD() {
        return this.xhtmlEntities;
    }

    private String readResource(String name) throws IOException {
        String resourceName = "xhtml/" + name;
        try (InputStream istr = ClassLoaderUtils.getResourceAsStream((String)resourceName, XhtmlEntityResolver.class);){
            String string;
            if (istr == null) {
                throw new IOException("The resource " + resourceName + " was not found on the classpath.");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(istr));){
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                string = builder.toString();
            }
            return string;
        }
    }
}

