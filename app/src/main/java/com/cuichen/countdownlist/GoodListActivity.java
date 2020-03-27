package com.cuichen.countdownlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.cuichen.countdownlist.adapter.BaseRecyclerAdapter;
import com.cuichen.countdownlist.adapter.BaseRecyclerHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class GoodListActivity extends AppCompatActivity {

    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        rv = findViewById(R.id.rv);
        initView();
        initData();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.addData(0,new TimeBean("附加商品、离活动结束还剩：" , 99));
                rv.scrollToPosition(0);
            }
        });
        findViewById(R.id.btnGood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    private ListAdapter mAdapter;

    private void initView() {
        mAdapter = new ListAdapter(this,R.layout.item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        Countdown();
    }

    private void initData() {
        List<TimeBean> datas = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            datas.add(new TimeBean("商品" + i + "、离活动结束还剩：", (i + 5) * i));
        }
        mAdapter.setNewData(datas);
    }

    private class ListAdapter extends BaseRecyclerAdapter<TimeBean>{

        public ListAdapter(Context context, int layoutRes) {
            super(context, layoutRes);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, List<TimeBean> items, int position) {
            ((TextView)holder.getView(R.id.tv)).setText(items.get(position).getStr());
            ((TextView)holder.getView(R.id.tvTime)).setText(items.get(position).getTime()+"s");
        }

    }

    private Handler mHandler = new Handler();
    private Runnable runnable;

    private void Countdown() {
        runnable = new Runnable() {
            @Override
            public void run() {
                List<TimeBean> deleteDatas = null; //记录需要删除的条目
                for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                    TimeBean bean = mAdapter.getDatas().get(i);
                    if (bean.getTime() > 0) {
                        bean.setTime(bean.getTime() - 1);
                        mAdapter.getDatas().set(i, bean); //时间未到的 ，只改变数据，暂不刷新条目
                    } else {
                        if (deleteDatas == null)
                            deleteDatas = new ArrayList<>();
                        deleteDatas.add(bean); //把需要删除的条目暂存起来，避免边遍历边操作集合；
                    }
                }
                if (deleteDatas != null && deleteDatas.size() != 0) { //删除条目
                    for (int i = 0; i < deleteDatas.size(); i++) {
                        mAdapter.remove(deleteDatas.get(i));
                    }
                }

                mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount(), R.id.tvTime); //刷新条目中的指定控件

                mHandler.postDelayed(runnable, 1000L);
            }
        };
        mHandler.postDelayed(runnable, 1000L);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
