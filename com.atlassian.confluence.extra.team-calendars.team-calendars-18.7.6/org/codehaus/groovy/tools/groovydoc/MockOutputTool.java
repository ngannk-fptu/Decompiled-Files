/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.tools.groovydoc.OutputTool;

public class MockOutputTool
implements OutputTool {
    Set outputAreas = new LinkedHashSet();
    Map output = new LinkedHashMap();

    @Override
    public void makeOutputArea(String filename) {
        this.outputAreas.add(filename);
    }

    @Override
    public void writeToOutput(String fileName, String text, String charset) throws Exception {
        this.output.put(fileName, text);
    }

    public boolean isValidOutputArea(String fileName) {
        return this.outputAreas.contains(fileName);
    }

    public String getText(String fileName) {
        return (String)this.output.get(fileName);
    }

    public String toString() {
        return "dirs:" + this.outputAreas + ", files:" + this.output.keySet();
    }
}

