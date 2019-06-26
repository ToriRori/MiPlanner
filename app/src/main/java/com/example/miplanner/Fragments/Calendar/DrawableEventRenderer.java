package com.example.miplanner.Fragments.Calendar;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miplanner.R;
import com.github.tibolte.agendacalendarview.render.EventRenderer;

import java.text.SimpleDateFormat;

public class DrawableEventRenderer extends EventRenderer<DrawableCalendarEvent> {

    // region Class - EventRenderer

    @Override
    public void render(View view, DrawableCalendarEvent event) {
        TextView txtTitle = view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_title);
        TextView txtLocation = view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location);
        TextView txtTime = view.findViewById(R.id.view_agenda_event_time);
        LinearLayout descriptionContainer = view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location_container);
        //ImageView locationImage = (ImageView) view.findViewById(R.id.image_location);
        descriptionContainer.setVisibility(View.VISIBLE);


        txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));
        txtTitle.setText(event.getTitle());

        if (event.isAllDay()) {
            txtTime.setText("весь день");
        } else {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            txtTime.setText(format.format(event.getStartTime().getTime())+" - "+format.format(event.getEndTime().getTime()));
        }


        if (event.getLocation() != null) {
            txtLocation.setText(event.getLocation());
            if (event.getLocation().length() > 0) {
                locationContainer.setVisibility(View.VISIBLE);
                txtLocation.setText(event.getLocation());
            } else {
                locationContainer.setVisibility(View.GONE);
            }
        }

        if (event.getTitle().equals(view.getResources().getString(com.github.tibolte.agendacalendarview.R.string.agenda_event_no_events))) {
            txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));
        } else {
            txtTitle.setTextColor(view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
        }
        descriptionContainer.setBackgroundColor(event.getColor());
        txtLocation.setTextColor(view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons));
    }

    @Override
    public int getEventLayout() {
        return R.layout.view_agenda_drawable_event;
    }

    @Override
    public Class<DrawableCalendarEvent> getRenderType() {
        return DrawableCalendarEvent.class;
    }

    // endregion
}
