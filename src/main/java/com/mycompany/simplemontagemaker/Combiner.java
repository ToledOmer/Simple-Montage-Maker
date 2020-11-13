/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

import static com.mycompany.simplemontagemaker.Settings.ffmpeg;
import static com.mycompany.simplemontagemaker.Settings.ffprobe;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

/**
 *
 * @author Omer
 */
public class Combiner implements Callable<String> {

    LinkedList<String> list;
    String path;
    private boolean addMp3;
    private boolean toMute;
    private String audio;

    public Combiner(LinkedList<File> list, String path, boolean addMp3, boolean toMute, String audio) {
        this.list = list.stream().map((f) -> f.getAbsolutePath()).collect(Collectors.toCollection(LinkedList::new));
        this.path = path;
        this.addMp3 = addMp3;
        this.toMute = toMute;
        this.audio = audio;
    }

    /**
     * combine a list of video files into one video file<br> </br>
     * if list.soze == 1 --> nothing will happen!!!<br> </br>
     *
     * @param format - "mp4" / "mkv" /"avi" / "flv"
     *
     * @param list - list of all the absulouts paths of the files we want to
     * combine<br> </br>
     * @param combo - absolutr path of the combines file(including format)<br> </br>
     */
    public String combineList() throws InterruptedException, IOException {
        String fmpeg = Settings.getFfmpeg().getPath();

        LinkedList<File> toRemove = new LinkedList<>();
        String c = "concat:";
        int t = 0;
        File pp = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
        Process processDuration;
//        System.err.println(list.size());
        while (list.size() > 1) {
            String p1 = list.get(0);
            File file1 = new File(p1);
            String out1 = file1.getParent() + "\\" + "temp" + t++ + dtf.format(now) + "." + "ts";
            new File(out1).deleteOnExit();
            toRemove.add(new File(out1));
            Settings.deleteList.add(new File(out1));
            new File(out1).deleteOnExit();

            processDuration = new ProcessBuilder(fmpeg, "-i", p1, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", out1).redirectErrorStream(true).start();
            processDuration.waitFor();

            String p2 = list.get(1);

            File file2 = new File(p2);
            String out2 = file2.getParent() + "\\" + "temp" + t++ + dtf.format(now) + "." + "ts";
            toRemove.add(new File(out2));
            Settings.deleteList.add(new File(out2));
            new File(out2).deleteOnExit();
            processDuration = new ProcessBuilder(fmpeg, "-i", p2, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", out2).redirectErrorStream(true).start();
            processDuration.waitFor();

            String tmpP = path;
            path += "\\" + "combo" + dtf.format(now) + t++ + ".mp4";
            processDuration = new ProcessBuilder(fmpeg, "-i", c + out1 + "|" + out2, "-c", "copy", "-bsf:a", "aac_adtstoasc", path).redirectErrorStream(true).start();
            processDuration.waitFor();
            pp = new File(path);
            Settings.deleteList.add(pp);
            new File(out1).delete();

            list.addFirst(path);

            path = tmpP;

            list.remove(1);
            list.remove(1);
            if (list.size() != 1) {
                pp.deleteOnExit();
                toRemove.add(pp);
                Settings.deleteList.add(pp);
            } else {

            }
            if (toRemove.size() > 2) {
                toRemove.pop().delete();

            }
        }
        for (File file : toRemove) {
            file.delete();
        }
        for (File file : toRemove) {
            file.delete();
        }

        String name = CreateOutNameWithFormat("ComboFinal","mp4" , path);
        if (pp != null) {
            pp.renameTo(new File(name));
            pp = new File(name);
            Settings.deleteList.remove(pp);
            return pp.getAbsolutePath();

        }
        return null;
    }

    private String CreateOutNameWithFormat(String fName , String format, String outPath) {
        
        int j = 1;
        String name = outPath + "\\" + fName + "."+format;
        while (new File(name).exists()) {
            name = outPath + "\\" + fName + "(" + j + ")" +"."+format;
            j++;
        }
        return name;
    }

    @Override
    public String call() throws Exception {
        String tmp = combineList();
        System.err.println(tmp);
        if (addMp3) {

            tmp = toMute ? MuteAndAdd(tmp) : AddMusic(tmp);
        } else if (toMute) {
            tmp = onlyMute(tmp);
        }
        Settings.getExecutor().submit(() -> {
            for (Object d : Settings.getfList()) {
                Future<String> future = (Future<String>) d;
                if (future.isDone()) {
                    String message = new String();
                    try {
                        message = "file:\n" + future.get() + "\nCombining Complete successfully";
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(Compressor.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(new JFrame(), message, "Combining success!",
                            JOptionPane.PLAIN_MESSAGE);
                    Settings.getfList().remove(future);
                    Combiner_Menu.refreshProgList();
                }
            }
        });

        return tmp;

    }

    private String AddMusic(String in) {

        File inFile = new File(in);
        String out = inFile.getParent() + "\\" + "Combo(Audio)" + ".mp4";
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .addExtraArgs("-i", audio)
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out)
                // 0 - mp3 , 1- mp4 (in)
                .addExtraArgs("-map", "1:0") //1 - mp4 // 0 - video
                .addExtraArgs("-map", "1:1") //1 - mp4 // 1 - audio
                .addExtraArgs("-map", "0:0") //0 - mp3 // 0 - for mp3 is audio
                .addExtraArgs("-shortest")
                .addExtraArgs("-c", "copy")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }
        inFile.delete();
        return out;

    }

    private String MuteAndAdd(String in) {
        File inFile = new File(in);
        String out = inFile.getParent() + "\\" + "Combo(mutedAudio)" + ".mp4";
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .addExtraArgs("-i", audio)
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out)
                // 0 - mp3 , 1- mp4 (in)
                .addExtraArgs("-map", "1:0") //1 - mp4 // 0 - video
                //                .addExtraArgs("-map", "1:1") //1 - mp4 // 1 - audio
                .addExtraArgs("-map", "0:0") //0 - mp3 // 0 - for mp3 is audio
                .addExtraArgs("-shortest")
                .addExtraArgs("-c", "copy")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }
        inFile.delete();

        return out;
    }

    private String onlyMute(String in) {
        File inFile = new File(in);
        String out = inFile.getParent() + "\\" + "Combo(Allmuted)" + ".mp4";
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists
                .addOutput(out)
                .addExtraArgs("-map", "0:0") //0 - mp3 // 0 - for mp3 is audio
                .addExtraArgs("-shortest")
                .addExtraArgs("-c", "copy")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run(); // Run a one-pass encode    }
        inFile.delete();

        return out;

    }

}
