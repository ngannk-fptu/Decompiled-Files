/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.document.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.DocumentVisitor;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.core.document.internal.ListDocument;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public final class MapDocument
implements Document {
    private static final long serialVersionUID = 1L;
    private final Map<String, Document> value;

    public MapDocument(Map<String, Document> documentMap) {
        Validate.notNull(documentMap, (String)"Map cannot be null", (Object[])new Object[0]);
        this.value = Collections.unmodifiableMap(documentMap);
    }

    public static Document.MapBuilder mapBuilder() {
        return new MapBuilderInternal();
    }

    @Override
    public Object unwrap() {
        LinkedHashMap unwrappedMap = new LinkedHashMap();
        this.value.entrySet().forEach(mapEntry -> unwrappedMap.put(mapEntry.getKey(), ((Document)mapEntry.getValue()).unwrap()));
        return Collections.unmodifiableMap(unwrappedMap);
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A Document Map cannot be converted to a Boolean.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A Document Map cannot be converted to a String.");
    }

    @Override
    public SdkNumber asNumber() {
        throw new UnsupportedOperationException("A Document Map cannot be converted to a Number.");
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public Map<String, Document> asMap() {
        return this.value;
    }

    @Override
    public List<Document> asList() {
        throw new UnsupportedOperationException("A Document Map cannot be converted to a List.");
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitMap(Collections.unmodifiableMap(this.asMap()));
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitMap(this.asMap());
    }

    public String toString() {
        if (this.value.isEmpty()) {
            return "{}";
        }
        StringBuilder output = new StringBuilder();
        output.append("{");
        this.value.forEach((k, v) -> output.append("\"").append((String)k).append("\": ").append(v.toString()).append(","));
        output.setCharAt(output.length() - 1, '}');
        return output.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapDocument)) {
            return false;
        }
        MapDocument that = (MapDocument)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public static class MapBuilderInternal
    implements Document.MapBuilder {
        private final Map<String, Document> documentMap = new LinkedHashMap<String, Document>();

        @Override
        public Document.MapBuilder putString(String key, String stringValue) {
            this.documentMap.put(key, Document.fromString(stringValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, SdkNumber numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, int numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, long numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, double numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, float numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, BigDecimal numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, BigInteger numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putNumber(String key, String numberValue) {
            this.documentMap.put(key, Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.MapBuilder putBoolean(String key, boolean booleanValue) {
            this.documentMap.put(key, Document.fromBoolean(booleanValue));
            return this;
        }

        @Override
        public Document.MapBuilder putDocument(String key, Document document) {
            Validate.notNull((Object)document, (String)"Document cannot be null", (Object[])new Object[0]);
            this.documentMap.put(key, document);
            return this;
        }

        @Override
        public Document.MapBuilder putNull(String key) {
            this.documentMap.put(key, Document.fromNull());
            return this;
        }

        @Override
        public Document.MapBuilder putList(String key, List<Document> documentList) {
            this.documentMap.put(key, Document.fromList(documentList));
            return this;
        }

        @Override
        public Document.MapBuilder putList(String key, Consumer<Document.ListBuilder> listBuilderConsumer) {
            Document.ListBuilder listBuilder = ListDocument.listBuilder();
            listBuilderConsumer.accept(listBuilder);
            this.documentMap.put(key, listBuilder.build());
            return this;
        }

        @Override
        public Document.MapBuilder putMap(String key, Map<String, Document> documentMap) {
            Validate.notNull(documentMap, (String)"documentMap cannot be null", (Object[])new Object[0]);
            this.documentMap.put(key, Document.fromMap(documentMap));
            return this;
        }

        @Override
        public Document.MapBuilder putMap(String key, Consumer<Document.MapBuilder> mapBuilderConsumer) {
            Document.MapBuilder mapBuilder = MapDocument.mapBuilder();
            mapBuilderConsumer.accept(mapBuilder);
            this.documentMap.put(key, mapBuilder.build());
            return this;
        }

        @Override
        public Document build() {
            return new MapDocument(this.documentMap);
        }
    }
}

