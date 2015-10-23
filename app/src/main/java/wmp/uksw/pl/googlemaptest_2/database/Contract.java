package wmp.uksw.pl.googlemaptest_2.database;

/**
 * Created by MSI on 2015-10-20.
 */
public class Contract {
    public Contract() {

    }

    public static abstract class Markers {
        public static final String TABLE_NAME = "markers";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TITLE = "title";
    }

    //aux constants
    static final String COMMA_SEP = ",";
    static final String TEXT_TYPE = " TEXT";
    static final String INTEGER_TYPE = " INTEGER";
    static final String REAL_TYPE = " REAL";

    private static final String AUTO_INCREMENT = " AUTOINCREMENT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String FOREIGN_KEY = " FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES";

    static final String SQL_CREATE_MARKERS =
            "CREATE TABLE IF NOT EXISTS " + Markers.TABLE_NAME +
                    " (" +
                    Markers.COLUMN_ID        + INTEGER_TYPE + PRIMARY_KEY + AUTO_INCREMENT + NOT_NULL + COMMA_SEP +
                    Markers.COLUMN_LATITUDE  + REAL_TYPE                                              + COMMA_SEP +
                    Markers.COLUMN_LONGITUDE + REAL_TYPE                                              + COMMA_SEP +
                    Markers.COLUMN_TITLE     + INTEGER_TYPE                                           +
                    " )";
    static final String SQL_DELETE_MARKERS =
            "DROP TABLE IF EXISTS " + Markers.TABLE_NAME;
}