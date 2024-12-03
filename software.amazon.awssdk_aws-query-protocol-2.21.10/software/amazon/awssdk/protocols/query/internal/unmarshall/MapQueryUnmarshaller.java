/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.traits.MapTrait
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerContext;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkInternalApi
public final class MapQueryUnmarshaller
implements QueryUnmarshaller<Map<String, ?>> {
    @Override
    public Map<String, ?> unmarshall(QueryUnmarshallerContext context, List<XmlElement> content, SdkField<Map<String, ?>> field) {
        HashMap map = new HashMap();
        MapTrait mapTrait = (MapTrait)field.getTrait(MapTrait.class);
        SdkField mapValueSdkField = mapTrait.valueFieldInfo();
        this.getEntries(content, mapTrait).forEach(entry -> {
            XmlElement key = entry.getElementByName(mapTrait.keyLocationName());
            XmlElement value = entry.getElementByName(mapTrait.valueLocationName());
            QueryUnmarshaller<Object> unmarshaller = context.getUnmarshaller(mapValueSdkField.location(), mapValueSdkField.marshallingType());
            map.put(key.textContent(), unmarshaller.unmarshall(context, Collections.singletonList(value), (SdkField<Object>)mapValueSdkField));
        });
        return map;
    }

    private List<XmlElement> getEntries(List<XmlElement> content, MapTrait mapTrait) {
        return mapTrait.isFlattened() ? content : content.get(0).getElementsByName("entry");
    }
}

