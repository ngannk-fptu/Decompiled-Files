/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.security.GroovyCodeSourcePermission;
import groovy.util.CharsetToolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class GroovyCodeSource {
    private CodeSource codeSource;
    private String name;
    private String scriptText;
    Certificate[] certs;
    private boolean cachable;
    private File file;
    private URL url;

    public GroovyCodeSource(String script, String name, String codeBase) {
        this.name = name;
        this.scriptText = script;
        this.codeSource = GroovyCodeSource.createCodeSource(codeBase);
        this.cachable = true;
    }

    public GroovyCodeSource(Reader reader, String name, String codeBase) {
        this.name = name;
        this.codeSource = GroovyCodeSource.createCodeSource(codeBase);
        try {
            this.scriptText = IOGroovyMethods.getText(reader);
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible to read the text content from that reader, for script: " + name + " with codeBase: " + codeBase, e);
        }
    }

    public GroovyCodeSource(final File infile, final String encoding) throws IOException {
        final File file = new File(infile.getCanonicalPath());
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString() + " (" + file.getAbsolutePath() + ")");
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file.toString() + " (" + file.getAbsolutePath() + ") is a directory not a Groovy source file.");
        }
        if (!file.canRead()) {
            throw new RuntimeException(file.toString() + " can not be read. Check the read permission of the file \"" + file.toString() + "\" (" + file.getAbsolutePath() + ").");
        }
        this.file = file;
        this.cachable = true;
        try {
            Object[] info = AccessController.doPrivileged(new PrivilegedExceptionAction<Object[]>(){

                @Override
                public Object[] run() throws IOException {
                    if (encoding != null) {
                        GroovyCodeSource.this.scriptText = ResourceGroovyMethods.getText(infile, encoding);
                    } else {
                        GroovyCodeSource.this.scriptText = ResourceGroovyMethods.getText(infile);
                    }
                    Object[] info = new Object[2];
                    URL url = file.toURI().toURL();
                    info[0] = url.toExternalForm();
                    info[1] = new CodeSource(url, (Certificate[])null);
                    return info;
                }
            });
            this.name = (String)info[0];
            this.codeSource = (CodeSource)info[1];
        }
        catch (PrivilegedActionException pae) {
            Throwable cause = pae.getCause();
            if (cause != null && cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new RuntimeException("Could not construct CodeSource for file: " + file, cause);
        }
    }

    public GroovyCodeSource(File infile) throws IOException {
        this(infile, CharsetToolkit.getDefaultSystemCharset().name());
    }

    public GroovyCodeSource(URI uri) throws IOException {
        this(uri.toURL());
    }

    public GroovyCodeSource(URL url) {
        if (url == null) {
            throw new RuntimeException("Could not construct a GroovyCodeSource from a null URL");
        }
        this.url = url;
        this.name = url.toExternalForm();
        this.codeSource = new CodeSource(url, (Certificate[])null);
        try {
            String contentEncoding = GroovyCodeSource.getContentEncoding(url);
            this.scriptText = contentEncoding != null ? ResourceGroovyMethods.getText(url, contentEncoding) : ResourceGroovyMethods.getText(url);
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible to read the text content from " + this.name, e);
        }
    }

    private static String getContentEncoding(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        String encoding = urlConnection.getContentEncoding();
        try {
            IOGroovyMethods.closeQuietly(urlConnection.getInputStream());
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return encoding;
    }

    CodeSource getCodeSource() {
        return this.codeSource;
    }

    public String getScriptText() {
        return this.scriptText;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public URL getURL() {
        return this.url;
    }

    public void setCachable(boolean b) {
        this.cachable = b;
    }

    public boolean isCachable() {
        return this.cachable;
    }

    private static CodeSource createCodeSource(String codeBase) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new GroovyCodeSourcePermission(codeBase));
        }
        try {
            return new CodeSource(new URL("file", "", codeBase), (Certificate[])null);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("A CodeSource file URL cannot be constructed from the supplied codeBase: " + codeBase);
        }
    }
}

