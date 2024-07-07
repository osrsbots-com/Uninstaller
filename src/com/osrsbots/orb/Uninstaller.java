package com.osrsbots.orb;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class Uninstaller extends JFrame {

    private JPanel root;
    private JButton start;
    private JCheckBox keepAccount, keepProxy, keepBreak;
    private JLabel subInfoLbl, infoLbl;
    private JCheckBox keepProfiles;

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

            keepProfiles.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            keepProfiles.setEnabled(false);

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

    private void uninstall() {
        /* Determine App Data Folder */
        final String os = System.getProperty("os.name").toLowerCase();
        String appDataDir;

        if (os.contains("win")) {
            appDataDir = System.getenv("LOCALAPPDATA") + File.separator + "ORB";
        } else if (os.contains("mac")) {
            appDataDir = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "ORB";
        } else {
            appDataDir = System.getProperty("user.home") + File.separator + "ORB" + File.separator + "ORB";
        }

        /* Delete app data */
        File[] files = new File(appDataDir).listFiles();

        for (File file : files) {
            deleteFile(file);
        }

        // Success
        infoLbl.setForeground(ColorScheme.BRAND_GREEN);
        infoLbl.setText("SUCCESS");
        start.setText("Uninstalled");
    }

    private boolean delete(final File file) {
        final String name = file.getName();
        System.out.println("Deleting=" + name);

        switch (name) {
            case ".breaks": // Breaks
                if (keepBreak.isSelected()) return true;
                break;
            case ".proxies": // Proxies
                if (keepProxy.isSelected()) return true;
                break;
            case ".data": // Accounts
                if (keepAccount.isSelected()) return true;
                break;
            case ".profiles": // Launch profiles
                if (keepProfiles.isSelected()) return true;
                break;
            case "uninstall.jar":
                // ignore
                return true;
            default:
                break;
        }

        if (!file.delete()) {
            infoLbl.setForeground(Color.red);
            infoLbl.setText("ERROR - Unable to delete file!");
            subInfoLbl.setText(file.getPath() + " @ " + file.getName());
            return false;
        }

        return true;
    }

    private boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            final File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (!deleteFile(f)) {
                        return false;
                    }
                }
            }
        }

        return delete(file);
    }
}
