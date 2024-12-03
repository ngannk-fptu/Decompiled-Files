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
public final class StringDocument
implements Document {
    private static final long serialVersionUID = 1L;
    private final String value;

    public StringDocument(String string) {
        Validate.notNull(string, "String cannot be null", new Object[0]);
        this.value = string;
    }

    @Override
    public Object unwrap() {
        return this.value;
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("A Document String cannot be converted to a Boolean.");
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public SdkNumber asNumber() {
        throw new UnsupportedOperationException("A Document String cannot be converted to a Number.");
    }

    @Override
    public Map<String, Document> asMap() {
        throw new UnsupportedOperationException("A Document String cannot be converted to a Map.");
    }

    @Override
    public List<Document> asList() {
        throw new UnsupportedOperationException("A Document String cannot be converted to a List.");
    }

    @Override
    public <R> R accept(DocumentVisitor<? extends R> visitor) {
        return visitor.visitString(this.asString());
    }

    @Override
    public void accept(VoidDocumentVisitor visitor) {
        visitor.visitString(this.asString());
    }

    public String toString() {
        return "\"" + this.value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringDocument)) {
            return false;
        }
        StringDocument that = (StringDocument)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}

