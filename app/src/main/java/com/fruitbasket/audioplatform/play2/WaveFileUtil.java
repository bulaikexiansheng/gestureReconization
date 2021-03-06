package com.fruitbasket.audioplatform.play2;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/5/4.
 */

public class WaveFileUtil {
    //录音输出文件
    private final static String Record_RAW_AUDIO_FILENAME = "RecordRawAudio";
    private final static String Record_WAV_AUDIO_FILENAME = "RecordWavAudio";
    private final static String PLAY_RAW_AUDIO_FILENAME = "PlayRawAudio";
    private final static String PLAY_WAV_AUDIO_FILENAME = "PlayWavAudio";
    private final static String RAW_FILEFORMAT = ".pcm";
    private final static String WAV_FILEFORMAT  = ".wav";
    private final static String TXT_FILEFORMAT  = ".txt";
    ///////
    //录制的AudioName裸音频数据文件 ，麦克风
    private String RecordRawAudioName = getFilePath(Record_RAW_AUDIO_FILENAME, RAW_FILEFORMAT);
    //录制的RecordWavAudioName可播放的音频文件
    private String RecordWavAudioName = getFilePath(Record_WAV_AUDIO_FILENAME, WAV_FILEFORMAT);

    //播放的PlayRawAudioName裸音频数据文件
    private String PlayRawAudioName = getFilePath(PLAY_RAW_AUDIO_FILENAME,RAW_FILEFORMAT);
    //播放的PlayWavAudioName可播放的音频文件
    private String PlayWavAudioName = getFilePath(PLAY_WAV_AUDIO_FILENAME, WAV_FILEFORMAT);
    private final static String  IosRecordFileName = "/storage/emulated/0/data/files/phase/rec_512_2.txt";
    private final static String  IosRecordFileNamePre = "/storage/emulated/0/data/files/phase/rec_512_";
    private final static String  AndroidPlayFileName = "/storage/emulated/0/data/files/phase/play.wav";
    private final static String  AndroidRecordFileName = "/storage/emulated/0/data/files/phase/record";
    private final static String  BaseBandRealFileName = "/storage/emulated/0/data/files/phase/real_";
    private final static String  BaseBandImageFileName = "/storage/emulated/0/data/files/phase/image_";
    private final static String  recordFileName = "/storage/emulated/0/data/files/phase/rec_";
    public  final static String  sAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/files/phase/";  //存放数据的绝对路径
    private final static String  BaseIQFileName = "/storage/emulated/0/data/files/phase/IQ_";
    private final static String  RecordTxtFileName = "/storage/emulated/0/data/files/phase/RecTxt_";
    public final static String  ReadRecordTxtFileName = "/storage/emulated/0/data/files/phase/ios_wav_dis_1511_wav.txt";
    private static String[] vFreqBaseIQFile;
    private static BufferedWriter[] vBufferedWriter;
    public static File[] vIQTxtFile ;
    public static DataOutputStream[] vIQDos;

    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //public final static int AUDIO_SAMPLE_RATE = 44100;  //44.1KHz,普遍使用的频率

    // 缓冲区字节大小
   // private int bufferSizeInBytes = 8192;
    public String getIosRecordFileNameByFrame(int iFrame){return IosRecordFileNamePre+iFrame+".txt";}
    public String getIosRecordFileName(){return IosRecordFileName;}
    public String getAndroidRecordFileName(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = AndroidRecordFileName+sDate+WAV_FILEFORMAT;
        return sFile;
    }

    public String getBaseBandRealFileName(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = BaseBandRealFileName+sDate+TXT_FILEFORMAT;
        return sFile;
    }

    public String getBaseBandImageFileName(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = BaseBandImageFileName+sDate+TXT_FILEFORMAT;
        return sFile;
    }

    public String getBaseBandIQFileName(int Freq){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = BaseIQFileName+"_"+Freq+"_"+sDate+TXT_FILEFORMAT;
        return sFile;
    }

