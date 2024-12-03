/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import java.util.HashMap;
import org.apache.batik.dom.events.DOMCustomEvent;
import org.apache.batik.dom.events.DOMEvent;
import org.apache.batik.dom.events.DOMKeyEvent;
import org.apache.batik.dom.events.DOMKeyboardEvent;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.DOMMutationEvent;
import org.apache.batik.dom.events.DOMMutationNameEvent;
import org.apache.batik.dom.events.DOMTextEvent;
import org.apache.batik.dom.events.DOMUIEvent;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;

public class DocumentEventSupport {
    public static final String EVENT_TYPE = "Event";
    public static final String MUTATION_EVENT_TYPE = "MutationEvent";
    public static final String MUTATION_NAME_EVENT_TYPE = "MutationNameEvent";
    public static final String MOUSE_EVENT_TYPE = "MouseEvent";
    public static final String UI_EVENT_TYPE = "UIEvent";
    public static final String KEYBOARD_EVENT_TYPE = "KeyboardEvent";
    public static final String TEXT_EVENT_TYPE = "TextEvent";
    public static final String CUSTOM_EVENT_TYPE = "CustomEvent";
    public static final String EVENT_DOM2_TYPE = "Events";
    public static final String MUTATION_EVENT_DOM2_TYPE = "MutationEvents";
    public static final String MOUSE_EVENT_DOM2_TYPE = "MouseEvents";
    public static final String UI_EVENT_DOM2_TYPE = "UIEvents";
    public static final String KEY_EVENT_DOM2_TYPE = "KeyEvents";
    protected HashMap<String, EventFactory> eventFactories = new HashMap();

    public DocumentEventSupport() {
        this.eventFactories.put(EVENT_TYPE.toLowerCase(), new SimpleEventFactory());
        this.eventFactories.put(MUTATION_EVENT_TYPE.toLowerCase(), new MutationEventFactory());
        this.eventFactories.put(MUTATION_NAME_EVENT_TYPE.toLowerCase(), new MutationNameEventFactory());
        this.eventFactories.put(MOUSE_EVENT_TYPE.toLowerCase(), new MouseEventFactory());
        this.eventFactories.put(KEYBOARD_EVENT_TYPE.toLowerCase(), new KeyboardEventFactory());
        this.eventFactories.put(UI_EVENT_TYPE.toLowerCase(), new UIEventFactory());
        this.eventFactories.put(TEXT_EVENT_TYPE.toLowerCase(), new TextEventFactory());
        this.eventFactories.put(CUSTOM_EVENT_TYPE.toLowerCase(), new CustomEventFactory());
        this.eventFactories.put(EVENT_DOM2_TYPE.toLowerCase(), new SimpleEventFactory());
        this.eventFactories.put(MUTATION_EVENT_DOM2_TYPE.toLowerCase(), new MutationEventFactory());
        this.eventFactories.put(MOUSE_EVENT_DOM2_TYPE.toLowerCase(), new MouseEventFactory());
        this.eventFactories.put(KEY_EVENT_DOM2_TYPE.toLowerCase(), new KeyEventFactory());
        this.eventFactories.put(UI_EVENT_DOM2_TYPE.toLowerCase(), new UIEventFactory());
    }

    public Event createEvent(String eventType) throws DOMException {
        EventFactory ef = this.eventFactories.get(eventType.toLowerCase());
        if (ef == null) {
            throw new DOMException(9, "Bad event type: " + eventType);
        }
        return ef.createEvent();
    }

    public void registerEventFactory(String eventType, EventFactory factory) {
        this.eventFactories.put(eventType.toLowerCase(), factory);
    }

    protected static class CustomEventFactory
    implements EventFactory {
        protected CustomEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMCustomEvent();
        }
    }

    protected static class TextEventFactory
    implements EventFactory {
        protected TextEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMTextEvent();
        }
    }

    protected static class UIEventFactory
    implements EventFactory {
        protected UIEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMUIEvent();
        }
    }

    protected static class KeyboardEventFactory
    implements EventFactory {
        protected KeyboardEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMKeyboardEvent();
        }
    }

    protected static class KeyEventFactory
    implements EventFactory {
        protected KeyEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMKeyEvent();
        }
    }

    protected static class MouseEventFactory
    implements EventFactory {
        protected MouseEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMMouseEvent();
        }
    }

    protected static class MutationNameEventFactory
    implements EventFactory {
        protected MutationNameEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMMutationNameEvent();
        }
    }

    protected static class MutationEventFactory
    implements EventFactory {
        protected MutationEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMMutationEvent();
        }
    }

    protected static class SimpleEventFactory
    implements EventFactory {
        protected SimpleEventFactory() {
        }

        @Override
        public Event createEvent() {
            return new DOMEvent();
        }
    }

    public static interface EventFactory {
        public Event createEvent();
    }
}

