/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.type.MapType
 */
package org.apache.commons.configuration2;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.configuration2.AbstractYAMLBasedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.InputStreamSupport;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class JSONConfiguration
extends AbstractYAMLBasedConfiguration
implements FileBasedConfiguration,
InputStreamSupport {
    private final ObjectMapper mapper = new ObjectMapper();
    private final MapType type = this.mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

    public JSONConfiguration() {
    }

    public JSONConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }

    @Override
    public void read(Reader in) throws ConfigurationException {
        try {
            this.load((Map)this.mapper.readValue(in, (JavaType)this.type));
        }
        catch (Exception e) {
            JSONConfiguration.rethrowException(e);
        }
    }

    @Override
    public void write(Writer out) throws ConfigurationException, IOException {
        this.mapper.writer().writeValue(out, this.constructMap(this.getNodeModel().getNodeHandler().getRootNode()));
    }

    @Override
    public void read(InputStream in) throws ConfigurationException {
        try {
            this.load((Map)this.mapper.readValue(in, (JavaType)this.type));
        }
        catch (Exception e) {
            JSONConfiguration.rethrowException(e);
        }
    }
}

