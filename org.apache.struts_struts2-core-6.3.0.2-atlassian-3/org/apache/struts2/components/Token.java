/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="token", tldTagClass="org.apache.struts2.views.jsp.ui.TokenTag", description="Stop double-submission of forms")
public class Token
extends UIBean {
    public static final String TEMPLATE = "token";

    public Token(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        String tokenName;
        super.evaluateExtraParams();
        Map<String, Object> parameters = this.getParameters();
        if (parameters.containsKey("name")) {
            tokenName = (String)parameters.get("name");
        } else {
            if (this.name == null) {
                tokenName = TEMPLATE;
            } else {
                tokenName = this.findString(this.name);
                if (tokenName == null) {
                    tokenName = this.name;
                }
            }
            this.addParameter("name", tokenName);
        }
        String token = this.buildToken(tokenName);
        this.addParameter(TEMPLATE, token);
        this.addParameter("tokenNameField", "struts.token.name");
    }

    private String buildToken(String name) {
        Map<String, Object> context = this.stack.getContext();
        Object myToken = context.get(name);
        if (myToken == null) {
            myToken = TokenHelper.setToken(name);
            context.put(name, myToken);
        }
        return myToken.toString();
    }
}

