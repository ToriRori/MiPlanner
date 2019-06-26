package com.example.miplanner.Data;

import android.provider.BaseColumns;

import com.example.miplanner.Event;

public final class Events {

    private Events() {
    }

    public static final class EventAdd implements BaseColumns {
        public final static String TABLE_NAME = "EventsTable";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_DATE_START = "date_start";
        public final static String COLUMN_DATE_END = "date_end";
        public final static String COLUMN_LOCATION = "location";
        public final static String COLUMN_REPEAT = "repeat";
        public final static String COLUMN_REPEAT_END = "end_repeat";
    }
}
