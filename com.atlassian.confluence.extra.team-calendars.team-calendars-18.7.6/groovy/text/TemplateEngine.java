/*
 * Decompiled with CFR 0.152.
 */
package groovy.text;

import groovy.text.Template;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;

public abstract class TemplateEngine {
    public abstract Template createTemplate(Reader var1) throws CompilationFailedException, ClassNotFoundException, IOException;

    public Template createTemplate(String templateText) throws CompilationFailedException, ClassNotFoundException, IOException {
        return this.createTemplate(new StringReader(templateText));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Template createTemplate(File file) throws CompilationFailedException, ClassNotFoundException, IOException {
        FileReader reader = new FileReader(file);
        try {
            Template template = this.createTemplate(reader);
            return template;
        }
        finally {
            DefaultGroovyMethodsSupport.closeWithWarning(reader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Template createTemplate(URL url) throws CompilationFailedException, ClassNotFoundException, IOException {
        InputStreamReader reader = new InputStreamReader(url.openStream());
        try {
            Template template = this.createTemplate(reader);
            return template;
        }
        finally {
            DefaultGroovyMethodsSupport.closeWithWarning(reader);
        }
    }
}

