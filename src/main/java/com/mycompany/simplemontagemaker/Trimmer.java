/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

/**
 *
 * @author Omer
 */
public class Trimmer implements Callable {

    static long getTotalDur(Path toPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private trimProp tprop;
    private String in;
    private String out;

    public Trimmer(trimProp tprop, String in,String out) {
        this.tprop = tprop;
        this.in = in;
           this.out = out;
    }

    private String trimFile() {

        FFmpeg ffmpeg = null;

        try {
            ffmpeg = new FFmpeg("C:\\ffmpeg-20200715-a54b367-win64-static\\bin\\ffmpeg.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FFprobe ffprobe = null;
        try {
            ffprobe = new FFprobe("C:\\ffmpeg-20200715-a54b367-win64-static\\bin\\ffprobe.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long secondsOffset = TimeUnit.SECONDS.toMillis(tprop.getSecondOff());
        Long minutesOffset = TimeUnit.MINUTES.toMillis(tprop.getMinutesOffset());
        Long hoursOffset = TimeUnit.HOURS.toMillis(tprop.getHoursOffset());
        Long secondsLen = TimeUnit.SECONDS.toMillis(tprop.getSecondsLen());
        Long minutesLen = TimeUnit.MINUTES.toMillis(tprop.getMinutesLen());
        Long hoursLen = TimeUnit.HOURS.toMillis(tprop.getHoursLen());

        File file = new File(in);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("start at " + dtf.format(now));
        String name = file.getName().substring(0, file.getName().indexOf("."));
        out = out + "\\" + name + "(Trimmed)" + "." + tprop.getFormat();
        FFmpegBuilder builder;
        builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out) // Filename for the destination
                .setStartOffset(hoursOffset + minutesOffset + secondsOffset, TimeUnit.MILLISECONDS)
                .setDuration(hoursLen + minutesLen + secondsLen, TimeUnit.MILLISECONDS)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }
        now = LocalDateTime.now();
        System.out.println("file " + file.getName() + " trimmed!, time:" + dtf.format(now));
        return out;
    }

    @Override
    public String call() throws Exception {
        String tmp = trimFile();
        
                Settings.getExecutor().submit(() -> {
//            synchronized (Settings.getfList()) {
            for (Object d : Settings.getfList()) {
                Future<String> future = (Future<String>) d;
                if (future.isDone()) {
                    String message = new String();
                    try {
                        message = "file:\n" + future.get() + "\nTrimming Complete successfully";

                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(Compressor.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(new JFrame(), message, "Combining success!",
                            JOptionPane.PLAIN_MESSAGE);
                    Settings.getfList().remove(future);
                    Trimmer_Menu.refreshProgList();
                }
//                }
            }

        });
        
        
        return tmp;
    }
}
