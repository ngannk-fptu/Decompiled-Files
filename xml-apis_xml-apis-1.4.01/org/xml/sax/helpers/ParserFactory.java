/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import org.xml.sax.Parser;
import org.xml.sax.helpers.NewInstance;
import org.xml.sax.helpers.SecuritySupport;

public class ParserFactory {
    private ParserFactory() {
    }

    public static Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException {
        String string = SecuritySupport.getSystemProperty("org.xml.sax.parser");
        if (string == null) {
            throw new NullPointerException("No value for sax.parser property");
        }
        return ParserFactory.makeParser(string);
    }

    public static Parser makeParser(String string) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException {
        return (Parser)NewInstance.newInstance(NewInstance.getClassLoader(), string);
    }
}

