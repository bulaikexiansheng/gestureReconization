package com.demo.mymusic;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mymusic.SongSheetActivity.MyHandler;
import com.fruitbasket.audioplatform.MyApp;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.util.Random;

import static com.demo.mymusic.Constents.cut_song_name;
import static com.demo.mymusic.Constents.jump_index;
import static com.demo.mymusic.Constents.list;
import static com.demo.mymusic.Constents.listsize;
import static com.demo.mymusic.Constents.playmode;
import static com.demo.mymusic.Constents.song_index;
import static com.fruitbasket.audioplatform.Constents.dataReady;
import static com.fruitbasket.audioplatform.Constents.predicting;

public class BroadcastActivity extends Activity {

    public ImageView music_symbol,next_song,pre_song,stopmusic,style,share,back,more;
    public TextView songname,songsinger,total_time,current_time;

    public MediaPlayer mplayer;
    public boolean ischanging = false;
    public SeekBar BroadcastseekBar;
    Thread broadcast_thread;

    private MyApp handlerAPP = null;

    private MyHandler mHandler = null;

    public  FreshHandler freshHandler=new FreshHandler();

    String cur_totaltime,cur_time;
    //11.22
    public static String TAG = "SongSheetActivity";
    private MyThread AirThread;
    private AirHandler handler = new AirHandler();
    //11.22
//edit 11.26
    Activity thisActivty;
    public boolean threadstop=false;
    //edit 11.26
    public void	onCreate(Bundle savedInstanceState)	{
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        int res=intent.getIntExtra("jump",-1);
        //edit 11.26
        thisActivty=this;
        MyApp.getInstance().addActivity(this);
        //edit 11.26
        if(res!=-1)
        {
            System.out.println("okk");
            jump_index=res;
        }
        setContentView(R.layout.broadcast);
        initview();
        setMediaPlayerListener();
    }

    public void initview()
    {
        music_symbol =  this.findViewById(R.id.imageview_face);
        next_song =  this.findViewById(R.id.imageview_next);
        pre_song =  this.findViewById(R.id.imageview_front);
        stopmusic =  this.findViewById(R.id.imageview_play);
        songname =  this.findViewById(R.id.song_name);
        songsinger =  this.findViewById(R.id.singer);
        style = this.findViewById(R.id.style);
        share = this.findViewById(R.id.share);
        total_time =  this.findViewById(R.id.total_time);
        current_time =  this.findViewById(R.id.current_time);
        back=this.findViewById(R.id.back);
//        more=this.findViewById(R.id.more);



        mplayer=Constents.mplayer;

        BroadcastseekBar=this.findViewById(R.id.broadcast_seekbar);
        if(song_index!=jump_index)
        {
            song_index=jump_index;
            musicplay(song_index);
        }
        songname.setText(cut_song_name(list.get(song_index).getSong()).trim());
        songsinger.setText(list.get(song_index).getSinger().trim());
//        text_main.setText(cut_song_name(list.get(song_index).getSong()));
        BroadcastseekBar.setMax(list.get(song_index).getDuration());
        broadcast_thread = new Thread(new BroadcastSeekBarThread());
        broadcast_thread.start();
        setclick();
        if(mplayer.isPlaying())
        {
            stopmusic.setImageResource(R.mipmap.broadcast);
        }
        else
        {
            stopmusic.setImageResource(R.mipmap.stop);
        }
        if(playmode==1)
        {
            style.setImageResource(R.mipmap.list_order3);
        }
        else
        {
            style.setImageResource(R.mipmap.random3);
        }

        refreshtime();

    }

    //11.22
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart()");
        startHandControl();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop()");

        if(predicting)
            AirThread.interrupt();
    }

    void startHandControl(){
        dataReady = false;
        if(predicting){
            AirThread = new MyThread();
            AirThread.start();
        }

    }
    private class MyThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    if (this.interrupted()){
                        throw new InterruptedException();
                    }
                    if(com.fruitbasket.audioplatform.Constents.dataReady == true){
                        // switchHandControl();
                        Message msg = new Message();
                        msg.what = com.fruitbasket.audioplatform.Constents.currentNum;
                        handler.sendMessage(msg);
                        com.fruitbasket.audioplatform.Constents.dataReady = false;
                    }
                }catch (InterruptedException e){
                    Log.d(TAG, "???????????? ");
                    return;
                }
            }
        }
    }
    //edit 11.26
    //TODO?????????????????????
    private class AirHandler extends Handler{
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0://?????????
                    frontMusic();
                    break;
                case 9://?????????
                    nextMusic();
                    break;
                case 1://??????????????????
                    change_style_image(R.mipmap.list_order3, R.mipmap.random3);
                    playmode=playmode*-1;
                    break;
                case 10:    //?????? //???????????????
                    change_play_image(R.mipmap.broadcast, R.mipmap.stop);
//

                    if (mplayer.isPlaying()) {
                        mplayer.pause();
//
                    } else {
                        mplayer.start();
                        broadcast_thread = new Thread(new BroadcastSeekBarThread());
                        broadcast_thread.start();
//
                    }
                    handlerAPP = (MyApp) getApplication();
                    // ???????????????????????????
                    mHandler = handlerAPP.getHandler();
                    mHandler.sendEmptyMessage(1);
                    break;
                case 12:    //?????? //????????????????????????
                    threadstop=true;
                    MyApp.getInstance().remove(thisActivty);
                    finish();
                    break;
                default:
                    break;
            }
        };
    };
    //11.22
