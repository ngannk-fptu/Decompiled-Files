/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.build.model.conversions.Converter;
import aQute.bnd.build.model.conversions.HeaderClauseListConverter;
import aQute.bnd.osgi.resource.CapReqBuilder;
import java.util.Map;
import org.osgi.resource.Requirement;

public class RequirementListConverter
extends HeaderClauseListConverter<Requirement> {
    public RequirementListConverter() {
        super(new Converter<Requirement, HeaderClause>(){

            @Override
            public Requirement convert(HeaderClause input) {
                if (input == null) {
                    return null;
                }
                String namespace = input.getName();
                CapReqBuilder builder = new CapReqBuilder(namespace);
                for (Map.Entry<String, String> entry : input.getAttribs().entrySet()) {
                    String key = entry.getKey();
                    if (key.endsWith(":")) {
                        key = key.substring(0, key.length() - 1);
                        builder.addDirective(key, entry.getValue());
                        continue;
                    }
                    try {
                        builder.addAttribute(key, entry.getValue());
                    }
                    catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }
                return builder.buildSyntheticRequirement();
            }

            @Override
            public Requirement error(String msg) {
                CapReqBuilder builder = new CapReqBuilder(msg);
                return builder.buildSyntheticRequirement();
            }
        });
    }
}

