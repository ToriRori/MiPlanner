package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.models.MonthItem;
import com.github.tibolte.agendacalendarview.models.WeekItem;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.utils.Events;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * This class manages information about the calendar. (Events, weather info...)
 * Holds reference to the days list of the calendar.
 * As the app is using several views, we want to keep everything in one place.
 */
public class CalendarManager {

    private static final String LOG_TAG = CalendarManager.class.getSimpleName();

    private static CalendarManager mInstance;

    private Context mContext;
    private Locale mLocale;
    private Calendar mToday = Calendar.getInstance();
    private SimpleDateFormat mWeekdayFormatter;
    private SimpleDateFormat mMonthHalfNameFormat;

    /**
     * List of days used by the calendar
     */
    private List<DayItem> mDays = new ArrayList<>();
    /**
     * List of weeks used by the calendar
     */
    private List<WeekItem> mWeeks = new ArrayList<>();
    /**
     * List of months used by the calendar
     */
    private List<MonthItem> mMonths = new ArrayList<>();
    /**
     * List of events instances
     */
    private List<CalendarEvent> mEvents = new ArrayList<>();
    /**
     * Helper to build our list of weeks
     */
    private Calendar mWeekCounter;
    /**
     * The start date given to the calendar view
     */
    private Calendar mMinCal;
    /**
     * The end date given to the calendar view
     */
    private Calendar mMaxCal;

    // region Constructors

    public CalendarManager(Context context) {
        this.mContext = context;
    }

