package com.test.battlegrid;



import android.app.Dialog;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.net.UnknownHostException;


public class Main extends AppCompatActivity {

    private ImageView[] targets = new ImageView[45];



    public boolean is_server = false;
    public boolean is_client = false;

    private ServerSocket serverSocket;

    private Socket clientSocket;

    Handler updateConversationHandler;

    Thread serverThread = null;

    Thread clientThread = null;

    Dialog dialog;

    public static final int SERVER_PORT = 6000;

    public String server_ip = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateConversationHandler = new Handler();

        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid1);

        gridLayout.removeAllViews();

        int total = 45;
        int column = 9;
        int row = total / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row + 1);

        for(int i =0, c = 0, r = 0; i < total; i++, c++)
        {
            if(c == column)
            {
                c = 0;
                r++;
            }
            ImageView oImageView = new ImageView(this);
            if(i==1) {
                oImageView.setImageResource(R.drawable.circle);
            } else if(i== 15) {
                oImageView.setImageResource(R.drawable.blue_circle);
            } else if(i== 27) {
                oImageView.setImageResource(R.drawable.yellow_circle);
            } else if(i== 43) {
                oImageView.setImageResource(R.drawable.green_circle);
            } else {
                oImageView.setImageResource(R.drawable.target);
            }

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();

            param.rightMargin = 18;
            param.leftMargin = 15;
            param.topMargin = 5;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            oImageView.setLayoutParams(param);
            oImageView.setId(i);

            oImageView.setOnLongClickListener(listenClick);
            oImageView.setOnDragListener(listenDrag);

            targets[i] = oImageView;

            gridLayout.addView(oImageView);
        }

    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(server_ip);

                clientSocket = new Socket(serverAddr, SERVER_PORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    public void msgSend(String string) {

        if (is_client) {

            try {
                String str = string;
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream())),
                        true);
                out.println(str);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket cSocket;

        private BufferedReader input;
        private BufferedWriter output;

        public CommunicationThread(Socket cSocket) {

            this.cSocket = cSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.cSocket.getInputStream()));
                this.output = new BufferedWriter(new OutputStreamWriter(this.cSocket.getOutputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        // only try to close serverSocket if it is open, else crash
        if (is_server) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    View.OnClickListener connector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            server_ip = ((EditText) dialog.findViewById(R.id.tv)).getText().toString();
            Log.i("TEST", "Connect button clicked with value " + server_ip);

            Main.this.clientThread = new Thread(new ClientThread());
            Main.this.clientThread.start();
            if (clientThread != null) {
                is_client = true;
            }
            dialog.dismiss();
        }
    };



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
            return true;
        }
        if (id == R.id.checkBox) {

            if (!is_server) {
                is_server = true;

                // interrupt clientThread and close clientSocket
                if (is_client) {
                    clientThread.interrupt();
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    is_client = false;
                }


                // Start server
                Log.i("TEST", "Set to server mode: true");
                Main.this.serverThread = new Thread(new ServerThread());
                Main.this.serverThread.start();
                Toast.makeText(getBaseContext(), "Server Mode: On", Toast.LENGTH_SHORT).show();

            } else {
                is_server = false;
                Log.i("TEST", "Set to server mode: false");

                serverThread.interrupt();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getBaseContext(), "Server Mode: Off", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        if (id == R.id.button) {
            Log.i("BATGRID", "button pressed");

            if (is_server) {
                Toast.makeText(getBaseContext(), "Error: can't connect to servers while in Server Mode", Toast.LENGTH_SHORT).show();
                return true;
            }
            dialog = new Dialog(Main.this);
            dialog.setContentView(R.layout.dialog);


            dialog.setTitle("Connect to a Battle Grid Server:");
            dialog.show();

            Button b = (Button) dialog.findViewById(R.id.server_connect);
            b.setOnClickListener(connector);

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

                    int id1 = target.getId();
                    int id2 = dragged.getId();
                    String ids = "From "+ id2 + " to " + id1;

                    Toast.makeText(getBaseContext(), ids, Toast.LENGTH_SHORT).show();

                    dragged.setImageDrawable(target_draw);
                    target.setImageDrawable(dragged_draw);

                    String msg = "MOVE " + id2 + " " + id1;
                    msgSend(msg);

                    break;

            }

            return true;
        }
    };

    class updateUIThread implements Runnable {

        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            Log.i("updateUIThread", msg);

            StringTokenizer tokens = new StringTokenizer(msg, " ");
            String cmd = tokens.nextToken();

            switch (cmd){

                // Moves tokens around when server receives a client message
                case "MOVE":

                    int arg1 = Integer.parseInt(tokens.nextToken());
                    int arg2 = Integer.parseInt(tokens.nextToken());
                    String ids = "From "+ arg2 + " to " + arg1;

                    ImageView target = targets[arg2];
                    ImageView dragged = targets[arg1];

                    Drawable target_draw = target.getDrawable();
                    Drawable dragged_draw = dragged.getDrawable();

                    Toast.makeText(getBaseContext(), ids, Toast.LENGTH_SHORT).show();

                    dragged.setImageDrawable(target_draw);
                    target.setImageDrawable(dragged_draw);

                    //TODO - Broadcast move to all clients (except original message sender) (OPTIONAL)

                    break;

                // Didn't recognize command
                default:
                    Log.e("BATTLEGRID", "Malformed command: " + cmd);
            }

        }
    }
}

