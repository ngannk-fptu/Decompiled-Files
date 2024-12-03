/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import org.mozilla.javascript.AbstractEcmaObjectOperations;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Constructable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.IteratorLikeIterable;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.LambdaConstructor;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

public class NativePromise
extends ScriptableObject {
    private State state = State.PENDING;
    private Object result = null;
    private boolean handled = false;
    private ArrayList<Reaction> fulfillReactions = new ArrayList();
    private ArrayList<Reaction> rejectReactions = new ArrayList();

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        LambdaConstructor constructor = new LambdaConstructor(scope, "Promise", 1, 2, NativePromise::constructor);
        constructor.setStandardPropertyAttributes(3);
        constructor.setPrototypePropertyAttributes(7);
        constructor.defineConstructorMethod(scope, "resolve", 1, NativePromise::resolve, 2, 3);
        constructor.defineConstructorMethod(scope, "reject", 1, NativePromise::reject, 2, 3);
        constructor.defineConstructorMethod(scope, "all", 1, NativePromise::all, 2, 3);
        constructor.defineConstructorMethod(scope, "race", 1, NativePromise::race, 2, 3);
        ScriptableObject speciesDescriptor = (ScriptableObject)cx.newObject(scope);
        ScriptableObject.putProperty((Scriptable)speciesDescriptor, "enumerable", (Object)false);
        ScriptableObject.putProperty((Scriptable)speciesDescriptor, "configurable", (Object)true);
        ScriptableObject.putProperty((Scriptable)speciesDescriptor, "get", (Object)new LambdaFunction(scope, "get [Symbol.species]", 0, (lcx, lscope, thisObj, args) -> constructor));
        constructor.defineOwnProperty(cx, SymbolKey.SPECIES, speciesDescriptor, false);
        constructor.definePrototypeMethod(scope, "then", 2, (lcx, lscope, thisObj, args) -> {
            NativePromise self = LambdaConstructor.convertThisObject(thisObj, NativePromise.class);
            return self.then(lcx, lscope, constructor, args);
        }, 2, 3);
        constructor.definePrototypeMethod(scope, "catch", 1, NativePromise::doCatch, 2, 3);
        constructor.definePrototypeMethod(scope, "finally", 1, (lcx, lscope, thisObj, args) -> NativePromise.doFinally(lcx, lscope, thisObj, constructor, args), 2, 3);
        constructor.definePrototypeProperty(SymbolKey.TO_STRING_TAG, (Object)"Promise", 3);
        ScriptableObject.defineProperty(scope, "Promise", constructor, 2);
        if (sealed) {
            constructor.sealObject();
        }
    }

    private static Scriptable constructor(Context cx, Scriptable scope, Object[] args) {
        Scriptable tcs;
        if (args.length < 1 || !(args[0] instanceof Callable)) {
            throw ScriptRuntime.typeErrorById("msg.function.expected", new Object[0]);
        }
        Callable executor = (Callable)args[0];
        NativePromise promise = new NativePromise();
        ResolvingFunctions resolving = new ResolvingFunctions(scope, promise);
        Scriptable thisObj = Undefined.SCRIPTABLE_UNDEFINED;
        if (!cx.isStrictMode() && (tcs = cx.topCallScope) != null) {
            thisObj = tcs;
        }
        try {
            executor.call(cx, scope, thisObj, new Object[]{resolving.resolve, resolving.reject});
        }
        catch (RhinoException re) {
            resolving.reject.call(cx, scope, thisObj, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
        }
        return promise;
    }

    @Override
    public String getClassName() {
        return "Promise";
    }

    Object getResult() {
        return this.result;
    }

    private static Object resolve(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!ScriptRuntime.isObject(thisObj)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(thisObj));
        }
        Object arg = args.length > 0 ? args[0] : Undefined.instance;
        return NativePromise.resolveInternal(cx, scope, thisObj, arg);
    }

    private static Object resolveInternal(Context cx, Scriptable scope, Object constructor, Object arg) {
        Object argConstructor;
        if (arg instanceof NativePromise && (argConstructor = ScriptRuntime.getObjectProp(arg, "constructor", cx, scope)) == constructor) {
            return arg;
        }
        Capability cap = new Capability(cx, scope, constructor);
        cap.resolve.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{arg});
        return cap.promise;
    }

    private static Object reject(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!ScriptRuntime.isObject(thisObj)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(thisObj));
        }
        Object arg = args.length > 0 ? args[0] : Undefined.instance;
        Capability cap = new Capability(cx, scope, thisObj);
        cap.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{arg});
        return cap.promise;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object all(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        IteratorLikeIterable iterable;
        Capability cap = new Capability(cx, scope, thisObj);
        Object arg = args.length > 0 ? args[0] : Undefined.instance;
        try {
            Object maybeIterable = ScriptRuntime.callIterator(arg, cx, scope);
            iterable = new IteratorLikeIterable(cx, scope, maybeIterable);
        }
        catch (RhinoException re) {
            cap.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
            return cap.promise;
        }
        IteratorLikeIterable.Itr iterator = iterable.iterator();
        PromiseAllResolver resolver = new PromiseAllResolver(iterator, thisObj, cap);
        try {
            Object object = resolver.resolve(cx, scope);
            if (!iterator.isDone()) {
                iterable.close();
            }
            return object;
        }
        catch (Throwable throwable) {
            try {
                if (!iterator.isDone()) {
                    iterable.close();
                }
                throw throwable;
            }
            catch (RhinoException re) {
                cap.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
                return cap.promise;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object race(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        IteratorLikeIterable iterable;
        Capability cap = new Capability(cx, scope, thisObj);
        Object arg = args.length > 0 ? args[0] : Undefined.instance;
        try {
            Object maybeIterable = ScriptRuntime.callIterator(arg, cx, scope);
            iterable = new IteratorLikeIterable(cx, scope, maybeIterable);
        }
        catch (RhinoException re) {
            cap.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
            return cap.promise;
        }
        IteratorLikeIterable.Itr iterator = iterable.iterator();
        try {
            Object object = NativePromise.performRace(cx, scope, iterator, thisObj, cap);
            if (!iterator.isDone()) {
                iterable.close();
            }
            return object;
        }
        catch (Throwable throwable) {
            try {
                if (!iterator.isDone()) {
                    iterable.close();
                }
                throw throwable;
            }
            catch (RhinoException re) {
                cap.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
                return cap.promise;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object performRace(Context cx, Scriptable scope, IteratorLikeIterable.Itr iterator, Scriptable thisObj, Capability cap) {
        Callable resolve = ScriptRuntime.getPropFunctionAndThis(thisObj, "resolve", cx, scope);
        Scriptable localThis = ScriptRuntime.lastStoredScriptable(cx);
        while (true) {
            boolean hasNext;
            Object nextVal = Undefined.instance;
            boolean nextOk = false;
            try {
                hasNext = iterator.hasNext();
                if (hasNext) {
                    nextVal = iterator.next();
                }
                nextOk = true;
            }
            finally {
                if (!nextOk) {
                    iterator.setDone(true);
                }
            }
            if (!hasNext) {
                return cap.promise;
            }
            Object nextPromise = resolve.call(cx, scope, localThis, new Object[]{nextVal});
            Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(nextPromise, "then", cx, scope);
            thenFunc.call(cx, scope, ScriptRuntime.lastStoredScriptable(cx), new Object[]{cap.resolve, cap.reject});
        }
    }

    private Object then(Context cx, Scriptable scope, LambdaConstructor defaultConstructor, Object[] args) {
        Constructable constructable = AbstractEcmaObjectOperations.speciesConstructor(cx, this, defaultConstructor);
        Capability capability = new Capability(cx, scope, constructable);
        Callable onFulfilled = null;
        if (args.length >= 1 && args[0] instanceof Callable) {
            onFulfilled = (Callable)args[0];
        }
        Callable onRejected = null;
        if (args.length >= 2 && args[1] instanceof Callable) {
            onRejected = (Callable)args[1];
        }
        Reaction fulfillReaction = new Reaction(capability, ReactionType.FULFILL, onFulfilled);
        Reaction rejectReaction = new Reaction(capability, ReactionType.REJECT, onRejected);
        if (this.state == State.PENDING) {
            this.fulfillReactions.add(fulfillReaction);
            this.rejectReactions.add(rejectReaction);
        } else if (this.state == State.FULFILLED) {
            cx.enqueueMicrotask(() -> fulfillReaction.invoke(cx, scope, this.result));
        } else {
            assert (this.state == State.REJECTED);
            if (!this.handled) {
                cx.getUnhandledPromiseTracker().promiseHandled(this);
            }
            cx.enqueueMicrotask(() -> rejectReaction.invoke(cx, scope, this.result));
        }
        this.handled = true;
        return capability.promise;
    }

    private static Object doCatch(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Object arg = args.length > 0 ? args[0] : Undefined.instance;
        Scriptable coercedThis = ScriptRuntime.toObject(cx, scope, thisObj);
        Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(coercedThis, "then", cx, scope);
        return thenFunc.call(cx, scope, ScriptRuntime.lastStoredScriptable(cx), new Object[]{Undefined.instance, arg});
    }

    private static Object doFinally(Context cx, Scriptable scope, Scriptable thisObj, LambdaConstructor defaultConstructor, Object[] args) {
        if (!ScriptRuntime.isObject(thisObj)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(thisObj));
        }
        Scriptable onFinally = args.length > 0 ? args[0] : Undefined.SCRIPTABLE_UNDEFINED;
        Object thenFinally = onFinally;
        Object catchFinally = onFinally;
        Constructable constructor = AbstractEcmaObjectOperations.speciesConstructor(cx, thisObj, defaultConstructor);
        if (onFinally instanceof Callable) {
            Callable callableOnFinally = (Callable)thenFinally;
            thenFinally = NativePromise.makeThenFinally(scope, constructor, callableOnFinally);
            catchFinally = NativePromise.makeCatchFinally(scope, constructor, callableOnFinally);
        }
        Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(thisObj, "then", cx, scope);
        Scriptable to = ScriptRuntime.lastStoredScriptable(cx);
        return thenFunc.call(cx, scope, to, new Object[]{thenFinally, catchFinally});
    }

    private static Callable makeThenFinally(Scriptable scope, Object constructor, Callable onFinally) {
        return new LambdaFunction(scope, 1, (cx, ls, thisObj, args) -> {
            Object value = args.length > 0 ? args[0] : Undefined.instance;
            LambdaFunction valueThunk = new LambdaFunction(scope, 0, (vc, vs, vt, va) -> value);
            Object result = onFinally.call(cx, ls, Undefined.SCRIPTABLE_UNDEFINED, ScriptRuntime.emptyArgs);
            Object promise = NativePromise.resolveInternal(cx, scope, constructor, result);
            Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(promise, "then", cx, scope);
            return thenFunc.call(cx, scope, ScriptRuntime.lastStoredScriptable(cx), new Object[]{valueThunk});
        });
    }

    private static Callable makeCatchFinally(Scriptable scope, Object constructor, Callable onFinally) {
        return new LambdaFunction(scope, 1, (cx, ls, thisObj, args) -> {
            Object reason = args.length > 0 ? args[0] : Undefined.instance;
            LambdaFunction reasonThrower = new LambdaFunction(scope, 0, (vc, vs, vt, va) -> {
                throw new JavaScriptException(reason, null, 0);
            });
            Object result = onFinally.call(cx, ls, Undefined.SCRIPTABLE_UNDEFINED, ScriptRuntime.emptyArgs);
            Object promise = NativePromise.resolveInternal(cx, scope, constructor, result);
            Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(promise, "then", cx, scope);
            return thenFunc.call(cx, scope, ScriptRuntime.lastStoredScriptable(cx), new Object[]{reasonThrower});
        });
    }

    private Object fulfillPromise(Context cx, Scriptable scope, Object value) {
        assert (this.state == State.PENDING);
        this.result = value;
        ArrayList<Reaction> reactions = this.fulfillReactions;
        this.fulfillReactions = new ArrayList();
        if (!this.rejectReactions.isEmpty()) {
            this.rejectReactions = new ArrayList();
        }
        this.state = State.FULFILLED;
        for (Reaction r : reactions) {
            cx.enqueueMicrotask(() -> r.invoke(cx, scope, value));
        }
        return Undefined.instance;
    }

    private Object rejectPromise(Context cx, Scriptable scope, Object reason) {
        assert (this.state == State.PENDING);
        this.result = reason;
        ArrayList<Reaction> reactions = this.rejectReactions;
        this.rejectReactions = new ArrayList();
        if (!this.fulfillReactions.isEmpty()) {
            this.fulfillReactions = new ArrayList();
        }
        this.state = State.REJECTED;
        cx.getUnhandledPromiseTracker().promiseRejected(this);
        for (Reaction r : reactions) {
            cx.enqueueMicrotask(() -> r.invoke(cx, scope, reason));
        }
        return Undefined.instance;
    }

    private void callThenable(Context cx, Scriptable scope, Object resolution, Callable thenFunc) {
        ResolvingFunctions resolving = new ResolvingFunctions(scope, this);
        Scriptable thisObj = resolution instanceof Scriptable ? (Scriptable)resolution : Undefined.SCRIPTABLE_UNDEFINED;
        try {
            thenFunc.call(cx, scope, thisObj, new Object[]{resolving.resolve, resolving.reject});
        }
        catch (RhinoException re) {
            resolving.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
        }
    }

    private static Object getErrorObject(Context cx, Scriptable scope, RhinoException re) {
        if (re instanceof JavaScriptException) {
            return ((JavaScriptException)re).getValue();
        }
        TopLevel.NativeErrors constructor = TopLevel.NativeErrors.Error;
        if (re instanceof EcmaError) {
            EcmaError ee = (EcmaError)re;
            switch (ee.getName()) {
                case "EvalError": {
                    constructor = TopLevel.NativeErrors.EvalError;
                    break;
                }
                case "RangeError": {
                    constructor = TopLevel.NativeErrors.RangeError;
                    break;
                }
                case "ReferenceError": {
                    constructor = TopLevel.NativeErrors.ReferenceError;
                    break;
                }
                case "SyntaxError": {
                    constructor = TopLevel.NativeErrors.SyntaxError;
                    break;
                }
                case "TypeError": {
                    constructor = TopLevel.NativeErrors.TypeError;
                    break;
                }
                case "URIError": {
                    constructor = TopLevel.NativeErrors.URIError;
                    break;
                }
                case "InternalError": {
                    constructor = TopLevel.NativeErrors.InternalError;
                    break;
                }
                case "JavaException": {
                    constructor = TopLevel.NativeErrors.JavaException;
                    break;
                }
            }
        }
        return ScriptRuntime.newNativeError(cx, scope, constructor, new Object[]{re.getMessage()});
    }

    private static class PromiseElementResolver {
        private boolean alreadyCalled = false;
        private final int index;

        PromiseElementResolver(int ix) {
            this.index = ix;
        }

        Object resolve(Context cx, Scriptable scope, Object result, PromiseAllResolver resolver) {
            if (this.alreadyCalled) {
                return Undefined.instance;
            }
            this.alreadyCalled = true;
            resolver.values.set(this.index, result);
            if (--resolver.remainingElements == 0) {
                resolver.finalResolution(cx, scope);
            }
            return Undefined.instance;
        }
    }

    private static class PromiseAllResolver {
        private static final int MAX_PROMISES = 0x200000;
        final ArrayList<Object> values = new ArrayList();
        int remainingElements = 1;
        IteratorLikeIterable.Itr iterator;
        Scriptable thisObj;
        Capability capability;

        PromiseAllResolver(IteratorLikeIterable.Itr iter, Scriptable thisObj, Capability cap) {
            this.iterator = iter;
            this.thisObj = thisObj;
            this.capability = cap;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Object resolve(Context topCx, Scriptable topScope) {
            int index = 0;
            Callable resolve = ScriptRuntime.getPropFunctionAndThis(this.thisObj, "resolve", topCx, topScope);
            Scriptable storedThis = ScriptRuntime.lastStoredScriptable(topCx);
            while (true) {
                boolean hasNext;
                if (index == 0x200000) {
                    throw ScriptRuntime.rangeErrorById("msg.promise.all.toobig", new Object[0]);
                }
                Object nextVal = Undefined.instance;
                boolean nextOk = false;
                try {
                    hasNext = this.iterator.hasNext();
                    if (hasNext) {
                        nextVal = this.iterator.next();
                    }
                    nextOk = true;
                }
                finally {
                    if (!nextOk) {
                        this.iterator.setDone(true);
                    }
                }
                if (!hasNext) {
                    if (--this.remainingElements == 0) {
                        this.finalResolution(topCx, topScope);
                    }
                    return this.capability.promise;
                }
                this.values.add(Undefined.instance);
                Object nextPromise = resolve.call(topCx, topScope, storedThis, new Object[]{nextVal});
                PromiseElementResolver eltResolver = new PromiseElementResolver(index);
                LambdaFunction resolveFunc = new LambdaFunction(topScope, 1, (cx, scope, thisObj, args) -> eltResolver.resolve(cx, scope, args.length > 0 ? args[0] : Undefined.instance, this));
                resolveFunc.setStandardPropertyAttributes(3);
                ++this.remainingElements;
                Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(nextPromise, "then", topCx, topScope);
                thenFunc.call(topCx, topScope, ScriptRuntime.lastStoredScriptable(topCx), new Object[]{resolveFunc, this.capability.reject});
                ++index;
            }
        }

        void finalResolution(Context cx, Scriptable scope) {
            Scriptable newArray = cx.newArray(scope, this.values.toArray());
            this.capability.resolve.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{newArray});
        }
    }

    private static class Capability {
        Object promise;
        private Object rawResolve = Undefined.instance;
        Callable resolve;
        private Object rawReject = Undefined.instance;
        Callable reject;

        Capability(Context topCx, Scriptable topScope, Object pc) {
            if (!(pc instanceof Constructable)) {
                throw ScriptRuntime.typeErrorById("msg.constructor.expected", new Object[0]);
            }
            Constructable promiseConstructor = (Constructable)pc;
            LambdaFunction executorFunc = new LambdaFunction(topScope, 2, (cx, scope, thisObj, args) -> this.executor(args));
            executorFunc.setStandardPropertyAttributes(3);
            this.promise = promiseConstructor.construct(topCx, topScope, new Object[]{executorFunc});
            if (!(this.rawResolve instanceof Callable)) {
                throw ScriptRuntime.typeErrorById("msg.function.expected", new Object[0]);
            }
            this.resolve = (Callable)this.rawResolve;
            if (!(this.rawReject instanceof Callable)) {
                throw ScriptRuntime.typeErrorById("msg.function.expected", new Object[0]);
            }
            this.reject = (Callable)this.rawReject;
        }

        private Object executor(Object[] args) {
            if (!Undefined.isUndefined(this.rawResolve) || !Undefined.isUndefined(this.rawReject)) {
                throw ScriptRuntime.typeErrorById("msg.promise.capability.state", new Object[0]);
            }
            if (args.length > 0) {
                this.rawResolve = args[0];
            }
            if (args.length > 1) {
                this.rawReject = args[1];
            }
            return Undefined.instance;
        }
    }

    private static class Reaction {
        Capability capability;
        ReactionType reaction = ReactionType.REJECT;
        Callable handler;

        Reaction(Capability cap, ReactionType type, Callable handler) {
            this.capability = cap;
            this.reaction = type;
            this.handler = handler;
        }

        void invoke(Context cx, Scriptable scope, Object arg) {
            try {
                Object result = null;
                if (this.handler == null) {
                    switch (this.reaction) {
                        case FULFILL: {
                            result = arg;
                            break;
                        }
                        case REJECT: {
                            this.capability.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{arg});
                            return;
                        }
                    }
                } else {
                    result = this.handler.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{arg});
                }
                this.capability.resolve.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{result});
            }
            catch (RhinoException re) {
                this.capability.reject.call(cx, scope, Undefined.SCRIPTABLE_UNDEFINED, new Object[]{NativePromise.getErrorObject(cx, scope, re)});
            }
        }
    }

    private static class ResolvingFunctions {
        private boolean alreadyResolved = false;
        LambdaFunction resolve;
        LambdaFunction reject;

        ResolvingFunctions(Scriptable topScope, NativePromise promise) {
            this.resolve = new LambdaFunction(topScope, 1, (cx, scope, thisObj, args) -> this.resolve(cx, scope, promise, args.length > 0 ? args[0] : Undefined.instance));
            this.resolve.setStandardPropertyAttributes(3);
            this.reject = new LambdaFunction(topScope, 1, (cx, scope, thisObj, args) -> this.reject(cx, scope, promise, args.length > 0 ? args[0] : Undefined.instance));
            this.reject.setStandardPropertyAttributes(3);
        }

        private Object reject(Context cx, Scriptable scope, NativePromise promise, Object reason) {
            if (this.alreadyResolved) {
                return Undefined.instance;
            }
            this.alreadyResolved = true;
            return promise.rejectPromise(cx, scope, reason);
        }

        private Object resolve(Context cx, Scriptable scope, NativePromise promise, Object resolution) {
            if (this.alreadyResolved) {
                return Undefined.instance;
            }
            this.alreadyResolved = true;
            if (resolution == promise) {
                Scriptable err = ScriptRuntime.newNativeError(cx, scope, TopLevel.NativeErrors.TypeError, new Object[]{"No promise self-resolution"});
                return promise.rejectPromise(cx, scope, err);
            }
            if (!ScriptRuntime.isObject(resolution)) {
                return promise.fulfillPromise(cx, scope, resolution);
            }
            Scriptable sresolution = ScriptableObject.ensureScriptable(resolution);
            Object thenObj = ScriptableObject.getProperty(sresolution, "then");
            if (!(thenObj instanceof Callable)) {
                return promise.fulfillPromise(cx, scope, resolution);
            }
            cx.enqueueMicrotask(() -> promise.callThenable(cx, scope, resolution, (Callable)thenObj));
            return Undefined.instance;
        }
    }

    static enum ReactionType {
        FULFILL,
        REJECT;

    }

    static enum State {
        PENDING,
        FULFILLED,
        REJECTED;

    }
}

