/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.Collection;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.GreedyInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.apache.tools.ant.input.MultipleChoiceInputRequest;
import org.apache.tools.ant.input.PropertyFileInputHandler;
import org.apache.tools.ant.input.SecureInputHandler;
import org.apache.tools.ant.taskdefs.DefBase;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.StringUtils;

public class Input
extends Task {
    private String validargs = null;
    private String message = "";
    private String addproperty = null;
    private String defaultvalue = null;
    private Handler handler = null;
    private boolean messageAttribute;

    public void setValidargs(String validargs) {
        this.validargs = validargs;
    }

    public void setAddproperty(String addproperty) {
        this.addproperty = addproperty;
    }

    public void setMessage(String message) {
        this.message = message;
        this.messageAttribute = true;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    public void addText(String msg) {
        if (this.messageAttribute && msg.trim().isEmpty()) {
            return;
        }
        this.message = this.message + this.getProject().replaceProperties(msg);
    }

    @Override
    public void execute() throws BuildException {
        if (this.addproperty != null && this.getProject().getProperty(this.addproperty) != null) {
            this.log("skipping " + this.getTaskName() + " as property " + this.addproperty + " has already been set.");
            return;
        }
        InputRequest request = null;
        if (this.validargs != null) {
            Vector<String> accept = StringUtils.split(this.validargs, 44);
            request = new MultipleChoiceInputRequest(this.message, (Collection<String>)accept);
        } else {
            request = new InputRequest(this.message);
        }
        request.setDefaultValue(this.defaultvalue);
        InputHandler h = this.handler == null ? this.getProject().getInputHandler() : this.handler.getInputHandler();
        h.handleInput(request);
        String value = request.getInput();
        if ((value == null || value.trim().isEmpty()) && this.defaultvalue != null) {
            value = this.defaultvalue;
        }
        if (this.addproperty != null && value != null) {
            this.getProject().setNewProperty(this.addproperty, value);
        }
    }

    public Handler createHandler() {
        if (this.handler != null) {
            throw new BuildException("Cannot define > 1 nested input handler");
        }
        this.handler = new Handler();
        return this.handler;
    }

    public class Handler
    extends DefBase {
        private String refid = null;
        private HandlerType type = null;
        private String classname = null;

        public void setRefid(String refid) {
            this.refid = refid;
        }

        public String getRefid() {
            return this.refid;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getClassname() {
            return this.classname;
        }

        public void setType(HandlerType type) {
            this.type = type;
        }

        public HandlerType getType() {
            return this.type;
        }

        private InputHandler getInputHandler() {
            if (this.type != null) {
                return this.type.getInputHandler();
            }
            if (this.refid != null) {
                try {
                    return (InputHandler)this.getProject().getReference(this.refid);
                }
                catch (ClassCastException e) {
                    throw new BuildException(this.refid + " does not denote an InputHandler", e);
                }
            }
            if (this.classname != null) {
                return ClasspathUtils.newInstance(this.classname, this.createLoader(), InputHandler.class);
            }
            throw new BuildException("Must specify refid, classname or type");
        }
    }

    public static class HandlerType
    extends EnumeratedAttribute {
        private static final String[] VALUES = new String[]{"default", "propertyfile", "greedy", "secure"};
        private static final InputHandler[] HANDLERS = new InputHandler[]{new DefaultInputHandler(), new PropertyFileInputHandler(), new GreedyInputHandler(), new SecureInputHandler()};

        @Override
        public String[] getValues() {
            return VALUES;
        }

        private InputHandler getInputHandler() {
            return HANDLERS[this.getIndex()];
        }
    }
}

