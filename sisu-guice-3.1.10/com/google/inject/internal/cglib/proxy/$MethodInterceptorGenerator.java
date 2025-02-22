/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$ClassInfo;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$CollectionUtils;
import com.google.inject.internal.cglib.core.$Constants;
import com.google.inject.internal.cglib.core.$EmitUtils;
import com.google.inject.internal.cglib.core.$Local;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$ObjectSwitchCallback;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.cglib.core.$Transformer;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.proxy.$CallbackGenerator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class $MethodInterceptorGenerator
implements $CallbackGenerator {
    public static final $MethodInterceptorGenerator INSTANCE = new $MethodInterceptorGenerator();
    static final String EMPTY_ARGS_NAME = "CGLIB$emptyArgs";
    static final String FIND_PROXY_NAME = "CGLIB$findMethodProxy";
    static final Class[] FIND_PROXY_TYPES = new Class[]{class$net$sf$cglib$core$Signature == null ? (class$net$sf$cglib$core$Signature = $MethodInterceptorGenerator.class$("com.google.inject.internal.cglib.core.$Signature")) : class$net$sf$cglib$core$Signature};
    private static final $Type ABSTRACT_METHOD_ERROR = $TypeUtils.parseType("AbstractMethodError");
    private static final $Type METHOD = $TypeUtils.parseType("java.lang.reflect.Method");
    private static final $Type REFLECT_UTILS = $TypeUtils.parseType("com.google.inject.internal.cglib.core.$ReflectUtils");
    private static final $Type METHOD_PROXY = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$MethodProxy");
    private static final $Type METHOD_INTERCEPTOR = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$MethodInterceptor");
    private static final $Signature GET_DECLARED_METHODS = $TypeUtils.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
    private static final $Signature GET_DECLARING_CLASS = $TypeUtils.parseSignature("Class getDeclaringClass()");
    private static final $Signature FIND_METHODS = $TypeUtils.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
    private static final $Signature MAKE_PROXY = new $Signature("create", METHOD_PROXY, new $Type[]{$Constants.TYPE_CLASS, $Constants.TYPE_CLASS, $Constants.TYPE_STRING, $Constants.TYPE_STRING, $Constants.TYPE_STRING});
    private static final $Signature INTERCEPT = new $Signature("intercept", $Constants.TYPE_OBJECT, new $Type[]{$Constants.TYPE_OBJECT, METHOD, $Constants.TYPE_OBJECT_ARRAY, METHOD_PROXY});
    private static final $Signature FIND_PROXY = new $Signature("CGLIB$findMethodProxy", METHOD_PROXY, new $Type[]{$Constants.TYPE_SIGNATURE});
    private static final $Signature TO_STRING = $TypeUtils.parseSignature("String toString()");
    private static final $Transformer METHOD_TO_CLASS = new $Transformer(){

        public Object transform(Object value) {
            return (($MethodInfo)value).getClassInfo();
        }
    };
    private static final $Signature CSTRUCT_SIGNATURE = $TypeUtils.parseConstructor("String, String");
    static /* synthetic */ Class class$net$sf$cglib$core$Signature;

    $MethodInterceptorGenerator() {
    }

    private String getMethodField($Signature impl) {
        return impl.getName() + "$Method";
    }

    private String getMethodProxyField($Signature impl) {
        return impl.getName() + "$Proxy";
    }

    public void generate($ClassEmitter ce, $CallbackGenerator.Context context, List methods) {
        HashMap<String, String> sigMap = new HashMap<String, String>();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            $MethodInfo method = ($MethodInfo)it.next();
            $Signature sig = method.getSignature();
            $Signature impl = context.getImplSignature(method);
            String methodField = this.getMethodField(impl);
            String methodProxyField = this.getMethodProxyField(impl);
            sigMap.put(sig.toString(), methodProxyField);
            ce.declare_field(26, methodField, METHOD, null);
            ce.declare_field(26, methodProxyField, METHOD_PROXY, null);
            ce.declare_field(26, EMPTY_ARGS_NAME, $Constants.TYPE_OBJECT_ARRAY, null);
            $CodeEmitter e = ce.begin_method(16, impl, method.getExceptionTypes());
            $MethodInterceptorGenerator.superHelper(e, method, context);
            e.return_value();
            e.end_method();
            e = context.beginMethod(ce, method);
            $Label nullInterceptor = e.make_label();
            context.emitCallback(e, context.getIndex(method));
            e.dup();
            e.ifnull(nullInterceptor);
            e.load_this();
            e.getfield(methodField);
            if (sig.getArgumentTypes().length == 0) {
                e.getfield(EMPTY_ARGS_NAME);
            } else {
                e.create_arg_array();
            }
            e.getfield(methodProxyField);
            e.invoke_interface(METHOD_INTERCEPTOR, INTERCEPT);
            e.unbox_or_zero(sig.getReturnType());
            e.return_value();
            e.mark(nullInterceptor);
            $MethodInterceptorGenerator.superHelper(e, method, context);
            e.return_value();
            e.end_method();
        }
        this.generateFindProxy(ce, sigMap);
    }

    private static void superHelper($CodeEmitter e, $MethodInfo method, $CallbackGenerator.Context context) {
        if ($TypeUtils.isAbstract(method.getModifiers())) {
            e.throw_exception(ABSTRACT_METHOD_ERROR, method.toString() + " is abstract");
        } else {
            e.load_this();
            e.load_args();
            context.emitInvoke(e, method);
        }
    }

    public void generateStatic($CodeEmitter e, $CallbackGenerator.Context context, List methods) throws Exception {
        e.push(0);
        e.newarray();
        e.putfield(EMPTY_ARGS_NAME);
        $Local thisclass = e.make_local();
        $Local declaringclass = e.make_local();
        $EmitUtils.load_class_this(e);
        e.store_local(thisclass);
        Map methodsByClass = $CollectionUtils.bucket(methods, METHOD_TO_CLASS);
        Iterator i = methodsByClass.keySet().iterator();
        while (i.hasNext()) {
            $Signature sig;
            $MethodInfo method;
            int index;
            $ClassInfo classInfo = ($ClassInfo)i.next();
            List classMethods = (List)methodsByClass.get(classInfo);
            e.push(2 * classMethods.size());
            e.newarray($Constants.TYPE_STRING);
            for (index = 0; index < classMethods.size(); ++index) {
                method = ($MethodInfo)classMethods.get(index);
                sig = method.getSignature();
                e.dup();
                e.push(2 * index);
                e.push(sig.getName());
                e.aastore();
                e.dup();
                e.push(2 * index + 1);
                e.push(sig.getDescriptor());
                e.aastore();
            }
            $EmitUtils.load_class(e, classInfo.getType());
            e.dup();
            e.store_local(declaringclass);
            e.invoke_virtual($Constants.TYPE_CLASS, GET_DECLARED_METHODS);
            e.invoke_static(REFLECT_UTILS, FIND_METHODS);
            for (index = 0; index < classMethods.size(); ++index) {
                method = ($MethodInfo)classMethods.get(index);
                sig = method.getSignature();
                $Signature impl = context.getImplSignature(method);
                e.dup();
                e.push(index);
                e.array_load(METHOD);
                e.putfield(this.getMethodField(impl));
                e.load_local(declaringclass);
                e.load_local(thisclass);
                e.push(sig.getDescriptor());
                e.push(sig.getName());
                e.push(impl.getName());
                e.invoke_static(METHOD_PROXY, MAKE_PROXY);
                e.putfield(this.getMethodProxyField(impl));
            }
            e.pop();
        }
    }

    public void generateFindProxy($ClassEmitter ce, final Map sigMap) {
        final $CodeEmitter e = ce.begin_method(9, FIND_PROXY, null);
        e.load_arg(0);
        e.invoke_virtual($Constants.TYPE_OBJECT, TO_STRING);
        $ObjectSwitchCallback callback = new $ObjectSwitchCallback(){

            public void processCase(Object key, $Label end) {
                e.getfield((String)sigMap.get(key));
                e.return_value();
            }

            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        };
        $EmitUtils.string_switch(e, sigMap.keySet().toArray(new String[0]), 1, callback);
        e.end_method();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

