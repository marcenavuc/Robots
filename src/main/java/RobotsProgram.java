import gui.*;
import gui.windows.AckFrame;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.Const.*;

public class RobotsProgram {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            //        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            boolean notLoad = true;

            if (checkExistFile("")) {
                ResourceBundle tempBundle = ResourceBundle.getBundle(baseNameBundle, Locale.getDefault());
                AckFrame ackFrame = new AckFrame();
                notLoad = ackFrame.ackExit(tempBundle.getString("ack.load"), tempBundle) != 0;
                ackFrame.dispose();
            }
            try {
                new MainFrame(notLoad, new String[] { "ru", "en" });
            }
            catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean checkExistFile(String path){
        if (!path.equals("") && !path.endsWith("/"))
            path += "/";

        for (String file : Arrays.asList(mainFile, gameFile, logFile))
            if ((new File(path + file)).exists())
                return true;
            return false;
    }
}
