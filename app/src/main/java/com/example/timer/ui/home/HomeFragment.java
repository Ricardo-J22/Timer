package com.example.timer.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timer.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ArrayList<Task> todoTasks;
    private ArrayList<Task> doneTasks;
    private RecycleAdapter recycleAdapter;
    private ListViewAdapter listViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initData();
        addListViewAdapter();
        addRecycleAdapter();
        homeViewModel.getLiveTodoTasks().observe(getActivity() , new Observer<ArrayList<Task>>() {
            @Override
            public void onChanged(ArrayList<Task> tasks) {
                recycleAdapter.notifyItemInserted(tasks.size() - 1);
                recycleAdapter.notifyItemRangeChanged(1, tasks.size() + 1);
            }
        });
        homeViewModel.getLiveDoneTasks().observe(getActivity(), new Observer<ArrayList<Task>>() {
            @Override
            public void onChanged(ArrayList<Task> tasks) {
                listViewAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    private void initData() {
        homeViewModel =
            new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        todoTasks = homeViewModel.getLiveTodoTasks().getValue();
        doneTasks = homeViewModel.getLiveDoneTasks().getValue();
    }

    private void addListViewAdapter() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("已完成");
        ArrayList<ArrayList<Task>> taskFinish = new ArrayList<ArrayList<Task>>();
        taskFinish.add(doneTasks);
        ExpandableListView listView = binding.expandListview;
        listViewAdapter = new ListViewAdapter(strings,taskFinish, getContext(), homeViewModel);
        listView.setAdapter(listViewAdapter);
    }

    private void addRecycleAdapter() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView = binding.recycler;
        recyclerView.setLayoutManager(manager);
        this.recycleAdapter = new RecycleAdapter(getContext(), homeViewModel);
        this.recycleAdapter.setTasks(todoTasks);
        recyclerView.setAdapter(this.recycleAdapter);
        recycleAdapter.setOnMyItemClickListener(new RecycleAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, int pos) {
            }

            @Override
            public void mLongClick(View v, int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("确认删除待办项吗");
                builder.setMessage("删除第" + (pos+1) +"项待办事项");
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recycleAdapter.removeItem(pos);
                        dialog.dismiss();
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}