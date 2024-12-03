/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeMatcher;
import org.apache.commons.lang3.StringUtils;

public enum NodeNameMatchers implements NodeMatcher<String>
{
    EQUALS{

        @Override
        public <T> boolean matches(T node, NodeHandler<T> handler, String criterion) {
            return StringUtils.equals((CharSequence)criterion, (CharSequence)handler.nodeName(node));
        }
    }
    ,
    EQUALS_IGNORE_CASE{

        @Override
        public <T> boolean matches(T node, NodeHandler<T> handler, String criterion) {
            return StringUtils.equalsIgnoreCase((CharSequence)criterion, (CharSequence)handler.nodeName(node));
        }
    };

}

