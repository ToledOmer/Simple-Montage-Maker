/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simplemontagemaker;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

/**
 *
 * @author Omer
 */
class Settings {

    static void init() {
        initSetting();
    }
    static ExecutorService executor;

    static FFmpeg ffmpeg = null;
    static FFprobe ffprobe = null;
    static LinkedBlockingQueue fList;
    static Map<Future<String>, String> progMap = new ConcurrentHashMap<>();
    static Map<Future<String>, String> progPathMap = new ConcurrentHashMap<>();
    static Map<String, String> InOutMap = new ConcurrentHashMap<>();
    static Map<String, ReentrantLock> InKeyMap = new ConcurrentHashMap<>();
    static ConcurrentLinkedDeque<File> deleteList = new ConcurrentLinkedDeque<>();
//    static LinkedList<File> FilesToDelete = new LinkedList<>();
       static String dir = System.getProperty("user.dir");

    public static LinkedBlockingQueue getfList() {
        return fList;
    }

    public static Map<Future<String>, String> getProgMap() {
        return progMap;
    }

    static void initSetting() {
        if (ffmpeg == null || ffprobe == null) {
            initFF();
        }
        if (executor == null) {
            executor = Executors.newFixedThreadPool(10);
        }
        if (fList == null) {
            fList = new LinkedBlockingQueue(10);
        }
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static FFmpeg getFfmpeg() {
        return ffmpeg;
    }

    public static FFprobe getFfprobe() {
        return ffprobe;
    }

    static FFprobe getFFprobe() {
        return ffprobe;
    }

    static FFmpeg getFmpeg() {
        return ffmpeg;
    }

    private static void initFF() {
        JFileChooser chooser = new JFileChooser();

        try {
            File f = new File(dir + "\\ffmpeg.exe");
            if (f.exists()) {
                ffmpeg = new FFmpeg(f.getAbsolutePath());

            } else {
                boolean chooseGood = false;
                while (!chooseGood) {
                    chooser.setDialogTitle("Choose FFMPEG File/s");
                    chooser.setMultiSelectionEnabled(false);
                    if (chooser.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                        chooser.setCurrentDirectory(chooser.getSelectedFile().getParentFile());

                        if (chooser.getSelectedFile().getName().equals("ffmpeg.exe")) {
                            ffmpeg = new FFmpeg(chooser.getSelectedFile().getAbsolutePath());
                            chooseGood = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "The file: " + chooser.getSelectedFile().getName() + " is not FFMPEG , please choose again");

                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No file Selected, the program wont run!!!");
                        JOptionPane.showMessageDialog(null, "EXITING!");
                        System.exit(-2);
                    }
//                    ffmpeg = new FFmpeg("C:\\ffmpeg-20200715-a54b367-win64-static\\bin\\ffmpeg.exe");

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            File f = new File(dir + "\\ffprobe.exe");
            if (f.exists()) {
                ffprobe = new FFprobe(f.getAbsolutePath());
            } else {
                boolean chooseGood = false;
                while (!chooseGood) {
                    chooser.setDialogTitle("Choose FFPROBE File/s");
                    chooser.setMultiSelectionEnabled(false);
                    if (chooser.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                        chooser.setCurrentDirectory(chooser.getSelectedFile().getParentFile());
                        if (chooser.getSelectedFile().getName().equals("ffprobe.exe")) {
                            ffmpeg = new FFmpeg(chooser.getSelectedFile().getAbsolutePath());
                            chooseGood = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "The file: " + chooser.getSelectedFile().getName() + " is not FFPROBE , please choose again");

                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No file Selected, the program wont run!!!");
                        JOptionPane.showMessageDialog(null, "EXITING!");
                        System.exit(-2);

                    }
//                    ffmpeg = new FFmpeg("C:\\ffmpeg-20200715-a54b367-win64-static\\bin\\ffmpeg.exe");

                }
            }

//            ffprobe = new FFprobe("C:\\ffmpeg-20200715-a54b367-win64-static\\bin\\ffprobe.exe");
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Map<String, String> getInOutMap() {
        return InOutMap;
    }

    static void CancelProg(String tmp, String menu) {
        if (tmp == null) {

            JOptionPane.showMessageDialog(new JFrame(), "No file selected!!!", "Compression success!",
                    JOptionPane.PLAIN_MESSAGE);

            return;
        }

//        int dialogButton = JOptionPane.YES_NO_OPTION;
//
//            int dialogResult = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to cancel Progress?\n" + tmp, "Cancel Progress", dialogButton);
//            if (dialogResult == 0) {
        System.err.println("outside");

        for (Object object : fList) {
            Future<String> future = (Future<String>) object;
            if (progMap.get(future).equals(tmp)) {
                System.err.println(tmp);
                File f = new File(InOutMap.get(tmp));
                f.deleteOnExit();
                deleteList.add(f);
                InKeyMap.remove(tmp);
                Settings.getfList().remove(future);
                progMap.remove(future);
                refreshMenu(menu);

            }

//                }
        }

    }

    static void exit() {
//        System.err.println("exitttt");
        
        ProcessHandle.current().children().forEach(c -> {
            kill(c);
        });
        for (Object object : fList) {
            
        }
        
        deleteList.forEach(File::delete);

    }

    static void kill(ProcessHandle handle) {
        handle.descendants().forEach((child) -> kill(child));
        handle.destroy();
    }

    static ReentrantLock getFileKey(String tmp) {
        if (InKeyMap.get(tmp) == null) {
            InKeyMap.put(tmp, new ReentrantLock());
        }
        return InKeyMap.get(tmp);

    }

    private static void refreshMenu(String menu) {
        switch (menu) {
            case "comp": {

                Compressor_Menu.refreshProgList();
                break;
            }

            case "trim": {
                Trimmer_Menu.refreshProgList();

                break;
            }
            case "comb": {

                Combiner_Menu.refreshProgList();

                break;
            }
        }
    }

}
