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
import com.aiwriter.assistant.data.model.GeneratedText;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class GeneratedTextDao_Impl implements GeneratedTextDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GeneratedText> __insertionAdapterOfGeneratedText;

  private final EntityDeletionOrUpdateAdapter<GeneratedText> __deletionAdapterOfGeneratedText;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTextById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTexts;

  public GeneratedTextDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGeneratedText = new EntityInsertionAdapter<GeneratedText>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `generated_texts` (`id`,`input`,`presetId`,`presetName`,`version1`,`version2`,`version3`,`style1Label`,`style2Label`,`style3Label`,`modelProvider`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeneratedText entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getInput());
        statement.bindString(3, entity.getPresetId());
        statement.bindString(4, entity.getPresetName());
        statement.bindString(5, entity.getVersion1());
        statement.bindString(6, entity.getVersion2());
        statement.bindString(7, entity.getVersion3());
        statement.bindString(8, entity.getStyle1Label());
        statement.bindString(9, entity.getStyle2Label());
        statement.bindString(10, entity.getStyle3Label());
        statement.bindString(11, entity.getModelProvider());
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfGeneratedText = new EntityDeletionOrUpdateAdapter<GeneratedText>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `generated_texts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeneratedText entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTextById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM generated_texts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTexts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM generated_texts";
        return _query;
      }
    };
  }

  @Override
  public Object insertText(final GeneratedText text, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGeneratedText.insert(text);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteText(final GeneratedText text, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGeneratedText.handle(text);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTextById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTextById.acquire();
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
          __preparedStmtOfDeleteTextById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTexts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTexts.acquire();
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
          __preparedStmtOfDeleteAllTexts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GeneratedText>> getRecentTexts(final int limit) {
    final String _sql = "SELECT * FROM generated_texts ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"generated_texts"}, new Callable<List<GeneratedText>>() {
      @Override
      @NonNull
      public List<GeneratedText> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfInput = CursorUtil.getColumnIndexOrThrow(_cursor, "input");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfPresetName = CursorUtil.getColumnIndexOrThrow(_cursor, "presetName");
          final int _cursorIndexOfVersion1 = CursorUtil.getColumnIndexOrThrow(_cursor, "version1");
          final int _cursorIndexOfVersion2 = CursorUtil.getColumnIndexOrThrow(_cursor, "version2");
          final int _cursorIndexOfVersion3 = CursorUtil.getColumnIndexOrThrow(_cursor, "version3");
          final int _cursorIndexOfStyle1Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style1Label");
          final int _cursorIndexOfStyle2Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style2Label");
          final int _cursorIndexOfStyle3Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style3Label");
          final int _cursorIndexOfModelProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "modelProvider");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<GeneratedText> _result = new ArrayList<GeneratedText>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GeneratedText _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpInput;
            _tmpInput = _cursor.getString(_cursorIndexOfInput);
            final String _tmpPresetId;
            _tmpPresetId = _cursor.getString(_cursorIndexOfPresetId);
            final String _tmpPresetName;
            _tmpPresetName = _cursor.getString(_cursorIndexOfPresetName);
            final String _tmpVersion1;
            _tmpVersion1 = _cursor.getString(_cursorIndexOfVersion1);
            final String _tmpVersion2;
            _tmpVersion2 = _cursor.getString(_cursorIndexOfVersion2);
            final String _tmpVersion3;
            _tmpVersion3 = _cursor.getString(_cursorIndexOfVersion3);
            final String _tmpStyle1Label;
            _tmpStyle1Label = _cursor.getString(_cursorIndexOfStyle1Label);
            final String _tmpStyle2Label;
            _tmpStyle2Label = _cursor.getString(_cursorIndexOfStyle2Label);
            final String _tmpStyle3Label;
            _tmpStyle3Label = _cursor.getString(_cursorIndexOfStyle3Label);
            final String _tmpModelProvider;
            _tmpModelProvider = _cursor.getString(_cursorIndexOfModelProvider);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new GeneratedText(_tmpId,_tmpInput,_tmpPresetId,_tmpPresetName,_tmpVersion1,_tmpVersion2,_tmpVersion3,_tmpStyle1Label,_tmpStyle2Label,_tmpStyle3Label,_tmpModelProvider,_tmpCreatedAt);
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
  public Object getTextById(final String id,
      final Continuation<? super GeneratedText> $completion) {
    final String _sql = "SELECT * FROM generated_texts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GeneratedText>() {
      @Override
      @Nullable
      public GeneratedText call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfInput = CursorUtil.getColumnIndexOrThrow(_cursor, "input");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfPresetName = CursorUtil.getColumnIndexOrThrow(_cursor, "presetName");
          final int _cursorIndexOfVersion1 = CursorUtil.getColumnIndexOrThrow(_cursor, "version1");
          final int _cursorIndexOfVersion2 = CursorUtil.getColumnIndexOrThrow(_cursor, "version2");
          final int _cursorIndexOfVersion3 = CursorUtil.getColumnIndexOrThrow(_cursor, "version3");
          final int _cursorIndexOfStyle1Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style1Label");
          final int _cursorIndexOfStyle2Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style2Label");
          final int _cursorIndexOfStyle3Label = CursorUtil.getColumnIndexOrThrow(_cursor, "style3Label");
          final int _cursorIndexOfModelProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "modelProvider");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final GeneratedText _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpInput;
            _tmpInput = _cursor.getString(_cursorIndexOfInput);
            final String _tmpPresetId;
            _tmpPresetId = _cursor.getString(_cursorIndexOfPresetId);
            final String _tmpPresetName;
            _tmpPresetName = _cursor.getString(_cursorIndexOfPresetName);
            final String _tmpVersion1;
            _tmpVersion1 = _cursor.getString(_cursorIndexOfVersion1);
            final String _tmpVersion2;
            _tmpVersion2 = _cursor.getString(_cursorIndexOfVersion2);
            final String _tmpVersion3;
            _tmpVersion3 = _cursor.getString(_cursorIndexOfVersion3);
            final String _tmpStyle1Label;
            _tmpStyle1Label = _cursor.getString(_cursorIndexOfStyle1Label);
            final String _tmpStyle2Label;
            _tmpStyle2Label = _cursor.getString(_cursorIndexOfStyle2Label);
            final String _tmpStyle3Label;
            _tmpStyle3Label = _cursor.getString(_cursorIndexOfStyle3Label);
            final String _tmpModelProvider;
            _tmpModelProvider = _cursor.getString(_cursorIndexOfModelProvider);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new GeneratedText(_tmpId,_tmpInput,_tmpPresetId,_tmpPresetName,_tmpVersion1,_tmpVersion2,_tmpVersion3,_tmpStyle1Label,_tmpStyle2Label,_tmpStyle3Label,_tmpModelProvider,_tmpCreatedAt);
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
  public Object getTextCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM generated_texts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
