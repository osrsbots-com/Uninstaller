package com.osrsbots.orb;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Uninstaller extends JFrame {

    private JPanel root;
    private JButton start;
    private JCheckBox keepAccount, keepProxy, keepBreak;
    private JLabel subInfoLbl, infoLbl;

    public Uninstaller() {
        initControls();

        setContentPane(root);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("ORB - Uninstaller");
        setIconImage(getIcon());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initControls() {
        final Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        keepAccount.setCursor(cursor);
        keepProxy.setCursor(cursor);
        keepBreak.setCursor(cursor);
        start.setCursor(cursor);

        start.addActionListener(e -> {
            if (!start.isEnabled()) return;
            start.setBackground(ColorScheme.DARK_GRAY_COLOR);
            start.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            start.setText("Started");
            start.setEnabled(false);

            subInfoLbl.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);

            keepAccount.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            keepAccount.setEnabled(false);

            keepProxy.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            keepProxy.setEnabled(false);

            keepBreak.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            keepBreak.setEnabled(false);

            infoLbl.setText("Uninstalling...");
            uninstall();
        });
    }

    private Image getIcon() {
        // Load the icon from the resource folder
        final URL iconURL = Executable.class.getResource("/resources/logo.png"); // Adjust the icon file name

        if (iconURL != null) {
            final ImageIcon icon = new ImageIcon(iconURL);
            return icon.getImage();
        } else {
            System.err.println("Icon not found in resources.");
        }

        return null;
    }

    private static final String dir = (System.getProperty("user.home") + File.separator + "ORB");

    private void uninstall() {
        /* Determine App Data Folder */
        final String os = System.getProperty("os.name").toLowerCase();
        String appDataDir;

        if (os.contains("win")) {
            appDataDir = System.getenv("LOCALAPPDATA") + File.separator + "ORB";
        } else if (os.contains("mac")) {
            appDataDir = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "ORB";
        } else {
            appDataDir = dir;
        }

        /* Delete logs */
        final String logsDir = dir + File.separator + "logs";

        List<File> files = getAllFiles(new File(logsDir + File.separator + "client"));
        files.forEach(f -> {
            if (f.isFile()) f.delete();
        });

        files = getAllFiles(new File(logsDir + File.separator + "launcher"));
        files.forEach(f -> {
            if (f.isFile()) f.delete();
        });

        /* Delete script jars */
        files = getAllFiles(new File(dir + File.separator + "scripts"));
        files.forEach(f -> {
            if (f.isFile()) f.delete();
        });

        /* Delete app data */
        files = getAllFiles(new File(appDataDir));

        for (File file : files) {
            final String name = file.getName();

            System.out.println("Deleting=" + name);

            switch (name) {
                case ".break":
                case ".breaks": // Breaks
                    if (keepBreak.isSelected())
                        continue;
                    break;
                case ".proxies": // Proxies
                    if (keepProxy.isSelected())
                        continue;
                    break;
                case ".data": // Accounts
                    if (keepAccount.isSelected())
                        continue;
                    break;
                case "uninstall.jar":
                    // ignore
                    continue;
                default:
                    break;
            }

            if (!file.delete()) {
                infoLbl.setForeground(Color.red);
                infoLbl.setText("ERROR - Unable to delete file!");
                subInfoLbl.setText(file.getPath() + " @ " + file.getName());
                return;
            }
        }

        // Delete folders
        final File buildFolder = new File(appDataDir + File.separator + "build");
        if (buildFolder.exists()) buildFolder.delete();

        final File javaFolder = new File(appDataDir + File.separator + "java");

        if (javaFolder.exists()) {
            if (deleteFolder(javaFolder)) {
                javaFolder.delete();
            }
        }

        final File libFolder = new File(appDataDir + File.separator + "libs");
        if (libFolder.exists()) libFolder.delete();

        if (!keepBreak.isSelected() && !keepProxy.isSelected() && !keepAccount.isSelected()) {
            final File dataFolder = new File(appDataDir + File.separator + "data");
            if (dataFolder.exists()) dataFolder.delete();
        }


        // Success
        infoLbl.setForeground(ColorScheme.BRAND_GREEN);
        infoLbl.setText("SUCCESS");
        start.setText("Uninstalled");
    }

    private static List<File> getAllFiles(File directory) {
        List<File> fileList = new ArrayList<>();

        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return fileList;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // If the current file is a regular file, add it to the list
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    // If the current file is a directory, recursively call getAllFiles
                    // to get files within this subdirectory
                    fileList.addAll(getAllFiles(file));
                }
            }
        }

        return fileList;
    }

    private static boolean deleteFolder(File folder) {
        if (folder == null || !folder.exists()) {
            // If folder is null or does not exist, consider it already "deleted"
            return true;
        }

        if (folder.isDirectory()) {
            // List all files and subdirectories within the folder
            File[] files = folder.listFiles();
            if (files != null) {
                // Recursively delete files and subdirectories
                for (File file : files) {
                    boolean success = deleteFolder(file);
                    if (!success) {
                        // If any deletion fails, return false immediately
                        return false;
                    }
                }
            }
        }

        // After deleting all contents (or if it's a file), attempt to delete the folder
        return folder.delete();
    }
}
