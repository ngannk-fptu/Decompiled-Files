/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.traits.ListTrait
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerContext;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkInternalApi
public final class ListQueryUnmarshaller
implements QueryUnmarshaller<List<?>> {
    @Override
    public List<?> unmarshall(QueryUnmarshallerContext context, List<XmlElement> content, SdkField<List<?>> field) {
        ListTrait listTrait = (ListTrait)field.getTrait(ListTrait.class);
        ArrayList list = new ArrayList();
        this.getMembers(content, listTrait).forEach(member -> {
            QueryUnmarshaller<Object> unmarshaller = context.getUnmarshaller(listTrait.memberFieldInfo().location(), listTrait.memberFieldInfo().marshallingType());
            list.add(unmarshaller.unmarshall(context, Collections.singletonList(member), (SdkField<Object>)listTrait.memberFieldInfo()));
        });
        return list;
    }

    private List<XmlElement> getMembers(List<XmlElement> content, ListTrait listTrait) {
        return listTrait.isFlattened() ? content : content.get(0).children();
    }
}