    public static CalendarManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CalendarManager(context);
        }
        return mInstance;
    }

    public static CalendarManager getInstance() {
        return mInstance;
    }

    // endregion

    // region Getters/Setters

    /**
     * Sets the current mLocale
     *
     * @param locale to be set
     */
    public void setLocale(Locale locale) {
        this.mLocale = locale;

        //apply the same locale to all variables depending on that
        setToday(Calendar.getInstance(mLocale));
        mWeekdayFormatter = new SimpleDateFormat(getContext().getString(R.string.day_name_format), mLocale);
        mMonthHalfNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_half_name_format), locale);
    }

    public Locale getLocale() {
        return mLocale;
    }

    public Context getContext() {
        return mContext;
    }

    public Calendar getToday() {
        return mToday;
    }

    public void setToday(Calendar today) {
        this.mToday = today;
    }

    public List<WeekItem> getWeeks() {
        return mWeeks;
    }

    public List<MonthItem> getMonths() {
        return mMonths;
    }

    public List<DayItem> getDays() {
        return mDays;
    }

    public List<CalendarEvent> getEvents() {
        return mEvents;
    }

    public SimpleDateFormat getWeekdayFormatter() {
        return mWeekdayFormatter;
    }

    public SimpleDateFormat getMonthHalfNameFormat() {
        return mMonthHalfNameFormat;
    }

    // endregion

    // region Public methods

    public void buildCal(Calendar minDate, Calendar maxDate, Locale locale, List<CalendarEvent> events) {
        if (minDate == null || maxDate == null) {
            throw new IllegalArgumentException(
                    "minDate and maxDate must be non-null.");
        }
        if (minDate.after(maxDate)) {
            throw new IllegalArgumentException(
                    "minDate must be before maxDate.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Locale is null.");
        }

        setLocale(locale);

        getDays().clear();
        getWeeks().clear();
        getMonths().clear();
        getEvents().clear();

        mMinCal = Calendar.getInstance(mLocale);
        mMaxCal = Calendar.getInstance(mLocale);
        mWeekCounter = Calendar.getInstance(mLocale);

        mMinCal.setTime(minDate.getTime());
        mMaxCal.setTime(maxDate.getTime());

        // maxDate is exclusive, here we bump back to the previous day, as maxDate if December 1st, 2020,
        // we don't include that month in our list
        mMaxCal.add(Calendar.MINUTE, -1);

        // Now iterate we iterate between mMinCal and mMaxCal so we build our list of weeks
        mWeekCounter.setTime(mMinCal.getTime());
        int maxMonth = mMaxCal.get(Calendar.MONTH);
        int maxYear = mMaxCal.get(Calendar.YEAR);
        // Build another month item and add it to our list, if this value change when we loop through the weeks
        int tmpMonth = -1;
        setToday(Calendar.getInstance(mLocale));

        // Loop through the weeks
        while ((mWeekCounter.get(Calendar.MONTH) <= maxMonth // Up to, including the month.
                || mWeekCounter.get(Calendar.YEAR) < maxYear) // Up to the year.
                && mWeekCounter.get(Calendar.YEAR) < maxYear + 1) { // But not > next yr.
            Date date = mWeekCounter.getTime();

            if (tmpMonth != mWeekCounter.get(Calendar.MONTH)) {
                MonthItem monthItem = new MonthItem(mWeekCounter.get(Calendar.YEAR), mWeekCounter.get(Calendar.MONTH));
                getMonths().add(monthItem);
            }

            // Build our week list
            WeekItem weekItem = new WeekItem(mWeekCounter.get(Calendar.WEEK_OF_YEAR), mWeekCounter.get(Calendar.YEAR), date, mMonthHalfNameFormat.format(date), mWeekCounter.get(Calendar.MONTH));
            List<DayItem> dayItems = getDayCells(mWeekCounter, events); // gather days for the built week
            weekItem.setDayItems(dayItems);
            getWeeks().add(weekItem);
            addWeekToLastMonth(weekItem);

            Log.d(LOG_TAG, String.format("Adding week: %s", weekItem));
            tmpMonth = mWeekCounter.get(Calendar.MONTH);
            mWeekCounter.add(Calendar.WEEK_OF_YEAR, 1);
        }

    }

    public void loadEvents(List<CalendarEvent> eventList) {
        /*CalendarLoadTask calendarLoadTask = new CalendarLoadTask();
        calendarLoadTask.execute();*/
        for (WeekItem weekItem : getWeeks()) {
            for (DayItem dayItem : weekItem.getDayItems()) {
                boolean isEventForDay = false;
                for (CalendarEvent event : eventList) {
                    /*int count = 0;
                    if ((event.getStartTime().get(Calendar.YEAR) < mMinCal.get(Calendar.YEAR))&&(event.getRepeat() != null)&&(!event.getRepeat().equals(""))&&(event.getEndRepeat().equals(""))){
                        Calendar temp = new GregorianCalendar();
                        temp.setTime(event.getStartTime().getTime());
                        String[] parts = event.getRepeat().split(" ");
                        Calendar end = new GregorianCalendar();
                        end.setTime(mMinCal.getTime());
                        end.add(Calendar.DAY_OF_YEAR, -7);
                        while (temp.before(end)){
                            if (!parts[6].equals("*")) {
                                temp.add(Calendar.YEAR, Integer.parseInt(parts[6]));
                                count++;
                            }
                            else if (!parts[4].equals("*")) {
                                temp.add(Calendar.MONTH, Integer.parseInt(parts[4]));
                                count++;
                            }
                            else if (!parts[3].equals("*")) {
                                temp.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(parts[3]));
                                count++;
                            }
                            else if (!parts[2].equals("*")) {
                                temp.add(Calendar.DAY_OF_YEAR, Integer.parseInt(parts[2]));
                                count++;
                            }
                        }
                    }

                    String repeat = event.getRepeat();
                    if ((repeat != null)&&(!repeat.equals(""))) {
                        String[] parts = repeat.split(" ");
                        if(!parts[2].equals("*")) {
                            String[] edges = parts[2].split("-");
                            if (edges.length == 1) {
                                int end;
                                if (event.getEndRepeat().equals(""))
                                    end = 405/Integer.parseInt(parts[2]);
                                else if ((event.getEndRepeat().split("\\.")).length == 1)
                                    end = Integer.parseInt(event.getEndRepeat());
                                else
                                {
                                    end = 0;
                                    Calendar cal1 = new GregorianCalendar();
                                    Calendar cal2 = new GregorianCalendar();
                                    cal1.setTime(event.getStartTime().getTime());
                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                    try {
                                        cal2.setTime(format.parse(event.getEndRepeat()));
                                    }
                                    catch(ParseException e) {
                                        e.printStackTrace();
                                    }
                                    while (cal1.before(cal2)) {
                                        end += 1;
                                        cal1.add(Calendar.DAY_OF_YEAR, Integer.parseInt(edges[0]));
                                    }
                                }

                                for (int i = 0; i < end; i++) {
                                    Calendar cal1 = new GregorianCalendar();
                                    Calendar cal2 = new GregorianCalendar();
                                    cal1.setTime(event.getStartTime().getTime());
                                    cal2.setTime(event.getEndTime().getTime());
                                    cal1.add(Calendar.DAY_OF_MONTH, count + i * Integer.parseInt(edges[0]));
                                    cal2.add(Calendar.DAY_OF_MONTH, count + i * Integer.parseInt(edges[0]));

                                    if (DateHelper.isBetweenUninclusive(dayItem.getDate(), cal1, cal2)) {
                                        CalendarEvent copy = event.copy();
                                        copy.setAllDay(true);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;
                                    }
                                    if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal1, dayItem.getDate())) {
                                        CalendarEvent copy = event.copy();
                                        Calendar cal = copy.getEndTime();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        copy.setEndTime(cal);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;

                                    }
                                    if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal2, dayItem.getDate())) {
                                        CalendarEvent copy = event.copy();
                                        Calendar cal = copy.getStartTime();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        copy.setStartTime(cal);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;

                                    }
                                    if (DateHelper.sameDate(cal2, cal1) && (DateHelper.sameDate(cal2, dayItem.getDate()) || DateHelper.sameDate(cal1, dayItem.getDate()))) {
                                        CalendarEvent copy = event.copy();
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;
                                    }
                                }
                            }
                        }
                        else if (!parts[3].equals("*")) {
                            int end;
                            if (event.getEndRepeat().equals(""))
                                end = 55/Integer.parseInt(parts[3]);
                            else if (event.getEndRepeat().split("\\.").length == 1)
                                end = Integer.parseInt(event.getEndRepeat());
                            else
                            {
                                end = 0;
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    cal2.setTime(format.parse(event.getEndRepeat()));
                                }
                                catch(ParseException e) {
                                    e.printStackTrace();
                                }
                                while (cal1.before(cal2)) {
                                    end += 1;
                                    cal1.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(parts[3]));
                                }
                            }

                            for (int i = 0; i < end; i++) {
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                cal2.setTime(event.getEndTime().getTime());
                                cal1.add(Calendar.WEEK_OF_YEAR, count + i*Integer.parseInt(parts[3]));
                                cal2.add(Calendar.WEEK_OF_YEAR, count + i*Integer.parseInt(parts[3]));
                                String[] days = parts[5].split(",");
                                boolean flag = false;
                                for (int j = 0; j < days.length; j++) {
                                    if (!flag) {
                                        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                        cal1.add(Calendar.DAY_OF_WEEK, Integer.parseInt(days[j])-1);
                                        cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                        cal2.add(Calendar.DAY_OF_WEEK, Integer.parseInt(days[j])-1);
                                        if (days[(j+1)%days.length].equals(Integer.toString((Integer.parseInt(days[j]) + 1)%7))) {
                                            flag = true;
                                            continue;
                                        }
                                    }
                                    else {
                                        cal2.add(Calendar.DAY_OF_WEEK, 1);
                                        if (days[(j+1)%days.length].equals(Integer.toString((Integer.parseInt(days[j]) + 1)%7))) {
                                            flag = true;
                                            continue;
                                        }
                                        else
                                            flag = false;
                                    }
                                    if (cal1.before(event.getStartTime()))
                                        continue;
                                    if (DateHelper.isBetweenUninclusive(dayItem.getDate(), cal1, cal2)) {
                                        CalendarEvent copy = event.copy();
                                        copy.setAllDay(true);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;
                                    }
                                    if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal1, dayItem.getDate())) {
                                        CalendarEvent copy = event.copy();
                                        Calendar cal = copy.getEndTime();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        copy.setEndTime(cal);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;

                                    }
                                    if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal2, dayItem.getDate())) {
                                        CalendarEvent copy = event.copy();
                                        Calendar cal = copy.getStartTime();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        copy.setStartTime(cal);
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;

                                    }
                                    if (DateHelper.sameDate(cal2, cal1) && (DateHelper.sameDate(cal2, dayItem.getDate()) || DateHelper.sameDate(cal1, dayItem.getDate()))) {
                                        int day1 = cal1.get(Calendar.DAY_OF_MONTH);
                                        int day2 = cal2.get(Calendar.DAY_OF_MONTH);
                                        CalendarEvent copy = event.copy();
                                        Calendar dayInstance = Calendar.getInstance();
                                        dayInstance.setTime(dayItem.getDate());
                                        copy.setInstanceDay(dayInstance);
                                        copy.setDayReference(dayItem);
                                        copy.setWeekReference(weekItem);
                                        // add instances in chronological order
                                        getEvents().add(copy);
                                        isEventForDay = true;
                                    }
                                }
                            }
                        }
                        else if (!parts[6].equals("*")) {
                            int end;
                            int period = Integer.parseInt(parts[6]);
                            if (event.getEndRepeat().equals(""))
                                end = 2;
                            else if (event.getEndRepeat().split("\\.").length == 1)
                                end = Integer.parseInt(event.getEndRepeat());
                            else
                            {
                                end = 0;
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    cal2.setTime(format.parse(event.getEndRepeat()));
                                }
                                catch(ParseException e) {
                                    e.printStackTrace();
                                }
                                while (cal1.before(cal2)) {
                                    end += 1;
                                    cal1.add(Calendar.YEAR, period);
                                }
                            }
                            for (int i = 0; i < end; i++) {
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                cal2.setTime(event.getEndTime().getTime());
                                cal1.add(Calendar.YEAR, count + i * Integer.parseInt(parts[6]));
                                cal2.add(Calendar.YEAR, count + i * Integer.parseInt(parts[6]));

                                if (DateHelper.isBetweenUninclusive(dayItem.getDate(), cal1, cal2)) {
                                    CalendarEvent copy = event.copy();
                                    copy.setAllDay(true);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;
                                }
                                if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal1, dayItem.getDate())) {
                                    CalendarEvent copy = event.copy();
                                    Calendar cal = copy.getEndTime();
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    copy.setEndTime(cal);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;

                                }
                                if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal2, dayItem.getDate())) {
                                    CalendarEvent copy = event.copy();
                                    Calendar cal = copy.getStartTime();
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    copy.setStartTime(cal);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;

                                }
                                if (DateHelper.sameDate(cal2, cal1) && (DateHelper.sameDate(cal2, dayItem.getDate()) || DateHelper.sameDate(cal1, dayItem.getDate()))) {
                                    CalendarEvent copy = event.copy();
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;
                                }
                            }
                        }
                        else if (!parts[4].equals("*")) {
                            int end = 1;
                            if (event.getEndRepeat().equals(""))
                                end = 13/Integer.parseInt(parts[4]);
                            else if (event.getEndRepeat().split("\\.").length == 1)
                                end = Integer.parseInt(event.getEndRepeat());
                            else
                            {
                                end = 0;
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    cal2.setTime(format.parse(event.getEndRepeat()));
                                }
                                catch(ParseException e) {
                                    e.printStackTrace();
                                }
                                while (cal1.before(cal2)) {
                                    end += 1;
                                    cal1.add(Calendar.YEAR, Integer.parseInt(parts[4]));
                                }
                            }

                            for (int i = 0; i < end; i++) {
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(event.getStartTime().getTime());
                                cal2.setTime(event.getEndTime().getTime());
                                cal1.add(Calendar.MONTH, count + i * Integer.parseInt(parts[4]));
                                cal2.add(Calendar.MONTH, count + i * Integer.parseInt(parts[4]));

                                if (DateHelper.isBetweenUninclusive(dayItem.getDate(), cal1, cal2)) {
                                    CalendarEvent copy = event.copy();
                                    copy.setAllDay(true);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;
                                }
                                if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal1, dayItem.getDate())) {
                                    CalendarEvent copy = event.copy();
                                    Calendar cal = copy.getEndTime();
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    copy.setEndTime(cal);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;

                                }
                                if ((!DateHelper.sameDate(cal2, cal1)) && DateHelper.sameDate(cal2, dayItem.getDate())) {
                                    CalendarEvent copy = event.copy();
                                    Calendar cal = copy.getStartTime();
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    copy.setStartTime(cal);
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;

                                }
                                if (DateHelper.sameDate(cal2, cal1) && (DateHelper.sameDate(cal2, dayItem.getDate()) || DateHelper.sameDate(cal1, dayItem.getDate()))) {
                                    CalendarEvent copy = event.copy();
                                    Calendar dayInstance = Calendar.getInstance();
                                    dayInstance.setTime(dayItem.getDate());
                                    copy.setInstanceDay(dayInstance);
                                    copy.setDayReference(dayItem);
                                    copy.setWeekReference(weekItem);
                                    // add instances in chronological order
                                    getEvents().add(copy);
                                    isEventForDay = true;
                                }
                            }
                        }
                    }
                    else {*/
                        if (DateHelper.isBetweenUninclusive(dayItem.getDate(), event.getStartTime(), event.getEndTime())) {
                            CalendarEvent copy = event.copy();
                            copy.setAllDay(true);
                            Calendar dayInstance = Calendar.getInstance();
                            dayInstance.setTime(dayItem.getDate());
                            copy.setInstanceDay(dayInstance);
                            copy.setDayReference(dayItem);
                            copy.setWeekReference(weekItem);
                            // add instances in chronological order
                            getEvents().add(copy);
                            isEventForDay = true;
                        }
                        if ((!DateHelper.sameDate(event.getEndTime(), event.getStartTime())) && DateHelper.sameDate(event.getStartTime(), dayItem.getDate())) {
                            CalendarEvent copy = event.copy();
                            Calendar cal = copy.getEndTime();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            copy.setEndTime(cal);
                            Calendar dayInstance = Calendar.getInstance();
                            dayInstance.setTime(dayItem.getDate());
                            copy.setInstanceDay(dayInstance);
                            copy.setDayReference(dayItem);
                            copy.setWeekReference(weekItem);
                            // add instances in chronological order
                            getEvents().add(copy);
                            isEventForDay = true;

                        }
                        if ((!DateHelper.sameDate(event.getEndTime(), event.getStartTime())) && DateHelper.sameDate(event.getEndTime(), dayItem.getDate())) {
                            CalendarEvent copy = event.copy();
                            Calendar cal = copy.getStartTime();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            copy.setStartTime(cal);
                            Calendar dayInstance = Calendar.getInstance();
                            dayInstance.setTime(dayItem.getDate());
                            copy.setInstanceDay(dayInstance);
                            copy.setDayReference(dayItem);
                            copy.setWeekReference(weekItem);
                            // add instances in chronological order
                            getEvents().add(copy);
                            isEventForDay = true;

                        }
                        if (DateHelper.sameDate(event.getEndTime(), event.getStartTime())&&(DateHelper.sameDate(event.getEndTime(), dayItem.getDate())||DateHelper.sameDate(event.getStartTime(), dayItem.getDate()))) {
                            CalendarEvent copy = event.copy();
                            Calendar dayInstance = Calendar.getInstance();
                            dayInstance.setTime(dayItem.getDate());
                            copy.setInstanceDay(dayInstance);
                            copy.setDayReference(dayItem);
                            copy.setWeekReference(weekItem);
                            // add instances in chronological order
                            getEvents().add(copy);
                            isEventForDay = true;

                        }
                    //}
                }
                if (!isEventForDay) {
                    Calendar dayInstance = Calendar.getInstance();
                    dayInstance.setTime(dayItem.getDate());
                    BaseCalendarEvent event = new BaseCalendarEvent(dayInstance, getContext().getResources().getString(R.string.agenda_event_no_events));
                    event.setDayReference(dayItem);
                    event.setWeekReference(weekItem);
                    getEvents().add(event);
                }
            }
        }

        BusProvider.getInstance().send(new Events.EventsFetched());
        Log.d(LOG_TAG, "CalendarEventTask finished");
    }

    // endregion

    // region Private methods

    private List<DayItem> getDayCells(Calendar startCal, List<CalendarEvent> events) {
        Calendar cal = Calendar.getInstance(mLocale);
        cal.setTime(startCal.getTime());
        List<DayItem> dayItems = new ArrayList<>();

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (offset > 0) {
            offset -= 7;
        }
        cal.add(Calendar.DATE, offset);

        Log.d(LOG_TAG, String.format("Buiding row week starting at %s", cal.getTime()));
        for (int c = 0; c < 7; c++) {
            DayItem dayItem = DayItem.buildDayItemFromCal(cal, events);
            dayItem.setDayOftheWeek(c);
            dayItems.add(dayItem);
            cal.add(Calendar.DATE, 1);
        }

        getDays().addAll(dayItems);
        return dayItems;
    }

    private void addWeekToLastMonth(WeekItem weekItem) {
        getLastMonth().getWeeks().add(weekItem);
        getLastMonth().setMonth(mWeekCounter.get(Calendar.MONTH) + 1);
    }

    private MonthItem getLastMonth() {
        return getMonths().get(getMonths().size() - 1);
    }
    // endregion
}