    public String getRecordTxtFileName(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = RecordTxtFileName+ "_"+sDate+TXT_FILEFORMAT;
        return sFile;
    }
    public String getRecordFileName(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate    =    sDateFormat.format(new    java.util.Date());
        String sFile = recordFileName+sDate+TXT_FILEFORMAT;
        return sFile;
    }


    public String getAndroidPlayFileName(){return AndroidPlayFileName;}

    public String getRecordRawAudioName(){
        return RecordRawAudioName;
    }

    public String getRecordWavAudioName(){
        return RecordWavAudioName;
    }

    public String getPlayRawAudioName(){
        return PlayRawAudioName;
    }

    public String getPlayWavAudioName(){
        return PlayWavAudioName;
    }

    public FileOutputStream  createFileOutputStream(String sFileAbsolutePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(sFileAbsolutePath);//stWaveFileUtil.getRecordRawAudioName());
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fos;
    }

    public static void initIQFile()
    {
        vFreqBaseIQFile = new String[GlobalConfig.NUM_FREQ];
        //vBufferedWriter = new BufferedWriter[mNumFreqs];
        vIQTxtFile = new File[GlobalConfig.NUM_FREQ];
        vIQDos = new DataOutputStream[GlobalConfig.NUM_FREQ];

        for(int i=0; i<GlobalConfig.NUM_FREQ; i++)
        {
            vFreqBaseIQFile[i] = GlobalConfig.stWaveFileUtil.getBaseBandIQFileName(i);
            //vBufferedWriter[i] = GlobalConfig.stWaveFileUtil.getFileBufferedWriter(vFreqBaseIQFile[i]);
            vIQTxtFile[i] = GlobalConfig.stWaveFileUtil.createFile(vFreqBaseIQFile[i]);
            if (vIQTxtFile[i]  != null) {
                try {
                    vIQDos[i] = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(vIQTxtFile[i] )));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void  destroy() {
        for (int i = 0; i < GlobalConfig.NUM_FREQ; i++) {
            try {
                //vBufferedWriter[i].close();
                //vIQTxtFile[i].close();
                vIQDos[i].close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static File  createFile(String sFileName){
        File file = new File(sFileName);
        if (!file.exists() != false) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void writeToTxtFileFast(File fTxtFile, DataOutputStream stDos, byte[] recData){

            int iReadSize = recData.length;
            Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);
            if (iReadSize > 0 && recData != null) {

                if (stDos == null) {
                    Log.i("record", "resdos is null");
                    if (fTxtFile != null) {
                        try {
                            stDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fTxtFile)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //循环将buffer中的音频数据写入到OutputStream中
                if (stDos != null) {
                    for (int i = 0; i < iReadSize; i=i+2) {
                        try {
                            short iData = (short) ((recData[i] & 0xff) | (recData[i+ 1] & 0xff) << 8);
                            String sData = String.valueOf(iData);
                            stDos.writeBytes(sData);
                            stDos.writeByte('\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


    public void writeToTxtFileFast(File fTxtFile, DataOutputStream stDos, short[] recData){

        int iReadSize = recData.length;
        Log.i("WaveFileUtil ", "|before writetoFile iReadSize:" + iReadSize);
        if (iReadSize > 0 && recData != null) {

            if (stDos == null) {
                Log.i("record", "resdos is null");
                if (fTxtFile != null) {
                    try {
                        stDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fTxtFile)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            //循环将buffer中的音频数据写入到OutputStream中
            if (stDos != null) {
                for (int i = 0; i < iReadSize; i++) {
                    try {
                        String sData = String.valueOf(recData[i]);
                        stDos.writeBytes(sData);
                        stDos.writeByte('\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void writeToTxtFileFast(File fTxtFile, DataOutputStream stDos, String  sBuf){

        if (sBuf != null) {
            //循环将buffer中的音频数据写入到OutputStream中
            if (stDos != null) {
                    try {
                        stDos.writeBytes(sBuf);
                        stDos.writeByte('\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }
    }



    public void writetoFile(short[] sAudioData, int iSize, FileOutputStream fos) {

        Log.i("WaveFileUtil ","| writetoFile iReadSize:"+ iSize);
        byte[] bAudiodata = new byte[iSize*2];

        for(int i=0; i <iSize; i++){
            short iData = (sAudioData[i]);
            putShort(bAudiodata, iData, i*2);
           // Log.i("WaveFileUtil","| writetoFile sAudioData:"+ iData);
        }
        writeDateTOFile(bAudiodata, iSize*2, fos);
    }
    /**
     * 转换short为byte
     *
     * @param b
     * @param s
     *            需要转换的short
     * @param index
     */
    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }


    /**
     * 判断是否有外部存储设备sdcard
     * @return true | false
     */
    public static boolean isSdcardExit(){
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取文件路径
     * @return
     */
    public  static String getFilePath(String sFileName, String sFormat){
        String sFilePath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyyMMddhhmmss");
            String    sDate    =    sDateFormat.format(new    java.util.Date());
            sFilePath = fileBasePath+"/audio/"+sFileName+sDate+sFormat;
        }
        return sFilePath;
    }


    /**
     * 获取文件大小
     * @param path,文件的绝对路径
     * @return
     */
    public static long getFileSize(String path){
        File mFile = new File(path);
        if(!mFile.exists())
            return -1;
        return mFile.length();
    }



    //class AudioRecordThread implements Runnable {
    //    @Override
    //    public void run() {
    //        //writeDateTOFile();//往文件中写入裸数据
     //       copyWaveFile(AudioName, NewAudioName);//给裸数据加上头文件
      //  }
    //}

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    /*private void writeDateTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(AudioName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecord == true) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos!=null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if(fos != null)
                fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void writeDateTOFile(byte[] audiodata, int readsize, FileOutputStream fos) {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        //byte[] audiodata = new byte[bufferSizeInBytes];

        // while (isRecord == true) {
        //readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
        if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos!=null) {
            try {
                fos.write(audiodata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    // 这里得到可播放的音频文件
    public static void copyWaveFile(String inFilename, String outFilename, long longSampleRate, int  channels,int bufferSizeInBytes,short bitsPerSample ) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        //long longSampleRate = AUDIO_SAMPLE_RATE;
        //int channels = 2;
        long byteRate = 16 * longSampleRate * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate,bitsPerSample);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public static void copyTxtFile(String inFilename, String outFilename, int frameNum) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = 0;
        byte[] data = new byte[2];
        File file = null;
        BufferedWriter bw = null;
        file = new File(outFilename);
        if (!file.exists() != false) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            while (in.read(byte[] buffer, int byteOffset, int byteCount != -1) {

                try {
                    bw = new BufferedWriter(new FileWriter(file));

                    for(byte d : data){
                        System.out.println(String.valueOf(d));
                        bw.write(String.valueOf(d));//输出字符串
                        bw.newLine();//换行
                        //bw.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        bw.flush();
                        bw.close();
                        //bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    // 这里得到可播放的音频文件
    public static void saveDataToWav(short[] viData, String outFilename, long longSampleRate, int  channels,int bufferSizeInBytes,short bitsPerSample ) {
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = 0;
        //long longSampleRate = AUDIO_SAMPLE_RATE;
        //int channels = 2;
        long byteRate = 16 * longSampleRate * channels / 8;
        try {
            out = new FileOutputStream(outFilename);
            totalAudioLen = viData.length*Short.SIZE/8;
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate,bitsPerSample);

            for (short data: viData) {
                byte bData = (byte) (data & 0x00ff);
                out.write(bData);
                bData = (byte) ((data & 0xff00) >>> 8);
                out.write(bData);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 这里得到可播放的音频文件
    public static void saveDataToWav(byte[] viData, String outFilename, long longSampleRate, int  channels,int bufferSizeInBytes,short bitsPerSample ) {
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = 0;
        //long longSampleRate = AUDIO_SAMPLE_RATE;
        //int channels = 2;
        long byteRate = 16 * longSampleRate * channels / 8;
        try {
            out = new FileOutputStream(outFilename);
            totalAudioLen = viData.length;
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate,bitsPerSample);

            for (short data: viData) {
                out.write(data);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeTxtData(String sFileName, double[] baseWaveData) {
        try {
           /* PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName, true)));
            for(double d : baseWaveData)
            {
                out.println(String.valueOf(d));        //向t.txt写入一行
                out.flush();
            }
            out.close();
            */
            File file = null;
            BufferedWriter bw = null;
            file = new File(sFileName);
            if (!file.exists() != false) {
                try {
                    file.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bw = new BufferedWriter(new FileWriter(file));

                for(double d : baseWaveData){
                    System.out.println(String.valueOf(d));
                    bw.write(String.valueOf(d));//输出字符串
                    bw.newLine();//换行
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    bw.flush();
                    bw.close();
                    //bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public static void writeTxtData(String sFileName, byte[] baseWaveData) {
        try {
           /* PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName, true)));
            for(double d : baseWaveData)
            {
                out.println(String.valueOf(d));        //向t.txt写入一行
                out.flush();
            }
            out.close();
            */
       // }


    public static BufferedWriter getFileBufferedWriter(String sFileName) {
        File file = null;
        BufferedWriter bw = null;
        file = new File(sFileName);
        if (file.exists() == false) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bw = new BufferedWriter(new FileWriter(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bw;
    }

    public static void writeTxtData(BufferedWriter bw, String sData) {
        try {
            try {
                    bw.write(sData);//输出字符串
                    bw.newLine();//换行

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void readTxtDataToShort(String sFileName) {
        try {
            File filename = new File(sFileName);
            FileInputStream in = new FileInputStream(sFileName);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            long totalAudioLen = in.getChannel().size();
            short[] dData = new short[(int)totalAudioLen];
            short iTmp = 0;
            int i = 0;
            while ((line = br.readLine()) != null) {
                iTmp = Short.valueOf(line);
                dData[i] = iTmp;
                i++;

            }
            System.out.println("readTxtData dataNum is " + String.valueOf(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readTxtDataToShort(String sFileName, short[] dData) {
        try {
            File filename = new File(sFileName);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            short iTmp = 0;
            int i = 0;
            while ((line = br.readLine()) != null) {
                iTmp = Short.valueOf(line);
                dData[i] = iTmp;
                i++;
            }
            System.out.println("readTxtData dataNum is " + String.valueOf(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getWaveFile(String pcmFile){
        SimpleDateFormat sDateFormat  =   new SimpleDateFormat("yyyyMMddhhmmss");
        String    sDate   =    sDateFormat.format(new    java.util.Date());
        int iStart = pcmFile.length();
        String sWavPath =  pcmFile.substring(0,iStart-4) + "_"+ sDate +".wav";
        return sWavPath;
    }


    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */

    //ByteBuffer

    private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate, short bitsPerSample )
            throws IOException {
        byte[] header = new byte[44];

        //ckid    4  char   "RIFF" 标志, 大写
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        //cksize   4  int32  文件长度。这个长度不包括"RIFF"标志 和  文件长度 本身所占字节, 下面的子块大小也是这样。
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        //fcc type  4       char      "WAVE" 类型块标识, 大写。
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        //ckid  4       char      表示"fmt" chunk的开始。此块中包括文件内部格式信息。小写, 最后一个字符是空格。
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        //cksize          4       int32     文件内部格式信息数据的大小。
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        //FormatTag     2       int16     音频数据的编码方式。1 表示是 PCM 编码
        header[20] = 1; // format = 1
        header[21] = 0;

        //Channels      2       int16     声道数，单声道为1，双声道为2
        header[22] = (byte) (channels & 0xff);
        header[23] = (byte) ((channels >> 8) & 0xff);

        //SamplesPerSec 4       int32     采样率(每秒样本数), 比如 44100 等
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);

        //BytesPerSec   4       int32     音频数据传送速率, 单位是字节。其值为采样率×每次采样大小。播放软件 利用此值可以估计缓冲区的大小。
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);

        //BlockAlign    2       int16     每次采样的大小 = 采样精度*声道数/8(单位是字节); 这也是字节对齐的最小单位, 譬如 16bit 立体声在这里的值是 4 字节。播放软件需要一次处理多个该值大小的字节数据，以便将其值用于缓冲区的调整。
        short blockAlign = (short)(channels * bitsPerSample / 8);
        header[32] = (byte) (blockAlign& 0xff); // block align
        header[33] = (byte) ((blockAlign >> 8) & 0xff);

        //BitsPerSample 2       int16     每个声道的采样精度; 譬如 16bit 在这里的值就是16。如果有多个声道，则每个声道的采样精度大小都一样的。
        header[34] = (byte) (bitsPerSample & 0xff);; // bits per sample
        header[35] = (byte) ((bitsPerSample >> 8) & 0xff);;

        // ckid              4       char      表示 "data" chunk的开始。此块中包含音频数据。小写。
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        // cksize            4       int32     音频数据的长度
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    /**
     * byte数组与short数组转换
     *
     * @param data
     * * @param items
     * @return
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        if (data == null) {
            // Log.e(TAG, "byteArray2ShortArray, data = null");
            return null;
        }

        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        }

        return retVal;
    }

    public static double[] byteArray2DoubleArray(byte[] data) {
        if (data == null) {
            // Log.e(TAG, "byteArray2ShortArray, data = null");
            return null;
        }

        double[] retVal = new double[data.length /2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (double)((short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8));
            //retVal[i] = retVal[i] *10;
        }
        return retVal;
    }
    public static byte[] shortArray2ByteArray(short[] shortData){
        byte[] byteData = new byte[shortData.length*2];
        int idx = 0;
        for (final short val : shortData) {
            // in 16 bit wav PCM, first byte is the low order byte
            byteData[idx++] = (byte) (val & 0x00ff);
            byteData[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return byteData;
    }



    /**
     * byte数组与short数组转换
     *
     * @param data
     * * @param items
     * @return
     */
    /*
    public byte[] shortArray2ByteArray(short[] data) {
        byte[] retVal = new byte[data.length * 2];
        for (int i = 0; i < retVal.length; i++) {
            int mod = i % 2;
            if (mod == 0) {
                retVal[i] = (byte) (data[i / 2]);
            } else {
                retVal[i] = (byte) (data[i / 2] >> 8);
            }
        }
        return retVal;
    }*/

    /**
     * 将字节数组（byte[]）转为整形(int)
     */
    public void judgeEddian() throws IOException
    {
       /* byte[] byteAr = new byte[]{0x78,0x56,0x34,0x12};
        ByteArrayInputStream bais = new ByteArrayInputStream(byteAr);
        DataInputStream dis = new DataInputStream(bais);
        System.out.println(Integer.toHexString(dis.readInt()));
*/
        /**
         * 将整形(int)转为字节数组（byte[]）
         */
        int a = 0x12345678;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(a);
        byte[] b = baos.toByteArray();
        //byte[] b = baos.toByteArray();
        for(int i = 3; i >= 0; i--)
        {
            System.out.print(Integer.toHexString(b[i]));
        }
        System.out.println();
    }


    public static void readPlayData() throws IOException {
        GlobalConfig.playPcmFile = new File(GlobalConfig.sPlayPcmFileName);
        //DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(playPcmFile)));
        GlobalConfig.playPcmDis = new DataInputStream(new FileInputStream(GlobalConfig.playPcmFile));
        int iLen = GlobalConfig.playPcmDis.available();
               int i = 0;
        boolean iFirst = true;
        int iPcmIndex = 0;
        while(iPcmIndex <GlobalConfig.playData.length){
            if(GlobalConfig.sPlayPcmFileName.indexOf("wav")>0 && i<44 && iFirst){
                GlobalConfig.playPcmDis.readByte();
            }
            else{
                GlobalConfig.playData[iPcmIndex] = GlobalConfig.playPcmDis.readByte();
                iPcmIndex++;
            }
            i++;
        }
        if(iPcmIndex > 0){
            GlobalConfig.bPlayDataReady = true;
        }

        Log.i("play","read finish");


    }





}
