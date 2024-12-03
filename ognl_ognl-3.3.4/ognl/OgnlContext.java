/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ognl.ClassResolver;
import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.Evaluation;
import ognl.MemberAccess;
import ognl.Node;
import ognl.OgnlOps;
import ognl.OgnlRuntime;
import ognl.TypeConverter;
import ognl.enhance.LocalReference;

public class OgnlContext
implements Map {
    public static final String ROOT_CONTEXT_KEY = "root";
    public static final String THIS_CONTEXT_KEY = "this";
    public static final String TRACE_EVALUATIONS_CONTEXT_KEY = "_traceEvaluations";
    public static final String LAST_EVALUATION_CONTEXT_KEY = "_lastEvaluation";
    public static final String KEEP_LAST_EVALUATION_CONTEXT_KEY = "_keepLastEvaluation";
    @Deprecated
    public static final String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";
    private static final String PROPERTY_KEY_PREFIX = "ognl";
    private static boolean DEFAULT_TRACE_EVALUATIONS = false;
    private static boolean DEFAULT_KEEP_LAST_EVALUATION = false;
    private static final Map<Object, Object> RESERVED_KEYS = new HashMap<Object, Object>(6);
    private Object _root;
    private Object _currentObject;
    private Node _currentNode;
    private boolean _traceEvaluations = DEFAULT_TRACE_EVALUATIONS;
    private Evaluation _rootEvaluation;
    private Evaluation _currentEvaluation;
    private Evaluation _lastEvaluation;
    private boolean _keepLastEvaluation = DEFAULT_KEEP_LAST_EVALUATION;
    private final Map _values;
    private final ClassResolver _classResolver;
    private final TypeConverter _typeConverter;
    private final MemberAccess _memberAccess;
    private final List<Class<?>> _typeStack = new ArrayList(3);
    private final List<Class<?>> _accessorStack = new ArrayList(3);
    private int _localReferenceCounter = 0;
    private Map<String, LocalReference> _localReferenceMap = null;

    public OgnlContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess) {
        this(memberAccess, classResolver, typeConverter, new HashMap(23));
    }

    public OgnlContext(MemberAccess memberAccess, ClassResolver classResolver, TypeConverter typeConverter, Map values) {
        this._values = values != null ? values : new HashMap(23);
        this._classResolver = classResolver != null ? classResolver : new DefaultClassResolver();
        this._typeConverter = typeConverter != null ? typeConverter : new DefaultTypeConverter();
        if (memberAccess == null) {
            throw new IllegalArgumentException("MemberAccess implementation must be provided - null not permitted!");
        }
        this._memberAccess = memberAccess;
    }

    public void setValues(Map value) {
        for (Object k : value.keySet()) {
            this._values.put(k, value.get(k));
        }
    }

    public Map getValues() {
        return this._values;
    }

    @Deprecated
    public void setClassResolver(ClassResolver ignore) {
    }

    public ClassResolver getClassResolver() {
        return this._classResolver;
    }

    @Deprecated
    public void setTypeConverter(TypeConverter ignore) {
    }

    public TypeConverter getTypeConverter() {
        return this._typeConverter;
    }

    @Deprecated
    public void setMemberAccess(MemberAccess ignore) {
    }

    public MemberAccess getMemberAccess() {
        return this._memberAccess;
    }

    public void setRoot(Object value) {
        this._root = value;
        this._accessorStack.clear();
        this._typeStack.clear();
        this._currentObject = value;
        if (this._currentObject != null) {
            this.setCurrentType(this._currentObject.getClass());
        }
    }

    public Object getRoot() {
        return this._root;
    }

    public boolean getTraceEvaluations() {
        return this._traceEvaluations;
    }

    public void setTraceEvaluations(boolean value) {
        this._traceEvaluations = value;
    }

    public Evaluation getLastEvaluation() {
        return this._lastEvaluation;
    }

    public void setLastEvaluation(Evaluation value) {
        this._lastEvaluation = value;
    }

    @Deprecated
    public void recycleLastEvaluation() {
        OgnlRuntime.getEvaluationPool().recycleAll(this._lastEvaluation);
        this._lastEvaluation = null;
    }

    public boolean getKeepLastEvaluation() {
        return this._keepLastEvaluation;
    }

    public void setKeepLastEvaluation(boolean value) {
        this._keepLastEvaluation = value;
    }

    public void setCurrentObject(Object value) {
        this._currentObject = value;
    }

    public Object getCurrentObject() {
        return this._currentObject;
    }

    public void setCurrentAccessor(Class type) {
        this._accessorStack.add(type);
    }

    public Class getCurrentAccessor() {
        if (this._accessorStack.isEmpty()) {
            return null;
        }
        return this._accessorStack.get(this._accessorStack.size() - 1);
    }

    public Class getPreviousAccessor() {
        if (this._accessorStack.isEmpty()) {
            return null;
        }
        if (this._accessorStack.size() > 1) {
            return this._accessorStack.get(this._accessorStack.size() - 2);
        }
        return null;
    }

    public Class getFirstAccessor() {
        if (this._accessorStack.isEmpty()) {
            return null;
        }
        return this._accessorStack.get(0);
    }

    public Class getCurrentType() {
        if (this._typeStack.isEmpty()) {
            return null;
        }
        return this._typeStack.get(this._typeStack.size() - 1);
    }

    public void setCurrentType(Class type) {
        this._typeStack.add(type);
    }

    public Class getPreviousType() {
        if (this._typeStack.isEmpty()) {
            return null;
        }
        if (this._typeStack.size() > 1) {
            return this._typeStack.get(this._typeStack.size() - 2);
        }
        return null;
    }

    public void setPreviousType(Class type) {
        if (this._typeStack.isEmpty() || this._typeStack.size() < 2) {
            return;
        }
        this._typeStack.set(this._typeStack.size() - 2, type);
    }

    public Class getFirstType() {
        if (this._typeStack.isEmpty()) {
            return null;
        }
        return this._typeStack.get(0);
    }

    public void setCurrentNode(Node value) {
        this._currentNode = value;
    }

    public Node getCurrentNode() {
        return this._currentNode;
    }

    public Evaluation getCurrentEvaluation() {
        return this._currentEvaluation;
    }

    public void setCurrentEvaluation(Evaluation value) {
        this._currentEvaluation = value;
    }

    public Evaluation getRootEvaluation() {
        return this._rootEvaluation;
    }

    public void setRootEvaluation(Evaluation value) {
        this._rootEvaluation = value;
    }

    public Evaluation getEvaluation(int relativeIndex) {
        Evaluation result = null;
        if (relativeIndex <= 0) {
            for (result = this._currentEvaluation; ++relativeIndex < 0 && result != null; result = result.getParent()) {
            }
        }
        return result;
    }

    public void pushEvaluation(Evaluation value) {
        if (this._currentEvaluation != null) {
            this._currentEvaluation.addChild(value);
        } else {
            this.setRootEvaluation(value);
        }
        this.setCurrentEvaluation(value);
    }

    public Evaluation popEvaluation() {
        Evaluation result = this._currentEvaluation;
        this.setCurrentEvaluation(result.getParent());
        if (this._currentEvaluation == null) {
            this.setLastEvaluation(this.getKeepLastEvaluation() ? result : null);
            this.setRootEvaluation(null);
            this.setCurrentNode(null);
        }
        return result;
    }

    public int incrementLocalReferenceCounter() {
        return ++this._localReferenceCounter;
    }

    public void addLocalReference(String key, LocalReference reference) {
        if (this._localReferenceMap == null) {
            this._localReferenceMap = new LinkedHashMap<String, LocalReference>();
        }
        this._localReferenceMap.put(key, reference);
    }

    public Map getLocalReferences() {
        return this._localReferenceMap;
    }

    @Override
    public int size() {
        return this._values.size();
    }

    @Override
    public boolean isEmpty() {
        return this._values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this._values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this._values.containsValue(value);
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Object get(Object key) {
        void var2_8;
        if (RESERVED_KEYS.containsKey(key)) {
            if (key.equals(THIS_CONTEXT_KEY)) {
                Object object = this.getCurrentObject();
                return var2_8;
            } else if (key.equals(ROOT_CONTEXT_KEY)) {
                Object object = this.getRoot();
                return var2_8;
            } else if (key.equals(TRACE_EVALUATIONS_CONTEXT_KEY)) {
                Boolean bl = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
                return var2_8;
            } else if (key.equals(LAST_EVALUATION_CONTEXT_KEY)) {
                Evaluation evaluation = this.getLastEvaluation();
                return var2_8;
            } else {
                if (!key.equals(KEEP_LAST_EVALUATION_CONTEXT_KEY)) throw new IllegalArgumentException("unknown reserved key '" + key + "'");
                Boolean bl = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
            }
            return var2_8;
        } else {
            Object v = this._values.get(key);
        }
        return var2_8;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Object put(Object key, Object value) {
        Object result;
        if (!RESERVED_KEYS.containsKey(key)) return this._values.put(key, value);
        if (key.equals(THIS_CONTEXT_KEY)) {
            result = this.getCurrentObject();
            this.setCurrentObject(value);
            return result;
        } else if (key.equals(ROOT_CONTEXT_KEY)) {
            result = this.getRoot();
            this.setRoot(value);
            return result;
        } else if (key.equals(TRACE_EVALUATIONS_CONTEXT_KEY)) {
            result = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
            this.setTraceEvaluations(OgnlOps.booleanValue(value));
            return result;
        } else if (key.equals(LAST_EVALUATION_CONTEXT_KEY)) {
            result = this.getLastEvaluation();
            this._lastEvaluation = (Evaluation)value;
            return result;
        } else {
            if (!key.equals(KEEP_LAST_EVALUATION_CONTEXT_KEY)) throw new IllegalArgumentException("unknown reserved key '" + key + "'");
            result = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
            this.setKeepLastEvaluation(OgnlOps.booleanValue(value));
        }
        return result;
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public Object remove(Object key) {
        void var2_6;
        if (!RESERVED_KEYS.containsKey(key)) {
            Object v = this._values.remove(key);
            return var2_6;
        }
        if (key.equals(THIS_CONTEXT_KEY)) {
            Object object = this.getCurrentObject();
            this.setCurrentObject(null);
            return var2_6;
        }
        if (key.equals(ROOT_CONTEXT_KEY)) {
            Object object = this.getRoot();
            this.setRoot(null);
            return var2_6;
        }
        if (key.equals(TRACE_EVALUATIONS_CONTEXT_KEY)) {
            throw new IllegalArgumentException("can't remove _traceEvaluations from context");
        }
        if (key.equals(LAST_EVALUATION_CONTEXT_KEY)) {
            Evaluation evaluation = this._lastEvaluation;
            this.setLastEvaluation(null);
            return var2_6;
        }
        if (!key.equals(KEEP_LAST_EVALUATION_CONTEXT_KEY)) throw new IllegalArgumentException("unknown reserved key '" + key + "'");
        throw new IllegalArgumentException("can't remove _keepLastEvaluation from context");
    }

    public void putAll(Map t) {
        for (Object k : t.keySet()) {
            this.put(k, t.get(k));
        }
    }

    @Override
    public void clear() {
        this._values.clear();
        this._typeStack.clear();
        this._accessorStack.clear();
        this._localReferenceCounter = 0;
        if (this._localReferenceMap != null) {
            this._localReferenceMap.clear();
        }
        this.setRoot(null);
        this.setCurrentObject(null);
        this.setRootEvaluation(null);
        this.setCurrentEvaluation(null);
        this.setLastEvaluation(null);
        this.setCurrentNode(null);
    }

    public Set keySet() {
        return this._values.keySet();
    }

    public Collection values() {
        return this._values.values();
    }

    public Set entrySet() {
        return this._values.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this._values.equals(o);
    }

    @Override
    public int hashCode() {
        return this._values.hashCode();
    }

    static {
        RESERVED_KEYS.put(ROOT_CONTEXT_KEY, null);
        RESERVED_KEYS.put(THIS_CONTEXT_KEY, null);
        RESERVED_KEYS.put(TRACE_EVALUATIONS_CONTEXT_KEY, null);
        RESERVED_KEYS.put(LAST_EVALUATION_CONTEXT_KEY, null);
        RESERVED_KEYS.put(KEEP_LAST_EVALUATION_CONTEXT_KEY, null);
        try {
            String s = System.getProperty("ognl.traceEvaluations");
            if (s != null) {
                DEFAULT_TRACE_EVALUATIONS = Boolean.valueOf(s.trim());
            }
            if ((s = System.getProperty("ognl.keepLastEvaluation")) != null) {
                DEFAULT_KEEP_LAST_EVALUATION = Boolean.valueOf(s.trim());
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }
}

