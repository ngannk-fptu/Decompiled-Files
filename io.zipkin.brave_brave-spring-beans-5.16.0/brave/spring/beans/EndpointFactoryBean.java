/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  zipkin2.Endpoint
 *  zipkin2.Endpoint$Builder
 */
package brave.spring.beans;

import org.springframework.beans.factory.FactoryBean;
import zipkin2.Endpoint;

public class EndpointFactoryBean
implements FactoryBean {
    String serviceName;
    String ip;
    Integer port;

    public Endpoint getObject() {
        Endpoint.Builder builder = Endpoint.newBuilder();
        if (this.serviceName != null) {
            builder.serviceName(this.serviceName);
        }
        if (this.ip != null && !builder.parseIp(this.ip)) {
            throw new IllegalArgumentException("endpoint.ip: " + this.ip + " is not an IP literal");
        }
        if (this.port != null) {
            builder.port(this.port);
        }
        return builder.build();
    }

    public Class<? extends Endpoint> getObjectType() {
        return Endpoint.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}

