/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;

public class AdminClient {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$client$AdminClient == null ? (class$org$apache$axis$client$AdminClient = AdminClient.class$("org.apache.axis.client.AdminClient")) : class$org$apache$axis$client$AdminClient).getName());
    private static ThreadLocal defaultConfiguration = new ThreadLocal();
    protected Call call;
    protected static final String ROOT_UNDEPLOY = WSDDConstants.QNAME_UNDEPLOY.getLocalPart();
    static /* synthetic */ Class class$org$apache$axis$client$AdminClient;

    public static void setDefaultConfiguration(EngineConfiguration config) {
        defaultConfiguration.set(config);
    }

    private static String getUsageInfo() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append(Messages.getMessage("acUsage00")).append(lSep);
        msg.append(Messages.getMessage("acUsage01")).append(lSep);
        msg.append(Messages.getMessage("acUsage02")).append(lSep);
        msg.append(Messages.getMessage("acUsage03")).append(lSep);
        msg.append(Messages.getMessage("acUsage04")).append(lSep);
        msg.append(Messages.getMessage("acUsage05")).append(lSep);
        msg.append(Messages.getMessage("acUsage06")).append(lSep);
        msg.append(Messages.getMessage("acUsage07")).append(lSep);
        msg.append(Messages.getMessage("acUsage08")).append(lSep);
        msg.append(Messages.getMessage("acUsage09")).append(lSep);
        msg.append(Messages.getMessage("acUsage10")).append(lSep);
        msg.append(Messages.getMessage("acUsage11")).append(lSep);
        msg.append(Messages.getMessage("acUsage12")).append(lSep);
        msg.append(Messages.getMessage("acUsage13")).append(lSep);
        msg.append(Messages.getMessage("acUsage14")).append(lSep);
        msg.append(Messages.getMessage("acUsage15")).append(lSep);
        msg.append(Messages.getMessage("acUsage16")).append(lSep);
        msg.append(Messages.getMessage("acUsage17")).append(lSep);
        msg.append(Messages.getMessage("acUsage18")).append(lSep);
        msg.append(Messages.getMessage("acUsage19")).append(lSep);
        msg.append(Messages.getMessage("acUsage20")).append(lSep);
        msg.append(Messages.getMessage("acUsage21")).append(lSep);
        msg.append(Messages.getMessage("acUsage22")).append(lSep);
        msg.append(Messages.getMessage("acUsage23")).append(lSep);
        msg.append(Messages.getMessage("acUsage24")).append(lSep);
        msg.append(Messages.getMessage("acUsage25")).append(lSep);
        msg.append(Messages.getMessage("acUsage26")).append(lSep);
        return msg.toString();
    }

    public AdminClient() {
        try {
            this.initAdminClient();
        }
        catch (ServiceException e) {
            System.err.println(Messages.getMessage("couldntCall00") + ": " + e);
            this.call = null;
        }
    }

    public AdminClient(boolean ignored) throws ServiceException {
        this.initAdminClient();
    }

    private void initAdminClient() throws ServiceException {
        EngineConfiguration config = (EngineConfiguration)defaultConfiguration.get();
        Service service = config != null ? new Service(config) : new Service();
        this.call = (Call)service.createCall();
    }

    public Call getCall() {
        return this.call;
    }

    public String list(Options opts) throws Exception {
        this.processOpts(opts);
        return this.list();
    }

    public String list() throws Exception {
        log.debug((Object)Messages.getMessage("doList00"));
        String str = "<m:list xmlns:m=\"http://xml.apache.org/axis/wsdd/\"/>";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return this.process(input);
    }

    public String quit(Options opts) throws Exception {
        this.processOpts(opts);
        return this.quit();
    }

    public String quit() throws Exception {
        log.debug((Object)Messages.getMessage("doQuit00"));
        String str = "<m:quit xmlns:m=\"http://xml.apache.org/axis/wsdd/\"/>";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return this.process(input);
    }

    public String undeployHandler(String handlerName) throws Exception {
        log.debug((Object)Messages.getMessage("doQuit00"));
        String str = "<m:" + ROOT_UNDEPLOY + " xmlns:m=\"" + "http://xml.apache.org/axis/wsdd/" + "\">" + "<handler name=\"" + handlerName + "\"/>" + "</m:" + ROOT_UNDEPLOY + ">";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return this.process(input);
    }

    public String undeployService(String serviceName) throws Exception {
        log.debug((Object)Messages.getMessage("doQuit00"));
        String str = "<m:" + ROOT_UNDEPLOY + " xmlns:m=\"" + "http://xml.apache.org/axis/wsdd/" + "\">" + "<service name=\"" + serviceName + "\"/>" + "</m:" + ROOT_UNDEPLOY + ">";
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        return this.process(input);
    }

    public String process(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();
        Options opts = new Options(args);
        opts.setDefaultURL("http://localhost:8080/axis/services/AdminService");
        if (opts.isFlagSet('d') > 0) {
            // empty if block
        }
        if ((args = opts.getRemainingArgs()) == null || opts.isFlagSet('?') > 0) {
            System.out.println(Messages.getMessage("usage00", "AdminClient [Options] [list | <deployment-descriptor-files>]"));
            System.out.println("");
            System.out.println(AdminClient.getUsageInfo());
            return null;
        }
        for (int i = 0; i < args.length; ++i) {
            ByteArrayInputStream input = null;
            if (args[i].equals("list")) {
                sb.append(this.list(opts));
                continue;
            }
            if (args[i].equals("quit")) {
                sb.append(this.quit(opts));
                continue;
            }
            if (args[i].equals("passwd")) {
                System.out.println(Messages.getMessage("changePwd00"));
                if (args[i + 1] == null) {
                    System.err.println(Messages.getMessage("needPwd00"));
                    return null;
                }
                String str = "<m:passwd xmlns:m=\"http://xml.apache.org/axis/wsdd/\">";
                str = str + args[i + 1];
                str = str + "</m:passwd>";
                input = new ByteArrayInputStream(str.getBytes());
                ++i;
                sb.append(this.process(opts, input));
                continue;
            }
            if (args[i].indexOf(File.pathSeparatorChar) == -1) {
                System.out.println(Messages.getMessage("processFile00", args[i]));
                sb.append(this.process(opts, args[i]));
                continue;
            }
            StringTokenizer tokenizer = null;
            tokenizer = new StringTokenizer(args[i], File.pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                String file = tokenizer.nextToken();
                System.out.println(Messages.getMessage("processFile00", file));
                sb.append(this.process(opts, file));
                if (!tokenizer.hasMoreTokens()) continue;
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void processOpts(Options opts) throws Exception {
        if (this.call == null) {
            throw new Exception(Messages.getMessage("nullCall00"));
        }
        URL address = new URL(opts.getURL());
        this.setTargetEndpointAddress(address);
        this.setLogin(opts.getUser(), opts.getPassword());
        String tName = opts.isValueSet('t');
        this.setTransport(tName);
    }

    public void setLogin(String user, String password) {
        this.call.setUsername(user);
        this.call.setPassword(password);
    }

    public void setTargetEndpointAddress(URL address) {
        this.call.setTargetEndpointAddress(address);
    }

    public void setTransport(String transportName) {
        if (transportName != null && !transportName.equals("")) {
            this.call.setProperty("transport_name", transportName);
        }
    }

    public String process(InputStream input) throws Exception {
        return this.process(null, input);
    }

    public String process(URL xmlURL) throws Exception {
        return this.process(null, xmlURL.openStream());
    }

    public String process(String xmlFile) throws Exception {
        FileInputStream in = new FileInputStream(xmlFile);
        String result = this.process(null, in);
        return result;
    }

    public String process(Options opts, String xmlFile) throws Exception {
        this.processOpts(opts);
        return this.process(xmlFile);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String process(Options opts, InputStream input) throws Exception {
        try {
            if (this.call == null) {
                throw new Exception(Messages.getMessage("nullCall00"));
            }
            if (opts != null) {
                this.processOpts(opts);
            }
            this.call.setUseSOAPAction(true);
            this.call.setSOAPActionURI("urn:AdminService");
            Vector result = null;
            Object[] params = new Object[]{new SOAPBodyElement(input)};
            result = (Vector)this.call.invoke(params);
            if (result == null || result.isEmpty()) {
                throw new AxisFault(Messages.getMessage("nullResponse00"));
            }
            SOAPBodyElement body = (SOAPBodyElement)result.elementAt(0);
            String string = body.toString();
            return string;
        }
        finally {
            input.close();
        }
    }

    public static void main(String[] args) {
        try {
            AdminClient admin = new AdminClient();
            String result = admin.process(args);
            if (result != null) {
                System.out.println(StringUtils.unescapeNumericChar(result));
            } else {
                System.exit(1);
            }
        }
        catch (AxisFault ae) {
            System.err.println(Messages.getMessage("exception00") + " " + ae.dumpToString());
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println(Messages.getMessage("exception00") + " " + e.getMessage());
            System.exit(1);
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

