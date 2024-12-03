/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.url;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.url.DefaultURLConnectionHandler;
import aQute.lib.converter.Converter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@BndPlugin(name="url.settings", parameters=Config.class)
public class ConnectionSettings
extends DefaultURLConnectionHandler {
    final Map<String, String> headers = new HashMap<String, String>();
    Config config;

    @Override
    public void handle(URLConnection connection) throws Exception {
        if (this.matches(connection)) {
            if (this.config.connectTimeout() != 0) {
                connection.setConnectTimeout(this.config.connectTimeout());
            }
            if (this.config.readTimeout() != 0) {
                connection.setConnectTimeout(this.config.readTimeout());
            }
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                if (!Character.isUpperCase(entry.getKey().charAt(0))) continue;
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection http = (HttpURLConnection)connection;
                if (this.config.chunk() > 0) {
                    http.setChunkedStreamingMode(this.config.chunk());
                }
                http.setInstanceFollowRedirects(!this.config.noredirect());
            }
        }
    }

    @Override
    public void setProperties(Map<String, String> map) throws Exception {
        super.setProperties(map);
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            if (!Character.isUpperCase(entry.getKey().charAt(0))) continue;
            this.headers.put(entry.getKey(), entry.getValue());
        }
        this.config = Converter.cnv(Config.class, map);
    }

    static interface Config
    extends DefaultURLConnectionHandler.Config {
        public int connectTimeout();

        public int readTimeout();

        public boolean useCaches();

        public int chunk();

        public boolean noredirect();
    }
}

