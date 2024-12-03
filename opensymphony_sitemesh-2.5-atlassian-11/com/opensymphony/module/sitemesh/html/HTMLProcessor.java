/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.HTMLProcessorContext;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.TagRule;
import com.opensymphony.module.sitemesh.html.Text;
import com.opensymphony.module.sitemesh.html.TextFilter;
import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;
import java.io.IOException;

public class HTMLProcessor {
    private final SitemeshBuffer sitemeshBuffer;
    private final SitemeshBufferFragment.Builder body;
    private final State defaultState;
    private State currentState;

    public HTMLProcessor(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment.Builder body) {
        this.currentState = this.defaultState = new State();
        this.sitemeshBuffer = sitemeshBuffer;
        this.body = body;
    }

    public State defaultState() {
        return this.defaultState;
    }

    public void addRule(TagRule rule) {
        this.defaultState.addRule(rule);
    }

    public void process() throws IOException {
        TagTokenizer tokenizer = new TagTokenizer(this.sitemeshBuffer.getCharArray(), this.sitemeshBuffer.getBufferLength());
        final HTMLProcessorContext context = new HTMLProcessorContext(){
            private SitemeshBufferFragment.Builder[] buffers = new SitemeshBufferFragment.Builder[10];
            private int size;

            public SitemeshBuffer getSitemeshBuffer() {
                return HTMLProcessor.this.sitemeshBuffer;
            }

            public State currentState() {
                return HTMLProcessor.this.currentState;
            }

            public void changeState(State newState) {
                HTMLProcessor.this.currentState = newState;
            }

            public void pushBuffer(SitemeshBufferFragment.Builder buffer) {
                if (this.size == this.buffers.length) {
                    SitemeshBufferFragment.Builder[] newBuffers = new SitemeshBufferFragment.Builder[this.buffers.length * 2];
                    System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
                    this.buffers = newBuffers;
                }
                this.buffers[this.size++] = buffer;
            }

            public SitemeshBufferFragment.Builder currentBuffer() {
                return this.buffers[this.size - 1];
            }

            public SitemeshBufferFragment.Builder popBuffer() {
                SitemeshBufferFragment.Builder last = this.buffers[this.size - 1];
                this.buffers[--this.size] = null;
                return last;
            }
        };
        context.pushBuffer(this.body);
        tokenizer.start(new TokenHandler(){

            public boolean shouldProcessTag(String name) {
                return HTMLProcessor.this.currentState.shouldProcessTag(name.toLowerCase());
            }

            public void tag(Tag tag) {
                TagRule tagRule = HTMLProcessor.this.currentState.getRule(tag.getName().toLowerCase());
                tagRule.setContext(context);
                tagRule.process(tag);
            }

            public void text(Text text) {
                HTMLProcessor.this.currentState.handleText(text, context);
            }

            public void warning(String message, int line, int column) {
            }
        });
        this.defaultState.endOfState();
    }

    public void addTextFilter(TextFilter textFilter) {
        this.currentState.addTextFilter(textFilter);
    }
}

