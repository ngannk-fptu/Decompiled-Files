/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.UrlEncoding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Operation
implements Serializable {
    protected final String name;
    protected final boolean multivar;
    private static Map<String, Operation> operations = Operation.getOperations();

    protected Operation(String name) {
        this(name, false);
    }

    protected Operation(String name, boolean multivar) {
        this.name = name;
        this.multivar = multivar;
    }

    public final String name() {
        return this.name;
    }

    public abstract String evaluate(String var1, String var2, Context var3);

    public abstract void explain(String var1, String var2, Appendable var3) throws IOException;

    public String[] getVariables(String var) {
        ArrayList<String> list = new ArrayList<String>();
        if (!this.multivar) {
            String name = Operation.tokenName(var);
            if (!list.contains(name)) {
                list.add(name);
            }
        } else {
            String[] vardefs = var.split("\\+?\\s*,\\s*");
            for (int n = 0; n < vardefs.length; ++n) {
                String vardef = vardefs[n];
                String name = vardef.split("=", 2)[0];
                if (list.contains(name)) continue;
                list.add(name);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private static Map<String, Operation> getOperations() {
        HashMap<String, Operation> ops = new HashMap<String, Operation>();
        ops.put("", new DefaultOperation());
        ops.put("prefix", new PrefixOperation());
        ops.put("suffix", new AppendOperation());
        ops.put("join", new JoinOperation());
        ops.put("list", new ListJoinOperation());
        ops.put("opt", new OptOperation());
        ops.put("neg", new NegOperation());
        ops.put("append", (Operation)ops.get("suffix"));
        ops.put("listjoin", (Operation)ops.get("list"));
        return ops;
    }

    public static void register(Operation operation) {
        operations.put(operation.name(), operation);
    }

    public static Operation get(String name) {
        Operation op;
        if (name == null) {
            name = "";
        }
        if ((op = operations.get(name)) != null) {
            return op;
        }
        throw new UnsupportedOperationException(name);
    }

    private static String tokenName(String token) {
        String[] vardef = token.split("=", 2);
        return vardef[0];
    }

    private static String evallist(String token, Context context, String sep) {
        StringBuilder buf = new StringBuilder();
        Object value = context.resolve(token);
        if (value != null) {
            if (value instanceof String) {
                String val = Operation.toString(value, context);
                if (val != null) {
                    buf.append(val);
                }
            } else if (value.getClass().isArray()) {
                Object[] values;
                for (Object obj : values = (Object[])value) {
                    String val = Operation.toString(obj, context);
                    if (val == null) continue;
                    if (buf.length() > 0) {
                        buf.append(sep);
                    }
                    buf.append(val);
                }
            } else if (value instanceof Iterable) {
                Iterable iterable = (Iterable)value;
                for (Object obj : iterable) {
                    String val = Operation.toString(obj, context);
                    if (val == null) continue;
                    if (buf.length() > 0) {
                        buf.append(sep);
                    }
                    buf.append(val);
                }
            }
        } else {
            return null;
        }
        return buf.toString();
    }

    protected static String eval(String token, Context context) {
        String[] vardef = token.split("=", 2);
        String var = vardef[0];
        String def = vardef.length > 1 ? vardef[1] : null;
        Object rep = context.resolve(var);
        String val = Operation.toString(rep, context);
        return val != null ? val : (def != null ? def : null);
    }

    private static String toString(Object val, Context context) {
        if (val == null) {
            return null;
        }
        if (val.getClass().isArray()) {
            Object[] array;
            if (val instanceof byte[]) {
                return UrlEncoding.encode((byte[])val);
            }
            if (val instanceof char[]) {
                String chars = new String((char[])val);
                return UrlEncoding.encode((CharSequence)Normalizer.normalize(chars, Normalizer.Form.KC).toString(), context.isIri() ? CharUtils.Profile.IUNRESERVED.filter() : CharUtils.Profile.UNRESERVED.filter());
            }
            if (val instanceof short[]) {
                short[] array2;
                StringBuilder buf = new StringBuilder();
                for (short obj : array2 = (short[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            if (val instanceof int[]) {
                int[] array3;
                StringBuilder buf = new StringBuilder();
                for (int obj : array3 = (int[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            if (val instanceof long[]) {
                long[] array4;
                StringBuilder buf = new StringBuilder();
                for (long obj : array4 = (long[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            if (val instanceof double[]) {
                double[] array5;
                StringBuilder buf = new StringBuilder();
                for (double obj : array5 = (double[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            if (val instanceof float[]) {
                float[] array6;
                StringBuilder buf = new StringBuilder();
                for (float obj : array6 = (float[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            if (val instanceof boolean[]) {
                boolean[] array7;
                StringBuilder buf = new StringBuilder();
                for (boolean obj : array7 = (boolean[])val) {
                    if (buf.length() > 0) {
                        buf.append("%2C");
                    }
                    buf.append(String.valueOf(obj));
                }
                return buf.toString();
            }
            StringBuilder buf = new StringBuilder();
            for (Object obj : array = (Object[])val) {
                buf.append(Operation.toString(obj, context));
            }
            return buf.toString();
        }
        if (val instanceof Template) {
            return Operation.toString(((Template)val).getPattern(), context);
        }
        if (val instanceof InputStream) {
            try {
                return UrlEncoding.encode((InputStream)val);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (val instanceof Readable) {
            try {
                return UrlEncoding.encode((Readable)val, "UTF-8", context.isIri() ? CharUtils.Profile.IUNRESERVED.filter() : CharUtils.Profile.UNRESERVED.filter());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (val instanceof CharSequence) {
            return Operation.encode((CharSequence)val, context.isIri());
        }
        if (val instanceof Byte) {
            return UrlEncoding.encode((Byte)val);
        }
        if (val instanceof Iterable) {
            StringBuilder buf = new StringBuilder();
            Iterable i = (Iterable)val;
            for (Object obj : i) {
                buf.append(Operation.toString(obj, context));
            }
            return buf.toString();
        }
        return Operation.encode(val != null ? val.toString() : null, context.isIri());
    }

    protected static String eval(String token, String arg, Context context) {
        String[] vardef = token.split("=", 2);
        String var = vardef[0];
        String def = vardef.length > 1 ? vardef[1] : null;
        Object rep = context.resolve(var);
        if (rep != null) {
            StringBuilder buf = new StringBuilder();
            if (rep.getClass().isArray()) {
                if (rep instanceof byte[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof char[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof short[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof int[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof long[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof double[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof float[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else if (rep instanceof boolean[]) {
                    String val = Operation.toString(rep, context);
                    if (val != null) {
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                } else {
                    Object[] array;
                    for (Object obj : array = (Object[])rep) {
                        String val = Operation.toString(obj, context);
                        if (val == null) continue;
                        if (buf.length() > 0) {
                            buf.append(arg);
                        }
                        buf.append(var);
                        buf.append("=");
                        buf.append(val);
                    }
                }
            } else if (rep instanceof Iterable) {
                Iterable list = (Iterable)rep;
                for (Object obj : list) {
                    String val = Operation.toString(obj, context);
                    if (val == null) continue;
                    if (buf.length() > 0) {
                        buf.append(arg);
                    }
                    buf.append(var);
                    buf.append("=");
                    buf.append(val);
                }
            } else {
                String val = Operation.toString(rep, context);
                if (val != null) {
                    buf.append(var);
                    buf.append("=");
                    buf.append(val);
                }
            }
            return buf.toString();
        }
        if (def != null && def.length() > 0) {
            StringBuilder buf = new StringBuilder();
            buf.append(var);
            buf.append("=");
            buf.append(def);
            return buf.toString();
        }
        return null;
    }

    protected static boolean isdefined(String token, Context context) {
        String[] vardef = token.split("=", 2);
        String var = vardef[0];
        String def = vardef.length > 1 ? vardef[1] : null;
        Object rep = context.resolve(var);
        if (rep == null) {
            rep = def;
        }
        if (rep == null) {
            return false;
        }
        if (rep.getClass().isArray()) {
            if (rep instanceof byte[]) {
                return ((byte[])rep).length > 0;
            }
            if (rep instanceof short[]) {
                return ((short[])rep).length > 0;
            }
            if (rep instanceof char[]) {
                return ((char[])rep).length > 0;
            }
            if (rep instanceof int[]) {
                return ((int[])rep).length > 0;
            }
            if (rep instanceof long[]) {
                return ((long[])rep).length > 0;
            }
            if (rep instanceof double[]) {
                return ((double[])rep).length > 0;
            }
            if (rep instanceof float[]) {
                return ((float[])rep).length > 0;
            }
            if (rep instanceof boolean[]) {
                return ((boolean[])rep).length > 0;
            }
            if (rep instanceof Object[]) {
                return ((Object[])rep).length > 0;
            }
        }
        return true;
    }

    private static String encode(CharSequence val, boolean isiri) {
        return UrlEncoding.encode((CharSequence)Normalizer.normalize(val, Normalizer.Form.KC).toString(), isiri ? CharUtils.Profile.IUNRESERVED.filter() : CharUtils.Profile.UNRESERVED.filter());
    }

    private static final class NegOperation
    extends Operation {
        private static final long serialVersionUID = 1936380358902743528L;

        public NegOperation() {
            super("neg", true);
        }

        public String evaluate(String var, String arg, Context context) {
            String[] vardefs;
            for (String v : vardefs = var.split("\\s*,\\s*")) {
                if (!NegOperation.isdefined(v, context)) continue;
                return null;
            }
            return arg;
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("If [");
            String[] vars = this.getVariables(var);
            boolean b = false;
            for (String v : vars) {
                if (b) {
                    buf.append(',');
                } else {
                    b = true;
                }
                buf.append("'").append(v).append("'");
            }
            buf.append("] is undefined, or a zero length list, then insert '").append(arg).append("'");
        }
    }

    private static final class OptOperation
    extends Operation {
        private static final long serialVersionUID = 7808433764609641180L;

        public OptOperation() {
            super("opt", true);
        }

        public String evaluate(String var, String arg, Context context) {
            String[] vardefs;
            for (String v : vardefs = var.split("\\s*,\\s*")) {
                if (!OptOperation.isdefined(v, context)) continue;
                return arg;
            }
            return null;
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("If [");
            String[] vars = this.getVariables(var);
            boolean b = false;
            for (String v : vars) {
                if (b) {
                    buf.append(',');
                } else {
                    b = true;
                }
                buf.append("'").append(v).append("'");
            }
            buf.append("] is defined and a string, or a list with one or more members, then insert '").append(arg).append("'");
        }
    }

    private static final class ListJoinOperation
    extends Operation {
        private static final long serialVersionUID = -8314383556644740425L;

        public ListJoinOperation() {
            super("list");
        }

        public String evaluate(String var, String arg, Context context) {
            return Operation.evallist(var, context, arg);
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("Join the members of the list '").append(var).append("' together with '").append(arg).append("'");
        }
    }

    private static final class JoinOperation
    extends Operation {
        private static final long serialVersionUID = -4102440981071994082L;

        public JoinOperation() {
            super("join", true);
        }

        public String evaluate(String var, String arg, Context context) {
            StringBuilder buf = new StringBuilder();
            String[] vardefs = var.split("\\+?\\s*,\\s*");
            String val = null;
            for (int n = 0; n < vardefs.length; ++n) {
                String vardef = vardefs[n];
                val = JoinOperation.eval(vardef, arg, context);
                if (val == null) continue;
                if (buf.length() > 0) {
                    buf.append(arg);
                }
                buf.append(val);
            }
            String value = buf.toString();
            return value;
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("Join 'var=value' with '" + arg + "' for each variable in [");
            String[] vars = this.getVariables(var);
            boolean b = false;
            for (String v : vars) {
                if (b) {
                    buf.append(',');
                } else {
                    b = true;
                }
                buf.append("'").append(v).append("'");
            }
            buf.append("]");
        }
    }

    private static final class AppendOperation
    extends Operation {
        private static final long serialVersionUID = -2742793539643289075L;

        public AppendOperation() {
            super("suffix");
        }

        public String evaluate(String var, String arg, Context context) {
            String value = Operation.evallist(var, context, arg);
            return value == null || value.length() == 0 ? "" : (arg != null ? value + arg : value);
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("If '").append(var).append("' is defined then append '").append(arg).append("' to the value of '").append(var).append("'");
        }
    }

    private static final class PrefixOperation
    extends Operation {
        private static final long serialVersionUID = 2738115969196268525L;

        public PrefixOperation() {
            super("prefix");
        }

        public String evaluate(String var, String arg, Context context) {
            String value = Operation.evallist(var, context, arg);
            return value == null || value.length() == 0 ? "" : (arg != null ? arg + value : value);
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("If '").append(var).append("' is defined then prefix the value of '").append(var).append("' with '").append(arg).append("'");
        }
    }

    private static final class DefaultOperation
    extends Operation {
        private static final long serialVersionUID = -1279818778391836528L;

        public DefaultOperation() {
            super("");
        }

        public String evaluate(String var, String arg, Context context) {
            return DefaultOperation.eval(var, context);
        }

        public void explain(String var, String arg, Appendable buf) throws IOException {
            buf.append("Replaced with the value of '").append(var).append("'");
        }
    }
}

