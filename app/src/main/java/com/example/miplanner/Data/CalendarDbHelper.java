package com.example.miplanner.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.miplanner.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = CalendarDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "calendar.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 28;

    /**
     * Конструктор {@link CalendarDbHelper}.
     *
     * @param context Контекст приложения
     */
    public CalendarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + Events.EventAdd.TABLE_NAME + " ("
                + Events.EventAdd._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Events.EventAdd.COLUMN_NAME + " TEXT NOT NULL, "
                + Events.EventAdd.COLUMN_DESCRIPTION + " TEXT, "
                + Events.EventAdd.COLUMN_DATE_START + " TEXT NOT NULL, "
                + Events.EventAdd.COLUMN_DATE_END + " TEXT NOT NULL, "
                + Events.EventAdd.COLUMN_LOCATION + " TEXT, "
                + Events.EventAdd.COLUMN_REPEAT + " TEXT, "
                + Events.EventAdd.COLUMN_REPEAT_END + ");";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS " + Events.EventAdd.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }

    public void insertEventD(String name, String description, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DESCRIPTION, description);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }

    public void insertEventL(String name, String location, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_LOCATION, location);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }
    public void insertEvent(String name, String description, String location, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DESCRIPTION, description);
        values.put(Events.EventAdd.COLUMN_LOCATION, location);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }

    public void insertEvent(String name, String description, String location, String repeat, String end_repeat, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DESCRIPTION, description);
        values.put(Events.EventAdd.COLUMN_LOCATION, location);
        values.put(Events.EventAdd.COLUMN_REPEAT, repeat);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);
        values.put(Events.EventAdd.COLUMN_REPEAT_END, end_repeat);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }


    public void insertEvent(String name, String description, String location, String repeat, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DESCRIPTION, description);
        values.put(Events.EventAdd.COLUMN_LOCATION, location);
        values.put(Events.EventAdd.COLUMN_REPEAT, repeat);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }

    public void insertEvent(String name, String date_start, String date_end) {

        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);

        long newRowId = db.insert(Events.EventAdd.TABLE_NAME, null, values);
    }

    public boolean eventsIsEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {
                Events.EventAdd._ID,
                Events.EventAdd.COLUMN_NAME,
                Events.EventAdd.COLUMN_DESCRIPTION,
                Events.EventAdd.COLUMN_DATE_START,
                Events.EventAdd.COLUMN_DATE_END,
                Events.EventAdd.COLUMN_LOCATION,
                Events.EventAdd.COLUMN_REPEAT,
                Events.EventAdd.COLUMN_REPEAT_END};

        Cursor cursor = db.query(
                Events.EventAdd.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        if  (cursor.moveToNext()) { return false; } else { return true; }
    }

    public Event getEventById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * " + " FROM " + Events.EventAdd.TABLE_NAME + " WHERE " + Events.EventAdd._ID + " = " + id;
        Cursor cursor2 = db.rawQuery(query, null);
        cursor2.moveToNext();

        String mName = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_NAME));
        String mDescription = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_DESCRIPTION));
        String mStartDate = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_DATE_START));
        String mEndDate = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_DATE_END));
        String mLocation = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_LOCATION));
        String mRepeat = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_REPEAT));
        String mRepeatEnd = cursor2.getString(cursor2
                .getColumnIndex(Events.EventAdd.COLUMN_REPEAT_END));
        cursor2.close();

        return new Event(id, mName, mStartDate, mEndDate, mDescription, mLocation, mRepeat, mRepeatEnd);
    }

    public void editEventById(int id, String name, String description, String location, String repeat, String date_start, String date_end) {
        // Gets the database in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(Events.EventAdd.COLUMN_NAME, name);
        values.put(Events.EventAdd.COLUMN_DESCRIPTION, description);
        values.put(Events.EventAdd.COLUMN_LOCATION, location);
        values.put(Events.EventAdd.COLUMN_DATE_START, date_start);
        values.put(Events.EventAdd.COLUMN_DATE_END, date_end);
        values.put(Events.EventAdd.COLUMN_REPEAT, repeat);


        db.update(Events.EventAdd.TABLE_NAME,
                values,
                Events.EventAdd._ID + " = ?",
                new String[] {Integer.toString(id)});
    }

    public void deleteEventById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
         db.delete(Events.EventAdd.TABLE_NAME,
                 Events.EventAdd._ID + " = ?",
                 new String[] {Integer.toString(id)});

    }
}
