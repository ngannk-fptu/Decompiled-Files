/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xml;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ResourceEntityResolver
implements EntityResolver {
    ClassLoader cl;
    String prefix;

    public ResourceEntityResolver(ClassLoader classLoader, String string) {
        this.cl = classLoader;
        this.prefix = string;
    }

    public ResourceEntityResolver(Class clazz) {
        this(clazz.getClassLoader(), ResourceEntityResolver.classToPrefix(clazz));
    }

    @Override
    public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
        if (string2 == null) {
            return null;
        }
        int n = string2.lastIndexOf(47);
        String string3 = n >= 0 ? string2.substring(n + 1) : string2;
        InputStream inputStream = this.cl.getResourceAsStream(this.prefix + string3);
        return inputStream == null ? null : new InputSource(inputStream);
    }

    private static String classToPrefix(Class clazz) {
        String string = clazz.getName();
        int n = string.lastIndexOf(46);
        String string2 = n > 0 ? string.substring(0, n) : null;
        StringBuffer stringBuffer = new StringBuffer(256);
        if (string2 != null) {
            stringBuffer.append(string2);
            int n2 = stringBuffer.length();
            for (int i = 0; i < n2; ++i) {
                if (stringBuffer.charAt(i) != '.') continue;
                stringBuffer.setCharAt(i, '/');
            }
            stringBuffer.append('/');
        }
        return stringBuffer.toString();
    }
}

