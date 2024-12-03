/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class HandlerInfo
implements Serializable {
    private Class handlerClass;
    private Map config;
    private QName[] headers;

    public HandlerInfo() {
        this.handlerClass = null;
        this.config = new HashMap();
    }

    public HandlerInfo(Class handlerClass, Map config, QName[] headers) {
        this.handlerClass = handlerClass;
        this.config = config;
        this.headers = headers;
    }

    public void setHandlerClass(Class handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class getHandlerClass() {
        return this.handlerClass;
    }

    public void setHandlerConfig(Map config) {
        this.config = config;
    }

    public Map getHandlerConfig() {
        return this.config;
    }

    public void setHeaders(QName[] headers) {
        this.headers = headers;
    }

    public QName[] getHeaders() {
        return this.headers;
    }
}

