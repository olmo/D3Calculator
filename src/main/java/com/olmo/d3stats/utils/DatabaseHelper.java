package com.olmo.d3stats.utils;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "d3stats.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 7;

    // the DAO object we use to access the SimpleData table
    private Dao<Hero, Integer> heroDao = null;
    private Dao<Item, Integer> itemDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Hero.class);
            TableUtils.createTable(connectionSource, Item.class);

            // here we try inserting data in the on-create as a test
            /*Dao<Hero, Integer> dao = getSimpleDataDao();
            long millis = System.currentTimeMillis();
            // create some entries in the onCreate
            Hero simple = new Hero(millis);
            dao.create(simple);
            simple = new Hero(millis + 1);
            dao.create(simple);
            Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);*/
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Hero.class, true);
            TableUtils.dropTable(connectionSource, Item.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Hero, Integer> getHeroDao() throws SQLException {
        if (heroDao == null) {
            heroDao = getDao(Hero.class);
        }
        return heroDao;
    }

    public Dao<Item, Integer> getItemDao() throws SQLException {
        if (itemDao == null) {
            itemDao = getDao(Item.class);
        }
        return itemDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        heroDao = null;
        itemDao = null;
    }
}
