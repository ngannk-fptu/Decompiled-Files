/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Janitor;

public class MultipleCompilationErrorsException
extends CompilationFailedException {
    protected ErrorCollector collector;

    public MultipleCompilationErrorsException(ErrorCollector ec) {
        super(0, null);
        if (ec == null) {
            CompilerConfiguration config = super.getUnit() != null ? super.getUnit().getConfiguration() : new CompilerConfiguration();
            this.collector = new ErrorCollector(config);
        } else {
            this.collector = ec;
        }
    }

    public ErrorCollector getErrorCollector() {
        return this.collector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getMessage() {
        StringWriter data = new StringWriter();
        PrintWriter writer = new PrintWriter(data);
        Janitor janitor = new Janitor();
        writer.write(super.getMessage());
        writer.println(":");
        try {
            this.collector.write(writer, janitor);
        }
        finally {
            janitor.cleanup();
        }
        return data.toString();
    }
}

