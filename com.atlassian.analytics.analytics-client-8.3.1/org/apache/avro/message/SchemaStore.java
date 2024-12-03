/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;

public interface SchemaStore {
    public Schema findByFingerprint(long var1);

    public static class Cache
    implements SchemaStore {
        private final Map<Long, Schema> schemas = new ConcurrentHashMap<Long, Schema>();

        public void addSchema(Schema schema) {
            long fp = SchemaNormalization.parsingFingerprint64(schema);
            this.schemas.put(fp, schema);
        }

        @Override
        public Schema findByFingerprint(long fingerprint) {
            return this.schemas.get(fingerprint);
        }
    }
}

