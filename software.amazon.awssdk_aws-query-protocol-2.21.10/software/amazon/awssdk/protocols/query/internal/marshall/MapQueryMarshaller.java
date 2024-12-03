/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.traits.MapTrait
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerContext;

@SdkInternalApi
public class MapQueryMarshaller
implements QueryMarshaller<Map<String, ?>> {
    @Override
    public void marshall(QueryMarshallerContext context, String path, Map<String, ?> val, SdkField<Map<String, ?>> sdkField) {
        MapTrait mapTrait = (MapTrait)sdkField.getTrait(MapTrait.class);
        AtomicInteger entryNum = new AtomicInteger(1);
        val.forEach((key, value) -> {
            String mapKeyPath = MapQueryMarshaller.resolveMapPath(path, mapTrait, entryNum, mapTrait.keyLocationName());
            context.request().putRawQueryParameter(mapKeyPath, key);
            String mapValuePath = MapQueryMarshaller.resolveMapPath(path, mapTrait, entryNum, mapTrait.valueLocationName());
            QueryMarshaller<Object> marshaller = context.marshallerRegistry().getMarshaller(mapTrait.valueFieldInfo().marshallingType(), val);
            marshaller.marshall(context, mapValuePath, value, (SdkField<Object>)mapTrait.valueFieldInfo());
            entryNum.incrementAndGet();
        });
    }

    private static String resolveMapPath(String path, MapTrait mapTrait, AtomicInteger entryNum, String s) {
        return mapTrait.isFlattened() ? String.format("%s.%d.%s", path, entryNum.get(), s) : String.format("%s.entry.%d.%s", path, entryNum.get(), s);
    }
}

