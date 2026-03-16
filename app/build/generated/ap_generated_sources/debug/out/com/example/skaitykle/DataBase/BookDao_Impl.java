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
public final class BookDao_Impl implements BookDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Book> __insertAdapterOfBook;

  private final EntityDeleteOrUpdateAdapter<Book> __deleteAdapterOfBook;

  private final EntityDeleteOrUpdateAdapter<Book> __updateAdapterOfBook;

  public BookDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfBook = new EntityInsertAdapter<Book>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Book` (`bid`,`title`,`description`,`author`,`book_path`,`cover_uri`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Book entity) {
        statement.bindLong(1, entity.bid);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.description);
        }
        if (entity.author == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.author);
        }
        if (entity.bookPath == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.bookPath);
        }
        if (entity.coverUri == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.coverUri);
        }
      }
    };
    this.__deleteAdapterOfBook = new EntityDeleteOrUpdateAdapter<Book>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Book` WHERE `bid` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Book entity) {
        statement.bindLong(1, entity.bid);
      }
    };
    this.__updateAdapterOfBook = new EntityDeleteOrUpdateAdapter<Book>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Book` SET `bid` = ?,`title` = ?,`description` = ?,`author` = ?,`book_path` = ?,`cover_uri` = ? WHERE `bid` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Book entity) {
        statement.bindLong(1, entity.bid);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.description);
        }
        if (entity.author == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.author);
        }
        if (entity.bookPath == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.bookPath);
        }
        if (entity.coverUri == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.coverUri);
        }
        statement.bindLong(7, entity.bid);
      }
    };
  }

  @Override
  public void insertAll(final Book... books) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfBook.insert(_connection, books);
      return null;
    });
  }

  @Override
  public void insert(final Book book) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfBook.insert(_connection, book);
      return null;
    });
  }

  @Override
  public void delete(final Book book) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfBook.handle(_connection, book);
      return null;
    });
  }

  @Override
  public void update(final Book book) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfBook.handle(_connection, book);
      return null;
    });
  }

  @Override
  public LiveData<List<Book>> getAllBooks() {
    final String _sql = "SELECT * FROM book ORDER BY bid ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"book"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfBid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "bid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfAuthor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "author");
        final int _columnIndexOfBookPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "book_path");
        final int _columnIndexOfCoverUri = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cover_uri");
        final List<Book> _result = new ArrayList<Book>();
        while (_stmt.step()) {
          final Book _item;
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpAuthor;
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null;
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor);
          }
          final String _tmpBookPath;
          if (_stmt.isNull(_columnIndexOfBookPath)) {
            _tmpBookPath = null;
          } else {
            _tmpBookPath = _stmt.getText(_columnIndexOfBookPath);
          }
          final String _tmpCoverUri;
          if (_stmt.isNull(_columnIndexOfCoverUri)) {
            _tmpCoverUri = null;
          } else {
            _tmpCoverUri = _stmt.getText(_columnIndexOfCoverUri);
          }
          _item = new Book(_tmpTitle,_tmpDescription,_tmpAuthor,_tmpBookPath,_tmpCoverUri);
          _item.bid = (int) (_stmt.getLong(_columnIndexOfBid));
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Book> loadAllIds(final int[] bookIds) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM book WHERE bid IN (");
    final int _inputSize = bookIds == null ? 1 : bookIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (bookIds == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (int _item : bookIds) {
            _stmt.bindLong(_argIndex, _item);
            _argIndex++;
          }
        }
        final int _columnIndexOfBid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "bid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfAuthor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "author");
        final int _columnIndexOfBookPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "book_path");
        final int _columnIndexOfCoverUri = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cover_uri");
        final List<Book> _result = new ArrayList<Book>();
        while (_stmt.step()) {
          final Book _item_1;
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpAuthor;
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null;
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor);
          }
          final String _tmpBookPath;
          if (_stmt.isNull(_columnIndexOfBookPath)) {
            _tmpBookPath = null;
          } else {
            _tmpBookPath = _stmt.getText(_columnIndexOfBookPath);
          }
          final String _tmpCoverUri;
          if (_stmt.isNull(_columnIndexOfCoverUri)) {
            _tmpCoverUri = null;
          } else {
            _tmpCoverUri = _stmt.getText(_columnIndexOfCoverUri);
          }
          _item_1 = new Book(_tmpTitle,_tmpDescription,_tmpAuthor,_tmpBookPath,_tmpCoverUri);
          _item_1.bid = (int) (_stmt.getLong(_columnIndexOfBid));
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Book findByTitleOrAuthor(final String title, final String author) {
    final String _sql = "SELECT * FROM book WHERE title LIKE ? OR author LIKE ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        _argIndex = 2;
        if (author == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, author);
        }
        final int _columnIndexOfBid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "bid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfAuthor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "author");
        final int _columnIndexOfBookPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "book_path");
        final int _columnIndexOfCoverUri = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cover_uri");
        final Book _result;
        if (_stmt.step()) {
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpAuthor;
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null;
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor);
          }
          final String _tmpBookPath;
          if (_stmt.isNull(_columnIndexOfBookPath)) {
            _tmpBookPath = null;
          } else {
            _tmpBookPath = _stmt.getText(_columnIndexOfBookPath);
          }
          final String _tmpCoverUri;
          if (_stmt.isNull(_columnIndexOfCoverUri)) {
            _tmpCoverUri = null;
          } else {
            _tmpCoverUri = _stmt.getText(_columnIndexOfCoverUri);
          }
          _result = new Book(_tmpTitle,_tmpDescription,_tmpAuthor,_tmpBookPath,_tmpCoverUri);
          _result.bid = (int) (_stmt.getLong(_columnIndexOfBid));
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
