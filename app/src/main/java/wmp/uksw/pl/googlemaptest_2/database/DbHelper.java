package wmp.uksw.pl.googlemaptest_2.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import wmp.uksw.pl.googlemaptest_2.models.MarkerRow;

/**
 * Created by MSI on 2015-10-20.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "map.db";

    public DbHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE_MARKERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.SQL_DELETE_MARKERS);

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {super.onOpen(db);}

    /**
     * This makes constraints work
     * @param db - SQLiteDatabase
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) // that's because setForeigkKeyConstaintsEnabled works on API >= 16
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    public MarkerRow getMarker(int id) {
        return null;
    }

    public List<MarkerRow> getAllMarkers() {
        // Gets the data repository in read mode
        SQLiteDatabase db = this.getReadableDatabase();
        // Preparing query (only for convience purposes)
        String query = "SELECT * FROM " + Contract.Markers.TABLE_NAME;
        // Preparing cursor for getting rows
        Cursor cursor = db.rawQuery(query, null);
        // Creating list
        List<MarkerRow> markerRows = new ArrayList<>();

        // Looping through all rows and selecting
        if (cursor.moveToFirst()) {
            do {
                MarkerRow markerRow = new MarkerRow();
                markerRow.setId(cursor.getInt(1));
                markerRow.setLatitude(cursor.getDouble(2));
                markerRow.setLongitude(cursor.getDouble(3));
                markerRow.setTitle(cursor.getString(4));

                // Adding to list
                markerRows.add(markerRow);
            } while(cursor.moveToNext());

            return markerRows;
        }
        else {
            return markerRows;
        }
    }
}
