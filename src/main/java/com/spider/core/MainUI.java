package com.spider.core;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.util.StrUtil;

/**
 * @author shiliang.li
 * @date 2020/5/19
 */
public class MainUI extends JFrame implements ActionListener, TextListener {

    /**
     * 搜索的url
     */
    public static final String SEACH_URL = "/w?q={skuCode}&vst={skuCode}";

    /**
     * 驱动位置
     */
    public static final String CHROME_DRIVER = "D:\\chromedriver\\chromedriver.exe";

    private JLabel nikeUrlLabel;

    private JTextField nikeUrlText;

    private JLabel skuCodeLabel;

    private JTextField skuCodeText;

    private JLabel pdpUrlLabel;

    private JTextField pdpUrlText;

    private JLabel cssStyleLabel;

    private JTextField cssStyleText;

    private JLabel fileSavePathLabel;

    private JTextField fileSavePathText;

    private JButton fileSaveBtn;

    private JButton spiderBtn;

    private JProgressBar progressBar;

    public MainUI() throws HeadlessException {
        this.setLayout(null);
        this.setName("主界面");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(500, 500);
        this.setSize(600, 300);
        this.setResizable(false);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/icon_1.png"));
        this.setIconImage(imageIcon.getImage());
        this.setVisible(true);
        this.nikeUrlLabel = new JLabel("nikeUrl");
        nikeUrlLabel.setBounds(50, 10, 80, 25);
        this.add(nikeUrlLabel);

        this.nikeUrlText = new JTextField();
        nikeUrlText.setBounds(150, 10, 300, 25);
        nikeUrlText.addActionListener(this);
        this.add(nikeUrlText);

        this.skuCodeLabel = new JLabel("skuCode:");
        skuCodeLabel.setBounds(50, 40, 80, 25);
        this.add(skuCodeLabel);

        this.skuCodeText = new JTextField();
        skuCodeText.setBounds(150, 40, 300, 25);
        skuCodeText.addActionListener(this);
        this.add(skuCodeText);

        this.pdpUrlLabel = new JLabel("pdpUrl:");
        pdpUrlLabel.setBounds(50, 70, 80, 25);
        this.add(pdpUrlLabel);

        this.pdpUrlText = new JTextField();
        pdpUrlText.setBounds(150, 70, 300, 25);
        pdpUrlText.addActionListener(this);
        this.add(pdpUrlText);

        this.cssStyleLabel = new JLabel("cssStyle:");
        cssStyleLabel.setBounds(50, 100, 80, 25);
        this.add(cssStyleLabel);

        this.cssStyleText = new JTextField();
        cssStyleText.setBounds(150, 100, 300, 25);
        cssStyleText.setText("css-1bhwifz");
        cssStyleText.addActionListener(this);
        this.add(cssStyleText);

        this.fileSavePathLabel = new JLabel("save path:");
        fileSavePathLabel.setBounds(40, 130, 100, 25);
        this.add(fileSavePathLabel);
        this.fileSavePathText = new JTextField();
        fileSavePathText.setBounds(150, 130, 300, 25);
        fileSavePathText.addActionListener(this);
        this.add(fileSavePathText);
        this.fileSaveBtn = new JButton("...");
        fileSaveBtn.setBounds(455, 130, 60, 25);
        fileSaveBtn.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Component component = null;
            int saveDialog = jFileChooser.showSaveDialog(component);
            if (saveDialog == JFileChooser.APPROVE_OPTION) {
                String selectPath = jFileChooser.getSelectedFile().getPath() + "\\";
                System.out.println("你选择的目录是：" + selectPath);
                fileSavePathText.setText(selectPath);
            }
        });
        this.add(fileSaveBtn);
        this.progressBar = new JProgressBar();
        progressBar.setBounds(150, 160, 300, 25);
        progressBar.setStringPainted(true);
        this.add(progressBar);
        this.spiderBtn = new JButton("抓取");
        spiderBtn.setBounds(220, 190, 80, 25);
        spiderBtn.addActionListener(this);
        this.add(spiderBtn);
        this.repaint();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        new Thread(() -> {
            if (e.getSource() == spiderBtn) {
                for (int i = 0; i < 4; i++) {
                    try {
                        Thread.sleep(100);
                        progressBar.setValue(i * 25);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                }
                doDown();
                try {
                    // 休眠线程使刷新线程能够启动
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        repaint();

    }

    private void doDown() {
        try {
            spiderBtn.setEnabled(false);
            SpiderUtils.downZip(prihandleSpiderCommand());
            progressBar.setValue(100);
            JOptionPane.showMessageDialog(null, "下载成功！", "", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "下载失败！", "", JOptionPane.ERROR_MESSAGE);
        } finally {
            spiderBtn.setEnabled(true);
        }

    }

    private SpiderCommand prihandleSpiderCommand() {
        SpiderCommand spiderCommand = new SpiderCommand();
        spiderCommand.setChromDriverPath(CHROME_DRIVER);
        if (StringUtils.isNotBlank(skuCodeText.getText())) {
            spiderCommand.setSkuCode(skuCodeText.getText());
        }
        if (StringUtils.isNotBlank(skuCodeText.getText())) {
            String nikeUrlTextValue = StrUtil.removeSuffix(nikeUrlText.getText(), "/").replaceAll(" ", "");
            String skuCodeTextValue = replaceAll(skuCodeText.getText());
            spiderCommand.setMasterStationUrl(nikeUrlTextValue
                    + StringUtils.replaceEachRepeatedly(SEACH_URL, new String[] { "{skuCode}", "{skuCode}" },
                            new String[] { skuCodeTextValue, skuCodeTextValue }));

        } else {
            if (StringUtils.isNotBlank(pdpUrlText.getText())) {
                String pdpUrlTextValue = replaceAll(pdpUrlText.getText());
                spiderCommand.setMasterStationUrl(pdpUrlTextValue);
                spiderCommand.setPdpUrl(pdpUrlTextValue);
            }
        }
        spiderCommand.setCssStyle(replaceAll(cssStyleText.getText()));
        spiderCommand.setExportPath(fileSavePathText.getText());
        return spiderCommand;
    }

    private String replaceAll(String str) {
        return str.replaceAll(" ", "");
    }

    public static void main(String[] args) {
        MainUI mainUI = new MainUI();
    }

    @Override
    public void textValueChanged(TextEvent e) {

    }
}
