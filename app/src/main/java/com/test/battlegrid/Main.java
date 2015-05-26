package com.test.battlegrid;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.net.URI;


public class Main extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ball1).setOnLongClickListener(listenClick);
        findViewById(R.id.ball1).setOnDragListener(listenDrag);
        findViewById(R.id.target1).setOnLongClickListener(listenClick);
        findViewById(R.id.target1).setOnDragListener(listenDrag);
        findViewById(R.id.target2).setOnDragListener(listenDrag);
        findViewById(R.id.target2).setOnLongClickListener(listenClick);
        findViewById(R.id.target3).setOnDragListener(listenDrag);
        findViewById(R.id.target3).setOnLongClickListener(listenClick);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnLongClickListener listenClick = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {

            ClipData data = ClipData.newPlainText("","");
            DragShadow dragShadow = new DragShadow(v);

            v.startDrag(data, dragShadow, v, 0);

            return false;
        }
    };

    private class DragShadow extends View.DragShadowBuilder
    {
        ColorDrawable greyBox;

        public DragShadow(View view)
        {
            super(view);
            greyBox = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onDrawShadow(Canvas canvas)
        {
            greyBox.draw(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize,
                                           Point shadowTouchPoint)
        {
            View v = getView();

            int height = (int) v.getHeight();
            int width = (int) v.getWidth();

            greyBox.setBounds(0, 0, width, height);

            shadowSize.set(width, height);

            shadowTouchPoint.set((int)width/2, (int)height/2);
        }
    }

    View.OnDragListener listenDrag = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event)
        {
            int dragEvent = event.getAction();

            switch (dragEvent)
            {
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i("Drag Event", "Entered");
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i("Drag Event", "Exited");
                    break;

                case DragEvent.ACTION_DROP:
                    ImageView target = (ImageView) v;

                    ImageView dragged = (ImageView) event.getLocalState();

                    Drawable target_draw = target.getDrawable();
                    Drawable dragged_draw = dragged.getDrawable();

                    dragged.setImageDrawable(target_draw);

                    //target.setImageURI((Uri) event.getLocalState());
                    target.setImageDrawable(dragged_draw);



                    break;

            }

            return true;
        }
    };
}

