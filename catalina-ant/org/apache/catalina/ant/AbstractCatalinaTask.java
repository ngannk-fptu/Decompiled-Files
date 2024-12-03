/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.util.IOTools
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import org.apache.catalina.ant.BaseRedirectorHelperTask;
import org.apache.catalina.util.IOTools;
import org.apache.tools.ant.BuildException;

public abstract class AbstractCatalinaTask
extends BaseRedirectorHelperTask {
    private static final String CHARSET = "utf-8";
    protected String charset = "ISO-8859-1";
    protected String password = null;
    protected String url = "http://localhost:8080/manager/text";
    protected String username = null;
    protected boolean ignoreResponseConstraint = false;

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIgnoreResponseConstraint() {
        return this.ignoreResponseConstraint;
    }

    public void setIgnoreResponseConstraint(boolean ignoreResponseConstraint) {
        this.ignoreResponseConstraint = ignoreResponseConstraint;
    }

    public void execute() throws BuildException {
        if (this.username == null || this.password == null || this.url == null) {
            throw new BuildException("Must specify all of 'username', 'password', and 'url'");
        }
    }

    public void execute(String command) throws BuildException {
        this.execute(command, null, null, -1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(String command, InputStream istream, String contentType, long contentLength) throws BuildException {
        URLConnection conn = null;
        InputStreamReader reader = null;
        try {
            int ch;
            Authenticator.setDefault(new TaskAuthenticator(this.username, this.password));
            URI uri = new URI(this.url + command);
            uri.parseServerAuthority();
            conn = uri.toURL().openConnection();
            HttpURLConnection hconn = (HttpURLConnection)conn;
            hconn.setAllowUserInteraction(false);
            hconn.setDoInput(true);
            hconn.setUseCaches(false);
            if (istream != null) {
                this.preAuthenticate();
                hconn.setDoOutput(true);
                hconn.setRequestMethod("PUT");
                if (contentType != null) {
                    hconn.setRequestProperty("Content-Type", contentType);
                }
                if (contentLength >= 0L) {
                    hconn.setRequestProperty("Content-Length", "" + contentLength);
                    hconn.setFixedLengthStreamingMode(contentLength);
                }
            } else {
                hconn.setDoOutput(false);
                hconn.setRequestMethod("GET");
            }
            hconn.setRequestProperty("User-Agent", "Catalina-Ant-Task/1.0");
            hconn.connect();
            if (istream != null) {
                try (OutputStream ostream2 = hconn.getOutputStream();){
                    IOTools.flow((InputStream)istream, (OutputStream)ostream2);
                }
                finally {
                    try {
                        istream.close();
                    }
                    catch (Exception ostream2) {}
                }
            }
            reader = new InputStreamReader(hconn.getInputStream(), CHARSET);
            StringBuilder buff = new StringBuilder();
            String error = null;
            int msgPriority = 2;
            boolean first = true;
            while ((ch = reader.read()) >= 0) {
                if (ch == 13 || ch == 10) {
                    if (buff.length() <= 0) continue;
                    String line = buff.toString();
                    buff.setLength(0);
                    if (!this.ignoreResponseConstraint && first) {
                        if (!line.startsWith("OK -")) {
                            error = line;
                            msgPriority = 0;
                        }
                        first = false;
                    }
                    this.handleOutput(line, msgPriority);
                    continue;
                }
                buff.append((char)ch);
            }
            if (buff.length() > 0) {
                this.handleOutput(buff.toString(), msgPriority);
            }
            if (error != null && this.isFailOnError()) {
                throw new BuildException(error);
            }
        }
        catch (Exception e) {
            if (this.isFailOnError()) {
                throw new BuildException((Throwable)e);
            }
            this.handleErrorOutput(e.getMessage());
        }
        finally {
            this.closeRedirector();
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException iOException) {}
                reader = null;
            }
            if (istream != null) {
                try {
                    istream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private void preAuthenticate() throws IOException, URISyntaxException {
        URLConnection conn = null;
        URI uri = new URI(this.url);
        uri.parseServerAuthority();
        conn = uri.toURL().openConnection();
        HttpURLConnection hconn = (HttpURLConnection)conn;
        hconn.setAllowUserInteraction(false);
        hconn.setDoInput(true);
        hconn.setUseCaches(false);
        hconn.setDoOutput(false);
        hconn.setRequestMethod("OPTIONS");
        hconn.setRequestProperty("User-Agent", "Catalina-Ant-Task/1.0");
        hconn.connect();
        try (InputStream is = hconn.getInputStream();){
            IOTools.flow((InputStream)is, null);
        }
    }

    private static class TaskAuthenticator
    extends Authenticator {
        private final String user;
        private final String password;

        private TaskAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password.toCharArray());
        }
    }
}

