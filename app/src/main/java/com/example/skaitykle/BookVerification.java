package com.example.skaitykle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skaitykle.DataBase.Book;
import com.example.skaitykle.DataBase.BooksViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin-only screen that lists all PENDING book submissions.
 * The operator can approve or reject each one, which updates
 * the Book's status in the local Room database.
 *
 * Accessible from Profile when user.isAdmin() == true.
 */
public class BookVerification extends AppCompatActivity {

    private BooksViewModel booksViewModel;
    private SubmissionAdapter adapter;
    private RecyclerView recyclerView;
    private TextView     tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_verification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Book Verification");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView     = findViewById(R.id.rvPendingBooks);
        tvEmpty          = findViewById(R.id.tvEmptyPending);
        booksViewModel   = new ViewModelProvider(this).get(BooksViewModel.class);

        adapter = new SubmissionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // LiveData from Room — auto-updates when status changes
        booksViewModel.getPendingBooks().observe(this, books -> {
            adapter.setData(books);
            boolean empty = books == null || books.isEmpty();
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ── Approve ───────────────────────────────────────────────────────────────

    private void approve(Book book) {
        booksViewModel.updateStatus(book.getBid(), Book.STATUS_APPROVED);
        Toast.makeText(this, "Approved: " + book.getTitle(), Toast.LENGTH_SHORT).show();
    }

    // ── Reject ────────────────────────────────────────────────────────────────

    private void reject(Book book) {
        final android.widget.EditText noteInput = new android.widget.EditText(this);
        noteInput.setHint("Reason (optional)");
        noteInput.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Reject \"" + book.getTitle() + "\"?")
                .setView(noteInput)
                .setPositiveButton("Reject", (d, w) ->  {
                    booksViewModel.updateStatus(book.getBid(), Book.STATUS_REJECTED);
                    Toast.makeText(this, "Rejected: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    private class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.VH> {

        private final List<Book> items = new ArrayList<>();

        void setData(List<Book> data) {
            items.clear();
            if (data != null) items.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pending_book, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvAuthor, tvGenres, tvPages, tvUserId;
            Button   btnApprove, btnReject;

            VH(@NonNull View itemView) {
                super(itemView);
                tvTitle   = itemView.findViewById(R.id.tvPendingTitle);
                tvAuthor  = itemView.findViewById(R.id.tvPendingAuthor);
                tvGenres  = itemView.findViewById(R.id.tvPendingGenres);
                tvPages   = itemView.findViewById(R.id.tvPendingPages);
                tvUserId  = itemView.findViewById(R.id.tvPendingUserId);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnReject  = itemView.findViewById(R.id.btnReject);
            }

            void bind(Book book) {
                tvTitle.setText(book.getTitle());
                tvAuthor.setText("by " + book.getAuthor());
                tvGenres.setText("Genres: " + book.getGenres());
                tvPages.setText("Pages: " + book.getTotalPages());
                tvUserId.setText("Submitted by user ID: " + book.getAddedByUserId());
                btnApprove.setOnClickListener(v -> approve(book));
                btnReject.setOnClickListener(v  -> reject(book));
            }
        }
    }
}