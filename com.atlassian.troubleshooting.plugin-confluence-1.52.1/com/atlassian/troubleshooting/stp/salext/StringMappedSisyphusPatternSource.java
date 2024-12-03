/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext;

import com.atlassian.sisyphus.MappedSisyphusPatternSource;
import com.atlassian.sisyphus.SisyphusPatternPersister;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringMappedSisyphusPatternSource
extends MappedSisyphusPatternSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringMappedSisyphusPatternSource.class);

    public StringMappedSisyphusPatternSource(String string) {
        try {
            this.regexMap = new SisyphusPatternPersister().readPatternsIn(new StringReader(string));
        }
        catch (ClassNotFoundException e) {
            LOGGER.error("The data that is being loaded refers to classes that do not exist in the classpath", (Throwable)e);
        }
        catch (StreamCorruptedException e) {
            LOGGER.error("The data that is being loaded is invalid, it might be something other than serialisised SisyphusPatterns", (Throwable)e);
        }
        catch (IOException e) {
            LOGGER.error("Some other IO exception happened", (Throwable)e);
        }
    }
}

