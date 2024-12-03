/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import software.amazon.ion.IonText;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

abstract class IonTextLite
extends IonValueLite
implements IonText {
    private String _text_value;

    protected IonTextLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonTextLite(IonTextLite existing, IonContext context) {
        super(existing, context);
        this._text_value = existing._text_value;
    }

    public abstract IonTextLite clone();

    public void setValue(String value) {
        this.checkForLock();
        this._set_value(value);
    }

    protected final String _get_value() {
        return this._text_value;
    }

    public String stringValue() {
        return this._text_value;
    }

    protected final void _set_value(String value) {
        this._text_value = value;
        this._isNullValue(value == null);
    }
}

