package com.aiwriter.assistant.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.aiwriter.assistant.data.database.dao.GeneratedTextDao;
import com.aiwriter.assistant.data.database.dao.GeneratedTextDao_Impl;
import com.aiwriter.assistant.data.database.dao.WritingPresetDao;
import com.aiwriter.assistant.data.database.dao.WritingPresetDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile WritingPresetDao _writingPresetDao;

  private volatile GeneratedTextDao _generatedTextDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `writing_presets` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `systemPrompt` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `generated_texts` (`id` TEXT NOT NULL, `input` TEXT NOT NULL, `presetId` TEXT NOT NULL, `presetName` TEXT NOT NULL, `version1` TEXT NOT NULL, `version2` TEXT NOT NULL, `version3` TEXT NOT NULL, `style1Label` TEXT NOT NULL, `style2Label` TEXT NOT NULL, `style3Label` TEXT NOT NULL, `modelProvider` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7626795d6ce28eb1c6daed602814a4b7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `writing_presets`");
        db.execSQL("DROP TABLE IF EXISTS `generated_texts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsWritingPresets = new HashMap<String, TableInfo.Column>(7);
        _columnsWritingPresets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("systemPrompt", new TableInfo.Column("systemPrompt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWritingPresets.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWritingPresets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWritingPresets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWritingPresets = new TableInfo("writing_presets", _columnsWritingPresets, _foreignKeysWritingPresets, _indicesWritingPresets);
        final TableInfo _existingWritingPresets = TableInfo.read(db, "writing_presets");
        if (!_infoWritingPresets.equals(_existingWritingPresets)) {
          return new RoomOpenHelper.ValidationResult(false, "writing_presets(com.aiwriter.assistant.data.model.WritingPreset).\n"
                  + " Expected:\n" + _infoWritingPresets + "\n"
                  + " Found:\n" + _existingWritingPresets);
        }
        final HashMap<String, TableInfo.Column> _columnsGeneratedTexts = new HashMap<String, TableInfo.Column>(12);
        _columnsGeneratedTexts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("input", new TableInfo.Column("input", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("presetId", new TableInfo.Column("presetId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("presetName", new TableInfo.Column("presetName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("version1", new TableInfo.Column("version1", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("version2", new TableInfo.Column("version2", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("version3", new TableInfo.Column("version3", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("style1Label", new TableInfo.Column("style1Label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("style2Label", new TableInfo.Column("style2Label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("style3Label", new TableInfo.Column("style3Label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("modelProvider", new TableInfo.Column("modelProvider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeneratedTexts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGeneratedTexts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGeneratedTexts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGeneratedTexts = new TableInfo("generated_texts", _columnsGeneratedTexts, _foreignKeysGeneratedTexts, _indicesGeneratedTexts);
        final TableInfo _existingGeneratedTexts = TableInfo.read(db, "generated_texts");
        if (!_infoGeneratedTexts.equals(_existingGeneratedTexts)) {
          return new RoomOpenHelper.ValidationResult(false, "generated_texts(com.aiwriter.assistant.data.model.GeneratedText).\n"
                  + " Expected:\n" + _infoGeneratedTexts + "\n"
                  + " Found:\n" + _existingGeneratedTexts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7626795d6ce28eb1c6daed602814a4b7", "dec1f22e68d424244a9433ab5321a8e3");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "writing_presets","generated_texts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `writing_presets`");
      _db.execSQL("DELETE FROM `generated_texts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(WritingPresetDao.class, WritingPresetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GeneratedTextDao.class, GeneratedTextDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public WritingPresetDao writingPresetDao() {
    if (_writingPresetDao != null) {
      return _writingPresetDao;
    } else {
      synchronized(this) {
        if(_writingPresetDao == null) {
          _writingPresetDao = new WritingPresetDao_Impl(this);
        }
        return _writingPresetDao;
      }
    }
  }

  @Override
  public GeneratedTextDao generatedTextDao() {
    if (_generatedTextDao != null) {
      return _generatedTextDao;
    } else {
      synchronized(this) {
        if(_generatedTextDao == null) {
          _generatedTextDao = new GeneratedTextDao_Impl(this);
        }
        return _generatedTextDao;
      }
    }
  }
}
