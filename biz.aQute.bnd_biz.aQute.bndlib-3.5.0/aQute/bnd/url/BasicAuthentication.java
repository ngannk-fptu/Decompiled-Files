/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.url;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.url.DefaultURLConnectionHandler;
import aQute.lib.base64.Base64;
import aQute.libg.cryptography.SHA1;
import aQute.service.reporter.Reporter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BndPlugin(name="url.basic.authentication", parameters=Config.class)
public class BasicAuthentication
extends DefaultURLConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthentication.class);
    private static final String USER = "user";
    private static final String PASSWORD = ".password";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_BASIC_AUTH = "Basic ";
    private String password;
    private String user;
    private String authentication;
    private String sha;

    public BasicAuthentication() {
    }

    public BasicAuthentication(String user, String password, Reporter reporter) {
        this.user = user;
        this.password = password;
        this.setReporter(reporter);
        this.init(null);
    }

    @Override
    public void setProperties(Map<String, String> map) throws Exception {
        super.setProperties(map);
        this.password = map.get(PASSWORD);
        this.user = map.get(USER);
        this.init(map);
    }

    void init(Map<String, String> map) {
        if (this.password == null) {
            this.error("No .password property set on this plugin %s", map);
        }
        if (this.password == null) {
            this.error("No user property set on this plugin %s", map);
        }
        String authString = this.user + ":" + this.password;
        try {
            String encoded = Base64.encodeBase64(authString.getBytes(StandardCharsets.UTF_8));
            this.authentication = PREFIX_BASIC_AUTH + encoded;
            this.sha = SHA1.digest(this.password.getBytes()).asHex();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void handle(URLConnection connection) {
        if (connection instanceof HttpURLConnection && this.matches(connection) && this.password != null && this.user != null) {
            if (!(connection instanceof HttpsURLConnection)) {
                logger.debug("using basic authentication with http instead of https, this is very insecure: {}", (Object)connection.getURL());
            }
            connection.setRequestProperty(HEADER_AUTHORIZATION, this.authentication);
        }
    }

    public String toString() {
        return "BasicAuthentication [password=" + this.sha + ", user=" + this.user + "]";
    }

    static interface Config
    extends DefaultURLConnectionHandler.Config {
        public String user();

        public String _password();
    }
}

