/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.taskdefs.AbstractJarSignerTask;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;

public class VerifyJar
extends AbstractJarSignerTask {
    public static final String ERROR_NO_FILE = "Not found :";
    public static final String ERROR_NO_VERIFY = "Failed to verify ";
    private static final String VERIFIED_TEXT = "jar verified.";
    private boolean certificates = false;
    private BufferingOutputFilter outputCache = new BufferingOutputFilter();
    private String savedStorePass = null;

    public void setCertificates(boolean certificates) {
        this.certificates = certificates;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        boolean hasJar;
        boolean bl = hasJar = this.jar != null;
        if (!hasJar && !this.hasResources()) {
            throw new BuildException("jar must be set through jar attribute or nested filesets");
        }
        this.beginExecution();
        RedirectorElement redirector = this.getRedirector();
        redirector.setAlwaysLog(true);
        FilterChain outputFilterChain = redirector.createOutputFilterChain();
        outputFilterChain.add(this.outputCache);
        try {
            Path sources = this.createUnifiedSourcePath();
            for (Resource r : sources) {
                FileProvider fr = r.as(FileProvider.class);
                this.verifyOneJar(fr.getFile());
            }
        }
        finally {
            this.endExecution();
        }
    }

    @Override
    protected void beginExecution() {
        if (this.storepass != null) {
            this.savedStorePass = this.storepass;
            this.setStorepass(null);
        }
        super.beginExecution();
    }

    @Override
    protected void endExecution() {
        if (this.savedStorePass != null) {
            this.setStorepass(this.savedStorePass);
            this.savedStorePass = null;
        }
        super.endExecution();
    }

    private void verifyOneJar(File jar) {
        if (!jar.exists()) {
            throw new BuildException(ERROR_NO_FILE + jar);
        }
        ExecTask cmd = this.createJarSigner();
        this.setCommonOptions(cmd);
        this.bindToKeystore(cmd);
        if (this.savedStorePass != null) {
            this.addValue(cmd, "-storepass");
            this.addValue(cmd, this.savedStorePass);
        }
        this.addValue(cmd, "-verify");
        if (this.certificates) {
            this.addValue(cmd, "-certs");
        }
        this.addValue(cmd, jar.getPath());
        if (this.alias != null) {
            this.addValue(cmd, this.alias);
        }
        this.log("Verifying JAR: " + jar.getAbsolutePath());
        this.outputCache.clear();
        BuildException ex = null;
        try {
            cmd.execute();
        }
        catch (BuildException e) {
            ex = e;
        }
        String results = this.outputCache.toString();
        if (ex != null) {
            if (results.contains("zip file closed")) {
                this.log("You are running jarsigner against a JVM with a known bug that manifests as an IllegalStateException.", 1);
            } else {
                throw ex;
            }
        }
        if (!results.contains(VERIFIED_TEXT)) {
            throw new BuildException(ERROR_NO_VERIFY + jar);
        }
    }

    private static class BufferingOutputFilter
    implements ChainableReader {
        private BufferingOutputFilterReader buffer;

        private BufferingOutputFilter() {
        }

        @Override
        public Reader chain(Reader rdr) {
            this.buffer = new BufferingOutputFilterReader(rdr);
            return this.buffer;
        }

        public String toString() {
            return this.buffer.toString();
        }

        public void clear() {
            if (this.buffer != null) {
                this.buffer.clear();
            }
        }
    }

    private static class BufferingOutputFilterReader
    extends Reader {
        private Reader next;
        private StringBuffer buffer = new StringBuffer();

        public BufferingOutputFilterReader(Reader next) {
            this.next = next;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int result = this.next.read(cbuf, off, len);
            this.buffer.append(cbuf, off, len);
            return result;
        }

        @Override
        public void close() throws IOException {
            this.next.close();
        }

        public String toString() {
            return this.buffer.toString();
        }

        public void clear() {
            this.buffer = new StringBuffer();
        }
    }
}

