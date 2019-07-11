package com.example.miplanner.Fragments.Calendar;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.io.Serializable;
import java.util.Calendar;

public class DrawableCalendarEvent extends BaseCalendarEvent implements Serializable {

    // region Constructors
    public DrawableCalendarEvent(long id, int color, String title, String owner, String description, String location, String rrule,
                                 Calendar dateStart, Calendar dateEnd, Calendar dateStartGlobal, Calendar dateEndGlobal, boolean allDay, long duration) {
        super(id, color, title, owner, description, location, rrule, dateStart, dateEnd, dateStartGlobal, dateEndGlobal, allDay, duration);
    }


    public DrawableCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay) {
        super(title, description, location, color, startTime, endTime, allDay);
    }

    public DrawableCalendarEvent(String title, String description, int color, Calendar startTime, Calendar endTime, boolean allDay) {
        super(title, description, color, startTime, endTime, allDay);
    }

    public DrawableCalendarEvent(DrawableCalendarEvent calendarEvent) {
        super(calendarEvent);
    }

    // endregion

    // region Public methods

    // endregion

    // region Class - BaseCalendarEvent

    @Override
    public CalendarEvent copy() {
        return new DrawableCalendarEvent(this);
    }

    // endregion
}
