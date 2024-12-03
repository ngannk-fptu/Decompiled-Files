/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.privilege;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.spi.PrivilegeDefinition;
import org.apache.jackrabbit.spi.commons.privilege.ParseException;
import org.apache.jackrabbit.spi.commons.privilege.PrivilegeXmlHandler;

public class PrivilegeDefinitionReader {
    private final PrivilegeDefinition[] privilegeDefinitions;
    private final Map<String, String> namespaces = new HashMap<String, String>();

    public PrivilegeDefinitionReader(InputStream in, String contentType) throws ParseException {
        if (!PrivilegeXmlHandler.isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported content type " + contentType);
        }
        PrivilegeXmlHandler pxh = new PrivilegeXmlHandler();
        this.privilegeDefinitions = pxh.readDefinitions(in, this.namespaces);
    }

    public PrivilegeDefinitionReader(Reader reader, String contentType) throws ParseException {
        if (!PrivilegeXmlHandler.isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported content type " + contentType);
        }
        PrivilegeXmlHandler pxh = new PrivilegeXmlHandler();
        this.privilegeDefinitions = pxh.readDefinitions(reader, this.namespaces);
    }

    public PrivilegeDefinition[] getPrivilegeDefinitions() {
        return this.privilegeDefinitions;
    }

    public Map<String, String> getNamespaces() {
        return this.namespaces;
    }
}

