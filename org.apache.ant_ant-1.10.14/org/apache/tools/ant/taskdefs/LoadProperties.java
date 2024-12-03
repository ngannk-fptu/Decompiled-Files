/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;

public class LoadProperties
extends Task {
    private Resource src = null;
    private final List<FilterChain> filterChains = new Vector<FilterChain>();
    private String encoding = null;
    private String prefix = null;
    private boolean prefixValues = true;

    public final void setSrcFile(File srcFile) {
        this.addConfigured(new FileResource(srcFile));
    }

    public void setResource(String resource) {
        this.getRequiredJavaResource().setName(resource);
    }

    public final void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setClasspath(Path classpath) {
        this.getRequiredJavaResource().setClasspath(classpath);
    }

    public Path createClasspath() {
        return this.getRequiredJavaResource().createClasspath();
    }

    public void setClasspathRef(Reference r) {
        this.getRequiredJavaResource().setClasspathRef(r);
    }

    public Path getClasspath() {
        return this.getRequiredJavaResource().getClasspath();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefixValues(boolean b) {
        this.prefixValues = b;
    }

    @Override
    public final void execute() throws BuildException {
        if (this.src == null) {
            throw new BuildException("A source resource is required.");
        }
        if (!this.src.isExists()) {
            if (this.src instanceof JavaResource) {
                this.log("Unable to find resource " + this.src, 1);
                return;
            }
            throw new BuildException("Source resource does not exist: " + this.src);
        }
        Charset charset = this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding);
        try (ChainReaderHelper.ChainReader instream = new ChainReaderHelper(this.getProject(), new InputStreamReader((InputStream)new BufferedInputStream(this.src.getInputStream()), charset), this.filterChains).getAssembledReader();){
            String text = instream.readFully();
            if (text != null && !text.isEmpty()) {
                if (!text.endsWith("\n")) {
                    text = text + "\n";
                }
                ByteArrayInputStream tis = new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1));
                Properties props = new Properties();
                props.load(tis);
                Property propertyTask = new Property();
                propertyTask.bindToOwner(this);
                propertyTask.setPrefix(this.prefix);
                propertyTask.setPrefixValues(this.prefixValues);
                propertyTask.addProperties(props);
            }
        }
        catch (IOException ioe) {
            throw new BuildException("Unable to load file: " + ioe, ioe, this.getLocation());
        }
    }

    public final void addFilterChain(FilterChain filter) {
        this.filterChains.add(filter);
    }

    public synchronized void addConfigured(ResourceCollection a) {
        if (this.src != null) {
            throw new BuildException("only a single source is supported");
        }
        if (a.size() != 1) {
            throw new BuildException("only single-element resource collections are supported");
        }
        this.src = (Resource)a.iterator().next();
    }

    private synchronized JavaResource getRequiredJavaResource() {
        if (this.src == null) {
            this.src = new JavaResource();
            this.src.setProject(this.getProject());
        } else if (!(this.src instanceof JavaResource)) {
            throw new BuildException("expected a java resource as source");
        }
        return (JavaResource)this.src;
    }
}

