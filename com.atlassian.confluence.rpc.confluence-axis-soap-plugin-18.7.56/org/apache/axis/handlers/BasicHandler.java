/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.LockableHashtable;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BasicHandler
implements Handler {
    private static Log log = LogFactory.getLog((class$org$apache$axis$handlers$BasicHandler == null ? (class$org$apache$axis$handlers$BasicHandler = BasicHandler.class$("org.apache.axis.handlers.BasicHandler")) : class$org$apache$axis$handlers$BasicHandler).getName());
    protected boolean makeLockable = false;
    protected Hashtable options;
    protected String name;
    static /* synthetic */ Class class$org$apache$axis$handlers$BasicHandler;

    protected void setOptionsLockable(boolean makeLockable) {
        this.makeLockable = makeLockable;
    }

    protected void initHashtable() {
        this.options = this.makeLockable ? new LockableHashtable() : new Hashtable();
    }

    public void init() {
    }

    public void cleanup() {
    }

    public boolean canHandleBlock(QName qname) {
        return false;
    }

    public void onFault(MessageContext msgContext) {
    }

    public void setOption(String name, Object value) {
        if (this.options == null) {
            this.initHashtable();
        }
        this.options.put(name, value);
    }

    public boolean setOptionDefault(String name, Object value) {
        boolean val;
        boolean bl = val = (this.options == null || this.options.get(name) == null) && value != null;
        if (val) {
            this.setOption(name, value);
        }
        return val;
    }

    public Object getOption(String name) {
        if (this.options == null) {
            return null;
        }
        return this.options.get(name);
    }

    public Hashtable getOptions() {
        return this.options;
    }

    public void setOptions(Hashtable opts) {
        this.options = opts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Element getDeploymentData(Document doc) {
        log.debug((Object)"Enter: BasicHandler::getDeploymentData");
        Element root = doc.createElementNS("", "handler");
        root.setAttribute("class", this.getClass().getName());
        this.options = this.getOptions();
        if (this.options != null) {
            Enumeration e = this.options.keys();
            while (e.hasMoreElements()) {
                String k = (String)e.nextElement();
                Object v = this.options.get(k);
                Element e1 = doc.createElementNS("", "option");
                e1.setAttribute("name", k);
                e1.setAttribute("value", v.toString());
                root.appendChild(e1);
            }
        }
        log.debug((Object)"Exit: BasicHandler::getDeploymentData");
        return root;
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
    }

    public List getUnderstoodHeaders() {
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

