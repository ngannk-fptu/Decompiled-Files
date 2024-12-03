/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;

public class ThrowableDeserializer
extends BeanDeserializer {
    private static final long serialVersionUID = 1L;
    protected static final String PROP_NAME_MESSAGE = "message";
    protected static final String PROP_NAME_SUPPRESSED = "suppressed";

    public ThrowableDeserializer(BeanDeserializer baseDeserializer) {
        super(baseDeserializer);
        this._vanillaProcessing = false;
    }

    protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper) {
        super((BeanDeserializerBase)src, unwrapper);
    }

    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        if (this.getClass() != ThrowableDeserializer.class) {
            return this;
        }
        return new ThrowableDeserializer(this, unwrapper);
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    @Override
    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._beanType.isAbstract()) {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "abstract type (need to add/enable type information?)", new Object[0]);
        }
        hasStringCreator = this._valueInstantiator.canCreateFromString();
        hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
        if (!hasStringCreator && !hasDefaultCtor) {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "Throwable needs a default constructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
        }
        throwable = null;
        pending = null;
        suppressed = null;
        pendingIx = 0;
        while (!p.hasToken(JsonToken.END_OBJECT)) {
            block20: {
                block21: {
                    block19: {
                        propName = p.currentName();
                        prop = this._beanProperties.find(propName);
                        p.nextToken();
                        if (prop == null) break block19;
                        if (throwable != null) {
                            prop.deserializeAndSet(p, ctxt, throwable);
                        } else {
                            if (pending == null) {
                                len = this._beanProperties.size();
                                pending = new Object[len + len];
                            }
                            pending[pendingIx++] = prop;
                            pending[pendingIx++] = prop.deserialize(p, ctxt);
                        }
                        break block20;
                    }
                    if (!"message".equals(propName)) break block21;
                    if (!hasStringCreator) ** GOTO lbl-1000
                    throwable = (Throwable)this._valueInstantiator.createFromString(ctxt, p.getValueAsString());
                    break block20;
                }
                if ("suppressed".equals(propName)) {
                    suppressed = ctxt.readValue(p, Throwable[].class);
                } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    p.skipChildren();
                } else if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
                } else {
                    this.handleUnknownProperty(p, ctxt, throwable, propName);
                }
            }
            p.nextToken();
        }
        if (throwable == null) {
            throwable = hasStringCreator != false ? (Throwable)this._valueInstantiator.createFromString(ctxt, null) : (Throwable)this._valueInstantiator.createUsingDefault(ctxt);
        }
        if (pending != null) {
            len = pendingIx;
            for (i = 0; i < len; i += 2) {
                prop = (SettableBeanProperty)pending[i];
                prop.set(throwable, pending[i + 1]);
            }
        }
        if (suppressed != null) {
            for (void s : suppressed) {
                throwable.addSuppressed((Throwable)s);
            }
        }
        return throwable;
    }
}

