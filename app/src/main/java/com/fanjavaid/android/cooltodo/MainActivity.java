package com.fanjavaid.android.cooltodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fanjavaid.android.cooltodo.adapter.TodoAdapter;
import com.fanjavaid.android.cooltodo.data.TodoContract;
import com.fanjavaid.android.cooltodo.data.TodoDummyUtil;
import com.fanjavaid.android.cooltodo.data.TodoSqlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        TodoAdapter.TodoItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SQLiteOpenHelper mTodoOpenHelper;
    private SQLiteDatabase mDb;

    private SharedPreferences mSharedPreferences;
    private int maxListItem;

    private TodoAdapter mAdapter;

    @BindView(R.id.rv_todo_list) protected RecyclerView mTodoRecyclerView;
    @BindView(R.id.fab_add) protected FloatingActionButton mAddFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mTodoOpenHelper = new TodoSqlHelper(this);
        mDb = mTodoOpenHelper.getWritableDatabase();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initDummyData();
        initRecyclerView();

        // Action to add note
        mAddFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(addIntent);
            }
        });

        // Add swipe mechanism to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final long id = (long) viewHolder.itemView.getTag();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title_confirmation))
                        .setMessage(getString(R.string.dialog_message_confirmation))
                        .setPositiveButton(getString(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTodo(id);
                                initRecyclerView();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_negative_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                initRecyclerView();
                            }
                        });

                alertBuilder.show();
            }
        }).attachToRecyclerView(mTodoRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        maxListItem = Integer.parseInt(mSharedPreferences.getString(getString(R.string.pref_max_key), "20"));
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_todo, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        Cursor todosDataCursor = getAllTodos();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTodoRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new TodoAdapter(this, todosDataCursor, this, mSharedPreferences);
        mTodoRecyclerView.setAdapter(mAdapter);
    }

    private void initDummyData() {
        Cursor cursor = mDb.query(
                TodoContract.TodoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.getCount() == 0) {
            TodoDummyUtil.insertDummyData(mDb);
        }

        closeCursor(cursor);
    }

    private Cursor getAllTodos() {
        Cursor cursor = mDb.query(
                TodoContract.TodoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                String.valueOf(maxListItem)
        );

        return cursor;
    }

    private boolean deleteTodo(long id) {
        return mDb.delete(
                TodoContract.TodoEntry.TABLE_NAME,
                TodoContract.TodoEntry._ID + " = ?",
                new String[] { String.valueOf(id) }
        ) > 0;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null)
            cursor.close();
    }

    @Override
    public void onClickListener(long itemId) {
        Cursor cursor = mDb.query(
                TodoContract.TodoEntry.TABLE_NAME,
                null,
                TodoContract.TodoEntry._ID + " = ?",
                new String[] { String.valueOf(itemId) },
                null,
                null,
                null
        );

        cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION));
        String date = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_CREATED_DATE));

        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.ARG_ID, itemId);
        detailIntent.putExtra(DetailActivity.ARG_TITLE, title);
        detailIntent.putExtra(DetailActivity.ARG_DESC, description);
        detailIntent.putExtra(DetailActivity.ARG_DATE, date);

        startActivity(detailIntent);

        cursor.close();
    }
}
