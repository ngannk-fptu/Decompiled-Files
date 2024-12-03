/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.ArrayHandler;
import aQute.lib.json.BooleanHandler;
import aQute.lib.json.ByteArrayHandler;
import aQute.lib.json.CharacterHandler;
import aQute.lib.json.CollectionHandler;
import aQute.lib.json.DateHandler;
import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.EnumHandler;
import aQute.lib.json.FileHandler;
import aQute.lib.json.Handler;
import aQute.lib.json.MapHandler;
import aQute.lib.json.NumberHandler;
import aQute.lib.json.ObjectHandler;
import aQute.lib.json.SpecialHandler;
import aQute.lib.json.StringHandler;
import aQute.lib.json.UUIDHandler;
import java.io.EOFException;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class JSONCodec {
    static final String START_CHARACTERS = "[{\"-0123456789tfn";
    private static final WeakHashMap<Type, Handler> handlers = new WeakHashMap();
    private static StringHandler sh = new StringHandler();
    private static BooleanHandler bh = new BooleanHandler();
    private static CharacterHandler ch = new CharacterHandler();
    private static CollectionHandler dch = new CollectionHandler(ArrayList.class, (Type)((Object)Object.class));
    private static SpecialHandler sph = new SpecialHandler(Pattern.class, null, null);
    private static DateHandler sdh = new DateHandler();
    private static FileHandler fh = new FileHandler();
    private static ByteArrayHandler byteh = new ByteArrayHandler();
    private static UUIDHandler uuidh = new UUIDHandler();
    boolean ignorenull;
    Map<Type, Handler> localHandlers = new ConcurrentHashMap<Type, Handler>();

    public Encoder enc() {
        return new Encoder(this);
    }

    public Decoder dec() {
        return new Decoder(this);
    }

    void encode(Encoder app, Object object, Type type, Map<Object, Type> visited) throws Exception {
        if (object == null) {
            app.append("null");
            return;
        }
        if (type == null || type == Object.class) {
            type = object.getClass();
        }
        Handler h = this.getHandler(type, object.getClass());
        h.encode(app, object, visited);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Handler getHandler(Type type, Class<?> actual) throws Exception {
        Handler h;
        Type sub;
        if (type == String.class) {
            return sh;
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return bh;
        }
        if (type == byte[].class) {
            return byteh;
        }
        if (Character.class == type || Character.TYPE == type) {
            return ch;
        }
        if (Pattern.class == type) {
            return sph;
        }
        if (Date.class == type) {
            return sdh;
        }
        if (File.class == type) {
            return fh;
        }
        if (UUID.class == type) {
            return uuidh;
        }
        if (type instanceof GenericArrayType && (sub = ((GenericArrayType)type).getGenericComponentType()) == Byte.TYPE) {
            return byteh;
        }
        WeakHashMap<Type, Handler> weakHashMap = handlers;
        synchronized (weakHashMap) {
            h = handlers.get(type);
        }
        if (h != null) {
            return h;
        }
        h = this.localHandlers.get(type);
        if (h != null) {
            return h;
        }
        if (type instanceof Class) {
            Class clazz = (Class)type;
            if (Enum.class.isAssignableFrom(clazz)) {
                h = new EnumHandler(clazz);
            } else if (Iterable.class.isAssignableFrom(clazz)) {
                h = dch;
            } else if (clazz.isArray()) {
                h = new ArrayHandler(clazz, clazz.getComponentType());
            } else if (Map.class.isAssignableFrom(clazz)) {
                h = new MapHandler(clazz, (Type)((Object)Object.class), (Type)((Object)Object.class));
            } else if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
                h = new NumberHandler(clazz);
            } else {
                Method valueOf = null;
                Constructor constructor = null;
                try {
                    constructor = clazz.getConstructor(String.class);
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    valueOf = clazz.getMethod("valueOf", String.class);
                }
                catch (Exception e) {
                    // empty catch block
                }
                h = constructor != null || valueOf != null ? new SpecialHandler(clazz, constructor, valueOf) : new ObjectHandler(this, clazz);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class) {
                Class rawClass = (Class)rawType;
                if (Iterable.class.isAssignableFrom(rawClass)) {
                    h = new CollectionHandler(rawClass, pt.getActualTypeArguments()[0]);
                } else if (Map.class.isAssignableFrom(rawClass)) {
                    h = new MapHandler(rawClass, pt.getActualTypeArguments()[0], pt.getActualTypeArguments()[1]);
                } else {
                    if (!Dictionary.class.isAssignableFrom(rawClass)) return this.getHandler(rawType, null);
                    h = new MapHandler(Hashtable.class, pt.getActualTypeArguments()[0], pt.getActualTypeArguments()[1]);
                }
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)type;
            h = gat.getGenericComponentType() == byte[].class ? byteh : new ArrayHandler(this.getRawClass(type), gat.getGenericComponentType());
        } else {
            TypeVariable tv;
            Type[] bounds;
            if (!(type instanceof TypeVariable)) throw new IllegalArgumentException("Found a parameterized type that is not a map or collection");
            h = actual != null ? this.getHandler(actual, null) : ((bounds = (tv = (TypeVariable)type).getBounds()) == null || bounds.length == 0 ? new ObjectHandler(this, Object.class) : this.getHandler(bounds[bounds.length - 1], null));
        }
        weakHashMap = handlers;
        synchronized (weakHashMap) {
            handlers.put(type, h);
            return h;
        }
    }

    Object decode(Type type, Decoder isr) throws Exception {
        int c = isr.skipWs();
        if (type == null || type == Object.class) {
            switch (c) {
                case 123: {
                    type = LinkedHashMap.class;
                    break;
                }
                case 91: {
                    type = ArrayList.class;
                    break;
                }
                case 34: {
                    return this.parseString(isr);
                }
                case 110: {
                    isr.expect("ull");
                    return null;
                }
                case 116: {
                    isr.expect("rue");
                    return true;
                }
                case 102: {
                    isr.expect("alse");
                    return false;
                }
                case 45: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    return this.parseNumber(isr);
                }
                default: {
                    throw new IllegalArgumentException("Invalid character at begin of token: " + (char)c);
                }
            }
        }
        Handler h = this.getHandler((Type)((Object)type), null);
        switch (c) {
            case 123: {
                return h.decodeObject(isr);
            }
            case 91: {
                return h.decodeArray(isr);
            }
            case 34: {
                String string = this.parseString(isr);
                return h.decode(isr, string);
            }
            case 110: {
                isr.expect("ull");
                return h.decode(isr);
            }
            case 116: {
                isr.expect("rue");
                return h.decode(isr, Boolean.TRUE);
            }
            case 102: {
                isr.expect("alse");
                return h.decode(isr, Boolean.FALSE);
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                return h.decode(isr, this.parseNumber(isr));
            }
        }
        throw new IllegalArgumentException("Unexpected character in input stream: " + (char)c);
    }

    String parseString(Decoder r) throws Exception {
        assert (r.current() == 34);
        int c = r.next();
        StringBuilder sb = new StringBuilder();
        while (c != 34) {
            if (c < 0 || Character.isISOControl(c)) {
                throw new IllegalArgumentException("JSON strings may not contain control characters: " + r.current());
            }
            if (c == 92) {
                c = r.read();
                switch (c) {
                    case 34: 
                    case 47: 
                    case 92: {
                        sb.append((char)c);
                        break;
                    }
                    case 98: {
                        sb.append('\b');
                        break;
                    }
                    case 102: {
                        sb.append('\f');
                        break;
                    }
                    case 110: {
                        sb.append('\n');
                        break;
                    }
                    case 114: {
                        sb.append('\r');
                        break;
                    }
                    case 116: {
                        sb.append('\t');
                        break;
                    }
                    case 117: {
                        int a3 = this.hexDigit(r.read()) << 12;
                        int a2 = this.hexDigit(r.read()) << 8;
                        int a1 = this.hexDigit(r.read()) << 4;
                        int a0 = this.hexDigit(r.read()) << 0;
                        c = a3 + a2 + a1 + a0;
                        sb.append((char)c);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("The only characters after a backslash are \", \\, b, f, n, r, t, and u but got " + c);
                    }
                }
            } else {
                sb.append((char)c);
            }
            c = r.read();
        }
        assert (c == 34);
        r.read();
        return sb.toString();
    }

    private int hexDigit(int c) throws EOFException {
        if (c >= 48 && c <= 57) {
            return c - 48;
        }
        if (c >= 65 && c <= 70) {
            return c - 65 + 10;
        }
        if (c >= 97 && c <= 102) {
            return c - 97 + 10;
        }
        throw new IllegalArgumentException("Invalid hex character: " + c);
    }

    private Number parseNumber(Decoder r) throws Exception {
        int c;
        StringBuilder sb = new StringBuilder();
        boolean d = false;
        if (r.current() == 45) {
            sb.append('-');
            r.read();
        }
        if ((c = r.current()) == 48) {
            sb.append('0');
            c = r.read();
        } else if (c >= 49 && c <= 57) {
            sb.append((char)c);
            c = r.read();
            while (c >= 48 && c <= 57) {
                sb.append((char)c);
                c = r.read();
            }
        } else {
            throw new IllegalArgumentException("Expected digit");
        }
        if (c == 46) {
            d = true;
            sb.append('.');
            c = r.read();
            while (c >= 48 && c <= 57) {
                sb.append((char)c);
                c = r.read();
            }
        }
        if (c == 101 || c == 69) {
            d = true;
            sb.append('e');
            c = r.read();
            if (c == 43) {
                sb.append('+');
                c = r.read();
            } else if (c == 45) {
                sb.append('-');
                c = r.read();
            }
            while (c >= 48 && c <= 57) {
                sb.append((char)c);
                c = r.read();
            }
        }
        if (d) {
            return Double.parseDouble(sb.toString());
        }
        long l = Long.parseLong(sb.toString());
        if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
            return l;
        }
        return (int)l;
    }

    void parseArray(Collection<Object> list, Type componentType, Decoder r) throws Exception {
        assert (r.current() == 91);
        int c = r.next();
        while (START_CHARACTERS.indexOf(c) >= 0) {
            Object o = this.decode(componentType, r);
            list.add(o);
            c = r.skipWs();
            if (c == 93) break;
            if (c == 44) {
                c = r.next();
                continue;
            }
            throw new IllegalArgumentException("Invalid character in parsing list, expected ] or , but found " + (char)c);
        }
        assert (r.current() == 93);
        r.read();
    }

    Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return this.getRawClass(((ParameterizedType)type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            Type subType = ((GenericArrayType)type).getGenericComponentType();
            Class<?> c = this.getRawClass(subType);
            return Array.newInstance(c, 0).getClass();
        }
        throw new IllegalArgumentException("Does not support generics beyond Parameterized Type  and GenericArrayType, got " + type);
    }

    public JSONCodec setIgnorenull(boolean ignorenull) {
        this.ignorenull = ignorenull;
        return this;
    }

    public boolean isIgnorenull() {
        return this.ignorenull;
    }

    public JSONCodec addHandler(Type type, Handler handler) {
        this.localHandlers.put(type, handler);
        return this;
    }
}

