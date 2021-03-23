package gui;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import sun.rmi.runtime.Log;

public class LogWindow extends JInternalFrame implements LogChangeListener, Externalizable
{
    private LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    public void exit() {
        m_logSource.unregisterListener(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeObject(m_logSource);   // saving LogWindow not working
        out.writeObject(m_logContent.getText()); // saving Text
        out.writeObject(m_logContent.getSize());  // saving Size
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_logSource = (LogWindowSource) in.readObject();
        String text = (String) in.readObject();
        Dimension dim = (Dimension) in.readObject();
        m_logContent.setText(text);
        m_logContent.setSize(dim);
    }
}
