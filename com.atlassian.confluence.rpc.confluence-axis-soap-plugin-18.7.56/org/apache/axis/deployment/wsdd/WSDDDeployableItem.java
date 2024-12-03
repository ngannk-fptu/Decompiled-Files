/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public abstract class WSDDDeployableItem
extends WSDDElement {
    public static final int SCOPE_PER_ACCESS = 0;
    public static final int SCOPE_PER_REQUEST = 1;
    public static final int SCOPE_SINGLETON = 2;
    public static String[] scopeStrings = new String[]{"per-access", "per-request", "singleton"};
    protected static Log log = LogFactory.getLog((class$org$apache$axis$deployment$wsdd$WSDDDeployableItem == null ? (class$org$apache$axis$deployment$wsdd$WSDDDeployableItem = WSDDDeployableItem.class$("org.apache.axis.deployment.wsdd.WSDDDeployableItem")) : class$org$apache$axis$deployment$wsdd$WSDDDeployableItem).getName());
    protected LockableHashtable parameters;
    protected QName qname;
    protected QName type;
    protected int scope = 2;
    protected Handler singletonInstance = null;
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDDeployableItem;

    public WSDDDeployableItem() {
    }

    public WSDDDeployableItem(Element e) throws WSDDException {
        super(e);
        String scopeStr;
        String typeStr;
        String name = e.getAttribute("name");
        if (name != null && !name.equals("")) {
            this.qname = new QName("", name);
        }
        if ((typeStr = e.getAttribute("type")) != null && !typeStr.equals("")) {
            this.type = XMLUtils.getQNameFromString(typeStr, e);
        }
        if ((scopeStr = e.getAttribute("scope")) != null) {
            for (int i = 0; i < scopeStrings.length; ++i) {
                if (!scopeStr.equals(scopeStrings[i])) continue;
                this.scope = i;
                break;
            }
        }
        this.parameters = new LockableHashtable();
        Element[] paramElements = this.getChildElements(e, "parameter");
        for (int i = 0; i < paramElements.length; ++i) {
            Element param = paramElements[i];
            String pname = param.getAttribute("name");
            String value = param.getAttribute("value");
            String locked = param.getAttribute("locked");
            this.parameters.put(pname, value, JavaUtils.isTrueExplicitly(locked));
        }
    }

    public void setName(String name) {
        this.qname = new QName(null, name);
    }

    public void setQName(QName qname) {
        this.qname = qname;
    }

    public QName getQName() {
        return this.qname;
    }

    public QName getType() {
        return this.type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    public void setParameter(String name, String value) {
        if (this.parameters == null) {
            this.parameters = new LockableHashtable();
        }
        this.parameters.put(name, value);
    }

    public String getParameter(String name) {
        if (name == null || this.parameters == null) {
            return null;
        }
        return (String)this.parameters.get(name);
    }

    public LockableHashtable getParametersTable() {
        return this.parameters;
    }

    public void setOptionsHashtable(Hashtable hashtable) {
        if (hashtable == null) {
            return;
        }
        this.parameters = new LockableHashtable((Map)hashtable);
    }

    public void writeParamsToContext(SerializationContext context) throws IOException {
        if (this.parameters == null) {
            return;
        }
        Set entries = this.parameters.entrySet();
        Iterator i = entries.iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            String name = (String)entry.getKey();
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "name", "name", "CDATA", name);
            attrs.addAttribute("", "value", "value", "CDATA", entry.getValue().toString());
            if (this.parameters.isKeyLocked(name)) {
                attrs.addAttribute("", "locked", "locked", "CDATA", "true");
            }
            context.startElement(QNAME_PARAM, attrs);
            context.endElement();
        }
    }

    public void removeParameter(String name) {
        if (this.parameters != null) {
            this.parameters.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Handler getInstance(EngineConfiguration registry) throws ConfigurationException {
        if (this.scope == 2) {
            WSDDDeployableItem wSDDDeployableItem = this;
            synchronized (wSDDDeployableItem) {
                if (this.singletonInstance == null) {
                    this.singletonInstance = this.getNewInstance(registry);
                }
            }
            return this.singletonInstance;
        }
        return this.getNewInstance(registry);
    }

    private Handler getNewInstance(EngineConfiguration registry) throws ConfigurationException {
        QName type = this.getType();
        if (type == null || "http://xml.apache.org/axis/wsdd/providers/java".equals(type.getNamespaceURI())) {
            return this.makeNewInstance(registry);
        }
        return registry.getHandler(type);
    }

    protected Handler makeNewInstance(EngineConfiguration registry) throws ConfigurationException {
        Class c = null;
        Handler h = null;
        try {
            c = this.getJavaClass();
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationException(e);
        }
        if (c != null) {
            try {
                h = (Handler)this.createInstance(c);
            }
            catch (Exception e) {
                throw new ConfigurationException(e);
            }
            if (h != null) {
                if (this.qname != null) {
                    h.setName(this.qname.getLocalPart());
                }
                h.setOptions(this.getParametersTable());
                try {
                    h.init();
                }
                catch (Exception e) {
                    String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                    log.debug((Object)msg);
                    throw new ConfigurationException(e);
                }
                catch (Error e) {
                    String msg = e + JavaUtils.LS + JavaUtils.stackToString(e);
                    log.debug((Object)msg);
                    throw new ConfigurationException(msg);
                }
            }
        } else {
            h = registry.getHandler(this.getType());
        }
        return h;
    }

    Object createInstance(Class _class) throws InstantiationException, IllegalAccessException {
        return _class.newInstance();
    }

    public Class getJavaClass() throws ClassNotFoundException {
        QName type = this.getType();
        if (type != null && "http://xml.apache.org/axis/wsdd/providers/java".equals(type.getNamespaceURI())) {
            return ClassUtils.forName(type.getLocalPart());
        }
        return null;
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

