/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.interwiki.InterWiki;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;
import org.radeox.util.Encoder;
import org.radeox.util.StringBufferWriter;

public class LinkTestFilter
extends LocaleRegexTokenFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$LinkTestFilter == null ? (class$org$radeox$filter$LinkTestFilter = LinkTestFilter.class$("org.radeox.filter.LinkTestFilter")) : class$org$radeox$filter$LinkTestFilter));
    static /* synthetic */ Class class$org$radeox$filter$LinkTestFilter;

    protected String getLocaleKey() {
        return "filter.linktest";
    }

    protected void setUp(FilterContext context) {
        context.getRenderContext().setCacheable(true);
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        block24: {
            RenderEngine engine = context.getRenderContext().getRenderEngine();
            if (engine instanceof WikiRenderEngine) {
                WikiRenderEngine wikiEngine = (WikiRenderEngine)((Object)engine);
                StringBufferWriter writer = new StringBufferWriter(buffer);
                String name = result.group(1);
                if (name != null) {
                    int atIndex;
                    int colonIndex;
                    if (name.indexOf("http://") != -1) {
                        try {
                            ((Writer)writer).write("<div class=\"error\">Do not surround URLs with [...].</div>");
                        }
                        catch (IOException e) {
                            // empty catch block
                        }
                        return;
                    }
                    name = Encoder.unescape(name.trim());
                    int pipeIndex = name.indexOf(124);
                    String alias = "";
                    if (-1 != pipeIndex) {
                        alias = name.substring(0, pipeIndex);
                        name = name.substring(pipeIndex + 1);
                    }
                    int hashIndex = name.lastIndexOf(35);
                    String hash = "";
                    if (-1 != hashIndex && hashIndex != name.length() - 1) {
                        hash = name.substring(hashIndex + 1);
                        name = name.substring(0, hashIndex);
                    }
                    if (-1 != (colonIndex = name.indexOf(58))) {
                        name = name.substring(colonIndex + 1);
                    }
                    if (-1 != (atIndex = name.lastIndexOf(64))) {
                        String extSpace = name.substring(atIndex + 1);
                        InterWiki interWiki = InterWiki.getInstance();
                        if (interWiki.contains(extSpace)) {
                            String view = name;
                            if (-1 != pipeIndex) {
                                view = alias;
                            }
                            name = name.substring(0, atIndex);
                            try {
                                if (-1 != hashIndex) {
                                    interWiki.expand(writer, extSpace, name, view, hash);
                                    break block24;
                                }
                                interWiki.expand(writer, extSpace, name, view);
                            }
                            catch (IOException e) {
                                log.debug((Object)("InterWiki " + extSpace + " not found."));
                            }
                        } else {
                            buffer.append("&#91;<span class=\"error\">");
                            buffer.append(result.group(1));
                            buffer.append("?</span>&#93;");
                        }
                    } else if (wikiEngine.exists(name, -1)) {
                        String view = this.getWikiView(name);
                        if (-1 != pipeIndex) {
                            view = alias;
                        }
                        if (-1 != hashIndex) {
                            wikiEngine.appendLink(buffer, name, view, hash, null, -1);
                        } else {
                            wikiEngine.appendLink(buffer, name, view, null, -1);
                        }
                    } else if (wikiEngine.showCreate(-1)) {
                        wikiEngine.appendCreateLink(buffer, name, this.getWikiView(name), null, -1);
                        context.getRenderContext().setCacheable(false);
                    } else {
                        buffer.append(name);
                    }
                } else {
                    buffer.append(Encoder.escape(result.group(0)));
                }
            }
        }
    }

    protected String getWikiView(String name) {
        return name;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

