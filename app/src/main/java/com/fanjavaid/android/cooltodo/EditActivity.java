package com.fanjavaid.android.cooltodo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fanjavaid.android.cooltodo.data.TodoContract;
import com.fanjavaid.android.cooltodo.data.TodoSqlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final int REQUEST_CODE = 3092;

    @BindView(R.id.et_title) protected EditText mTitleEditText;
    @BindView(R.id.et_description) protected EditText mDescriptionEditText;
    @BindView(R.id.btn_submit) protected Button mSubmitButton;

    private SQLiteDatabase mDb;
    private TodoSqlHelper mTodoSqlHelper;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit");

        ButterKnife.bind(this);

        mTodoSqlHelper = new TodoSqlHelper(this);
        mDb = mTodoSqlHelper.getWritableDatabase();

        mSubmitButton.setOnClickListener(this);

        initValue();
    }

    @Override
    public void onClick(View v) {
        updateTodo(
                id,
                mTitleEditText.getText().toString(),
                mDescriptionEditText.getText().toString()
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initValue() {
        Bundle bundle = getIntent().getExtras();

        id = bundle.getLong(DetailActivity.ARG_ID);
        mTitleEditText.setText(bundle.getString(DetailActivity.ARG_TITLE));
        mDescriptionEditText.setText(bundle.getString(DetailActivity.ARG_DESC));
    }

    private void updateTodo(long id, String title, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(TodoContract.TodoEntry.COLUMN_NAME, title);
        cv.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, desc);

        final int rowsUpdated = mDb.update(
                TodoContract.TodoEntry.TABLE_NAME,
                cv,
                TodoContract.TodoEntry._ID + " = ?",
                new String[] { String.valueOf(id) }
        );

        if (rowsUpdated > 0) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_info))
                    .setMessage(getString(R.string.dialog_message_info_success_updated,
                            String.valueOf(rowsUpdated)))
                    .setPositiveButton(getString(R.string.dialog_positive_button),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(RESULT_OK);

                            finish();
                        }
                    });

            alertBuilder.show();
        }

    }
}
