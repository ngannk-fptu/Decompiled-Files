/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 */
package org.apache.velocity.runtime;

import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.Uberspect;

public interface RuntimeServices
extends RuntimeLogger {
    public void init();

    public void setProperty(String var1, Object var2);

    public void setConfiguration(ExtendedProperties var1);

    public void addProperty(String var1, Object var2);

    public void clearProperty(String var1);

    public Object getProperty(String var1);

    public void init(Properties var1);

    public void init(String var1);

    public SimpleNode parse(String var1, String var2) throws ParseException;

    public SimpleNode parse(Reader var1, String var2) throws ParseException;

    public SimpleNode parse(Reader var1, String var2, boolean var3) throws ParseException;

    public boolean evaluate(Context var1, Writer var2, String var3, String var4);

    public boolean evaluate(Context var1, Writer var2, String var3, Reader var4);

    public boolean invokeVelocimacro(String var1, String var2, String[] var3, Context var4, Writer var5);

    public Template getTemplate(String var1) throws ResourceNotFoundException, ParseErrorException;

    public Template getTemplate(String var1, String var2) throws ResourceNotFoundException, ParseErrorException;

    public ContentResource getContent(String var1) throws ResourceNotFoundException, ParseErrorException;

    public ContentResource getContent(String var1, String var2) throws ResourceNotFoundException, ParseErrorException;

    public String getLoaderNameForResource(String var1);

    public String getString(String var1, String var2);

    public Directive getVelocimacro(String var1, String var2);

    public Directive getVelocimacro(String var1, String var2, String var3);

    public boolean addVelocimacro(String var1, String var2, String[] var3, String var4);

    public boolean addVelocimacro(String var1, Node var2, String[] var3, String var4);

    public boolean isVelocimacro(String var1, String var2);

    public boolean dumpVMNamespace(String var1);

    public String getString(String var1);

    public int getInt(String var1);

    public int getInt(String var1, int var2);

    public boolean getBoolean(String var1, boolean var2);

    public ExtendedProperties getConfiguration();

    public Object getApplicationAttribute(Object var1);

    public Object setApplicationAttribute(Object var1, Object var2);

    public Uberspect getUberspect();

    public Log getLog();

    public EventCartridge getApplicationEventCartridge();

    public Introspector getIntrospector();

    public boolean isInitialized();

    public Parser createNewParser();

    public Directive getDirective(String var1);
}

