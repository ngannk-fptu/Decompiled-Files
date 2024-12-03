/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.analytics.event.serialization;

import com.atlassian.analytics.event.serialization.SchemaProvider;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import org.apache.avro.Schema;

public class DefaultSchemaProvider
implements SchemaProvider {
    private static final String CONFIG_FILENAME = "/event-message-schema-ids.properties";
    private static DefaultSchemaProvider instance;
    private final ImmutableMap<Integer, Schema> idToSchema = DefaultSchemaProvider.loadSchemaIds();

    public static synchronized DefaultSchemaProvider instance() throws IOException {
        if (null == instance) {
            instance = new DefaultSchemaProvider();
        }
        return instance;
    }

    private DefaultSchemaProvider() throws IOException {
    }

    @Override
    public Schema get(int id) {
        if (!this.idToSchema.containsKey((Object)id)) {
            throw new IllegalArgumentException(String.format("Schema id %d is not in mapping file %s.", id, CONFIG_FILENAME));
        }
        return (Schema)this.idToSchema.get((Object)id);
    }

    private static ImmutableMap<Integer, Schema> loadSchemaIds() throws IOException {
        ImmutableMap.Builder idToSchemaBuilder = ImmutableMap.builder();
        Properties mapping = new Properties();
        try (InputStream inputStream = DefaultSchemaProvider.class.getResourceAsStream(CONFIG_FILENAME);){
            mapping.load(inputStream);
        }
        for (String idString : mapping.stringPropertyNames()) {
            if (null == idString) {
                throw new InvalidPropertiesFormatException(String.format("%s contains a property with no name: this is invalid.", CONFIG_FILENAME));
            }
            String schemaFilename = mapping.getProperty(idString);
            if (null == schemaFilename) {
                throw new InvalidPropertiesFormatException(String.format("%s contains a property \"%s\" with no value: this is invalid.", CONFIG_FILENAME, idString));
            }
            Integer id = Integer.valueOf(idString);
            InputStream inputStream = DefaultSchemaProvider.class.getResourceAsStream(schemaFilename);
            Throwable throwable = null;
            try {
                idToSchemaBuilder.put((Object)id, (Object)new Schema.Parser().parse(inputStream));
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (inputStream == null) continue;
                if (throwable != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                    continue;
                }
                inputStream.close();
            }
        }
        return idToSchemaBuilder.build();
    }
}

