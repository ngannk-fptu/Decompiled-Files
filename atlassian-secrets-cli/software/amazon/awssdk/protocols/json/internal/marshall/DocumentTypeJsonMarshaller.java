/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;

@SdkInternalApi
public class DocumentTypeJsonMarshaller
implements VoidDocumentVisitor {
    private final StructuredJsonGenerator jsonGenerator;

    public DocumentTypeJsonMarshaller(StructuredJsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
    }

    @Override
    public void visitNull() {
        this.jsonGenerator.writeNull();
    }

    @Override
    public void visitBoolean(Boolean document) {
        this.jsonGenerator.writeValue(document);
    }

    @Override
    public void visitString(String document) {
        this.jsonGenerator.writeValue(document);
    }

    @Override
    public void visitNumber(SdkNumber document) {
        this.jsonGenerator.writeNumber(document.stringValue());
    }

    @Override
    public void visitMap(Map<String, Document> documentMap) {
        this.jsonGenerator.writeStartObject();
        documentMap.entrySet().forEach(entry -> {
            this.jsonGenerator.writeFieldName((String)entry.getKey());
            ((Document)entry.getValue()).accept(this);
        });
        this.jsonGenerator.writeEndObject();
    }

    @Override
    public void visitList(List<Document> documentList) {
        this.jsonGenerator.writeStartArray();
        documentList.stream().forEach(document -> document.accept(this));
        this.jsonGenerator.writeEndArray();
    }
}

