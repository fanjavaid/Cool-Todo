package com.fanjavaid.android.cooltodo;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.fanjavaid.android.cooltodo.data.TodoContract;
import com.fanjavaid.android.cooltodo.data.TodoSqlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_title) protected EditText mTitleEditText;
    @BindView(R.id.et_description) protected EditText mDescriptionEditText;
    @BindView(R.id.btn_submit) protected Button mSubmitButton;

    private SQLiteDatabase mDb;
    private TodoSqlHelper mTodoSqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);

        mTodoSqlHelper = new TodoSqlHelper(this);
        mDb = mTodoSqlHelper.getWritableDatabase();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNote(String title, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(TodoContract.TodoEntry.COLUMN_NAME, title);
        cv.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, desc);

        long returnValue = mDb.insert(TodoContract.TodoEntry.TABLE_NAME, null, cv);

        if (returnValue > -1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_info))
                    .setMessage(getString(R.string.dialog_message_info_success))
                    .setPositiveButton(getString(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            NavUtils.navigateUpFromSameTask(AddActivity.this);
                        }
                    });

            alert.show();
        }

        mDb.close();
    }

    @Override
    public void onClick(View v) {
        String title = mTitleEditText.getText().toString();
        String desc = mDescriptionEditText.getText().toString();

        addNote(title, desc);
    }
}
