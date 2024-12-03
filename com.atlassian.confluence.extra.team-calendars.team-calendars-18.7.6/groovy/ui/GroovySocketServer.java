/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.ui.GroovyMain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class GroovySocketServer
implements Runnable {
    private URL url;
    private GroovyShell groovy;
    private GroovyCodeSource source;
    private boolean autoOutput;
    private static int counter;
    private static final Pattern URI_PATTERN;

    public GroovySocketServer(GroovyShell groovy, boolean isScriptFile, String scriptFilenameOrText, boolean autoOutput, int port) {
        this(groovy, GroovySocketServer.getCodeSource(isScriptFile, scriptFilenameOrText), autoOutput, port);
    }

    private static GroovyCodeSource getCodeSource(boolean scriptFile, String scriptFilenameOrText) {
        if (scriptFile) {
            try {
                if (URI_PATTERN.matcher(scriptFilenameOrText).matches()) {
                    return new GroovyCodeSource(new URI(scriptFilenameOrText));
                }
                return new GroovyCodeSource(GroovyMain.searchForGroovyScriptFile(scriptFilenameOrText));
            }
            catch (IOException e) {
                throw new GroovyRuntimeException("Unable to get script from: " + scriptFilenameOrText, e);
            }
            catch (URISyntaxException e) {
                throw new GroovyRuntimeException("Unable to get script from URI: " + scriptFilenameOrText, e);
            }
        }
        return new GroovyCodeSource(scriptFilenameOrText, GroovySocketServer.generateScriptName(), "/groovy/shell");
    }

    private static synchronized String generateScriptName() {
        return "ServerSocketScript" + ++counter + ".groovy";
    }

    public GroovySocketServer(GroovyShell groovy, GroovyCodeSource source, boolean autoOutput, int port) {
        this.groovy = groovy;
        this.source = source;
        this.autoOutput = autoOutput;
        try {
            this.url = new URL("http", InetAddress.getLocalHost().getHostAddress(), port, "/");
            System.out.println("groovy is listening on port " + port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.url.getPort());
            while (true) {
                Script script = this.groovy.parse(this.source);
                new GroovyClientConnection(script, this.autoOutput, serverSocket.accept());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    static {
        URI_PATTERN = Pattern.compile("\\p{Alpha}[-+.\\p{Alnum}]*:.*");
    }

    class GroovyClientConnection
    implements Runnable {
        private Script script;
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean autoOutputFlag;

        GroovyClientConnection(Script script, boolean autoOutput, Socket socket) throws IOException {
            this.script = script;
            this.autoOutputFlag = autoOutput;
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
            new Thread((Runnable)this, "Groovy client connection - " + socket.getInetAddress().getHostAddress()).start();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                String line = null;
                this.script.setProperty("out", this.writer);
                this.script.setProperty("socket", this.socket);
                this.script.setProperty("init", Boolean.TRUE);
                while ((line = this.reader.readLine()) != null) {
                    this.script.setProperty("line", line);
                    Object o = this.script.run();
                    this.script.setProperty("init", Boolean.FALSE);
                    if (o != null) {
                        if ("success".equals(o)) {
                            break;
                        }
                        if (this.autoOutputFlag) {
                            this.writer.println(o);
                        }
                    }
                    this.writer.flush();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    this.writer.flush();
                    this.writer.close();
                }
                finally {
                    try {
                        this.socket.close();
                    }
                    catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }
}

