/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.document.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.DocumentVisitor;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public class NumberDocument
implements Document {
    private static final long serialVersionUID = 1L;
    private final SdkNumber number;

    public NumberDocument(SdkNumber number) {
        Validate.notNull(number, "Number cannot be null.", new Object[0]);
        this.number = number;
    }

    @Override
    public Object unwrap() {
        return this.number.stringValue();
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A Document Number cannot be converted to a Boolean.");
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("A Document Number cannot be converted to a String.");
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public SdkNumber asNumber() {
        return this.number;
    }

    @Override
    public Map<String, Document> asMap() {
        throw new UnsupportedOperationException("A Document Number cannot be converted to a Map.");
    }

    @Override
    public List<Document> asList() {
        throw new UnsupportedOperationException("A Document Number cannot be converted to a List.");
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitNumber(this.asNumber());
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitNumber(this.asNumber());
    }

    public String toString() {
        return String.valueOf(this.number);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NumberDocument)) {
            return false;
        }
        NumberDocument that = (NumberDocument)o;
        return Objects.equals(this.number, that.number);
    }

    public int hashCode() {
        return Objects.hashCode(this.number);
    }
}

