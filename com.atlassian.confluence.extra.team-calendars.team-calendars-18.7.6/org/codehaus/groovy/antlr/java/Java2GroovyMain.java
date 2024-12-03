/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.GroovyInternalPosixParser;
import groovyjarjarcommonscli.Options;
import java.util.Arrays;
import org.codehaus.groovy.antlr.java.Java2GroovyProcessor;

public class Java2GroovyMain {
    public static void main(String[] args) {
        try {
            Options options = new Options();
            GroovyInternalPosixParser cliParser = new GroovyInternalPosixParser();
            CommandLine cli = cliParser.parse(options, args);
            String[] filenames = cli.getArgs();
            if (filenames.length == 0) {
                System.err.println("Needs at least one filename");
            }
            Java2GroovyProcessor.processFiles(Arrays.asList(filenames));
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

