/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.compiler;

import java.io.IOException;
import java.util.List;

public interface Compiler {
    public void addFile(String var1);

    public void setSource(String var1);

    public void setDestination(String var1);

    public void setClasspath(String var1);

    public void setEncoding(String var1);

    public boolean compile() throws IOException;

    public List getErrors() throws IOException;
}

