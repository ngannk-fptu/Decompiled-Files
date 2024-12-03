/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bea.jvm.ClassLibrary
 *  com.bea.jvm.ClassPreProcessor
 *  com.bea.jvm.JVMFactory
 */
package org.aspectj.weaver.loadtime;

import com.bea.jvm.ClassLibrary;
import com.bea.jvm.JVMFactory;
import java.util.Stack;
import org.aspectj.weaver.loadtime.Aj;
import org.aspectj.weaver.loadtime.ClassPreProcessor;

public class JRockitAgent
implements com.bea.jvm.ClassPreProcessor {
    private ClassPreProcessor preProcessor = new Aj();
    private static ThreadLocalStack stack = new ThreadLocalStack();

    public JRockitAgent() {
        ClassLibrary cl = JVMFactory.getJVM().getClassLibrary();
        cl.setClassPreProcessor((com.bea.jvm.ClassPreProcessor)this);
    }

    public byte[] preProcess(ClassLoader loader, String className, byte[] bytes) {
        byte[] newBytes = bytes;
        if (stack.empty()) {
            stack.push(className);
            newBytes = this.preProcessor.preProcess(className, bytes, loader, null);
            stack.pop();
        }
        return newBytes;
    }

    private static class ThreadLocalStack
    extends ThreadLocal {
        private ThreadLocalStack() {
        }

        public boolean empty() {
            Stack stack = (Stack)this.get();
            return stack.empty();
        }

        public Object peek() {
            Object obj = null;
            Stack stack = (Stack)this.get();
            if (!stack.empty()) {
                obj = stack.peek();
            }
            return obj;
        }

        public void push(Object obj) {
            Stack stack = (Stack)this.get();
            if (!stack.empty() && obj == stack.peek()) {
                throw new RuntimeException(obj.toString());
            }
            stack.push(obj);
        }

        public Object pop() {
            Stack stack = (Stack)this.get();
            return stack.pop();
        }

        protected Object initialValue() {
            return new Stack();
        }
    }
}

