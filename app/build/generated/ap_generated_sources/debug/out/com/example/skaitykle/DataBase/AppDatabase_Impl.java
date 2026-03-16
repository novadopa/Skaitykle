package com.example.skaitykle.DataBase;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
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
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile BookDao _bookDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(2, "aaf0717c4cdb657962794a063d96b0ad", "15be89cd780700b2cd851aa4fb85eae8") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `User` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `first_name` TEXT NOT NULL, `last_name` TEXT NOT NULL, `email` TEXT NOT NULL, `password` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `Book` (`bid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `author` TEXT NOT NULL, `book_path` TEXT NOT NULL, `cover_uri` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'aaf0717c4cdb657962794a063d96b0ad')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `User`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `Book`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsUser = new HashMap<String, TableInfo.Column>(5);
        _columnsUser.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("first_name", new TableInfo.Column("first_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("last_name", new TableInfo.Column("last_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysUser = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesUser = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUser = new TableInfo("User", _columnsUser, _foreignKeysUser, _indicesUser);
        final TableInfo _existingUser = TableInfo.read(connection, "User");
        if (!_infoUser.equals(_existingUser)) {
          return new RoomOpenDelegate.ValidationResult(false, "User(com.example.skaitykle.DataBase.User).\n"
                  + " Expected:\n" + _infoUser + "\n"
                  + " Found:\n" + _existingUser);
        }
        final Map<String, TableInfo.Column> _columnsBook = new HashMap<String, TableInfo.Column>(6);
        _columnsBook.put("bid", new TableInfo.Column("bid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBook.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBook.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBook.put("author", new TableInfo.Column("author", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBook.put("book_path", new TableInfo.Column("book_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBook.put("cover_uri", new TableInfo.Column("cover_uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysBook = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesBook = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBook = new TableInfo("Book", _columnsBook, _foreignKeysBook, _indicesBook);
        final TableInfo _existingBook = TableInfo.read(connection, "Book");
        if (!_infoBook.equals(_existingBook)) {
          return new RoomOpenDelegate.ValidationResult(false, "Book(com.example.skaitykle.DataBase.Book).\n"
                  + " Expected:\n" + _infoBook + "\n"
                  + " Found:\n" + _existingBook);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "User", "Book");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "User", "Book");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookDao.class, BookDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
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
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public BookDao bookDao() {
    if (_bookDao != null) {
      return _bookDao;
    } else {
      synchronized(this) {
        if(_bookDao == null) {
          _bookDao = new BookDao_Impl(this);
        }
        return _bookDao;
      }
    }
  }
}
