/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.FilterParser;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PermissionGenerator {
    public static final String KEY = "permissions";
    private static final String MATCH_ALL = "*";
    private static final String VALID_WILDCARD = ".*";
    private final Builder builder;
    private final Set<Parameter> parameters;

    public static Set<String> getDeclaredServices(Builder builder) {
        TreeSet<String> declaredServices = new TreeSet<String>();
        for (Map.Entry<String, Attrs> entry : builder.getProvideCapability().entrySet()) {
            Attrs attrs;
            String ifaces;
            if (!Processor.removeDuplicateMarker(entry.getKey()).equals("osgi.service") || (ifaces = (attrs = entry.getValue()).get("objectClass")) == null) continue;
            for (String iface : ifaces.split(",")) {
                declaredServices.add(iface.trim());
            }
        }
        return declaredServices;
    }

    public static Set<String> getReferencedServices(Builder builder) {
        TreeSet<String> referencedServices = new TreeSet<String>();
        for (Map.Entry<String, Attrs> entry : builder.getRequireCapability().entrySet()) {
            Attrs attrs;
            String filter;
            if (!Processor.removeDuplicateMarker(entry.getKey()).equals("osgi.service") || (filter = (attrs = entry.getValue()).get("filter:")) == null || filter.isEmpty()) continue;
            FilterParser filterParser = new FilterParser();
            FilterParser.Expression expr = filterParser.parse(filter);
            referencedServices.addAll((Collection<String>)expr.visit(new FindReferencedServices()));
        }
        if (referencedServices.contains(MATCH_ALL)) {
            return Collections.singleton(MATCH_ALL);
        }
        return referencedServices;
    }

    private static EnumSet<Parameter> parseParams(Builder builder, String ... args) {
        EnumSet<Parameter> parameters = EnumSet.noneOf(Parameter.class);
        for (int ix = 1; ix < args.length; ++ix) {
            String name = args[ix].toUpperCase();
            try {
                parameters.add(Parameter.valueOf(name));
                continue;
            }
            catch (IllegalArgumentException ex) {
                builder.error("Could not parse argument for ${permissions}: %s", args[ix]);
            }
        }
        return parameters;
    }

    public PermissionGenerator(Builder builder, String ... args) {
        assert (args.length > 0 && KEY.equals(args[0]));
        this.builder = builder;
        this.parameters = PermissionGenerator.parseParams(builder, args);
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (Parameter param : this.parameters) {
            param.generate(sb, this.builder);
        }
        return sb.toString();
    }

    static final class FindReferencedServices
    extends FilterParser.ExpressionVisitor<Set<String>> {
        public FindReferencedServices() {
            super(Collections.emptySet());
        }

        @Override
        public Set<String> visit(FilterParser.SimpleExpression expr) {
            if ("objectClass".equals(expr.getKey())) {
                if (expr.getOp() == FilterParser.Op.EQUAL) {
                    String v = expr.getValue();
                    if (!v.contains(PermissionGenerator.MATCH_ALL) || v.equals(PermissionGenerator.MATCH_ALL)) {
                        return Collections.singleton(v);
                    }
                    if (v.endsWith(PermissionGenerator.VALID_WILDCARD) && !v.substring(0, v.length() - 2).contains(PermissionGenerator.MATCH_ALL)) {
                        return Collections.singleton(v);
                    }
                    return Collections.emptySet();
                }
                return Collections.emptySet();
            }
            return Collections.emptySet();
        }

        @Override
        public Set<String> visit(FilterParser.PatternExpression expr) {
            return this.visit((FilterParser.SimpleExpression)expr);
        }

        @Override
        public Set<String> visit(FilterParser.Or expr) {
            HashSet<String> matches = new HashSet<String>();
            for (FilterParser.Expression expression : expr.getExpressions()) {
                matches.addAll((Collection<String>)expression.visit(this));
            }
            return matches;
        }

        @Override
        public Set<String> visitTrue() {
            return Collections.singleton(PermissionGenerator.MATCH_ALL);
        }
    }

    public static enum Parameter {
        ADMIN{

            @Override
            public void generate(StringBuilder sb, Builder builder) {
                sb.append("(org.osgi.framework.AdminPermission)\n");
            }
        }
        ,
        CAPABILITIES{

            @Override
            public void generate(StringBuilder sb, Builder builder) {
                for (String namespace : builder.getProvideCapability().keySet()) {
                    if (Processor.isDuplicate(namespace)) continue;
                    sb.append("(org.osgi.framework.CapabilityPermission \"").append(namespace).append("\" \"provide\")\n");
                }
                for (String namespace : builder.getRequireCapability().keySet()) {
                    if (Processor.isDuplicate(namespace)) continue;
                    sb.append("(org.osgi.framework.CapabilityPermission \"").append(namespace).append("\" \"require\")\n");
                }
            }
        }
        ,
        PACKAGES{

            @Override
            public void generate(StringBuilder sb, Builder builder) {
                if (builder.getImports() != null) {
                    for (Descriptors.PackageRef imp : builder.getImports().keySet()) {
                        if (imp.isJava()) continue;
                        sb.append("(org.osgi.framework.PackagePermission \"");
                        sb.append(imp);
                        sb.append("\" \"import\")\n");
                    }
                }
                if (builder.getExports() != null) {
                    for (Descriptors.PackageRef exp : builder.getExports().keySet()) {
                        sb.append("(org.osgi.framework.PackagePermission \"");
                        sb.append(exp);
                        sb.append("\" \"export\")\n");
                    }
                }
            }
        }
        ,
        SERVICES{

            @Override
            public void generate(StringBuilder sb, Builder builder) {
                for (String declaredService : PermissionGenerator.getDeclaredServices(builder)) {
                    sb.append("(org.osgi.framework.ServicePermission \"").append(declaredService).append("\" \"register\")\n");
                }
                for (String referencedService : PermissionGenerator.getReferencedServices(builder)) {
                    sb.append("(org.osgi.framework.ServicePermission \"").append(referencedService).append("\" \"get\")\n");
                }
            }
        };


        public abstract void generate(StringBuilder var1, Builder var2);
    }
}

