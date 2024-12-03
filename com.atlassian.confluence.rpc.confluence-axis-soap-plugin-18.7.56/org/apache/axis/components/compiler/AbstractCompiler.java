/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.components.compiler.Compiler;

public abstract class AbstractCompiler
implements Compiler {
    protected ArrayList fileList = new ArrayList();
    protected String srcDir;
    protected String destDir;
    protected String classpath;
    protected String encoding = null;
    protected InputStream errors;

    public void addFile(String file) {
        this.fileList.add(file);
    }

    public void setSource(String srcDir) {
        this.srcDir = srcDir;
    }

    public void setDestination(String destDir) {
        this.destDir = destDir;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public List getErrors() throws IOException {
        return this.parseStream(new BufferedReader(new InputStreamReader(this.errors)));
    }

    protected abstract List parseStream(BufferedReader var1) throws IOException;

    protected List fillArguments(List arguments) {
        arguments.add("-d");
        arguments.add(this.destDir);
        arguments.add("-classpath");
        arguments.add(this.classpath);
        if (this.srcDir != null) {
            arguments.add("-sourcepath");
            arguments.add(this.srcDir);
        }
        arguments.add("-O");
        arguments.add("-g");
        if (this.encoding != null) {
            arguments.add("-encoding");
            arguments.add(this.encoding);
        }
        return arguments;
    }

    protected String[] toStringArray(List arguments) {
        int i;
        String[] args = new String[arguments.size() + this.fileList.size()];
        for (i = 0; i < arguments.size(); ++i) {
            args[i] = (String)arguments.get(i);
        }
        for (int j = 0; j < this.fileList.size(); ++j) {
            args[i] = (String)this.fileList.get(j);
            ++i;
        }
        return args;
    }
}

