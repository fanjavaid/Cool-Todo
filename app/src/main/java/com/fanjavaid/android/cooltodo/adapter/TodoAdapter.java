package com.fanjavaid.android.cooltodo.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanjavaid.android.cooltodo.R;
import com.fanjavaid.android.cooltodo.data.TodoContract;
import com.fanjavaid.android.cooltodo.util.CommonUtil;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fanjavaid on 6/23/17.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    public static final String TAG = TodoAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;
    private TodoItemClickListener mListener;

    private SharedPreferences mSharedPreferences;

    public TodoAdapter(Context mContext, Cursor mCursor, TodoItemClickListener mListener, SharedPreferences mSharedPreferences) {
        this.mContext = mContext;
        this.mCursor = mCursor;
        this.mListener = mListener;
        this.mSharedPreferences = mSharedPreferences;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemListLayout = R.layout.item_todo;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(itemListLayout, parent, false);
        TodoViewHolder viewHolder = new TodoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        if (mCursor != null) {
            if (!mCursor.moveToPosition(position))
                return;

            long id = mCursor.getLong(mCursor.getColumnIndex(TodoContract.TodoEntry._ID));
            String title = mCursor.getString(mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME));
            String description = mCursor.getString(mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION));
            String date = mCursor.getString(mCursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_CREATED_DATE));

            holder.itemView.setTag(id);

            holder.mTitleTextView.setText(title);
            holder.mDescriptionTextView.setText(description);

            try {
                holder.mDateTextView.setText(CommonUtil.formatDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            boolean isShowDate = mSharedPreferences.getBoolean(mContext
                    .getString(R.string.pref_show_date_key), true);

            if (isShowDate) {
                holder.mDateTextView.setVisibility(View.VISIBLE);
            } else {
                holder.mDateTextView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_title) protected TextView mTitleTextView;
        @BindView(R.id.tv_summary) protected TextView mDescriptionTextView;
        @BindView(R.id.tv_date) protected TextView mDateTextView;

        public TodoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            mCursor.moveToPosition(position);
            long id = mCursor.getLong(mCursor.getColumnIndex(TodoContract.TodoEntry._ID));

            Log.e(TAG, "onClick: " + id);

            mListener.onClickListener(id);
        }
    }

    public interface TodoItemClickListener {
        void onClickListener(long itemId);
    }
}
