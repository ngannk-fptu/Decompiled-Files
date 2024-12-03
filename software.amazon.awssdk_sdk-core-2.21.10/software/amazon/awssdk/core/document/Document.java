/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkNumber;
import software.amazon.awssdk.core.document.DocumentVisitor;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.core.document.internal.BooleanDocument;
import software.amazon.awssdk.core.document.internal.ListDocument;
import software.amazon.awssdk.core.document.internal.MapDocument;
import software.amazon.awssdk.core.document.internal.NullDocument;
import software.amazon.awssdk.core.document.internal.NumberDocument;
import software.amazon.awssdk.core.document.internal.StringDocument;

@SdkPublicApi
@Immutable
public interface Document
extends Serializable {
    public static Document fromString(String string) {
        return new StringDocument(string);
    }

    public static Document fromBoolean(boolean booleanValue) {
        return new BooleanDocument(booleanValue);
    }

    public static Document fromNumber(SdkNumber number) {
        return new NumberDocument(number);
    }

    public static Document fromNumber(int number) {
        return new NumberDocument(SdkNumber.fromInteger(number));
    }

    public static Document fromNumber(long number) {
        return new NumberDocument(SdkNumber.fromLong(number));
    }

    public static Document fromNumber(float number) {
        return new NumberDocument(SdkNumber.fromFloat(number));
    }

    public static Document fromNumber(double number) {
        return new NumberDocument(SdkNumber.fromDouble(number));
    }

    public static Document fromNumber(BigDecimal number) {
        return new NumberDocument(SdkNumber.fromBigDecimal(number));
    }

    public static Document fromNumber(BigInteger number) {
        return new NumberDocument(SdkNumber.fromBigInteger(number));
    }

    public static Document fromNumber(String number) {
        return new NumberDocument(SdkNumber.fromString(number));
    }

    public static Document fromMap(Map<String, Document> documentMap) {
        return new MapDocument(documentMap);
    }

    public static Document fromList(List<Document> documentList) {
        return new ListDocument(documentList);
    }

    public static MapBuilder mapBuilder() {
        return MapDocument.mapBuilder();
    }

    public static ListBuilder listBuilder() {
        return ListDocument.listBuilder();
    }

    public static Document fromNull() {
        return new NullDocument();
    }

    public Object unwrap();

    default public boolean isNull() {
        return false;
    }

    default public boolean isBoolean() {
        return false;
    }

    public boolean asBoolean();

    default public boolean isString() {
        return false;
    }

    public String asString();

    default public boolean isNumber() {
        return false;
    }

    public SdkNumber asNumber();

    default public boolean isMap() {
        return false;
    }

    public Map<String, Document> asMap();

    default public boolean isList() {
        return false;
    }

    public List<Document> asList();

    public <R> R accept(DocumentVisitor<? extends R> var1);

    public void accept(VoidDocumentVisitor var1);

    public static interface ListBuilder {
        public ListBuilder addString(String var1);

        public ListBuilder addBoolean(boolean var1);

        public ListBuilder addNumber(SdkNumber var1);

        public ListBuilder addNumber(int var1);

        public ListBuilder addNumber(long var1);

        public ListBuilder addNumber(float var1);

        public ListBuilder addNumber(double var1);

        public ListBuilder addNumber(BigDecimal var1);

        public ListBuilder addNumber(BigInteger var1);

        public ListBuilder addNumber(String var1);

        public ListBuilder addDocument(Document var1);

        public ListBuilder addMap(Consumer<MapBuilder> var1);

        public ListBuilder addNull();

        public Document build();
    }

    public static interface MapBuilder {
        public MapBuilder putString(String var1, String var2);

        public MapBuilder putNumber(String var1, SdkNumber var2);

        public MapBuilder putNumber(String var1, int var2);

        public MapBuilder putNumber(String var1, long var2);

        public MapBuilder putNumber(String var1, double var2);

        public MapBuilder putNumber(String var1, float var2);

        public MapBuilder putNumber(String var1, BigDecimal var2);

        public MapBuilder putNumber(String var1, BigInteger var2);

        public MapBuilder putNumber(String var1, String var2);

        public MapBuilder putBoolean(String var1, boolean var2);

        public MapBuilder putDocument(String var1, Document var2);

        public MapBuilder putNull(String var1);

        public MapBuilder putList(String var1, List<Document> var2);

        public MapBuilder putList(String var1, Consumer<ListBuilder> var2);

        public MapBuilder putMap(String var1, Map<String, Document> var2);

        public MapBuilder putMap(String var1, Consumer<MapBuilder> var2);

        public Document build();
    }
}

