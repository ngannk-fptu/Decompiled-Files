/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;
import org.apache.catalina.ant.AbstractCatalinaCommandTask;
import org.apache.tools.ant.BuildException;

public class DeployTask
extends AbstractCatalinaCommandTask {
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("\\w{3,5}\\:");
    protected String config = null;
    protected String localWar = null;
    protected String tag = null;
    protected boolean update = false;
    protected String war = null;

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getLocalWar() {
        return this.localWar;
    }

    public void setLocalWar(String localWar) {
        this.localWar = localWar;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getUpdate() {
        return this.update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getWar() {
        return this.war;
    }

    public void setWar(String war) {
        this.war = war;
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        if (this.path == null) {
            throw new BuildException("Must specify 'path' attribute");
        }
        if (this.war == null && this.localWar == null && this.config == null && this.tag == null) {
            throw new BuildException("Must specify either 'war', 'localWar', 'config', or 'tag' attribute");
        }
        BufferedInputStream stream = null;
        String contentType = null;
        long contentLength = -1L;
        if (this.war != null) {
            if (PROTOCOL_PATTERN.matcher(this.war).lookingAt()) {
                try {
                    URI uri = new URI(this.war);
                    URLConnection conn = uri.toURL().openConnection();
                    contentLength = conn.getContentLengthLong();
                    stream = new BufferedInputStream(conn.getInputStream(), 1024);
                }
                catch (IOException | URISyntaxException e) {
                    throw new BuildException((Throwable)e);
                }
            }
            FileInputStream fsInput = null;
            try {
                fsInput = new FileInputStream(this.war);
                FileChannel fsChannel = fsInput.getChannel();
                contentLength = fsChannel.size();
                stream = new BufferedInputStream(fsInput, 1024);
            }
            catch (IOException e) {
                if (fsInput != null) {
                    try {
                        fsInput.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                throw new BuildException((Throwable)e);
            }
            contentType = "application/octet-stream";
        }
        StringBuilder sb = this.createQueryString("/deploy");
        try {
            if (this.war == null && this.config != null) {
                sb.append("&config=");
                sb.append(URLEncoder.encode(this.config, this.getCharset()));
            }
            if (this.war == null && this.localWar != null) {
                sb.append("&war=");
                sb.append(URLEncoder.encode(this.localWar, this.getCharset()));
            }
            if (this.update) {
                sb.append("&update=true");
            }
            if (this.tag != null) {
                sb.append("&tag=");
                sb.append(URLEncoder.encode(this.tag, this.getCharset()));
            }
            this.execute(sb.toString(), stream, contentType, contentLength);
        }
        catch (UnsupportedEncodingException e) {
            throw new BuildException("Invalid 'charset' attribute: " + this.getCharset());
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }
}

