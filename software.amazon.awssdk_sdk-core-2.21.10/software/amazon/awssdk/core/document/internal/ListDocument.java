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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.DocumentVisitor;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.core.document.internal.MapDocument;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public final class ListDocument
implements Document {
    private static final long serialVersionUID = 1L;
    private final List<Document> value;

    public ListDocument(List<Document> documentList) {
        Validate.notNull(documentList, (String)"List documentList cannot be null", (Object[])new Object[0]);
        this.value = Collections.unmodifiableList(documentList);
    }

    public static Document.ListBuilder listBuilder() {
        return new ListBuilderInternal();
    }

    @Override
    public Object unwrap() {
        return this.value.stream().map(Document::unwrap).collect(Collectors.toList());
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A Document List cannot be converted to a Boolean.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A Document List cannot be converted to a String.");
    }

    @Override
    public SdkNumber asNumber() {
        throw new UnsupportedOperationException("A Document List cannot be converted to a Number.");
    }

    @Override
    public Map<String, Document> asMap() {
        throw new UnsupportedOperationException("A Document List cannot be converted to a Map.");
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public List<Document> asList() {
        return this.value;
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitList(this.asList());
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitList(Collections.unmodifiableList(this.asList()));
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListDocument)) {
            return false;
        }
        ListDocument that = (ListDocument)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public static class ListBuilderInternal
    implements Document.ListBuilder {
        private final List<Document> documentList = new ArrayList<Document>();

        @Override
        public Document.ListBuilder addString(String stringValue) {
            this.documentList.add(Document.fromString(stringValue));
            return this;
        }

        @Override
        public Document.ListBuilder addBoolean(boolean booleanValue) {
            this.documentList.add(Document.fromBoolean(booleanValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(SdkNumber numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(int numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(long numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(float numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(double numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(BigDecimal numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(BigInteger numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addNumber(String numberValue) {
            this.documentList.add(Document.fromNumber(numberValue));
            return this;
        }

        @Override
        public Document.ListBuilder addDocument(Document document) {
            this.documentList.add(document);
            return this;
        }

        @Override
        public Document.ListBuilder addMap(Consumer<Document.MapBuilder> mapBuilderConsumer) {
            Document.MapBuilder mapBuilder = MapDocument.mapBuilder();
            mapBuilderConsumer.accept(mapBuilder);
            this.documentList.add(mapBuilder.build());
            return this;
        }

        @Override
        public Document.ListBuilder addNull() {
            this.documentList.add(Document.fromNull());
            return this;
        }

        @Override
        public Document build() {
            return new ListDocument(this.documentList);
        }
    }
}

