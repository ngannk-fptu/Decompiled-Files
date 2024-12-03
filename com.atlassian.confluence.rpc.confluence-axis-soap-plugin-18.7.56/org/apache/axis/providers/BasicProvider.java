/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers;

import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

public abstract class BasicProvider
extends BasicHandler {
    public static final String OPTION_WSDL_PORTTYPE = "wsdlPortType";
    public static final String OPTION_WSDL_SERVICEELEMENT = "wsdlServiceElement";
    public static final String OPTION_WSDL_SERVICEPORT = "wsdlServicePort";
    public static final String OPTION_WSDL_TARGETNAMESPACE = "wsdlTargetNamespace";
    public static final String OPTION_WSDL_INPUTSCHEMA = "wsdlInputSchema";
    public static final String OPTION_WSDL_SOAPACTION_MODE = "wsdlSoapActionMode";
    public static final String OPTION_EXTRACLASSES = "extraClasses";
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$BasicProvider == null ? (class$org$apache$axis$providers$BasicProvider = BasicProvider.class$("org.apache.axis.providers.BasicProvider")) : class$org$apache$axis$providers$BasicProvider).getName());
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    static /* synthetic */ Class class$org$apache$axis$providers$BasicProvider;

    public abstract void initServiceDesc(SOAPService var1, MessageContext var2) throws AxisFault;

    public void addOperation(String name, QName qname) {
        Hashtable<QName, String> operations = (Hashtable<QName, String>)this.getOption("Operations");
        if (operations == null) {
            operations = new Hashtable<QName, String>();
            this.setOption("Operations", operations);
        }
        operations.put(qname, name);
    }

    public String getOperationName(QName qname) {
        Hashtable operations = (Hashtable)this.getOption("Operations");
        if (operations == null) {
            return null;
        }
        return (String)operations.get(qname);
    }

    public QName[] getOperationQNames() {
        Hashtable operations = (Hashtable)this.getOption("Operations");
        if (operations == null) {
            return null;
        }
        Object[] keys = operations.keySet().toArray();
        QName[] qnames = new QName[keys.length];
        System.arraycopy(keys, 0, qnames, 0, keys.length);
        return qnames;
    }

    public String[] getOperationNames() {
        Hashtable operations = (Hashtable)this.getOption("Operations");
        if (operations == null) {
            return null;
        }
        Object[] values = operations.values().toArray();
        String[] names = new String[values.length];
        System.arraycopy(values, 0, names, 0, values.length);
        return names;
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Enter: BasicProvider::generateWSDL (" + this + ")"));
        }
        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);
        try {
            String targetNamespace;
            String interfaceNamespace;
            String locationUrl = msgContext.getStrProp("axis.wsdlgen.serv.loc.url");
            if (locationUrl == null) {
                locationUrl = serviceDesc.getEndpointURL();
            }
            if (locationUrl == null) {
                locationUrl = msgContext.getStrProp("transport.url");
            }
            if ((interfaceNamespace = msgContext.getStrProp("axis.wsdlgen.intfnamespace")) == null) {
                interfaceNamespace = serviceDesc.getDefaultNamespace();
            }
            if (interfaceNamespace == null) {
                interfaceNamespace = locationUrl;
            }
            Emitter emitter = new Emitter();
            String alias = (String)service.getOption("alias");
            if (alias != null) {
                emitter.setServiceElementName(alias);
            }
            emitter.setStyle(serviceDesc.getStyle());
            emitter.setUse(serviceDesc.getUse());
            if (serviceDesc instanceof JavaServiceDesc) {
                emitter.setClsSmart(((JavaServiceDesc)serviceDesc).getImplClass(), locationUrl);
            }
            if ((targetNamespace = (String)service.getOption(OPTION_WSDL_TARGETNAMESPACE)) == null || targetNamespace.length() == 0) {
                targetNamespace = interfaceNamespace;
            }
            emitter.setIntfNamespace(targetNamespace);
            emitter.setLocationUrl(locationUrl);
            emitter.setServiceDesc(serviceDesc);
            emitter.setTypeMappingRegistry(msgContext.getTypeMappingRegistry());
            String wsdlPortType = (String)service.getOption(OPTION_WSDL_PORTTYPE);
            String wsdlServiceElement = (String)service.getOption(OPTION_WSDL_SERVICEELEMENT);
            String wsdlServicePort = (String)service.getOption(OPTION_WSDL_SERVICEPORT);
            String wsdlInputSchema = (String)service.getOption(OPTION_WSDL_INPUTSCHEMA);
            String wsdlSoapActinMode = (String)service.getOption(OPTION_WSDL_SOAPACTION_MODE);
            String extraClasses = (String)service.getOption(OPTION_EXTRACLASSES);
            if (wsdlPortType != null && wsdlPortType.length() > 0) {
                emitter.setPortTypeName(wsdlPortType);
            }
            if (wsdlServiceElement != null && wsdlServiceElement.length() > 0) {
                emitter.setServiceElementName(wsdlServiceElement);
            }
            if (wsdlServicePort != null && wsdlServicePort.length() > 0) {
                emitter.setServicePortName(wsdlServicePort);
            }
            if (wsdlInputSchema != null && wsdlInputSchema.length() > 0) {
                emitter.setInputSchema(wsdlInputSchema);
            }
            if (wsdlSoapActinMode != null && wsdlSoapActinMode.length() > 0) {
                emitter.setSoapAction(wsdlSoapActinMode);
            }
            if (extraClasses != null && extraClasses.length() > 0) {
                emitter.setExtraClasses(extraClasses);
            }
            if (msgContext.isPropertyTrue("emitAllTypesInWSDL")) {
                emitter.setEmitAllTypes(true);
            }
            Document doc = emitter.emit(0);
            msgContext.setProperty("WSDL", doc);
        }
        catch (NoClassDefFoundError e) {
            entLog.info((Object)Messages.getMessage("toAxisFault00"), (Throwable)e);
            throw new AxisFault(e.toString(), e);
        }
        catch (Exception e) {
            entLog.info((Object)Messages.getMessage("toAxisFault00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Exit: BasicProvider::generateWSDL (" + this + ")"));
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

