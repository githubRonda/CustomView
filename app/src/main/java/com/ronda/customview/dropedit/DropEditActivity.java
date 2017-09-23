package com.ronda.customview.dropedit;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ronda.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/11
 * Version: v1.0
 */

public class DropEditActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtInput;
    private List<String> mDataList;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_edit);

        initView();

        mDataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mDataList.add(10000 + i + "");
        }
    }

    private void initView() {
        mEtInput = (EditText) findViewById(R.id.et_input);
        findViewById(R.id.ib_drop_arrow).setOnClickListener(this);
    }


    //下拉箭头的点击事件
    @Override
    public void onClick(View v) {

        showPopupWindow();
    }

    /**
     * 显示 PopupWindow
     */
    private void showPopupWindow() {

        ListView listView = new ListView(this);
        listView.setBackgroundResource(R.drawable.listview_background);
        listView.setDividerHeight(0);//隐藏ItemView之间的分割线
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DropEditActivity.this, "item position: "+ position, Toast.LENGTH_SHORT).show();
                mEtInput.setText(mDataList.get(position));
                mPopupWindow.dismiss();
            }
        });

        listView.setAdapter(new MyAdapter());



        mPopupWindow = new PopupWindow(listView, mEtInput.getWidth(), 500);

        //设置点击外部或返回键，可dismiss弹框
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//设置空的(透明)背景
        mPopupWindow.showAsDropDown(mEtInput, 0, -5);

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public String getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_drop_edit, parent, false);
            }

            TextView tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.findViewById(R.id.ib_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDataList.remove(position);
                    notifyDataSetChanged();

                    //当itemView 全都删完之后，则dismiss掉弹框
                    if (mDataList.isEmpty()){
                        mPopupWindow.dismiss();
                    }
                }
            });



            tv_number.setText(getItem(position));

            return convertView;
        }
    }
}
