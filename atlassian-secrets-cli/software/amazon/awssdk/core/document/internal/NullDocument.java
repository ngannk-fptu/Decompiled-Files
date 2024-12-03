/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.document.internal;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.DocumentVisitor;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;

@SdkInternalApi
@Immutable
public final class NullDocument
implements Document {
    private static final long serialVersionUID = 1L;

    @Override
    public Object unwrap() {
        return null;
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A Document Null cannot be converted to a Boolean.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A Document Null cannot be converted to a String.");
    }

    @Override
    public SdkNumber asNumber() {
        throw new UnsupportedOperationException("A Document Null cannot be converted to a Number.");
    }

    @Override
    public Map<String, Document> asMap() {
        throw new UnsupportedOperationException("A Document Null cannot be converted to a Map.");
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public List<Document> asList() {
        throw new UnsupportedOperationException("A Document Null cannot be converted to a List.");
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitNull();
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitNull();
    }

    public String toString() {
        return "null";
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NullDocument)) {
            return false;
        }
        NullDocument that = (NullDocument)obj;
        return that.isNull() == this.isNull();
    }
}

