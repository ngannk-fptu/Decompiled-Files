/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.css.extend.ContentFunction;
import org.xhtmlrenderer.css.parser.FSFunction;

public class FunctionData {
    private ContentFunction _contentFunction;
    private FSFunction _function;

    public FunctionData() {
    }

    public FunctionData(ContentFunction contentFunction, FSFunction function) {
        this._contentFunction = contentFunction;
        this._function = function;
    }

    public ContentFunction getContentFunction() {
        return this._contentFunction;
    }

    public void setContentFunction(ContentFunction contentFunction) {
        this._contentFunction = contentFunction;
    }

    public FSFunction getFunction() {
        return this._function;
    }

    public void setFunction(FSFunction function) {
        this._function = function;
    }
}

