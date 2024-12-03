/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.connection.settings;

import aQute.bnd.connection.settings.ProxyDTO;
import aQute.bnd.connection.settings.ServerDTO;
import aQute.bnd.connection.settings.SettingsDTO;
import aQute.bnd.connection.settings.SettingsParser;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.http.HttpClient;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.url.ProxyHandler;
import aQute.bnd.service.url.URLConnectionHandler;
import aQute.bnd.url.BasicAuthentication;
import aQute.bnd.url.HttpsVerification;
import aQute.lib.concurrentinit.ConcurrentInitialize;
import aQute.lib.converter.Converter;
import aQute.lib.io.IO;
import aQute.lib.mavenpasswordobfuscator.MavenPasswordObfuscator;
import aQute.lib.xpath.XPathParser;
import aQute.libg.glob.Glob;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionSettings
extends Processor {
    static final Logger logger = LoggerFactory.getLogger(ConnectionSettings.class);
    public static final String M2_SETTINGS_SECURITY_XML = "~/.m2/settings-security.xml";
    public static final String M2_SETTINGS_SECURITY_PROPERTY = "settings.security";
    private static final String M2_SETTINGS_XML = "~/.m2/settings.xml";
    private static final String BND_CONNECTION_SETTINGS_XML = "~/.bnd/connection-settings.xml";
    private static final String CONNECTION_SETTINGS = "-connection-settings";
    private HttpClient client;
    private List<ServerDTO> servers = new ArrayList<ServerDTO>();
    private ConcurrentInitialize<String> mavenMasterPassphrase = new ConcurrentInitialize<String>(){

        @Override
        public String create() throws Exception {
            return ConnectionSettings.this.readMavenMasterPassphrase();
        }
    };
    static final String IPNR_PART_S = "([01]\\d\\d)|(2[0-4]\\d)|(25[0-5])";
    static final String IPNR_S = "([01]\\d\\d)|(2[0-4]\\d)|(25[0-5]).([01]\\d\\d)|(2[0-4]\\d)|(25[0-5]).([01]\\d\\d)|(2[0-4]\\d)|(25[0-5]).([01]\\d\\d)|(2[0-4]\\d)|(25[0-5])";
    static Pattern MASK_P = Pattern.compile("(?<if>[^:]):(?<ip>[^/])/(?<valid>.*)");

    public ConnectionSettings(Processor processor, HttpClient client) throws Exception {
        super(processor);
        this.client = client;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readSettings() throws Exception {
        File tmp = null;
        try {
            Parameters connectionSettings = new Parameters(this.mergeProperties(CONNECTION_SETTINGS), this.getParent());
            if (connectionSettings.isEmpty()) {
                File file = IO.getFile(BND_CONNECTION_SETTINGS_XML);
                if (!file.isFile() && !(file = IO.getFile(M2_SETTINGS_XML)).isFile()) {
                    return;
                }
                this.parse(file);
                return;
            }
            for (Map.Entry<String, Attrs> entry : connectionSettings.entrySet()) {
                File file;
                String key = entry.getKey();
                if ("false".equalsIgnoreCase(key)) continue;
                switch (key) {
                    case "maven": {
                        key = M2_SETTINGS_XML;
                        break;
                    }
                    case "bnd": {
                        key = BND_CONNECTION_SETTINGS_XML;
                        break;
                    }
                    case "env": {
                        Attrs attrs = entry.getValue();
                        String variable = attrs.get("var");
                        if (variable == null) {
                            this.getParent().error("Specified -connection-settings: %s, with 'env' but the 'var' parameter is no found", connectionSettings);
                            break;
                        }
                        String value = System.getenv(key);
                        if (value != null) {
                            tmp = File.createTempFile("tmp", ".bnd");
                            IO.store((Object)value, tmp);
                            key = tmp.getAbsolutePath();
                            break;
                        }
                        this.getParent().error("Specified -connection-settings: %s, but no such environment variable %s is found", connectionSettings, key);
                    }
                }
                boolean ignoreError = false;
                if (key.startsWith("-")) {
                    ignoreError = true;
                    key = key.substring(1);
                }
                if ("server".equals(key = Processor.removeDuplicateMarker(key))) {
                    this.parseServer(entry.getValue());
                    continue;
                }
                File file2 = file = this.getParent() != null ? IO.getFile(key) : this.getParent().getFile(key);
                if (!file.isFile()) {
                    if (ignoreError) continue;
                    Reporter.SetLocation error = this.getParent().error("Specified -connection-settings: %s, but no such file or is directory", file);
                    Processor.FileLine header = this.getParent().getHeader(CONNECTION_SETTINGS, key);
                    if (header == null) continue;
                    header.set(error);
                    continue;
                }
                this.parse(file);
            }
        }
        finally {
            if (tmp != null) {
                IO.delete(tmp);
            }
        }
    }

    private void parseServer(Attrs value) throws Exception {
        ServerDTO server = Converter.cnv(ServerDTO.class, (Object)value);
        if (this.isPassword(server) || this.isPrivateKey(server)) {
            if (server.id == null) {
                server.id = "*";
            }
            this.add(server);
        }
    }

    private boolean isPrivateKey(ServerDTO server) {
        return !this.isEmpty(server.privateKey) && !this.isEmpty(server.passphrase);
    }

    private boolean isPassword(ServerDTO server) {
        return !this.isEmpty(server.username) && !this.isEmpty(server.password);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public URLConnectionHandler createUrlConnectionHandler(ServerDTO serverDTO) {
        final Glob match = new Glob(serverDTO.match == null ? serverDTO.id : serverDTO.match);
        final BasicAuthentication basic = this.getBasicAuthentication(serverDTO.username, serverDTO.password);
        final HttpsVerification https = new HttpsVerification(serverDTO.trust, serverDTO.verify, (Reporter)this.getParent());
        return new URLConnectionHandler(){

            @Override
            public boolean matches(URL url) {
                String scheme = url.getProtocol().toLowerCase();
                StringBuilder address = new StringBuilder();
                address.append(scheme).append("://").append(url.getHost());
                if (url.getPort() > 0 && url.getPort() != url.getDefaultPort()) {
                    address.append(":").append(url.getPort());
                }
                return match.matcher(address).matches();
            }

            @Override
            public void handle(URLConnection connection) throws Exception {
                if (basic != null) {
                    basic.handle(connection);
                }
                if (this.isHttps(connection) && https != null) {
                    https.handle(connection);
                }
            }

            boolean isHttps(URLConnection connection) {
                return "https".equalsIgnoreCase(connection.getURL().getProtocol());
            }

            public String toString() {
                return "Server [ match=" + match + ", basic=" + basic + ", https=" + https + "]";
            }
        };
    }

    public BasicAuthentication getBasicAuthentication(String username, String password) {
        if (username != null && password != null) {
            return new BasicAuthentication(username, password, this.getParent());
        }
        return null;
    }

    public static ProxyHandler createProxyHandler(final ProxyDTO proxyDTO) {
        return new ProxyHandler(){
            Glob[] globs;
            private ProxyHandler.ProxySetup proxySetup;

            @Override
            public ProxyHandler.ProxySetup forURL(URL url) throws Exception {
                Proxy.Type type;
                switch (proxyDTO.protocol.toUpperCase()) {
                    case "DIRECT": {
                        type = Proxy.Type.DIRECT;
                        break;
                    }
                    case "HTTP": {
                        type = Proxy.Type.HTTP;
                        if (url.getProtocol().equalsIgnoreCase("http")) break;
                        return null;
                    }
                    case "HTTPS": {
                        type = Proxy.Type.HTTP;
                        if (url.getProtocol().equalsIgnoreCase("https")) break;
                        return null;
                    }
                    case "SOCKS": {
                        type = Proxy.Type.SOCKS;
                        break;
                    }
                    default: {
                        type = Proxy.Type.HTTP;
                    }
                }
                String host = url.getHost();
                if (host != null && this.isNonProxyHost(host)) {
                    return null;
                }
                if (this.proxySetup == null) {
                    this.proxySetup = new ProxyHandler.ProxySetup();
                    if (proxyDTO.username != null && proxyDTO.password != null) {
                        this.proxySetup.authentication = new PasswordAuthentication(proxyDTO.username, proxyDTO.password.toCharArray());
                    }
                    InetSocketAddress socketAddress = proxyDTO.host != null ? new InetSocketAddress(proxyDTO.host, proxyDTO.port) : new InetSocketAddress(proxyDTO.port);
                    this.proxySetup.proxy = new Proxy(type, socketAddress);
                }
                return this.proxySetup;
            }

            public boolean isNonProxyHost(String host) {
                Glob[] globs;
                for (Glob glob : globs = this.getNonProxyHosts(proxyDTO)) {
                    if (!glob.matcher(host).matches()) continue;
                    return true;
                }
                return false;
            }

            public Glob[] getNonProxyHosts(ProxyDTO proxyDTO2) {
                if (this.globs == null) {
                    if (proxyDTO2.nonProxyHosts == null) {
                        this.globs = new Glob[0];
                    } else {
                        String[] parts = proxyDTO2.nonProxyHosts.split("\\s*\\|\\s*");
                        this.globs = new Glob[parts.length];
                        for (int i = 0; i < parts.length; ++i) {
                            this.globs[i] = new Glob(parts[i]);
                        }
                    }
                }
                return this.globs;
            }
        };
    }

    private void parse(File file) throws Exception {
        assert (file != null) : "File must be set";
        assert (file.isFile()) : "File must be a file and exist";
        SettingsParser parser = new SettingsParser(file);
        SettingsDTO settings = parser.getSettings();
        for (ProxyDTO proxyDTO : settings.proxies) {
            if (!this.isActive(proxyDTO)) continue;
            this.add(proxyDTO);
        }
        ServerDTO deflt = null;
        for (ServerDTO serverDTO : settings.servers) {
            String masterPassphrase;
            serverDTO.trust = ConnectionSettings.makeAbsolute(file, serverDTO.trust);
            if (MavenPasswordObfuscator.isObfuscatedPassword(serverDTO.password) && (masterPassphrase = this.mavenMasterPassphrase.get()) != null) {
                serverDTO.password = MavenPasswordObfuscator.decrypt(serverDTO.password, masterPassphrase);
            }
            if ("default".equals(serverDTO.id)) {
                deflt = serverDTO;
                continue;
            }
            this.add(serverDTO);
        }
        if (deflt != null) {
            this.add(deflt);
        }
    }

    private String readMavenMasterPassphrase() throws Exception {
        String path = System.getProperty(M2_SETTINGS_SECURITY_PROPERTY, M2_SETTINGS_SECURITY_XML);
        File file = IO.getFile(path);
        if (!file.isFile()) {
            logger.info("No Maven security settings file {}", (Object)path);
            return null;
        }
        XPathParser sp = new XPathParser(file);
        String master = sp.parse("/settingsSecurity/master");
        if (master == null || master.isEmpty()) {
            this.warning("Found Maven security settings file %s but not master password in it", path);
            return null;
        }
        if (!MavenPasswordObfuscator.isObfuscatedPassword(master)) {
            this.warning("Master password in %s was not obfuscated, using actual value", path);
            return master;
        }
        try {
            return MavenPasswordObfuscator.decrypt(master, M2_SETTINGS_SECURITY_PROPERTY);
        }
        catch (Exception e) {
            this.exception(e, "Could not decrypt the master password from %s with key %s", path, M2_SETTINGS_SECURITY_PROPERTY);
            return null;
        }
    }

    private boolean isActive(ProxyDTO proxy) throws SocketException {
        String[] clauses;
        if (!proxy.active) {
            return false;
        }
        String mask = proxy.mask;
        if (mask == null) {
            return true;
        }
        for (String clause : clauses = mask.split("\\s*,\\s*")) {
            try {
                String[] parts = clause.split("\\s*:\\s*");
                Glob g = new Glob(parts[0]);
                byte[] address = null;
                int maskLength = 0;
                if (parts.length > 1) {
                    String[] pp = parts[1].split("/");
                    address = InetAddress.getByName(pp[0]).getAddress();
                    maskLength = pp.length > 1 ? Integer.parseInt(pp[1]) : address.length * 8;
                }
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    if (ni == null || !ni.isUp() || !g.matcher(ni.getName()).matches()) continue;
                    if (address == null) {
                        return true;
                    }
                    for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                        byte[] iaa = ia.getAddress().getAddress();
                        if (address.length != iaa.length || maskLength != 0 && ia.getNetworkPrefixLength() != maskLength || !Arrays.equals(address, iaa)) continue;
                        return true;
                    }
                }
            }
            catch (Exception e) {
                this.exception(e, "Failed to parse proxy 'mask' clause in settings: %s", clause);
            }
        }
        return false;
    }

    public static String makeAbsolute(File cwd, String trust) {
        String[] parts;
        if (trust == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (String part : parts = trust.split("\\s*,\\s*")) {
            File file = new File(cwd, part).getAbsoluteFile();
            sb.append(del);
            del = ",";
            sb.append(file.getAbsolutePath());
        }
        return sb.toString();
    }

    public void add(ServerDTO server) {
        this.servers.add(server);
        if (this.client != null) {
            this.client.addURLConnectionHandler(this.createUrlConnectionHandler(server));
        }
    }

    public void add(ProxyDTO proxy) {
        if (this.client != null) {
            this.client.addProxyHandler(ConnectionSettings.createProxyHandler(proxy));
        }
    }

    public List<ServerDTO> getServerDTOs() {
        return this.servers;
    }
}

