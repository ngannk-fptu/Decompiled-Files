/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.transport.http;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.http.HttpAdapter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract class HttpAdapterList<T extends HttpAdapter>
extends AbstractList<T>
implements DeploymentDescriptorParser.AdapterFactory<T> {
    private final List<T> adapters = new ArrayList<T>();
    private final Map<PortInfo, String> addressMap = new HashMap<PortInfo, String>();

    @Override
    public T createAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
        T t = this.createHttpAdapter(name, urlPattern, endpoint);
        this.adapters.add(t);
        WSDLPort port = endpoint.getPort();
        if (port != null) {
            PortInfo portInfo = new PortInfo(port.getOwner().getName(), port.getName().getLocalPart(), endpoint.getImplementationClass());
            this.addressMap.put(portInfo, this.getValidPath(urlPattern));
        }
        return t;
    }

    protected abstract T createHttpAdapter(String var1, String var2, WSEndpoint<?> var3);

    private String getValidPath(@NotNull String urlPattern) {
        if (urlPattern.endsWith("/*")) {
            return urlPattern.substring(0, urlPattern.length() - 2);
        }
        return urlPattern;
    }

    public PortAddressResolver createPortAddressResolver(final String baseAddress, final Class<?> endpointImpl) {
        return new PortAddressResolver(){

            @Override
            public String getAddressFor(@NotNull QName serviceName, @NotNull String portName) {
                String urlPattern = (String)HttpAdapterList.this.addressMap.get(new PortInfo(serviceName, portName, endpointImpl));
                if (urlPattern == null) {
                    for (Map.Entry e : HttpAdapterList.this.addressMap.entrySet()) {
                        if (!serviceName.equals(((PortInfo)e.getKey()).serviceName) || !portName.equals(((PortInfo)e.getKey()).portName)) continue;
                        urlPattern = (String)e.getValue();
                        break;
                    }
                }
                return urlPattern == null ? null : baseAddress + urlPattern;
            }
        };
    }

    @Override
    public T get(int index) {
        return (T)((HttpAdapter)this.adapters.get(index));
    }

    @Override
    public int size() {
        return this.adapters.size();
    }

    private static class PortInfo {
        private final QName serviceName;
        private final String portName;
        private final Class<?> implClass;

        PortInfo(@NotNull QName serviceName, @NotNull String portName, Class<?> implClass) {
            this.serviceName = serviceName;
            this.portName = portName;
            this.implClass = implClass;
        }

        public boolean equals(Object portInfo) {
            if (portInfo instanceof PortInfo) {
                PortInfo that = (PortInfo)portInfo;
                if (this.implClass == null) {
                    return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && that.implClass == null;
                }
                return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && this.implClass.equals(that.implClass);
            }
            return false;
        }

        public int hashCode() {
            int retVal = this.serviceName.hashCode() + this.portName.hashCode();
            return this.implClass != null ? retVal + this.implClass.hashCode() : retVal;
        }
    }
}

