/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.spi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.spi.Provider;

public class Service
extends ProjectComponent {
    private List<Provider> providerList = new ArrayList<Provider>();
    private String type;

    public void setProvider(String className) {
        Provider provider = new Provider();
        provider.setClassName(className);
        this.providerList.add(provider);
    }

    public void addConfiguredProvider(Provider provider) {
        provider.check();
        this.providerList.add(provider);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InputStream getAsStream() throws IOException {
        return new ByteArrayInputStream(this.providerList.stream().map(Provider::getClassName).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
    }

    public void check() {
        if (this.type == null) {
            throw new BuildException("type attribute must be set for service element", this.getLocation());
        }
        if (this.type.isEmpty()) {
            throw new BuildException("Invalid empty type classname", this.getLocation());
        }
        if (this.providerList.isEmpty()) {
            throw new BuildException("provider attribute or nested provider element must be set!", this.getLocation());
        }
    }
}

