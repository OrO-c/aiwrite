package com.aiwriter.assistant.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aiwriter.assistant.data.model.WritingPreset;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WritingPresetDao_Impl implements WritingPresetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WritingPreset> __insertionAdapterOfWritingPreset;

  private final EntityDeletionOrUpdateAdapter<WritingPreset> __deletionAdapterOfWritingPreset;

  private final EntityDeletionOrUpdateAdapter<WritingPreset> __updateAdapterOfWritingPreset;

  private final SharedSQLiteStatement __preparedStmtOfDeletePresetById;

  private final SharedSQLiteStatement __preparedStmtOfClearDefaultFlags;

  public WritingPresetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWritingPreset = new EntityInsertionAdapter<WritingPreset>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `writing_presets` (`id`,`name`,`description`,`systemPrompt`,`isDefault`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WritingPreset entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getSystemPrompt());
        final int _tmp = entity.isDefault() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfWritingPreset = new EntityDeletionOrUpdateAdapter<WritingPreset>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `writing_presets` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WritingPreset entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfWritingPreset = new EntityDeletionOrUpdateAdapter<WritingPreset>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `writing_presets` SET `id` = ?,`name` = ?,`description` = ?,`systemPrompt` = ?,`isDefault` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WritingPreset entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getSystemPrompt());
        final int _tmp = entity.isDefault() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getUpdatedAt());
        statement.bindString(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeletePresetById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM writing_presets WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearDefaultFlags = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE writing_presets SET isDefault = 0";
        return _query;
      }
    };
  }

  @Override
  public Object insertPreset(final WritingPreset preset,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWritingPreset.insert(preset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePreset(final WritingPreset preset,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWritingPreset.handle(preset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePreset(final WritingPreset preset,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWritingPreset.handle(preset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePresetById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePresetById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePresetById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDefaultFlags(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDefaultFlags.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDefaultFlags.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WritingPreset>> getAllPresets() {
    final String _sql = "SELECT * FROM writing_presets ORDER BY isDefault DESC, updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"writing_presets"}, new Callable<List<WritingPreset>>() {
      @Override
      @NonNull
      public List<WritingPreset> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSystemPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "systemPrompt");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<WritingPreset> _result = new ArrayList<WritingPreset>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WritingPreset _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpSystemPrompt;
            _tmpSystemPrompt = _cursor.getString(_cursorIndexOfSystemPrompt);
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new WritingPreset(_tmpId,_tmpName,_tmpDescription,_tmpSystemPrompt,_tmpIsDefault,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPresetById(final String id,
      final Continuation<? super WritingPreset> $completion) {
    final String _sql = "SELECT * FROM writing_presets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WritingPreset>() {
      @Override
      @Nullable
      public WritingPreset call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSystemPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "systemPrompt");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final WritingPreset _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpSystemPrompt;
            _tmpSystemPrompt = _cursor.getString(_cursorIndexOfSystemPrompt);
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new WritingPreset(_tmpId,_tmpName,_tmpDescription,_tmpSystemPrompt,_tmpIsDefault,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDefaultPreset(final Continuation<? super WritingPreset> $completion) {
    final String _sql = "SELECT * FROM writing_presets WHERE isDefault = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WritingPreset>() {
      @Override
      @Nullable
      public WritingPreset call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSystemPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "systemPrompt");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final WritingPreset _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpSystemPrompt;
            _tmpSystemPrompt = _cursor.getString(_cursorIndexOfSystemPrompt);
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new WritingPreset(_tmpId,_tmpName,_tmpDescription,_tmpSystemPrompt,_tmpIsDefault,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
