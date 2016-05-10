package ua.in.devapp.ussd;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DB {

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    Resources locRes;
    private SharedPreferences sp;

    // РёРјСЏ С‚Р°Р±Р»РёС†С‹ РєРѕРјРїР°РЅРёР№, РїРѕР»СЏ Рё Р·Р°РїСЂРѕСЃ СЃРѕР·РґР°РЅРёСЏ
    private static final String OPERATOR_TABLE = "operators";
    private static final String DROP_OPERATOR_TABLE = "DROP TABLE IF EXISTS "+OPERATOR_TABLE;
    public static final String OPERATOR_COLUMN_ID = "_id";
    public static final String OPERATOR_COLUMN_NAME = "name";
    private static final String OPERATOR_TABLE_CREATE = "create table "
            + OPERATOR_TABLE + "(" + OPERATOR_COLUMN_ID
            + " integer primary key, " + OPERATOR_COLUMN_NAME + " text" + ");";

    // РёРјСЏ С‚Р°Р±Р»РёС†С‹ С‚РµР»РµС„РѕРЅРѕРІ, РїРѕР»СЏ Рё Р·Р°РїСЂРѕСЃ СЃРѕР·РґР°РЅРёСЏ
    private static final String COMAND_TABLE = "commands";
    private static final String DROP_COMAND_TABLE = "DROP TABLE IF EXISTS "+COMAND_TABLE;
    public static final String COMAND_COLUMN_ID = "_id";
    public static final String COMAND_COLUMN_COMMAND = "Command";
    public static final String COMAND_COLUMN_COMMENT = "Comment";
    //public static final String COMAND_COLUMN_TYPEC = "TypeCommand";
    public static final String COMAND_OPERATOR_ID = "id_operator";
    private static final String COMAND_TABLE_CREATE = "create table "
            + COMAND_TABLE + "(" + COMAND_COLUMN_ID
            + " integer primary key autoincrement, "
            + COMAND_COLUMN_COMMAND + " text, "
            + COMAND_COLUMN_COMMENT + " text, "
            //      + COMAND_COLUMN_TYPEC + " text, "
            + COMAND_OPERATOR_ID + " integer" + ");";
    public static final String COMAND_TABLE_DELETE = "DELETE FROM "+COMAND_TABLE;
    public static final String OPERATOR_TABLE_DELETE = "DELETE FROM "+OPERATOR_TABLE;

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
//    Map<String, String> mapPref;

    public DB(Context ctx) {
        mCtx = ctx;
        locRes = mCtx.getResources();
        sp = PreferenceManager.getDefaultSharedPreferences(mCtx);


//        mapPref = new HashMap<String, String>();
//        mapPref.put("life","1");
//        mapPref.put("mts","3");
//        mapPref.put("ks","2");
    }

    // РѕС‚РєСЂС‹РІР°РµРј РїРѕРґРєР»СЋС‡РµРЅРёРµ
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // Р·Р°РєСЂС‹РІР°РµРј РїРѕРґРєР»СЋС‡РµРЅРёРµ
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    // РґР°РЅРЅС‹Рµ РїРѕ РєРѕРјРїР°РЅРёСЏРј
    public Cursor getOperatorData() {
    // переменные для query
        String[] columns = null;
        String selection = "";
        String[] selectionArgs = new String[1];
        selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = OPERATOR_COLUMN_ID;

        //java.util.Set<Map.Entry<String, String>> set = mapPref.entrySet();
        int countSel = 0;
        boolean prefVal;
        String bufParam = "";
        /*
        for (Map.Entry<String, String> me : set) {
            prefVal = sp.getBoolean(me.getKey(), locRes.getBoolean(R.bool.myPrefDefault));
            if (prefVal){
                //if(!bufParam.equals("")) bufParam = bufParam.concat(",");
                if (countSel > 0 ) selection = selection.concat(" or ");
                selection = selection.concat(OPERATOR_COLUMN_ID + " = " + me.getValue());
                //bufParam = bufParam.concat(me.getValue());
                countSel++;
            }
        }
        */

        for(Operators op : Operators.values()) {
            prefVal = sp.getBoolean(op.name(), locRes.getBoolean(R.bool.myPrefDefault));
            if (prefVal){
                //if(!bufParam.equals("")) bufParam = bufParam.concat(",");
                if (countSel > 0 ) selection = selection.concat(" or ");
                selection = selection.concat(OPERATOR_COLUMN_ID + " = " + op.getId());
                //bufParam = bufParam.concat(me.getValue());
                countSel++;
            }
        }

        /*if (countSel == 0){
            selectionArgs = null;
            selection = null;
        }
        else{
            selectionArgs[0] = bufParam;
            selection = ""+OPERATOR_COLUMN_ID + " in (?)";
        }
        */

        return mDB.query(OPERATOR_TABLE, null, selection, selectionArgs, null, null, orderBy);
    }

    // РґР°РЅРЅС‹Рµ РїРѕ С‚РµР»РµС„РѕРЅР°Рј РєРѕРЅРєСЂРµС‚РЅРѕР№ РіСЂСѓРїРїС‹
    public Cursor getCommandData(int operatorID) {


        Cursor c = mDB.query(COMAND_TABLE, null, COMAND_OPERATOR_ID + " = "
                + operatorID, null, null, null, null);
        return c;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(OPERATOR_TABLE_CREATE);
            db.execSQL(COMAND_TABLE_CREATE);

            LoadData(db);
            /*
            // РЅР°Р·РІР°РЅРёСЏ РєРѕРјРїР°РЅРёР№ (РіСЂСѓРїРї)
            String[] companies = new String[] { "mts", "Life", "KievStar" };

            // СЃРѕР·РґР°РµРј Рё Р·Р°РїРѕР»РЅСЏРµРј С‚Р°Р±Р»РёС†Сѓ РєРѕРјРїР°РЅРёР№
            db.execSQL(OPERATOR_TABLE_CREATE);
            for (int i = 0; i < companies.length; i++) {
                cv.put(OPERATOR_COLUMN_ID, i + 1);
                cv.put(OPERATOR_COLUMN_NAME, companies[i]);
                db.insert(OPERATOR_TABLE, null, cv);
            }

            // РЅР°Р·РІР°РЅРёСЏ С‚РµР»РµС„РѕРЅРѕРІ (СЌР»РµРјРµРЅС‚РѕРІ)
            String[] commMTS = new String[] { "*111#", "*111#",
                    "*111#", "*111#" };
            String[] commLife = new String[] { "*222#", "*222#",
                    "*222#" };
            String[] commKS = new String[] { "*333#", "*333#",
                    "*333#", "*333#" };

            // СЃРѕР·РґР°РµРј Рё Р·Р°РїРѕР»РЅСЏРµРј С‚Р°Р±Р»РёС†Сѓ С‚РµР»РµС„РѕРЅРѕРІ
            db.execSQL(COMAND_TABLE_CREATE);

            cv.clear();
            for (int i = 0; i < commMTS.length; i++) {
                cv.put(COMAND_OPERATOR_ID, 1);
                cv.put(COMAND_COLUMN_COMMAND, commMTS[i]);
                db.insert(COMAND_TABLE, null, cv);
            }
            for (int i = 0; i < commLife.length; i++) {
                cv.put(COMAND_OPERATOR_ID, 2);
                cv.put(COMAND_COLUMN_COMMAND, commLife[i]);
                db.insert(COMAND_TABLE, null, cv);
            }
            for (int i = 0; i < commKS.length; i++) {
                cv.put(COMAND_OPERATOR_ID, 3);
                cv.put(COMAND_COLUMN_COMMAND, commKS[i]);
                db.insert(COMAND_TABLE, null, cv);
            }

            */
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_OPERATOR_TABLE);
            db.execSQL(DROP_COMAND_TABLE);
            onCreate(db);
        }
    }

    public void ReLoadData(){
        DeleteData();
        LoadData(mDB);
    }

    public void DeleteData(){
        mDB.execSQL(COMAND_TABLE_DELETE);
        mDB.execSQL(OPERATOR_TABLE_DELETE);
    }

    public void LoadData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        Map<String, Integer> hmDb = new HashMap<String, Integer>();

