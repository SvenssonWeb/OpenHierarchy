package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.enums.EventSource;


public interface EventListener<EventType> {

	public void processEvent(EventType event, EventSource eventSource);
}
