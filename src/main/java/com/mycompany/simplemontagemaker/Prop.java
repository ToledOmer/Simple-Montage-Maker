/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

/**
 *
 * @author Omer
 */
class Prop {

    private String forma;
    private int numOfChannel;
    private String audioCodec;
    private int audioSampleRate;
    private String videoCodec;
    private int frame;
    private int per;
    private int res1;
    private int res2;
    private long videoBitRate;
    private long audioBitRate;
    private double length;

    public String getForma() {
        return forma;
    }

    public int getNumOfChannel() {
        return numOfChannel;
    }

    public double getLength() {
        return length;
    }

    public Prop() {

        this.forma = "mp4";
        this.numOfChannel = 1;
        this.audioCodec = "aac";
        this.audioSampleRate = 48_000;
        this.videoCodec = "libx264";
        this.frame = 60;
        this.per = 1;
        this.res1 = 1920;
        this.res2 = 1080;
        this.videoBitRate = 4000000;

    }

    @Override
    public String toString() {
        return "Video Format:" + forma + "\n"
                + "numOfChannel:" + numOfChannel + "\n"
                + "audioCodec:" + audioCodec + "\n"
                + "audioSampleRate:" + audioSampleRate + "\n"
                + "videoCodec:" + videoCodec + "\n"
                + frame + " frames per " + per + " seconds\n"
                + "resulution: " + res1 + "x" + res2 + "\n"
                + "videoBitRate:" + (videoBitRate / 1000) + "kbps\n"
                + "audioBitRate:" + audioBitRate + "\n"
                + "length:" + lenToString();
    }

    public Prop(String forma, int numOfChannel,
            String audioCodec, int audioSampleRate,
            String videoCodec, int frame, int per,
            int res1, int res2, long videoBitRate,
            long audioBitRate) {
        this.forma = forma;
        this.numOfChannel = numOfChannel;
        this.audioCodec = audioCodec;
        this.audioSampleRate = audioSampleRate;
        this.videoCodec = videoCodec;
        this.frame = frame;
        this.per = per;
        this.res1 = res1;
        this.res2 = res2;
        this.videoBitRate = videoBitRate;
        this.audioBitRate = audioBitRate;
    }

    public Prop(String forma, int numOfChannel,
            String audioCodec, int audioSampleRate,
            String videoCodec, int frame, int per,
            int res1, int res2, long videoBitRate,
            long audioBitRate, double length) {
        this.forma = forma;
        this.numOfChannel = numOfChannel;
        this.audioCodec = audioCodec;
        this.audioSampleRate = audioSampleRate;
        this.videoCodec = videoCodec;
        this.frame = frame;
        this.per = per;
        this.res1 = res1;
        this.res2 = res2;
        this.videoBitRate = videoBitRate;
        this.audioBitRate = audioBitRate;
        this.length = length;
    }

    String getFormat() {
        return forma;
    }

    int getNumOfChannels() {
        return numOfChannel;
    }

    String getAudioCodec() {
        return audioCodec;
    }

    int getAudioSampleRate() {
        return audioSampleRate;
    }

    String getVideoCodec() {
        return videoCodec;
    }

    int getFrame() {
        return frame;
    }

    int getPer() {
        return per;
    }

    int getRes1() {
        return res1;
    }

    int getRes2() {
        return res2;
    }

    long getVideoBitRate() {
        return videoBitRate;
    }

    long getAudioBitRate() {
        return audioBitRate;
    }

    static String lenToString(double dur) {

        String len = "";

        if (dur == 0) {
            return "0";
        }
        Double h = (Math.floor(dur / 3600));
        Double m = (Math.floor((dur / 60)) - h * 60);
        Double s = Math.floor((((dur / 60) - Math.floor((dur / 60))) * 60));
        if (h < 10) {
            len += "0" + h.intValue() + ":";

        } else {
            len += h.intValue() + ":";

        }
        if (m < 10) {
            len += "0" + m.intValue() + ":";

        } else {
            len += m.intValue() + ":";

        }
        if (s < 10) {
            len += "0" + s.intValue();

        } else {
            len += s.intValue();

        }
        return len;

    }

    private String lenToString() {

        String len = "";

        if (length == 0) {
            return "0";
        }
        Double h = (Math.floor(length / 3600));
        Double m = (Math.floor((length / 60)) - h * 60);
        Double s = Math.floor((((length / 60) - Math.floor((length / 60))) * 60));
        if (h < 10) {
            len += "0" + h.intValue() + ":";

        } else {
            len += h.intValue() + ":";

        }
        if (m < 10) {
            len += "0" + m.intValue() + ":";

        } else {
            len += m.intValue() + ":";

        }
        if (s < 10) {
            len += "0" + s.intValue();

        } else {
            len += s.intValue();

        }
        return len;

    }

}
