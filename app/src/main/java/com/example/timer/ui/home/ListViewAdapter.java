package com.example.timer.ui.home;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.timer.R;

import java.util.ArrayList;

import kotlin.random.FallbackThreadLocalRandom;
import tools.SharedPreferencesUtils;

public class ListViewAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> groupList;
    private ArrayList<ArrayList<Task>> childList;
    private Context context;
    private HomeViewModel viewModel;

    public ListViewAdapter(ArrayList<String> groupList, ArrayList<ArrayList<Task>> childList, Context context, HomeViewModel viewModel) {
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder groupHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_group_list, parent, false);
            groupHolder = new ViewGroupHolder();
            groupHolder.groupText = (TextView) convertView.findViewById(R.id.group_id);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewGroupHolder) convertView.getTag();
        }
        groupHolder.groupText.setText(groupList.get(groupPosition) + " (" + childList.get(0).size() + ")");
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewItemHolder itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_recyclerview, parent, false);
            itemHolder = new ViewItemHolder();
            itemHolder.taskName = convertView.findViewById(R.id.test_text);
            itemHolder.taskDuration =  convertView.findViewById(R.id.card_task_duration);
            itemHolder.checkBox = convertView.findViewById(R.id.check_finish);
            itemHolder.imageButton = convertView.findViewById(R.id.start_task_button);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewItemHolder) convertView.getTag();
        }
//        itemHolder.img_icon.setImageResource(iData.get(groupPosition).get(childPosition).getiId());
        itemHolder.imageButton.setVisibility(View.INVISIBLE);
        itemHolder.taskName.setText(childList.get(groupPosition).get(childPosition).getTarget());
        itemHolder.taskName.setText(childList.get(groupPosition).get(childPosition).getTarget());
        itemHolder.checkBox.setChecked(true);
        itemHolder.taskName.setPaintFlags(itemHolder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        itemHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    itemHolder.taskName.setPaintFlags(itemHolder.taskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    Task temp = childList.get(groupPosition).get(childPosition);
                    viewModel.insertToUndo(temp);
                    childList.get(groupPosition).remove(childPosition);
                    notifyDataSetChanged();
                    viewModel.saveToLocal();
                }
            }
        });
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class ViewGroupHolder{
        private TextView groupText;
    }

    private static class ViewItemHolder{
        private TextView taskName;
        private TextView taskDuration;
        private CheckBox checkBox;
        private ImageButton imageButton;
    }
}
