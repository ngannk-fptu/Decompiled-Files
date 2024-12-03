/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.util.StrutsUtil
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 */
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.util.StrutsUtil;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityStrutsUtil
extends StrutsUtil {
    private final Context ctx;
    private final VelocityEngine velocityEngine;

    public VelocityStrutsUtil(VelocityEngine engine, Context ctx, ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.ctx = ctx;
        this.velocityEngine = engine;
    }

    public String evaluate(String expression) throws IOException, ResourceNotFoundException, MethodInvocationException, ParseErrorException {
        CharArrayWriter writer = new CharArrayWriter();
        this.velocityEngine.evaluate(this.ctx, (Writer)writer, "Error parsing " + expression, expression);
        return writer.toString();
    }
}