// РџРѕРјРµС‰Р°РµРј РґР°РЅРЅС‹Рµ РЅР° РєР°СЂС‚РѕС‡РєСѓ
//        hmDb.put("Life", R.raw.life);
//        hmDb.put("MTS",R.raw.mtc);
//        hmDb.put("Kievstar",R.raw.ks);

//        hmDb.put(locRes.getString(R.string.life_title), R.raw.life);
//        hmDb.put(locRes.getString(R.string.mts_title),R.raw.mtc);
//        hmDb.put(locRes.getString(R.string.ks_title),R.raw.ks);

// РџРѕР»СѓС‡Р°РµРј РЅР°Р±РѕСЂ СЌР»РµРјРµРЅС‚РѕРІ
        java.util.Set<Map.Entry<String, Integer>> set = hmDb.entrySet();
        ArrayList listDB = new ArrayList();
// РћС‚РѕР±СЂР°Р·РёРј РЅР°Р±РѕСЂ
        int countOperator = 0;

        //for (Map.Entry<String, Integer> me : set) {
        for(Operators op : Operators.values()) {
            countOperator++;
            cv.put(OPERATOR_COLUMN_ID, op.getId());
            cv.put(OPERATOR_COLUMN_NAME, locRes.getString(op.getTitle()));
            db.insert(OPERATOR_TABLE, null, cv);

            int tempRaw = 0;
            if(op.getId() == 1){
                tempRaw = R.raw.ks;
            }else if(op.getId() == 2){
                tempRaw = R.raw.life;
            }else if(op.getId() == 3){
                tempRaw = R.raw.mtc;
            }

            InputStream inStream = locRes.openRawResource(tempRaw);//R.raw.ks
            InputStreamReader sr = new InputStreamReader(inStream);
            // СЃРѕР·РґР°РµРј Р±СѓС„РµСЂ РґР»СЏ С‡С‚РµРЅРёСЏ С„Р°Р№Р»Р°
            BufferedReader reader = new BufferedReader(sr);

            String str;

            // С‡РёС‚Р°РµРј РґР°РЅРЅС‹Рµ РІ Р±СѓС„РµСЂ
            try {
                while ((str = reader.readLine()) != null) {

                    String[] strTemp = str.split(";");
                    String txtCommand = strTemp[0];
                    String txtComment = strTemp[1];
                    ContentValues cvC = new ContentValues();

                    cvC.put(COMAND_COLUMN_COMMENT, txtComment);
                    cvC.put(COMAND_COLUMN_COMMAND, txtCommand);
                    cvC.put(COMAND_OPERATOR_ID, countOperator);
                    //cvC.put("TypeCommand", TypeCommand);
                    long rowID = db.insert(COMAND_TABLE, null, cvC);
                    // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ ID
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                inStream.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

}