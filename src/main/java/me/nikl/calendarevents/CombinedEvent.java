package me.nikl.calendarevents;

import java.util.List;

public class CombinedEvent {
  private List<String> childEvents;
  private String lable;

  public CombinedEvent(String lable, List<String> childEvents) {
    this.childEvents = childEvents;
    this.lable = lable;
  }

  public String getLable() {
    return this.lable;
  }

  public List<String> getChildEvents() {
    return this.childEvents;
  }

  public boolean isCalled(List<String> events) {
    for (String event : this.childEvents) {
      if (!events.contains(event)) return false;
    }
    return true;
  }
}