/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

import java.io.IOException;
import java.nio.file.Path;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

/**
 *
 * @author Omer
 */
class trimProp {

    static long getTotalDur(String toPath) throws IOException {

        FFprobe ffprobe = Settings.getFFprobe();
        FFmpegProbeResult probeResult = ffprobe.probe(toPath);
        FFmpegFormat forma = probeResult.getFormat();
        double length = forma.duration;
         Double h = (Math.floor(length / 3600));
        Double m = (Math.floor((length / 60)) -h*60);
        Double s = Math.floor((((length / 60) - Math.floor((length / 60))) * 60));
        long ret =new Double((10000 * h) + (100 * m) + (s)).longValue(); 
        return ret;

    }

    private long secondOff;
    private long minutesOff;
    private long hoursOff;
    private long secondsLen;
    private long minutesLen;
    private long hoursLen;
    private String forma;

    public trimProp(long secondOff, long minutesOff, long hoursOff, long secondsLen, long minutesLen, long hoursLen, String forma) {
        this.secondOff = secondOff;
        this.minutesOff = minutesOff;
        this.hoursOff = hoursOff;
        this.secondsLen = secondsLen;
        this.minutesLen = minutesLen;
        this.hoursLen = hoursLen;
        this.forma = forma;
    }

    public long getSecondOff() {
        return secondOff;
    }

    public long getMinutesOff() {
        return minutesOff;
    }

    public long getHoursOff() {
        return hoursOff;
    }

    public String getForma() {
        return forma;
    }

    long getMinutesOffset() {
        return minutesOff;
    }

    long getHoursOffset() {
        return hoursOff;
    }

    long getSecondsLen() {
        return secondsLen;
    }

    long getMinutesLen() {
        return minutesLen;
    }

    long getHoursLen() {
        return hoursLen;
    }

    String getFormat() {
        return forma;
    }

}
