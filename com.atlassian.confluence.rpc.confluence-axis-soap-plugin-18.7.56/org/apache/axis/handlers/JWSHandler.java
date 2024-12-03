/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.compiler.Compiler;
import org.apache.axis.components.compiler.CompilerError;
import org.apache.axis.components.compiler.CompilerFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.ClasspathUtils;
import org.apache.axis.utils.JWSClassLoader;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JWSHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$JWSHandler == null ? (class$org$apache$axis$handlers$JWSHandler = JWSHandler.class$("org.apache.axis.handlers.JWSHandler")) : class$org$apache$axis$handlers$JWSHandler).getName());
    public final String OPTION_JWS_FILE_EXTENSION = "extension";
    public final String DEFAULT_JWS_FILE_EXTENSION = ".jws";
    protected static HashMap soapServices = new HashMap();
    static /* synthetic */ Class class$org$apache$axis$handlers$JWSHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JWSHandler::invoke");
        }
        try {
            this.setupService(msgContext);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
    }

    protected void setupService(MessageContext msgContext) throws Exception {
        String realpath = msgContext.getStrProp("realpath");
        String extension = (String)this.getOption("extension");
        if (extension == null) {
            extension = ".jws";
        }
        if (realpath != null && realpath.endsWith(extension)) {
            ClassLoader cl;
            File outDirectory;
            String jwsFile = realpath;
            String rel = msgContext.getStrProp("path");
            File f2 = new File(jwsFile);
            if (!f2.exists()) {
                throw new FileNotFoundException(rel);
            }
            if (rel.charAt(0) == '/') {
                rel = rel.substring(1);
            }
            int lastSlash = rel.lastIndexOf(47);
            String dir = null;
            if (lastSlash > 0) {
                dir = rel.substring(0, lastSlash);
            }
            String file = rel.substring(lastSlash + 1);
            String outdir = msgContext.getStrProp("jws.classDir");
            if (outdir == null) {
                outdir = ".";
            }
            if (dir != null) {
                outdir = outdir + File.separator + dir;
            }
            if (!(outDirectory = new File(outdir)).exists()) {
                outDirectory.mkdirs();
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("jwsFile: " + jwsFile));
            }
            String jFile = outdir + File.separator + file.substring(0, file.length() - extension.length() + 1) + "java";
            String cFile = outdir + File.separator + file.substring(0, file.length() - extension.length() + 1) + "class";
            if (log.isDebugEnabled()) {
                log.debug((Object)("jFile: " + jFile));
                log.debug((Object)("cFile: " + cFile));
                log.debug((Object)("outdir: " + outdir));
            }
            File f1 = new File(cFile);
            String clsName = null;
            if (clsName == null) {
                clsName = f2.getName();
            }
            if (clsName != null && clsName.charAt(0) == '/') {
                clsName = clsName.substring(1);
            }
            clsName = clsName.substring(0, clsName.length() - extension.length());
            clsName = clsName.replace('/', '.');
            if (log.isDebugEnabled()) {
                log.debug((Object)("ClsName: " + clsName));
            }
            if (!f1.exists() || f2.lastModified() > f1.lastModified()) {
                int rc;
                log.debug((Object)Messages.getMessage("compiling00", jwsFile));
                log.debug((Object)Messages.getMessage("copy00", jwsFile, jFile));
                FileReader fr = new FileReader(jwsFile);
                FileWriter fw = new FileWriter(jFile);
                char[] buf = new char[4096];
                while ((rc = fr.read(buf, 0, 4095)) >= 0) {
                    fw.write(buf, 0, rc);
                }
                fw.close();
                fr.close();
                log.debug((Object)("javac " + jFile));
                Compiler compiler = CompilerFactory.getCompiler();
                compiler.setClasspath(ClasspathUtils.getDefaultClasspath(msgContext));
                compiler.setDestination(outdir);
                compiler.addFile(jFile);
                boolean result = compiler.compile();
                new File(jFile).delete();
                if (!result) {
                    new File(cFile).delete();
                    Document doc = XMLUtils.newDocument();
                    Element root = doc.createElementNS("", "Errors");
                    StringBuffer message = new StringBuffer("Error compiling ");
                    message.append(jFile);
                    message.append(":\n");
                    List errors = compiler.getErrors();
                    int count = errors.size();
                    for (int i = 0; i < count; ++i) {
                        CompilerError error = (CompilerError)errors.get(i);
                        if (i > 0) {
                            message.append("\n");
                        }
                        message.append("Line ");
                        message.append(error.getStartLine());
                        message.append(", column ");
                        message.append(error.getStartColumn());
                        message.append(": ");
                        message.append(error.getMessage());
                    }
                    root.appendChild(doc.createTextNode(message.toString()));
                    throw new AxisFault("Server.compileError", Messages.getMessage("badCompile00", jFile), null, new Element[]{root});
                }
                ClassUtils.removeClassLoader(clsName);
                soapServices.remove(clsName);
            }
            if ((cl = ClassUtils.getClassLoader(clsName)) == null) {
                cl = new JWSClassLoader(clsName, msgContext.getClassLoader(), cFile);
            }
            msgContext.setClassLoader(cl);
            SOAPService rpc = (SOAPService)soapServices.get(clsName);
            if (rpc == null) {
                rpc = new SOAPService(new RPCProvider());
                rpc.setName(clsName);
                rpc.setOption("className", clsName);
                rpc.setEngine(msgContext.getAxisEngine());
                String allowed = (String)this.getOption("allowedMethods");
                if (allowed == null) {
                    allowed = "*";
                }
                rpc.setOption("allowedMethods", allowed);
                String scope = (String)this.getOption("scope");
                if (scope == null) {
                    scope = Scope.DEFAULT.getName();
                }
                rpc.setOption("scope", scope);
                rpc.getInitializedServiceDesc(msgContext);
                soapServices.put(clsName, rpc);
            }
            rpc.setEngine(msgContext.getAxisEngine());
            rpc.init();
            msgContext.setService(rpc);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JWSHandler::invoke");
        }
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            this.setupService(msgContext);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
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

