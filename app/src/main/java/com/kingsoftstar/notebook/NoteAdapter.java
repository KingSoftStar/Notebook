package com.kingsoftstar.notebook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by KingSoftStar on 2016/12/1.
 */

class NoteAdapter extends ArrayAdapter<Note> {
    private int resourceId;

    public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.listview_cell_note_title);
            viewHolder.time = (TextView) view.findViewById(R.id.listview_cell_note_edit_time);
            viewHolder.content = (TextView) view.findViewById(R.id.listview_cell_note_simple_content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(note.getTitle());
        viewHolder.time.setText(note.getEditTime());
        viewHolder.content.setText(note.getSimpleContent());
        return view;
    }

    class ViewHolder {
        TextView title, time, content;
    }

}
