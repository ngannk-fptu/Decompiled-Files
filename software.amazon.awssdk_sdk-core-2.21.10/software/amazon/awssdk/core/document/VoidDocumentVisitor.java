/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.document;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;

@SdkPublicApi
public interface VoidDocumentVisitor {
    default public void visitNull() {
    }

    default public void visitBoolean(Boolean document) {
    }

    default public void visitString(String document) {
    }

    default public void visitNumber(SdkNumber document) {
    }

    default public void visitMap(Map<String, Document> documentMap) {
    }

    default public void visitList(List<Document> documentList) {
    }
}

