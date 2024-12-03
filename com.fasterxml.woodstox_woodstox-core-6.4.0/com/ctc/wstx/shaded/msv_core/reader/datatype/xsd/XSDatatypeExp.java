/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.LazyTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Locator;

public class XSDatatypeExp
extends ReferenceExp
implements GrammarReader.BackPatch {
    private final String namespaceUri;
    private XSDatatype dt;
    private ExpressionPool pool;
    private transient State ownerState;
    private transient Renderer renderer;

    public XSDatatypeExp(XSDatatype dt, ExpressionPool _pool) {
        super(dt.getName());
        this.namespaceUri = dt.getNamespaceUri();
        this.dt = dt;
        this.pool = _pool;
        this.ownerState = null;
        this.renderer = null;
        this.exp = _pool.createData(dt);
    }

    public XSDatatypeExp(String nsUri, String typeName, GrammarReader reader, Renderer _renderer) {
        super(typeName);
        this.namespaceUri = nsUri;
        this.dt = null;
        this.ownerState = reader.getCurrentState();
        this.renderer = _renderer;
        this.pool = reader.pool;
        reader.addBackPatchJob(this);
    }

    private XSDatatypeExp(String nsUri, String localName) {
        super(localName);
        this.namespaceUri = nsUri;
    }

    public XSTypeIncubator createIncubator() {
        if (this.isLateBind()) {
            return new LazyTypeIncubator(this, this.ownerState.reader);
        }
        return new XSTypeIncubator(){
            private final TypeIncubator core;
            {
                this.core = new TypeIncubator(XSDatatypeExp.this.dt);
            }

            public void addFacet(String name, String value, boolean fixed, ValidationContext context) throws DatatypeException {
                this.core.addFacet(name, value, fixed, context);
            }

            public XSDatatypeExp derive(String uri, String localName) throws DatatypeException {
                return new XSDatatypeExp(this.core.derive(uri, localName), XSDatatypeExp.this.pool);
            }
        };
    }

    public XSDatatype getCreatedType() {
        if (this.dt == null) {
            throw new IllegalStateException();
        }
        return this.dt;
    }

    public XSDatatype getType(RenderingContext context) {
        if (this.dt != null) {
            return this.dt;
        }
        if (context == null) {
            context = new RenderingContext();
        }
        if (context.callStack.contains(this)) {
            Vector<Locator> locs = new Vector<Locator>();
            for (int i = 0; i < context.callStack.size(); ++i) {
                locs.add(((XSDatatypeExp)((RenderingContext)context).callStack.get((int)i)).ownerState.getLocation());
            }
            this.ownerState.reader.reportError(locs.toArray(new Locator[0]), "GrammarReader.RecursiveDatatypeDefinition", null);
            return StringType.theInstance;
        }
        context.callStack.push(this);
        try {
            this.dt = this.renderer.render(context);
        }
        catch (DatatypeException e) {
            this.ownerState.reader.reportError("GrammarReader.BadType", new Object[]{e}, e, new Locator[]{this.ownerState.getLocation()});
            this.dt = StringType.theInstance;
        }
        context.callStack.pop();
        if (this.dt == null) {
            throw new Error();
        }
        this.exp = this.pool.createData(this.dt);
        return this.dt;
    }

    public void patch() {
        this.getType(null);
    }

    public State getOwnerState() {
        return this.ownerState;
    }

    public final boolean isLateBind() {
        return this.dt == null;
    }

    public XSDatatypeExp getClone() {
        XSDatatypeExp t = new XSDatatypeExp(this.namespaceUri, this.name);
        t.redefine(this);
        return t;
    }

    public void redefine(XSDatatypeExp rhs) {
        this.exp = rhs.exp;
        this.dt = rhs.dt;
        this.pool = rhs.pool;
        this.ownerState = rhs.ownerState;
        this.renderer = rhs.renderer;
        if (this.ownerState != null) {
            this.ownerState.reader.addBackPatchJob(this);
        }
    }

    public XSDatatypeExp createFinalizedType(final int finalValue, GrammarReader reader) {
        if (finalValue == 0) {
            return this;
        }
        if (!this.isLateBind()) {
            return new XSDatatypeExp(new FinalComponent((XSDatatypeImpl)this.dt, finalValue), this.pool);
        }
        return new XSDatatypeExp(this.namespaceUri, this.name, reader, new Renderer(){

            public XSDatatype render(RenderingContext context) throws DatatypeException {
                return new FinalComponent((XSDatatypeImpl)XSDatatypeExp.this.getType(context), finalValue);
            }
        });
    }

    public static XSDatatypeExp makeList(final String nsUri, final String typeName, final XSDatatypeExp itemType, GrammarReader reader) throws DatatypeException {
        if (!itemType.isLateBind()) {
            return new XSDatatypeExp(DatatypeFactory.deriveByList(nsUri, typeName, itemType.dt), reader.pool);
        }
        return new XSDatatypeExp(nsUri, typeName, reader, new Renderer(){

            public XSDatatype render(RenderingContext context) throws DatatypeException {
                return DatatypeFactory.deriveByList(nsUri, typeName, itemType.getType(context));
            }
        });
    }

    public static XSDatatypeExp makeUnion(final String typeNameUri, final String typeName, final Collection members, GrammarReader reader) throws DatatypeException {
        final XSDatatype[] m = new XSDatatype[members.size()];
        int i = 0;
        for (XSDatatypeExp item : members) {
            if (item.isLateBind()) {
                return new XSDatatypeExp(typeNameUri, typeName, reader, new Renderer(){

                    public XSDatatype render(RenderingContext context) throws DatatypeException {
                        int i = 0;
                        Iterator itr = members.iterator();
                        while (itr.hasNext()) {
                            m[i++] = ((XSDatatypeExp)itr.next()).getType(context);
                        }
                        return DatatypeFactory.deriveByUnion(typeNameUri, typeName, m);
                    }
                });
            }
            m[i++] = item.dt;
        }
        return new XSDatatypeExp(DatatypeFactory.deriveByUnion(typeNameUri, typeName, m), reader.pool);
    }

    public static class RenderingContext {
        private final Stack callStack = new Stack();

        RenderingContext() {
        }
    }

    public static interface Renderer {
        public XSDatatype render(RenderingContext var1) throws DatatypeException;
    }
}

