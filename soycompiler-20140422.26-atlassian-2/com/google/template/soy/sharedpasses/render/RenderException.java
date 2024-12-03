/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses.render;

import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Iterator;
import java.util.LinkedList;

public class RenderException
extends RuntimeException {
    private SourceLocation partialStackTraceElement = null;
    private final LinkedList<StackTraceElement> soyStackTrace = new LinkedList();

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    RenderException addPartialStackTraceElement(SourceLocation srcLocation) {
        if (this.partialStackTraceElement != null) {
            this.soyStackTrace.add(new StackTraceElement("[Unknown]", "[Unknown]", this.partialStackTraceElement.getFileName(), this.partialStackTraceElement.getLineNumber()));
        }
        this.partialStackTraceElement = srcLocation;
        return this;
    }

    RenderException completeStackTraceElement(TemplateNode node) {
        if (this.partialStackTraceElement == null) {
            this.soyStackTrace.add(node.createStackTraceElement(SourceLocation.UNKNOWN));
        } else {
            this.soyStackTrace.add(node.createStackTraceElement(this.partialStackTraceElement));
        }
        this.partialStackTraceElement = null;
        return this;
    }

    public void finalizeStackTrace() {
        this.finalizeStackTrace(this);
    }

    public void finalizeStackTrace(Throwable t) {
        t.setStackTrace(this.concatWithJavaStackTrace(t.getStackTrace()));
    }

    private StackTraceElement[] concatWithJavaStackTrace(StackTraceElement[] javaStackTrace) {
        if (this.soyStackTrace.isEmpty()) {
            return javaStackTrace;
        }
        StackTraceElement[] finalStackTrace = new StackTraceElement[this.soyStackTrace.size() + javaStackTrace.length];
        int i = 0;
        Iterator iterator = this.soyStackTrace.iterator();
        while (iterator.hasNext()) {
            StackTraceElement soyStackTraceElement;
            finalStackTrace[i] = soyStackTraceElement = (StackTraceElement)iterator.next();
            ++i;
        }
        System.arraycopy(javaStackTrace, 0, finalStackTrace, this.soyStackTrace.size(), javaStackTrace.length);
        return finalStackTrace;
    }
}

