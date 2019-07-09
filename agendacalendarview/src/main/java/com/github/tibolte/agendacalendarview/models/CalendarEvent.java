package com.github.tibolte.agendacalendarview.models;

import java.io.Serializable;
import java.util.Calendar;

public interface CalendarEvent extends Serializable {

    long getId();

    void setId(long mId);

    Calendar getStartTime();

    void setStartTime(Calendar mStartTime);

    Calendar getEndTime();

    void setEndTime(Calendar mEndTime);

    String getTitle();

    void setTitle(String mTitle);

    Calendar getInstanceDay();

    void setInstanceDay(Calendar mInstanceDay);

    DayItem getDayReference();

    void setDayReference(DayItem mDayReference);

    WeekItem getWeekReference();

    void setWeekReference(WeekItem mWeekReference);

    public boolean isAllDay();

    public void setAllDay(boolean allDay);

    CalendarEvent copy();

    String getDescription();

    String getLocation();

    String getRrule();
}
