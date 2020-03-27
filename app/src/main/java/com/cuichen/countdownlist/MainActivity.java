package com.cuichen.countdownlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认单计时器刷新列表 RecyclerView + BaseRecyclerViewAdapterHelper
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.rv);
        initView();
        initData();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quickAdapter.addData(0,new TimeBean("附加商品、离活动结束还剩：" , 99));
                rv.scrollToPosition(0);
            }
        });
        findViewById(R.id.btnGood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GoodListActivity.class));
            }
        });
    }

    private QuickAdapter quickAdapter;

    private void initView() {
        if (quickAdapter == null)quickAdapter = new QuickAdapter(R.layout.item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(quickAdapter);
        rv.setItemAnimator(null);
        Countdown();
    }

    private void initData() {
        List<TimeBean> datas = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            datas.add(new TimeBean("商品" + i + "、离活动结束还剩：", (i + 5) * i));
        }
        quickAdapter.setNewData(datas);
    }


    private class QuickAdapter extends BaseQuickAdapter<TimeBean, BaseViewHolder> {

        public QuickAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder vh, TimeBean datas) {
            vh.setText(R.id.tv, datas.getStr() + "");
            vh.setText(R.id.tvTime, datas.getTime() + "s");
        }
    }

    private Handler mHandler = new Handler();
    private Runnable runnable;

    private void Countdown() {
        runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < quickAdapter.getData().size(); i++) {
                    TimeBean bean = quickAdapter.getData().get(i);
                    if (bean.getTime() > 0) {
                        bean.setTime(bean.getTime() - 1);
                        quickAdapter.setData(i, bean);
                    } else {
                        quickAdapter.remove(i); //时间到，移除条目
                    }
                }
                mHandler.postDelayed(runnable, 1000L);
            }
        };
        mHandler.postDelayed(runnable, 1000L);
    }


    private void Countdown2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    SystemClock.sleep(1000L);
                    for (int i = 0; i < quickAdapter.getData().size(); i++) {
                        final TimeBean bean = quickAdapter.getData().get(i);
                        final int finalI = i;
                        if (bean.getTime() > 0) {
                            bean.setTime(bean.getTime() - 1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    quickAdapter.setData(finalI, bean);
                                }
                            });
                        } else {
                            // 当时间是0时 移除条目（子线程加睡眠模式移除条目有问题，原因时数据源未更新）
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    quickAdapter.remove(finalI);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
