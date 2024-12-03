/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.activation.DataSource;

public class URLDataSource
implements DataSource {
    private URL url = null;
    private URLConnection url_conn = null;

    public URLDataSource(URL url) {
        this.url = url;
    }

    @Override
    public String getContentType() {
        String type = null;
        try {
            if (this.url_conn == null) {
                this.url_conn = this.url.openConnection();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.url_conn != null) {
            type = this.url_conn.getContentType();
        }
        if (type == null) {
            type = "application/octet-stream";
        }
        return type;
    }

    @Override
    public String getName() {
        return this.url.getFile();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.url.openStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        this.url_conn = this.url.openConnection();
        if (this.url_conn != null) {
            this.url_conn.setDoOutput(true);
            return this.url_conn.getOutputStream();
        }
        return null;
    }

    public URL getURL() {
        return this.url;
    }
}

