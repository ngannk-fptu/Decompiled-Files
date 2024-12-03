/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkNumber
 *  software.amazon.awssdk.core.document.Document
 *  software.amazon.awssdk.core.document.VoidDocumentVisitor
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

    public void visitNull() {
        this.jsonGenerator.writeNull();
    }

    public void visitBoolean(Boolean document) {
        this.jsonGenerator.writeValue(document);
    }

    public void visitString(String document) {
        this.jsonGenerator.writeValue(document);
    }

    public void visitNumber(SdkNumber document) {
        this.jsonGenerator.writeNumber(document.stringValue());
    }

    public void visitMap(Map<String, Document> documentMap) {
        this.jsonGenerator.writeStartObject();
        documentMap.entrySet().forEach(entry -> {
            this.jsonGenerator.writeFieldName((String)entry.getKey());
            ((Document)entry.getValue()).accept((VoidDocumentVisitor)this);
        });
        this.jsonGenerator.writeEndObject();
    }

    public void visitList(List<Document> documentList) {
        this.jsonGenerator.writeStartArray();
        documentList.stream().forEach(document -> document.accept((VoidDocumentVisitor)this));
        this.jsonGenerator.writeEndArray();
    }
}

