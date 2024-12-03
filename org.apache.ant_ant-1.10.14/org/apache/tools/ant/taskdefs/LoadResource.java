/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class LoadResource
extends Task {
    private Resource src;
    private boolean failOnError = true;
    private boolean quiet = false;
    private String encoding = null;
    private String property = null;
    private final List<FilterChain> filterChains = new Vector<FilterChain>();

    public final void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public final void setProperty(String property) {
        this.property = property;
    }

    public final void setFailonerror(boolean fail) {
        this.failOnError = fail;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
        if (quiet) {
            this.failOnError = false;
        }
    }

    @Override
    public final void execute() throws BuildException {
        if (this.src == null) {
            throw new BuildException("source resource not defined");
        }
        if (this.property == null) {
            throw new BuildException("output property not defined");
        }
        if (this.quiet && this.failOnError) {
            throw new BuildException("quiet and failonerror cannot both be set to true");
        }
        if (!this.src.isExists()) {
            String message = this.src + " doesn't exist";
            if (this.failOnError) {
                throw new BuildException(message);
            }
            this.log(message, this.quiet ? 3 : 0);
            return;
        }
        this.log("loading " + this.src + " into property " + this.property, 3);
        Charset charset = this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding);
        try {
            String text;
            long len = this.src.getSize();
            this.log("resource size = " + (len != -1L ? String.valueOf(len) : "unknown"), 4);
            int size = (int)len;
            if (size != 0) {
                try (ChainReaderHelper.ChainReader chainReader = new ChainReaderHelper(this.getProject(), new InputStreamReader((InputStream)new BufferedInputStream(this.src.getInputStream()), charset), this.filterChains).with(crh -> {
                    if (this.src.getSize() != -1L) {
                        crh.setBufferSize(size);
                    }
                }).getAssembledReader();){
                    text = chainReader.readFully();
                }
            } else {
                this.log("Do not set property " + this.property + " as its length is 0.", this.quiet ? 3 : 2);
                text = null;
            }
            if (text != null && !text.isEmpty()) {
                this.getProject().setNewProperty(this.property, text);
                this.log("loaded " + text.length() + " characters", 3);
                this.log(this.property + " := " + text, 4);
            }
        }
        catch (IOException ioe) {
            String message = "Unable to load resource: " + ioe;
            if (this.failOnError) {
                throw new BuildException(message, ioe, this.getLocation());
            }
            this.log(message, this.quiet ? 3 : 0);
        }
        catch (BuildException be) {
            if (this.failOnError) {
                throw be;
            }
            this.log(be.getMessage(), this.quiet ? 3 : 0);
        }
    }

    public final void addFilterChain(FilterChain filter) {
        this.filterChains.add(filter);
    }

    public void addConfigured(ResourceCollection a) {
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported");
        }
        this.src = (Resource)a.iterator().next();
    }
}

