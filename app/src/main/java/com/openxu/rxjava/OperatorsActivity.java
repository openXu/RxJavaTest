package com.openxu.rxjava;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.openxu.rxjava.operators.OperatorsBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * RxJava －－（二.操作符）
 * 常用的操作符列表：
 *    创建操作 Create, Defer, Empty/Never/Throw, From, Interval, Just, Range, Repeat, Start, Timer
 *    变换操作 Buffer, FlatMap, GroupBy, Map, Scan和Window
 *    过滤操作 Debounce, Distinct, ElementAt, Filter, First, IgnoreElements, Last, Sample, Skip, SkipLast, Take, TakeLast
 *    组合操作 And/Then/When, CombineLatest, Join, Merge, StartWith, Switch, Zip
 *    错误处理 Catch和Retry
 *    辅助操作 Delay, Do, Materialize/Dematerialize, ObserveOn, Serialize, Subscribe, SubscribeOn, TimeInterval, Timeout, Timestamp, Using
 *    条件和布尔操作 All, Amb, Contains, DefaultIfEmpty, SequenceEqual, SkipUntil, SkipWhile, TakeUntil, TakeWhile
 *    算术和集合操作 Average, Concat, Count, Max, Min, Reduce, Sum
 *    转换操作 To
 *    连接操作 Connect, Publish, RefCount, Replay
 *    反压操作，用于增加特殊的流程控制策略的操作符
 *
 * 这些操作符并不全都是ReactiveX的核心组成部分，有一些是语言特定的实现或可选的模块。
 *
 *
 */
public class OperatorsActivity extends AppCompatActivity {

    private String TAG = "OperatorsActivity";

    @Bind(R.id.ex_list)
    ExpandableListView exList;

    private Context mContext;

    private String[] groupList;
    private List<String[]> childLists;
    private int EXPAND_GROUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);
        ButterKnife.bind(this);

        mContext = this;
        groupList = mContext.getResources().getStringArray(R.array.operators_list);  //分组
        childLists = new ArrayList<>();
        childLists.add(mContext.getResources().getStringArray(R.array.operators_create));//创建操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_change));//变换操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_filter));//过滤操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_group));//组合操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_error));//错误处理
        childLists.add(mContext.getResources().getStringArray(R.array.operators_assist));//辅助操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_boolean));//条件和布尔操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_math));//算术和集合操作
        childLists.add(mContext.getResources().getStringArray(R.array.operators_connect));//连接操作

        exList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        for (int i = 0, count = exList
                                .getExpandableListAdapter().getGroupCount(); i < count; i++) {
                            if (groupPosition != i) {// 关闭其他分组
                                exList.collapseGroup(i);
                            }
                        }
                    }
                });
        exList.setAdapter(new MyAdapter(mContext));
//        exList.expandGroup(EXPAND_GROUP);
    }

    private class MyAdapter extends BaseExpandableListAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context mContext) {
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getGroupCount() {
            return groupList.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return childLists.get(i).length;
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.operators_group, null);
                holder = new ViewHolder();
                holder.tv_text = (TextView) view.findViewById(R.id.tv_group);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.tv_text.setText(groupList[i]);
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.operators_child, null);
                holder = new ViewHolder();
                holder.tv_text = (TextView) view.findViewById(R.id.tv_child);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.tv_text.setText(childLists.get(i)[i1]);
            holder.tv_text.setOnClickListener(v -> {
                String methodStr = "op_"+holder.tv_text.getText().toString().trim();
                methodStr = methodStr.replaceAll("/", "_");
                try {
                    OperatorsBase operators = OperatorsBase.getOperators(i);
                    Method method = operators.getClass().getDeclaredMethod(methodStr, TextView.class);
                    method.setAccessible(true);// 调用private方法的关键一句话
                    method.invoke(operators, holder.tv_text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    static class ViewHolder {
        TextView tv_text;
    }


















}
