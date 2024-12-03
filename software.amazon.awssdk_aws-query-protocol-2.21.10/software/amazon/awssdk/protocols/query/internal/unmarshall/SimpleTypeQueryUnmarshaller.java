/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerContext;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkInternalApi
public final class SimpleTypeQueryUnmarshaller<T>
implements QueryUnmarshaller<T> {
    private final StringToValueConverter.StringToValue<T> stringToValue;

    public SimpleTypeQueryUnmarshaller(StringToValueConverter.StringToValue<T> stringToValue) {
        this.stringToValue = stringToValue;
    }

    @Override
    public T unmarshall(QueryUnmarshallerContext context, List<XmlElement> content, SdkField<T> field) {
        if (content == null) {
            return null;
        }
        return (T)this.stringToValue.convert(content.get(0).textContent(), field);
    }
}

