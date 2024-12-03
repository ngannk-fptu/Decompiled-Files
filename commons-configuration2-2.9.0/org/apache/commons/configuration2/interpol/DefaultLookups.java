/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.lookup.StringLookupFactory
 */
package org.apache.commons.configuration2.interpol;

import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.interpol.StringLookupAdapter;
import org.apache.commons.text.lookup.StringLookupFactory;

public enum DefaultLookups {
    BASE64_DECODER("base64Decoder", new StringLookupAdapter(StringLookupFactory.INSTANCE.base64DecoderStringLookup())),
    BASE64_ENCODER("base64Encoder", new StringLookupAdapter(StringLookupFactory.INSTANCE.base64EncoderStringLookup())),
    CONST("const", new StringLookupAdapter(StringLookupFactory.INSTANCE.constantStringLookup())),
    DATE("date", new StringLookupAdapter(StringLookupFactory.INSTANCE.dateStringLookup())),
    DNS("dns", new StringLookupAdapter(StringLookupFactory.INSTANCE.dnsStringLookup())),
    ENVIRONMENT("env", new StringLookupAdapter(StringLookupFactory.INSTANCE.environmentVariableStringLookup())),
    FILE("file", new StringLookupAdapter(StringLookupFactory.INSTANCE.fileStringLookup())),
    JAVA("java", new StringLookupAdapter(StringLookupFactory.INSTANCE.javaPlatformStringLookup())),
    LOCAL_HOST("localhost", new StringLookupAdapter(StringLookupFactory.INSTANCE.localHostStringLookup())),
    PROPERTIES("properties", new StringLookupAdapter(StringLookupFactory.INSTANCE.propertiesStringLookup())),
    RESOURCE_BUNDLE("resourceBundle", new StringLookupAdapter(StringLookupFactory.INSTANCE.resourceBundleStringLookup())),
    SCRIPT("script", new StringLookupAdapter(StringLookupFactory.INSTANCE.scriptStringLookup())),
    SYSTEM_PROPERTIES("sys", new StringLookupAdapter(StringLookupFactory.INSTANCE.systemPropertyStringLookup())),
    URL("url", new StringLookupAdapter(StringLookupFactory.INSTANCE.urlStringLookup())),
    URL_DECODER("urlDecoder", new StringLookupAdapter(StringLookupFactory.INSTANCE.urlDecoderStringLookup())),
    URL_ENCODER("urlEncoder", new StringLookupAdapter(StringLookupFactory.INSTANCE.urlEncoderStringLookup())),
    XML("xml", new StringLookupAdapter(StringLookupFactory.INSTANCE.xmlStringLookup()));

    private final Lookup lookup;
    private final String prefix;

    private DefaultLookups(String prefix, Lookup lookup) {
        this.prefix = prefix;
        this.lookup = lookup;
    }

    public Lookup getLookup() {
        return this.lookup;
    }

    public String getPrefix() {
        return this.prefix;
    }
}

