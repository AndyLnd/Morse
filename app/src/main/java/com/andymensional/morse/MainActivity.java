package com.andymensional.morse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


public class MainActivity extends Activity {
    static public TextView chatConsole;
    static Vibrator vibrator;
    static Socket socket;
    static ViewAnimator viewAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnConnect = (Button) findViewById(R.id.btnConnect);
        final Button btnSend = (Button) findViewById(R.id.btnSend);
        chatConsole = (TextView) findViewById(R.id.chatConsole);
        viewAnimator =(ViewAnimator) findViewById(R.id.viewAnimator);
        final Animation inAnim = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        final Animation outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        viewAnimator.setInAnimation(inAnim);
        viewAnimator.setOutAnimation(outAnim);

        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        try {
            socket = IO.socket("http://andylind.lepus.uberspace.de:64021/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatConsole.append("\nconnected");
                    }
                });

            }
        });
        socket.on(Socket.EVENT_DISCONNECT,new Emitter.Listener(){

            @Override
            public void call(Object... args) {

            }
        });
        socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String message = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatConsole.append("\n" + message);
                    }
                });
                Log.d("message", "message received: " + message);
            }
        });


        socket.connect();


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtChatId = (EditText) findViewById(R.id.txtChatId);
                connectId(txtChatId.getText().toString());
                viewAnimator.setDisplayedChild(1);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText chatInput = (EditText) findViewById(R.id.chatInput);
                sendMessage(chatInput.getText().toString());
                chatInput.setText("");
            }
        });
    }

    static void showMorse(long[] morsePattern) {
        vibrator.vibrate(morsePattern, -1);
    }

    static void connectId(String id) {
        socket.emit("join room", id);
        chatConsole.append("\njoining room " + id);
        //  showMorse(new long[]{0L, 10L, 50L, 10L, 50L, 10L, 50L, 10L});
    }

    static void sendMessage(String text) {
        socket.emit("message", text);
        Log.d("send", text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_connect:
                viewAnimator.setDisplayedChild(0);
                break;
            case R.id.action_chat:
                viewAnimator.setDisplayedChild(1);
                break;
            case R.id.action_disconnect:
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    private void changeView(){


    }
}
