/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPattern;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SisyphusPatternPersister {
    private static final Logger log = LoggerFactory.getLogger(SisyphusPatternPersister.class);
    private final XStream xstream = new XStream();

    public SisyphusPatternPersister() {
        XStream.setupDefaultSecurity(this.xstream);
        this.xstream.allowTypes(new Class[]{SisyphusPattern.class});
    }

    public void writePatternsOut(Writer out, Map<String, SisyphusPattern> patterns) throws IOException {
        this.xstream.alias("RegexEntry", SisyphusPattern.class);
        try (ObjectOutputStream xmlout = this.xstream.createObjectOutputStream(new BufferedWriter(out));){
            for (SisyphusPattern pattern : patterns.values()) {
                xmlout.writeObject(pattern);
            }
        }
    }

    public Map<String, SisyphusPattern> readPatternsIn(Reader reader) throws IOException, ClassNotFoundException {
        HashMap<String, SisyphusPattern> patterns = new HashMap<String, SisyphusPattern>();
        this.xstream.alias("RegexEntry", SisyphusPattern.class);
        ObjectInputStream xmlIn = this.xstream.createObjectInputStream(new BufferedReader(reader));
        Throwable throwable = null;
        try {
            try {
                while (true) {
                    SisyphusPattern pat = (SisyphusPattern)xmlIn.readObject();
                    pat.getPattern();
                    if (pat.isBrokenPattern()) {
                        log.info("INVALID PATTERN: '" + pat.getRegex() + " at " + pat.getURL());
                        continue;
                    }
                    patterns.put(pat.getId(), pat);
                }
            }
            catch (EOFException eof) {
                if (xmlIn != null) {
                    if (throwable != null) {
                        try {
                            xmlIn.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        xmlIn.close();
                    }
                }
            }
        }
        catch (Throwable throwable3) {
            try {
                throwable = throwable3;
                throw throwable3;
            }
            catch (Throwable throwable4) {
                if (xmlIn != null) {
                    if (throwable != null) {
                        try {
                            xmlIn.close();
                        }
                        catch (Throwable throwable5) {
                            throwable.addSuppressed(throwable5);
                        }
                    } else {
                        xmlIn.close();
                    }
                }
                throw throwable4;
            }
        }
        return patterns;
    }
}

