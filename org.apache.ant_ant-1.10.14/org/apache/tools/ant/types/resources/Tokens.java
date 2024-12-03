/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.LineTokenizer;
import org.apache.tools.ant.util.Tokenizer;

public class Tokens
extends BaseResourceCollectionWrapper {
    private Tokenizer tokenizer;
    private String encoding;

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected synchronized Collection<Resource> getCollection() {
        ResourceCollection rc = this.getResourceCollection();
        if (rc.isEmpty()) {
            return Collections.emptySet();
        }
        if (this.tokenizer == null) {
            this.tokenizer = new LineTokenizer();
        }
        try (ConcatResourceInputStream cat = new ConcatResourceInputStream(rc);){
            ArrayList<Resource> arrayList;
            try (InputStreamReader rdr = new InputStreamReader((InputStream)cat, this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding));){
                cat.setManagingComponent(this);
                ArrayList<Resource> result = new ArrayList<Resource>();
                String s = this.tokenizer.getToken(rdr);
                while (s != null) {
                    StringResource resource = new StringResource(s);
                    resource.setProject(this.getProject());
                    result.add(resource);
                    s = this.tokenizer.getToken(rdr);
                }
                arrayList = result;
            }
            return arrayList;
        }
        catch (IOException e) {
            throw new BuildException("Error reading tokens", e);
        }
    }

    public synchronized void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public synchronized void add(Tokenizer tokenizer) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.tokenizer != null) {
            throw new BuildException("Only one nested tokenizer allowed.");
        }
        this.tokenizer = tokenizer;
        this.setChecked(false);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            if (this.tokenizer instanceof DataType) {
                Tokens.pushAndInvokeCircularReferenceCheck((DataType)((Object)this.tokenizer), stk, p);
            }
            this.setChecked(true);
        }
    }
}

