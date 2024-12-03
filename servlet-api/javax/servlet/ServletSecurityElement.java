/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.HttpConstraintElement;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.ServletSecurity;

public class ServletSecurityElement
extends HttpConstraintElement {
    private final Map<String, HttpMethodConstraintElement> methodConstraints = new HashMap<String, HttpMethodConstraintElement>();

    public ServletSecurityElement() {
    }

    public ServletSecurityElement(HttpConstraintElement httpConstraintElement) {
        this(httpConstraintElement, null);
    }

    public ServletSecurityElement(Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        this.addHttpMethodConstraints(httpMethodConstraints);
    }

    public ServletSecurityElement(HttpConstraintElement httpConstraintElement, Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        super(httpConstraintElement.getEmptyRoleSemantic(), httpConstraintElement.getTransportGuarantee(), httpConstraintElement.getRolesAllowed());
        this.addHttpMethodConstraints(httpMethodConstraints);
    }

    public ServletSecurityElement(ServletSecurity annotation) {
        this(new HttpConstraintElement(annotation.value().value(), annotation.value().transportGuarantee(), annotation.value().rolesAllowed()));
        ArrayList<HttpMethodConstraintElement> l = new ArrayList<HttpMethodConstraintElement>();
        HttpMethodConstraint[] constraints = annotation.httpMethodConstraints();
        if (constraints != null) {
            for (HttpMethodConstraint constraint : constraints) {
                HttpMethodConstraintElement e = new HttpMethodConstraintElement(constraint.value(), new HttpConstraintElement(constraint.emptyRoleSemantic(), constraint.transportGuarantee(), constraint.rolesAllowed()));
                l.add(e);
            }
        }
        this.addHttpMethodConstraints(l);
    }

    public Collection<HttpMethodConstraintElement> getHttpMethodConstraints() {
        HashSet<HttpMethodConstraintElement> result = new HashSet<HttpMethodConstraintElement>(this.methodConstraints.values());
        return result;
    }

    public Collection<String> getMethodNames() {
        HashSet<String> result = new HashSet<String>(this.methodConstraints.keySet());
        return result;
    }

    private void addHttpMethodConstraints(Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        if (httpMethodConstraints == null) {
            return;
        }
        for (HttpMethodConstraintElement constraint : httpMethodConstraints) {
            String method = constraint.getMethodName();
            if (this.methodConstraints.containsKey(method)) {
                throw new IllegalArgumentException("Duplicate method name: " + method);
            }
            this.methodConstraints.put(method, constraint);
        }
    }
}

