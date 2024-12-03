/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.json.JsonException;
import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GString;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StreamingJsonBuilder
extends GroovyObjectSupport {
    private static final String DOUBLE_CLOSE_BRACKET = "}}";
    private static final String COLON_WITH_OPEN_BRACE = ":{";
    private Writer writer;

    public StreamingJsonBuilder(Writer writer) {
        this.writer = writer;
    }

    public StreamingJsonBuilder(Writer writer, Object content) throws IOException {
        this(writer);
        if (content != null) {
            writer.write(JsonOutput.toJson(content));
        }
    }

    public Object call(Map m) throws IOException {
        this.writer.write(JsonOutput.toJson(m));
        return m;
    }

    public void call(String name) throws IOException {
        this.writer.write(JsonOutput.toJson(Collections.singletonMap(name, Collections.emptyMap())));
    }

    public Object call(List l) throws IOException {
        this.writer.write(JsonOutput.toJson(l));
        return l;
    }

    public Object call(Object ... args) throws IOException {
        return this.call(Arrays.asList(args));
    }

    public Object call(Iterable coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        return StreamingJsonDelegate.writeCollectionWithClosure(this.writer, coll, c);
    }

    public Object call(Collection coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        return this.call((Iterable)coll, c);
    }

    public Object call(@DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        this.writer.write(123);
        StreamingJsonDelegate.cloneDelegateAndGetContent(this.writer, c);
        this.writer.write(125);
        return null;
    }

    public void call(String name, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        this.writer.write(123);
        this.writer.write(JsonOutput.toJson(name));
        this.writer.write(58);
        this.call(c);
        this.writer.write(125);
    }

    public void call(String name, Iterable coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        this.writer.write(123);
        this.writer.write(JsonOutput.toJson(name));
        this.writer.write(58);
        this.call(coll, c);
        this.writer.write(125);
    }

    public void call(String name, Collection coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
        this.call(name, (Iterable)coll, c);
    }

    public void call(String name, Map map, @DelegatesTo(value=StreamingJsonDelegate.class) Closure callable) throws IOException {
        this.writer.write(123);
        this.writer.write(JsonOutput.toJson(name));
        this.writer.write(COLON_WITH_OPEN_BRACE);
        boolean first = true;
        for (Map.Entry it : map.entrySet()) {
            if (!first) {
                this.writer.write(44);
            } else {
                first = false;
            }
            Map.Entry entry = it;
            this.writer.write(JsonOutput.toJson(entry.getKey()));
            this.writer.write(58);
            this.writer.write(JsonOutput.toJson(entry.getValue()));
        }
        StreamingJsonDelegate.cloneDelegateAndGetContent(this.writer, callable, map.size() == 0);
        this.writer.write(DOUBLE_CLOSE_BRACKET);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        boolean notExpectedArgs = false;
        if (args != null && Object[].class.isAssignableFrom(args.getClass())) {
            Object[] arr = (Object[])args;
            try {
                switch (arr.length) {
                    case 0: {
                        this.call(name);
                        break;
                    }
                    case 1: {
                        if (arr[0] instanceof Closure) {
                            Closure callable = (Closure)arr[0];
                            this.call(name, callable);
                            break;
                        }
                        if (arr[0] instanceof Map) {
                            Map<String, Map> map = Collections.singletonMap(name, (Map)arr[0]);
                            this.call(map);
                            break;
                        }
                        notExpectedArgs = true;
                        break;
                    }
                    case 2: {
                        Object first = arr[0];
                        Object second = arr[1];
                        boolean isClosure = second instanceof Closure;
                        if (isClosure && first instanceof Map) {
                            Closure callable = (Closure)second;
                            this.call(name, (Map)first, callable);
                            break;
                        }
                        if (isClosure && first instanceof Iterable) {
                            Iterable coll = (Iterable)first;
                            Closure callable = (Closure)second;
                            this.call(name, coll, callable);
                            break;
                        }
                        if (isClosure && first.getClass().isArray()) {
                            List<Object> coll = Arrays.asList((Object[])first);
                            Closure callable = (Closure)second;
                            this.call(name, (Iterable)coll, callable);
                            break;
                        }
                        notExpectedArgs = true;
                        break;
                    }
                    default: {
                        notExpectedArgs = true;
                        break;
                    }
                }
            }
            catch (IOException ioe) {
                throw new JsonException(ioe);
            }
        } else {
            notExpectedArgs = true;
        }
        if (!notExpectedArgs) {
            return this;
        }
        throw new JsonException("Expected no arguments, a single map, a single closure, or a map and closure as arguments.");
    }

    public static class StreamingJsonDelegate
    extends GroovyObjectSupport {
        protected final Writer writer;
        protected boolean first;
        protected State state;

        public StreamingJsonDelegate(Writer w, boolean first) {
            this.writer = w;
            this.first = first;
        }

        public Writer getWriter() {
            return this.writer;
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            if (args != null && Object[].class.isAssignableFrom(args.getClass())) {
                try {
                    Object[] arr = (Object[])args;
                    int len = arr.length;
                    switch (len) {
                        case 1: {
                            Object value = arr[0];
                            if (value instanceof Closure) {
                                this.call(name, (Closure)value);
                            } else if (value instanceof Writable) {
                                this.call(name, (Writable)value);
                            } else {
                                this.call(name, value);
                            }
                            return null;
                        }
                        case 2: {
                            if (!(arr[len - 1] instanceof Closure)) break;
                            Object obj = arr[0];
                            Closure callable = (Closure)arr[1];
                            if (obj instanceof Iterable) {
                                this.call(name, (Iterable)obj, callable);
                                return null;
                            }
                            if (obj.getClass().isArray()) {
                                this.call(name, Arrays.asList((Object[])obj), callable);
                                return null;
                            }
                            this.call(name, obj, callable);
                            return null;
                        }
                    }
                    List<Object> list = Arrays.asList(arr);
                    this.call(name, list);
                }
                catch (IOException ioe) {
                    throw new JsonException(ioe);
                }
            }
            return this;
        }

        public void call(String name, List<Object> list) throws IOException {
            this.writeName(name);
            this.writeArray(list);
        }

        public void call(String name, Object ... array) throws IOException {
            this.writeName(name);
            this.writeArray(Arrays.asList(array));
        }

        public void call(String name, Iterable coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
            this.writeName(name);
            this.writeObjects(coll, c);
        }

        public void call(String name, Collection coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
            this.call(name, (Iterable)coll, c);
        }

        public void call(String name, Object value) throws IOException {
            this.writeName(name);
            this.writeValue(value);
        }

        public void call(String name, Object value, @DelegatesTo(value=StreamingJsonDelegate.class) Closure callable) throws IOException {
            this.writeName(name);
            this.verifyValue();
            StreamingJsonDelegate.writeObject(this.writer, value, callable);
        }

        public void call(String name, @DelegatesTo(value=StreamingJsonDelegate.class) Closure value) throws IOException {
            this.writeName(name);
            this.verifyValue();
            this.writer.write(123);
            StreamingJsonDelegate.cloneDelegateAndGetContent(this.writer, value);
            this.writer.write(125);
        }

        public void call(String name, JsonOutput.JsonUnescaped json) throws IOException {
            this.writeName(name);
            this.verifyValue();
            this.writer.write(json.toString());
        }

        public void call(String name, Writable json) throws IOException {
            this.writeName(name);
            this.verifyValue();
            if (json instanceof GString) {
                this.writer.write(JsonOutput.toJson(json.toString()));
            } else {
                json.writeTo(this.writer);
            }
        }

        private void writeObjects(Iterable coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) throws IOException {
            this.verifyValue();
            StreamingJsonDelegate.writeCollectionWithClosure(this.writer, coll, c);
        }

        protected void verifyValue() {
            if (this.state == State.VALUE) {
                throw new IllegalStateException("Cannot write value when value has just been written. Write a name first!");
            }
            this.state = State.VALUE;
        }

        protected void writeName(String name) throws IOException {
            if (this.state == State.NAME) {
                throw new IllegalStateException("Cannot write a name when a name has just been written. Write a value first!");
            }
            this.state = State.NAME;
            if (!this.first) {
                this.writer.write(44);
            } else {
                this.first = false;
            }
            this.writer.write(JsonOutput.toJson(name));
            this.writer.write(58);
        }

        protected void writeValue(Object value) throws IOException {
            this.verifyValue();
            this.writer.write(JsonOutput.toJson(value));
        }

        protected void writeArray(List<Object> list) throws IOException {
            this.verifyValue();
            this.writer.write(JsonOutput.toJson(list));
        }

        public static boolean isCollectionWithClosure(Object[] args) {
            return args.length == 2 && args[0] instanceof Iterable && args[1] instanceof Closure;
        }

        public static Object writeCollectionWithClosure(Writer writer, Collection coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure closure) throws IOException {
            return StreamingJsonDelegate.writeCollectionWithClosure(writer, (Iterable)coll, closure);
        }

        public static Object writeCollectionWithClosure(Writer writer, Iterable coll, @DelegatesTo(value=StreamingJsonDelegate.class) Closure closure) throws IOException {
            writer.write(91);
            boolean first = true;
            for (Object it : coll) {
                if (!first) {
                    writer.write(44);
                } else {
                    first = false;
                }
                StreamingJsonDelegate.writeObject(writer, it, closure);
            }
            writer.write(93);
            return writer;
        }

        private static void writeObject(Writer writer, Object object, Closure closure) throws IOException {
            writer.write(123);
            StreamingJsonDelegate.curryDelegateAndGetContent(writer, closure, object);
            writer.write(125);
        }

        public static void cloneDelegateAndGetContent(Writer w, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c) {
            StreamingJsonDelegate.cloneDelegateAndGetContent(w, c, true);
        }

        public static void cloneDelegateAndGetContent(Writer w, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c, boolean first) {
            StreamingJsonDelegate delegate = new StreamingJsonDelegate(w, first);
            Closure cloned = (Closure)c.clone();
            cloned.setDelegate(delegate);
            cloned.setResolveStrategy(1);
            cloned.call();
        }

        public static void curryDelegateAndGetContent(Writer w, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c, Object o) {
            StreamingJsonDelegate.curryDelegateAndGetContent(w, c, o, true);
        }

        public static void curryDelegateAndGetContent(Writer w, @DelegatesTo(value=StreamingJsonDelegate.class) Closure c, Object o, boolean first) {
            StreamingJsonDelegate delegate = new StreamingJsonDelegate(w, first);
            Closure curried = c.curry(o);
            curried.setDelegate(delegate);
            curried.setResolveStrategy(1);
            curried.call();
        }

        private static enum State {
            NAME,
            VALUE;

        }
    }
}

