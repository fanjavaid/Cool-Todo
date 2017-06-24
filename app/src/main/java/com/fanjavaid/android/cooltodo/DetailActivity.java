package com.fanjavaid.android.cooltodo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fanjavaid.android.cooltodo.data.TodoContract;
import com.fanjavaid.android.cooltodo.data.TodoSqlHelper;
import com.fanjavaid.android.cooltodo.util.CommonUtil;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";
    public static final String ARG_DESC = "description";
    public static final String ARG_DATE = "created_date";

    @BindView(R.id.tv_title) protected TextView mTitleTextView;
    @BindView(R.id.tv_summary) protected TextView mDescriptionTextView;
    @BindView(R.id.tv_date) protected TextView mDateTextView;
    @BindView(R.id.fab_edit) protected FloatingActionButton mEditFloatingButton;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Detail");

        mBundle = getIntent().getExtras();
        initValue();

        mEditFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(DetailActivity.this, EditActivity.class);
                editIntent.putExtras(mBundle);
                startActivityForResult(editIntent, EditActivity.REQUEST_CODE);
            }
        });
    }

    private void initValue() {
        mTitleTextView.setText(mBundle.getString(ARG_TITLE));
        mDescriptionTextView.setText(mBundle.getString(ARG_DESC));

        try {
            mDateTextView.setText(CommonUtil.formatDate(mBundle.getString(ARG_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_todo, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);

            return true;
        }

        if (itemId == R.id.action_settings) {
            Intent settingIntent = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(settingIntent);

            return true;
        }

        if (itemId == R.id.action_share) {
            startActivity(shareTask());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent shareTask() {
        String subject = mTitleTextView.getText().toString();
        String text = mDateTextView.getText().toString() + "\n\n" +
                mDescriptionTextView.getText().toString();

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setSubject(subject)
                .setText(text)
                .setChooserTitle("Share Task")
                .getIntent();

        return shareIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Do update from database
                SQLiteDatabase db = new TodoSqlHelper(this).getReadableDatabase();
                Cursor cursor = db.query(
                        TodoContract.TodoEntry.TABLE_NAME,
                        null,
                        TodoContract.TodoEntry._ID + " = ?",
                        new String[] { String.valueOf(mBundle.getLong(ARG_ID)) },
                        null,
                        null,
                        null
                );

                if (cursor != null) {
                    cursor.moveToFirst();

                    String title = cursor.getString(cursor.getColumnIndex(
                            TodoContract.TodoEntry.COLUMN_NAME));
                    String desc = cursor.getString(cursor.getColumnIndex(
                            TodoContract.TodoEntry.COLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndex(
                            TodoContract.TodoEntry.COLUMN_CREATED_DATE));

                    mTitleTextView.setText(title);

                    try {
                        mDateTextView.setText(CommonUtil.formatDate(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    mDescriptionTextView.setText(desc);
                }

                // close resouces
                cursor.close();
                db.close();
            }

        }

    }
}
