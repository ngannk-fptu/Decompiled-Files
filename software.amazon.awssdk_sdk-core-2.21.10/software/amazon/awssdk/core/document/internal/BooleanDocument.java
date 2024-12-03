/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
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
public final class BooleanDocument
implements Document {
    private static final long serialVersionUID = 1L;
    private final boolean value;

    public BooleanDocument(boolean value) {
        this.value = value;
    }

    @Override
    public Object unwrap() {
        return this.value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        return this.value;
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A Document Boolean cannot be converted to a String.");
    }

    @Override
    public SdkNumber asNumber() {
        throw new UnsupportedOperationException("A Document Boolean cannot be converted to a Number.");
    }

    @Override
    public Map<String, Document> asMap() {
        throw new UnsupportedOperationException("A Document Boolean cannot be converted to a Map.");
    }

    @Override
    public List<Document> asList() {
        throw new UnsupportedOperationException("A Document Boolean cannot be converted to a List.");
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitBoolean(this.asBoolean());
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitBoolean(this.asBoolean());
    }

    public String toString() {
        return Boolean.toString(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanDocument)) {
            return false;
        }
        BooleanDocument that = (BooleanDocument)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return Boolean.hashCode(this.value);
    }
}

