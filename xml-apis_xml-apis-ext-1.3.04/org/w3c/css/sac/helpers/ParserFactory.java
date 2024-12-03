/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac.helpers;

import org.w3c.css.sac.Parser;

public class ParserFactory {
    public Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException {
        String string = System.getProperty("org.w3c.css.sac.parser");
        if (string == null) {
            throw new NullPointerException("No value for sac.parser property");
        }
        return (Parser)Class.forName(string).newInstance();
    }
}