//edit 11.26
    public class FreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                refreshtime();
            }
        }
    }

    public void refreshtime()
    {
        int cur_duration = list.get(song_index).getDuration();
        cur_totaltime = MusicUtils.formatTime(cur_duration);
        total_time.setText(cur_totaltime);
        String cur_curtime=MusicUtils.formatTime(mplayer.getCurrentPosition());
        current_time.setText(cur_curtime);
    }


    public void setclick()
    {
        stopmusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//
                change_play_image(R.mipmap.broadcast, R.mipmap.stop);
//

                if (mplayer.isPlaying()) {
                    mplayer.pause();
//
                } else {
                    mplayer.start();
                    broadcast_thread = new Thread(new BroadcastSeekBarThread());
                    broadcast_thread.start();
//
                }
                handlerAPP = (MyApp) getApplication();
                // ???????????????????????????
                mHandler = handlerAPP.getHandler();
                mHandler.sendEmptyMessage(1);
            }
        });

        style.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//
                change_style_image(R.mipmap.list_order3, R.mipmap.random3);
                playmode=playmode*-1;
             //   Toast.makeText(getApplicationContext(), "????????????9??????????????????", Toast.LENGTH_SHORT).show();

            }
        });

        next_song.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                nextMusic();
          //      Toast.makeText(getApplicationContext(), "????????????1???????????????", Toast.LENGTH_SHORT).show();
//                auto_change_listview();
//                }
            }
        });

        pre_song.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Toast.makeText(getApplicationContext(), "????????????0???????????????", Toast.LENGTH_SHORT).show();
                frontMusic();
//                auto_change_listview();
//                }
            }
        });

        BroadcastseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                ischanging = false;
                mplayer.seekTo(seekBar.getProgress());
                broadcast_thread = new Thread(new BroadcastSeekBarThread());
                broadcast_thread.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                ischanging = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                // ????????????????????????????????????????????????
            }


        });

        //edit 11.26
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goback();

                threadstop=true;
                MyApp.getInstance().remove(thisActivty);
                finish();
            }
        });
        //edit 11.26
    }



    private void change_play_image(int resID_play, int resID_pause) {
        if (stopmusic
                .getDrawable()
                .getCurrent()
                .getConstantState()
                .equals(getResources().getDrawable(resID_play)
                        .getConstantState())) {
            stopmusic.setImageResource(resID_pause);
        } else {
            stopmusic.setImageResource(resID_play);
        }
    }

    private void change_style_image(int resID_ordered, int resID_unordered) {
        if (style
                .getDrawable()
                .getCurrent()
                .getConstantState()
                .equals(getResources().getDrawable(resID_ordered)
                        .getConstantState())) {
            style.setImageResource(resID_unordered);
            //edit 11.26
            Toast.makeText(getApplicationContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
            //edit 11.26
        } else {
            style.setImageResource(resID_ordered);
            //edit 11.26
            Toast.makeText(getApplicationContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
            //edit 11.26
        }
    }

    class BroadcastSeekBarThread implements Runnable {

        @Override
        public void run() {
            while (!threadstop&&!ischanging && mplayer.isPlaying()) {
                Log.d(TAG, "keyrun1");
                // ???SeekBar?????????????????????????????????
                Message message1=new Message();
                message1.what=100;
                freshHandler.sendMessage(message1);
                BroadcastseekBar.setProgress(mplayer.getCurrentPosition());

                try {
                    // ???500????????????????????????
                    Thread.sleep(500);
                    // ????????????

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "keyrun2");
            }
        }
    }

    public void setMediaPlayerListener() {
        // ??????mediaplayer?????????????????????
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
//
                // ????????????????????????????????????????????????????????????????????????????????????????????????
                nextMusic();
//                auto_change_listview();
//
//
            }
        });

    }

    private void musicplay(int position) {
        songname.setText(cut_song_name(list.get(position).getSong()).trim());
        songsinger.setText(list.get(position).getSinger().trim());
//        text_main.setText(cut_song_name(list.get(song_index).getSong()));
        BroadcastseekBar.setMax(list.get(position).getDuration());
        stopmusic.setImageResource(R.mipmap.broadcast);
        handlerAPP = (MyApp) getApplication();
        // ???????????????????????????
        mHandler = handlerAPP.getHandler();
        mHandler.sendEmptyMessage(1);
        try {
            mplayer.reset();
            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        broadcast_thread = new Thread(new BroadcastSeekBarThread());
        broadcast_thread.start();


        total_time.setText(cur_totaltime);
    }

    private void frontMusic() {
        if(playmode==1)
        {
            song_index--;
            if (song_index < 0) {
                song_index = list.size() - 1;
            }
            System.out.println("songindex:"+song_index);
        }
        else {
            Random random=new Random();
            int r=song_index;
            while (r==song_index)
            {
                r=random.nextInt(listsize);
            }
            System.out.println("rrrr:"+r);
            song_index=r;
        }
        musicplay(song_index);
//
    }

    private void nextMusic() {
        if(playmode==1)
        {
            song_index++;
            if (song_index > list.size() - 1) {
                song_index = 0;
            }
            System.out.println("songindex:"+song_index);
        }
        else
        {
            Random random=new Random();
            int r=song_index;
            while (r==song_index)
            {
                r=random.nextInt(listsize);
            }
            System.out.println("rrrr:"+r);
            song_index=r;
        }
        musicplay(song_index);
    }
    //edit 11.26
    @Override
    public void onBackPressed() {
        threadstop=true;
        MyApp.getInstance().remove(this);
        finish();
    }
    //edit 11.26




}
