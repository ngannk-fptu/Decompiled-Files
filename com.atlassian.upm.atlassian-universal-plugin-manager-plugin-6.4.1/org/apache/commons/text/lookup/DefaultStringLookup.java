/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;

public enum DefaultStringLookup {
    BASE64_DECODER("base64Decoder", StringLookupFactory.INSTANCE.base64DecoderStringLookup()),
    BASE64_ENCODER("base64Encoder", StringLookupFactory.INSTANCE.base64EncoderStringLookup()),
    CONST("const", StringLookupFactory.INSTANCE.constantStringLookup()),
    DATE("date", StringLookupFactory.INSTANCE.dateStringLookup()),
    DNS("dns", StringLookupFactory.INSTANCE.dnsStringLookup()),
    ENVIRONMENT("env", StringLookupFactory.INSTANCE.environmentVariableStringLookup()),
    FILE("file", StringLookupFactory.INSTANCE.fileStringLookup()),
    JAVA("java", StringLookupFactory.INSTANCE.javaPlatformStringLookup()),
    LOCAL_HOST("localhost", StringLookupFactory.INSTANCE.localHostStringLookup()),
    PROPERTIES("properties", StringLookupFactory.INSTANCE.propertiesStringLookup()),
    RESOURCE_BUNDLE("resourceBundle", StringLookupFactory.INSTANCE.resourceBundleStringLookup()),
    SCRIPT("script", StringLookupFactory.INSTANCE.scriptStringLookup()),
    SYSTEM_PROPERTIES("sys", StringLookupFactory.INSTANCE.systemPropertyStringLookup()),
    URL("url", StringLookupFactory.INSTANCE.urlStringLookup()),
    URL_DECODER("urlDecoder", StringLookupFactory.INSTANCE.urlDecoderStringLookup()),
    URL_ENCODER("urlEncoder", StringLookupFactory.INSTANCE.urlEncoderStringLookup()),
    XML("xml", StringLookupFactory.INSTANCE.xmlStringLookup());

    private final String key;
    private final StringLookup lookup;

    private DefaultStringLookup(String prefix, StringLookup lookup) {
        this.key = prefix;
        this.lookup = lookup;
    }

    public String getKey() {
        return this.key;
    }

    public StringLookup getStringLookup() {
        return this.lookup;
    }
}

