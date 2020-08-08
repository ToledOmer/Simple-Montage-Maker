/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Omer
 */
public class Compressor implements Callable {

    private boolean toMute = false;
    private String in;
    private String out;
    private Prop p;
    private FFmpegBuilder builder;

    public Compressor(boolean toMute, String in,String out, Prop p) {
        this.toMute = toMute;
        this.in = in;
        this.p = p;
        this.out = out;
    }

//    public static void CompressFiles(LinkedList<Pair<String, Prop>> list) {
//        for (Pair<String, Prop> pair : list) {
//            CompressFile(pair.first, pair.second);
//        }
//
//    }
    public String CompressFile() {
        FFmpeg ffmpeg = Settings.getFmpeg();
        FFprobe ffprobe = Settings.getFFprobe();

        if (p == null) {
            System.err.println("prop is null!!!!");
            p = new Prop();
        }
        File file = new File(in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println("start at " + dtf.format(now));
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
         out = out + "\\" + name + "(compressed)" + dtf.format(now) + "." + p.getFormat();
        //if the file got

        Settings.getInOutMap().put(in, out);

        builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out) // Filename for the destination
                .setFormat(p.getFormat()) // Format is inferred from filename, or can be set
                .setAudioChannels(p.getNumOfChannels())
                .setAudioCodec(p.getAudioCodec()) // using the aac codec
                .setAudioSampleRate(p.getAudioSampleRate()) // at 48KHz
                .setAudioBitRate(p.getAudioBitRate()) // at 64 kbit/s
                .setVideoCodec(p.getVideoCodec()) // Video using x264
                .setVideoFrameRate(p.getFrame(), p.getPer()) // at 24 frames per second
                .setVideoResolution(p.getRes1(), p.getRes2()) // at 640x480 resolution
                .setVideoBitRate(p.getVideoBitRate())
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }
        now = LocalDateTime.now();
        return out;
    }

    public String CompressFileSize(long sz) {
        FFmpeg ffmpeg = Settings.getFmpeg();
        FFprobe ffprobe = Settings.getFFprobe();

        if (p == null) {
            System.err.println("prop is null!!!!");
            p = new Prop();
        }
        File file = new File(in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println("start at " + dtf.format(now));
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        String out = file.getParent() + "\\" + name + "(compressed)" + dtf.format(now) + "." + p.getFormat();
        //in wont be in the map untl now
        Settings.getInOutMap().put(in, out);

        FFmpegProbeResult probeResult;
        try {
            probeResult = ffprobe.probe(in);

            builder = new FFmpegBuilder()
                    .setInput(probeResult) // Filename, or a FFmpegProbeResult
                    .overrideOutputFiles(true) // Override the output if it exists
                    .addOutput(out) // Filename for the destination
                    .setTargetSize(2_000_000) //in mb -> sz-mb
                    .setFormat(p.getFormat()) // Format is inferred from filename, or can be set
                    .setAudioChannels(p.getNumOfChannels())
                    .setAudioCodec(p.getAudioCodec()) // using the aac codec
                    .setAudioSampleRate(p.getAudioSampleRate()) // at 48KHz
                    .setAudioBitRate(p.getAudioBitRate()) // at 64 kbit/s
                    .setVideoCodec(p.getVideoCodec()) // Video using x264
                    .setVideoFrameRate(p.getFrame(), p.getPer()) // at 24 frames per second
                    .setVideoResolution(p.getRes1(), p.getRes2()) // at 640x480 resolution
                    .setVideoBitRate(p.getVideoBitRate())
                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                    .done();
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run(); // Run a one-pass encode    }
        } catch (IOException ex) {
            Logger.getLogger(Compressor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    public String CompressFileDisAudio() {

        FFmpeg ffmpeg = Settings.getFmpeg();
        FFprobe ffprobe = Settings.getFFprobe();
        if (p == null) {
            System.err.println("prop is null!!!!");
            p = new Prop();
        }
        File file = new File(in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
//        System.out.println("start at " + dtf.format(now));
        String name = file.getName().substring(0, file.getName().indexOf("."));
         out = out + "\\" + name + "(compressed)(muted)" + dtf.format(now) + "." + p.getFormat();
        Settings.getInOutMap().put(in, out);

        builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out) // Filename for the destination
                .setFormat(p.getFormat()) // Format is inferred from filename, or can be set
                .addExtraArgs("-map", "0:0")
                .setVideoCodec(p.getVideoCodec()) // Video using x264
                .setVideoFrameRate(p.getFrame(), p.getPer()) // at 24 frames per second
                .setVideoResolution(p.getRes1(), p.getRes2()) // at 640x480 resolution
                .setVideoBitRate(p.getVideoBitRate())
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }

        now = LocalDateTime.now();
        return out;
    }

    @Override
    public String call() throws Exception {
        String tmp = toMute ? CompressFileDisAudio() : CompressFile();
        Settings.getExecutor().submit(() -> {
            ReentrantLock lock = Settings.getFileKey(in);
            lock.lock();
            try {

                synchronized (Settings.getfList()) {
                    for (Object d : Settings.getfList()) {
                        Future<String> future = (Future<String>) d;
                        if (future.isDone() && Settings.progMap.get(future).equals(in)) {
                            String message = new String();
                            try {
                                message = "file:\n" + future.get() + "\ncompressd successfully";
                            } catch (InterruptedException | ExecutionException ex) {
                                Logger.getLogger(Compressor.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            JOptionPane.showMessageDialog(new JFrame(), message, "Compression success!",
                                    JOptionPane.PLAIN_MESSAGE);
                            Settings.getInOutMap().remove(in);
                            Settings.getfList().remove(future);
                            Settings.getProgMap().remove(future);

                            Compressor_Menu.refreshProgList();
                        }
                    }
                }
            } finally {
                lock.unlock();
            }

        });
        return tmp;

    }

    public FFmpegBuilder getBuilder() {
        return builder;
    }

}
