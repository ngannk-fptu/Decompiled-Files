/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParserFactory;

public class ParserFactoryFactory
implements Serializable {
    private static final long serialVersionUID = 4710974869988895410L;
    private final String className;
    private final Map<String, String> args;

    public ParserFactoryFactory(String className, Map<String, String> args) {
        this.className = className;
        this.args = args;
    }

    public ParserFactory build() throws TikaException {
        try {
            Class<?> clazz = Class.forName(this.className);
            Constructor<?> con = clazz.getConstructor(Map.class);
            return (ParserFactory)con.newInstance(this.args);
        }
        catch (IllegalStateException | ReflectiveOperationException e) {
            throw new TikaException("Couldn't create factory", e);
        }
    }
}

