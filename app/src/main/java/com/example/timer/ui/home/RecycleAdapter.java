package com.example.timer.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timer.LockActivity;
import com.example.timer.MainActivity;
import com.example.timer.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Task> tasks;
    private OnMyItemClickListener listener;
    private HomeViewModel viewModel;

    public RecycleAdapter(Context context, HomeViewModel viewModel) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        tasks = new ArrayList<>();
        this.viewModel = viewModel;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.listener = listener;

    }


    public interface OnMyItemClickListener {
        void myClick(View v, int pos);

        void mLongClick(View v, int pos);
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_recyclerview, parent, false);
        //将view传递给我们自定义的ViewHolder
        MyHolder holder = new MyHolder(view);
        //返回这个MyHolder实体
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder,  int position) {
        holder.taskName.setText(tasks.get(position).getTarget());
        holder.taskDuration.setText(tasks.get(position).getDuration() + " 分钟");
        holder.checkBox.setChecked(false);
        holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.startTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"开始任务", Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(mContext, LockActivity.class);
                intent.putExtra("minute", tasks.get(position).getDuration());
                intent.putExtra("taskTarget", tasks.get(position).getTarget());
                intent.putExtra("task", position);
                mContext.startActivity(intent);
                ((AppCompatActivity)mContext).getViewModelStore().clear();
                ((AppCompatActivity)mContext).finish();;
            }
        });
        if (listener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.myClick(v, position);
                }
            });


            // set LongClick
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.mLongClick(v, position);
                    return true;
                }
            });
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认：");
                builder.setMessage("确认已完成"+tasks.get(position).getTarget()+"?");

                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        Task tempTask = tasks.get(position);
                        viewModel.insertToDone(tempTask);
                        removeItem(position);
                        Toast.makeText(mContext,"已完成第"+position +"项任务", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.checkBox.setChecked(false);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

    }

    public void removeItem(int pos) {
        tasks.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, tasks.size());
        viewModel.saveToLocal();
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView taskName;
        public TextView taskDuration;
        public CheckBox checkBox;
        public ImageButton startTask;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_one);
            taskName = itemView.findViewById(R.id.test_text);
            checkBox = itemView.findViewById(R.id.check_finish);
            taskDuration = itemView.findViewById(R.id.card_task_duration);
            startTask = itemView.findViewById(R.id.start_task_button);
        }
    }
}

