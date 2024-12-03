/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

public abstract class _DelayedConversionToString {
    private static final String NOT_SET = new String();
    private Object object;
    private volatile String stringValue = NOT_SET;

    public _DelayedConversionToString(Object object) {
        this.object = object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        String stringValue = this.stringValue;
        if (stringValue == NOT_SET) {
            _DelayedConversionToString _DelayedConversionToString2 = this;
            synchronized (_DelayedConversionToString2) {
                stringValue = this.stringValue;
                if (stringValue == NOT_SET) {
                    this.stringValue = stringValue = this.doConversion(this.object);
                    this.object = null;
                }
            }
        }
        return stringValue;
    }

    protected abstract String doConversion(Object var1);
}

