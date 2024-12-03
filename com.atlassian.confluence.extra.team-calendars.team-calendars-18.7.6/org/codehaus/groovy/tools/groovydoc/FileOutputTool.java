/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.io.File;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.OutputTool;

public class FileOutputTool
implements OutputTool {
    @Override
    public void makeOutputArea(String filename) {
        File dir = new File(filename);
        dir.mkdirs();
    }

    @Override
    public void writeToOutput(String fileName, String text, String charset) throws Exception {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        ResourceGroovyMethods.write(file, text, charset);
    }
}

