package com.fruitbasket.audioplatform.play2;

import android.app.Application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by Administrator on 2017/7/5.
 */



public class GlobalConfig extends Application {

       private static GlobalConfig stGlobalConfig ;
      //Record sample rate
       public static int AUDIO_SAMPLE_RATE = 48000;//44100;//16000;//48000
       public static boolean bByte = true;
       public static int  RECORD_FRAME_SIZE    = 512*2; //byte
       public static int  MAX_RECORD_CACHE_SIZE    = 4096;
       ////////////////switch///////////////////////////////////////////
        public static boolean bSupportLLAP = true; //是否支持相位
        public static boolean bRecord = true;  //是否支持录制
        public static boolean isRecording=false;  //是否正在录制
        public static boolean bPlayDataReady = false; //bPlayData是否READY
        public static boolean bSaveWavFile = true;//是否保存wav文件
        public static boolean bPlayThreadFlag = true;//播放线程false
        public static boolean bRecordThreadFlag =  true;//播放线程
        public static boolean bDataProcessThreadFlag = true;//处理线程true
        ///////////////////////////////////////////
        public SynchLinkedList<byte[]> byteSynchLinkedList = new SynchLinkedList<byte[]>();

        /////////////////file play//////////////////////////////////////
        public static String  sPlayPcmFileName = WaveFileUtil.sAbsolutePath + "qilixiangmono3.wav";//;//"btts.pcm";//auda20k40s.wav";//"auda20k40s.wav";//"auda18k40s.wav";//"btts.pcm";//;//"AUDA18SIN.wav";//"btts.pcm";//"sin20k40s.pcm";//"sin18k40s.pcm";//"AutoRecording-823432235.pcm";                 //播放的pcm文件路径
        public static File playPcmFile = null;
        public static DataInputStream playPcmDis = null;
        public static byte[] playData;
        public static DataOutputStream recDos = null;
        public static File fPcmRecordFile;
        public static String  sRecordPcmFileName = "AutoRecording";
        public static DataOutputStream recDos2 = null;
        public static DataOutputStream recTxtDos = null;
        public static File fPcmRecordFile2;
        public static String  sRecordPcmFileName2 = "ProxyRecording";
    ///////////////////////IOS==>ANDR///////////////////////////
    public static short[] vIosData = new short[512];
    public static int FRAME_NUM =17;
    public static short[][] vvIosData = new short[FRAME_NUM][512];
    ///////////////////////////////////////////////////////////////////////////////////
        public static PhaseAudioRecord stPhaseAudioRecord = new PhaseAudioRecord(); //
        public static PhaseProxy stPhaseProxy = new PhaseProxy();
        public static WaveFileUtil stWaveFileUtil = new WaveFileUtil();
        public static File fAbsolutepath = new File(WaveFileUtil.sAbsolutePath);

        //Start audio frequency
        public static float  START_FREQ      =  (float)17500.0;
        //Frequency interval
        public static float FREQ_INTERVAL    =  (float)350.0;
        //Number of frequency
        public static int   NUM_FREQ         =   1;
        //Number of frame size ，简洁控制了播放声音的单位长度
        public static int  MAX_FRAME_SIZE    = 48000;


        private GlobalConfig (){}
        public static GlobalConfig getInstance() {
            if (stGlobalConfig == null) {
                stGlobalConfig = new GlobalConfig();
            }
            return stGlobalConfig;//内嵌自身
        }

        public void  pushRecData(byte[] recData) throws InterruptedException {
            //Log.i("queue","record|rec[0]:"+recData[0] + "|rec1:"+recData[1] + "|rec[2]:"+recData[2] + "|rec[3]:"+recData[3] + "|rec[310]:" + recData[310] + "|rec[311]:" + recData[311]+ "|rec[312]:" + recData[312]+ "|rec[313]:" + recData[313]);
            byteSynchLinkedList.push(recData);//线程安全的 LinkedList
        }

        public byte[]  popByteRecData( ) throws InterruptedException {
            byte[] recData =byteSynchLinkedList.pop(true);
            //Log.i("queue", "queue|rec[0]:" + recData[0] + "|rec1:" + recData[1] + "|rec[2]:" + recData[2] + "|rec[3]:" + recData[3] + "|rec[310]:" + recData[310] + "|rec[311]:" + recData[311]+ "|rec[312]:" + recData[312]+ "|rec[313]:" + recData[313]);
            return recData;
       }

    }

