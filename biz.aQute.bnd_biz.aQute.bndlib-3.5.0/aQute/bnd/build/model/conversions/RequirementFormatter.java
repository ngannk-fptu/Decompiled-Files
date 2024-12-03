/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;
import java.util.Map;
import org.osgi.resource.Requirement;

public class RequirementFormatter
implements Converter<String, Requirement> {
    @Override
    public String convert(Requirement req) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder();
        builder.append(req.getNamespace());
        for (Map.Entry<String, String> directive : req.getDirectives().entrySet()) {
            builder.append(';').append(directive.getKey()).append(":='").append(directive.getValue()).append('\'');
        }
        for (Map.Entry<String, Object> attribute : req.getAttributes().entrySet()) {
            builder.append(';').append(attribute.getKey()).append("='").append(attribute.getValue()).append('\'');
        }
        return builder.toString();
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

