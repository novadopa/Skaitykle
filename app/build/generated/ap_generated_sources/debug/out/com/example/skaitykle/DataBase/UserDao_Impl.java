package com.example.skaitykle.DataBase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<User> __insertAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __deleteAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __updateAdapterOfUser;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUser = new EntityInsertAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `User` (`uid`,`first_name`,`last_name`,`email`,`password`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.uid);
        if (entity.firstName == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.firstName);
        }
        if (entity.lastName == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.lastName);
        }
        if (entity.email == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.email);
        }
        if (entity.password == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.password);
        }
      }
    };
    this.__deleteAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `User` WHERE `uid` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.uid);
      }
    };
    this.__updateAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `User` SET `uid` = ?,`first_name` = ?,`last_name` = ?,`email` = ?,`password` = ? WHERE `uid` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.uid);
        if (entity.firstName == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.firstName);
        }
        if (entity.lastName == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.lastName);
        }
        if (entity.email == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.email);
        }
        if (entity.password == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.password);
        }
        statement.bindLong(6, entity.uid);
      }
    };
  }

  @Override
  public void insertAll(final User... users) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfUser.insert(_connection, users);
      return null;
    });
  }

  @Override
  public void insert(final User user) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfUser.insert(_connection, user);
      return null;
    });
  }

  @Override
  public void delete(final User user) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfUser.handle(_connection, user);
      return null;
    });
  }

  @Override
  public void update(final User users) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfUser.handle(_connection, users);
      return null;
    });
  }

  @Override
  public LiveData<List<User>> getAllUsers() {
    final String _sql = "SELECT * FROM user ORDER BY uid ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfFirstName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "first_name");
        final int _columnIndexOfLastName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "last_name");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          final String _tmpFirstName;
          if (_stmt.isNull(_columnIndexOfFirstName)) {
            _tmpFirstName = null;
          } else {
            _tmpFirstName = _stmt.getText(_columnIndexOfFirstName);
          }
          final String _tmpLastName;
          if (_stmt.isNull(_columnIndexOfLastName)) {
            _tmpLastName = null;
          } else {
            _tmpLastName = _stmt.getText(_columnIndexOfLastName);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _item = new User(_tmpFirstName,_tmpLastName,_tmpEmail,_tmpPassword);
          _item.uid = (int) (_stmt.getLong(_columnIndexOfUid));
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<User> loadAllIds(final int[] userIds) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM user WHERE uid IN (");
    final int _inputSize = userIds == null ? 1 : userIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userIds == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (int _item : userIds) {
            _stmt.bindLong(_argIndex, _item);
            _argIndex++;
          }
        }
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfFirstName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "first_name");
        final int _columnIndexOfLastName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "last_name");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item_1;
          final String _tmpFirstName;
          if (_stmt.isNull(_columnIndexOfFirstName)) {
            _tmpFirstName = null;
          } else {
            _tmpFirstName = _stmt.getText(_columnIndexOfFirstName);
          }
          final String _tmpLastName;
          if (_stmt.isNull(_columnIndexOfLastName)) {
            _tmpLastName = null;
          } else {
            _tmpLastName = _stmt.getText(_columnIndexOfLastName);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _item_1 = new User(_tmpFirstName,_tmpLastName,_tmpEmail,_tmpPassword);
          _item_1.uid = (int) (_stmt.getLong(_columnIndexOfUid));
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public User findByName(final String first, final String last) {
    final String _sql = "SELECT * FROM user WHERE first_name LIKE ? AND last_name LIKE ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (first == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, first);
        }
        _argIndex = 2;
        if (last == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, last);
        }
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfFirstName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "first_name");
        final int _columnIndexOfLastName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "last_name");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final User _result;
        if (_stmt.step()) {
          final String _tmpFirstName;
          if (_stmt.isNull(_columnIndexOfFirstName)) {
            _tmpFirstName = null;
          } else {
            _tmpFirstName = _stmt.getText(_columnIndexOfFirstName);
          }
          final String _tmpLastName;
          if (_stmt.isNull(_columnIndexOfLastName)) {
            _tmpLastName = null;
          } else {
            _tmpLastName = _stmt.getText(_columnIndexOfLastName);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpPassword;
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _tmpPassword = null;
          } else {
            _tmpPassword = _stmt.getText(_columnIndexOfPassword);
          }
          _result = new User(_tmpFirstName,_tmpLastName,_tmpEmail,_tmpPassword);
          _result.uid = (int) (_stmt.getLong(_columnIndexOfUid));
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
