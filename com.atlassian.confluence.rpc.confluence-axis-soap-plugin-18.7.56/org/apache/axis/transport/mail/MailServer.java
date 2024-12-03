/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Session
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.logging.Log
 *  org.apache.commons.net.pop3.POP3Client
 *  org.apache.commons.net.pop3.POP3MessageInfo
 */
package org.apache.axis.transport.mail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.i18n.Messages;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.mail.MailWorker;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;

public class MailServer
implements Runnable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$mail$MailServer == null ? (class$org$apache$axis$transport$mail$MailServer = MailServer.class$("org.apache.axis.transport.mail.MailServer")) : class$org$apache$axis$transport$mail$MailServer).getName());
    private String host;
    private int port;
    private String userid;
    private String password;
    private static boolean doThreads = true;
    private static AxisServer myAxisServer = null;
    private boolean stopped = false;
    private POP3Client pop3;
    static /* synthetic */ Class class$org$apache$axis$transport$mail$MailServer;

    public MailServer(String host, int port, String userid, String password) {
        this.host = host;
        this.port = port;
        this.userid = userid;
        this.password = password;
    }

    public void setDoThreads(boolean value) {
        doThreads = value;
    }

    public boolean getDoThreads() {
        return doThreads;
    }

    public String getHost() {
        return this.host;
    }

    protected static synchronized AxisServer getAxisServer() {
        if (myAxisServer == null) {
            myAxisServer = new AxisServer();
        }
        return myAxisServer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void run() {
        log.info((Object)Messages.getMessage("start00", "MailServer", this.host + ":" + this.port));
        while (!this.stopped) {
            this.pop3.connect(this.host, this.port);
            this.pop3.login(this.userid, this.password);
            POP3MessageInfo[] messages = this.pop3.listMessages();
            if (messages != null && messages.length > 0) {
                for (int i = 0; i < messages.length; ++i) {
                    int ch;
                    Reader reader = this.pop3.retrieveMessage(messages[i].number);
                    if (reader == null) continue;
                    StringBuffer buffer = new StringBuffer();
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    while ((ch = bufferedReader.read()) != -1) {
                        buffer.append((char)ch);
                    }
                    bufferedReader.close();
                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
                    Properties prop = new Properties();
                    Session session = Session.getDefaultInstance((Properties)prop, null);
                    MimeMessage mimeMsg = new MimeMessage(session, (InputStream)bais);
                    this.pop3.deleteMessage(messages[i].number);
                    if (mimeMsg == null) continue;
                    MailWorker worker = new MailWorker(this, mimeMsg);
                    if (doThreads) {
                        Thread thread = new Thread(worker);
                        thread.setDaemon(true);
                        thread.start();
                        continue;
                    }
                    worker.run();
                }
            }
            Object var14_15 = null;
            try {
                this.pop3.logout();
                this.pop3.disconnect();
                Thread.sleep(3000L);
            }
            catch (Exception e2) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e2);
            }
            continue;
            {
                catch (InterruptedIOException iie) {
                    var14_15 = null;
                    try {
                        this.pop3.logout();
                        this.pop3.disconnect();
                        Thread.sleep(3000L);
                    }
                    catch (Exception e2) {
                        log.error((Object)Messages.getMessage("exception00"), (Throwable)e2);
                    }
                    continue;
                }
                catch (Exception e) {
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    var14_15 = null;
                    try {
                        this.pop3.logout();
                        this.pop3.disconnect();
                        Thread.sleep(3000L);
                    }
                    catch (Exception e2) {
                        log.error((Object)Messages.getMessage("exception00"), (Throwable)e2);
                    }
                    break;
                }
            }
            catch (Throwable throwable) {
                var14_15 = null;
                try {
                    this.pop3.logout();
                    this.pop3.disconnect();
                    Thread.sleep(3000L);
                }
                catch (Exception e2) {
                    log.error((Object)Messages.getMessage("exception00"), (Throwable)e2);
                }
                throw throwable;
            }
        }
        log.info((Object)Messages.getMessage("quit00", "MailServer"));
    }

    public POP3Client getPOP3() {
        return this.pop3;
    }

    public void setPOP3(POP3Client pop3) {
        this.pop3 = pop3;
    }

    public void start(boolean daemon) throws Exception {
        if (doThreads) {
            Thread thread = new Thread(this);
            thread.setDaemon(daemon);
            thread.start();
        } else {
            this.run();
        }
    }

    public void start() throws Exception {
        this.start(false);
    }

    public void stop() throws Exception {
        this.stopped = true;
        log.info((Object)Messages.getMessage("quit00", "MailServer"));
        System.exit(0);
    }

    public static void main(String[] args) {
        Options opts = null;
        try {
            opts = new Options(args);
        }
        catch (MalformedURLException e) {
            log.error((Object)Messages.getMessage("malformedURLException00"), (Throwable)e);
            return;
        }
        try {
            doThreads = opts.isFlagSet('t') > 0;
            String host = opts.getHost();
            int port = opts.isFlagSet('p') > 0 ? opts.getPort() : 110;
            POP3Client pop3 = new POP3Client();
            MailServer sas = new MailServer(host, port, opts.getUser(), opts.getPassword());
            sas.setPOP3(pop3);
            sas.start();
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            return;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

