package com.spider.core;

import org.apache.commons.lang3.StringUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

/**
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderMainUI {
    public static final String SEACH_URL = "/w?q={skuCode}&vst={skuCode}";

    /**
     * 驱动位置
     */
    public static final String CHROME_DRIVER = "D:\\chromedriver\\chromedriver.exe";

    static JFrame jFrame = new JFrame();

    public void initJframe() {
        jFrame.setName("主界面");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocation(500, 500);
        jFrame.setSize(600, 300);
        jFrame.setResizable(false);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/icon_1.png"));
        jFrame.setIconImage(imageIcon.getImage());
        JPanel panel = new JPanel();
        jFrame.add(panel);
        placeComponentsV1(panel);
        jFrame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        /*
         * 布局部分我们这边不多做介绍 这边设置布局为 null
         */
        panel.setLayout(null);

        JLabel nikeUrl = new JLabel("nikeUrl");
        nikeUrl.setBounds(50, 10, 80, 25);
        panel.add(nikeUrl);
        JTextField nikeUrlText = new JTextField();
        nikeUrlText.setBounds(150, 10, 300, 25);
        panel.add(nikeUrlText);

        // 创建 skuCodeJLabel
        JLabel skuCodeLabel = new JLabel("skuCode:");
        skuCodeLabel.setBounds(50, 40, 80, 25);
        panel.add(skuCodeLabel);
        JTextField skuCodeText = new JTextField();
        skuCodeText.setBounds(150, 40, 300, 25);
        panel.add(skuCodeText);

        // pdpurl
        JLabel pdpUrlLabel = new JLabel("pdpUrl:");
        pdpUrlLabel.setBounds(50, 70, 80, 25);
        panel.add(pdpUrlLabel);
        JTextField pdpUrlText = new JTextField();
        pdpUrlText.setBounds(150, 70, 300, 25);
        panel.add(pdpUrlText);

        // cssStyle
        JLabel cssStyleLabel = new JLabel("cssStyle:");
        cssStyleLabel.setBounds(50, 100, 80, 25);
        panel.add(cssStyleLabel);
        JTextField cssStyleText = new JTextField();
        cssStyleText.setBounds(150, 100, 300, 25);
        cssStyleText.setText("css-1bhwifz");
        panel.add(cssStyleText);
        // 保存路径
        JLabel fileSavePathLabel = new JLabel("save path:");
        fileSavePathLabel.setBounds(40, 130, 100, 25);
        panel.add(fileSavePathLabel);
        JTextField fileSavePathText = new JTextField();
        fileSavePathText.setBounds(150, 130, 300, 25);
        panel.add(fileSavePathText);
        JButton fileSaveBtn = new JButton("...");
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
        panel.add(fileSaveBtn);

        // 抓取
        JButton spiderBtn = new JButton("抓取");
        spiderBtn.setBounds(220, 170, 80, 25);
        panel.add(spiderBtn);
        spiderBtn.addActionListener(e -> {
            InfiniteProgressPanel glasspane = new InfiniteProgressPanel();
            try {
                glasspane.setBounds(100, 100, 50, 50);
                jFrame.setGlassPane(glasspane);
                glasspane.setText("Loading data, Please wait ...");
                glasspane.start();// 开始动画加载效果
                System.out.println(123);
                // SpiderCommand spiderCommand = new SpiderCommand();
                // spiderCommand.setChromDriverPath(CHROME_DRIVER);
                // spiderCommand.setSkuCode(skuCodeText.getText());
                // if (StringUtils.isNotBlank(skuCodeText.getText())) {
                // spiderCommand.setMasterStationUrl(
                // nikeUrlText.getText() + StringUtils.replaceEachRepeatedly(SEACH_URL,
                // new String[] { "{skuCode}", "{skuCode}" },
                // new String[] { skuCodeText.getText(), skuCodeText.getText() }));
                // } else {
                // if (StringUtils.isNotBlank(pdpUrlText.getText())) {
                // spiderCommand.setMasterStationUrl(pdpUrlText.getText());
                // spiderCommand.setPdpUrl(pdpUrlText.getText());
                // }
                // }
                // spiderCommand.setCssStyle(cssStyleText.getText());
                // spiderCommand.setExportPath(fileSavePathText.getText());
                //
                //// SpiderUtils.downZip(spiderCommand);
                // JOptionPane.showMessageDialog(null, "下载成功！", "", JOptionPane.INFORMATION_MESSAGE);
                glasspane.stop();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "下载失败！", "", JOptionPane.ERROR_MESSAGE);
                glasspane.stop();
            }
        });

    }

    void placeComponentsV1(JPanel panel) {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // JLabel nikeUrl = new JLabel("nikeUrl");
        // nikeUrl.setBounds(50, 10, 80, 25);
        // panel.add(nikeUrl);
        // JTextField nikeUrlText = new JTextField();
        // nikeUrlText.setBounds(150, 10, 300, 25);
        // panel.add(nikeUrlText);
        //
        // // 创建 skuCodeJLabel
        // JLabel skuCodeLabel = new JLabel("skuCode:");
        // skuCodeLabel.setBounds(50, 40, 80, 25);
        // panel.add(skuCodeLabel);
        // JTextField skuCodeText = new JTextField();
        // skuCodeText.setBounds(150, 40, 300, 25);
        // panel.add(skuCodeText);
        //
        // // pdpurl
        // JLabel pdpUrlLabel = new JLabel("pdpUrl:");
        // pdpUrlLabel.setBounds(50, 70, 80, 25);
        // panel.add(pdpUrlLabel);
        // JTextField pdpUrlText = new JTextField();
        // pdpUrlText.setBounds(150, 70, 300, 25);
        // panel.add(pdpUrlText);
        //
        // // cssStyle
        // JLabel cssStyleLabel = new JLabel("cssStyle:");
        // cssStyleLabel.setBounds(50, 100, 80, 25);
        // panel.add(cssStyleLabel);
        // JTextField cssStyleText = new JTextField();
        // cssStyleText.setBounds(150, 100, 300, 25);
        // cssStyleText.setText("css-1bhwifz");
        // panel.add(cssStyleText);
        // // 保存路径
        // JLabel fileSavePathLabel = new JLabel("save path:");
        // fileSavePathLabel.setBounds(40, 130, 100, 25);
        // panel.add(fileSavePathLabel);
        // JTextField fileSavePathText = new JTextField();
        // fileSavePathText.setBounds(150, 130, 300, 25);
        // panel.add(fileSavePathText);
        // JButton fileSaveBtn = new JButton("...");
        // fileSaveBtn.setBounds(455, 130, 60, 25);
        // fileSaveBtn.addActionListener(e -> {
        // JFileChooser jFileChooser = new JFileChooser();
        // jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Component component = null;
        // int saveDialog = jFileChooser.showSaveDialog(component);
        // if (saveDialog == JFileChooser.APPROVE_OPTION) {
        // String selectPath = jFileChooser.getSelectedFile().getPath() + "\\";
        // System.out.println("你选择的目录是：" + selectPath);
        // fileSavePathText.setText(selectPath);
        // }
        // });
        // panel.add(fileSaveBtn);

        final JProgressBar progressBar = new JProgressBar();

        JButton jButton = new JButton("抓取");
        jButton.setBounds(220, 170, 80, 25);
        panel.add(jButton);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        for (int i = 0; i <= 3; i++) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressBar.setValue(i * 25);
                        }
                    }
                }.start();
                System.out.println(1234);
                // try {
                // SpiderCommand spiderCommand = new SpiderCommand();
                // spiderCommand.setChromDriverPath(CHROME_DRIVER);
                // spiderCommand.setSkuCode(skuCodeText.getText());
                // if (StringUtils.isNotBlank(skuCodeText.getText())) {
                // spiderCommand.setMasterStationUrl(
                // nikeUrlText.getText() + StringUtils.replaceEachRepeatedly(SEACH_URL,
                // new String[] { "{skuCode}", "{skuCode}" },
                // new String[] { skuCodeText.getText(), skuCodeText.getText() }));
                // } else {
                // if (StringUtils.isNotBlank(pdpUrlText.getText())) {
                // spiderCommand.setMasterStationUrl(pdpUrlText.getText());
                // spiderCommand.setPdpUrl(pdpUrlText.getText());
                // }
                // }
                // spiderCommand.setCssStyle(cssStyleText.getText());
                // spiderCommand.setExportPath(fileSavePathText.getText());
                //
                // SpiderUtils.downZip(spiderCommand);
                // JOptionPane.showMessageDialog(null, "下载成功！", "", JOptionPane.INFORMATION_MESSAGE);
                // } catch (Exception exception) {
                // exception.printStackTrace();
                // JOptionPane.showMessageDialog(null, "下载失败！", "", JOptionPane.ERROR_MESSAGE);
                // }
            }
        });
        progressBar.setStringPainted(true);
        panel.add(progressBar);

    }

    public static void main(String[] args) {
        SpiderMainUI spiderMainUI = new SpiderMainUI();
        spiderMainUI.initJframe();
    }

}
