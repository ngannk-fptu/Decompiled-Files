/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.http11.AbstractHttp11JsseProtocol
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.lang.reflect.Method;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

public class ConnectorCreateRule
extends Rule {
    private static final Log log = LogFactory.getLog(ConnectorCreateRule.class);
    protected static final StringManager sm = StringManager.getManager(ConnectorCreateRule.class);

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String sslImplementationName;
        Service svc = (Service)this.digester.peek();
        Executor ex = null;
        String executorName = attributes.getValue("executor");
        if (executorName != null) {
            ex = svc.getExecutor(executorName);
        }
        String protocolName = attributes.getValue("protocol");
        Connector con = new Connector(protocolName);
        if (ex != null) {
            ConnectorCreateRule.setExecutor(con, ex);
        }
        if ((sslImplementationName = attributes.getValue("sslImplementationName")) != null) {
            ConnectorCreateRule.setSSLImplementationName(con, sslImplementationName);
        }
        this.digester.push((Object)con);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(Connector.class.getName()).append(' ').append(this.digester.toVariableName((Object)con));
            code.append(" = new ").append(Connector.class.getName());
            code.append("(new ").append(con.getProtocolHandlerClassName()).append("());");
            code.append(System.lineSeparator());
            if (ex != null) {
                code.append(this.digester.toVariableName((Object)con)).append(".getProtocolHandler().setExecutor(");
                code.append(this.digester.toVariableName((Object)svc)).append(".getExecutor(").append(executorName);
                code.append("));");
                code.append(System.lineSeparator());
            }
            if (sslImplementationName != null) {
                code.append("((").append(AbstractHttp11JsseProtocol.class.getName()).append("<?>) ");
                code.append(this.digester.toVariableName((Object)con)).append(".getProtocolHandler()).setSslImplementationName(\"");
                code.append(sslImplementationName).append("\");");
                code.append(System.lineSeparator());
            }
        }
    }

    private static void setExecutor(Connector con, Executor ex) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), (String)"setExecutor", (Class[])new Class[]{java.util.concurrent.Executor.class});
        if (m != null) {
            m.invoke((Object)con.getProtocolHandler(), ex);
        } else {
            log.warn((Object)sm.getString("connector.noSetExecutor", new Object[]{con}));
        }
    }

    private static void setSSLImplementationName(Connector con, String sslImplementationName) throws Exception {
        Method m = IntrospectionUtils.findMethod(con.getProtocolHandler().getClass(), (String)"setSslImplementationName", (Class[])new Class[]{String.class});
        if (m != null) {
            m.invoke((Object)con.getProtocolHandler(), sslImplementationName);
        } else {
            log.warn((Object)sm.getString("connector.noSetSSLImplementationName", new Object[]{con}));
        }
    }

    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}

