package com.github.tibolte.agendacalendarview.models;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.utils.DateHelper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Day model class.
 */
public class DayItem implements Parcelable {
    private Date mDate;
    private int mValue;
    private int mDayOfTheWeek;
    private boolean mToday;
    private boolean mFirstDayOfTheMonth;
    private boolean mSelected;
    private String mMonth;
    private boolean mHasEvents;

    // region Constructor

    public DayItem(Date date, int value, boolean today, String month, Calendar calendar, List<CalendarEvent> events) {
        this.mDate = date;
        this.mValue = value;
        this.mToday = today;
        this.mMonth = month;
		if(hasEventForDate(calendar,events)) {
            mHasEvents = true;
        } else {
            mHasEvents = false;
        }
    }

    // endregion

    // region Getters/Setters

	public boolean hasEvent() {
        return mHasEvents;
    }

    public void setHasEvents(boolean mHasEvents) {
        this.mHasEvents = mHasEvents;
    }
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }

    public boolean isToday() {
        return mToday;
    }

    public void setToday(boolean today) {
        this.mToday = today;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isFirstDayOfTheMonth() {
        return mFirstDayOfTheMonth;
    }

    public void setFirstDayOfTheMonth(boolean firstDayOfTheMonth) {
        this.mFirstDayOfTheMonth = firstDayOfTheMonth;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        this.mMonth = month;
    }

    public int getDayOftheWeek() {
        return mDayOfTheWeek;
    }

    public void setDayOftheWeek(int mDayOftheWeek) {
        this.mDayOfTheWeek = mDayOftheWeek;
    }

    // region Public methods

    public static DayItem buildDayItemFromCal(Calendar calendar, List<CalendarEvent> events) {
        Date date = calendar.getTime();
        boolean isToday = DateHelper.sameDate(calendar, CalendarManager.getInstance().getToday());
        int value = calendar.get(Calendar.DAY_OF_MONTH);
        DayItem dayItem = new DayItem(date, value, isToday, CalendarManager.getInstance().getMonthHalfNameFormat().format(date), calendar, events);
        if (value == 1) {
            dayItem.setFirstDayOfTheMonth(true);
        }
        dayItem.setToday(isToday);
        return dayItem;
    }

    /**
     *
     * @param calendar
     * @param events
     * @return true if day has a event, false if not
     */
	private boolean hasEventForDate(Calendar calendar,List<CalendarEvent> events) {
	    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        for(int i=0;i<events.size();i++) {
            Date current = calendar.getTime();

            /*int count = 0;
            if ((events.get(i).getStartTime().get(Calendar.YEAR) < calendar.get(Calendar.YEAR))&&(events.get(i).getRepeat() != null)&&(!events.get(i).getRepeat().equals(""))&&(events.get(i).getEndRepeat().equals(""))){
                Calendar temp = new GregorianCalendar();
                temp.setTime(events.get(i).getStartTime().getTime());
                String[] parts = events.get(i).getRepeat().split(" ");
                while (temp.get(Calendar.YEAR) < calendar.get(Calendar.YEAR)){
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

            }*/


            Calendar StartDate = events.get(i).getStartTime();
            Calendar EndDate = events.get(i).getEndTime();
           // if ((events.get(i).getRepeat() == null) || events.get(i).getRepeat().equals("")) {
                if (DateHelper.isBetweenInclusive(current, StartDate, EndDate)) {
                    return true;
                }
            //}
            /*else {
                    String[] parts = events.get(i).getRepeat().split(" ");
                    if (!parts[2].equals("*")) {
                        String[] edges = parts[2].split("-");
                        if (edges.length == 1) {
                            int end = 1;
                            String temp = events.get(i).getEndRepeat();
                            if (events.get(i).getEndRepeat().equals(""))
                                end = 405/Integer.parseInt(parts[2]);
                            else if ((events.get(i).getEndRepeat().split("\\.")).length == 1)
                                end = Integer.parseInt(events.get(i).getEndRepeat());
                            else
                            {
                                end = 0;
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(events.get(i).getStartTime().getTime());
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    cal2.setTime(format.parse(events.get(i).getEndRepeat()));
                                }
                                catch(ParseException e) {
                                    e.printStackTrace();
                                }
                                while (cal1.before(cal2)) {
                                    end += 1;
                                    cal1.add(Calendar.DAY_OF_YEAR, Integer.parseInt(edges[0]));
                                }
                            }
                            for (int j = 0; j < end; j++) {
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(events.get(i).getStartTime().getTime());
                                cal2.setTime(events.get(i).getEndTime().getTime());
                                cal1.add(Calendar.DAY_OF_YEAR, count + j * Integer.parseInt(edges[0]));
                                cal2.add(Calendar.DAY_OF_YEAR, count + j * Integer.parseInt(edges[0]));
                                if (DateHelper.isBetweenInclusive(current, cal1, cal2)) {
                                    return true;
                                }
                            }
                        }
                        else {
                            int end;
                            int period = Integer.parseInt(parts[4]);
                            if (events.get(i).getEndRepeat().equals(""))
                                end = 15/Integer.parseInt(parts[4]);
                            else if (events.get(i).getEndRepeat().split("\\.").length == 1)
                                end = Integer.parseInt(events.get(i).getEndRepeat());
                            else
                            {
                                end = 0;
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(events.get(i).getStartTime().getTime());
                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    cal2.setTime(format.parse(events.get(i).getEndRepeat()));
                                }
                                catch(ParseException e) {
                                    e.printStackTrace();
                                }
                                while (cal1.before(cal2)) {
                                    end += 1;
                                    cal1.add(Calendar.YEAR, period);
                                }
                            }

                            for (int j = 0; j < end; j++) {
                                Calendar cal1 = new GregorianCalendar();
                                Calendar cal2 = new GregorianCalendar();
                                cal1.setTime(events.get(i).getStartTime().getTime());
                                cal2.setTime(events.get(i).getEndTime().getTime());
                                cal1.add(Calendar.MONTH, count + j * Integer.parseInt(parts[4]));
                                cal2.add(Calendar.MONTH, count + j * Integer.parseInt(parts[4]));
                                cal1.set(Calendar.DAY_OF_MONTH, 1);
                                cal1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                cal2.set(Calendar.DAY_OF_MONTH, 1);
                                cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                if (DateHelper.isBetweenInclusive(current, cal1, cal2)) {
                                    return true;
                                }
                            }
                        }
                    } else if (!parts[3].equals("*")) {
                        int end;
                        int period = Integer.parseInt(parts[3]);
                        if (events.get(i).getEndRepeat().equals(""))
                            end = 55/Integer.parseInt(parts[3]);
                        else if (events.get(i).getEndRepeat().split("\\.").length == 1)
                            end = Integer.parseInt(events.get(i).getEndRepeat());
                        else
                        {
                            end = 0;
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                cal2.setTime(format.parse(events.get(i).getEndRepeat()));
                            }
                            catch(ParseException e) {
                                e.printStackTrace();
                            }
                            while (cal1.before(cal2)) {
                                end += 1;
                                cal1.add(Calendar.WEEK_OF_YEAR, period);
                            }
                        }
                        for (int j = 0; j < end; j++) {
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            cal2.setTime(events.get(i).getEndTime().getTime());
                            cal1.add(Calendar.WEEK_OF_YEAR, count + j*period);
                            cal2.add(Calendar.WEEK_OF_YEAR, count + j*period);
                            int day1 = cal1.get(Calendar.DAY_OF_MONTH);
                            int day2 = cal2.get(Calendar.DAY_OF_MONTH);
                            String[] days = parts[5].split(",");
                            for (String day : days) {
                                cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                cal1.add(Calendar.DAY_OF_WEEK, Integer.parseInt(day)-1);
                                day1 = cal1.get(Calendar.DAY_OF_MONTH);
                                day2 = cal2.get(Calendar.DAY_OF_MONTH);
                                cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                cal2.add(Calendar.DAY_OF_WEEK, Integer.parseInt(day)-1);
                                day1 = cal1.get(Calendar.DAY_OF_MONTH);
                                day2 = cal2.get(Calendar.DAY_OF_MONTH);
                                if (cal1.before(events.get(i).getStartTime()))
                                    continue;
                                if (DateHelper.isBetweenInclusive(current, cal1, cal2)) {
                                    return true;
                                }
                            }
                        }
                    } else if (!parts[6].equals("*")) {
                        int end;
                        int period = Integer.parseInt(parts[6]);
                        if (events.get(i).getEndRepeat().equals(""))
                            end = 2;
                        else if (events.get(i).getEndRepeat().split("\\.").length == 1)
                            end = Integer.parseInt(events.get(i).getEndRepeat());
                        else
                        {
                            end = 0;
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                cal2.setTime(format.parse(events.get(i).getEndRepeat()));
                            }
                            catch(ParseException e) {
                                e.printStackTrace();
                            }
                            while (cal1.before(cal2)) {
                                end += 1;
                                cal1.add(Calendar.YEAR, period);
                            }
                        }
                        for (int j = 0; j < end; j++) {
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            cal2.setTime(events.get(i).getEndTime().getTime());
                            cal1.add(Calendar.YEAR, count + j * Integer.parseInt(parts[6]));
                            cal2.add(Calendar.YEAR, count + j * Integer.parseInt(parts[6]));
                            if (DateHelper.isBetweenInclusive(current, cal1, cal2)) {
                                return true;
                            }
                        }
                    } else if (!parts[4].equals("*")) {
                        int end;
                        int period = Integer.parseInt(parts[4]);
                        if (events.get(i).getEndRepeat().equals(""))
                            end = 15/Integer.parseInt(parts[4]);
                        else if (events.get(i).getEndRepeat().split("\\.").length == 1)
                            end = Integer.parseInt(events.get(i).getEndRepeat());
                        else
                        {
                            end = 0;
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                cal2.setTime(format.parse(events.get(i).getEndRepeat()));
                            }
                            catch(ParseException e) {
                                e.printStackTrace();
                            }
                            while (cal1.before(cal2)) {
                                end += 1;
                                cal1.add(Calendar.YEAR, period);
                            }
                        }

                        for (int j = 0; j < end; j++) {
                            Calendar cal1 = new GregorianCalendar();
                            Calendar cal2 = new GregorianCalendar();
                            cal1.setTime(events.get(i).getStartTime().getTime());
                            cal2.setTime(events.get(i).getEndTime().getTime());
                            cal1.add(Calendar.MONTH, j * Integer.parseInt(parts[4]));
                            cal2.add(Calendar.MONTH, j * Integer.parseInt(parts[4]));
                            if (DateHelper.isBetweenInclusive(current, cal1, cal2)) {
                                return true;
                            }
                        }
                    }
                }*/
        }
        return false;
    }
    // endregion

    @Override
    public String toString() {
        return "DayItem{"
                + "Date='"
                + mDate.toString()
                + ", value="
                + mValue
                + '}';
    }

    // region Interface - Parcelable

    protected DayItem(Parcel in) {
        long tmpMDate = in.readLong();
        mDate = tmpMDate != -1 ? new Date(tmpMDate) : null;
        mValue = in.readInt();
        mDayOfTheWeek = in.readInt();
        mToday = in.readByte() != 0x00;
        mFirstDayOfTheMonth = in.readByte() != 0x00;
        mSelected = in.readByte() != 0x00;
        mMonth = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate != null ? mDate.getTime() : -1L);
        dest.writeInt(mValue);
        dest.writeInt(mDayOfTheWeek);
        dest.writeByte((byte) (mToday ? 0x01 : 0x00));
        dest.writeByte((byte) (mFirstDayOfTheMonth ? 0x01 : 0x00));
        dest.writeByte((byte) (mSelected ? 0x01 : 0x00));
        dest.writeString(mMonth);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DayItem> CREATOR = new Parcelable.Creator<DayItem>() {
        @Override
        public DayItem createFromParcel(Parcel in) {
            return new DayItem(in);
        }

        @Override
        public DayItem[] newArray(int size) {
            return new DayItem[size];
        }
    };

    // endregion
}
