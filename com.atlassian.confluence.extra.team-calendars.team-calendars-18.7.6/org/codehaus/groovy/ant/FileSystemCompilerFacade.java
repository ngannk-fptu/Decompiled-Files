/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ant;

import java.util.ArrayList;
import java.util.Arrays;
import org.codehaus.groovy.tools.FileSystemCompiler;

public class FileSystemCompilerFacade {
    public static void main(String[] args) {
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        boolean forceLookupUnnamedFiles = argList.contains("--forceLookupUnnamedFiles");
        if (forceLookupUnnamedFiles) {
            argList.remove("--forceLookupUnnamedFiles");
        }
        String[] newArgs = forceLookupUnnamedFiles ? argList.toArray(new String[argList.size()]) : args;
        FileSystemCompiler.commandLineCompileWithErrorHandling(newArgs, forceLookupUnnamedFiles);
    }
}

