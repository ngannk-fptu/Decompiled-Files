/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.privilege;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.apache.jackrabbit.spi.PrivilegeDefinition;
import org.apache.jackrabbit.spi.commons.privilege.PrivilegeHandler;
import org.apache.jackrabbit.spi.commons.privilege.PrivilegeXmlHandler;

public class PrivilegeDefinitionWriter {
    private final PrivilegeHandler ph;

    public PrivilegeDefinitionWriter(String contentType) {
        if (!PrivilegeXmlHandler.isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported content type");
        }
        this.ph = new PrivilegeXmlHandler();
    }

    public void writeDefinitions(OutputStream out, PrivilegeDefinition[] privilegeDefinitions, Map<String, String> namespaces) throws IOException {
        this.ph.writeDefinitions(out, privilegeDefinitions, namespaces);
    }

    public void writeDefinitions(Writer writer, PrivilegeDefinition[] privilegeDefinitions, Map<String, String> namespaces) throws IOException {
        this.ph.writeDefinitions(writer, privilegeDefinitions, namespaces);
    }
}

