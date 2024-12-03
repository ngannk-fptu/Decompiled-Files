/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class Upgrade {
    private final String protocolName;
    private final String protocolVersion;

    private Upgrade(String protocolName, String protocolVersion) {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public String toString() {
        if (this.protocolVersion == null) {
            return this.protocolName;
        }
        return this.protocolName + "/" + this.protocolVersion;
    }

    public static List<Upgrade> parse(Enumeration<String> headerValues) {
        try {
            ArrayList<Upgrade> result = new ArrayList<Upgrade>();
            while (headerValues.hasMoreElements()) {
                SkipResult skipComma;
                String headerValue = headerValues.nextElement();
                if (headerValue == null) {
                    return null;
                }
                StringReader r = new StringReader(headerValue);
                do {
                    HttpParser.skipLws(r);
                    String protocolName = HttpParser.readToken(r);
                    if (protocolName == null || protocolName.isEmpty()) {
                        return null;
                    }
                    String protocolVersion = null;
                    if (HttpParser.skipConstant(r, "/") == SkipResult.FOUND && ((protocolVersion = HttpParser.readToken(r)) == null || protocolVersion.isEmpty())) {
                        return null;
                    }
                    HttpParser.skipLws(r);
                    skipComma = HttpParser.skipConstant(r, ",");
                    if (skipComma == SkipResult.NOT_FOUND) {
                        return null;
                    }
                    result.add(new Upgrade(protocolName, protocolVersion));
                } while (skipComma == SkipResult.FOUND);
            }
            return result;
        }
        catch (IOException ioe) {
            return null;
        }
    }
}

