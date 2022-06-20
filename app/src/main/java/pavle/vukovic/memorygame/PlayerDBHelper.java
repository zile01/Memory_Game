package pavle.vukovic.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDBHelper extends SQLiteOpenHelper {
    private final String TABLE_NAME = "GAMES";
    public static final String COLUMN_GAME_ID = "GameID";
    public static final String COLUMN_PLAYER_USERNAME = "Username";
    public static final String COLUMN_PLAYER_EMAIL = "Email";
    public static final String COLUMN_POINTS = "Points";

    public PlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_GAME_ID + " TEXT, " +
                COLUMN_PLAYER_USERNAME + " TEXT, " +
                COLUMN_PLAYER_EMAIL + " TEXT," +
                COLUMN_POINTS + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void insert(String game_id, String username, String email, String points) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_ID, game_id);
        values.put(COLUMN_PLAYER_USERNAME, username);
        values.put(COLUMN_PLAYER_EMAIL, email);
        values.put(COLUMN_POINTS, points);

        db.insert(TABLE_NAME, null, values);
        close();
    }

    public void delete(String username) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_PLAYER_USERNAME + " =?", new String[]{username});
        close();
    }

    public List<Element> readPlayers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }

        List<Element> players = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            boolean flag_allow = true;

            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_USERNAME));

            for(Element player:players){
                if(player.getmText1_1().equals(username)){
                    flag_allow = false;
                }
            }

            if(flag_allow == false){
                continue;
            }else{
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_EMAIL));

                List<String> points = readResultForPlayer(username);

                players.add(new Element(username, email, points));
            }
        }

        close();
        return players;
    }

    public List<String> readResultForPlayer(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_PLAYER_USERNAME + " =?", new String[] {username}, null, null, null);
        List<String> points = new ArrayList<>();

        if (cursor.getCount() <= 0) {
            return null;
        }
        Log.d("cursor",String.valueOf(cursor));

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            points.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POINTS)));
        }

        close();

        return points;
    }
}