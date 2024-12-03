/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.document.Document
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall.document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeVisitor;

@SdkInternalApi
public class DocumentUnmarshaller
implements JsonNodeVisitor<Document> {
    public Document visitNull() {
        return Document.fromNull();
    }

    public Document visitBoolean(boolean bool) {
        return Document.fromBoolean((boolean)bool);
    }

    public Document visitNumber(String number) {
        return Document.fromNumber((String)number);
    }

    public Document visitString(String string) {
        return Document.fromString((String)string);
    }

    public Document visitArray(List<JsonNode> array) {
        return Document.fromList(array.stream().map(node -> (Document)node.visit((JsonNodeVisitor)this)).collect(Collectors.toList()));
    }

    public Document visitObject(Map<String, JsonNode> object) {
        return Document.fromMap((Map)object.entrySet().stream().collect(Collectors.toMap(entry -> (String)entry.getKey(), entry -> (Document)((JsonNode)entry.getValue()).visit((JsonNodeVisitor)this), (left, right) -> left, LinkedHashMap::new)));
    }

    public Document visitEmbeddedObject(Object embeddedObject) {
        throw new UnsupportedOperationException("Embedded objects are not supported within Document types.");
    }
}

