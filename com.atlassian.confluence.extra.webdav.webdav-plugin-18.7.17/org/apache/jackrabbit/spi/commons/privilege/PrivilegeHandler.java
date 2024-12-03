/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.privilege;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.apache.jackrabbit.spi.PrivilegeDefinition;
import org.apache.jackrabbit.spi.commons.privilege.ParseException;

public interface PrivilegeHandler {
    public PrivilegeDefinition[] readDefinitions(InputStream var1, Map<String, String> var2) throws ParseException;

    public PrivilegeDefinition[] readDefinitions(Reader var1, Map<String, String> var2) throws ParseException;

    public void writeDefinitions(OutputStream var1, PrivilegeDefinition[] var2, Map<String, String> var3) throws IOException;

    public void writeDefinitions(Writer var1, PrivilegeDefinition[] var2, Map<String, String> var3) throws IOException;
}

